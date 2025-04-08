<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>借阅信息</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <h2>借阅操作结果</h2>

    <%
        String message = (String) request.getAttribute("message");
        if (message != null) {
    %>
    <div class="alert alert-info" role="alert">
        <%= message %>
    </div>
    <%
        }

        // Check if there is a warning for over 90 days
        String warning = (String) request.getAttribute("warning");
        if (warning != null) {
    %>
    <div class="alert alert-warning" role="alert">
        <%= warning %>
    </div>
    <%
        }

        Integer recordId = (Integer) request.getAttribute("recordId");
        if (recordId != null) {
    %>
    <p>借阅记录ID: <%= recordId.toString() %></p>
    <p>您的借阅操作已经处理完毕。</p>
    <%
        }
    %>

    <div class="mt-3">
        <a href="customer_menu?username=<%= request.getParameter("username") %>" class="btn btn-secondary">返回菜单</a>
    </div>
</div>

<!-- 引入Bootstrap JavaScript -->
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.11.6/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.min.js"></script>
</body>
</html>
