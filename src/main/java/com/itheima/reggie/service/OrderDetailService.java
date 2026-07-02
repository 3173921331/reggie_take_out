package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.OrderDetail;

public interface OrderDetailService extends IService<OrderDetail> {
    R<Page> pageOrderDetail(int page, int pageSize);

}
