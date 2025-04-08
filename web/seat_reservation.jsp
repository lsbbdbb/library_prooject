<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*, java.util.*" %>
<%@ page import="com.LibraryDB" %>
<%@ page import="org.json.JSONArray, org.json.JSONObject" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>座位预约系统</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        /* 整体页面样式 */
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f0f2f5;
            margin: 0;
            padding: 0;
        }
        /* 页面容器 */
        .container {
            width: 90%;
            max-width: 1000px;
            margin: 20px auto;
            background: #fff;
            padding: 25px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            border-radius: 8px;
        }
        /* 标题样式 */
        h2, h3 {
            text-align: center;
            color: #333;
            margin-top: 0;
            margin-bottom: 20px;
        }
        /* 表格样式 */
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 25px;
        }
        th, td {
            padding: 12px 15px;
            text-align: center;
            border: 1px solid #ddd;
        }
        th {
            background-color: #007BFF;
            color: #fff;
        }
        .available { background-color: #c8e6c9; }  /* 可用座位（绿色） */
        .reserved { background-color: #ffccbc; }   /* 已预约（橙色） */
        .unavailable { background-color: #e0e0e0; }  /* 不可用（灰色） */
        /* 按钮样式 */
        button {
            background-color: #007BFF;
            color: #fff;
            border: none;
            padding: 8px 16px;
            cursor: pointer;
            border-radius: 4px;
            font-size: 14px;
        }
        button:hover {
            background-color: #0056b3;
        }
        /* 表单容器 */
        .form-container {
            background: #f9f9f9;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 30px;
            text-align: center;
        }
        .form-container input {
            padding: 10px;
            margin: 10px 0;
            width: 100%;
            max-width: 300px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
        }
        .form-container button {
            width: 100%;
            max-width: 150px;
            margin: 10px 5px;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>座位预约系统</h2>

    <!-- 显示座位状态 -->
    <h3>座位状态</h3>
    <table>
        <tr>
            <th>座位号</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        <%
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            try {
                conn = LibraryDB.getConnection();
                String sql = "SELECT * FROM seats";
                stmt = conn.prepareStatement(sql);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    int seatId = rs.getInt("seat_id");
                    int seatNumber = rs.getInt("seat_number");
                    String status = rs.getString("status");
                    String className = status.equals("可用") ? "available" : (status.equals("已预约") ? "reserved" : "unavailable");
        %>
        <tr class="<%= className %>">
            <td><%= seatNumber %></td>
            <td><%= status %></td>
            <td>
                <% if ("可用".equals(status)) { %>
                <button onclick="showReserveForm(<%= seatId %>)">预约</button>
                <% } else { %>
                <span>不可预约</span>
                <% } %>
            </td>
        </tr>
        <%
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        %>
    </table>

    <!-- 预约表单 -->
    <div id="reservationForm" class="form-container" style="display: none;">
        <h3>预约座位</h3>
        <form id="reserveSeatForm">
            <input type="hidden" id="seatId" name="seat_id">
            用户邮箱: <input type="email" name="customer_user" required><br>
            开始时间: <input type="datetime-local" name="start_time" required><br>
            结束时间: <input type="datetime-local" name="end_time" required><br>
            <button type="submit">提交预约</button>
            <button type="button" onclick="hideReserveForm()">取消</button>
        </form>
    </div>

    <!-- 显示用户预约记录 -->
    <h3>我的预约</h3>
    <table>
        <tr>
            <th>预约ID</th>
            <th>座位号</th>
            <th>开始时间</th>
            <th>结束时间</th>
            <th>操作</th>
        </tr>
        <%
            String username = request.getParameter("username");  // 获取 URL 参数中的 username
            String customerUser = null;

            try {
                // 根据 username 查询 customer_user（邮箱）
                conn = LibraryDB.getConnection();
                String customerSql = "SELECT customer_user FROM Customer WHERE customer_name = ?";
                stmt = conn.prepareStatement(customerSql);
                stmt.setString(1, username);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    customerUser = rs.getString("customer_user");
                }

                // 查询当前用户的预约记录
                if (customerUser != null) {
                    String reservationSql = "SELECT r.reservation_id, s.seat_number, r.start_time, r.end_time " +
                            "FROM reservations r " +
                            "JOIN seats s ON r.seat_id = s.seat_id " +
                            "WHERE r.customer_user = ?";
                    stmt = conn.prepareStatement(reservationSql);
                    stmt.setString(1, customerUser);  // 绑定查询条件
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        int reservationId = rs.getInt("reservation_id");
                        int seatNumber = rs.getInt("seat_number");
                        String startTime = rs.getString("start_time");
                        String endTime = rs.getString("end_time");
        %>
        <tr>
            <td><%= reservationId %></td>
            <td><%= seatNumber %></td>
            <td><%= startTime %></td>
            <td><%= endTime %></td>
            <td>
                <button class="cancelReservation" data-id="<%= reservationId %>">取消预约</button>
            </td>
        </tr>
        <%
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (rs != null) rs.close();
                    if (stmt != null) stmt.close();
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        %>
    </table>
    <a href="customer_menu?username=${username}" class="btn btn-secondary">返回菜单</a>
</div>

<script>
    function showReserveForm(seatId) {
        $("#seatId").val(seatId);
        $("#reservationForm").show();
    }
    function hideReserveForm() {
        $("#reservationForm").hide();
    }
    // 预约座位请求
    $("#reserveSeatForm").submit(function(e) {
        e.preventDefault();
        $.post("seat_reservation", $(this).serialize() + "&action=reserveSeat", function(response) {
            alert(response.success || response.error);
            location.reload();
        }, "json");
    });
    // 取消预约请求
    $(".cancelReservation").click(function() {
        var reservationId = $(this).data("id");
        $.post("seat_reservation", { reservation_id: reservationId, action: "cancelReservation" }, function(response) {
            alert(response.success || response.error);
            location.reload();
        }, "json");
    });
</script>

</body>
</html>

