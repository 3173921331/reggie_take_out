package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Orders;

import java.util.Date;

public interface OrderService extends IService<Orders> {
    R<String> submitOrder(Orders orders);

    Page<Orders> pageOrders(int page, int pageSize, String number, Date beginTime, Date endTime);
}
