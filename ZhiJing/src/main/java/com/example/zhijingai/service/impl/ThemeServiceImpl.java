package com.example.zhijingai.service.impl;

import com.example.zhijingai.demo.exception.ThemeDataErrorException;
import com.example.zhijingai.demo.exception.ThemeNotDisabledException;
import com.example.zhijingai.demo.exception.ThemeStatusDataException;
import com.example.zhijingai.entitys.dto.ThemeDTO;
import com.example.zhijingai.entitys.dto.ThemePageQueryDTO;
import com.example.zhijingai.entitys.entity.*;
import com.example.zhijingai.entitys.vo.ThemeVO;
import com.example.zhijingai.mapper.ThemeMapper;
import com.example.zhijingai.result.PageResult;
import com.example.zhijingai.service.ThemeService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ThemeServiceImpl implements ThemeService {

    @Autowired
    private ThemeMapper themeMapper;

    /**
     * 添加ppt主题
     * @param themeDTO
     */
    @Override
    public void addTheme(ThemeDTO themeDTO) {

        List<ThemePicture> themePictureList = themeDTO.getThemePictureList();
        List<ThemeTitleStyle> themeTitleStyleList = themeDTO.getThemeTitleStyleList();
        List<ThemeSubStyle> themeSubStyleList = themeDTO.getThemeSubStyleList();
        List<ThemeTextStyle> themeTextStyleList = themeDTO.getThemeTextStyleList();
        List<ThemeEndStyle> themeEndStyleList = themeDTO.getThemeEndStyleList();

        if(themePictureList == null || themeTitleStyleList == null || themeSubStyleList == null || themeTextStyleList == null || themeEndStyleList == null) {
            throw new ThemeDataErrorException("PPT主题参数存在缺失，请完善");
        }

        //将dto中的数据复制给theme
        Theme theme = new Theme();
        BeanUtils.copyProperties(themeDTO, theme);

        theme.setCreateTime(LocalDateTime.now());
        theme.setUpdateTime(LocalDateTime.now());

        //先保存主题名称，此时在xml中会返回一个id值，此id值就是ppt的主题id
        themeMapper.insertTheme(theme);

        Integer pptId = theme.getId();

        //存入背景图片
        if(themePictureList.size() > 0) {
            themePictureList.forEach(themePicture -> {
                themePicture.setId(pptId);
            });
            themeMapper.insertPicture(themePictureList);
        }

        /*存入各种参数*/
        //标题参数
        if(themeTitleStyleList.size() > 0) {
            themeTitleStyleList.forEach(titleStyle ->
                    titleStyle.setId(pptId));
            themeMapper.insertTitleStyle(themeTitleStyleList);
        }

        //内容标题参数
        if(themeSubStyleList.size() > 0) {
            themeSubStyleList.forEach(subStyle -> {
                subStyle.setId(pptId);
            });
            themeMapper.insertSubStyle(themeSubStyleList);
        }

        //文本参数
        if(themeTextStyleList.size() > 0) {
            themeTextStyleList.forEach(textStyle ->
                    textStyle.setId(pptId));
            themeMapper.insertTextStyle(themeTextStyleList);
        }

        //结尾参数
        if(themeEndStyleList.size() > 0) {
            themeEndStyleList.forEach(endStyle ->
                    endStyle.setId(pptId));
            themeMapper.insertEndStyle(themeEndStyleList);
        }

    }

    /**
     * 根据id查询主题
     * @param id
     * @return
     */
    @Override
    public ThemeVO selectById(Integer id) {
        //获取主题基本信息
        Theme theme = themeMapper.selectThemeById(id);

        //获取主题背景图片信息
        List<ThemePicture> themePictureList = themeMapper.selectPicById(id);

        //获取标题参数信息
        List<ThemeTitleStyle> themeTitleStyleList = themeMapper.selectTitById(id);

        //获取内容标题参数信息
        List<ThemeSubStyle> themeSubStyleList = themeMapper.selectSubById(id);

        //获取文本参数信息
        List<ThemeTextStyle> themeTextStyleList = themeMapper.selectTextById(id);

        //获取结尾参数信息
        List<ThemeEndStyle> themeEndStyleList = themeMapper.selectEndById(id);

        ThemeVO themeVO = ThemeVO.builder()
                .pptName(theme.getPptName())
                .status(theme.getStatus())
                .vip(theme.getVip())
                .themePictureList(themePictureList)
                .themeTitleStyleList(themeTitleStyleList)
                .themeSubStyleList(themeSubStyleList)
                .themeTextStyleList(themeTextStyleList)
                .themeEndStyleList(themeEndStyleList)
                .build();
        return themeVO;
    }

    /**
     * 分页查询
     * @param themePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(ThemePageQueryDTO themePageQueryDTO) {
        PageHelper.startPage(themePageQueryDTO.getPage(), themePageQueryDTO.getPageSize());

        Page<Theme> page = themeMapper.pageQuery(themePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 修改主题信息和参数
     * @param themeDTO
     */
    @Override
    public void updateTheme(ThemeDTO themeDTO) {
        Theme theme = new Theme();
        BeanUtils.copyProperties(themeDTO, theme);

        theme.setUpdateTime(LocalDateTime.now());

        themeMapper.updateTheme(theme);

        Integer pptId = theme.getId();

        //修改背景图片信息
        List<ThemePicture> themePictureList = themeDTO.getThemePictureList();
    }

    /**
     * 删除主题信息和参数
     * @param ids
     */
    @Transactional
    @Override
    public void deleteTheme(List<Integer> ids) {

        //删除之前先查询是否为启用状态status = 0
        for(Integer id : ids) {
            Theme theme = themeMapper.selectThemeById(id);
            //如果为启用状态，则不能删除
            if(theme.getStatus() == 0) {
                throw new ThemeNotDisabledException("存在正在使用中的主题，无法删除");
            }
        }

        //删除主题信息及其下参数
        themeMapper.deleteTheme(ids);

        themeMapper.deleteThemePic(ids);

        themeMapper.deleteTitle(ids);

        themeMapper.deleteSub(ids);

        themeMapper.deleteText(ids);

        themeMapper.deleteEnd(ids);
    }

    /**
     * 设置ppt主题的启用禁用状态
     * @param status
     * @param id
     */
    @Override
    public void updateStatusById(int status, int id) {

        if(!(status == 0 || status == 1)) {
            throw new ThemeStatusDataException("状态参数出错，请重试");
        }
        Theme theme = Theme.builder()
                .id(id)
                .status(status)
                .build();
        themeMapper.updateTheme(theme);
    }
}
