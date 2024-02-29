package com.example.zhijingai.entitys.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceVO {
    private Long id;

    private Integer status;

    private String file;

    private String name;

    private String photo;
}
