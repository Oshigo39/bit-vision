package com.chiho.bitvision.entity.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 基础的分页请求对象
 */
@Data
public class BasePage {

    private Long page = 1L;     // 默认当前页码
    private Long limit = 15L;       // 默认每页记录数量

    // MP的IPage对象
    public IPage page(){
        return new Page(
                page == null ? 1L : this.page,
                limit == null ? 15L : this.limit
        );
    }
}
