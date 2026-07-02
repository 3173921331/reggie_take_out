package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Coupon;
import com.itheima.reggie.entity.UserCoupon;
import com.itheima.reggie.service.CouponService;
import com.itheima.reggie.service.UserCouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/userCoupon")
@Slf4j
public class UserCouponController {

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private CouponService couponService;

    /**
     * 查询当前登录用户的可用优惠券
     */
    @GetMapping("/list")
    public R<List<Coupon>> list() {
        // 1. 获取当前用户ID
        Long userId = BaseContext.getCurrentId();

        // 2. 查询 user_coupon 表中归属该用户且状态为 0 (未使用) 的记录
        LambdaQueryWrapper<UserCoupon> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCoupon::getUserId, userId);
        queryWrapper.eq(UserCoupon::getStatus, 0); // 0:未使用
        List<UserCoupon> userCoupons = userCouponService.list(queryWrapper);

        if (userCoupons == null || userCoupons.isEmpty()) {
            return R.success(new ArrayList<>());
        }

        // 3. 取出所有的 couponId
        List<Long> couponIds = userCoupons.stream()
                .map(UserCoupon::getCouponId)
                .collect(Collectors.toList());

        // 4. 查出优惠券详情（名称、金额等）
        List<Coupon> coupons = couponService.listByIds(couponIds);

        // 5. 【关键一步】把 UserCoupon 的主键 ID 赋值给 Coupon 对象返回给前端
        // 因为前端下单时，传给后端的是 "领券记录ID" (UserCoupon.id)，而不是 "优惠券模板ID"
        for (Coupon coupon : coupons) {
            for (UserCoupon uc : userCoupons) {
                if (uc.getCouponId().equals(coupon.getId())) {
                    coupon.setId(uc.getId()); // 偷梁换柱：返回给前端的是领券记录ID
                    break;
                }
            }
        }

        return R.success(coupons);
    }
}