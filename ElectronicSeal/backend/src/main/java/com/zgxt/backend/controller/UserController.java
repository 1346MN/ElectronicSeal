package com.zgxt.backend.controller;

import com.zgxt.backend.bean.LoginBean;
import com.zgxt.backend.bean.RegisterBean;
import com.zgxt.backend.entity.UserEntity;
import com.zgxt.backend.service.UserService;
import com.zgxt.backend.util.AjaxResult;
import com.zgxt.backend.util.TokenUtil;
import com.zgxt.backend.util.seal.Seal;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户页面控制器
 */
@RestController
@Log4j2
public class UserController {
    @Resource
    private UserService userService;

    @ApiOperation(value = "登录",notes = "输入用户名和密码登录")
    @ApiImplicitParam(name = "loginBean",value = "用户登录信息",required = true,dataType = "LoginBean")
    @PostMapping("/user/login")
    public AjaxResult<String> login(@RequestBody LoginBean loginBean) {
        log.info(loginBean.toString());
        if (userService.isLogin(loginBean)) {
            //创建
            String token = TokenUtil.sign(loginBean.getUsername(),String.valueOf(System.currentTimeMillis()));
            AjaxResult<String> result = new AjaxResult<>(200, "login ok");
            result.setData(token);
            return result;
        } else {
            return new AjaxResult<String>(403,"login fail");
        }
    }

    @ApiOperation(value = "注册",notes = "根据填写信息注册")
    @ApiImplicitParam(name = "registerBean",value = "用户注册信息",required = true,dataType = "RegisterBean")
    @PostMapping("/user/register")
    public AjaxResult<String> register(@RequestBody RegisterBean registerBean) {
        log.info(registerBean.toString());
        if (userService.register(registerBean)) {
            return new AjaxResult<String>(200,"register ok");
        } else {
            return new AjaxResult<String>(403,"register fail");
        }
    }

    @ApiOperation(value = "用户信息",notes = "获取用户详细信息")
    @GetMapping("/user/info")
    public AjaxResult<RegisterBean> getUserInfo(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String username = TokenUtil.getLoginName(token);
        RegisterBean registerBean = userService.getUser(username);
        AjaxResult<RegisterBean> result = new AjaxResult<>(200, "ok");
        result.setData(registerBean);
        return result;
    }

    @ApiOperation(value = "印章",notes = "获取用户印章")
    @GetMapping("/user/seal")
    public AjaxResult<String> getUserSeal(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String username = TokenUtil.getLoginName(token);
        String sealImg = userService.getSealImg(username);
        AjaxResult<String> result = new AjaxResult<>(200, "ok");
        result.setData(sealImg);
        return result;
    }
}
