package com.example.zhijingai.entitys.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 类别表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Classification implements Serializable {
    // id
    private Long id;
    // 分类名称
    private String name;
    // total表id,区分对应行业还是类别
    private Long totalId;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
