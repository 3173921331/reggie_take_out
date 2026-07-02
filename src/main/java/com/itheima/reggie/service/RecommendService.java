package com.itheima.reggie.service;

import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import java.util.List;

/**
 * 智能推荐服务接口
 * 提供基于用户行为分析的个性化菜品推荐功能。
 */
public interface RecommendService {

    /**
     * 获取“猜你喜欢”推荐菜品列表
     * 核心逻辑：
     * 1. 分析用户历史订单，挖掘高频喜好菜品。
     * 2. 若历史数据不足，自动补全全站热销菜品。
     *
     * @param userId 当前登录用户的ID（基于此ID分析历史口味）
     * @return 包含菜品详细信息（含口味、分类名）的推荐列表 R<List<DishDto>>
     */
    R<List<DishDto>> getRecommendDishes(Long userId);

}