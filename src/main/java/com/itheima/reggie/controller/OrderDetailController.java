package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.itheima.reggie.common.R;

/**
 * 订单明细
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户端展示自己的订单分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize) {
        return orderDetailService.pageOrderDetail(page, pageSize);
    }
}