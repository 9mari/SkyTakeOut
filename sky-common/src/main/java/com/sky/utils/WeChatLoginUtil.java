package com.sky.utils;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.LoginConstant;
import com.sky.constant.MessageConstant;
import com.sky.exception.LoginFailedException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;

@AllArgsConstructor
public class WeChatLoginUtil {
    public final static String APPID = "appid";
    public final static String SECRET = "secret";
    public final static String JS_CODE = "js_code";
    public final static String GRANT_TYPE = "grant_type";
    public final static String OPENID = "openid";

    private String appid;
    private String secret;
    private String grantType;
    private String url;

    public String login (String code){
        HashMap<String,String> map = new HashMap<>();
        map.put(APPID,appid);
        map.put(SECRET,secret);
        map.put(JS_CODE,code);
        map.put(GRANT_TYPE,grantType);
        String json = HttpClientUtil.doGet(url, map);
        JSONObject jsonObject = JSONObject.parseObject(json);
        return jsonObject.getString(OPENID);
    }

}
