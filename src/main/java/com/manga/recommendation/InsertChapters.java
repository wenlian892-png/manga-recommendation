package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InsertChapters {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        int mangaIds[] = {1, 2, 3, 4, 5, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 46, 47};
        String[] chapterNames = {"第一话", "第二话", "第三话", "第四话", "第五话"};

        int count = 0;
        for (int mangaId : mangaIds) {
            for (int i = 0; i < 5; i++) {
                String contentUrl = "https://picsum.photos/seed/m" + mangaId + "_ch" + (i+1) + "/800/1200";
                String sql = String.format(
                    "INSERT INTO manga_chapter (manga_id, title, sort_order, content_urls, create_time) " +
                    "VALUES (%d, '%s', %d, '%s', NOW())",
                    mangaId, chapterNames[i], i + 1, contentUrl
                );
                try {
                    st.execute(sql);
                    count++;
                } catch (Exception e) {
                    System.out.println("[FAIL] mangaId=" + mangaId + " chapter=" + (i+1) + ": " + e.getMessage());
                }
            }
            System.out.println("[OK] mangaId=" + mangaId + " added 5 chapters");
        }

        System.out.println("\n共插入 " + count + " 个章节");

        java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM manga_chapter");
        if (rs.next()) {
            System.out.println("manga_chapter 表总记录数: " + rs.getInt(1));
        }
        rs.close();

        st.close();
        conn.close();
    }
}