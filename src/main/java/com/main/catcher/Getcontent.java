package com.main.catcher;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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


    public static List<Map<String, String>> sortContentTolist(String content) {
        //整理內容,回傳List


       String regex ="<meta name=\"twitter:url\" content=\"(.*?)\" />"
                +"[\\s\\S]*?<a href=\".*?\" class=\"c-link c-link--gn u-ellipsis\">([\\s\\S]*?)</a>"
                +"[\\s\\S]*?<h1 class=\"t2\">(.*?)</h1>[\\s\\S]*?<span class=\"o-fNotes o-fSubMini\">(\\w+-\\w+-\\w+ \\w+:\\w+)"
                +"[\\s\\S]*?<div itemprop=\"articleBody\">([\\s\\S]*?)</div>"
                +"[\\s\\S]*?<span class=\"o-fNotes o-fSubMini\">(#[1-9])+</span>";




        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Pattern pa = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);                                                     //忽略大小寫
        Matcher ma = pa.matcher(content);


        while (ma.find()) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("網址", ma.group(1));
            map.put("作者", sortTag(ma.group(2)));
            map.put("標題", ma.group(3));
            map.put("發文時間", ma.group(4));
            map.put("內文", (sortTag(ma.group(5))));
            map.put("樓", ma.group(6));



            list.add(map);


        }

        for(int i = 0;i<list.size();i++)
        {
            System.out.println("標題: "+list.get(i).get("標題")
                                    +"\n網址:"+list.get(i).get("網址")
                                    +"\n樓層:"+list.get(i).get("樓")
                                    +"\n發文者:" +list.get(i).get("作者")
                                    +"\n發文時間:" +list.get(i).get("發文時間")
                                    +"\n內文:"+list.get(i).get("內文")
                                    +"\n\n\n\n\n");}
        return list;
    }

    public static String sortTag(String content){

        String brTag="<br>";
        Pattern pa=Pattern.compile(brTag,Pattern.CASE_INSENSITIVE);
        Matcher ma=pa.matcher(content);
        content=ma.replaceAll("\r\n");

        String regex_html="<[^>]+>";
        pa=Pattern.compile(regex_html,Pattern.CASE_INSENSITIVE);
        ma=pa.matcher(content);
        content=ma.replaceAll("");

        return content.trim();
    }


    public static List<Map<String, String>> sortIndexTolist(String content2) {

        String regex ="<div class=\"l-articlePage\">"
                +"[\\s\\S]*?<div class=\"c-authorInfo__id\">[\\s\\S]*?<a href=\".*?\" class=\"c-link c-link--gn u-ellipsis\">([\\s\\S]*?)</a>"
                      +"[\\s\\S]*?<article id=\".*?\" class=\"[\\s\\S]*?\">([\\s\\S]*?)</article>"
                      +"[\\s\\S]*?<span class=\"o-fNotes o-fSubMini\">(\\w+-\\w+-\\w+ \\w+:\\w+)"
                      +"[\\s\\S]*?<span class=\"o-fNotes o-fSubMini\">(#[0-9]*)</span>[\\s\\S]*?</div>";



        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Pattern pa = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher ma = pa.matcher(content2);

        while (ma.find()) {

            if (getRepoTime(ma.group(3))>getDateTimeForHostDays()) {

                Map<String, String> map = new HashMap<String, String>();
                map.put("名字",sortTag(ma.group(1)));
                map.put("內容",sortTag(ma.group(2)));
                map.put("時間", ma.group(3));
                map.put("樓層", ma.group(4));
                map.put("標題","用iPhone又用&quot;仿&quot;AirPods是什麼心態？");
                list.add(map);
            }

        }

        for(int i = 0;i<list.size();i++)
        {
            System.out.println((i+1)+"\n標題:"+list.get(i).get("標題")
                                   +"\n名字:"+list.get(i).get("名字")
                                   +"\n樓層:"+list.get(i).get("樓層")
                                   +"\n時間:"+list.get(i).get("時間")
                                   +"\n回文:"+list.get(i).get("內容")+"\n\n"
            );

        }

        return list;
    }
    private static long getDateTimeForHostDays() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        return cal.getTimeInMillis();
    }

    private static long getRepoTime(String dateTime) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }








}
