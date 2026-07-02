package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.common.R;

import java.util.List;

public interface ShoppingCartService extends IService<ShoppingCart> {
    R<ShoppingCart> subDishOrSetmeal(ShoppingCart shoppingCart);

    R<ShoppingCart> addDishOrSetmeal(ShoppingCart shoppingCart);

    R<String> cleanShoppingCart();

    R<List<ShoppingCart>> selectShoppingCart();
}
