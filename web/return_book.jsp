<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List, com.BorrowRecord, java.net.URLEncoder" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>归还图书</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            text-align: center;
        }
        table {
            width: 80%;
            border-collapse: collapse;
            margin: 20px auto;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: center;
        }
        th {
            background-color: #f4f4f4;
        }
        .overdue {
            color: #d9534f; /* 颜色对比度更强 */
            font-weight: bold;
        }
        .borrowed {
            color: green;
        }
        .returned {
            color: gray;
        }
        .lost {
            color: black;
            font-style: italic;
        }
        .button {
            padding: 5px 10px;
            color: white;
            border: none;
            cursor: pointer;
        }
        .return-btn {
            background-color: blue;
        }
        .return-btn:hover {
            background-color: darkblue;
        }
    </style>
</head>
<body>
<h2>借书记录</h2>

<%
    String username = (String) request.getAttribute("username");
    List<BorrowRecord> records = (List<BorrowRecord>) request.getAttribute("records");
    if (records == null || records.isEmpty()) {
%>
<p style="color:red;">您没有未归还的书籍。</p>
<%
} else {
%>
<table>
    <tr>
        <th>书名</th>
        <th>借阅日期</th>
        <th>归还截止日期</th>
        <th>实际归还日期</th>
        <th>状态</th>
        <th>操作</th>
    </tr>
    <c:forEach var="record" items="${records}">
        <tr>
            <td><c:out value="${record.title}" /></td>
            <td><c:out value="${record.borrowDate}" /></td>
            <td><c:out value="${record.dueDate}" /></td>
            <td><c:out value="${record.returnDate != null ? record.returnDate : '未归还'}" /></td>
            <td class="${record.status == '逾期' ? 'overdue' : record.status == '借阅中' ? 'borrowed' : record.status == '已归还' ? 'returned' : 'lost'}">
                <c:out value="${record.status}" />
            </td>
            <td>
                <c:if test="${record.status == '借阅中' || record.status == '逾期'}">
                    <form action="return_book" method="post">
                        <input type="hidden" name="recordId" value="<c:out value='${record.recordId}' />">
                        <input type="hidden" name="username" value="<c:out value='${username}' />">
                        <button type="submit" class="button return-btn">归还</button>
                    </form>
                </c:if>
                <c:if test="${record.status != '借阅中' && record.status != '逾期'}">
                    -
                </c:if>
            </td>
        </tr>
    </c:forEach>
</table>
<%
    }
%>

<p><a href="customer_menu?username=${username}" class="btn btn-secondary">返回菜单</a></p>
</body>
</html>
