package com.cnki.cqmuseum.bean;

/**
 * evenbus实体基类
 * Created by liweidong on 2019/4/1.
 */

public class BaseEvenBusBean<T> {

    private String tag;//标志
    private T object;
    private String robotMsg;//机器人默认语料

    public BaseEvenBusBean(String tag){
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public String getRobotMsg() {
        return robotMsg;
    }

    public void setRobotMsg(String robotMsg) {
        this.robotMsg = robotMsg;
    }
}
