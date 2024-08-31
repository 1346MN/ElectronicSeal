package com.zgxt.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_seal")
public class SealEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private String filename;
    private String type;
    private String username;
    private String imgBase64;
}
