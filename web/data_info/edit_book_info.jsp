<%@ page import="com.LibraryDB" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>编辑图书信息</title>
</head>
<body>
<h2>编辑图书信息</h2>

<%
    // 获取书籍ID
    String bookId = request.getParameter("book_id");

    Connection conn = LibraryDB.getConnection();
    String sql = "SELECT * FROM books WHERE book_id = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setInt(1, Integer.parseInt(bookId));
    ResultSet rs = stmt.executeQuery();
    String[] book = null;
    if (rs.next()) {
        try {
            book = new String[] {
                    rs.getString("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getString("publisher"),
                    rs.getString("year"),
                    rs.getString("category_id"),
                    rs.getString("location"),
                    rs.getString("total_copies"),
                    rs.getString("available_copies"),
                    rs.getString("status")
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
%>

<form action="${pageContext.request.contextPath}/UpdateBookInfoServlet" method="post">
    <input type="hidden" name="book_id" value="<%= book[0] %>" />
    <label for="title">书名：</label>
    <input type="text" id="title" name="title" value="<%= book[1] %>" required /><br/>

    <label for="author">作者：</label>
    <input type="text" id="author" name="author" value="<%= book[2] %>" required /><br/>

    <label for="isbn">ISBN：</label>
    <input type="text" id="isbn" name="isbn" value="<%= book[3] %>" required /><br/>

    <label for="publisher">出版社：</label>
    <input type="text" id="publisher" name="publisher" value="<%= book[4] %>" required /><br/>

    <label for="year">年份：</label>
    <input type="text" id="year" name="year" value="<%= book[5] %>" required /><br/>

    <label for="category_id">分类ID：</label>
    <input type="text" id="category_id" name="category_id" value="<%= book[6] %>" required /><br/>

    <label for="location">位置：</label>
    <input type="text" id="location" name="location" value="<%= book[7] %>" required /><br/>

    <label for="total_copies">总数量：</label>
    <input type="text" id="total_copies" name="total_copies" value="<%= book[8] %>" required /><br/>

    <label for="available_copies">可借数量：</label>
    <input type="text" id="available_copies" name="available_copies" value="<%= book[9] %>" required /><br/>

    <label for="status">状态：</label>
    <select id="status" name="status">
        <option value="正常" <%= "正常".equals(book[10]) ? "selected" : "" %>>正常</option>
        <option value="维修" <%= "维修".equals(book[10]) ? "selected" : "" %>>维修</option>
        <option value="丢失" <%= "丢失".equals(book[10]) ? "selected" : "" %>>丢失</option>
    </select><br/>


    <input type="submit" value="提交修改" />
</form>

</body>
</html>
