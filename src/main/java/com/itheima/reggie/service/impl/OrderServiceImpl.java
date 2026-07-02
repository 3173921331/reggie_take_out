package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.*;
import com.itheima.reggie.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public R<String> submitOrder(Orders orders) {
        // 1. 获取用户ID
        Long userId = BaseContext.getCurrentId();

        // 2. 查询购物车
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectList(queryWrapper);

        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空不能下单!");
        }

        // 3. 查询用户 (防止空指针)
        User user = userMapper.selectById(userId);
        if(user == null){
            throw new CustomException("用户信息异常");
        }

        // 4. 查询地址
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookMapper.selectById(addressBookId);
        if (addressBook == null) {
            throw new CustomException("地址信息有误不能下单!");
        }

        long orderId = IdWorker.getId();

        // 【修正1】使用 BigDecimal 计算总金额，避免精度丢失
        AtomicReference<BigDecimal> amount = new AtomicReference<>(BigDecimal.ZERO);

        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());

            // 累加金额：单价 * 数量
            BigDecimal itemAmount = item.getAmount().multiply(new BigDecimal(item.getNumber()));
            amount.set(amount.get().add(itemAmount));

            return orderDetail;
        }).collect(Collectors.toList());

        // 原始金额
        BigDecimal originalAmount = amount.get();
        BigDecimal finalAmount = originalAmount;

        // 5. 优惠券逻辑
        Long userCouponId = orders.getCouponId();
        if (userCouponId != null) {
            UserCoupon userCoupon = userCouponService.getById(userCouponId);
            if (userCoupon != null && userCoupon.getStatus() == 0) {
                Coupon coupon = couponService.getById(userCoupon.getCouponId());
                if (coupon != null && originalAmount.compareTo(coupon.getMinSpend()) >= 0) {
                    // 计算优惠后金额
                    if (coupon.getType() == 1) { // 满减
                        finalAmount = originalAmount.subtract(coupon.getAmount());
                    } else if (coupon.getType() == 2) { // 折扣
                        finalAmount = originalAmount.multiply(coupon.getAmount()).divide(new BigDecimal("10"));
                    }
                    // 标记优惠券已使用
                    userCoupon.setStatus(1);
                    userCoupon.setUseTime(LocalDateTime.now());
                    userCouponService.updateById(userCoupon);
                }
            }
        }

        // 防止金额小于0
        if(finalAmount.compareTo(BigDecimal.ZERO) < 0){
            finalAmount = BigDecimal.ZERO;
        }

        // 6. 填充订单数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(finalAmount); // 【修正2】设置计算后的最终金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders); // 如果报错 unknown column coupon_id，请检查数据库

        // 7. 插入明细

        orderDetailService.saveBatch(orderDetails); // 建议使用 saveBatch 提高性能

        // 8. 清空购物车
        shoppingCartMapper.delete(queryWrapper);

        return R.success("下单成功");
    }

    @Override
    public Page<Orders> pageOrders(int page, int pageSize, String number, Date beginTime, Date endTime) {
        // 根据以上信息进行分页查询。
        // 创建分页对象
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        // 创建查询条件对象。
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(number), Orders::getNumber, number);
        if (beginTime != null) {
            queryWrapper.between(Orders::getOrderTime, beginTime, endTime);
        }
        ordersMapper.selectPage(pageInfo, queryWrapper);
        return pageInfo;
    }
}
