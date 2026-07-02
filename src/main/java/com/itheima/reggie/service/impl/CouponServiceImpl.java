package com.itheima.reggie.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Coupon;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.entity.UserCoupon;
import com.itheima.reggie.mapper.CouponMapper;
import com.itheima.reggie.service.CouponService;
import com.itheima.reggie.service.UserCouponService;
import com.itheima.reggie.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements CouponService {

    @Autowired
    private UserService userService; // 需要查所有用户

    @Autowired
    private UserCouponService userCouponService; // 需要插入关联表

    @Override
    @Transactional
    public void distributeToAll(Long couponId) {
        // 1. 查出所有用户的 ID
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(User::getId); // 只查 ID，性能优化
        List<User> users = userService.list(queryWrapper);

        if (users == null || users.isEmpty()) {
            return;
        }

        // 2. 准备批量插入的数据
        List<UserCoupon> userCoupons = users.stream().map(user -> {
            UserCoupon uc = new UserCoupon();
            uc.setUserId(user.getId());
            uc.setCouponId(couponId);
            uc.setReceiveTime(LocalDateTime.now());
            uc.setStatus(0); // 0:未使用
            return uc;
        }).collect(Collectors.toList());

        // 3. 批量插入到 user_coupon 表
        userCouponService.saveBatch(userCoupons);
    }

}