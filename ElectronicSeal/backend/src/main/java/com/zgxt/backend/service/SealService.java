package com.zgxt.backend.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zgxt.backend.bean.*;
import com.zgxt.backend.dao.SealMapper;
import com.zgxt.backend.entity.SealEntity;
import com.zgxt.backend.util.HttpUtils;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 签约业务逻辑类
 */
@Service
@Log4j2
public class SealService {
    @Resource
    private SealMapper sealMapper;

    public boolean signature(ContractBean contractBean, String chainAccount) {
        String code = UUID.randomUUID().toString(); //获取签名文档uuid编码
        SealEntity sealEntity = new SealEntity();
        BeanUtils.copyProperties(contractBean, sealEntity);
        String[] str = contractBean.getFilename().split("\\.");
        sealEntity.setFilename(str[0]);
        sealEntity.setType(str[1]);
        sealEntity.setCode(code);

        //签章记录上链
        try {
            //获取印章文档Hash值
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = messageDigest.digest(contractBean.getImgBase64().getBytes("UTF-8"));
            String hash = Hex.toHexString(bytes);
            log.info("hash:" + hash);

            List funcParams = new ArrayList<>();
            funcParams.add(chainAccount);
            funcParams.add(hash);
            funcParams.add(code);
            funcParams.add(String.valueOf(System.currentTimeMillis()));
            JSONObject res = HttpUtils.writeContract("sealSignature", funcParams);
            if (res == null) {
                log.info("印章记录上链失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int result = sealMapper.insert(sealEntity);

        return result > 0 ? true : false;
    }

    public List<RecordBean> getRecord(String username, String chainAccount) {
        List<RecordBean> recordBeans = new ArrayList<>();

        List params = new ArrayList();
        params.add(chainAccount);
        JSONArray allHash = HttpUtils.readContract(chainAccount, "queryAllHash", params);
        if (allHash != null && allHash.size() != 0) {
            List<String> list = JSONArray.parseArray(allHash.getJSONArray(0).toJSONString(), String.class);
            for (int index = 0; index < list.size(); index++) {
                String hash = list.get(index);
                List funcParams = new ArrayList();
                funcParams.add(chainAccount);
                funcParams.add(hash);
                JSONArray signature = HttpUtils.readContract(chainAccount, "querySignature", funcParams);
                if (signature != null && signature.size() != 0) {
                    List<String> infos = JSONObject.parseArray(signature.toJSONString(), String.class);
                    RecordBean record = new RecordBean();
                    record.setCode(infos.get(0));
                    record.setDatetime(DateUtil.date(Long.valueOf(infos.get(1))).toString());

                    QueryWrapper<SealEntity> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("username", username);
                    queryWrapper.eq("code", record.getCode());
                    SealEntity sealEntity = sealMapper.selectOne(queryWrapper);
                    if (sealEntity != null) {
                        record.setId(sealEntity.getId());
                        record.setFilename(sealEntity.getFilename());
                        record.setType(sealEntity.getType());
                        record.setImgBase64(sealEntity.getImgBase64());
                    }

                    recordBeans.add(record);
                }
            }
        }
        return recordBeans;
    }

    public VerifyBean verify(String chainAccount, String imgBase64) {
        VerifyBean verifyBean = new VerifyBean();
        verifyBean.setChainAccount(chainAccount);

        //获取账户信息
        List params = new ArrayList();
        params.add(chainAccount);
        JSONArray accountInfo = HttpUtils.readContract(chainAccount, "getSealAccount", params);
        if (accountInfo == null) {
            log.info("获取区块链账户信息失败");
            return null;
        }

        List<String> list = JSONArray.parseArray(accountInfo.toJSONString(), String.class);
        String name = list.get(0);
        String cardId = list.get(1);
        verifyBean.setName(name);
        verifyBean.setCardId(cardId);

        try {
            //获取印章文档Hash值
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = messageDigest.digest(imgBase64.getBytes("UTF-8"));
            String hash = Hex.toHexString(bytes);
            log.info("hash:"+hash);

            //获取印章信息
            List funcParams = new ArrayList();
            funcParams.add(chainAccount);
            funcParams.add(hash);
            JSONArray sealInfo = HttpUtils.readContract(chainAccount,"querySignature", funcParams);
            if (sealInfo == null) {
                log.info("获取区块链印章信息失败");
                return null;
            }

            List<String> list2 = JSONArray.parseArray(sealInfo.toJSONString(), String.class);
            String code = list2.get(0);
            String datetime = list2.get(1);
            //判断是否为空字符串
            if (code.isEmpty() || datetime.isEmpty()) {
                log.info("hash值不一致，验证签章失败");
                return null;
            }

            verifyBean.setCode(code);
            verifyBean.setDatetime(DateUtil.date(Long.valueOf(datetime)).toString());

            //查询文档filename
            QueryWrapper<SealEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", code);
            SealEntity sealEntity = sealMapper.selectOne(queryWrapper);
            verifyBean.setFilename(sealEntity.getFilename());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return verifyBean;
    }
}
