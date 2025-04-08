<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.LibraryDB" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>编辑管理员信息</title>
</head>
<body>
<h2>编辑管理员信息</h2>

<%
  // 获取用户邮箱 (customer_user)
  String managerUser = request.getParameter("user_name");

  // 输出customer_user信息以确认
  out.println("<p>当前用户邮箱: " + managerUser + "</p>");

  // 检查customerUser是否为有效的邮箱格式
  if (managerUser == null || !managerUser.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
    out.println("<p style='color:red;'>无效的邮箱地址！</p>");
  } else {
    // 查询用户信息
    Connection conn = LibraryDB.getConnection();
    String sql = "SELECT * FROM Manager WHERE manager_user = ?";
    PreparedStatement stmt = conn.prepareStatement(sql);
    stmt.setString(1, managerUser);
    ResultSet rs = stmt.executeQuery();

    String managerName = "";
    String managerPassword = "";
    if (rs.next()) {
      managerName = rs.getString("manager_name");
      managerPassword = rs.getString("manager_password");
    }
%>

<form action="${pageContext.request.contextPath}/UpdateManagerInfoServlet" method="post">
  <input type="hidden" name="manager_user" value="<%= managerUser %>" />

  <label for="manager_name">客户名称：</label>
  <input type="text" id="manager_name" name="manager_name" value="<%= managerName %>" required /><br/>

  <label for="manager_password">客户密码：</label>
  <input type="password" id="manager_password" name="manager_password" value="<%= managerPassword %>" required /><br/>

  <input type="submit" value="提交修改" />
</form>

<%
  }
%>

</body>
</html>
