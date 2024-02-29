package com.example.zhijingai.service.impl;

import com.example.zhijingai.entitys.dto.ClassificationDTO;
import com.example.zhijingai.entitys.dto.PageQueryDTO;
import com.example.zhijingai.entitys.dto.TemplateDTO;
import com.example.zhijingai.entitys.dto.TemplatePageQueryDTO;
import com.example.zhijingai.entitys.entity.*;
import com.example.zhijingai.mapper.TemplateMapper;
import com.example.zhijingai.result.PageResult;
import com.example.zhijingai.service.TemplateService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    TemplateMapper templateMapper;

    /**
     * 查询模板表中所有数据的分页查询
     */
    @Transactional
    public PageResult allTemplatePageQuery(PageQueryDTO pageQueryDTO){
        PageHelper.startPage(pageQueryDTO.getPage(),pageQueryDTO.getPageSize());

        Page<Template> page = templateMapper.allPageQuery();
        return new PageResult(page.getTotal(),page.getResult());
    }
    /**
     * 用户端模板根据属性分类的分页查询
     */
    @Transactional
    public PageResult pageQuery(TemplatePageQueryDTO templatePageQueryDTO){
        // 开始分页查询
        /*
         * 在数据库查询之前，调用PageHelper.startPage方法,会对紧随其后的第一个查询进行分页设置.之后的查询不受影响，除非再次调用PageHelper.startPage。
         * PageHelper.startPage 方法会自动将分页参数添加到 SQL 查询中，实现数据库的分页查询。
         * 其中用到了ThreadLocal，在一个线程内进行，当在数据库查询时，先会进行分页查询
         */
        PageHelper.startPage(templatePageQueryDTO.getPage(),templatePageQueryDTO.getPageSize());

        Page<Template> page = templateMapper.pageQuery(templatePageQueryDTO.getClassificationIds(),templatePageQueryDTO.getClassificationIds().size());
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 管理端的总表信息集合查询
     */
    public List<Total> totalList(){
        return templateMapper.totalList();
    }

    /**
     * 管理端根据分类id去对应查询相关类别数据集合
     */
    public List<Classification> classList(Long totalId){
        return templateMapper.classList(totalId);
    }

    /**
     * 管理端添加模板
     */
    public void save(TemplateDTO templateDTO){
        // 将其他数据添加进template表中，并获取到id给下一个表添加使用
        Template1 template1 = new Template1();
        BeanUtils.copyProperties(templateDTO,template1);
        templateMapper.insert(template1);
        // 将classificationId分类id集合动态加入到connects表中，首先得到template表中添加数据后的id
        Long templateId = template1.getId();
        List<Long> classificationIds = templateDTO.getClassificationId();

        if(classificationIds != null && classificationIds.size() >0){
            for(Long classificationId : classificationIds){
                templateMapper.insertConnect(classificationId,templateId);
            }
        }
    }

    /**
     * 管理端添加分类字段
     */
    @Transactional
    public void saveTotalAndClass(ClassificationDTO classificationDTO){
        // 将第一级分类名称分类存在total表中，然后获取其刚生成的id给后面使用
        Total total = new Total();
        total.setName(classificationDTO.getName());
        templateMapper.insertTotal(total);

        // 将第二级分类名称和第一级分类id存在classification表中
        Long totalId = total.getId();
        Classification classification = new Classification();
        classification.setName(classificationDTO.getClassificationName());
        classification.setTotalId(totalId);
        templateMapper.insertClassification(classification);
    }
    /**
     * 单独添加第一级类别名称
     */
    public void saveTotal(String totalName){
        Total total = new Total();
        total.setName(totalName);
        templateMapper.insertTotal(total);
    }

    /**
     * 管理端单独添加分类字段classification
     */
    public Boolean saveClass(String className,Long totalId){
        Total total = templateMapper.selectByIdTotal(totalId);
        if(total != null){
            Classification classification = new Classification();
            classification.setTotalId(totalId);
            classification.setName(className);
            templateMapper.insertClassification(classification);
            return true;
        }else{
            return false;
        }

    }

    /**
     * 管理端批量删除模板
     */
    @Transactional
    public void deleteBatch(List<Long> ids){
        // 先删除模板表与分类表中的对应数据，根据id去删除
        for(Long id : ids){
            templateMapper.deleteConnects(id);
        }
        // 再删除模板表对应的数据
        for(Long id : ids){
            templateMapper.deleteTemplate(id);
        }
    }

    /**
     * 管理端修改模板
     */
    public void update(TemplateDTO templateDTO){
        Template1 template = new Template1();
        BeanUtils.copyProperties(templateDTO,template);
        // 修改内容
        // 公共字段填充
        templateMapper.update(template);
    }
}
