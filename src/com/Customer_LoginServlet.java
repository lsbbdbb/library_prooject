package com.library.servlet;

import com.LibraryDB;

import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/Customer_LoginServlet")
public class Customer_LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 获取请求参数
        String username = request.getParameter("emails");
        String password = request.getParameter("password");

        // 验证用户信息
        Customer customer = validateUser(username, password);

        if (customer != null) {
            // 登录成功：保存信息到 session
            HttpSession session = request.getSession();
            session.setAttribute("customer_user", customer.customerUser);   // 登录标识
            session.setAttribute("customer_name", customer.customerName);   // 可选展示

            // 跳转页面
            String encodedCustomerName = URLEncoder.encode(customer.customerName, "UTF-8");
            response.sendRedirect("customer_menu?username=" + encodedCustomerName);
        } else {
            // 登录失败，转发回登录页并提示
            request.setAttribute("errorMessage", "用户名或密码错误！");
            request.getRequestDispatcher("login.html").forward(request, response);
        }
    }

    // 验证用户，并返回包含信息的对象
    private Customer validateUser(String username, String password) {
        String sql = "SELECT customer_user, customer_name FROM Customer WHERE customer_user = ? AND customer_password = ?";
        try (Connection conn = LibraryDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.customerUser = rs.getString("customer_user");
                    customer.customerName = rs.getString("customer_name");
                    return customer;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 内部类用于封装用户信息
    private static class Customer {
        String customerUser;
        String customerName;
    }
}
