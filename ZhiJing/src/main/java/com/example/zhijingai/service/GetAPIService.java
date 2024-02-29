package com.example.zhijingai.service;

import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.example.zhijingai.entitys.entity.HistoryMessageManager;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface GetAPIService {
    /**
     * Ai多轮会话
     * @param question
     * @param response
     * @throws NoApiKeyException
     * @throws InputRequiredException
     * @throws IOException
     */
    void setAi(String question, HttpServletResponse response) throws NoApiKeyException, InputRequiredException, IOException;

    /**
     * 新增ai会话历史记录
     */
    Boolean addHistory(String messageManager, Long userId);


    /**
     * 查询历史记录
     */
    List<HistoryMessageManager> list(Long userId);

    /**
     * 获取每次ai回答的完整文本，用于word生成
     * @return
     */
    String getAiText();

    /**
     * 获取msgManager
     * @return
     */
    MessageManager getMsgManager();
}
