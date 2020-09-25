package com.example.demo.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("COLUMNS")
public class InformationScheduleDO {

    private String tableName;

    private String columnName;

    private String isNullable;

    private String columnType;

    private String columnComment;

}
