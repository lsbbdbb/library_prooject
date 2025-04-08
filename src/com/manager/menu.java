//package com.manager;
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
//@WebServlet("/manager_menu")
//public class menu extends HttpServlet {
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        // 获取操作类型参数
//        String action = request.getParameter("action");
//        String manager_name = request.getParameter("username");
//        if (action == null) {
//            action = "";
//        }
//
//        // URL 编码用户名
//        String encodedCustomerName = "";
//        try {
//            encodedCustomerName = URLEncoder.encode(manager_name, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();  // 如果编码失败，打印异常信息
//        }
//
//        // 根据操作类型调用相应的方法
//        switch (action) {
//            case "addBook":
//                showAddBookPage(response, manager_name);
//                break;
//            case "manageBorrow":
//                manageBorrow(request, response, manager_name);
//                break;
//            case "manageLocation":
//                manageLocation(request, response, manager_name);
//                break;
//            case "updateInfo":
//                updateInfo(request, response, manager_name);
//                break;
//            default:
//                showMenu(request, response, manager_name);
//                break;
//        }
//    }
//
//    // 显示主菜单
//    private void showMenu(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {
//        response.setContentType("text/html;charset=UTF-8");
//        response.getWriter().write(String.format("""
//            <!DOCTYPE html>
//            <html lang="zh">
//            <head>
//                <meta charset="UTF-8">
//                <meta name="viewport" content="width=device-width, initial-scale=1">
//                <title>管理员菜单</title>
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
//                    <h2>欢迎 %s 使用图书馆管理系统</h2>
//                    <ul class="list-group">
//                        <li class="list-group-item"><a href="?action=addBook&username=%s" class="btn btn-primary w-100">录入新书</a></li>
//                        <li class="list-group-item"><a href="?action=manageBorrow&username=%s" class="btn btn-success w-100">借书管理</a></li>
//                        <li class="list-group-item"><a href="?action=manageLocation&username=%s" class="btn btn-warning w-100">位置管理</a></li>
//                        <li class="list-group-item"><a href="?action=updateInfo&username=%s" class="btn btn-info w-100">更新信息</a></li>
//                    </ul>
//                </div>
//            </body>
//            </html>
//        """, username, username, username, username, username));
//    }
//
//    // 录入新书功能
//    private void showAddBookPage(HttpServletResponse response, String username) throws IOException {
//        showFeaturePage(response, "录入新书", "此功能正在开发中...", username);
//    }
//
//    private void showFeaturePage(HttpServletResponse response, String title, String message, String username) throws IOException {
//        response.setContentType("text/html;charset=UTF-8");
//        response.getWriter().write(String.format("""
//        <!DOCTYPE html>
//        <html lang="zh">
//        <head>
//            <meta charset="UTF-8">
//            <meta name="viewport" content="width=device-width, initial-scale=1">
//            <title>%s</title>
//            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
//        </head>
//        <body class="d-flex justify-content-center align-items-center vh-100" style="background-color: #f8f9fa;">
//            <div class="menu-container bg-white p-4 rounded shadow text-center">
//                <h2>%s</h2>
//                <p>%s</p>
//                <a href="?username=%s" class="btn btn-secondary">返回菜单</a>
//            </div>
//        </body>
//        </html>
//    """, title, title, message, username));
//    }
//
//
//
//    // 借书管理功能
//    private void manageBorrow(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {
//        // 实现借书管理的功能
//        response.getWriter().write("<h3>借书管理功能正在开发中...</h3>");
//    }
//
//    // 位置管理功能
//    private void manageLocation(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {
//        // 实现位置管理功能
//        response.getWriter().write("<h3>位置管理功能正在开发中...</h3>");
//    }
//
//    // 更新信息功能
//    private void updateInfo(HttpServletRequest request, HttpServletResponse response, String username) throws IOException {
//        // 实现更新信息功能
//        response.getWriter().write("<h3>更新信息功能正在开发中...</h3>");
//    }
//}
package com.manager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/manager_menu")
public class menu extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String manager_name = request.getParameter("username");
        if (manager_name == null) {
            manager_name = "管理员";
        }

        showMenu(response, manager_name);
    }

    private void showMenu(HttpServletResponse response, String username) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(String.format("""
        <!DOCTYPE html>
        <html lang="zh">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1">
            <title>管理员菜单</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        </head>
        <body class="d-flex justify-content-center align-items-center vh-100" style="background-color: #f8f9fa;">
            <div class="menu-container bg-white p-4 rounded shadow text-center">
                <h2>欢迎 %s 使用图书馆管理系统</h2>
                <ul class="list-group">
                    <li class="list-group-item"><a href="add_book?username=%s" class="btn btn-primary w-100">录入新书</a></li>
                    <li class="list-group-item"><a href="check_book?username=%s" class="btn btn-primary w-100">图书管理</a></li>
                    <li class="list-group-item"><a href="seat_manager?username=%s" class="btn btn-primary w-100">位置管理</a></li>
                    <li class="list-group-item"><a href="DatabaseInfoServlet?username=%s" class="btn btn-primary w-100">更新信息</a></li>
                    <li class="list-group-item"><a href="logout" class="btn btn-danger w-100">退出登录</a></li>
                </ul>
            </div>
        </body>
        </html>
    """, username, username, username, username, username));
    }

}
