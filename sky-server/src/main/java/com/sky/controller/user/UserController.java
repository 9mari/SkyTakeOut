package com.sky.controller.user;

import com.sky.constant.LoginConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.result.Result;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RequestMapping("/user/user")
@RestController
public class UserController {

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        HashMap<String,String> map = new HashMap<>();
        map.put("appid","wx4fb499b50278528a");
        map.put("secret","d5984cf6662e7ba9596ea23d45260646");
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type","authorization_code");
        String code = HttpClientUtil.doGet(LoginConstant.WX_LOGIN_URL,map);
        return Result.success();
    }
}
