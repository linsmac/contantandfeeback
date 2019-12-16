package com.main.catcher;

import com.main.MainClient;
import entities.MobilecontentEntity;
import entities.MobilephoneEntity;
import entities.MobilereplyEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import sun.applet.Main;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Getcontent {

    public static String getSource(String url) {
        //取出網頁全部內容
        String content = new String();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        //以get請求

        CloseableHttpResponse response = null;                                                                  //建ht r存著 允穫數據


        try {

            response = httpclient.execute(httpget);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {


                content = EntityUtils.toString(response.getEntity(), "utf-8");
                //EntityUtils表示http響應的內容//EntityUtils.toString將內容解釋為String並返回

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return content;
    }

/////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////<<<列表頁>>>////////////////////////////////////////////////////

    public static List<Map<String, String>> sortTitleTolist(String content) throws InterruptedException {

        /*Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();*/
        /*Session session = sessionFactory.openSession();
        Transaction ts = session.beginTransaction();*/

        String regex = "<div class=\"c-listTableTd__title\">[\\s\\S]*?<a href=\"(.*?)\" class=\"c-link u-ellipsis\">([\\s\\S]*?)</a>"
                + "[\\s\\S]*?<a href=\".*?\" class=\"c-link u-ellipsis\">([\\s\\S]*?)</a>"
                + "[\\s\\S]*?<div class=\"o-fNotes\">(.*?)</div>"
                + "[\\s\\S]*?<a href=\".*?\" class=\"c-link u-ellipsis\">([\\s\\S]*?)</a>"
                + "[\\s\\S]*?<div class=\"o-fNotes\">(.*?)</div>"
                + "[\\s\\S]*?<div class=\"o-fMini\">(.*?)</div>";


        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Pattern pa = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);                                                     //忽略大小寫
        Matcher ma = pa.matcher(content);

        //  try {
        while (ma.find()) {
            if (getRepoTime(ma.group(6)) > getDateTimeForHostDays()) {


                Map<String, String> map = new HashMap<String, String>();
                map.put("網址", ma.group(1));
                map.put("標題", sortTag(ma.group(2)));
                map.put("作者", ma.group(3));
                map.put("發文時間", ma.group(4));
                map.put("回文者", ma.group(5));
                map.put("回文時間", ma.group(6));
                map.put("回應數", ma.group(7));

                Pattern pmobile = Pattern.compile("<img srcset=\".*?\" class=\"o-logo\" alt=\"(Mobile01)\">", Pattern.CASE_INSENSITIVE);
                Matcher mmobile = pmobile.matcher(content);

                while (mmobile.find()) {
                    map.put("論壇", mmobile.group(1));
                }

                list.add(map);
                // System.out.println(ma.group(1));

                String content2 = Getcontent.getSource("https://www.mobile01.com/" + ma.group(1));
                Getcontent.sortMainTolist(content2);
                for (int a = 0; a < list.size(); a++) {
                    String content3 = Getcontent.getSource("https://www.mobile01.com/" + ma.group(1) + "&p=" + a);
                    Getcontent.sortIndexTolist(content3);
                }
            }
        }

        // System.out.println(content2);


        Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        for (
                int i = 0; i < list.size(); i++) {

            Session session = sessionFactory.openSession();
            Transaction ts = session.beginTransaction();
            MobilephoneEntity MP = new MobilephoneEntity();

            int oncetime = 1;
            if (oncetime == 1) {

                MP.setUrl(list.get(i).get("網址"));
                MP.setWriter(list.get(i).get("作者"));
                MP.setTitle(list.get(i).get("標題"));
                MP.setForum(list.get(i).get("論壇"));
                MP.setReplyTime(list.get(i).get("回文時間"));
                MP.setPostTime(list.get(i).get("發文時間"));
                MP.setLastReplyPerson(list.get(i).get("回文者"));
                MP.setResponses(list.get(i).get("回應數"));
                session.clear();
                session.save(MP);
                ts.commit();
            }
            session.close();
        }



              /*  for (Map m : list) {

                    MP.setUrl(m.get("網址").toString());
                    MP.setWriter(m.get("作者").toString());
                    MP.setTitle(m.get("標題").toString());
                    MP.setForum(m.get("論壇").toString());
                    MP.setReplyTime(m.get("回文時間").toString());
                    MP.setPostTime(m.get("發文時間").toString());
                    MP.setLastReplyPerson(m.get("回文者").toString());
                    MP.setResponses(m.get("回應數").toString());
                }*/

/*

            for (int i = 0; i < list.size(); i++) {
                System.out.println("標題: " + list.get(i).get("標題")
                        + "\n網址:https://www.mobile01.com/" + list.get(i).get("網址")
                        + "\n發文者:" + list.get(i).get("作者")
                        + "\n發文時間:" + list.get(i).get("發文時間")
                        + "\n回文者:" + list.get(i).get("回文者")
                        + "\n回文時間:" + list.get(i).get("回文時間")
                        + "\n回應數:" + list.get(i).get("回應數")
                        + "\n論壇:" + list.get(i).get("論壇")

                        + "\n\n\n");
            }
*/


        return list;
    }

       /* } catch (
                Exception e) {
            if (ts != null) {
                ts.rollback();
            }
            throw new RuntimeException(e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
*/


    ////////////////////////////////////////////////////////////////////////////
    //////////////<<去標籤>>////////////////
    public static String sortTag(String content) {

        String brTag = "<br>";
        Pattern pa = Pattern.compile(brTag, Pattern.CASE_INSENSITIVE);
        Matcher ma = pa.matcher(content);
        content = ma.replaceAll("\r\n");

        String regex_html = "<[^>]+>";
        pa = Pattern.compile(regex_html, Pattern.CASE_INSENSITIVE);
        ma = pa.matcher(content);
        content = ma.replaceAll("");

        return content.trim();
    }
////////////////////////////////////////////////////////////////////////////
    ///////////////////<<<<主文>>>>//////////////////////


    public static List<Map<String, String>> sortMainTolist(String content) {
        //整理內容,回傳List
      /*  Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();*/
       /* Session session = sessionFactory.openSession();
        Transaction ts = session.beginTransaction();*/

        String regex = "<meta name=\"twitter:url\" content=\"(.*?)\" />"
                + "[\\s\\S]*?<a href=\".*?\" class=\"c-link c-link--gn u-ellipsis\">([\\s\\S]*?)</a>"
                + "[\\s\\S]*?<h1 class=\"t2\">(.*?)</h1>[\\s\\S]*?<span class=\"o-fNotes o-fSubMini\">(\\w+-\\w+-\\w+ \\w+:\\w+)"
                + "[\\s\\S]*?<div itemprop=\"articleBody\">([\\s\\S]*?)</div>"
                + "[\\s\\S]*?<span class=\"o-fNotes o-fSubMini\">#([1-9])+</span>";

/*
        MobilecontentEntity MC = new MobilecontentEntity();
*/
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Pattern pa = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);                                                     //忽略大小寫
        Matcher ma = pa.matcher(content);

        //  try {
        while (ma.find()) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("網址", ma.group(1));
            map.put("作者", sortTag(ma.group(2)));
            map.put("標題", ma.group(3));
            map.put("發文時間", ma.group(4));
            map.put("內文", (sortTag(ma.group(5))));
            map.put("樓", ma.group(6));

            Pattern pmobile = Pattern.compile("<img srcset=\".*?\" class=\"o-logo\" alt=\"(Mobile01)\">", Pattern.CASE_INSENSITIVE);
            Matcher mmobile = pmobile.matcher(content);

            while (mmobile.find()) {
                map.put("論壇", mmobile.group(1));
            }

            list.add(map);
        }



        /*if(ma.group(1)!=null){}*/
 /*          int i=0;
                while(i!=list.size()) {
                    Query query = session.createQuery("from entities.MobilecontentEntity");
                    List names = query.list();
                    Iterator iterator = names.iterator();
                    MobilecontentEntity user = (MobilecontentEntity) iterator.next();

                    System.out.println(user.getUrl());
                }*/
/* for (Map m : list) {
                MC.setUrl(m.get("網址").toString());
                MC.setWriter(m.get("作者").toString());
                MC.setTitle(m.get("標題").toString());
                MC.setForum(m.get("論壇").toString());
                MC.setStep(m.get("樓").toString());
                MC.setPostTime(m.get("發文時間").toString());
                MC.setContent(m.get("內文").toString());
                session.save(MC);
                ts.commit();
            }*/
        Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        for (int i = 0; i < list.size(); i++) {
            Session session = sessionFactory.openSession();
            Transaction ts = session.beginTransaction();
            MobilecontentEntity MC = new MobilecontentEntity();

            int oncetime = 1;
            if (oncetime == 1) {
                MC.setUrl(list.get(i).get("網址"));
                MC.setWriter(list.get(i).get("作者"));
                MC.setTitle(list.get(i).get("標題"));
                MC.setForum(list.get(i).get("論壇"));
                MC.setStep(list.get(i).get("樓"));
                MC.setPostTime(list.get(i).get("發文時間"));
                MC.setContent(list.get(i).get("內文"));
                session.clear();
                session.save(MC);
                ts.commit();
            }
            session.close();
        }

          /*  for (int i = 0; i < list.size(); i++) {
                System.out.println("標題: " + list.get(i).get("標題")
                        + "\n網址:" + list.get(i).get("網址")
                        + "\n樓層:" + list.get(i).get("樓")
                        + "\n發文者:" + list.get(i).get("作者")
                        + "\n發文時間:" + list.get(i).get("發文時間")
                        + "\n內文:" + list.get(i).get("內文")
                        + "\n論壇:" + list.get(i).get("論壇")
                        + "\n\n\n\n\n");
            }*/
        return list;
       /* } catch (
                Exception e) {
            if (ts != null) {
                ts.rollback();
            }
            throw new RuntimeException(e.getMessage());
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }*/
    }

