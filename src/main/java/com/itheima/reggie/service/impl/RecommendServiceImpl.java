package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能推荐服务实现类
 * 核心策略：
 * 1. 个性化：基于用户最近的订单历史，挖掘其高频点选的菜品。
 * 2. 兜底策略：当用户历史数据不足（新用户）或偏好菜品下架时，自动补全全站热销菜品。
 * 3. 数据完整性：不仅返回菜品基本信息，还封装了分类名称和口味列表，确保前端能直接使用。
 */

@Service
public class RecommendServiceImpl implements RecommendService {
    // 订单服务：用于获取用户的下单记录
    // 注入现有的 Service 或 Mapper 来获取数据
    @Autowired
    private OrderService orderService;

    // 订单明细服务：用于统计具体吃了哪些菜，以及全站热销统计
    @Autowired
    private OrderDetailService orderDetailService;

    // 菜品服务：用于查询菜品的详细信息（图片、价格、状态等）
    @Autowired
    private DishService dishService;

    // 分类服务：用于将分类ID转换为分类名称展示
    @Autowired
    private CategoryService categoryService; // 需要注入分类服务查询分类名

    // 口味服务：用于获取菜品的规格/口味
    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 实现接口定义的方法
     * 获取推荐菜品列表
     *
     * @param userId 当前登录用户的ID
     * @return 封装了完整信息的菜品DTO列表
     */
    @Override
    public R<List<DishDto>> getRecommendDishes(Long userId) {
        // 最终推荐的菜品列表
        List<Dish> recommendDishes = new ArrayList<>();
        // 记录已选中的ID，用于排重
        Set<Long> selectedIds = new HashSet<>();

        // ==========================================
        // 1. 历史记录优先 (个性化) - 仅登录用户有
        // ==========================================
        if (userId != null) {
            // 查询当前用户最近100条已完成的订单
            List<Orders> historyOrders = orderService.lambdaQuery()
                    .eq(Orders::getUserId, userId)
                    .eq(Orders::getStatus, 4) // 4代表已完成
                    .orderByDesc(Orders::getOrderTime)
                    .last("limit 100")
                    .list();

            if (!historyOrders.isEmpty()) {
                List<Long> orderIds = historyOrders.stream().map(Orders::getId).collect(Collectors.toList());
                // 获取这些订单里的菜品
                List<OrderDetail> details = orderDetailService.lambdaQuery()
                        .in(OrderDetail::getOrderId, orderIds)
                        .list();

                // 统计购买频次最高的5个菜品ID（过滤掉套餐，只统计菜品）
                List<Long> favoriteIds = details.stream()
                        .filter(d -> d.getDishId() != null) // 过滤掉dishId为null的套餐记录
                        .collect(Collectors.groupingBy(OrderDetail::getDishId, Collectors.counting()))
                        .entrySet().stream()
                        .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                        .limit(5)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                if (!favoriteIds.isEmpty()) {
                    // 查询具体的菜品对象（必须是起售状态）
                    List<Dish> favorites = dishService.lambdaQuery()
                            .in(Dish::getId, favoriteIds)
                            .eq(Dish::getStatus, 1)
                            .list();
                    recommendDishes.addAll(favorites);
                    favorites.forEach(d -> selectedIds.add(d.getId()));
                }
            }
        }

        // ==========================================
        // 2. 全站热销保底 (补齐至5个)
        // ==========================================
        int minRecommendDishes = 5;
        if (recommendDishes.size() < minRecommendDishes) {
            int needCount = minRecommendDishes - recommendDishes.size();

            // 2.1 先获取所有已完成订单的ID
            List<Orders> completedOrders = orderService.lambdaQuery()
                    .eq(Orders::getStatus, 4) // 核心修复：只选状态为4（已完成）的订单
                    .select(Orders::getId)
                    .list();

            // 如果全站没有任何已完成订单，则不执行热销统计，防止统计到无效订单
            if (!completedOrders.isEmpty()) {
                List<Long> completedOrderIds = completedOrders.stream()
                        .map(Orders::getId)
                        .collect(Collectors.toList());

                // 2.2 统计 已完成订单 中的热销菜品（只统计菜品，排除套餐）
                QueryWrapper<OrderDetail> hotQuery = new QueryWrapper<>();
                hotQuery.select("dish_id", "sum(number) as totalCount") // 建议用sum(number)统计真实份数而非行数
                        .in("order_id", completedOrderIds) // 将统计范围限制在已完成订单内
                        .isNotNull("dish_id") // 排除套餐记录，只统计菜品
                        .groupBy("dish_id")
                        .orderByDesc("totalCount")
                        .notIn(!selectedIds.isEmpty(), "dish_id", selectedIds)
                        .last("limit " + needCount);

                List<Map<String, Object>> hotMaps = orderDetailService.listMaps(hotQuery);
                if (hotMaps != null && !hotMaps.isEmpty()) {
                    List<Long> hotIds = hotMaps.stream()
                            .map(m -> Long.parseLong(m.get("dish_id").toString()))
                            .collect(Collectors.toList());

                    List<Dish> hotDishes = dishService.lambdaQuery()
                            .in(Dish::getId, hotIds)
                            .eq(Dish::getStatus, 1) // 必须是起售状态
                            .list();
                    recommendDishes.addAll(hotDishes);
                }
            }
        }

        // ==========================================
        // 3. 数据封装 (封装 DTO 供前端展示)
        // ==========================================
        List<DishDto> dishDtoList = recommendDishes.stream().map(dish -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);

            // 3.1 关联分类名称
            Category category = categoryService.getById(dish.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }

            // 3.2 关联口味列表
            dishDto.setFlavors(dishFlavorService.lambdaQuery().eq(DishFlavor::getDishId, dish.getId()).list());

            // 3.3 统计真正的“月销量” (近30天已完成订单的份数总和)

            // 获取近30天已完成订单的ID列表
            java.time.LocalDateTime thirtyDaysAgo = java.time.LocalDateTime.now().minusDays(30);
            List<Long> completedOrderIds = orderService.lambdaQuery()
                    .eq(Orders::getStatus, 4) // 必须是已完成
                    .ge(Orders::getOrderTime, thirtyDaysAgo) // 必须是30天内
                    .select(Orders::getId)
                    .list()
                    .stream().map(Orders::getId).collect(java.util.stream.Collectors.toList());

            Integer totalSales = 0;
            if (!completedOrderIds.isEmpty()) {
                //  累加这些订单中该菜品的 number 字段 (SUM)
                QueryWrapper<OrderDetail> sumQuery = new QueryWrapper<>();
                sumQuery.select("sum(number) as totalSales")
                        .eq("dish_id", dish.getId())
                        .in("order_id", completedOrderIds);

                Map<String, Object> map = orderDetailService.getMap(sumQuery);
                if (map != null && map.get("totalSales") != null) {
                    totalSales = Integer.valueOf(map.get("totalSales").toString());
                }
            }

            dishDto.setSaleNum(totalSales); // 设置正确的月销量

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
}
