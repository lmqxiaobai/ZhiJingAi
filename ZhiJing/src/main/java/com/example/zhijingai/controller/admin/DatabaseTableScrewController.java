package com.example.zhijingai.controller.admin;

import com.example.zhijingai.service.DatabaseTableScrew;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/databaseTableScrew")
@Api(tags = "数据库表结构文档生成工具")
public class DatabaseTableScrewController {
    @Autowired
    private DatabaseTableScrew databaseTableScrew;

    /**
     * 生成数据库表结构文档
     */
    @ApiOperation("数据库表结构文档生成工具接口")
    @GetMapping("/generatorDocument")
    public void generatorDocument(){

        databaseTableScrew.generatorDocument();
    }
}
