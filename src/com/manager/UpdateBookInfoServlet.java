package com.servlet;

import com.LibraryDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;

@WebServlet("/UpdateBookInfoServlet")
public class UpdateBookInfoServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String bookId = request.getParameter("book_id");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String isbn = request.getParameter("isbn");
        String publisher = request.getParameter("publisher");
        String year = request.getParameter("year");
        String categoryId = request.getParameter("category_id");
        String location = request.getParameter("location");
        String totalCopies = request.getParameter("total_copies");
        String availableCopies = request.getParameter("available_copies");
        String status = request.getParameter("status");

        // 校验状态
        if (status == null || !(status.equals("正常") || status.equals("维修") || status.equals("丢失"))) {
            response.getWriter().println("非法的状态值！");
            return;
        }

        // 更新图书信息的 SQL 语句
        String sql = "UPDATE books SET title=?, author=?, isbn=?, publisher=?, year=?, category_id=?, location=?, total_copies=?, available_copies=?, status=? WHERE book_id=?";
        try (Connection conn = LibraryDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, isbn);
            stmt.setString(4, publisher);
            stmt.setInt(5, Integer.parseInt(year));  // 转换为整数
            stmt.setInt(6, Integer.parseInt(categoryId));  // 转换为整数
            stmt.setString(7, location);
            stmt.setInt(8, Integer.parseInt(totalCopies));  // 转换为整数
            stmt.setInt(9, Integer.parseInt(availableCopies));  // 转换为整数
            stmt.setString(10, status);
            stmt.setInt(11, Integer.parseInt(bookId));  // 转换为整数

            // 执行更新
            int result = stmt.executeUpdate();
            if (result > 0) {
                // 获取 session 中的 username
                HttpSession session = request.getSession(false);
                String username = (session != null) ? (String) session.getAttribute("username") : null;

                // 如果 username 非空，进行编码并重定向，否则不传递 username
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

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            response.getWriter().println("数据库错误或输入格式错误！");
        }
    }
}
