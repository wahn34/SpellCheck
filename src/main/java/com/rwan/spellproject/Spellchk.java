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
        //필요한 부분만 파싱
        result = result.replace("jQuery112409807856261295123_1568709443948", "");
        //완전 수정된 결과값 반환하는 코드
        String strPerfectSentence = null;
        int iPerfect = 0, end =0;
        iPerfect = result.indexOf("\"notag_html\"");
        end = result.indexOf("}}});");
        strPerfectSentence = result.substring(iPerfect, end+1);
        strPerfectSentence = strPerfectSentence.replace("\"notag_html\":", "");
        strPerfectSentence = strPerfectSentence.replace("\"", "");
        strPerfectSentence = strPerfectSentence.replace("}", "");
        //완전 수정된 결과 값 추출 완료

        //입력된 값 나누기, 수정된 값 나누기
        String[] arrInput = strInput.split(" ");
        String[] arrPerfect = strPerfectSentence.split(" ");

        //추출된 작업이 틀린 구분에 존재하는지 확인
        //red 맞춤법 violet 표준어 의심 green 띄어쓰기 blue 통계적교정
        return  strPerfectSentence;
    }

    public static String returnPerfectSentence(String strInput)
    {
        //완전 수정된 결과값 반환하는 코드
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
        //완전 수정된 결과값 반환하는 코드
        String strPerfectSentence = null;
        String[] arrInput = strInput.split(" ");
        String[] arrPerfect = strPerpect.split(" ");
        return strPerfectSentence;
    }

    private void chkSpellingCheck() {

    }
}
