package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.UserCoupon;
import com.itheima.reggie.mapper.UserCouponMapper;
import com.itheima.reggie.service.UserCouponService;
import org.springframework.stereotype.Service;

@Service
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements UserCouponService {
    // IService 内部已经实现了 saveBatch (批量保存) 方法
    // 可以在 Controller 或 CouponService 中直接调用 this.saveBatch(list)
}