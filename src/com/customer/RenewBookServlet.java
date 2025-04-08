package com.customer;

import com.LibraryDB;
import com.BorrowRecord;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/renew_book")
public class RenewBookServlet extends HttpServlet {
    private Connection connection;

    public RenewBookServlet() {
        try {
            this.connection = LibraryDB.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String customerName = request.getParameter("username");
        if (customerName == null || customerName.isEmpty()) {
            response.sendRedirect("login.html");
            return;
        }

        String confirm = request.getParameter("confirm");
        String recordIdParam = request.getParameter("recordId");

        if (confirm != null && confirm.equals("yes") && recordIdParam != null) {
            int recordId;
            try {
                recordId = Integer.parseInt(recordIdParam);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的借阅记录ID");
                return;
            }

            // Check if the borrow duration exceeds 120 days
            boolean canRenew = !isOver120Days(recordId);
            if (canRenew) {
                boolean success = renewBook(recordId);
                if (success) {
                    request.setAttribute("message", "续借成功！");
                } else {
                    request.setAttribute("message", "续借失败，请重试！");
                }
            } else {
                request.setAttribute("message", "无法续借：借阅时间超过120天，不能继续续借！");
            }

            // Check if borrow duration exceeds 90 days
            if (isOver90Days(recordId)) {
                request.setAttribute("warning", "警告：您的借阅时间已经超过90天，续借可能会受到限制！");
            }

            request.setAttribute("recordId", recordId); // Pass the recordId to the JSP
            request.getRequestDispatcher("borrow_info.jsp").forward(request, response);
            return;
        }

        String customerUser = getCustomerUser(customerName);
        if (customerUser == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户不存在");
            return;
        }

        List<BorrowRecord> records = getRenewableRecords(customerUser);
        request.setAttribute("records", records);
        request.setAttribute("username", customerName);
        request.getRequestDispatcher("renew_book.jsp").forward(request, response);
    }

    private String getCustomerUser(String customerName) {
        String query = "SELECT customer_user FROM Customer WHERE customer_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, customerName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("customer_user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<BorrowRecord> getRenewableRecords(String customerUser) {
        List<BorrowRecord> records = new ArrayList<>();
        String query = "SELECT br.record_id, b.title, br.borrow_date, br.due_date, br.return_date, br.status " +
                "FROM borrow_records br JOIN books b ON br.book_id = b.book_id " +
                "WHERE br.customer_user = ? AND br.status IN ('借阅中','逾期')";
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

    private boolean renewBook(int recordId) {
        String updateQuery = "UPDATE borrow_records SET due_date = DATE_ADD(due_date, INTERVAL 30 DAY) WHERE record_id = ? AND status IN ('借阅中', '逾期')";
        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.setInt(1, recordId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to check if borrow time exceeds 90 days
    private boolean isOver90Days(int recordId) {
        String query = "SELECT borrow_date, due_date FROM borrow_records WHERE record_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, recordId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Date borrowDate = rs.getDate("borrow_date");
                Date dueDate = rs.getDate("due_date");
                if (borrowDate != null && dueDate != null) {
                    long diff = dueDate.getTime() - borrowDate.getTime();
                    long diffDays = diff / (1000 * 60 * 60 * 24); // Convert milliseconds to days
                    return diffDays > 90;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to check if borrow time exceeds 120 days
    private boolean isOver120Days(int recordId) {
        String query = "SELECT borrow_date, due_date FROM borrow_records WHERE record_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, recordId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Date borrowDate = rs.getDate("borrow_date");
                Date dueDate = rs.getDate("due_date");
                if (borrowDate != null && dueDate != null) {
                    long diff = dueDate.getTime() - borrowDate.getTime();
                    long diffDays = diff / (1000 * 60 * 60 * 24); // Convert milliseconds to days
                    return diffDays > 120;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void destroy() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
