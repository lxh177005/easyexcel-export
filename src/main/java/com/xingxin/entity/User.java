package com.xingxin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 用户扩展信息表
 * </p>
 *
 * @author liuxh
 * @since 2022-03-30
 */

@Data
@Accessors(chain = true)
@TableName("user_extra")
public class User implements Serializable {

    private static final long serialVersionUID = -3910564185071090636L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String userId;

    /**
     * 消费金额
     */
    private Double consAmount;

    /**
     * 实付金额
     */
    private Double actualAmount;

    /**
     * 消费次数
     */
    private Integer consTimes;

    /**
     * 平均折扣
     */
    private Double averDiscount;

    /**
     * 充值金额
     */
    private Double rechargeAmount;

    /**
     * 充值次数
     */
    private Integer rechargeTimes;

    /**
     * 售后金额
     */
    private Double refundAmount;

    /**
     * 售后次数
     */
    private Integer refundTimes;

    private Date createTime;

    private Date updateTime;
}
