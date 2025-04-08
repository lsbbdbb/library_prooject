package com.servlet;

import com.LibraryDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/DatabaseInfoServlet")
public class DatabaseInfoServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 查询 Manager 表
        List<String[]> managerList = getManagerData();
        // 查询 Customer 表
        List<String[]> customerList = getCustomerData();
        // 查询 Books 表
        List<String[]> booksList = getBooksData();

        // 获取 session 对象
        HttpSession session = request.getSession();

        // 将数据保存到 session 中
        session.setAttribute("managerList", managerList);
        session.setAttribute("customerList", customerList);
        session.setAttribute("booksList", booksList);

        // 获取 username 参数，并进行 URL 编码
        String username = request.getParameter("username");
        if (username != null && !username.isEmpty()) {
            // 只进行一次编码
            String encodedUsername = URLEncoder.encode(username, "UTF-8");
            session.setAttribute("username", encodedUsername);
        }

        // 重定向到数据库信息展示页面
        response.sendRedirect("data_info/database_info.jsp");
    }

    private List<String[]> getManagerData() {
        return fetchDataFromDatabase("SELECT * FROM Manager");
    }

    private List<String[]> getCustomerData() {
        return fetchDataFromDatabase("SELECT * FROM Customer");
    }

    private List<String[]> getBooksData() {
        return fetchDataFromDatabase("SELECT * FROM books");
    }

    private List<String[]> fetchDataFromDatabase(String sql) {
        List<String[]> data = new ArrayList<>();
        try (Connection conn = LibraryDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                List<String> rowData = new ArrayList<>();
                int columnCount = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.add(rs.getString(i));
                }
                data.add(rowData.toArray(new String[0]));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
