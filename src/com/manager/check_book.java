package com.manager;

import com.LibraryDB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@WebServlet("/check_book")
public class check_book extends HttpServlet {
    private Connection connection;

    public check_book() {
        try {
            this.connection = LibraryDB.getConnection(); // 使用 LibraryDB 获取数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database connection failed!");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        request.setAttribute("username", username); // 无论 action 是什么都先加这行
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "";
        }
        try {
            switch (action) {
                case "updateStatus":
                    updateBookStatus(request);
                    break;
                case "updateBorrowRecord":
                    updateBorrowRecord(request);
                    break;
                default:
                    // 在更新后重新加载页面数据
                    showBookList(request);
                    showBorrowRecords(request);
                    break;
            }
            // 重新渲染更新后的页面
            RequestDispatcher dispatcher = request.getRequestDispatcher("/check_book.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "数据库操作错误: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/check_book.jsp");
            dispatcher.forward(request, response);
        }
    }

    // 更新书籍状态
    private void updateBookStatus(HttpServletRequest request) throws SQLException {

        int bookId = Integer.parseInt(request.getParameter("bookId"));
        String newStatus = request.getParameter("status");
        if (!newStatus.equals("正常") && !newStatus.equals("维修") && !newStatus.equals("丢失")) {
            throw new SQLException("Invalid status value for book");
        }
        String sql = "UPDATE books SET status = ? WHERE book_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
        // 更新状态后重新获取数据并加载到页面
        showBookList(request);
        showBorrowRecords(request);
    }

    // 更新借阅记录状态
    private void updateBorrowRecord(HttpServletRequest request) throws SQLException {
        String username = request.getParameter("username");
        int recordId = Integer.parseInt(request.getParameter("recordId"));
        String newStatus = request.getParameter("status");
        String currentStatus = getCurrentStatus(recordId);
        if (currentStatus != null && !currentStatus.equals(newStatus)) {
            if (!newStatus.equals("借阅中") && !newStatus.equals("已归还") &&
                    !newStatus.equals("逾期") && !newStatus.equals("丢失")) {
                throw new SQLException("Invalid status value for borrow record: " + newStatus);
            }

            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = LibraryDB.getConnection();
                conn.setAutoCommit(false); // 开启事务，确保数据一致性

                // 更新借阅记录的状态
                String sql;
                if (newStatus.equals("已归还")) {
                    sql = "UPDATE borrow_records SET status = ?, return_date = CURRENT_DATE WHERE record_id = ?";
                } else if (newStatus.equals("借阅中")) {
                    sql = "UPDATE borrow_records SET status = ?, borrow_date = CURRENT_DATE, return_date = NULL WHERE record_id = ?";
                } else {
                    sql = "UPDATE borrow_records SET status = ? WHERE record_id = ?";
                }

                stmt = conn.prepareStatement(sql);
                stmt.setString(1, newStatus);
                stmt.setInt(2, recordId);
                stmt.executeUpdate();
                stmt.close();

                // 如果是 "已归还"，则更新 books 表的 available_copies
                if (newStatus.equals("已归还")) {
                    String updateBookQuery = "UPDATE books SET available_copies = available_copies + 1 " +
                            "WHERE book_id = (SELECT book_id FROM borrow_records WHERE record_id = ?)";
                    stmt = conn.prepareStatement(updateBookQuery);
                    stmt.setInt(1, recordId);
                    stmt.executeUpdate();
                }
                if (newStatus.equals("借阅中")) {
                    String updateBookQuery = "UPDATE books SET available_copies = available_copies - 1 " +
                            "WHERE book_id = (SELECT book_id FROM borrow_records WHERE record_id = ?) AND available_copies > 0";
                    stmt = conn.prepareStatement(updateBookQuery);
                    stmt.setInt(1, recordId);
                    stmt.executeUpdate();
                }
                conn.commit(); // 提交事务
            } catch (SQLException e) {
                if (conn != null) conn.rollback(); // 发生错误时回滚事务
                throw e;
            } finally {
                if (stmt != null) stmt.close();
                if (conn != null) conn.setAutoCommit(true);
            }
        }

        // 更新状态后重新获取数据并加载到页面
        showBookList(request);
        showBorrowRecords(request);
    }

    // 展示书籍列表
    private void showBookList(HttpServletRequest request) throws SQLException {
        int page = 1;
        int recordsPerPage = 10;
        String pageParam = request.getParameter("bookPage");
        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }
        int start = (page - 1) * recordsPerPage;

        String sql = "SELECT * FROM books LIMIT ?, ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, start);
            stmt.setInt(2, recordsPerPage);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> bookList = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> book = new HashMap<>();
                    book.put("book_id", rs.getInt("book_id"));
                    book.put("title", rs.getString("title"));
                    book.put("author", rs.getString("author"));
                    book.put("status", rs.getString("status"));
                    bookList.add(book);
                }
                request.setAttribute("bookList", bookList);
            }
        }
        String paginationSql = "SELECT COUNT(*) FROM books";
        try (PreparedStatement countStmt = connection.prepareStatement(paginationSql);
             ResultSet countRs = countStmt.executeQuery()) {
            if (countRs.next()) {
                int totalRecords = countRs.getInt(1);
                int totalPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
                request.setAttribute("bookTotalPages", totalPages);
            }
        }
    }

    // 展示借阅记录列表
    private void showBorrowRecords(HttpServletRequest request) throws SQLException {
        int page = 1;
        int recordsPerPage = 10;
        String pageParam = request.getParameter("recordPage");
        if (pageParam != null) {
            page = Integer.parseInt(pageParam);
        }
        int start = (page - 1) * recordsPerPage;

        String sql = "SELECT br.record_id, c.customer_name, b.title, br.borrow_date, br.due_date, br.return_date, br.status " +
                "FROM borrow_records br " +
                "JOIN Customer c ON br.customer_user = c.customer_user " +
                "JOIN books b ON br.book_id = b.book_id " +
                "LIMIT ?, ?";
        List<Map<String, Object>> borrowRecords = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, start);
            stmt.setInt(2, recordsPerPage);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("customer_name", rs.getString("customer_name"));
                    record.put("book_title", rs.getString("title"));
                    record.put("borrow_date", rs.getDate("borrow_date"));
                    record.put("due_date", rs.getDate("due_date"));
                    record.put("return_date", rs.getDate("return_date"));
                    record.put("status", rs.getString("status"));
                    record.put("record_id", rs.getInt("record_id"));
                    borrowRecords.add(record);
                }
            }
        }
        request.setAttribute("borrowRecordList", borrowRecords);
        String paginationSql = "SELECT COUNT(*) FROM borrow_records";
        try (PreparedStatement countStmt = connection.prepareStatement(paginationSql);
             ResultSet countRs = countStmt.executeQuery()) {
            if (countRs.next()) {
                int totalRecords = countRs.getInt(1);
                int totalPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
                request.setAttribute("recordTotalPages", totalPages);
            }
        }
    }

    // 获取当前借阅记录的状态
    private String getCurrentStatus(int recordId) throws SQLException {
        String sql = "SELECT status FROM borrow_records WHERE record_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return null;
    }

    @Override
    public void destroy() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
