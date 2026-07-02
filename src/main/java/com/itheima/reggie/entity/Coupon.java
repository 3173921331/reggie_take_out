package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体
 */
@Data
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    // 优惠券名称
    private String name;

    // 类型 1:满减 2:折扣
    private Integer type;

    // 优惠金额或折扣
    private BigDecimal amount;

    // 最低消费门槛
    private BigDecimal minSpend;

    // 有效期开始
    private LocalDateTime startTime;

    // 有效期结束
    private LocalDateTime endTime;

    // 状态 0:禁用 1:启用
    private Integer status;

    // 公共字段：创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 公共字段：更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 公共字段：创建人
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    // 公共字段：更新人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}