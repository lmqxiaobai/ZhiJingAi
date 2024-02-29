package com.example.zhijingai.mapper;

import com.example.zhijingai.entitys.dto.ThemePageQueryDTO;
import com.example.zhijingai.entitys.entity.*;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ThemeMapper {

    /*--------------------------------------添加---------------------------------------------*/

    /**
     * 添加ppt主题
     * @param theme
     */
    void insertTheme(Theme theme);

    /**
     * 添加背景图片
     * @param themePictureList
     */
    void insertPicture(List<ThemePicture> themePictureList);

    /**
     * 添加标题参数
     * @param themeTitleStyleList
     */
    void insertTitleStyle(List<ThemeTitleStyle> themeTitleStyleList);

    /**
     * 添加文本内容参数
     * @param themeTextStyleList
     */
    void insertTextStyle(List<ThemeTextStyle> themeTextStyleList);

    /**
     * 添加结尾参数
     * @param themeEndStyleList
     */
    void insertEndStyle(List<ThemeEndStyle> themeEndStyleList);

    /**
     * 添加内容标题参数
     * @param themeSubStyleList
     */
    void insertSubStyle(List<ThemeSubStyle> themeSubStyleList);



    /*--------------------------------------查询---------------------------------------------*/

    /**
     * 根据id查询主题
     * @param id
     * @return
     */
    @Select("select * from ppttheme where id = #{id}")
    Theme selectThemeById(Integer id);

    /**
     * 根据pptId获取主题背景图片
     * @param pptId
     * @return
     */
    @Select("select * from pptpicture where ppt_id = #{pptId}")
    List<ThemePicture> selectPicById(Integer pptId);

    /**
     * 根据pptId获取标题参数
     * @param pptId
     * @return
     */
    @Select("select * from boxtitle where ppt_id = #{pptId}")
    List<ThemeTitleStyle> selectTitById(Integer pptId);

    /**
     * 根据pptId获取文本内容参数
     * @param pptId
     * @return
     */
    @Select("select * from boxtext where ppt_id = #{pptId}")
    List<ThemeTextStyle> selectTextById(Integer pptId);

    /**
     * 根据pptId获取结尾参数
     * @param pptId
     * @return
     */
    @Select("select * from boxend where ppt_id = #{pptId}")
    List<ThemeEndStyle> selectEndById(Integer pptId);

    /**
     * 分类查询
     * @param themePageQueryDTO
     * @return
     */
    Page<Theme> pageQuery(ThemePageQueryDTO themePageQueryDTO);

    /**
     * 查询内容标题参数
     * @param pptId
     * @return
     */
    @Select("select * from boxsub where ppt_id = #{pptId}")
    List<ThemeSubStyle> selectSubById(Integer pptId);


    /*--------------------------------------修改---------------------------------------------*/

    /**
     * 修改主题基本信息
     * @param theme
     */
    void updateTheme(Theme theme);

    /**
     * 修改背景图片信息
     * @param themePicture
     */
    //void updatePic(ThemePicture themePicture);


    /*--------------------------------------删除---------------------------------------------*/

    /**
     * 批量删除主题基本信息
     * @param ids
     */
    void deleteTheme(List<Integer> ids);

    /**
     * 批量删除主题背景图片信息
     * @param ids
     */
    void deleteThemePic(List<Integer> ids);

    /**
     * 批量删除标题参数信息
     * @param ids
     */
    void deleteTitle(List<Integer> ids);

    /**
     * 删除目录参数信息
     * @param ids
     */
    void deleteSub(List<Integer> ids);

    /**
     * 删除文本参数信息
     * @param ids
     */
    void deleteText(List<Integer> ids);

    /**
     * 删除结尾参数信息
     * @param ids
     */
    void deleteEnd(List<Integer> ids);

}
