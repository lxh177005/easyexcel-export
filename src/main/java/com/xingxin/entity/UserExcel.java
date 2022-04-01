package com.xingxin.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author liuxh
 * @date 2022/3/30
 */
@Data
public class UserExcel {

    @ExcelProperty(value = "用户编号", index = 0)
    private String userId;

    @ExcelProperty(value = "消费金额", index = 1)
    private Double consAmount;

    @ExcelProperty(value = "实付金额", index = 2)
    private Double actualAmount;

    @ExcelProperty(value = "消费次数", index = 3)
    private Integer consTimes;

    @ExcelProperty(value = "平均折扣", index = 4)
    private Double averDiscount;

    @ExcelProperty(value = "充值金额", index = 5)
    private Double rechargeAmount;

    @ExcelProperty(value = "充值次数", index = 6)
    private Integer rechargeTimes;

    @ExcelProperty(value = "售后金额", index = 7)
    private Double afterSaleAmount;

    @ExcelProperty(value = "售后次数", index = 8)
    private Integer afterSaleTimes;
}
