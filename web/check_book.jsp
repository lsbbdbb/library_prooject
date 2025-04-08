<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh">
<head>
  <meta charset="UTF-8">
  <title>书籍与借阅记录管理系统</title>
  <style>
    /* Reset 部分 */
    * { margin: 0; padding: 0; box-sizing: border-box; }

    body {
      font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
      background-color: #f4f7f9;
      color: #333;
      padding: 20px;
    }

    .container {
      max-width: 1200px;
      margin: 0 auto;
    }

    h1, h2 {
      text-align: center;
      margin-bottom: 20px;
      color: #007BFF;
    }

    /* 提示信息 */
    .error {
      text-align: center;
      color: #d9534f;
      margin-bottom: 20px;
    }

    /* 表格样式 */
    table {
      width: 100%;
      border-collapse: collapse;
      background-color: #fff;
      margin-bottom: 20px;
      box-shadow: 0 2px 5px rgba(0,0,0,0.1);
    }

    th, td {
      padding: 12px 15px;
      border: 1px solid #dee2e6;
      text-align: center;
    }

    th {
      background-color: #007BFF;
      color: #fff;
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    tr:nth-child(even) {
      background-color: #f9f9f9;
    }

    /* 表单与按钮 */
    form {
      display: inline-block;
    }

    select, input[type="submit"] {
      padding: 5px 10px;
      border: 1px solid #ccc;
      border-radius: 4px;
      font-size: 14px;
    }

    input[type="submit"] {
      background-color: #007BFF;
      color: #fff;
      cursor: pointer;
      transition: background-color 0.3s ease;
      border: none;
    }

    input[type="submit"]:hover {
      background-color: #0056b3;
    }

    /* 分页样式 */
    .pagination {
      text-align: center;
      margin: 20px 0;
    }

    .pagination a {
      display: inline-block;
      margin: 0 5px;
      padding: 8px 12px;
      background-color: #007BFF;
      color: #fff;
      text-decoration: none;
      border-radius: 4px;
      transition: background-color 0.3s ease;
    }

    .pagination a:hover {
      background-color: #0056b3;
    }

    hr {
      border: none;
      height: 1px;
      background: #ddd;
      margin: 40px 0;
    }

    /* 状态样式 */
    .status-repair {
      color: gray;
    }

    .status-lost {
      color: red;
    }

    .status-overdue {
      color: yellow;
    }
  </style>

  <!-- 引入 jQuery -->
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <script>
    $(document).ready(function(){
      // 拦截借阅记录更新表单
      $("form.updateRecordStatus").submit(function(e){
        e.preventDefault();
        var form = $(this);
        $.ajax({
          url: form.attr("action"),
          type: "get",
          data: form.serialize(),
          success: function(response){
            var newStatus = form.find("select[name='status']").val();
            // 更新当前行状态显示（假设状态在第6列，从0开始计）
            var statusCell = form.closest("tr").find("td:eq(5)");
            statusCell.text(newStatus);
            // 根据状态更新字体颜色
            if (newStatus === "维修") {
              statusCell.addClass("status-repair").removeClass("status-lost status-overdue");
            } else if (newStatus === "丢失") {
              statusCell.addClass("status-lost").removeClass("status-repair status-overdue");
            } else if (newStatus === "逾期") {
              statusCell.addClass("status-overdue").removeClass("status-repair status-lost");
            } else {
              statusCell.removeClass("status-repair status-lost status-overdue");
            }

            // 如果状态更新为“已归还”，同时更新归还日期为当前日期（格式：YYYY-MM-DD）
            if(newStatus === "已归还"){
              var today = new Date();
              var yyyy = today.getFullYear();
              var mm = ("0" + (today.getMonth() + 1)).slice(-2);
              var dd = ("0" + today.getDate()).slice(-2);
              var currentDate = yyyy + "-" + mm + "-" + dd;
              form.closest("tr").find("td:eq(4)").text(currentDate);
            } else if (newStatus === "借阅中") {
              // 如果状态是“借阅中”，清空归还日期
              form.closest("tr").find("td:eq(4)").text('');
            }
            alert("借阅记录状态已更新");
          },
          error: function(xhr, status, error){
            alert("借阅记录状态更新失败，请重试");
          }
        });
      });
    });
  </script>
</head>
<body>
<div class="container">
  <h1>书籍与借阅记录管理系统</h1>

  <!-- 错误提示 -->
  <c:if test="${not empty errorMessage}">
    <div class="error">${errorMessage}</div>
  </c:if>

  <!-- 书籍列表 -->
  <h2>书籍列表</h2>
  <table>
    <thead>
    <tr>
      <th>书籍ID</th>
      <th>书名</th>
      <th>作者</th>
      <th>状态</th>
      <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="book" items="${bookList}">
      <tr>
        <td>${book.book_id}</td>
        <td>${book.title}</td>
        <td>${book.author}</td>
        <td>${book.status}</td>
        <td>
          <form class="updateBookStatus" action="check_book" method="get">
            <input type="hidden" name="action" value="updateStatus">
            <input type="hidden" name="bookId" value="${book.book_id}">
            <input type="hidden" name="username" value="${username}">
            <select name="status">
              <option value="正常" <c:if test="${book.status == '正常'}">selected</c:if>>正常</option>
              <option value="维修" <c:if test="${book.status == '维修'}">selected</c:if>>维修</option>
              <option value="丢失" <c:if test="${book.status == '丢失'}">selected</c:if>>丢失</option>
            </select>
            <input type="submit" value="更新状态">
          </form>
        </td>
      </tr>
    </c:forEach>
    </tbody>
  </table>

  <!-- 书籍分页 -->
  <div class="pagination">
    <c:if test="${bookTotalPages > 1}">
      <c:forEach begin="1" end="${bookTotalPages}" var="i">
        <a href="check_book?bookPage=${i}&username=${param.username}">${i}</a>
      </c:forEach>
    </c:if>
  </div>

  <hr>

  <!-- 借阅记录列表 -->
  <h2>借阅记录</h2>
  <table>
    <thead>
    <tr>
      <th>客户名</th>
      <th>书名</th>
      <th>借阅日期</th>
      <th>应还日期</th>
      <th>归还日期</th>
      <th>状态</th>
      <th>操作</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="record" items="${borrowRecordList}">
      <tr>
        <td>${record.customer_name}</td>
        <td>${record.book_title}</td>
        <td>${record.borrow_date}</td>
        <td>${record.due_date}</td>
        <td>${record.return_date}</td>
        <td class="status-${record.status == '维修' ? 'repair' : record.status == '丢失' ? 'lost' : record.status == '逾期' ? 'overdue' : ''}">
            ${record.status}
        </td>
        <td>
          <form class="updateRecordStatus" action="check_book" method="get">
            <input type="hidden" name="action" value="updateBorrowRecord">
            <input type="hidden" name="recordId" value="${record.record_id}">
            <input type="hidden" name="username" value="${username}">
            <select name="status">
              <option value="借阅中" <c:if test="${record.status == '借阅中'}">selected</c:if>>借阅中</option>
              <option value="已归还" <c:if test="${record.status == '已归还'}">selected</c:if>>已归还</option>
              <option value="逾期" <c:if test="${record.status == '逾期'}">selected</c:if>>逾期</option>
              <option value="丢失" <c:if test="${record.status == '丢失'}">selected</c:if>>丢失</option>
            </select>
            <input type="submit" value="更新状态">
          </form>
        </td>
      </tr>
    </c:forEach>
    </tbody>
  </table>

  <!-- 借阅记录分页 -->
  <div class="pagination">
    <c:if test="${recordTotalPages > 1}">
      <c:forEach begin="1" end="${recordTotalPages}" var="i">
        <a href="check_book?recordPage=${i}&username=${param.username}">${i}</a>
      </c:forEach>
    </c:if>
  </div>
</div>

<!-- 返回菜单按钮 -->
<div class="container" style="text-align: center; margin-top: 20px;">
  <a href="manager_menu?username=${param.username}" class="btn" style="padding: 10px 20px; background-color: #007BFF; color: #fff; text-decoration: none; border-radius: 4px; transition: background-color 0.3s ease;">返回菜单</a>
</div>

</body>
</html>
