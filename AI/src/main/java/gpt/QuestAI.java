package gpt;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import lombok.Data;

@Data
public class QuestAI {

    /**
     * 多轮会话流式输出
     */
    private static Generation gen;
    private static MessageManager msgManager;

    public MessageManager getMsgManager() {
        return msgManager;
    }

    public QuestAI() {
        // 初始化MessageManager和Generation对象，只需初始化一次
        msgManager = new MessageManager(10000);
        gen = new Generation();
        // 添加设定好系统指令
        msgManager.add(Message.builder().role(Role.SYSTEM.getValue())
                .content(Constants.INSTRUCT_TEXT)
                .build());
    }
    public String callWithMessage1(String question) throws NoApiKeyException, ApiException, InputRequiredException {
        // 添加用户询问的消息
        msgManager.add(Message.builder().role(Role.USER.getValue()).content(question).build());
        QwenParam param =
                QwenParam.builder().model(Generation.Models.QWEN_PLUS)
                        .messages(msgManager.get())
                        .resultFormat(QwenParam.ResultFormat.MESSAGE)
                        .topP(0.8)
                        .apiKey(Constants.apiKey)
                        .enableSearch(true)
                        .build();
        GenerationResult result = gen.call(param);
        String aiResponse = result.getOutput().getChoices().get(0).getMessage().getContent();

        // 添加助手的回应
        msgManager.add(Message.builder().role(Role.ASSISTANT.getValue()).content(aiResponse).build());
        System.out.println(aiResponse);
        return aiResponse;
    }

    public static void main(String[] args) throws NoApiKeyException, InputRequiredException {
        QuestAI ai = new QuestAI();
        ai.callWithMessage1("你好");
        msgManager.get();
        System.out.println(msgManager.get().subList(1,msgManager.get().size()));
        System.out.println(msgManager.get().get(1));
        System.out.println(msgManager.get().get(2));
    }
}
