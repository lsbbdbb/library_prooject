<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.LibraryDB" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>编辑用户信息</title>
</head>
<body>
<h2>编辑用户信息</h2>

<%
  // 获取用户邮箱 (customer_user)
  String customerUser = request.getParameter("user_name");

  // 输出customer_user信息以确认
  out.println("<p>当前用户邮箱: " + customerUser + "</p>");

  // 检查customerUser是否为有效的邮箱格式
  if (customerUser == null || !customerUser.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
    out.println("<p style='color:red;'>无效的邮箱地址！</p>");
  } else {
    // 查询用户信息
    Connection conn = LibraryDB.getConnection();
    String sql = "SELECT * FROM Customer WHERE customer_user = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setString(1, customerUser);
    ResultSet rs = stmt.executeQuery();

    String customerName = "";
    String customerPassword = "";
    if (rs.next()) {
      customerName = rs.getString("customer_name");
      customerPassword = rs.getString("customer_password");
    }
%>

<form action="${pageContext.request.contextPath}/UpdateCustomerInfoServlet" method="post">
  <input type="hidden" name="customer_user" value="<%= customerUser %>" />

  <label for="customer_name">客户名称：</label>
  <input type="text" id="customer_name" name="customer_name" value="<%= customerName %>" required /><br/>

  <label for="customer_password">客户密码：</label>
  <input type="password" id="customer_password" name="customer_password" value="<%= customerPassword %>" required /><br/>

  <input type="submit" value="提交修改" />
</form>

<%
  }
%>

</body>
</html>
