package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class AddManga {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        String sql = "INSERT INTO manga_info (id, title, author, cover_url, category, status, description, click_count, is_deleted, version, create_time) " +
                     "VALUES (26, '不死之王', '丸山くがね', 'https://picsum.photos/seed/m29/300/400', '热血', 1, '骷髅魔法师安兹乌尔恭，在异世界建立最强王国的传奇！', 8200, 0, 1, NOW())";
        try {
            st.execute(sql);
            System.out.println("[OK] 不死之王");
        } catch (Exception e) {
            System.out.println("[FAIL] " + e.getMessage());
        }

        java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM manga_info WHERE category = '热血' AND is_deleted = 0");
        if (rs.next()) {
            System.out.println("热血分类现在有: " + rs.getInt(1) + " 本");
        }
        rs.close();

        st.close();
        conn.close();
    }
}