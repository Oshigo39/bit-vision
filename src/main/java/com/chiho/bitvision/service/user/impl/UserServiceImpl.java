package com.chiho.bitvision.service.user.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.constant.AuditStatus;
import com.chiho.bitvision.constant.RedisConstant;
import com.chiho.bitvision.entity.response.AuditResponse;
import com.chiho.bitvision.entity.user.Favorites;
import com.chiho.bitvision.entity.user.User;
import com.chiho.bitvision.entity.vo.*;
import com.chiho.bitvision.exception.BaseException;
import com.chiho.bitvision.holder.UserHolder;
import com.chiho.bitvision.mapper.user.UserMapper;
import com.chiho.bitvision.service.FileService;
import com.chiho.bitvision.service.audit.ImageAuditService;
import com.chiho.bitvision.service.audit.TextAuditService;
import com.chiho.bitvision.service.user.FavoritesService;
import com.chiho.bitvision.service.user.FollowService;
import com.chiho.bitvision.service.user.UserService;
import com.chiho.bitvision.util.DateUtil;
import com.chiho.bitvision.util.RedisCacheUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String SALT = "oshigo39";

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Autowired
    private FavoritesService favoritesService;

    @Autowired
    private FollowService followService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TextAuditService textAuditService;

    @Autowired
    private ImageAuditService imageAuditService;

    // 注册
    @Override
    public boolean register(RegisterVO registerVO) throws Exception {
        // 邮箱是否存在
        final int count = (int) count(new LambdaQueryWrapper<User>().eq(User::getEmail, registerVO.getEmail()));
        if (count == 1){
            throw new BaseException("邮箱已经被注册");
        }
        final String code = registerVO.getCode();
        final Object o = redisCacheUtil.get(RedisConstant.EMAIL_CODE + registerVO.getEmail());
        if (o == null){
            throw new BaseException("邮箱验证码为空");
        }
        if (!code.equals(o)){
            return false;
        }
        // 初始化User相关的信息并保存
        final User user = new User();
        user.setNickName(registerVO.getNickName());
        user.setEmail(registerVO.getEmail());
        user.setDescription("这个人很懒...");
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex(
                (SALT + registerVO.getPassword()).getBytes()
        );
        user.setPassword(encryptPassword);
        user.setGmtCreated(new Date());
        save(user);

        // 创建默认收藏夹并存进数据库
        final Favorites favorites = new Favorites();
        favorites.setUserId(user.getId());
        favorites.setName("default favorites");
        favoritesService.save(favorites);

        // 这里如果单独抽出一个用户配置表就好了,但是没有必要再搞个表
        user.setDefaultFavoritesId(favorites.getId());
        updateById(user);
        return true;
    }

    // 找回密码
    @Override
    public Boolean findPassword(FindPWVO findPWVO) {
        final Object o = redisCacheUtil.get(RedisConstant.EMAIL_CODE + findPWVO.getEmail());
        if (o == null) return false;
        // 校验邮箱验证码（对象o转换成字符串后再parseInt()成int类型）
        if (Integer.parseInt(o.toString())
                != findPWVO.getCode()) return false;
        // 修改
        final User user = new User();
        user.setEmail(findPWVO.getEmail());
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex(
                (SALT + findPWVO.getNewPassword()).getBytes()
        );
        user.setPassword(encryptPassword);
        update(user,new UpdateWrapper<User>()
                .lambda()
                .set(User::getPassword,encryptPassword)
                .set(User::getGmtUpdated,new Date())
                .eq(User::getEmail,findPWVO.getEmail()));
        return true;
    }

    // 获取指定用户的个人信息
    @Override
    public UserVO getInfo(Long userId) {
        final User user = getById(userId);
        if (ObjectUtils.isEmpty(user)) {
            // 用户不存在，返回空对象，而不是异常
            return new UserVO();
        }
        final UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        // 查出当前用户关注的数量
        final long followCount = followService.getFollowCount(userId);

        // 获取粉丝数量
        final long fansCount = followService.getFansCount(userId);
        userVO.setFollow(followCount);
        userVO.setFans(fansCount);
        return userVO;
    }

    // 更新用户资料
    @Override
    public void updateUser(UpdateUserVO userVO) {
        final Long userId = userVO.getUserId();
        final User oldUser = getById(userId);

        // 审核（昵称、描述、头像等是否违规）
        if (!oldUser.getNickName().equals(userVO.getNickName())){
            oldUser.setNickName(userVO.getNickName());
            AuditResponse audit = textAuditService.audit(userVO.getNickName());
            // 检查昵称是否审核成功
            if (!Objects.equals(audit.getAuditStatus(), AuditStatus.SUCCESS)){
                throw new BaseException(audit.getMsg());
            }
        }
        if (!ObjectUtils.isEmpty(userVO.getDescription()) && !oldUser.getDescription().equals(userVO.getDescription())){
            oldUser.setDescription(userVO.getDescription());
            AuditResponse audit = textAuditService.audit(userVO.getDescription());
            if (!Objects.equals(audit.getAuditStatus(), AuditStatus.SUCCESS)) {
                throw new BaseException(audit.getMsg());
            }
        }
        if (!Objects.equals(userVO.getAvatar(),oldUser.getAvatar())){
            final AuditResponse audit = imageAuditService.audit(fileService.getById(userVO.getAvatar()).getFileKey());
            if (!Objects.equals(audit.getAuditStatus(), AuditStatus.SUCCESS)) {
                throw new BaseException(audit.getMsg());
            }
            oldUser.setAvatar(userVO.getAvatar());
        }

        if (!ObjectUtils.isEmpty(userVO.getDefaultFavoritesId())){
            // 校验默认收藏夹是否为空
            favoritesService.exist(userId,userVO.getDefaultFavoritesId());
        }

        oldUser.setSex(userVO.getSex());
        oldUser.setDefaultFavoritesId(userVO.getDefaultFavoritesId());
        updateById(oldUser);
    }

    /**
     * SELECT id, nick_name, sex, avatar, description
     * FROM user
     * WHERE id IN ( ?, ?, ?, ... )
     */
    @Override
    public List<User> list(Collection<Long> userIds) {
        // 高效地批量查询用户信息
        return list(new LambdaQueryWrapper<User>().in(User::getId,userIds)
                .select(User::getId,User::getNickName,User::getSex,User::getAvatar,User::getDescription));
    }

    // 关注/取关
    @Override
    public boolean follows(Long followsUserId,Long userId) {
        return followService.follows(followsUserId,userId);
    }

    // 获取关注
    @Override
    public Page<User> getFollows(BasePage basePage, Long userId) {
        Page<User> page = new Page<>();
        // 获取关注列表
        final Collection<Long> followIds = followService.getFollow(userId, basePage);
        if (ObjectUtils.isEmpty(followIds)) return page;
        // 获取粉丝列表
        // 这里需要将数据转换，因为存到redis中数值小是用int保存，取出来需要用long比较
        final HashSet<Long> fans = new HashSet<>(followService.getFans(userId, null));
        // 构建关注关系映射map
        Map<Long,Boolean> map = new HashMap<>();
        for (Long followId : followIds) {
            // 判断关注列表里的ID是否存在于粉丝列表中(相互关注)
            // key: followId, value: in fans list?
            map.put(followId,fans.contains(followId));
        }

        // 获取信息关注/粉丝信息
        final ArrayList<User> users = new ArrayList<>();
        final Map<Long, User> userMap = getBaseInfoUserToMap(map.keySet());
        final List<Long> avatarIds = userMap.values().stream().map(User::getAvatar).collect(Collectors.toList());
        for (Long followId : followIds) {
            final User user = userMap.get(followId);
            user.setEach(map.get(user.getId()));
            users.add(user);
        }
        page.setRecords(users);
        page.setTotal(users.size());

        return page;
    }

    @Override
    public Page<User> getFans(Long userId, BasePage basePage) {
        final Page<User> page = new Page<>();
        // 获取粉丝列表
        final Collection<Long> fansIds = followService.getFans(userId, basePage);
        if (ObjectUtils.isEmpty(fansIds)) return page;
        // 获取关注列表
        final HashSet<Long> followIds = new HashSet<>(followService.getFollow(userId, null));
        Map<Long,Boolean> map = new HashMap<>();
        // 遍历粉丝，查看关注列表中是否有
        for (Long fansId : fansIds) {
            map.put(fansId,followIds.contains(fansId));
        }
        final Map<Long, User> userMap = getBaseInfoUserToMap(map.keySet());
        final ArrayList<User> users = new ArrayList<>();
        // 遍历粉丝列表,保证有序性
        for (Long fansId : fansIds) {
            final User user = userMap.get(fansId);
            user.setEach(map.get(user.getId()));
            users.add(user);
        }

        page.setRecords(users);
        page.setTotal(users.size());
        return page;
    }

    private Map<Long,User> getBaseInfoUserToMap(Collection<Long> userIds){
        List<User> users = new ArrayList<>();
        if (!ObjectUtils.isEmpty(userIds)){
            users = list(new LambdaQueryWrapper<User>()
                    .in(User::getId, userIds)
                    .select(User::getId,
                            User::getNickName,
                            User::getDescription,
                            User::getSex,
                            User::getAvatar));
        }
        // 将收集到的user的list转化成数据流，然后收集起来再转化成map映射
        return users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        // Function.identity()：输入什么，就输出什么
    }
}
