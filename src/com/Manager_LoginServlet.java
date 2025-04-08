package com.servlet;

import com.LibraryDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/Manager_LoginServlet")
public class Manager_LoginServlet extends HttpServlet {
    String manager_name = null;
    String user_name = null;
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("emails");
        String password = request.getParameter("password");
        System.out.println(username);
        System.out.println(password);

        if (validateUser(username, password)) {
            // URL 编码 customer_name
            String encodedCustomerName = URLEncoder.encode(manager_name, "UTF-8");
            // 重定向到用户菜单，传递编码后的用户名
            response.sendRedirect("manager_menu?username=" + encodedCustomerName);
        } else {
            // 用户名或密码无效，返回错误信息
            request.setAttribute("errorMessage", "Invalid username or password");
            request.getRequestDispatcher("login.html").forward(request, response);
        }
    }

    private boolean validateUser(String username, String password) {
        String sql = "SELECT * FROM Manager WHERE manager_user = ? AND manager_password = ?";  // 使用条件查询来优化
        try (Connection conn = LibraryDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // 如果用户名和密码匹配，获取 manager_name（假设是第三列）
                    manager_name = rs.getString(3);  // 获取第三列的值，假设第三列是 manager_name
                    return true;  // 用户验证成功
                }
            }

        } catch (Exception e) {
            e.printStackTrace();  // 处理异常
        }
        return false;  // 用户未找到或凭据错误
    }
}
