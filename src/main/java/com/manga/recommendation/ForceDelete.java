package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ForceDelete {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        System.out.println("=== 检查 ID 7 的状态 ===");
        java.sql.ResultSet rs = st.executeQuery("SELECT id, title, is_deleted FROM manga_info WHERE id IN (6, 7, 47)");
        while (rs.next()) {
            System.out.println("ID:" + rs.getInt("id") + " is_deleted:" + rs.getInt("is_deleted") + " title:" + rs.getString("title"));
        }
        rs.close();

        System.out.println("\n=== 强制删除 ID 7 ===");
        st.execute("DELETE FROM manga_info WHERE id = 7");
        System.out.println("已执行 DELETE FROM manga_info WHERE id = 7");

        System.out.println("\n=== 验证结果 ===");
        rs = st.executeQuery("SELECT COUNT(*) as total FROM manga_info");
        if (rs.next()) {
            System.out.println("总记录数: " + rs.getInt("total"));
        }
        rs.close();

        rs = st.executeQuery("SELECT COUNT(*) as cnt FROM manga_info WHERE is_deleted = 0");
        if (rs.next()) {
            System.out.println("未删除记录数: " + rs.getInt("cnt"));
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