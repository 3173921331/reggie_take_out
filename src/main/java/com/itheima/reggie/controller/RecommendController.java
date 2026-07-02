package com.itheima.reggie.controller;

import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 智能推荐 Controller
 * 处理与"猜你喜欢"功能相关的前端请求。
 * 基础请求路径：/recommend
 */
@RestController
@RequestMapping("/recommend")
@Slf4j
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取"猜你喜欢"菜品列表
     * 请求路径: GET /recommend/list
     * 场景说明: 用户在移动端首页浏览时，展示基于其历史偏好的推荐菜品。
     * 优化：未登录时也能返回全站热销菜品，提升用户体验
     * @return R<List<DishDto>> 统一返回结果，data字段包含封装好的菜品DTO列表（含口味、分类名）
     */
    @GetMapping("/list")
    public R<List<DishDto>> list() {
        // 获取当前登录用户的ID（可能为null，未登录状态）
        Long currentId = BaseContext.getCurrentId();

        log.info("用户 {} 请求智能推荐菜品...", currentId);

        // 调用 Service 获取推荐结果
        // Service 层会根据此 ID 自动分析历史订单或进行热销补全，返回处理好的 DishDto 列表
        return recommendService.getRecommendDishes(currentId);
    }
}
