<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>续借确认</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container">
  <h2 class="text-center">续借确认</h2>
  <div class="alert alert-warning text-center">
    确定要续借此图书吗？新的到期日期将延长 30 天。
  </div>
  <form action="renew_book" method="get" class="text-center">
    <input type="hidden" name="username" value="${param.username}">
    <input type="hidden" name="recordId" value="${param.recordId}">
    <input type="hidden" name="confirm" value="yes">
    <button type="submit" class="btn btn-success">确认续借</button>
    <a href="renew_book?username=${param.username}" class="btn btn-secondary">取消</a>
  </form>
</div>
</body>
</html>
