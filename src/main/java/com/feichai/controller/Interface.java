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
            return "$illegal";
        }
        String sql = "select * from `users` where phone='"+phone+"'";
        try {
            connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql);
            if(result.next()){
                statement.close();
                return "$existed";
            }else{
                sql = "insert into `users` (`name`, `password`, `phone`, `img`, `saved`, `algorithm`, `robot`, `safe`) values ('"+name+"', '"+password+"', '"+phone+"', 'http://47.100.137.63:8080/cat.jpg', 'no', 'no', 'no', 'no')";
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
    public String login(String phone,String password,HttpSession session,HttpServletResponse response) throws SQLException {
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
    public String getInfo(HttpServletRequest request) throws SQLException {
        Cookie[] cookies = request.getCookies();
        String phone = null;
        String sessionId = null;
        boolean sign = true;
        if(cookies==null){
            return "$false";
        }
        for(Cookie cookie:cookies){
            if(cookie.getName().equals("sessionId")){
                sessionId = cookie.getValue();
                sign = false;
                break;
            }
        }
        if(sign){
            return "$false";
        }
        HttpSession session = map.get(sessionId);
        if(session==null){
            return "$false";
        }
        phone = session.getAttribute("user").toString();
        String sql = "select img,name from users where phone='"+phone+"'";
        connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        String message = (String)resultSet.getString("name")+"&";
        message+=resultSet.getString("img");
        connection.close();
        return message;
    }

    @PostMapping("/logout")
    @CrossOrigin
    public String logout(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        if(cookies == null){
            return "$ok";
        }
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
        if(session == null){
            return "$ok";
        }
        session.setAttribute("login","false");
        return "$ok";
    }
    @GetMapping("/account")
    @CrossOrigin
    public String account(HttpServletRequest request) throws SQLException {
        connection = dataSource.getConnection();
        String sql = "select money from money";
        String result = null;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        result = resultSet.getString("money")+"$";
        sql = "select detail,date from account";
        resultSet = statement.executeQuery(sql);
        while(resultSet.next()){
            result+= resultSet.getString("date")+" : "+resultSet.getString("detail")+"#";
        }
        connection.close();
        return result;
    }

    @GetMapping("/getUser")
    @CrossOrigin
    public String getUser(HttpServletRequest request) throws SQLException {
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        String phone = null;
        boolean sign =true;
        if(cookies == null){
            return "$false";
        }
        for(Cookie cookie: cookies){
            if(cookie.getName().equals("sessionId")){
                sessionId = cookie.getValue();
                sign = false;
                break;
            }
        }
        if(sign){
            return "$false";
        }
        HttpSession session = map.get(sessionId);
        phone = session.getAttribute("user").toString();
        String sql = "select * from users where phone='"+phone+"'";
        connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.next();
        String result = resultSet.getString("trueName")+"$";
        result += resultSet.getString("qq")+"$";
        result += resultSet.getString("num")+"$";
        result += resultSet.getString("sex")+"$";
        result += resultSet.getString("school")+"$";
        result += resultSet.getString("img")+"$";
        result += resultSet.getString("name")+"$";
        connection.close();
        return result;
    }

    @PostMapping("/setUser")
    @CrossOrigin
    public String setUser(HttpServletRequest request,String trueName,String qq,String num,String sex,String school) throws SQLException {
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        String phone = null;
        boolean sign =true;
        System.out.println("true");
        if(cookies == null){
            return "$false";
        }
        for(Cookie cookie: cookies){
            if(cookie.getName().equals("sessionId")){
                sessionId = cookie.getValue();
                sign = false;
                break;
            }
        }
        if(sign){
            return "$false";
        }
        HttpSession session = map.get(sessionId);
        if(session == null){
            return "$false";
        }
        phone = session.getAttribute("user").toString();
        String sql = "update `users` set trueName='"+trueName+"', qq='"+qq+"', num='"+num+"', sex='"+sex+"', school='"+school+"', saved='yes' where phone='"+phone+"'";
        connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        connection.close();
        return "$success";
    }

    @PostMapping("/setDepartment")
    @CrossOrigin
    public String algorithm(HttpServletRequest request, String department) throws SQLException {
        Cookie[] cookies = request.getCookies();
        String sessionId = null;
        String phone = null;
        if(cookies == null){
            return "$false";
        }
        boolean sign = true;
        for(Cookie cookie: cookies){
            if(cookie.getName().equals("sessionId")){
                sessionId = cookie.getValue();
                sign = false;
                break;
            }
        }
        if(sign){
            return "$false";
        }
        HttpSession session = map.get(sessionId);
        phone = session.getAttribute("phone").toString();
        connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = "update `users` set `"+department+"`='yes' where `phone`='"+phone+"'";
        statement.executeUpdate(sql);
        return "$succeed";
    }
    @GetMapping("/getDepartment")
    @CrossOrigin
    public String getDepartment(HttpServletRequest request, String department) throws SQLException {
        Cookie[] cookies = request.getCookies();
        String sessionId = "";
        String phone;
        if(cookies == null){
            return "$false";
        }
        boolean sign = true;
        for(Cookie cookie: cookies){
            if(cookie.getName().equals("sessionId")){
                sessionId = cookie.getValue();
                sign = false;
                break;
            }
        }
        if(sign){
            return "$false";
        }
        HttpSession session = map.get(sessionId);
        phone = session.getAttribute("phone").toString();
        connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql = "select "+department+" where `phone`='"+phone+"'";
        ResultSet resultSet = statement.executeQuery(sql);
        if(resultSet.next()){
            if(resultSet.getString(department).equals("yes")){
                return "$yes";
            }else {
                return "$no";
            }
        }
        return "$false";
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
