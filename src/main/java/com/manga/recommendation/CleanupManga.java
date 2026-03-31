package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CleanupManga {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        int[] duplicateIds = {26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45};
        for (int id : duplicateIds) {
            try {
                st.execute("DELETE FROM manga_info WHERE id = " + id);
                System.out.println("[DEL] ID: " + id);
            } catch (Exception e) {
                System.out.println("[SKIP] ID: " + id + " - " + e.getMessage());
            }
        }

        System.out.println("\n--- 清理后漫画列表 ---");
        java.sql.ResultSet rs = st.executeQuery("SELECT id, title, category FROM manga_info WHERE is_deleted = 0 ORDER BY id");
        while (rs.next()) {
            System.out.println(rs.getInt("id") + ". " + rs.getString("title") + " [" + rs.getString("category") + "]");
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