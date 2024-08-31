package com.zgxt.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户实体类
 */
@Data
@TableName("tb_user")
public class UserEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private String chainAccount;
    private String name;
    private String cardId;
    private String phone;
    private String email;
    private String sealImg;
}
