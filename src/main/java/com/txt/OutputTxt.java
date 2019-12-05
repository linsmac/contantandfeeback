package com.txt;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

public class OutputTxt {
    public static boolean outputPage(List<Map<String, String>> list, String path)
    {

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(path);
            osw = new OutputStreamWriter(fos, "utf-8");

            bw = new BufferedWriter(osw);
            //StringBuffer sb = new StringBuffer();
            String str;
            int i = 1;
            for (Object o : list) {
                Map m = (Map)o;

                str= i+". 標題:" + m.get("標題")+"\r\n"+
                        "網址:" + m.get("網址")+"\r\n"+
                        "作者:" + m.get("作者")+"\r\n"+
                        "發文時間:" + m.get("發文時間")+"\r\n"+
                        "內文:\r\n"+"\r\n" + m.get("內文")+"\r\n"+"\r\n";
                //System.out.println(str);

                bw.write(str);

                i++;

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                bw.close();

            } catch (IOException e) {

                e.printStackTrace();
            }

            try {
                osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }





        return false;




    }


}
