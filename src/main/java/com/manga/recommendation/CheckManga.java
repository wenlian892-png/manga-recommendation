package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class CheckManga {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        String[] categories = {"热血", "运动", "恋爱", "冒险", "悬疑"};
        for (String cat : categories) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM manga_info WHERE category = '" + cat + "' AND is_deleted = 0");
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println(cat + ": " + count + " 本");
            }
            rs.close();
        }

        System.out.println("\n--- 全部漫画 ---");
        ResultSet rs = st.executeQuery("SELECT id, title, category FROM manga_info WHERE is_deleted = 0 ORDER BY id");
        while (rs.next()) {
            System.out.println(rs.getInt("id") + ". " + rs.getString("title") + " [" + rs.getString("category") + "]");
        }
        rs.close();

        st.close();
        conn.close();
    }
}