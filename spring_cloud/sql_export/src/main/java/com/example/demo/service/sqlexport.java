package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.domain.TablesDO;
import com.example.demo.domain.InformationScheduleDO;
import com.example.demo.mapper.InformationScheduleMapper;
import com.example.demo.mapper.TablesMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class sqlexport {
    @Autowired
    private InformationScheduleMapper informationScheduleMapper;

    @Autowired
    private TablesMapper tablesMapper;

    @Value("${sqlexport.databaseName}")
    private String sheduleName;

    @Value("${sqlexport.tableSort}")
    private String tableSortArray;

    @Value("${sqlexport.exportName}")
    private String exportName;

    @Value("${sqlexport.textStartLevel}")
    private String textStartLevel;

    @Value("${sqlexport.textstartIndex}")
    private String textstartIndex;

    @Value("${sqlexport.tableSortDesc}")
    private Boolean tableSortDesc;


    private final String lineSeparator = java.security.AccessController
            .doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

    /**
     * 导出表字段到相应markdown文本.
     *
     * @return
     * @throws IOException
     */
    public Boolean getTableToMarkdown() throws IOException {

        File f = new File(exportName);
        f.delete();
        f.createNewFile();

        try (OutputStream output = new FileOutputStream(f)) {

            List<TablesDO> tablesList = tableInfoS();

            List<String> tableInfo = tablesList.stream().map(TablesDO::getTableName).collect(Collectors.toList());
            Map<String, List<InformationScheduleDO>> info
                    = getTableDetail(tableInfo);

            String table = getTableMarkdown(tablesList,info);
            output.write(table.getBytes());

        } catch (IOException e) {
            log.info("数据导出异常！", e);
            return false;
        }

        return true;
    }


    /**
     * 获取所有表.
     *
     * @return
     */
    private List<TablesDO> tableInfoS() {
        QueryWrapper query = new QueryWrapper();
        query.eq("TABLE_SCHEMA", sheduleName);
        String[] sortArray = tableSortArray.split(",");
        if(tableSortDesc){
            query.orderByDesc(sortArray);
        }else{
            query.orderByAsc(sortArray);
        }
        return tablesMapper.selectList(query);
    }


    /**
     * 获取所有表字段信息。
     *
     * @param tableNameS 表名数组
     * @return
     */
    private Map<String, List<InformationScheduleDO>> getTableDetail(List<String> tableNameS) {

        Map<String, List<InformationScheduleDO>> returnMap = new HashMap<>();

        for (String tableName : tableNameS) {
            QueryWrapper query = new QueryWrapper();
            query.eq("TABLE_NAME", tableName);
            query.eq("TABLE_SCHEMA", sheduleName);
            returnMap.put(tableName, informationScheduleMapper.selectList(query));
        }
        return returnMap;
    }


    /**
     * 获取所有表的完整markdown文本.
     *
     * @param tableInfo 表信息
     * @param tableColums 表字段
     * @return md表文本
     */
    private String getTableMarkdown(List<TablesDO> tableInfo, Map<String, List<InformationScheduleDO>> tableColums) {

        String totalStr = "";
        String indexPrefix = textstartIndex + ".";

        Integer testLevel = (Integer.parseInt(textStartLevel) > 1) ? Integer.parseInt(textStartLevel) - 1 : 0;
        while(testLevel > 0){
            totalStr += "#";
            testLevel --;
        }
        totalStr += " " + indexPrefix + " 数据库表设计" + lineSeparator;

        Integer indexId = 1;

        for (TablesDO tablesDO : tableInfo) {
            tablesDO.setTableType(tablesDO.getTableType().equals("BASE TABLE")? "数据库表" : "视图");
            String title = getTableTitle(tablesDO.getTableName(), tablesDO.getTableComment(),tablesDO.getTableType(),
                    indexPrefix + indexId.toString());
            Integer columnIndex = 1;
            for (InformationScheduleDO informationScheduleDO : tableColums.get(tablesDO.getTableName())) {
                String str =
                        "|" + columnIndex.toString() +
                        "|" + informationScheduleDO.getColumnName() + "|"
                        + informationScheduleDO.getColumnType() + "|"
                        + informationScheduleDO.getColumnComment() + "|"
                        + informationScheduleDO.getIsNullable() + "|";

                str = str.replaceAll("\r|\n", "");
                title += (str + lineSeparator);
                columnIndex ++;
            }
            totalStr += (title + lineSeparator);
            indexId ++;
        }

        return totalStr;
    }


    /**
     * 创建markdown形式的表头.
     *
     * @param name 表名
     * @param nickName 昵称
     * @param tableType 表类型
     * @param indexId 表前缀id
     * @return md表头
     */
    private String getTableTitle(String name, String nickName,String tableType,String indexId) {

        String titleName = (StringUtils.isEmpty(nickName) ? name : nickName);
        titleName =(titleName.equals("VIEW") ? name : titleName);

        Integer tableValue = Integer.parseInt(textStartLevel);
        String tableLevelStr = "";
        while(tableValue > 0){
            tableLevelStr += "#";
            tableValue --;
        }

        String tableTitle = lineSeparator + tableLevelStr + " " + indexId + " " + titleName + lineSeparator;
        tableTitle = tableTitle + "    类型：" + tableType + "    名称：" + name + lineSeparator;
        String title = tableTitle + lineSeparator + "| 序号 | 字段名 | 字段类型 | 注释 | 是否可为空 |"
                + lineSeparator + "| :---: |  :----:  | :----: | :----: | :----: |" + lineSeparator;
        return title;
    }


}
