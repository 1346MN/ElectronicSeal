package com.zgxt.backend.bean;

import lombok.Data;

/**
 * 注册 Bean
 */
@Data
public class RegisterBean {
    private String username;
    private String password;
    private String chainAccount;
    private String name;
    private String cardId;
    private String phone;
    private String email;
}
