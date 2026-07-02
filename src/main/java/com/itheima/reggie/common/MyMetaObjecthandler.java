package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */
@Component
@Slf4j
public class MyMetaObjecthandler implements MetaObjectHandler {
    /**
     * 插入操作时自动填充公共字段
     * 该方法会在插入数据库记录时自动设置创建时间、更新时间、创建用户和更新用户字段
     * @param metaObject 元对象，用于操作实体类的属性值
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());

        // 设置创建时间和更新时间为当前时间
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        // 设置创建用户和更新用户为当前登录用户ID
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }


    /**
     * 更新操作的自动填充方法，用于在更新数据时自动设置更新时间和更新用户字段
     * 实现了自动填充更新时间戳和当前操作用户的逻辑
     *
     * @param metaObject 元对象，包含需要更新的实体对象信息
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());

        // 获取当前线程ID用于日志追踪
        long id = Thread.currentThread().getId();
        log.info("线程id为：{}",id);

        // 设置更新时间字段为当前时间
        metaObject.setValue("updateTime",LocalDateTime.now());
        // 设置更新用户字段为当前操作用户ID
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }

}
