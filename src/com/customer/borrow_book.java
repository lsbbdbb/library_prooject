package com.manager;

import com.Book;
import com.LibraryDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/borrow_book")
public class borrow_book extends HttpServlet {

    private Connection connection;

    public borrow_book() {
        try {
            this.connection = LibraryDB.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String username = request.getParameter("username");
        String encodedUsername = URLEncoder.encode(username, "UTF-8");

        if ("borrow".equals(action)) {
            int bookId = Integer.parseInt(request.getParameter("bookId"));
            borrowBook(bookId, username, request, response);
        } else {
            showBooks(request, response, username);
        }
    }

    private void borrowBook(int bookId, String username, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String checkQuery = "SELECT available_copies, status, title FROM books WHERE book_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setInt(1, bookId);
            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next()) {
                int availableCopies = resultSet.getInt("available_copies");
                String bookStatus = resultSet.getString("status");
                String bookTitle = resultSet.getString("title");

                if (availableCopies > 0 && "正常".equals(bookStatus)) {
                    String sql = "SELECT customer_user FROM Customer WHERE customer_name=?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, username);
                    ResultSet rs = stmt.executeQuery();
                    String user = rs.next() ? rs.getString("customer_user") : null;

                    if (user != null) {
                        String borrowQuery = "INSERT INTO borrow_records (book_id, customer_user, borrow_date, status, due_date) VALUES (?, ?, CURDATE(), '借阅中', ?)";
                        LocalDate dueDate = LocalDate.now().plusDays(14);
                        PreparedStatement borrowStmt = connection.prepareStatement(borrowQuery);
                        borrowStmt.setInt(1, bookId);
                        borrowStmt.setString(2, user);
                        borrowStmt.setDate(3, java.sql.Date.valueOf(dueDate));
                        borrowStmt.executeUpdate();

                        String updateBookQuery = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?";
                        PreparedStatement updateStmt = connection.prepareStatement(updateBookQuery);
                        updateStmt.setInt(1, bookId);
                        updateStmt.executeUpdate();

                        // 成功借书后传递信息到前端页面
                        request.setAttribute("bookTitle", bookTitle);
                        request.setAttribute("borrowDate", LocalDate.now());
                        request.setAttribute("dueDate", dueDate);

                        // 跳转到借书成功页面
                        request.getRequestDispatcher("/borrow_success.jsp").forward(request, response);
                    }
                } else {
                    System.out.println("该图书不可借：库存不足或图书状态不为‘正常’。");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showBooks(HttpServletRequest request, HttpServletResponse response, String username) throws ServletException, IOException {
        try {
            String fetchBooksQuery = "SELECT * FROM books WHERE available_copies > 0 AND status = '正常'";
            PreparedStatement stmt = connection.prepareStatement(fetchBooksQuery);
            ResultSet rs = stmt.executeQuery();

            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setIsbn(rs.getString("isbn"));
                book.setAvailableCopies(rs.getInt("available_copies"));
                books.add(book);
            }
            request.setAttribute("books", books);
            request.setAttribute("username", username);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/borrow_book.jsp").forward(request, response);
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
