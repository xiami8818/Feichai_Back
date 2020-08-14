package com.feichai.controller;

import org.springframework.web.bind.annotation.*;
import java.sql.*;

@RestController
public class Interface {
    @PostMapping("/regist")
    public String index(String name,String password,String phone){
        String sql = "select * from `users` where phone='"+phone+"'";
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/feichai","xiami","19991026");
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);

            if(result.next()){
                return "existed";
            }else{
                sql = "insert into `users` (`name`, `password`, `phone`) values ('"+name+"', '"+password+"', '"+phone+"')";
                statement.execute(sql);
                return "succeed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return "failed";
        }
    }

    @GetMapping("/login")
    public String index(String phone,String password){
        String sql = "select * from users where name='"+phone+"' and password='"+password+"'";
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/feichai?autoReconnect=true&useSSL=false","xiami","19991026");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                return "success";
            }
            return "unexsited";
        }
        catch (SQLException e){
            e.printStackTrace();
            return "failed";
        }
    }
}