//////////////////////////////////////////////////////////////////////////////////
    /////////<<<<<回文>>>>>//////////

    public static List<Map<String, String>> sortIndexTolist(String content2) throws InterruptedException {
//        Configuration configuration = new Configuration().configure();
//        SessionFactory sessionFactory = configuration.buildSessionFactory();
//        Session session = sessionFactory.openSession();
        //    Transaction ts = session.beginTransaction();

        String regex = " <div class=\"l-articlePage\">"
                + "[\\s\\S]*?<div class=\"c-authorInfo__id\">[\\s\\S]*?<a href=\".*?\" class=\"c-link c-link--gn u-ellipsis\">([\\s\\S]*?)</a>"
                + "[\\s\\S]*?<article id=\".*?\" class=\"[\\s\\S]*?\">([\\s\\S]*?)</article>"
                + "[\\s\\S]*?<span class=\"o-fNotes o-fSubMini\">(\\w+-\\w+-\\w+ \\w+:\\w+)"
                + "[\\s\\S]*?<span class=\"o-fNotes o-fSubMini\">#([0-9]*)</span>[\\s\\S]*?</div>";


        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Pattern pa = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        Matcher ma = pa.matcher(content2);

        // try{
        while (ma.find()) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("名字", sortTag(ma.group(1)));
            map.put("內容", sortTag(ma.group(2)));
            map.put("時間", ma.group(3));
            map.put("樓層", ma.group(4));

            Pattern p = Pattern.compile("<h1 class=\"t2\">([\\s\\S]*?)</h1>", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(content2);

            while (m.find()) {
                map.put("標題", m.group(1));
            }

            Pattern pmobile = Pattern.compile("<img srcset=\".*?\" class=\"o-logo\" alt=\"(Mobile01)\">", Pattern.CASE_INSENSITIVE);
            Matcher mmobile = pmobile.matcher(content2);

            while (mmobile.find()) {
                map.put("論壇", mmobile.group(1));
            }
            list.add(map);

        }

        //   for (int i = 1; i < list.size(); i++) {


        Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();

        /* for (Map m : list) {
             Session session = sessionFactory.openSession();
             Transaction ts = session.beginTransaction();
             MobilereplyEntity MR = new MobilereplyEntity();
             int oncetime=1;
             if(oncetime==1){
                MR.setWriter(m.get("名字").toString());
                MR.setTitle(m.get("標題").toString());
                MR.setForum(m.get("論壇").toString());
                MR.setStep(m.get("樓層").toString());
                MR.setPostTime(m.get("時間").toString());
                MR.setContent(m.get("內容").toString());
               // Thread.sleep(100);
                session.save(MR);
                ts.commit();
             }

             session.close();

                        }*/
        // }


        for (int i = 0; i < list.size(); i++) {
            Session session = sessionFactory.openSession();
            Transaction ts = session.beginTransaction();
            MobilereplyEntity MR = new MobilereplyEntity();
            int oncetime = 1;
            if (oncetime == 1) {
                MR.setWriter(list.get(i).get("名字"));
                MR.setTitle(list.get(i).get("標題"));
                MR.setForum(list.get(i).get("論壇"));
                MR.setStep(list.get(i).get("樓層"));
                MR.setPostTime(list.get(i).get("時間"));
                MR.setContent(list.get(i).get("內容"));
                session.clear();
                session.save(MR);
                ts.commit();
            }
            session.close();
        }


        /*for (int i = 1; i < list.size(); i++) {
            System.out.println((i + 1) + "\n標題:" + list.get(i).get("標題")
                    + "\n名字:" + list.get(i).get("名字")
                    + "\n樓層:" + list.get(i).get("樓層")
                    + "\n時間:" + list.get(i).get("時間")
                    + "\n回文:" + list.get(i).get("內容")
                    + "\n論壇:" + list.get(i).get("論壇")
                    + "\n\n"
            );
        }*/

        return list;

//        }catch(
//    Exception e)
//
//    {
//        if (ts != null) {
//
//            ts.rollback();
//        }
//        throw new RuntimeException(e.getMessage());
//    } finally

//    {
//        if (session != null && session.isOpen()) {


//        }
//    }
    }
/////////////////////////////////////////////////////////////
    ////////<<<<天數選擇>>>>////////

    private static long getDateTimeForHostDays() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -3);
        return cal.getTimeInMillis();
    }

    private static long getRepoTime(String dateTime) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();

    }

    /////////////////////////////////////////////////////////////////
    /////////////<<<查詢>>>/////////////////
    public static void query() {
        Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();

        Query query = session.createQuery("from entities.MobilecontentEntity");

        List names = query.list();
        Iterator iterator = names.iterator();
        while (iterator.hasNext()) {
            MobilecontentEntity user = (MobilecontentEntity) iterator.next();

            System.out.println(user.getUrl());
        }
    }

}





