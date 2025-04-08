package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LibraryDB {
    // 数据库连接参数
    private static final String URL = "jdbc:mysql://localhost:3306/library_test";
    private static final String USER = "root";
    private static final String PASSWORD = "Zlh.0507150018";

    public static Connection getConnection() throws SQLException {

        try {
            // 显式加载 MySQL JDBC 驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 尝试获取连接
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            // 如果找不到驱动，打印错误
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver not found", e);
        }

    }
}

//    // 添加 main 方法用于测试数据库连接
//    public static void main(String[] args) {
//        try (Connection conn = getConnection()) {
//            if (conn != null) {
//                System.out.println("数据库连接成功！");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            System.out.println("数据库连接失败！");
//        }
//    }

