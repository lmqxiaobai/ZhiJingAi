package com.example.zhijingai.controller.admin;

import com.example.zhijingai.entitys.dto.ClassificationDTO;
import com.example.zhijingai.entitys.dto.TemplateDTO;
import com.example.zhijingai.result.Result;
import com.example.zhijingai.service.TemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端模板选择功能接口
 */
@RestController
@RequestMapping("/user/template")
@Slf4j
@Api(tags = "管理端模板界面相关处理")
public class AdminTemplateController {
    @Autowired
    TemplateService templateService;


    /**
     * 管理端添加模板
     * @param templateDTO
     * @return
     */
    @ApiOperation("管理端添加模板template")
    @PostMapping("saveTemplate")
    public Result saveTemplate(@RequestBody TemplateDTO templateDTO){
        templateService.save(templateDTO);
        return Result.success();
    }

    /**
     * 管理端添加类别表total和其对应的classification分类字段
     */
    @ApiOperation("管理端添加大分类total和其对应的小分类classification字段")
    @PostMapping("saveTotalAndClass")
    public Result saveTotal(@RequestBody ClassificationDTO classificationDTO){
        templateService.saveTotalAndClass(classificationDTO);
        return Result.success();
    }

    /**
     * 管理端单独添加类别表total
     */
    @ApiOperation("管理端单独添加大分类total")
    @PostMapping("saveTotal")
    public Result saveTotal(String totalName){
        templateService.saveTotal(totalName);
        return Result.success();
    }

    /**
     * 管理端单独添加分类字段classification
     */
    @ApiOperation("管理端单独添加小分类字段classification")
    @PostMapping("saveClass")
    public Result saveClass(String className, Long totalId){
        if(templateService.saveClass(className,totalId)){
            return Result.success();
        }
        return Result.error("不存在对应类别");
    }

    /**
     * 管理端批量删除模板
     */
    @DeleteMapping("/deleteTemplate")
    @ApiOperation("管理端批量删除模板")
    public Result deleteTemplate(@RequestParam List<Long> ids){
        templateService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 管理端修改模板
     */
    @PutMapping("/update")
    @ApiOperation("管理员修改模板内容")
    public Result update(@RequestBody TemplateDTO templateDTO){
        templateService.update(templateDTO);
        return Result.success();
    }

}

