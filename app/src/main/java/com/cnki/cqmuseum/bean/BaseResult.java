package com.cnki.cqmuseum.bean;

import java.util.ArrayList;

/**
 * 接口返回结果基类
 * Created by liweidong on 2019/11/7.
 */

public class BaseResult<T> {
    public int code;
    public String msg;
    public T result;
    public String time;
}
