package com.hncboy.beehive.base.resource.aip;

import lombok.Data;

/**
 * @author hncboy
 * @date 2023/6/6
 * 百度内容审核结果，返回值看文档
 * https://cloud.baidu.com/doc/ANTIPORN/s/Nk3h6xbb2
 */
@Data
public class AipContentResult {

    //... 省略其他字段

    /**
     * 审核结果类型 1.合规，2.不合规，3.疑似，4.审核失败
     */
    private Integer conclusionType;

    /**
     * 内层错误提示信息，底层服务失败才返回，成功不返回
     */
    private String errorMsg;
}
