package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户优惠券关联实体
 */
@Data
@TableName("user_coupon")
public class UserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    // 主键，使用雪花算法生成ID（或者根据你的数据库设置选择 AUTO）
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // 用户ID
    private Long userId;

    // 优惠券ID
    private Long couponId;

    // 状态: 0未使用, 1已使用, 2已过期
    private Integer status;

    // 领取时间
    private LocalDateTime receiveTime;

    // 使用时间
    private LocalDateTime useTime;
}