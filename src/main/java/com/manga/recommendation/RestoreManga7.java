package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class RestoreManga7 {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        String sql = "INSERT INTO manga_info (id, title, author, cover_url, category, status, description, click_count, is_deleted, version, create_time) " +
                     "VALUES (7, '鎷虫効娆х背鑼?', '阅板磶澶?', 'https://picsum.photos/seed/m7/300/400', '鐑?', 1, '灏忔湁灏忕殑鍖呮捣锛屽湪鍦颁笅鏍?鏂楀満鎶?链间富瀵间负姝?浠栫殑鑴т寒锛岃?鍙栦腑鑴т汉鍙风О涓烘捣鑴т汉锛?', 7500, 0, 1, NOW())";
        try {
            st.execute(sql);
            System.out.println("[OK] ID 7 宸叉坊");
        } catch (Exception e) {
            System.out.println("[FAIL] " + e.getMessage());
        }

        java.sql.ResultSet rs = st.executeQuery("SELECT id, title FROM manga_info WHERE id = 7");
        if (rs.next()) {
            System.out.println("ID 7: " + rs.getString("title"));
        }
        rs.close();

        st.close();
        conn.close();
    }
}