package com.library.servlet;

import com.LibraryDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import org.json.JSONObject;

@WebServlet("/seat_reservation")
public class SeatReservationServlet extends HttpServlet {

    // 处理 GET 请求，用于显示页面或返回必要的信息
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 如果需要，进行相关处理，比如查询当前用户的座位预约信息
        String username = request.getParameter("username");

        // 这里我们可以向页面发送当前用户的座位预约信息
        // 如果是展示页面，则转发到 JSP 页面
        request.setAttribute("username", username);
        request.getRequestDispatcher("/seat_reservation.jsp").forward(request, response);
    }

    // 处理 POST 请求，用于执行座位预约和取消预约操作
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取请求参数
        String action = request.getParameter("action");

        // 设置响应内容类型为 JSON
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 获取数据库连接
            Connection conn = LibraryDB.getConnection();
            JSONObject jsonResponse = new JSONObject();

            // 处理预约操作
            if ("reserveSeat".equals(action)) {
                int seatId = Integer.parseInt(request.getParameter("seat_id"));
                String username = request.getParameter("customer_user");
                String startTime = request.getParameter("start_time");
                String endTime = request.getParameter("end_time");

                // 时间格式检查（结束时间不能早于或等于开始时间）
                if (endTime.compareTo(startTime) <= 0) {
                    jsonResponse.put("error", "结束时间不能早于或等于开始时间！");
                    out.print(jsonResponse.toString());
                    return;
                }

                // 检查用户是否存在
                String getCustomerUserSql = "SELECT customer_user FROM customer WHERE customer_user = ?";
                PreparedStatement checkCustomerStmt = conn.prepareStatement(getCustomerUserSql);
                checkCustomerStmt.setString(1, username);
                ResultSet customerRs = checkCustomerStmt.executeQuery();

                if (!customerRs.next()) {
                    jsonResponse.put("error", "用户不存在！");
                    out.print(jsonResponse.toString());
                    return;
                }

                String customerUser = customerRs.getString("customer_user");

                // 检查该用户是否已有预约记录
                String checkExistingReservationSql = "SELECT COUNT(*) AS count FROM reservations WHERE customer_user = ?";
                PreparedStatement checkExistingStmt = conn.prepareStatement(checkExistingReservationSql);
                checkExistingStmt.setString(1, customerUser);
                ResultSet existingRs = checkExistingStmt.executeQuery();
                if (existingRs.next() && existingRs.getInt("count") > 0) {
                    jsonResponse.put("error", "您已有预约记录，请先取消后再预约！");
                    out.print(jsonResponse.toString());
                    return;
                }

                // 检查座位是否可用
                String checkAvailabilitySql = "SELECT status FROM seats WHERE seat_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkAvailabilitySql);
                checkStmt.setInt(1, seatId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && "可用".equals(rs.getString("status"))) {
                    // 更新座位状态
                    String updateSeatSql = "UPDATE seats SET status = '已预约' WHERE seat_id = ?";
                    PreparedStatement updateSeatStmt = conn.prepareStatement(updateSeatSql);
                    updateSeatStmt.setInt(1, seatId);
                    updateSeatStmt.executeUpdate();

                    // 插入预约记录
                    String insertReservationSql = "INSERT INTO reservations (seat_id, customer_user, start_time, end_time) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertReservationSql);
                    insertStmt.setInt(1, seatId);
                    insertStmt.setString(2, customerUser);
                    insertStmt.setString(3, startTime);
                    insertStmt.setString(4, endTime);
                    insertStmt.executeUpdate();

                    jsonResponse.put("success", "座位预约成功！");
                } else {
                    jsonResponse.put("error", "该座位不可预约！");
                }
            }

            // 处理取消预约操作
            else if ("cancelReservation".equals(action)) {
                int reservationId = Integer.parseInt(request.getParameter("reservation_id"));

                // 获取座位ID
                String getSeatIdSql = "SELECT seat_id FROM reservations WHERE reservation_id = ?";
                PreparedStatement getSeatStmt = conn.prepareStatement(getSeatIdSql);
                getSeatStmt.setInt(1, reservationId);
                ResultSet rs = getSeatStmt.executeQuery();
                if (rs.next()) {
                    int seatId = rs.getInt("seat_id");

                    // 删除预约记录
                    String deleteReservationSql = "DELETE FROM reservations WHERE reservation_id = ?";
                    PreparedStatement deleteStmt = conn.prepareStatement(deleteReservationSql);
                    deleteStmt.setInt(1, reservationId);
                    deleteStmt.executeUpdate();

                    // 更新座位状态为可用
                    String updateSeatSql = "UPDATE seats SET status = '可用' WHERE seat_id = ?";
                    PreparedStatement updateSeatStmt = conn.prepareStatement(updateSeatSql);
                    updateSeatStmt.setInt(1, seatId);
                    updateSeatStmt.executeUpdate();

                    jsonResponse.put("success", "预约取消成功！");
                } else {
                    jsonResponse.put("error", "无法找到该预约记录！");
                }
            }

            // 输出 JSON 响应
            out.print(jsonResponse.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "服务器内部错误");
            out.print(errorResponse.toString());
        }
    }
}
