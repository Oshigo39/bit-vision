package com.chiho.bitvision.service.video.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chiho.bitvision.config.LocalCache;
import com.chiho.bitvision.config.QiNiuConfig;
import com.chiho.bitvision.constant.AuditStatus;
import com.chiho.bitvision.entity.File;
import com.chiho.bitvision.entity.task.VideoTask;
import com.chiho.bitvision.entity.user.User;
import com.chiho.bitvision.entity.video.Type;
import com.chiho.bitvision.entity.video.Video;
import com.chiho.bitvision.entity.vo.BasePage;
import com.chiho.bitvision.entity.vo.UserVO;
import com.chiho.bitvision.exception.BaseException;
import com.chiho.bitvision.holder.UserHolder;
import com.chiho.bitvision.mapper.video.VideoMapper;
import com.chiho.bitvision.service.FileService;
import com.chiho.bitvision.service.audit.VideoPublishAuditServiceImpl;
import com.chiho.bitvision.service.user.FavoritesService;
import com.chiho.bitvision.service.user.FollowService;
import com.chiho.bitvision.service.user.UserService;
import com.chiho.bitvision.service.video.TypeService;
import com.chiho.bitvision.service.video.VideoService;
import com.chiho.bitvision.service.video.VideoStarService;
import com.chiho.bitvision.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FavoritesService favoritesService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private VideoStarService videoStarService;

    @Autowired
    private FollowService followService;

    @Autowired
    private VideoPublishAuditServiceImpl videoPublishAuditService;

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

    @Override
    public boolean favoritesVideo(Long fId, Long vId) {
        final Video video = getById(vId);
        if (video == null)
            throw new BaseException("指定视频不存在");
        final boolean favorites = favoritesService.favorites(fId, vId);
        // 收藏成功则更新计数
        updateFavorites(video, favorites ? 1L : -1L);

        // todo 修改用户模型，个性推送功能
//        final List<String> labels = video.buildLabel();
//        final UserModel userModel = UserModel.buildUserModel(labels,vId,2.0);
//        interestPushService.updateUserModel(userModel);

        return favorites;
    }

    // 获取收藏夹下的视频
    @Override
    public Collection<Video> listVideoByFavorites(Long favoritesId) {
        final List<Long> videoIds = favoritesService.listVideoIds(favoritesId, UserHolder.get());
        if (ObjectUtils.isEmpty(videoIds))
            return Collections.EMPTY_LIST;
        final Collection<Video> videos = listByIds(videoIds);
        setUserVoAndUrl(videos);
        return videos;
    }

    @Override
    public void publishVideo(Video video) {
        final Long userId = UserHolder.get();
        Video oldVideo = null;
        // 不允许修改视频
        final Long videoId = video.getId();
        if (videoId != null) {
            // url不能一致
            oldVideo = this.getOne(new LambdaQueryWrapper<Video>()
                    .eq(Video::getId, videoId)
                    .eq(Video::getUserId, userId));
            if (!(video.buildVideoUrl()).equals(oldVideo.buildVideoUrl()) || !(video.buildCoverUrl().equals(oldVideo.buildCoverUrl()))) {
                throw new BaseException("不能更换视频源,只能修改视频信息");
            }
        }
        // 判断对应分类是否存在
        Type type = typeService.getById(video.getTypeId());
        if (type == null) {
            throw new BaseException("分类不存在");
        }
        // 校验标签最多不超过5个
        if (video.buildLabel().size() > 5) {
            throw new BaseException("标签最多只能选择5个");
        }

        // 修改状态
        video.setAuditStatus(AuditStatus.PROCESS);  // 审核中
        video.setUserId(userId);    // 设置视频所属ID

        boolean isAdd = videoId == null;
        // 校验
        video.setYv(null);  // 重置视频唯一标识
        if (!isAdd) {
            // 如果是更新已有视频，则不允许更改这些信息
            video.setVideoType(null);
            video.setLabelNames(null);
            video.setUrl(null);
            video.setCover(null);
        } else {
            // 自动生成封面
            if (ObjectUtils.isEmpty(video.getCover())) {
                video.setCover(fileService.generatePhoto(video.getUrl(), userId));
            }
            // 生成图片的UUID
            video.setYv("YV" + UUID.randomUUID().toString().replace("-", "").substring(8));
        }

        if (isAdd || StringUtils.hasLength(oldVideo.getDuration())) {
            final String uuid = UUID.randomUUID().toString();
            LocalCache.put(uuid, true);
            try {
                Long url = video.getUrl();
                if (url == null || url == 0) if (oldVideo != null) {
                    url = oldVideo.getUrl();
                }
                final String fileKey = fileService.getById(url).getFileKey();
                final String duration = FileUtil.getVideoDuration(QiNiuConfig.CNAME + "/" + fileKey + "?uuid=" + uuid);
                video.setDuration(duration);
            } finally {
                LocalCache.rem(uuid);
            }
        }
        this.saveOrUpdate(video);

        final VideoTask videoTask = new VideoTask();
        videoTask.setOldVideo(video);
        videoTask.setVideo(video);
        videoTask.setIsAdd(isAdd);
        videoTask.setOldState(isAdd || video.getOpen());
        videoTask.setNewState(true);
        videoPublishAuditService.audit(videoTask, false);
    }

    // 根据视频ID获取视频信息
    @Override
    public Video getVideoById(Long videoId, Long userId) {
        final Video video = this.getOne(new LambdaQueryWrapper<Video>().eq(Video::getId, videoId)); // 视频表格比对id获取记录
        if (video == null) throw new BaseException("指定视频不存在");
        // 私密则返回为空视频
        if (video.getOpen()) return new Video();
        setUserVoAndUrl(Collections.singleton(video));
        // 当前视频用户自己是否有收藏/点赞过等信息
        // 这里需要优化 如果这里开线程获取则系统g了(因为这里的场景不适合) -> 请求数很多
        // 正确做法: 视频存储在redis中，点赞收藏等行为异步放入DB, 定时任务扫描DB中不重要更新redis
        video.setStart(videoStarService.starState(videoId, userId));
        video.setFavorites(favoritesService.favoritesState(videoId, userId));
        video.setFollow(followService.isFollows(video.getUserId(), userId));
        return video;
    }

    // 安全地更新视频的收藏数量
    public void updateFavorites(Video video, Long value) {
        // 创建更新包装器
        final UpdateWrapper<Video> updateWrapper = new UpdateWrapper<>();
        // 设置更新SQL（更新Video表中的favorites_count字段）
        updateWrapper.setSql("favorites_count = favorites_count + " + value);
        // 只更新指定ID的视频记录、乐观锁机制，确保只有当数据库中的收藏数与传入视频对象中的收藏数一致时才执行更新
        updateWrapper.lambda()
                .eq(Video::getId, video.getId())
                .eq(Video::getFavoritesCount, video.getFavoritesCount());
        update(video,updateWrapper);    // MP的更新操作
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
