package com.zgxt.backend.service;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zgxt.backend.bean.LoginBean;
import com.zgxt.backend.bean.RegisterBean;
import com.zgxt.backend.dao.UserMapper;
import com.zgxt.backend.entity.UserEntity;
import com.zgxt.backend.util.HttpUtils;
import com.zgxt.backend.util.seal.Seal;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

/**
 * 用户业务逻辑类
 */
@Service
@Log4j2
public class UserService extends ServiceImpl<UserMapper, UserEntity> {
    @Resource
    private UserMapper userMapper;

    /**
     * 登录
     *
     * @param loginBean 登录Bean
     * @return true：成功 false：失败
     */
    public boolean isLogin(LoginBean loginBean) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        loginBean.setPassword(SecureUtil.md5(loginBean.getPassword()));//md5加密密码
        queryWrapper.eq("username", loginBean.getUsername())
                .eq("password", loginBean.getPassword());
        UserEntity userEntity = userMapper.selectOne(queryWrapper);
        return userEntity != null ? true : false;
    }

    public boolean register(RegisterBean registerBean) {
        //校验用户名或账户地址是否重复
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", registerBean.getUsername())
                .or().eq("chain_account",registerBean.getChainAccount());
        UserEntity userOne = userMapper.selectOne(queryWrapper);
        if (userOne != null) {
            log.info("注册失败：用户名或账户地址重复");
            return false;
        }

        //拷贝对象
        UserEntity userEntity = new UserEntity();
        BeanUtils.copyProperties(registerBean, userEntity);
        userEntity.setPassword(SecureUtil.md5(userEntity.getPassword()));//md5加密密码

        //生成印章（Base64）
        byte[] buffer = Seal.getSealImg(registerBean.getUsername(), registerBean.getName());
        Encoder encode = Base64.getEncoder();
        String sealImg = encode.encodeToString(buffer);
        userEntity.setSealImg(sealImg);

        //用户注册信息上链
        List funcParams = new ArrayList<>();
        funcParams.add(registerBean.getChainAccount());
        funcParams.add(registerBean.getName());
        funcParams.add(registerBean.getCardId());
        funcParams.add(String.valueOf(System.currentTimeMillis()));
        JSONObject res = HttpUtils.writeContract("addSealAccount", funcParams);
        if (res == null) {
            log.info("注册失败：用户注册信息上链失败");
            return false;
        }

        int result = userMapper.insert(userEntity);

        return result > 0 ? true : false;
    }

    public RegisterBean getUser(String username) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        UserEntity userEntity = userMapper.selectOne(queryWrapper);
        RegisterBean registerBean = new RegisterBean();
        BeanUtils.copyProperties(userEntity, registerBean);
        return registerBean;
    }

    public String getSealImg(String username) {
        QueryWrapper<UserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        UserEntity userEntity = userMapper.selectOne(queryWrapper);
        return userEntity.getSealImg();
    }
}
