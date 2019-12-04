package com.cnki.cqmuseum.bean;

import java.util.ArrayList;

/**
 * Created by liweidong on 2019/8/22.
 */

public class PandaBean {
    public String oneName;
    public boolean isOpen;
    public ArrayList<SecondItem> secondItems;

    public static class SecondItem{
        public String secondName;
        public boolean isOpen;
        public ArrayList<String> threeNames;
    }
}
