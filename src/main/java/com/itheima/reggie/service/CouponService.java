package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Coupon;
import org.springframework.transaction.annotation.Transactional;

public interface CouponService extends IService<Coupon> {
    @Transactional
    void distributeToAll(Long couponId);
}