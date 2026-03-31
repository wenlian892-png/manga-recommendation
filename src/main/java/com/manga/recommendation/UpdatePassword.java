package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class UpdatePassword {

    public static void main(String[] args) throws Exception {
        String password = "123456";
        String salt = "manga_salt";
        String hash = md5(password + salt);

        System.out.println("密码: " + password);
        System.out.println("哈希值: " + hash);

        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";

        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        String sql = "UPDATE user_info SET password = '" + hash + "'";
        int rows = st.executeUpdate(sql);
        System.out.println("更新了 " + rows + " 条记录");

        st.close();
        conn.close();
        System.out.println("密码已全部更新为: " + password);
    }

    private static String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}