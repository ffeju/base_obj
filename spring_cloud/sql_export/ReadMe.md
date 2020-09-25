# 1. 概述
MySQL数据库结构导出成markdown文档形式

程序直接生成markdown文档，之后需要使用pandoc将md文件转换成docx文件。当生成docx文件后需要使用word宏功能进行编辑.

环境与组件：  
>1.Java 1.8  
>2.Windows 10  
>3.pandoc.exe 2.10.1  
>4.Word 2016

# 2. 使用指引

## 2.1 程序导出markdown文档，可在application.yaml中修改字段指定对应配置  

执行代码中的src.test.com.example.demo.service.sqlexportTest#testsql方法生成markdonw文档

字段对应配置

>1.数据库连接   

    spring:
      datasource:
        url: jdbc:mysql://[ip]:[port]/information_schema?useSSL=false&useUnicode=true&characterEncoding=utf8&useOldAliasMetadataBehavior=true&useTimezone=true&serverTimezone=GMT%2B8&useLegacyDatetimeCode=false&allowMultiQueries=true
        username: [username]
        password: [password]

>2.指定表排序字段

    #[sort column]可选字段：TABLE_NAME（表名）、TABLE_TYPE（表类型）、TABLE_COMMENT（表注释）
    sqlexport:
      tableSort: ['sort column','sort column']

>3.排序方向

    sqlexport:
      tableSortDesc: [sort way]

>4.导出文件名

    sqlexport:
      exportName: [export name]

>5.指定导出数据库

    sqlexport:
      databaseName: [database name]

>6.表格级别

    sqlexport:
          textStartLevel: [level]

>7.起始编号

    sqlexport:
              textstartIndex: [index]

## 2.2 markdown文件与word文档转换

控制台中使用pandoc进行转换，如下是示例命令

>pandoc D://test.md -o D://test1.docx

正常执行则控制台没有提示直接进入等待下一次输入的状态

## 2.3 word文档调整

打开对应的word文档在选项中添加开发人员选项，可以在顶部菜单栏中看到开发人员工具栏.

开发人员工具栏找到visual base粘贴以下脚本可以达到辅助效果

1.选择所有表格

    Sub 批量修改表格()
    Dim tempTable As Table
    Application.ScreenUpdating = False
    If ActiveDocument.ProtectionType = wdAllowOnlyFormFields Then
    MsgBox "文档已保护，此时不能选中多个表格！"
    Exit Sub
    End If
    ActiveDocument.DeleteAllEditableRanges wdEditorEveryone
    For Each tempTable In ActiveDocument.Tables
    tempTable.Range.Editors.Add wdEditorEveryone
    Next
    ActiveDocument.SelectAllEditableRanges wdEditorEveryone
    ActiveDocument.DeleteAllEditableRanges wdEditorEveryone
    Application.ScreenUpdating = True
    End Sub

2.所有表格根据窗口对齐

    Sub www()
    Dim oDoc As Document
    Dim oTable As Table
    Set oDoc = Documents.Open("路径\文件名.doc")
    For Each oTable In oDoc.Tables
    oTable.AutoFitBehavior (wdAutoFitWindow)
    Next
    MsgBox "自动调整表格宽度完成！"
    End Sub