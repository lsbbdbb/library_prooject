<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
  String username = (String) session.getAttribute("customer_user");
  if (username == null) {
    // 如果 session 中没有用户名，说明用户未登录，重定向到登录页面
    response.sendRedirect("login.html");
    return;  // 退出当前页面的执行，防止继续显示页面内容
  }
%>

<html>
<head>
  <title>续借书籍</title>
  <link rel="stylesheet" type="text/css" >
</head>
<body>
<div class="container">
  <h1>续借书籍</h1>
  <c:if test="${not empty message}">
    <div class="message">${message}</div>
  </c:if>

  <form action="renew_book" method="get">
    <input type="hidden" name="username" value="${param.username}">
    <input type="hidden" name="recordId" value="${param.recordId}">
    <div class="form-group">
      <button type="submit" class="btn">续借</button>
    </div>
  </form>

  <a href="return_book.jsp?username=${param.username}" class="back-link">返回借书记录</a>
</div>
</body>
</html>
