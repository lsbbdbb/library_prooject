<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>数据库信息展示</title>
  <link rel="stylesheet" type="text/css" href="styles.css">
</head>
<body>
<div class="container">
  <h2>欢迎访问数据库信息展示页面</h2>

  <div class="links">
    <a href="manager_info.jsp">查看管理员信息</a>
    <a href="customer_info.jsp">查看用户信息</a>
    <a href="books_info.jsp">查看图书信息</a>
  </div>

  <h3>简要信息：</h3>
  <div>
    <h4>管理员信息 (Manager)：</h4>
    <p>当前管理员数量：
      <c:choose>
        <c:when test="${not empty managerList}">${fn:length(managerList)}</c:when>
        <c:otherwise>没有数据</c:otherwise>
      </c:choose>
    </p>

    <h4>用户信息 (Customer)：</h4>
    <p>当前用户数量：
      <c:choose>
        <c:when test="${not empty customerList}">${fn:length(customerList)}</c:when>
        <c:otherwise>没有数据</c:otherwise>
      </c:choose>
    </p>

    <h4>图书信息 (Books)：</h4>
    <p>当前图书数量：
      <c:choose>
        <c:when test="${not empty booksList}">${fn:length(booksList)}</c:when>
        <c:otherwise>没有数据</c:otherwise>
      </c:choose>
    </p>
  </div>

  <a href="${pageContext.request.contextPath}/manager_menu?username=${sessionScope.username}" class="return-link">返回菜单</a>
</div>
</body>
</html>
