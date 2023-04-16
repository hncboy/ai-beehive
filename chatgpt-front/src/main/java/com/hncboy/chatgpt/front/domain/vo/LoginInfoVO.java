package com.hncboy.chatgpt.front.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功返回登录结果
 *
 * @author CoDeleven
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(title = "登录成功后返回前端登录结果")
public class LoginInfoVO {

    @Schema(title = "登录的Token")
    private String token;

    @Schema(title = "基础用户ID")
    private Integer baseUserId;
}
