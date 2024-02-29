package com.example.zhijingai.service;

import com.example.zhijingai.entitys.dto.ClassificationDTO;
import com.example.zhijingai.entitys.dto.PageQueryDTO;
import com.example.zhijingai.entitys.dto.TemplateDTO;
import com.example.zhijingai.entitys.dto.TemplatePageQueryDTO;
import com.example.zhijingai.entitys.entity.Classification;
import com.example.zhijingai.entitys.entity.Total;
import com.example.zhijingai.result.PageResult;

import java.util.List;

public interface TemplateService {

    /**
     * 查询模板表中所有数据的分页查询
     */
    PageResult allTemplatePageQuery(PageQueryDTO pageQueryDTO);

    /**
     * 用户端模板根据属性分类的分页查询
     */
    PageResult pageQuery(TemplatePageQueryDTO templatePageQueryDTO);

    /**
     * 管理端总表信息集合查询
     */
    List<Total> totalList();

    /**
     * 管理端根据分类id去对应查询相关类别数据集合
     */
    List<Classification> classList(Long totalId);

    /**
     * 管理端添加模板
     */
    void save(TemplateDTO templateDTO);

    /**
     * 管理端添加分类字段
     */
    void saveTotalAndClass(ClassificationDTO classificationDTO);

    /**
     * 单独添加第一级类别名称
     */
    void saveTotal(String totalName);

    /**
     * 管理端单独添加分类字段classification
     */
    Boolean saveClass(String className,Long totalId);

    /**
     * 管理端批量删除模板
     */
    void deleteBatch(List<Long> ids);

    /**
     * 管理端修改模板
     */
    void update(TemplateDTO templateDTO);
}
