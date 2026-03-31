package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class CheckRecommendResult {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM recommend_result");
        if (rs.next()) {
            System.out.println("recommend_result 表记录数: " + rs.getInt(1));
        }
        rs.close();

        rs = st.executeQuery("SELECT COUNT(*) FROM manga_score");
        if (rs.next()) {
            System.out.println("manga_score 表记录数: " + rs.getInt(1));
        }
        rs.close();

        st.close();
        conn.close();
    }
}