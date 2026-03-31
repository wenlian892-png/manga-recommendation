package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class CheckUserCollection {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        System.out.println("=== user_collection 表结构 ===");
        ResultSet rs = st.executeQuery("DESC user_collection");
        while (rs.next()) {
            System.out.println(rs.getString("Field") + " | " + rs.getString("Type"));
        }
        rs.close();

        System.out.println("\n=== user_collection 数据示例 ===");
        rs = st.executeQuery("SELECT * FROM user_collection LIMIT 3");
        while (rs.next()) {
            System.out.println("id:" + rs.getInt("id") + " user_id:" + rs.getInt("user_id") + " manga_id:" + rs.getInt("manga_id"));
        }
        rs.close();

        st.close();
        conn.close();
    }
}