package cn.beehive.base.config;

import cn.beehive.base.util.WebUtil;
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

    @Override
    public void insertFill(MetaObject metaObject) {
        Object createTime = getFieldValByName("createTime", metaObject);
        if (Objects.isNull(createTime)) {
            this.setFieldValByName("createTime", new Date(), metaObject);
        }
        Object updateTime = getFieldValByName("updateTime", metaObject);
        if (Objects.isNull(updateTime)) {
            this.setFieldValByName("updateTime", new Date(), metaObject);
        }
        Object ip = getFieldValByName("ip", metaObject);
        if (Objects.isNull(ip)) {
            this.setFieldValByName("ip", WebUtil.getIp(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
