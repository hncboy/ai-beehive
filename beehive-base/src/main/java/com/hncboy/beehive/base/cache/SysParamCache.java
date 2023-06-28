package com.hncboy.beehive.base.cache;

import com.hncboy.beehive.base.constant.PublicConstant;
import com.hncboy.beehive.base.enums.SysParamKeyEnum;
import com.hncboy.beehive.base.exception.ServiceException;
import com.hncboy.beehive.base.util.RedisUtil;
import com.hncboy.beehive.base.domain.entity.SysParamDO;
import com.hncboy.beehive.base.mapper.SysParamMapper;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hncboy
 * @date 2023/5/11
 * 系统参数相关缓存
 */
public class SysParamCache {

    private static final String SYS_PARAM_KEY = "sysParam";

    /**
     * 批次获取参数值
     * 通过 multiGet 批量获取时无法区分 hashKey 是不存在还是存在的 hashValue 为 null，因此不允许 paramValue 为空
     *
     * @param paramKeys paramKeys
     * @return 参数 Map
     */
    public static Map<String, String> multiGet(List<String> paramKeys) {
        Map<String, String> resultMap = new HashMap<>(paramKeys.size());

        // 批量获取缓存中的值
        List<Object> hashValues = RedisUtil.hMultiGet(SYS_PARAM_KEY, Convert.toList(Object.class, paramKeys));

        // 不存在的 paramKey
        Set<String> notExistParamKeySet = CollectionUtil.newHashSet();

        // 遍历查询的 paramKey
        for (int i = 0; i < paramKeys.size(); i++) {
            if (Objects.isNull(hashValues.get(i))) {
                notExistParamKeySet.add(paramKeys.get(i));
                continue;
            }
            resultMap.put(paramKeys.get(i), hashValues.get(i).toString());
        }

        // 判断是否有不存在的 paramKey
        if (!notExistParamKeySet.isEmpty()) {
            SysParamMapper sysParamMapper = SpringUtil.getBean(SysParamMapper.class);
            List<SysParamDO> sysParamDOList = sysParamMapper.selectList(new LambdaQueryWrapper<SysParamDO>()
                    .in(SysParamDO::getParamKey, notExistParamKeySet));
            // 转为 Map
            Map<String, String> sysParamMap = sysParamDOList.stream().collect(Collectors.toMap(SysParamDO::getParamKey, SysParamDO::getParamValue));

            // 遍历查询的 paramKey
            for (String paramKey : notExistParamKeySet) {
                // 如果 paramKey 存在则取数据的值否则取空值
                String sysParamValue = sysParamMap.getOrDefault(paramKey, PublicConstant.REDIS_CACHE_MISS_VALUE);
                RedisUtil.hPut(SYS_PARAM_KEY, paramKey, sysParamValue);
                resultMap.put(paramKey, sysParamValue);
            }
        }

        // 遍历结果集
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            if (Objects.equals(entry.getValue(), PublicConstant.REDIS_CACHE_MISS_VALUE)) {
                throw new ServiceException(StrUtil.format("paramKey [{}] 不存在", entry.getKey()));
            }
        }

        return resultMap;
    }

    /**
     * 获取参数值
     *
     * @param paramKey paramKey
     * @return String
     */
    public static String get(String paramKey) {
        return multiGet(Collections.singletonList(paramKey)).get(paramKey);
    }

    /**
     * 获取参数值
     *
     * @param paramKeyEnum paramKeyEnum
     * @return String
     */
    public static String get(SysParamKeyEnum paramKeyEnum) {
        return get(paramKeyEnum.getParamKey());
    }

    /**
     * 设置参数值
     *
     * @param paramKey   paramKey
     * @param paramValue paramValue
     */
    public static void setHashValue(String paramKey, String paramValue) {
        RedisUtil.hPut(SYS_PARAM_KEY, paramKey, paramValue);
    }

    /**
     * 删除参数 key
     *
     * @param paramKey paramKey
     */
    public static void deleteHashKey(String paramKey) {
        RedisUtil.hDelete(SYS_PARAM_KEY, paramKey);
    }
}
