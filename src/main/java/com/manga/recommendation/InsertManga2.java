package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class InsertManga2 {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        ResultSet rs = st.executeQuery("SELECT MAX(id) FROM manga_info");
        int maxId = 0;
        if (rs.next()) {
            maxId = rs.getInt(1);
        }
        rs.close();
        System.out.println("当前最大ID: " + maxId);

        String[][] mangaData = {
            {"热血", "刃牙道", "板垣惠介", "https://picsum.photos/seed/m26/300/400", "6800", "死囚战士们的巅峰对决，宫本武藏与宫本武藏跨越时空的终极格斗！"},
            {"热血", "进击的巨人", "谏山创", "https://picsum.photos/seed/m27/300/400", "9200", "人类与巨人的生死之战，为了自由而战的悲壮史诗！"},
            {"热血", "黑色五叶草", "田代裕彦", "https://picsum.photos/seed/m28/300/400", "7500", "没有魔力的少年亚斯塔，誓要成为魔法帝的逆袭之路！"},
            {"热血", "不死之王", "丸山くがね", "https://picsum.photos/seed/m29/300/400", "8200", "骷髅魔法师安兹乌尔恭，在异世界建立最强王国的传奇！"},

            {"运动", "排球少年", "古馆春一", "https://picsum.photos/seed/m30/300/400", "8400", "身材矮小的日向翔阳，如何在排球场上完成不可能的跳跃！"},
            {"运动", "第一神拳", "森川让次", "https://picsum.photos/seed/m31/300/400", "7800", "少年幕之内步的拳击成长之路，从被霸凌者到拳王！"},
            {"运动", "足球小将翼", "高桥阳一", "https://picsum.photos/seed/m32/300/400", "8600", "大空翼的足球梦想，从日本到世界舞台的热血征程！"},
            {"运动", "飙速宅男", "渡边航", "https://picsum.photos/seed/m33/300/400", "7100", "御宅族少年小野田坂道，骑上自行车挑战高中联赛的青春故事！"},

            {"恋爱", "四月是你的谎言", "新川直司", "https://picsum.photos/seed/m34/300/400", "7200", "钢琴天才少年与小提琴少女的唯美爱情，一段关于告别与成长的故事。"},
            {"恋爱", "好想告诉你", "椎名轻穗", "https://picsum.photos/seed/m35/300/400", "6800", "被称为贞子的少女与开朗男生的治愈系恋爱物语。"},
            {"恋爱", "赤发白雪姬", "白岩渺", "https://picsum.photos/seed/m36/300/400", "6900", "拥有罕见红发的白雪公主，为追求自由爱情逃出皇宫的冒险！"},
            {"恋爱", "青春之旅", "咲坂伊织", "https://picsum.photos/seed/m37/300/400", "6400", "高中生重盛朋的青春恋爱故事，友情与爱情的艰难抉择！"},

            {"冒险", "妖精的尾巴", "真岛浩", "https://picsum.photos/seed/m38/300/400", "8500", "魔导士公会妖精的尾巴成员们的冒险与羁绊！"},
            {"冒险", "全职猎人", "富坚义博", "https://picsum.photos/seed/m39/300/400", "8800", "小杰寻找父亲成为猎人，挑战贪婪之岛的惊险冒险！"},
            {"冒险", "文豪野犬", "朝雾卡夫卡", "https://picsum.photos/seed/m40/300/400", "7600", "拥有超能力的文豪们组成的侦探社，揭开不可思议之人的秘密！"},
            {"冒险", "失错之空", "筱原健太", "https://picsum.photos/seed/m41/300/400", "7300", "在天空之城的遗迹中探险，寻找世界真相的奇幻冒险！"},

            {"悬疑", "死亡笔记", "大场鸫", "https://picsum.photos/seed/m42/300/400", "9000", "高中生捡到死亡笔记，用它制裁罪犯的天才头脑对决！"},
            {"悬疑", "Another", "绫野绫", "https://picsum.photos/seed/m43/300/400", "5900", "转入夜见北中学的三年三班，被死亡的诅咒笼罩的恐怖悬疑！"},
            {"悬疑", "金田一少年事件簿", "佐藤文也", "https://picsum.photos/seed/m44/300/400", "8100", "名侦探金田一耕助之孙，破解无数诡异杀人案的推理传奇！"},
            {"悬疑", "尸鬼", "小野不由美", "https://picsum.photos/seed/m45/300/400", "6800", "寂静乡村的神秘死亡事件，村民与尸鬼的惊悚对决！"}
        };

        int count = 0;
        int nextId = maxId + 1;
        for (String[] m : mangaData) {
            String category = m[0];
            String title = m[1];
            String author = m[2];
            String coverUrl = m[3];
            String clickCount = m[4];
            String description = m[5];

            String sql = String.format(
                "INSERT INTO manga_info (id, title, author, cover_url, category, status, description, click_count, is_deleted, version, create_time) " +
                "VALUES (%d, '%s', '%s', '%s', '%s', 1, '%s', %s, 0, 1, NOW())",
                nextId, title, author, coverUrl, category, description, clickCount
            );
            try {
                st.execute(sql);
                count++;
                System.out.println("[OK] ID:" + nextId + " " + title);
            } catch (Exception e) {
                System.out.println("[FAIL] " + title + ": " + e.getMessage());
            }
            nextId++;
        }

        System.out.println("\n共插入 " + count + " 部漫画");
        st.close();
        conn.close();
    }
}