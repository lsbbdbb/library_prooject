<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String username = (String) session.getAttribute("customer_user");
    if (username == null) {
        // 如果 session 中没有用户名，说明用户未登录，重定向到登录页面
        response.sendRedirect("login.jsp");
        return;  // 退出当前页面的执行，防止继续显示页面内容
    }
%>

<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>借书管理</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <h2>图书借阅</h2>
    <div class="alert alert-info" role="alert">
        请根据下表选择您要借阅的图书。
    </div>

    <!-- 显示图书列表 -->
    <table class="table table-striped">
        <thead>
        <tr>
            <th>书名</th>
            <th>作者</th>
            <th>ISBN</th>
            <th>可借数量</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <!-- 使用 JSTL 循环展示图书 -->
        <c:forEach var="book" items="${books}">
            <tr>
                <td>${book.title}</td>
                <td>${book.author}</td>
                <td>${book.isbn}</td>
                <td>${book.availableCopies}</td> <!-- 修改此处 -->
                <td>
                    <!-- 判断是否有可借的书籍 -->
                    <c:if test="${book.availableCopies > 0}"> <!-- 修改此处 -->
                        <a href="borrow_book?action=borrow&bookId=${book.bookId}&username=${username}" class="btn btn-primary">借书</a> <!-- 修改此处 -->
                    </c:if>
                    <c:if test="${book.availableCopies <= 0}">
                        <span class="text-danger">无可借书籍</span>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="mt-3">
        <a href="customer_menu?username=${username}" class="btn btn-secondary">返回菜单</a>
    </div>
</div>

<!-- 引入Bootstrap JavaScript -->
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.min.js"></script>
</body>
</html>
