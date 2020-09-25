package com.example.demo.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("TABLES")
public class TablesDO {

    private String tableSchema;

    private String tableName;

    private String tableComment;

    private String tableType;

}
