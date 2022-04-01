package com.xingxin.controller;

import com.xingxin.entity.UserExcel;
import com.xingxin.template.impl.UserExportService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liuxh
 * @date 2022/4/1
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserExportService userExportService;

    @GetMapping("/userExport")
    public void userExport(HttpServletResponse response) {
        userExportService.defaultEasyExcelExport(response, UserExcel.class, "用户导出");
    }

}
