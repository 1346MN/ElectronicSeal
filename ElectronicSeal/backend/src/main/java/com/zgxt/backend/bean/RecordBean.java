package com.zgxt.backend.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordBean {
    private Integer id;
    private String filename;
    private String type;
    private String code;
    private String datetime;
    private String imgBase64;
}
