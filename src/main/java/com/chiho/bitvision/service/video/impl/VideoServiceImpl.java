package com.chiho.bitvision.service.video.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.constant.AuditStatus;
import com.chiho.bitvision.entity.File;
import com.chiho.bitvision.entity.user.User;
import com.chiho.bitvision.entity.video.Video;
import com.chiho.bitvision.entity.vo.BasePage;
import com.chiho.bitvision.entity.vo.UserVO;
import com.chiho.bitvision.mapper.video.VideoMapper;
import com.chiho.bitvision.service.FileService;
import com.chiho.bitvision.service.user.UserService;
import com.chiho.bitvision.service.video.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    // 根据userId获取对应视频,只包含公开的
    @Override
    public IPage<Video> listByUserIdOpenVideo(Long userId, BasePage basePage) {
        if (userId == null)
            return new Page<>();    // 返回空的数据
        final IPage<Video> page = page(
                basePage.page(),
                new LambdaQueryWrapper<Video>()
                        .eq(Video::getUserId, userId)
                        .eq(Video::getAuditStatus, AuditStatus.SUCCESS)
                        .orderByDesc(Video::getGmtCreated)
        );
        // 获取当前分页查询结果中的数据列表
        final List<Video> videos = page.getRecords();
        setUserVoAndUrl(videos);
        return page;
    }

    // 为视频集合补充完整的关联数据，使其可以直接提供给前端使用
    public void setUserVoAndUrl(Collection<Video> videos){
        if (!ObjectUtils.isEmpty(videos)){
            Set<Long> userIds = new HashSet<>();
            final ArrayList<Long> fileIds = new ArrayList<>();
            for (Video video : videos){
                userIds.add(video.getUserId());
                fileIds.add(video.getUrl());
                fileIds.add(video.getCover());
            }
            final Map<Long, File> fileMap = fileService.listByIds(fileIds).stream().collect(Collectors.toMap(File::getId, Function.identity()));
            final Map<Long, User> userMap = userService.list(userIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));

            for (Video video : videos) {
                final UserVO userVO = new UserVO();
                final User user = userMap.get(video.getUserId());
                userVO.setId(video.getUserId());
                userVO.setNickName(user.getNickName());
                userVO.setDescription(user.getDescription());
                userVO.setSex(user.getSex());
                video.setUser(userVO);
                final File file = fileMap.get(video.getUrl());
                video.setVideoType(file.getFormat());
            }
        }
    }
}
