package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class AddMangaToFix {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        String sql = "INSERT INTO manga_info (id, title, author, cover_url, category, status, description, click_count, is_deleted, version, create_time) " +
                     "VALUES (47, '刃牙道', '板垣惠介', 'https://picsum.photos/seed/m47/300/400', '热血', 1, '死囚战士们的巅峰对决，宫本武藏与宫本武藏跨越时空的终极格斗！', 6800, 0, 1, NOW())";
        try {
            st.execute(sql);
            System.out.println("[OK] 刃牙道 已添加");
        } catch (Exception e) {
            System.out.println("[FAIL] " + e.getMessage());
        }

        java.sql.ResultSet rs = st.executeQuery("SELECT COUNT(*) as cnt FROM manga_info WHERE is_deleted = 0");
        if (rs.next()) {
            System.out.println("总漫画数: " + rs.getInt("cnt"));
        }
        rs.close();

        System.out.println("\n--- 各分类数量 ---");
        String[] categories = {"热血", "运动", "恋爱", "冒险", "悬疑"};
        for (String cat : categories) {
            rs = st.executeQuery("SELECT COUNT(*) FROM manga_info WHERE category = '" + cat + "' AND is_deleted = 0");
            if (rs.next()) {
                System.out.println(cat + ": " + rs.getInt(1) + " 本");
            }
            rs.close();
        }

        st.close();
        conn.close();
    }
}