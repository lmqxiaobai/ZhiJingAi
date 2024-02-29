package com.example.zhijingai.controller.user;

import com.example.zhijingai.service.ThemeService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
@Api(tags = "用户使用PPT模板")
public class UserThemeController {

    @Autowired
    private ThemeService themeService;

}
