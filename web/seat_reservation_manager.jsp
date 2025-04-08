<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>座位预约管理</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #eef2f3;
      margin: 0;
      padding: 20px;
    }
    .container {
      max-width: 900px;
      margin: 0 auto;
      background: #fff;
      padding: 30px;
      border-radius: 8px;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }
    h2 {
      text-align: center;
      color: #333;
    }
    table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 20px;
    }
    th, td {
      padding: 12px;
      text-align: center;
      border: 1px solid #ccc;
    }
    th {
      background-color: #007BFF;
      color: #fff;
    }
    button {
      background-color: #f44336;
      color: white;
      border: none;
      padding: 8px 16px;
      cursor: pointer;
      border-radius: 4px;
      font-size: 14px;
    }
    button:hover {
      background-color: #d32f2f;
    }
    .refresh-btn {
      background-color: #007BFF;
    }
    .refresh-btn:hover {
      background-color: #0056b3;
    }
    .message {
      text-align: center;
      font-size: 14px;
      margin-top: 10px;
    }
  </style>
</head>
<body>

<div class="container">
  <h2>座位预约管理</h2>
  <button class="refresh-btn" onclick="window.location.reload()">刷新预约记录</button>
  <div class="message" id="message"></div>
  <table id="reservationsTable">
    <thead>
    <tr>
      <th>预约ID</th>
      <th>座位号</th>
      <th>预约用户</th>
      <th>开始时间</th>
      <th>结束时间</th>
      <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <!-- 使用 JSTL 来遍历 reservations 并显示 -->
    <c:forEach var="reservation" items="${reservations}">
      <tr>
        <td>${reservation.reservationId}</td>
        <td>${reservation.seatNumber}</td>
        <td>${reservation.customerUser}</td>
        <td>${reservation.startTime}</td>
        <td>${reservation.endTime}</td>
        <td>
          <button onclick="cancelReservation(${reservation.reservationId})">取消预约</button>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty reservations}">
      <tr><td colspan="6">暂无预约记录</td></tr>
    </c:if>
    </tbody>
  </table>
</div>

<script>
  // 取消预约
  function cancelReservation(reservationId) {
    if (confirm("确定要取消该预约吗？")) {
      $.post("seat_manager", { action: "cancelReservation", reservation_id: reservationId }, function(response) {
        if (response.success) {
          $("#message").text(response.success).css("color", "green");
          window.location.reload(); // 刷新页面
        } else {
          $("#message").text(response.error).css("color", "red");
        }
      }, "json");
    }
  }
</script>
<div class="container" style="text-align: center; margin-top: 20px;">
  <a href="manager_menu?username=${param.username}" class="btn" style="padding: 10px 20px; background-color: #007BFF; color: #fff; text-decoration: none; border-radius: 4px; transition: background-color 0.3s ease;">返回菜单</a>
</div>

</body>
</html>
