package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class CheckAllTables {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        System.out.println("=== manga_rec_db 所有表 ===");
        ResultSet rs = st.executeQuery("SHOW TABLES");
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
        rs.close();

        st.close();
        conn.close();
    }
}