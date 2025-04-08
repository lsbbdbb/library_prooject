package com.manager;

import com.LibraryDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/UpdateManagerInfoServlet")
public class UpdateManagerInfoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 获取表单提交的数据
        String managerUser = request.getParameter("manager_user");
        String managerName = request.getParameter("manager_name");
        String managerPassword = request.getParameter("manager_password");

        // 更新 Manager 表的 SQL 语句
        String sql = "UPDATE Manager SET manager_name=?, manager_password=? WHERE manager_user=?";
        try (Connection conn = LibraryDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 设置 SQL 语句中的参数
            stmt.setString(1, managerName);
            stmt.setString(2, managerPassword);
            stmt.setString(3, managerUser);

            // 执行更新操作
            int result = stmt.executeUpdate();
            if (result > 0) {
                // 获取 session 中的 username
                String username = (String) request.getSession().getAttribute("username");

                // 如果 username 存在，进行 URL 编码，避免路径问题
                if (username != null && !username.isEmpty()) {
                    // 判断是否已经编码，避免重复编码
                    String encodedUsername = username;
                    if (!username.contains("%")) {  // 如果字符串没有百分号，表示还没有编码
                        encodedUsername = URLEncoder.encode(username, "UTF-8");
                    }

                    // 重定向到数据库信息展示页面，并传递编码后的用户名
                    response.sendRedirect("DatabaseInfoServlet?username=" + encodedUsername);
                } else {
                    // 如果没有用户名，跳转到数据库信息展示页面
                    response.sendRedirect("DatabaseInfoServlet");
                }
            } else {
                response.getWriter().println("修改失败！");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("数据库错误！");
        }
    }
}
