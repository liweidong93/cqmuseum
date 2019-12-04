package com.cnki.cqmuseum.utils;

import android.util.Log;

import com.ubtechinc.cruzr.sdk.face.StringUtils;
import com.ubtechinc.cruzr.sdk.navigation.NavigationApi;
import com.ubtechinc.cruzr.sdk.navigation.model.MapPointModel;
import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;

import java.io.File;
import java.util.List;

public class MapUtils {

    private static final String TAG = "MapUtils";

    private static class InnerClass{
        private static MapUtils instance = new MapUtils();
    }

    private MapUtils() {
    }

    public static MapUtils getInstance(){
        return InnerClass.instance;
    }

    public String getCurMapName() {
        String curMapName = "";
        try {
            //调用ROS接口获取ROS当前加载的地图路径
            String rosMapPath = RosRobotApi.get().getCurrentMap();
            Log.d(TAG, "rosMapPath in ros :" + rosMapPath);

            if (StringUtils.isEmpty(rosMapPath)) {
                Log.d(TAG, "getCurrentMap is null! Not set Map! rosMapPath:" + rosMapPath);
                return "";
            }

            //根据ROS地图路径获取地图名称
            curMapName = getFileName(rosMapPath);

            Log.d(TAG, "curMapName:" + curMapName);
        } catch (Exception e) {
            e.printStackTrace();

            curMapName = "";
        }
        return curMapName;
    }

    /**
     * Get file name
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        String fileName = "";

        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }

        int fp = filePath.lastIndexOf(File.separator);

        if (fp == -1) {
            return filePath;
        }

        fileName = filePath.substring(fp + 1);

        fp = fileName.indexOf("?");

        if (fp == -1) {
            return fileName;
        }

        fileName.substring(0, fp);


        return fileName;
    }
    /**
     * 查询所有地点坐标位置
     *
     * @param mapName
     * @return List<MapPointModel> 导航位置点列表
     */
    public  List<MapPointModel> getAllMapPointModelByMapName(String mapName) {
        List<MapPointModel> mapPointModels = NavigationApi.get().queryAllMapPointByMapName(mapName);

        return mapPointModels;
    }

    public String comparePointNameWithCurrentMap(List<MapPointModel> allMapPointModelByMapName, String pointName) {
        for (int i = 0; i < allMapPointModelByMapName.size(); i++) {
            // if (pointName.contains(allMapPointModelByMapName.get(i).getPointName())) { // 原来的方法，用文字来判断
            if (TextStyleUtils.toPinyin(pointName).contains(TextStyleUtils.toPinyin(allMapPointModelByMapName.get(i).getPointName()))) {
                return allMapPointModelByMapName.get(i).getPointName();
            }
        }
        return null;
    }

    /**
     * 判断是否包含该点
     * @param allMapPointModelByMapName
     * @param pointName
     * @return
     */
    public boolean isContainsPoint(List<MapPointModel> allMapPointModelByMapName, String pointName) {
        for (int i = 0; i < allMapPointModelByMapName.size(); i++) {
            if (allMapPointModelByMapName.get(i).getPointName().equals(pointName)){
                return true;
            }
        }
        return false;
    }
}
