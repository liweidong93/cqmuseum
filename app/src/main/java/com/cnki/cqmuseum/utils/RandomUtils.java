package com.cnki.cqmuseum.utils;

import com.cnki.cqmuseum.constant.RobotKeyConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 取随机工具类
 */
public class RandomUtils {

    /**
     * 使用一个List来保存数组，每次随机取出一个移除一个。
     */
    public static ArrayList<String> getRandomArray(int n, String[] list) {
        Random random = new Random();
        List<String> strs = new ArrayList<String>();
        for (int i = 0; i < list.length; i++) {
            strs.add(list[i]);
        }
        // 当取出的元素个数大于数组的长度时，返回null
        if (n > strs.size()) {
            return null;
        }

        ArrayList<String> results = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            // 去一个随机数，随机范围是list的长度
            int index = random.nextInt(strs.size());
            results.add(strs.get(index));
            strs.remove(index);
        }
        return results;
    }

    /**
     * 获取随机聊天
     */
    public static String getRandomChat() {
        Random random = new Random();
        List<String> strs = new ArrayList<String>();
        for (int i = 0; i < RobotKeyConstant.chatLists.length; i++) {
            strs.add(RobotKeyConstant.chatLists[i]);
        }
        int index = random.nextInt(strs.size());
        return strs.get(index);
    }
}
