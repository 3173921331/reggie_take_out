package com.itheima.reggie.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果，服务端响应的数据最终都会封装成此对象
 * @param <T>
 */
@Data
public class R<T> {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

        /**
     * 创建一个表示成功的响应对象
     *
     * @param <T> 响应数据的类型
     * @param object 要包装在响应对象中的数据
     * @return 包含成功状态码(1)和指定数据的R对象
     */
    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }


        /**
     * 创建一个表示错误响应的R对象
     *
     * @param msg 错误消息字符串，用于描述错误的具体信息
     * @param <T> 泛型类型参数，表示响应数据的类型
     * @return 返回一个R类型的错误响应对象，其中code为0表示错误状态，msg为传入的错误消息
     */
    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }


    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
