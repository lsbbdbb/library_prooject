package com.servlet;

import com.LibraryDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;

@WebServlet("/UpdateCustomerInfoServlet")
public class UpdateCustomerInfoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 获取提交的表单数据
        String customerUser = request.getParameter("customer_user");
        String customerName = request.getParameter("customer_name");
        String customerPassword = request.getParameter("customer_password");

        // SQL 更新语句
        String sql = "UPDATE Customer SET customer_name=?, customer_password=? WHERE customer_user=?";

        try (Connection conn = LibraryDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerName);
            stmt.setString(2, customerPassword);
            stmt.setString(3, customerUser);

            // 执行更新操作
            int result = stmt.executeUpdate();
            if (result > 0) {
                // 获取 session 中的用户名
                HttpSession session = request.getSession(false);
                String username = (session != null) ? (String) session.getAttribute("username") : null;

                if (username != null) {
                    // 判断是否已编码，避免重复编码
                    String encodedUsername = username;
                    if (!username.contains("%")) {  // 如果字符串没有百分号，表示还没有编码
                        encodedUsername = URLEncoder.encode(username, "UTF-8");
                    }

                    // 重定向到数据展示页面，并传递编码后的用户名
                    response.sendRedirect("DatabaseInfoServlet?username=" + encodedUsername);
                } else {
                    // 如果没有用户名，跳转到数据库信息展示页面
                    response.sendRedirect("DatabaseInfoServlet");
                }

            } else {
                // 如果更新失败，显示失败信息
                response.getWriter().println("修改失败！");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("数据库错误！");
        }
    }
}
