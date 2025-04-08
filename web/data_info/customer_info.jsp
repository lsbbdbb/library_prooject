<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.util.List" %> <!-- 导入 List 类型 -->

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户信息</title>
    <style>
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        table, th, td { border: 1px solid black; }
        th, td { padding: 8px; text-align: left; }
    </style>
</head>
<body>
<h2>用户信息</h2>
<table>
    <thead>
    <tr>
        <th>用户名</th>
        <th>密码</th>
        <th>姓名</th>
        <th>操作</th> <!-- 新增操作列 -->
    </tr>
    </thead>
    <tbody>
    <%
        List<String[]> customerList = (List<String[]>) session.getAttribute("customerList");
        if (customerList != null && !customerList.isEmpty()) {
            // 使用 for 循环来处理客户列表
            for (int i = 0; i < customerList.size(); i++) {
                String[] customer = customerList.get(i);
    %>
    <tr>
        <td><%= customer[0] %></td>
        <td><%= customer[1] %></td>
        <td><%= customer[2] %></td>
        <td>
            <!-- 编辑链接，传递user_name -->
            <a href="edit_customer.jsp?user_name=<%= customer[0] %>">编辑</a>
        </td>
    </tr>
    <%
        }
    } else {
    %>
    <tr><td colspan="4">没有找到用户数据。</td></tr>
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
