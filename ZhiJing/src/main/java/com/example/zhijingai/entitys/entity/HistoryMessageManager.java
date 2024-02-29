package com.example.zhijingai.entitys.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 历史信息实体类
 */
@Data
@Builder
// 生成一个无参的构造方法
@NoArgsConstructor
// 生成一个包含所有类成员变量的参数的构造方法
@AllArgsConstructor
public class HistoryMessageManager implements Serializable {
    // 记录id
    private Integer record_id;
    // 历史记录拥有者用户id
    private Long user_id;
    // 历史记录
    private String messageManager;
    // 创建时间
    private LocalDateTime createTime;
    // 修改时间
    private LocalDateTime updateTime;
}
