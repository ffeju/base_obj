package com.example.demo.service;

import com.example.demo.JavaApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;


@SpringBootTest(classes = JavaApplication.class)
class sqlexportTest {

    @Autowired
    sqlexport sqlexport;

    @Test
    public void testsql() {
        try {
            sqlexport.getTableToMarkdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}