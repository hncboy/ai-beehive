/** 
 * @Title: ModerationEmitterChain.java
 * @Description: TODO(描述)
 * @author zzy1998
 * @date 2023-05-09 09:53:06
 */
package com.hncboy.chatgpt.front.handler.emitter;

import java.io.IOException;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.hncboy.chatgpt.base.util.ObjectMapperUtil;
import com.hncboy.chatgpt.front.api.apikey.ApiKeyChatClientBuilder;
import com.hncboy.chatgpt.front.domain.request.ChatProcessRequest;
import com.hncboy.chatgpt.front.domain.vo.ChatReplyMessageVO;

import cn.hutool.core.util.StrUtil;

/**
 * @ClassName: ModerationEmitterChain
 * @Description: OpenAI官方的审核策略
 * @author zzy1998
 * @date 2023-05-09 09:53:06
 */
public class ModerationEmitterChain extends AbstractResponseEmitterChain {

	/**
	 * @Title: doChain
	 * @Description: TODO(描述)
	 * @param request
	 * @param emitter
	 * @author zzy1998
	 * @date 2023-05-09 09:53:17
	 */
	@Override
	public void doChain(ChatProcessRequest request, ResponseBodyEmitter emitter) {
		// TODO 自动生成的方法存根
		String prompts = request.getPrompt();
		String systemMessages = request.getSystemMessage();
		try {
			// 取上一条消息的 parentMessageId 和 conversationId，使得敏感词检测未通过时不影响上下文
			ChatReplyMessageVO chatReplyMessageVO = ChatReplyMessageVO.onEmitterChainException(request);
			boolean moderation = ApiKeyChatClientBuilder.buildOpenAiClient().moderations(systemMessages + prompts)
					.getResults().get(0).isFlagged();

			if (moderation) {
				chatReplyMessageVO.setText(StrUtil.format("发送失败，包含敏感词，多次发送会被封禁，{}", systemMessages + ',' + prompts));
				emitter.send(ObjectMapperUtil.toJson(chatReplyMessageVO));
				emitter.complete();
				return;
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (getNext() != null) {
			getNext().doChain(request, emitter);
		}
	}

}
