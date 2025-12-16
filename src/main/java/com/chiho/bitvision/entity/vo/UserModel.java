package com.chiho.bitvision.entity.vo;

import com.chiho.bitvision.holder.UserHolder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户兴趣模型的核心数据结构
 * 用于构建、存储和更新用户的兴趣偏好
 * （视频观看停留、视频点赞、视频收藏、分类订阅）
 */
@Data
public class UserModel {
    // 用户标签列表
    private List<Model> models;
    private Long userId;

    public static UserModel buildUserModel(List<String> labels,Long videoId,Double score){
        final UserModel userModel = new UserModel();
        final ArrayList<Model> models = new ArrayList<>();
        userModel.setUserId(UserHolder.get());  // 设置用户ID
        for (String label : labels) {
            final Model model = new Model();
            model.setLabel(label);
            model.setScore(score);
            model.setVideoId(videoId);
            models.add(model);
        }
        userModel.setModels(models);
        return userModel;
    }

}

