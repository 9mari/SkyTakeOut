package com.sky.utils;

import com.alibaba.fastjson.JSONObject;
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

    private String appid;
    private String secret;
    private String grantType;
    private String url;

    public String login (String code){
        HashMap<String,String> map = new HashMap<>();
        map.put(LoginConstant.APPID,appid);
        map.put(LoginConstant.SECRET,secret);
        map.put(LoginConstant.JS_CODE,code);
        map.put(LoginConstant.GRANT_TYPE,grantType);
        String json = HttpClientUtil.doGet(url, map);
        if(ObjectUtils.isEmpty(json)){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        JSONObject jsonObject = JSONObject.parseObject(json);
        return jsonObject.getString("openid");
    }

}
