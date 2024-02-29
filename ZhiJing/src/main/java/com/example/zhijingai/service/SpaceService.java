package com.example.zhijingai.service;

import com.example.zhijingai.entitys.dto.SpaceDTO;
import com.example.zhijingai.entitys.dto.SpacePageQueryDTO;
import com.example.zhijingai.entitys.entity.Space;
import com.example.zhijingai.result.PageResult;

import java.io.IOException;
import java.util.List;

public interface SpaceService {
    /**
     * 添加文件
     * @param spaceDTO
     */
    void insertSpace(SpaceDTO spaceDTO) throws IOException;

    /**
     * 批量删除
     * @param ids
     */
    void deleteList(List<Long> ids);

    /**
     * 根据字段查询文件
     * @param space
     * @return
     */
    List<Space> select(Space space);

    /**
     * 分页查询
     * @param spacePageQueryDTO
     * @return
     */
    PageResult pageQuery(SpacePageQueryDTO spacePageQueryDTO);

    /**
     * 找回文件
     * @param ids
     */
    void recover(List<Long> ids);

    /**
     * 在草稿箱里修改文档
     * @param ids
     */
    void draft(List<Long> ids);

    /**
     * 彻底删除文档
     * @param id
     */
    void deleteFormTable(Long id);

    /**
     * 修改文档信息
     * @param space
     */
    void update(Space space);
}
