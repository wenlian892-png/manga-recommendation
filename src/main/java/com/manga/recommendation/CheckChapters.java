package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class CheckChapters {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM manga_chapter WHERE is_deleted = 0");
        if (rs.next()) {
            System.out.println("manga_chapter 记录数: " + rs.getInt(1));
        }
        rs.close();

        rs = st.executeQuery("SELECT manga_id, COUNT(*) as cnt FROM manga_chapter WHERE is_deleted = 0 GROUP BY manga_id LIMIT 5");
        while (rs.next()) {
            System.out.println("manga_id=" + rs.getInt("manga_id") + " 有 " + rs.getInt("cnt") + " 章");
        }
        rs.close();

        st.close();
        conn.close();
    }
}