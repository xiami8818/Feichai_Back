package com.feichai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.*;
import javax.sql.DataSource;
import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.net.HttpCookie;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/user")
public class Interface implements HandlerInterceptor {
    private String checkSQL = ".*[&$# '].*";
    private Connection connection;
    private Map<String, HttpSession> map = new HashMap<String, HttpSession>();
    @Autowired
    private DataSource dataSource;

    @PostMapping("/regist")
    @CrossOrigin
    public String regist(String name,String password,String phone,HttpSession session,HttpServletResponse response) throws SQLException {
        if(Pattern.matches(checkSQL,name)||Pattern.matches(checkSQL,password)||Pattern.matches(checkSQL,phone)){
            return "illegal";
        }
        String sql = "select * from `users` where phone='"+phone+"'";
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if(result.next()){
                statement.close();
                return "existed";
            }else{
                sql = "insert into `users` (`name`, `password`, `phone`) values ('"+name+"', '"+password+"', '"+phone+"')";
                statement.execute(sql);
                Cookie cookie = new Cookie("sessionId",session.getId());
                response.addCookie(cookie);
                map.put(session.getId(),session);
                session.setAttribute("login","true");
                session.setAttribute("user",phone);
                session.setMaxInactiveInterval(10800);
                statement.close();
                return "$succeed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return "$failed";
        }finally {
            connection.close();
        }
    }


    @PostMapping("/login")
    @CrossOrigin(origins = "*")
    public String login(String phone,String password,HttpSession session,HttpServletResponse response,HttpServletRequest request) throws SQLException {
        if(Pattern.matches(checkSQL,phone)||Pattern.matches(checkSQL,password)){
            return "illegal";
        }
        String sql = "select * from users where phone='"+phone+"' and password='"+password+"'";
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()){
                session.setAttribute("login","true");
                session.setAttribute("user",phone);
                session.setMaxInactiveInterval(10800);
                Cookie cookie = new Cookie("sessionId", session.getId());
                response.addCookie(cookie);
                map.put(session.getId(),session);
                return "$succeed";
            }
            return "$unexsited";
        }
        catch (SQLException e){
            e.printStackTrace();
            return "$failed";
        }finally {
            connection.close();
        }
    }
    @GetMapping("/check")
    @CrossOrigin(origins = "*")
    public String check(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        boolean sign = false;
        String sessionId=null;
        if(cookies!=null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals("sessionId")){
                    sessionId = cookie.getValue();
                    sign =true;
                    break;
                }
            }
            if(!sign){
                return "$false";
            }

        }
        HttpSession session = map.get(sessionId);
        if(session == null){
            return "$false";
        }
        if((String)session.getAttribute("login")=="true"){
            return session.getAttribute("user").toString();
        }else{
            return "$false";
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
        statement.close();
        return message;
    }

    @PostMapping("/logout")
    @CrossOrigin
    public String getInfo(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        boolean sign = false;
        if(cookies!=null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("sessionId")){
                    sessionId = cookie.getValue();
                    sign = true;
                    break;
                }
            }
            if(!sign){
                return "$ok";
            }
        }
        HttpSession session = map.get(sessionId);
        session.setAttribute("login","false");
        return "$ok";
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
