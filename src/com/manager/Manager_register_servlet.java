package com.manager;

import com.LibraryDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/Manager_register")
public class Manager_register_servlet extends HttpServlet {

    private Connection connection;

    public Manager_register_servlet() {
        try {
            this.connection = LibraryDB.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 获取表单数据
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // 简单校验
        if (name == null || name.isEmpty() ||
                email == null || email.isEmpty() ||
                password == null || password.isEmpty()) {
            alert(response, "所有字段都必须填写！", "customer_register.html");
            return;
        }

        try {
            // 查询邮箱是否已注册
            String checkSql = "SELECT * FROM Manager WHERE manager_user = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                alert(response, "此邮箱已被注册", "login.html");
            } else {
                // 插入新用户
                String insertSql = "INSERT INTO Manager (manager_name, manager_user, manager_password) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                insertStmt.setString(1, name);
                insertStmt.setString(2, email);
                insertStmt.setString(3, password);
                int rowsInserted = insertStmt.executeUpdate();

                if (rowsInserted > 0) {
                    alert(response, "注册成功！请登录。", "login.html");
                } else {
                    alert(response, "注册失败，请重试。", "login.html");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            alert(response, "系统错误，请稍后再试。", "manager_register.html");
        }
    }

    // 封装 alert 弹窗方法
    private void alert(HttpServletResponse response, String msg, String redirectURL) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script type='text/javascript'>");
        out.println("alert('" + msg + "');");
        out.println("window.location.href='" + redirectURL + "';");
        out.println("</script>");
    }
}
