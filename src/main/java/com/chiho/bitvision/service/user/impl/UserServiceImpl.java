package com.chiho.bitvision.service.user.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.constant.AuditStatus;
import com.chiho.bitvision.constant.RedisConstant;
import com.chiho.bitvision.entity.response.AuditResponse;
import com.chiho.bitvision.entity.user.Favorites;
import com.chiho.bitvision.entity.user.User;
import com.chiho.bitvision.entity.vo.FindPWVO;
import com.chiho.bitvision.entity.vo.RegisterVO;
import com.chiho.bitvision.entity.vo.UpdateUserVO;
import com.chiho.bitvision.entity.vo.UserVO;
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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
}
