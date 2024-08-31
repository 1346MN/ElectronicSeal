package com.zgxt.backend.bean;

import lombok.Data;

@Data
public class VerifyBean {
    private String name;
    private String cardId;
    private String chainAccount;
    private String filename;
    private String code;
    private String datetime;
}
