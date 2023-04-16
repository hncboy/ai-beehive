package com.hncboy.chatgpt.front.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功返回用户信息
 *
 * @author CoDeleven
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Schema(title = "登录成功后返回前端用户信息")
public class UserInfoVO {

    @Schema(title = "基础用户ID")
    private Integer baseUserId;

    @Schema(title = "用户昵称")
    private String nickname;

    @Schema(title = "邮箱")
    private String email;

    @Schema(title = "自我介绍")
    private String description;

    @Schema(title = "头像地址")
    private String avatarUrl;
}
