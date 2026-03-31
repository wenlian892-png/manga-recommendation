package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SqlRunner {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";

        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        try {
            st.execute("ALTER TABLE manga_info ADD COLUMN is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0=未删除, 1=已删除'");
            System.out.println("[OK] is_deleted column added to manga_info");
        } catch (Exception e) {
            System.out.println("[SKIP] " + e.getMessage());
        }

        try {
            st.execute("ALTER TABLE manga_info ADD COLUMN author VARCHAR(100) NOT NULL DEFAULT '' COMMENT '作者'");
            System.out.println("[OK] author column added to manga_info");
        } catch (Exception e) {
            System.out.println("[SKIP] " + e.getMessage());
        }

        try {
            st.execute("ALTER TABLE manga_info ADD COLUMN status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1=连载中, 0=已完结'");
            System.out.println("[OK] status column added to manga_info");
        } catch (Exception e) {
            System.out.println("[SKIP] " + e.getMessage());
        }

        st.close();
        conn.close();
        System.out.println("\n=== Done ===");
    }
}
