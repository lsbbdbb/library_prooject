package com.manager;

import com.LibraryDB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet("/add_book")
public class add_book extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        showAddBookPage(response, request.getParameter("username"));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // 处理中文乱码

        // 获取表单数据
        String username = request.getParameter("username"); // 获取用户名
        String bookTitle = request.getParameter("bookTitle");
        String author = request.getParameter("author");
        String isbn = request.getParameter("isbn");
        String publisher = request.getParameter("publisher");
        String yearStr = request.getParameter("year");
        String categoryIdStr = request.getParameter("categoryId");
        String location = request.getParameter("location");
        String totalCopiesStr = request.getParameter("totalCopies");

        // 转换数值类型字段
        int year = (yearStr != null && !yearStr.isEmpty()) ? Integer.parseInt(yearStr) : 0;
        int categoryId = (categoryIdStr != null && !categoryIdStr.isEmpty()) ? Integer.parseInt(categoryIdStr) : 0;
        int totalCopies = (totalCopiesStr != null && !totalCopiesStr.isEmpty()) ? Integer.parseInt(totalCopiesStr) : 1;

        // 数据库插入
        try (Connection conn = LibraryDB.getConnection()) {  // 使用 DatabaseConnection 获取连接
            // 先检查 ISBN 是否已存在，避免重复插入
            String checkSql = "SELECT COUNT(*) FROM books WHERE isbn = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, isbn);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    showMessagePage(response, "错误", "ISBN 已存在，不能重复添加！", username);
                    return;
                }
            }

            // 插入书籍信息
            String sql = "INSERT INTO books (title, author, isbn, publisher, year, category_id, location, total_copies, available_copies) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bookTitle);
                stmt.setString(2, author);
                stmt.setString(3, isbn);
                stmt.setString(4, publisher);
                stmt.setInt(5, year);
                stmt.setInt(6, categoryId);
                stmt.setString(7, location);
                stmt.setInt(8, totalCopies);
                stmt.setInt(9, totalCopies); // 可借阅数量等于总藏书量

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    showMessagePage(response, "录入成功", "新书已成功录入！", username);
                } else {
                    showMessagePage(response, "错误", "录入失败，请稍后重试！", username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showMessagePage(response, "数据库错误", "无法连接数据库，请联系管理员！", username);
        }
    }

    private void showAddBookPage(HttpServletResponse response, String username) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(String.format("""
            <!DOCTYPE html>
            <html lang="zh">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>录入新书</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            </head>
            <body class="d-flex justify-content-center align-items-center vh-100" style="background-color: #f8f9fa;">
                <div class="menu-container bg-white p-4 rounded shadow text-center">
                    <h2>录入新书</h2>
                    <form method="post">
                        <div class="mb-3">
                            <label class="form-label">书名：</label>
                            <input type="text" name="bookTitle" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">作者：</label>
                            <input type="text" name="author" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">ISBN：</label>
                            <input type="text" name="isbn" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">出版社：</label>
                            <input type="text" name="publisher" class="form-control">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">出版年份：</label>
                            <input type="number" name="year" class="form-control">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">类别ID：</label>
                            <input type="number" name="categoryId" class="form-control">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">存放位置：</label>
                            <input type="text" name="location" class="form-control">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">总藏书量：</label>
                            <input type="number" name="totalCopies" class="form-control" min="1" required>
                        </div>
                        <button type="submit" class="btn btn-primary">提交</button>
                        <a href="manager_menu?username=%s" class="btn btn-secondary">返回菜单</a>
                    </form>
                </div>
            </body>
            </html>
        """, username));
    }

    private void showMessagePage(HttpServletResponse response, String title, String message, String username) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(String.format("""
            <!DOCTYPE html>
            <html lang="zh">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>%s</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
            </head>
            <body class="d-flex justify-content-center align-items-center vh-100" style="background-color: #f8f9fa;">
                <div class="menu-container bg-white p-4 rounded shadow text-center">
                    <h2>%s</h2>
                    <p>%s</p>
                    <a href="manager_menu?username=%s" class="btn btn-secondary">返回菜单</a>
                </div>
            </body>
            </html>
        """, title, title, message, username));
    }
}
