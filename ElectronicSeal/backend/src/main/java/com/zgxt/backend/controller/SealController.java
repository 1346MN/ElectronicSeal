package com.zgxt.backend.controller;

import cn.hutool.http.HtmlUtil;
import com.zgxt.backend.bean.*;
import com.zgxt.backend.service.SealService;
import com.zgxt.backend.service.UserService;
import com.zgxt.backend.util.AjaxResult;
import com.zgxt.backend.util.TokenUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 印章页面控制器
 */
@RestController
@Log4j2
public class SealController {
    @Resource
    private SealService sealService;
    @Resource
    private UserService userService;

    @ApiOperation(value = "签章",notes = "上传签章图片信息")
    @ApiImplicitParam(name = "imgBase64",value = "签章图片Base64编码字符串",required = true,dataType = "String")
    @PostMapping("/seal/signature")
    public AjaxResult<String> signature(@RequestBody ContractBean contractBean, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String username = TokenUtil.getLoginName(token);
        contractBean.setUsername(username);
        RegisterBean registerBean = userService.getUser(username);

        if (sealService.signature(contractBean,registerBean.getChainAccount())) {
            return new AjaxResult<String>(200,"ok");
        } else {
            return new AjaxResult<String>(404,"fail");
        }
    }

    @ApiOperation(value = "签章记录",notes = "获取签章详细信息")
    @GetMapping("/seal/record")
    public AjaxResult<List<RecordBean>> getRecords(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String username = TokenUtil.getLoginName(token);
        RegisterBean registerBean = userService.getUser(username);
        List<RecordBean> records = sealService.getRecord(username, registerBean.getChainAccount());
        AjaxResult<List<RecordBean>> result = new AjaxResult<>(200, "ok");
        result.setData(records);
        return result;
    }

    @ApiOperation(value = "验证签章",notes = "验证签章文档真伪")
    @PostMapping("/seal/verify")
    public AjaxResult<VerifyBean> verify(@RequestBody ImgBase64Bean imgBase64Bean, HttpServletRequest request) {
        String imgBase64 = imgBase64Bean.getImgBase64();
        String token = request.getHeader("Authorization");
        String username = TokenUtil.getLoginName(token);
        RegisterBean registerBean = userService.getUser(username);
        VerifyBean verifyBean = sealService.verify(registerBean.getChainAccount(),imgBase64);
        if (verifyBean == null){
            return new AjaxResult<VerifyBean>(403,"fail");
        }
        AjaxResult<VerifyBean> result = new AjaxResult<>(200,"ok");
        result.setData(verifyBean);
        return result;
    }
}
