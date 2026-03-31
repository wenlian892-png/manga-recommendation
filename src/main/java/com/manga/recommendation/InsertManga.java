package com.manga.recommendation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InsertManga {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbUrl = "jdbc:mysql://localhost:3306/manga_rec_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        Connection conn = DriverManager.getConnection(dbUrl, "root", "123456");
        Statement st = conn.createStatement();

        String[][] mangaData = {
            {"热血", "6", "拳愿欧米茄", "里崎哲", "https://picsum.photos/seed/m6/300/400", "7500", "吴氏制药的机械狂人吴麟，从街头格斗到企业赌金格斗场的热血逆袭之路！"},
            {"热血", "7", "刃牙道", "板垣惠介", "https://picsum.photos/seed/m7/300/400", "6800", "死囚战士们的巅峰对决，宫本武藏与宫本武藏跨越时空的终极格斗！"},
            {"热血", "8", "进击的巨人", "谏山创", "https://picsum.photos/seed/m8/300/400", "9200", "人类与巨人的生死之战，为了自由而战的悲壮史诗！"},
            {"热血", "9", "黑瞳高中生", "星野之彦", "https://picsum.photos/seed/m9/300/400", "5600", "拥有神秘黑瞳的高校教师，揭开都市传说背后的真相！"},

            {"运动", "10", "排球少年", "古馆春一", "https://picsum.photos/seed/m10/300/400", "8400", "身材矮小的日向翔阳，如何在排球场上完成不可能的跳跃！"},
            {"运动", "11", "第一神拳", "森川让次", "https://picsum.photos/seed/m11/300/400", "7800", "少年幕之内步的拳击成长之路，从被霸凌者到拳王！"},
            {"运动", "12", "足球小将翼", "高桥阳一", "https://picsum.photos/seed/m12/300/400", "8600", "大空翼的足球梦想，从日本到世界舞台的热血征程！"},
            {"运动", "13", "灌篮高手", "井上雄彦", "https://picsum.photos/seed/m13/300/400", "9100", "湘北高中篮球队的全国大赛之旅，青春热血篮球梦！"},

            {"恋爱", "14", "四月是你的谎言", "新川直司", "https://picsum.photos/seed/m14/300/400", "7200", "钢琴天才少年与小提琴少女的唯美爱情，一段关于告别与成长的故事。"},
            {"恋爱", "15", "好想告诉你", "椎名轻穗", "https://picsum.photos/seed/m15/300/400", "6800", "被称为贞子的少女与开朗男生的治愈系恋爱物语。"},
            {"恋爱", "16", "NANA", "矢泽爱", "https://picsum.photos/seed/m16/300/400", "7900", "两个同名NANA的相遇，摇滚乐队BLACK STONES的梦想与爱情！"},
            {"恋爱", "17", "水果篮子", "高屋奈月", "https://picsum.photos/seed/m17/300/400", "6500", "开朗少女与十二生肖草帽族成员的奇幻恋爱喜剧！"},

            {"冒险", "18", "ONE PIECE", "尾田荣一郎", "https://picsum.photos/seed/m18/300/400", "9800", "路飞率领的草帽海贼团，寻找传说中的海贼王宝藏的冒险之旅！"},
            {"冒险", "19", "妖精的尾巴", "真岛浩", "https://picsum.photos/seed/m19/300/400", "8500", "魔导士公会妖精的尾巴成员们的冒险与羁绊！"},
            {"冒险", "20", "全职猎人", "富坚义博", "https://picsum.photos/seed/m20/300/400", "8800", "小杰寻找父亲成为猎人，挑战贪婪之岛的惊险冒险！"},
            {"冒险", "21", "文豪野犬", "朝雾卡夫卡", "https://picsum.photos/seed/m21/300/400", "7600", "拥有超能力的文豪们组成的侦探社，揭开不可思议之人的秘密！"},

            {"悬疑", "22", "金田一少年事件簿", "佐藤文也", "https://picsum.photos/seed/m22/300/400", "8100", "名侦探金田一耕助之孙，破解无数诡异杀人案的推理传奇！"},
            {"悬疑", "23", "死亡笔记", "大场鸫", "https://picsum.photos/seed/m23/300/400", "9000", "高中生捡到死亡笔记，用它制裁罪犯的天才头脑对决！"},
            {"悬疑", "24", "傀儡师", "乱步", "https://picsum.photos/seed/m24/300/400", "6200", "江户川乱步奖获奖作品，揭开人心深处的黑暗与疯狂！"},
            {"悬疑", "25", " Another", "绫野绫", "https://picsum.photos/seed/m25/300/400", "5900", "转入夜见北中学的三年三班，被死亡的诅咒笼罩的恐怖悬疑！"}
        };

        int count = 0;
        for (String[] m : mangaData) {
            String category = m[0];
            String id = m[1];
            String title = m[2];
            String author = m[3];
            String coverUrl = m[4];
            String clickCount = m[5];
            String description = m[6];

            String sql = String.format(
                "INSERT INTO manga_info (id, title, author, cover_url, category, status, description, click_count, is_deleted, version, create_time) " +
                "VALUES (%s, '%s', '%s', '%s', '%s', 1, '%s', %s, 0, 1, NOW())",
                id, title, author, coverUrl, category, description, clickCount
            );
            try {
                st.execute(sql);
                count++;
                System.out.println("[OK] " + title);
            } catch (Exception e) {
                System.out.println("[FAIL] " + title + ": " + e.getMessage());
            }
        }

        System.out.println("\n共插入 " + count + " 部漫画");
        st.close();
        conn.close();
    }
}