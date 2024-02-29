package com.example.zhijingai.mapper;

import com.example.zhijingai.entitys.entity.*;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TemplateMapper {

    /**
     * 查询模板表中所有数据的分页查询
     */
    Page<Template> allPageQuery();

    /**
     * 用户端模板根据属性分类的分页查询
     */
    Page<Template> pageQuery(@Param("classificationIds")List<Long> classificationIds, @Param("length")int length);

    /**
     * 管理端总表的信息集合查询
     */
    List<Total> totalList();

    /**
     * 根据totalId 查询total表中对应数据
     */
    Total selectByIdTotal(Long id);

    /**
     * 管理端根据分类id去对应查询相关类别数据集合
     */
    List<Classification> classList(Long totalId);


    /**
     * 管理端添加模板信息
     */
    void insert(Template1 template1);

    /**
     * 将分类id和模板id批量添加进connects表中
     * @param
     */
    void insertConnect(Long classificationId,Long templateId);

    /**
     * 管理端添加total第一级分类名称
     */
    void insertTotal(Total total);

    /**
     * 将第二级分类名称和第一级分类id存在classification表中
     */
    void insertClassification(Classification classification);

    /**
     * 根据模板id删除connects连接表中对应的数据
     */
    @Delete("delete from connects where template_id = #{id}")
    void deleteConnects(Long id);

    /**
     * 根据模板id模板表中对应的数据
     */
    @Delete("delete from template where id = #{id}")
    void deleteTemplate(Long id);

    /**
     * 根据classification_id 在connects表中查询对应的template_id
     */
    @Select("select template_id from connects where classification_id = #{classId} ")
    Long selectTe(Long classId);

    /**
     * 管理员修改模板内容
     */
    void update(Template1 template);
}
