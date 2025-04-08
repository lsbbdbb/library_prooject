<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>续借图书</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
    <h2 class="text-center">可续借图书列表</h2>
    <table class="table table-striped">
        <thead>
        <tr>
            <th>书名</th>
            <th>借阅日期</th>
            <th>到期日期</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="record" items="${records}">
            <tr>
                <td>${record.title}</td>
                <td>${record.borrowDate}</td>
                <td>${record.dueDate}</td>
                <td>${record.status}</td>
                <td>
                    <a href="renew_book_confirm.jsp?username=${username}&recordId=${record.recordId}" class="btn btn-primary">续借</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <a href="customer_menu?username=${username}" class="btn btn-secondary">返回菜单</a>
</div>
</body>
</html>
