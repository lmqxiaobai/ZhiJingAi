package com.example.zhijingai.entitys.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 模板表实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Template implements Serializable {
    // id
    private Long id;
    // 模板名称
    private String name;
    // 行业表id
    private Long industryId;
    // 类别表id
    private Long categoryId;
    // 图片路径
    private String image;
    // 文档文件
    private byte[] documentation;
    // 状态 1 可以编辑 0 不能编辑
    private int status;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}
