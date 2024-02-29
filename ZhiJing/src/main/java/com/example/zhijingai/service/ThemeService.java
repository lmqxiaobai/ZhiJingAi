package com.example.zhijingai.service;

import com.example.zhijingai.entitys.dto.ThemeDTO;
import com.example.zhijingai.entitys.dto.ThemePageQueryDTO;
import com.example.zhijingai.entitys.vo.ThemeVO;
import com.example.zhijingai.result.PageResult;

import java.util.List;

public interface ThemeService {

    /**
     * 添加ppt主题
     * @param themeDTO
     */
    void addTheme(ThemeDTO themeDTO);

    /**
     * 根据id查询主题
     * @param id
     * @return
     */
    ThemeVO selectById(Integer id);

    /**
     * 分页查询ppt主题信息
     * @param themePageQueryDTO
     * @return
     */
    PageResult pageQuery(ThemePageQueryDTO themePageQueryDTO);

    /**
     * 修改主题信息和参数
     * @param themeDTO
     */
    void updateTheme(ThemeDTO themeDTO);

    /**
     * 删除主题信息和参数
     * @param ids
     */
    void deleteTheme(List<Integer> ids);

    /**
     * 设置ppt主题的启用禁用状态
     * @param status
     * @param id
     */
    void updateStatusById(int status, int id);
}
