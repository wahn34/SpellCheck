package com.rwan.spellproject;

import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class Spellchk extends AsyncTask<String, Void, String> {
    static private DBHelper dbHelper;

    @Override
    protected String doInBackground(String ... params) {
        String str ="";
        try
        {
            str = SpellCheckMethod(params[0]);
        }
        catch (IOException io){

        }
        return str;
    }
    public static String SpellCheckMethod(String inputString) throws IOException {
        String getUrl = null;
        String result = null;
        String line = null;
        String strInput = null;
        line = inputString;
        strInput = line;
        line= line.replace(" ", "%20");
        try
        {
            Connection.Response response2 = Jsoup.connect("https://m.search.naver.com/p/csearch/ocontent/util/SpellerProxy?_callback=jQuery112409807856261295123_1568709443948&q="+line+"&where=nexearch&color_blindness=")
                    .method(Connection.Method.GET)
                    .execute();
            Document document = response2.parse();
            String html = document.html();
            String text = document.text();
        }
        catch (org.jsoup.UnsupportedMimeTypeException seo) {
            result = seo.getMessage();
            result = seo.toString();
            result = result.replace("org.jsoup.UnsupportedMimeTypeException: Unhandled content type. Must be text/*, application/xml, or application/xhtml+xml. Mimetype=application/json;charset=UTF-8, URL=", "");
            getUrl = result;
        }
        URL url = new URL(getUrl);
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        result = "";
        while ((line = br.readLine()) != null)
            result += line;
        br.close();
        //????????? ????????? ??????
        result = result.replace("jQuery112409807856261295123_1568709443948", "");
        //?????? ????????? ????????? ???????????? ??????
        String strPerfectSentence = null;
        int iPerfect = 0, end =0;
        iPerfect = result.indexOf("\"notag_html\"");
        end = result.indexOf("}}});");
        strPerfectSentence = result.substring(iPerfect, end+1);
        strPerfectSentence = strPerfectSentence.replace("\"notag_html\":", "");
        strPerfectSentence = strPerfectSentence.replace("\"", "");
        strPerfectSentence = strPerfectSentence.replace("}", "");
        //?????? ????????? ?????? ??? ?????? ??????

        //????????? ??? ?????????, ????????? ??? ?????????
        String[] arrInput = strInput.split(" ");
        String[] arrPerfect = strPerfectSentence.split(" ");

        //????????? ????????? ?????? ????????? ??????????????? ??????
        //red ????????? violet ????????? ?????? green ???????????? blue ???????????????
        return  strPerfectSentence;
    }

    public static String returnPerfectSentence(String strInput)
    {
        //?????? ????????? ????????? ???????????? ??????
        String strPerfectSentence = null;
        int iPerfect = 0, end =0;
        iPerfect = strInput.indexOf("\"notag_html\"");
        end = strInput.indexOf("}}});");
        strPerfectSentence = strInput.substring(iPerfect, end+1);
        strPerfectSentence = strPerfectSentence.replace("\"notag_html\":", "");
        strPerfectSentence = strPerfectSentence.replace("\"", "");
        strPerfectSentence = strPerfectSentence.replace("}", "");
        return strPerfectSentence;
    }

    public static String returnCollectSentence(String strInput, String strPerpect)
    {
        //?????? ????????? ????????? ???????????? ??????
        String strPerfectSentence = null;
        String[] arrInput = strInput.split(" ");
        String[] arrPerfect = strPerpect.split(" ");
        return strPerfectSentence;
    }

    private void chkSpellingCheck() {

    }
}
