package com.example.zhijingai.entitys.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分类表与模板表的连接
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Connects implements Serializable {
    private Long id;
    // 分类id
    private Long classificationId;
    // 模板id
    private Long templateId;
}
