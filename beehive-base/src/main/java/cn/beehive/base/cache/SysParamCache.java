package cn.beehive.base.cache;

import cn.beehive.base.constant.PublicConstant;
import cn.beehive.base.domain.entity.SysParamDO;
import cn.beehive.base.enums.SysParamKeyEnum;
import cn.beehive.base.exception.ServiceException;
import cn.beehive.base.mapper.SysParamMapper;
import cn.beehive.base.util.RedisUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Objects;

/**
 * @author hncboy
 * @date 2023/5/11
 * 系统参数相关缓存
 */
public class SysParamCache {

    private static final String PREFIX = "sysParam:";

    /**
     * 获取参数值
     * TODO 改成使用 hash，并且可以同时获取多个参数
     *
     * @param paramKeyEnum paramKeyEnum
     * @return String
     */
    public static String getValue(SysParamKeyEnum paramKeyEnum) {
        String errorMsg = StrUtil.format("paramKey [{}] 不存在", paramKeyEnum.getParamKey());

        String key = PREFIX + paramKeyEnum.getParamKey();
        // key 是否存在
        if (RedisUtil.hasKey(key)) {
            String value = RedisUtil.get(key);
            if (Objects.equals(value, PublicConstant.REDIS_CACHE_MISS_VALUE)) {
                throw new ServiceException(errorMsg);
            }
            return value;
        }

        // 查询数据库
        SysParamMapper sysParamMapper = SpringUtil.getBean(SysParamMapper.class);
        SysParamDO sysParamDO = sysParamMapper.selectOne(new LambdaQueryWrapper<SysParamDO>()
                .eq(SysParamDO::getParamKey, paramKeyEnum.getParamKey()));
        // 数据库不存在该 key
        if (Objects.isNull(sysParamDO)) {
            RedisUtil.set(key, PublicConstant.REDIS_CACHE_MISS_VALUE);
            throw new ServiceException(errorMsg);
        }

        RedisUtil.set(key, sysParamDO.getParamValue());
        return sysParamDO.getParamValue();
    }

    /**
     * 设置参数值
     *
     * @param paramKey   paramKey
     * @param paramValue paramValue
     */
    public static void setValue(String paramKey, String paramValue) {
        RedisUtil.set(PREFIX + paramKey, paramValue);
    }

    /**
     * 删除参数 key
     *
     * @param paramKey paramKey
     */
    public static void deleteKey(String paramKey) {
        RedisUtil.delete(PREFIX + paramKey);
    }
}
