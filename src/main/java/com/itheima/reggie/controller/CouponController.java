package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Coupon;
import com.itheima.reggie.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 优惠券管理
 */
@RestController
@RequestMapping("/coupon")
@Slf4j
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * 新增优惠券
     */
    @PostMapping
    public R<String> save(@RequestBody Coupon coupon){
        log.info("新增优惠券: {}", coupon.toString());
        couponService.save(coupon);
        return R.success("新增优惠券成功");
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        // 1. 构造分页构造器
        Page<Coupon> pageInfo = new Page<>(page, pageSize);

        // 2. 构造条件构造器
        LambdaQueryWrapper<Coupon> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件 (根据名称模糊查询)
        queryWrapper.like(name != null, Coupon::getName, name);
        // 添加排序条件 (按创建时间降序)
        queryWrapper.orderByDesc(Coupon::getCreateTime);

        // 3. 执行查询
        couponService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据ID查询 (回显数据用)
     */
    @GetMapping("/{id}")
    public R<Coupon> getById(@PathVariable Long id){
        Coupon coupon = couponService.getById(id);
        if(coupon != null){
            return R.success(coupon);
        }
        return R.error("没有查询到对应优惠券信息");
    }

    /**
     * 修改优惠券
     */
    @PutMapping
    public R<String> update(@RequestBody Coupon coupon){
        log.info("修改优惠券: {}", coupon.toString());
        couponService.updateById(coupon);
        return R.success("修改优惠券成功");
    }

    /**
     * 删除优惠券
     */
    @DeleteMapping
    public R<String> delete(Long ids){ // 注意：这里简化处理，只接收单个id，如需批量删除请用 List<Long> ids
        log.info("删除优惠券: {}", ids);
        couponService.removeById(ids);
        return R.success("优惠券删除成功");
    }

    /**
     * 启用/禁用优惠券
     * @param status 0:禁用 1:启用
     * @param ids 优惠券ID
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable("status") Integer status, Long ids) {
        log.info("修改优惠券状态: status={}, id={}", status, ids);

        Coupon coupon = new Coupon();
        coupon.setId(ids);
        coupon.setStatus(status);

        couponService.updateById(coupon);

        return R.success("状态修改成功");
    }

    @PostMapping("/distribute")
    public R<String> distributeCoupon(@RequestParam Long couponId) {
        // 调用 Service 方法
        couponService.distributeToAll(couponId);
        return R.success("优惠券分发成功");
    }

    

}