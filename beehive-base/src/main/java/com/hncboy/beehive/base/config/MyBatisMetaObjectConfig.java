package com.hncboy.beehive.base.config;

import com.hncboy.beehive.base.util.WebUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.Objects;

/**
 * @author hncboy
 * @date 2023-3-28
 * MybatisPlus 配置
 */
@Configuration
public class MyBatisMetaObjectConfig implements MetaObjectHandler {

    private static final String CREATE_TIME = "createTime";
    private static final String UPDATE_TIME = "updateTime";
    private static final String IP = "ip";

    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasGetter(CREATE_TIME) && Objects.isNull(getFieldValByName(CREATE_TIME, metaObject))) {
            this.setFieldValByName(CREATE_TIME, new Date(), metaObject);
        }

        if (metaObject.hasGetter(UPDATE_TIME) && Objects.isNull(getFieldValByName(UPDATE_TIME, metaObject))) {
            this.setFieldValByName(UPDATE_TIME, new Date(), metaObject);
        }

        if (metaObject.hasGetter(IP) && Objects.isNull(getFieldValByName(IP, metaObject))) {
            this.setFieldValByName(IP, WebUtil.getIp(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasGetter(UPDATE_TIME) && Objects.isNull(getFieldValByName(UPDATE_TIME, metaObject))) {
            this.setFieldValByName(UPDATE_TIME, new Date(), metaObject);
        }
    }
}
