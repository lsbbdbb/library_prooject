//package com.customer;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//
//@WebServlet("/customer_menu")
//public class menu extends HttpServlet {
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // 获取操作类型参数
////        String action = request.getParameter("action");
////        String customer_name = request.getParameter("username");
////        if (action == null) {
////            action = "";
////        }
//        // 获取操作类型参数
//        String action = request.getParameter("action");
//        String customer_name = request.getParameter("username");
//        if (action == null) {
//            action = "";
//        }
//
//        // URL 编码用户名
//        String encodedCustomerName = "";
//        try {
//            encodedCustomerName = URLEncoder.encode(customer_name, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();  // 如果编码失败，打印异常信息
//        }
//        // 根据操作类型调用相应的方法
//        switch (action) {
//            case "borrow":
//                borrowBook(request, response);
//                break;
//            case "return":
//                returnBook(request, response);
//                break;
//            case "renew":
//                renewBook(request, response);
//                break;
//            case "reserve":
//                reserveSeat(request, response);
//                break;
//            default:
//                showMenu(request, response, customer_name);
//                break;
//        }
//    }
//
//    // 显示主菜单
//    private void showMenu(HttpServletRequest request, HttpServletResponse response, String customer_name) throws IOException {
//        response.setContentType("text/html;charset=UTF-8");
//        response.getWriter().write(String.format("""
//            <!DOCTYPE html>
//            <html lang="zh">
//            <head>
//                <meta charset="UTF-8">
//                <meta name="viewport" content="width=device-width, initial-scale=1">
//                <title>图书馆服务</title>
//                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
//                <style>
//                    body {
//                        background-color: #f8f9fa;
//                        display: flex;
//                        justify-content: center;
//                        align-items: center;
//                        height: 100vh;
//                    }
//                    .menu-container {
//                        background: white;
//                        padding: 20px;
//                        border-radius: 10px;
//                        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
//                        text-align: center;
//                    }
//                    .menu-container h2 {
//                        margin-bottom: 20px;
//                    }
//                </style>
//            </head>
//            <body>
//                <div class="menu-container">
//                    <h2>欢迎 %s 使用图书馆服务</h2>
//                    <ul class="list-group">
//                        <li class="list-group-item"><a href="?action=borrow&username=%s" class="btn btn-primary w-100">借书</a></li>
//                        <li class="list-group-item"><a href="?action=return&username=%s" class="btn btn-success w-100">还书</a></li>
//                        <li class="list-group-item"><a href="?action=renew&username=%s" class="btn btn-warning w-100">续借</a></li>
//                        <li class="list-group-item"><a href="?action=reserve&username=%s" class="btn btn-info w-100">预约座位</a></li>
//                    </ul>
//                </div>
//            </body>
//            </html>
//        """, customer_name, customer_name, customer_name, customer_name, customer_name));
//    }
//
//    // 借书操作
//    private void borrowBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.getWriter().write("<h3>借书功能正在开发中...</h3>");
//    }
//
//    // 还书操作
//    private void returnBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.getWriter().write("<h3>还书功能正在开发中...</h3>");
//    }
//
//    // 续借操作
//    private void renewBook(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.getWriter().write("<h3>续借功能正在开发中...</h3>");
//    }
//
//    // 预约座位操作
//    private void reserveSeat(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.getWriter().write("<h3>预约座位功能正在开发中...</h3>");
//    }
//}

package com.customer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/customer_menu")
public class menu extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String customer_name = request.getParameter("username");
        if (customer_name == null) {
            customer_name = "同学";
        }

        showMenu(response, customer_name);
    }

    private void showMenu(HttpServletResponse response, String username) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(String.format("""
            <!DOCTYPE html>
            <html lang="zh">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>学生菜单</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            </head>
            <body class="d-flex justify-content-center align-items-center vh-100" style="background-color: #f8f9fa;">
                <div class="menu-container bg-white p-4 rounded shadow text-center">
                    <h2>欢迎 %s 使用图书馆管理系统</h2>
                    <ul class="list-group">
                        <li class="list-group-item"><a href="borrow_book?username=%s" class="btn btn-primary w-100">借阅图书</a></li>
                        <li class="list-group-item"><a href="return_book?username=%s" class="btn btn-primary w-100">归还图书</a></li>
                        <li class="list-group-item"><a href="renew_book?username=%s" class="btn btn-primary w-100">续借延期</a></li>
                        <li class="list-group-item"><a href="seat_reservation?username=%s" class="btn btn-info w-100">位置预约</a></li>
                        <li class="list-group-item"><a href="logout" class="btn btn-danger w-100">退出登录</a></li>
                
                    </ul>
                </div>
            </body>
            </html>
        """, username, username,username,username,username));
    }
}
