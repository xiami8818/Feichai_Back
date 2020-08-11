package com.feichai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication 标注一个主程序
@SpringBootApplication
public class Start {
    public static void main(String[] args) {
        //启动Spring应用
        SpringApplication.run(Start.class,args);
    }
}
