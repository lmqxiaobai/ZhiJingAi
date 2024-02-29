package com.example.zhijingai.mapper;

import com.example.zhijingai.entitys.entity.HistoryMessageManager;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface GetAPIMapper {
    /**
     * 添加历史记录
     */
//    @Insert("INSERT INTO historymessagemanager (record_id, user_id, messageManager, createTime, updateTime) " +
//            "VALUES (#{record_id}, #{user_id}, #{messageManager}, #{createTime}, #{updateTime})")
    int insertHistoryMessageManager(HistoryMessageManager historyMessageManager);

    /**
     * 查询历史记录
     * @param userId
     * @return
     */

    List<HistoryMessageManager> selectHistoryMessageManager(Long userId);
}
