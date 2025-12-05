package com.chiho.bitvision.entity.json;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 单一审核类型的结果映射类
 */
@Data
@ToString
public class TypeJson implements Serializable {
    String suggestion;
    List<CutsJson> cuts;
    List<DetailsJson> details;
}
