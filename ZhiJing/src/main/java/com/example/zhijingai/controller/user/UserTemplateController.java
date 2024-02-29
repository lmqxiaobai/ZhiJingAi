package com.example.zhijingai.controller.user;

import com.example.zhijingai.demo.constant.IpAddressConstant;
import com.example.zhijingai.entitys.dto.PageQueryDTO;
import com.example.zhijingai.entitys.dto.TemplatePageQueryDTO;
import com.example.zhijingai.entitys.entity.Classification;
import com.example.zhijingai.entitys.entity.Total;
import com.example.zhijingai.result.PageResult;
import com.example.zhijingai.result.Result;
import com.example.zhijingai.service.TemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端模板选择功能接口
 */
@RestController
@RequestMapping("/user/template")
@Slf4j
@Api(tags = "用户端模板界面相关处理")
@CrossOrigin(origins = IpAddressConstant.IPADDRESS_OPEN)
public class UserTemplateController {
    @Autowired
    TemplateService templateService;

    /**
     * 分页查询template表中所有的模板数据，用于模板界面初始化展示
     */
    @GetMapping("/allTemplatePageQuery")
    @ApiOperation("分页查询template表中所有的模板数据，用于模板界面初始化展示")
    public Result<PageResult> AllTemplatePageQuery(PageQueryDTO pageQueryDTO){
        PageResult pageResult = templateService.allTemplatePageQuery(pageQueryDTO);
        return Result.success(pageResult);
    }
    /**
     * 分页查询所有相关接口，根据前端选择不同查询返回对应数据
     * @return
     */
    @PostMapping("/pageQuery")
    @ApiOperation("根据用户属性分类选择，进行分页查询模板接口")
    public Result<PageResult> PageQuery(TemplatePageQueryDTO templatePageQueryDTO){
        PageResult pageResult = templateService.pageQuery(templatePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 用户端和管理端查询总表total相关字段。用于返回前端添加显示
     * @return
     */
    @ApiOperation("用户端和管理端查询总表total相关字段（行业、类别、场景）。用于返回前端添加显示")
    @GetMapping("/totalList")
    public Result<List<Total>> totalList(){
        List<Total> list = templateService.totalList();
        return Result.success(list);
    }

    /**
     * 用于管理端和用户端下拉框回显分类类别数据
     * @param totalId
     * @return
     */
    @ApiOperation("查询每个类别有哪些分类，用于显示每一行多个类别项")
    @GetMapping("/classList")
    public Result<List<Classification>> classList(Long totalId){
        List<Classification> list = templateService.classList(totalId);
        return Result.success(list);
    }
}
