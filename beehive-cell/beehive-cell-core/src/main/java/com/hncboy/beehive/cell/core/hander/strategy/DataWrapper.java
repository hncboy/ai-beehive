package com.hncboy.beehive.cell.core.hander.strategy;

import com.hncboy.beehive.base.exception.ServiceException;

import java.math.BigDecimal;

/**
 * @author hncboy
 * @date 2023/5/29
 * 数据包装
 */
public record DataWrapper(Object data) {

    /**
     * 将 data 转为 String
     *
     * @return String
     */
    public BigDecimal asBigDecimal() {
        if (data instanceof BigDecimal) {
            return (BigDecimal) data;
        }
        if (data instanceof String) {
            return new BigDecimal(String.valueOf(data));
        }
        throw new ServiceException("Cannot convert data to BigDecimal.");
    }

    /**
     * 将 data 转为 int
     *
     * @return int
     */
    public int asInt() {
        if (data instanceof Integer) {
            return (int) data;
        }
        if (data instanceof String) {
            return Integer.parseInt((String) data);
        }
        throw new ServiceException("Cannot convert data to int.");
    }

    /**
     * 将 data 转为 boolean
     *
     * @return boolean
     */
    public boolean asBoolean() {
        if (data instanceof Boolean) {
            return (boolean) data;
        }
        if (data instanceof String) {
            return Boolean.parseBoolean((String) data);
        }
        throw new ServiceException("Cannot convert data to boolean.");
    }

    /**
     * 将 data 转为 String
     *
     * @return String
     */
    public String asString() {
        return String.valueOf(data);
    }

    /**
     * 判断 data 是否为 null
     *
     * @return boolean
     */
    public boolean isNull() {
        return data == null;
    }

    /**
     * 判断 data 是否不为 null
     *
     * @return boolean
     */
    public boolean nonNull() {
        return !isNull();
    }
}
