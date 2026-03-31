package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DeleteManga7 {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        int rows = st.executeUpdate("UPDATE manga_info SET is_deleted = 1 WHERE id = 7");
        System.out.println("更新了 " + rows + " 条记录 (is_deleted=1)");

        java.sql.ResultSet rs = st.executeQuery("SELECT id, title, category FROM manga_info WHERE is_deleted = 0 ORDER BY id");
        System.out.println("\n--- 当前漫画列表 ---");
        while (rs.next()) {
            System.out.println(rs.getInt("id") + ". " + rs.getString("title") + " [" + rs.getString("category") + "]");
        }
        rs.close();

        st.close();
        conn.close();
    }
}