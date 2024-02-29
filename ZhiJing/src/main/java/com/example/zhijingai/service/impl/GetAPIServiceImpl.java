package com.example.zhijingai.service.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.example.zhijingai.entitys.entity.HistoryMessageManager;
import com.example.zhijingai.mapper.GetAPIMapper;
import com.example.zhijingai.service.GetAPIService;
import gpt.Constants;
import io.reactivex.Flowable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;


@Service
public class GetAPIServiceImpl implements GetAPIService {
    @Autowired
    private GetAPIMapper getAPIMapper;

    // 创建动态字符串，用来每次将输出的内容增加到动态字符串里面，用来获取最终回答文本
    public StringBuilder text = new StringBuilder();
    private static Generation gen;
    private static MessageManager msgManager;
    public GetAPIServiceImpl() {
        // 初始化MessageManager和Generation对象，只需初始化一次
        msgManager = new MessageManager(10000);
        gen = new Generation();
        // 添加设定好系统指令
        msgManager.add(Message.builder().role(Role.SYSTEM.getValue())
                .content(Constants.INSTRUCT_TEXT)
                .build());
    }

    /**
     * ai多轮会话
     * @param question
     * @param response
     * @throws NoApiKeyException
     * @throws InputRequiredException
     * @throws IOException
     */
    public void setAi(String question, HttpServletResponse response) throws NoApiKeyException, InputRequiredException, IOException {
        /**
         * 多轮会话解决，明天解决消息记录问题
         */

        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        // 添加用户询问的消息

        msgManager.add(Message.builder().role(Role.USER.getValue()).content(question).build());
        QwenParam param = QwenParam.builder()
                .model(Generation.Models.QWEN_MAX)
                .messages(msgManager.get())
                .resultFormat(QwenParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .apiKey(Constants.apiKey)
                // 流式输出
                .incrementalOutput(true)
                .build();
        Flowable<GenerationResult> resultStream = gen.streamCall(param);
        // 创建动态字符串，用来每次将输出的内容增加到动态字符串里面，用来获取最终回答文本，保存到text中
        StringBuilder fullContent = new StringBuilder();
        PrintWriter writer = response.getWriter();
        resultStream.blockingForEach(message ->{
            String output = message.getOutput().getChoices().get(0).getMessage().getContent();
            System.out.println(output);
            fullContent.append(output);
            String out = Base64.getEncoder().encodeToString(output.getBytes("UTF-8"));
            writer.write("data:"+out+"\n\n");
            writer.flush();

        });
        // 添加助手的回应
        msgManager.add(Message.builder().role(Role.ASSISTANT.getValue()).content(fullContent.toString()).build());
        text = fullContent;


    }

    /**
     * 新增历史记录
     * @param messageManager
     */
    public Boolean addHistory(String messageManager, Long uerId){
        HistoryMessageManager historyMessageManager = new HistoryMessageManager();
        historyMessageManager.setUser_id(uerId);
        historyMessageManager.setMessageManager(messageManager);
        historyMessageManager.setCreateTime(LocalDateTime.now());
        historyMessageManager.setUpdateTime(LocalDateTime.now());
        if (getAPIMapper.insertHistoryMessageManager(historyMessageManager)>0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 查询历史记录
     */
    public List<HistoryMessageManager> list(Long userId){
        return getAPIMapper.selectHistoryMessageManager(userId);
    }

    /**
     * 获取每次ai回答的完整文本，用于word生成
     * @return
     */
    public String getAiText(){
        return text.toString();
    }

    /**
     * 获取msgManager
     * @return
     */
    public MessageManager getMsgManager() {
        return msgManager;
    }
}
