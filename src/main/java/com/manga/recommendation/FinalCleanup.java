package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class FinalCleanup {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        int[] keepIds = {1, 2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};

        ResultSet rs = st.executeQuery("SELECT id FROM manga_info WHERE is_deleted = 0");
        java.util.List<Integer> toDelete = new java.util.ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            boolean shouldKeep = false;
            for (int keepId : keepIds) {
                if (id == keepId) {
                    shouldKeep = true;
                    break;
                }
            }
            if (!shouldKeep) {
                toDelete.add(id);
            }
        }
        rs.close();

        for (int id : toDelete) {
            try {
                st.execute("DELETE FROM manga_info WHERE id = " + id);
                System.out.println("[DEL] ID: " + id);
            } catch (Exception e) {
                System.out.println("[FAIL] ID: " + id + ": " + e.getMessage());
            }
        }

        System.out.println("\n=== 清理完成 ===");
        System.out.println("\n--- 各分类数量 ---");
        String[] categories = {"热血", "运动", "恋爱", "冒险", "悬疑"};
        for (String cat : categories) {
            rs = st.executeQuery("SELECT COUNT(*) FROM manga_info WHERE category = '" + cat + "' AND is_deleted = 0");
            if (rs.next()) {
                System.out.println(cat + ": " + rs.getInt(1) + " 本");
            }
            rs.close();
        }

        System.out.println("\n--- 全部漫画 ---");
        rs = st.executeQuery("SELECT id, title, category FROM manga_info WHERE is_deleted = 0 ORDER BY category, id");
        while (rs.next()) {
            System.out.println(rs.getInt("id") + ". " + rs.getString("title") + " [" + rs.getString("category") + "]");
        }
        rs.close();

        st.close();
        conn.close();
    }
}