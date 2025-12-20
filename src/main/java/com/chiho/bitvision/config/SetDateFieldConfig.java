package com.chiho.bitvision.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * MP中自动设置创建时间、更新时间字段的配置类
 */
@Configuration
public class SetDateFieldConfig implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("gmtCreated",new Date(),metaObject);

        // 为实体类中的更新时间创建初始化时间
        this.setFieldValByName("gmtUpdated",new Date(),metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("gmtUpdated",new Date(),metaObject);
    }
}
