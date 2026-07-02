package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrderService;
import com.itheima.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单管理
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submitOrder(@RequestBody Orders orders) {
        return orderService.submitOrder(orders);
    }

    /**
     * 订单分页查询
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> page(
            int page,
            int pageSize,
            String number,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beginTime,
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        log.info(
                "订单分页查询：page={}，pageSize={}，number={},beginTime={},endTime={}",
                page,
                pageSize,
                number,
                beginTime,
                endTime);
        Page<Orders> ordersPage = orderService.pageOrders(page, pageSize, number, beginTime, endTime);
        return R.success(ordersPage);
    }

    /**
     * 修改订单状态
     * @param orders 包含订单id和目标状态status
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders) {
        log.info("修改订单状态：{}", orders);
        // 直接调用 service 的 updateById 方法更新状态
        orderService.updateById(orders);
        return R.success("订单状态修改成功");
    }

}
