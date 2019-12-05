package com.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBConn {
    public static boolean insert(List<Map<String,String>> list)
    {
        Connection conn =null;
        PreparedStatement st = null;
        String query = "INSERT INTO mobile.mobile02 ("
                + "title,"+"content,"+"url,"+"step,"+"time,"+"writer ) VALUES ("
                + "?, ?, ?, ?, ?, ?)";

        try {
            conn = DBConn.conn();
            st = conn.prepareStatement(query);

            for(Map m : list){
                st.setString(1, m.get("標題").toString());
                st.setString(2, m.get("內文").toString());
                st.setString(3, m.get("網址").toString());
                st.setString(4, m.get("樓").toString());
                st.setString(5, m.get("發文時間").toString());
                st.setString(6, m.get("作者").toString());
                st.execute();
            }

        }
        catch (SQLException se)
        {
            se.printStackTrace();
            // log exception

        }finally
        {
            try {
                st.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;



    }

    public static Connection conn()
    {
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mobile?characterEncoding=UTF-8 & serverTimezone=UTC", "root", "1234");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
        }


        return conn;
    }



    public static void main(String[] args) {

        // TODO Auto-generated method stub

    }



    public static boolean insert2(List<Map<String,String>> list2)
    {
        Connection conn =null;
        PreparedStatement st = null;
        String query = "INSERT INTO mobile.mobile021 ("
                + "title,"+"content,"+"step,"+"writer,"+"time ) VALUES ("
                + "?, ?, ?, ?, ?)";

        try {
            conn = DBConn.conn();
            st = conn.prepareStatement(query);

            for(Map m : list2){
                st.setString(1, m.get("標題").toString());
                st.setString(2, m.get("內容").toString());
                st.setString(3, m.get("樓層").toString());
                st.setString(4, m.get("名字").toString());
                st.setString(5, m.get("時間").toString());
                st.execute();
            }

        }
        catch (SQLException se)
        {
            se.printStackTrace();
            // log exception

        }finally
        {
            try {
                st.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;



    }
}
