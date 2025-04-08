package com.library.servlet;

import com.LibraryDB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONArray;


@WebServlet("/seat_manager")
public class SeatReservationManagerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取预约记录
        List<Map<String, Object>> reservations = new ArrayList<>();
        try (Connection conn = LibraryDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT r.reservation_id, s.seat_number, r.customer_user, r.start_time, r.end_time " +
                             "FROM reservations r JOIN seats s ON r.seat_id = s.seat_id");
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> reservation = new HashMap<>();
                reservation.put("reservationId", rs.getInt("reservation_id"));
                reservation.put("seatNumber", rs.getInt("seat_number"));
                reservation.put("customerUser", rs.getString("customer_user"));
                reservation.put("startTime", rs.getString("start_time"));
                reservation.put("endTime", rs.getString("end_time"));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 将数据传递到 JSP 页面
        request.setAttribute("reservations", reservations);
        request.getRequestDispatcher("/seat_reservation_manager.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection conn = LibraryDB.getConnection()) {
            JSONObject jsonResponse = new JSONObject();

            if ("cancelReservation".equals(action)) {
                int reservationId = Integer.parseInt(request.getParameter("reservation_id"));
                String getSeatIdSql = "SELECT seat_id FROM reservations WHERE reservation_id = ?";
                PreparedStatement getSeatStmt = conn.prepareStatement(getSeatIdSql);
                getSeatStmt.setInt(1, reservationId);
                ResultSet rs = getSeatStmt.executeQuery();

                if (rs.next()) {
                    int seatId = rs.getInt("seat_id");
                    String deleteReservationSql = "DELETE FROM reservations WHERE reservation_id = ?";
                    PreparedStatement deleteStmt = conn.prepareStatement(deleteReservationSql);
                    deleteStmt.setInt(1, reservationId);
                    deleteStmt.executeUpdate();

                    String updateSeatSql = "UPDATE seats SET status = '可用' WHERE seat_id = ?";
                    PreparedStatement updateSeatStmt = conn.prepareStatement(updateSeatSql);
                    updateSeatStmt.setInt(1, seatId);
                    updateSeatStmt.executeUpdate();

                    jsonResponse.put("success", "预约取消成功！");
                } else {
                    jsonResponse.put("error", "无法找到该预约记录！");
                }
            }
            System.out.print(jsonResponse.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "服务器内部错误");
           System.out.print(errorResponse.toString());
        }
    }
}
