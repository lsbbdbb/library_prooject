package com.customer;

import com.LibraryDB;
import com.BorrowRecord;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/return_book")
public class ReturnBookServlet extends HttpServlet {
    private Connection connection;

    public ReturnBookServlet() {
        try {
            this.connection = LibraryDB.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        if (username == null || username.isEmpty()) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 获取 customer_user
        String customerUser = getCustomerUser(username);
        if (customerUser == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户不存在");
            return;
        }

        // 查询借书记录
        List<BorrowRecord> records = getUserBorrowRecords(customerUser);
        request.setAttribute("records", records);
        request.setAttribute("username", username);
        request.getRequestDispatcher("return_book.jsp").forward(request, response);
    }

    /**
     * 通过 customer_name 查找 customer_user
     */
    private String getCustomerUser(String userName) {
        String query = "SELECT customer_user FROM Customer WHERE customer_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("customer_user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取用户的借书记录
     */
    private List<BorrowRecord> getUserBorrowRecords(String customerUser) {
        List<BorrowRecord> records = new ArrayList<>();
        String query = "SELECT br.record_id, b.title, br.borrow_date, br.due_date, br.return_date, br.status " +
                "FROM borrow_records br JOIN books b ON br.book_id = b.book_id " +
                "WHERE br.customer_user = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, customerUser);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BorrowRecord record = new BorrowRecord(
                        rs.getInt("record_id"),
                        rs.getString("title"),
                        rs.getDate("borrow_date"),
                        rs.getDate("due_date"),
                        rs.getDate("return_date"),
                        rs.getString("status")
                );
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        int recordId = Integer.parseInt(request.getParameter("recordId"));
        String username = request.getParameter("username");

        returnBook(recordId);

        // **解决中文重定向问题**
        String encodedUsername = URLEncoder.encode(username, "UTF-8");
        response.sendRedirect("return_book?username=" + encodedUsername);
    }

    /**
     * 归还图书
     */
    private void returnBook(int recordId) {
        try {
            String updateQuery = "UPDATE borrow_records SET status = '已归还', return_date = CURDATE() WHERE record_id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, recordId);
                updateStmt.executeUpdate();
            }

            String updateBookQuery = "UPDATE books SET available_copies = available_copies + 1 " +
                    "WHERE book_id = (SELECT book_id FROM borrow_records WHERE record_id = ?)";
            try (PreparedStatement bookStmt = connection.prepareStatement(updateBookQuery)) {
                bookStmt.setInt(1, recordId);
                bookStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
