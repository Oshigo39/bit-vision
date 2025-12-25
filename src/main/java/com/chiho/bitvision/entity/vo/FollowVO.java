package com.chiho.bitvision.entity.vo;

import com.chiho.bitvision.entity.user.Follow;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FollowVO extends Follow {

    private String nickName;
}
