package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class CheckScores {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        System.out.println("=== 评分表中的漫画ID ===");
        ResultSet rs = st.executeQuery("SELECT DISTINCT manga_id FROM manga_score ORDER BY manga_id");
        while (rs.next()) {
            System.out.print(rs.getInt("manga_id") + " ");
        }
        rs.close();

        System.out.println("\n\n=== manga_info 中的所有ID ===");
        rs = st.executeQuery("SELECT id FROM manga_info WHERE is_deleted = 0 ORDER BY id");
        while (rs.next()) {
            System.out.print(rs.getInt("id") + " ");
        }
        rs.close();

        st.close();
        conn.close();
    }
}