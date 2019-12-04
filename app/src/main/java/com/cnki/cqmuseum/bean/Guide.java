package com.cnki.cqmuseum.bean;

import android.widget.RelativeLayout;

/**
 * 导航存储信息实体
 * Created by liweidong on 2019/11/8.
 */

public class Guide {
    private String name;
    private String pic;
    private String introduce;
    private RelativeLayout relativeLayout;

    public Guide(String name, String pic, String introduce, RelativeLayout relativeLayout) {
        this.name = name;
        this.pic = pic;
        this.introduce = introduce;
        this.relativeLayout = relativeLayout;
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    public void setRelativeLayout(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }
}
