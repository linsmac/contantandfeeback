package com.main;

import com.DB.DBConn;
import com.main.catcher.Getcontent;
import com.txt.OutputTxt;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

                String content = Getcontent.getSource("https://www.mobile01.com/topicdetail.php?f=177&t=5966663&p=1");
                List<Map<String, String>> list = Getcontent.sortContentTolist(content);
                DBConn.insert(list);
                int nextpage = 1;
                if(nextpage==1){
                while(nextpage!=5){
                String content2 = Getcontent.getSource("https://www.mobile01.com/topicdetail.php?f=177&t=5966663&p="+nextpage);

                List<Map<String, String>> list2 = Getcontent.sortIndexTolist(content2);
                DBConn.insert2(list2);
                nextpage++;
            }}

        //   OutputTxt.outputPage(list, "C:\\Users\\yongsyuanlin\\Desktop\\contentpage.txt");

         //System.out.println(content);
            }
}
