package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class CheckUserTables {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        System.out.println("=== user_info 表结构 ===");
        ResultSet rs = st.executeQuery("DESC user_info");
        while (rs.next()) {
            System.out.println(rs.getString("Field") + " | " + rs.getString("Type"));
        }
        rs.close();

        System.out.println("\n=== manga_score 表结构 ===");
        rs = st.executeQuery("DESC manga_score");
        while (rs.next()) {
            System.out.println(rs.getString("Field") + " | " + rs.getString("Type"));
        }
        rs.close();

        System.out.println("\n=== collect 表结构 ===");
        rs = st.executeQuery("DESC collect");
        while (rs.next()) {
            System.out.println(rs.getString("Field") + " | " + rs.getString("Type"));
        }
        rs.close();

        System.out.println("\n=== read_history 表结构 ===");
        rs = st.executeQuery("DESC read_history");
        while (rs.next()) {
            System.out.println(rs.getString("Field") + " | " + rs.getString("Type"));
        }
        rs.close();

        st.close();
        conn.close();
    }
}