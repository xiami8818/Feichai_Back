package com.feichai.controller;

import org.springframework.web.bind.annotation.*;
import javax.servlet.http.*;
import java.sql.*;

@RestController
@RequestMapping("/user")
public class Interface {
    @PostMapping("/regist")
    @CrossOrigin
    public String index(String name,String password,String phone,String num,String qq,HttpSession session){
        String sql = "select * from `users` where phone='"+phone+"'";
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/feichai","xiami","19991026");
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if(result.next()){
                return "existed";
            }else{
                sql = "insert into `users` (`name`, `password`, `phone`,`num`,`qq`) values ('"+name+"', '"+password+"', '"+phone+"','"+num+"','"+qq+"')";
                statement.execute(sql);
                session.setAttribute("login","true");
                session.setMaxInactiveInterval(10800);
                return "succeed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return "failed";
        }
    }

    @PostMapping("/login")
    @CrossOrigin
    public String index(String phone,String password,HttpSession session){
        String sql = "select * from users where phone='"+phone+"' and password='"+password+"'";
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/feichai?autoReconnect=true&useSSL=false","xiami","19991026");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                session.setAttribute("login","true");
                session.setMaxInactiveInterval(10800);
                return "succeed";
            }
            return "unexsited";
        }
        catch (SQLException e){
            e.printStackTrace();
            return "failed";
        }
    }
    @GetMapping("/check")
    @CrossOrigin
    public String check(HttpSession session){
        if((String)session.getAttribute("login")=="true"){
            return "true";
        }else{
            return "false";
        }
    }
    @GetMapping("/getInfo")
    @CrossOrigin
    public String getInfo(String phone) throws SQLException {
        String sql = "select img,name from users where phone='"+phone+"'";
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/feichai?autoReconnect=true&useSSL=false","xiami","19991026");
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        String message = (String)resultSet.getString("name")+"&";
        message+=resultSet.getString("img");
        return message;
    }
}
