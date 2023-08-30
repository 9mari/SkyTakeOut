package com.sky.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LBSYunUtil {

    //修改为你自己的AK
    private static final String LBS_AK = "ceuZtAhk0GuN8qO8VuRmYkCctqaCpezh";
    //修改为你自己的店铺地址，如果你不知道可以用parseAddress解析一次自己的店铺地址
    private static final String SHOP_LOCATION = "41.791571,123.409284";


    //匹配坐标截取6位的正则表达式
    private static final String regex = "\\d+\\.\\d{6}";
    //请求地址，如无特殊情况不要修改
    private static final String GEOCODING_URL = "https://api.map.baidu.com/geocoding/v3";
    private static final String DIRECTIONLITE_URL = "https://api.map.baidu.com/directionlite/v1/riding";

    //方法参数，如无特殊情况不要修改
    private static final String ADDRESS = "address";
    private static final String AK = "ak";
    private static final String OUTPUT = "output";
    private static final String JSON = "json";
    private static final String RESULT = "result";
    private static final String LOCATION = "location";
    private static final String ORIGIN = "origin";
    private static final String RIDING_TYPE_NAME = "riding_type";
    private static final String RIDING_TYPE = "1";  //0代表自行车 1代表电动车
    private static final String DESTINATION = "destination";
    private static final String ROUTES = "routes";
    private static final String DISTANCE = "distance";
    private static final String LNG = "lng";
    private static final String LAT = "lat";
    public static final String STATUS = "status";
    public static final String MSG = "msg";
    public static final String SUCCESS_STATUS = "0";

    //错误信息
    public static final String ADDRESS_CANNOT_NULL = "地址不能为空";
    public static final String POST_ERROR_MESSAGE = "请求百度地图接口时出现错误，百度地图返回信息为：";
    public static final String MESSAGE = "message";

    /**
     * 解析地址
     *
     * @param address 国内地址
     * @return 经纬坐标，保留6位小数
     * @throws Exception 服务器返回的错误信息
     */
    public static String parseAddress(String address) throws Exception {
        //非空校验
        if (StringUtils.isEmpty(address)) {
            throw new Exception(ADDRESS_CANNOT_NULL);
        }
        //构造参数
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put(OUTPUT, JSON);
        paramMap.put(ADDRESS, address);
        paramMap.put(AK, LBS_AK);
        //接收解析结果
        String s = HttpClientUtil.doGet(GEOCODING_URL, paramMap);
        JSONObject json = JSONObject.parseObject(s);
        String status = json.getString(STATUS);
        //如果状态码不为0抛出异常及服务器返回信息
        if (!status.equals(SUCCESS_STATUS)) {
            String msg = json.getString(MSG);
            throw new Exception(POST_ERROR_MESSAGE + msg);
        }
        //获取JSON内容
        JSONObject location = json.getJSONObject(RESULT).getJSONObject(LOCATION);
        //处理经纬度
        String lng = extractDecimal(location.getString(LNG));
        String lat = extractDecimal(location.getString(LAT));
        //返回完整坐标
        return lat + "," + lng;
    }

    /**
     * 传入一个参数计算距离，会使用该类内置的商铺地址
     *
     * @param destination 终点
     * @return 坐标
     * @throws Exception 服务器返回的错误信息
     */
    public static String distance(String destination) throws Exception {
        //非空校验
        if (StringUtils.isEmpty(destination)) {
            throw new Exception(ADDRESS_CANNOT_NULL);
        }
        //构造参数
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put(ORIGIN, SHOP_LOCATION);
        paramMap.put(DESTINATION, destination);
        paramMap.put(RIDING_TYPE_NAME, RIDING_TYPE);
        paramMap.put(AK, LBS_AK);
        //接收解析结果
        String s = HttpClientUtil.doGet(DIRECTIONLITE_URL, paramMap);
        JSONObject json = JSONObject.parseObject(s);
        String status = json.getString(STATUS);
        //如果状态码不为0抛出异常及服务器返回信息
        if (!status.equals(SUCCESS_STATUS)) {
            String msg = json.getString(MESSAGE);
            throw new Exception(POST_ERROR_MESSAGE + msg);
        }
        //提取距离信息
        JSONArray jsonArray = json.getJSONObject(RESULT).getJSONArray(ROUTES);
        return jsonArray.getJSONObject(0).getString(DISTANCE);
    }

    /**
     * 传入两个参数计算距离
     *
     * @param destination 终点
     * @param origin 起点
     * @return 距离（单位：米）
     * @throws Exception 服务器返回的错误信息
     */
    public static String distance(String destination,String origin) throws Exception {
        if (StringUtils.isEmpty(destination)) {
            throw new Exception(ADDRESS_CANNOT_NULL);
        }
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put(ORIGIN, origin);
        paramMap.put(DESTINATION, destination);
        paramMap.put(RIDING_TYPE_NAME,RIDING_TYPE);
        paramMap.put(AK, LBS_AK);
        String s = HttpClientUtil.doGet(DIRECTIONLITE_URL, paramMap);
        JSONObject json = JSONObject.parseObject(s);
        String status = json.getString(STATUS);
        if (!status.equals(SUCCESS_STATUS)) {
            String msg = json.getString(MESSAGE);
            throw new Exception(POST_ERROR_MESSAGE + msg);
        }
        JSONArray jsonArray = json.getJSONObject(RESULT).getJSONArray(ROUTES);
        return jsonArray.getJSONObject(0).getString(DISTANCE);
    }

    /**
     * 内部方法用以截取坐标，不要调用
     *
     * @param input
     * @return
     */
    private static String extractDecimal(String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }
}
