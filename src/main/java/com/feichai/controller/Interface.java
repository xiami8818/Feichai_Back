package com.feichai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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
}
