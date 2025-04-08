<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.util.List" %> <!-- 导入 List 类型 -->

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>图书信息</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            padding: 8px;
            text-align: left;
        }
        h2 {
            text-align: center;
        }
        .links {
            margin: 20px 0;
            text-align: center;
        }
        .links a {
            margin: 0 15px;
            text-decoration: none;
            font-weight: bold;
            color: blue;
        }
    </style>
</head>
<body>
<h2>图书信息展示</h2>

<!-- 图书信息表格 -->
<table>
    <thead>
    <tr>
        <th>书籍ID</th>
        <th>书名</th>
        <th>作者</th>
        <th>ISBN</th>
        <th>出版社</th>
        <th>年份</th>
        <th>分类ID</th>
        <th>位置</th>
        <th>总数量</th>
        <th>可借数量</th>
        <th>状态</th>
        <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <%
        // 从Session中获取图书数据
        List<String[]> booksList = (List<String[]>) session.getAttribute("booksList");
        if (booksList != null) {
            for (String[] book : booksList) {
    %>
    <tr>
        <td><%= book[0] %></td>
        <td><%= book[1] %></td>
        <td><%= book[2] %></td>
        <td><%= book[3] %></td>
        <td><%= book[4] %></td>
        <td><%= book[5] %></td>
        <td><%= book[6] %></td>
        <td><%= book[7] %></td>
        <td><%= book[8] %></td>
        <td><%= book[9] %></td>
        <td><%= book[10] %></td>
        <td>
            <!-- 修改按钮，跳转到修改页面 -->
            <a href="edit_book_info.jsp?book_id=<%= book[0] %>">修改</a>
        </td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="12">没有数据</td>
    </tr>
    <%
        }
    %>
    </tbody>
</table>
<a href="customer_info.jsp">查看用户信息</a> |
<a href="books_info.jsp">查看图书信息</a> |
<a href="database_info.jsp">返回主页</a>
</body>
</html>
