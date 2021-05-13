package com.rwan.spellproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


public class SpellCheckActivity extends AppCompatActivity {
    //int ifMethodSuccess = 1;
    private DBHelper dbHelper;
    ArrayList<HashMap<String, String>> wordList;
    public ListView list;
    private static final String TAG_INPUT = "strInput";
    private static final String TAG_OUTPUT ="strOutput";
    private static final String TAG_FREQ ="strFreq";
    private static final String TAG_AUTO ="strAuto";
    private static final String TAG_REASON ="strReason";
    public ListAdapter adapter;
    private String firstInput = "";
    private String firstOutput = "";
    String getCountString = "";
    String getCountWords = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_check);
        final EditText editInputText = (EditText) findViewById(R.id.editInputText);
        final TextView txtResultView = (TextView) findViewById(R.id.txtResultView);
        list = (ListView) findViewById(R.id.lstSpellChk);
        wordList = new ArrayList<HashMap<String,String>>();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fs = String.format(wordList.get(position).get(TAG_AUTO));
                String tm = String.format(wordList.get(position).get(TAG_INPUT));
                String ts = String.format(wordList.get(position).get(TAG_OUTPUT));
                if (fs.equals("yes"))
                {
                    Snackbar.make(getWindow().getDecorView().getRootView(), "이미 자동으로 변환되었습니다.", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    if (dbHelper == null) {
                        dbHelper = new DBHelper(SpellCheckActivity.this, "SPELL_WORD", null, 1);
                    }
                    checkWordAndAddDatabase(tm, ts);
                    String spellTmp = editInputText.getText().toString();
                    spellTmp = spellTmp.replace(tm, ts);
                    editInputText.setText(spellTmp);
                    Snackbar.make(getWindow().getDecorView().getRootView(), tm + "이 저장되었습니다.", Snackbar.LENGTH_SHORT).show();
                    wordList.clear();
                    showList(firstInput, firstOutput);
                    getCountString = tm;
                    new CreateTask().execute(String.format(wordList.get(position).get(TAG_INPUT)), "1");
                    System.out.println("QA: 삽입");

                }
                //wordList.get(position).get(TAG_INPUT)
                //Toast.makeText(SpellLogActivity.this ,wordList.get(position).get(TAG_INPUT),Toast.LENGTH_LONG).show();
            }
        });
        Button btnClipboard = (Button) findViewById(R.id.btnClipboard);
        btnClipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("label", editInputText.getText());
                clipboardManager.setPrimaryClip(clipData);
                Snackbar.make(getWindow().getDecorView().getRootView(), "클립보드에 복사 되었습니다.", Snackbar.LENGTH_SHORT).show();
            }
        });
        Button btnChkSpell = (Button) findViewById(R.id.btnChkSpell);
        btnChkSpell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    //ifMethodSuccess = 1;
                    String SpellTest = new Spellchk().execute(editInputText.getText().toString()).get();
                    txtResultView.setText(SpellTest);
                    firstInput = editInputText.getText().toString();
                    firstOutput = SpellTest;
                    //txtResultView.setText("테스트");
                } catch (Exception ex) {
                    //ifMethodSuccess = 0;
                    Snackbar.make(getWindow().getDecorView().getRootView(), "오류가 발생하였습니다.", Snackbar.LENGTH_SHORT).show();
                }
                wordList.clear();
                showList(editInputText.getText().toString(), txtResultView.getText().toString());
                editInputText.setText(checkDatabaseAndAutoReWriteText(editInputText.getText().toString(), txtResultView.getText().toString()));
                /*
                if(ifMethodSuccess == 1) {
                    try {
                        String spt = editInputText.getText().toString();
                        checkWordAndAddDatabase(spt, txtResultView.getText().toString());
                    }
                    catch (Exception ex)
                    {
                        ifMethodSuccess = 0;
                        Snackbar.make(getWindow().getDecorView().getRootView(), "데이터베이스에 문제가 있어요!", Snackbar.LENGTH_SHORT).show();
                    }
                }*/
            }
        });
    }
    protected void showList(String strInputs, String strOutputs){
        String[] arrInput = reBuildSpellCheckSentenceReturnInput(strInputs, strOutputs);
        //String[] arrInput = strInputs.split(" ");
        //String[] arrPerfect = strOutputs.split(" ");
        String[] arrPerfect = reBuildSpellCheckSentenceReturnOutput(strInputs, strOutputs);
        if (dbHelper == null) {
            dbHelper = new DBHelper(SpellCheckActivity.this, "SPELL_WORD", null, 1);
        }

        int req = 0;
        for (int i = 0; i < arrInput.length; i++) {
            if (arrInput.length < arrPerfect.length)
            {
                req = arrInput.length -1;
                //1)
                if (i<=arrInput.length)
                {
                    if (arrInput[req].matches(arrPerfect[i])) {

                    } else {
                        if (dbHelper.ifAlreadyWordData(arrInput[i]) == 0) {
                            HashMap<String, String> spWord = new HashMap<String, String>();
                            spWord.put(TAG_INPUT, arrInput[req]);
                            spWord.put(TAG_OUTPUT, arrPerfect[i]);
                            spWord.put(TAG_FREQ, "0");
                            spWord.put(TAG_AUTO, "새로 등록");
                            if(arrInput[req].contains(" "))
                                spWord.put(TAG_REASON, "띄어쓰기");
                            else
                                spWord.put(TAG_REASON, "맞춤법");
                            //ArrayList에 추가합니다..
                            wordList.add(spWord);
                            System.out.println("List<i<arrInput<Add : "+arrInput[req] + " " + arrPerfect[i] + "0 새로 등록");
                        } else {
                            try {
                                SQLiteDatabase ReadDB = this.openOrCreateDatabase("SPELL_WORD", MODE_PRIVATE, null);
                                //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
                                Cursor c = ReadDB.rawQuery("SELECT * FROM " + "SPELL_WORD WHERE spell_input = '" + arrInput[i] + "';", null);
                                if (c != null) {
                                    if (c.moveToFirst()) {
                                        do {
                                            //테이블에서 두개의 컬럼값을 가져와서
                                            String strInput = c.getString(c.getColumnIndex("SPELL_INPUT"));
                                            String strOutput = c.getString(c.getColumnIndex("SPELL_OUTPUT"));
                                            int iFreq = c.getInt(c.getColumnIndex("SPELL_FREQ"));
                                            String strFreq = String.format(iFreq + "");
                                            String strAuto = c.getString(c.getColumnIndex("SPELL_AUTO"));
                                            //HashMap에 넣습니다.
                                            HashMap<String, String> spWord = new HashMap<String, String>();
                                            spWord.put(TAG_INPUT, strInput);
                                            spWord.put(TAG_OUTPUT, strOutput);
                                            spWord.put(TAG_FREQ, strFreq);
                                            spWord.put(TAG_AUTO, strAuto);
                                            if(strInput.contains(" "))
                                                spWord.put(TAG_REASON, "띄어쓰기");
                                            else
                                                spWord.put(TAG_REASON, "맞춤법");
                                            //ArrayList에 추가합니다..
                                            wordList.add(spWord);
                                            //ArrayList에 추가합니다..
                                            System.out.println("List<Perfect : "+strInput + " " + strOutput + " " + strFreq + " " + strAuto);
                                            wordList.add(spWord);
                                            //System.out.println(strInput + ", " + strOutput + ", " + strFreq + ", " + strAuto);

                                        } while (c.moveToNext());
                                    }
                                }
                                ReadDB.close();
                            } catch (SQLiteException se) {
                                Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } // 1)
                }
                else
                {
                    if (arrInput[i].matches(arrPerfect[i])) {

                    } else {
                        if (dbHelper.ifAlreadyWordData(arrInput[i]) == 0) {
                            HashMap<String, String> spWord = new HashMap<String, String>();
                            spWord.put(TAG_INPUT, arrInput[i]);
                            spWord.put(TAG_OUTPUT, arrPerfect[i]);
                            spWord.put(TAG_FREQ, "0");
                            spWord.put(TAG_AUTO, "새로 등록");
                            //ArrayList에 추가합니다..
                            if(arrInput[i].contains(" "))
                                spWord.put(TAG_REASON, "띄어쓰기");
                            else
                                spWord.put(TAG_REASON, "맞춤법");
                            //ArrayList에 추가합니다..
                            wordList.add(spWord);
                            System.out.println("List<arrInput<Add : "+arrInput[i] + " " + arrPerfect[i] + "0 새로 등록");
                        } else {
                            try {
                                SQLiteDatabase ReadDB = this.openOrCreateDatabase("SPELL_WORD", MODE_PRIVATE, null);
                                //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
                                Cursor c = ReadDB.rawQuery("SELECT * FROM " + "SPELL_WORD WHERE spell_input = '" + arrInput[i] + "';", null);
                                if (c != null) {
                                    if (c.moveToFirst()) {
                                        do {
                                            //테이블에서 두개의 컬럼값을 가져와서
                                            String strInput = c.getString(c.getColumnIndex("SPELL_INPUT"));
                                            String strOutput = c.getString(c.getColumnIndex("SPELL_OUTPUT"));
                                            int iFreq = c.getInt(c.getColumnIndex("SPELL_FREQ"));
                                            String strFreq = String.format(iFreq + "");
                                            String strAuto = c.getString(c.getColumnIndex("SPELL_AUTO"));
                                            //HashMap에 넣습니다.
                                            HashMap<String, String> spWord = new HashMap<String, String>();
                                            spWord.put(TAG_INPUT, strInput);
                                            spWord.put(TAG_OUTPUT, strOutput);
                                            spWord.put(TAG_FREQ, strFreq);
                                            spWord.put(TAG_AUTO, strAuto);
                                            //ArrayList에 추가합니다..
                                            if(strInput.contains(" "))
                                                spWord.put(TAG_REASON, "띄어쓰기");
                                            else
                                                spWord.put(TAG_REASON, "맞춤법");
                                            //ArrayList에 추가합니다..
                                            System.out.println("List<Perfect : "+strInput + " " + strOutput + " " + strFreq + " " + strAuto);
                                            wordList.add(spWord);
                                            //System.out.println(strInput + ", " + strOutput + ", " + strFreq + ", " + strAuto);

                                        } while (c.moveToNext());
                                    }
                                }
                                ReadDB.close();
                            } catch (SQLiteException se) {
                                Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } // 1)
                }
            }
            else if (arrPerfect.length < arrInput.length)
            {
                req = arrPerfect.length -1;
                //1)
                if(i<=arrPerfect.length)
                {
                    if (arrInput[i].matches(arrPerfect[req])) {

                    } else {
                        if (dbHelper.ifAlreadyWordData(arrInput[i]) == 0) {
                            HashMap<String, String> spWord = new HashMap<String, String>();
                            spWord.put(TAG_INPUT, arrInput[i]);
                            spWord.put(TAG_OUTPUT, arrPerfect[req]);
                            spWord.put(TAG_FREQ, "0");
                            spWord.put(TAG_AUTO, "새로 등록");
                            //ArrayList에 추가합니다..
                            if(arrInput[i].contains(" "))
                                spWord.put(TAG_REASON, "띄어쓰기");
                            else
                                spWord.put(TAG_REASON, "맞춤법");
                            //ArrayList에 추가합니다..
                            System.out.println("List<i<arrPerfect<Add : "+arrInput[i] + " " + arrPerfect[req] + "0 새로 등록");
                            wordList.add(spWord);
                        } else {
                            try {
                                SQLiteDatabase ReadDB = this.openOrCreateDatabase("SPELL_WORD", MODE_PRIVATE, null);
                                //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
                                Cursor c = ReadDB.rawQuery("SELECT * FROM " + "SPELL_WORD WHERE spell_input = '" + arrInput[i] + "';", null);
                                if (c != null) {
                                    if (c.moveToFirst()) {
                                        do {
                                            //테이블에서 두개의 컬럼값을 가져와서
                                            String strInput = c.getString(c.getColumnIndex("SPELL_INPUT"));
                                            String strOutput = c.getString(c.getColumnIndex("SPELL_OUTPUT"));
                                            int iFreq = c.getInt(c.getColumnIndex("SPELL_FREQ"));
                                            String strFreq = String.format(iFreq + "");
                                            String strAuto = c.getString(c.getColumnIndex("SPELL_AUTO"));
                                            //HashMap에 넣습니다.
                                            HashMap<String, String> spWord = new HashMap<String, String>();
                                            spWord.put(TAG_INPUT, strInput);
                                            spWord.put(TAG_OUTPUT, strOutput);
                                            spWord.put(TAG_FREQ, strFreq);
                                            spWord.put(TAG_AUTO, strAuto);
                                            //ArrayList에 추가합니다..
                                            if(strInput.contains(" "))
                                                spWord.put(TAG_REASON, "띄어쓰기");
                                            else
                                                spWord.put(TAG_REASON, "맞춤법");
                                            //ArrayList에 추가합니다..
                                            wordList.add(spWord);
                                            System.out.println("List<Perfect : "+strInput + " " + strOutput + " " + strFreq + " " + strAuto);


                                        } while (c.moveToNext());
                                    }
                                }
                                ReadDB.close();
                            } catch (SQLiteException se) {
                                Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } // 1)
                }
                else
                {
                    if (arrInput[i].matches(arrPerfect[i])) {

                    } else {
                        if (dbHelper.ifAlreadyWordData(arrInput[i]) == 0) {
                            HashMap<String, String> spWord = new HashMap<String, String>();
                            spWord.put(TAG_INPUT, arrInput[i]);
                            spWord.put(TAG_OUTPUT, arrPerfect[i]);
                            spWord.put(TAG_FREQ, "0");
                            spWord.put(TAG_AUTO, "새로 등록");
                            //ArrayList에 추가합니다..
                            if(arrInput[i].contains(" "))
                                spWord.put(TAG_REASON, "띄어쓰기");
                            else
                                spWord.put(TAG_REASON, "맞춤법");
                            //ArrayList에 추가합니다..
                            System.out.println("List<arrPerfect<Add : "+arrInput[i] + " " + arrPerfect[i] + "0 새로 등록");
                            wordList.add(spWord);
                        } else {
                            try {
                                SQLiteDatabase ReadDB = this.openOrCreateDatabase("SPELL_WORD", MODE_PRIVATE, null);
                                //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
                                Cursor c = ReadDB.rawQuery("SELECT * FROM " + "SPELL_WORD WHERE spell_input = '" + arrInput[i] + "';", null);
                                if (c != null) {
                                    if (c.moveToFirst()) {
                                        do {
                                            //테이블에서 두개의 컬럼값을 가져와서
                                            String strInput = c.getString(c.getColumnIndex("SPELL_INPUT"));
                                            String strOutput = c.getString(c.getColumnIndex("SPELL_OUTPUT"));
                                            int iFreq = c.getInt(c.getColumnIndex("SPELL_FREQ"));
                                            String strFreq = String.format(iFreq + "");
                                            String strAuto = c.getString(c.getColumnIndex("SPELL_AUTO"));
                                            //HashMap에 넣습니다.
                                            HashMap<String, String> spWord = new HashMap<String, String>();
                                            spWord.put(TAG_INPUT, strInput);
                                            spWord.put(TAG_OUTPUT, strOutput);
                                            spWord.put(TAG_FREQ, strFreq);
                                            spWord.put(TAG_AUTO, strAuto);
                                            //ArrayList에 추가합니다..
                                            if(strInput.contains(" "))
                                                spWord.put(TAG_REASON, "띄어쓰기");
                                            else
                                                spWord.put(TAG_REASON, "맞춤법");
                                            //ArrayList에 추가합니다..
                                            wordList.add(spWord);
                                            System.out.println("List<Perfect : "+strInput + " " + strOutput + " " + strFreq + " " + strAuto);


                                        } while (c.moveToNext());
                                    }
                                }
                                ReadDB.close();
                            } catch (SQLiteException se) {
                                Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    } // 1)
                }
            }
            else
            {
                //1)
                if (arrInput[i].matches(arrPerfect[i])) {

                } else {
                    if (dbHelper.ifAlreadyWordData(arrInput[i]) == 0) {
                        HashMap<String, String> spWord = new HashMap<String, String>();
                        spWord.put(TAG_INPUT, arrInput[i]);
                        spWord.put(TAG_OUTPUT, arrPerfect[i]);
                        spWord.put(TAG_FREQ, "0");
                        spWord.put(TAG_AUTO, "새로 등록");
                        //ArrayList에 추가합니다..
                        System.out.println("List<=<Add : "+arrInput[i] + " " + arrPerfect[i] + "0 새로 등록");
                        if(arrInput[i].contains(" "))
                            spWord.put(TAG_REASON, "띄어쓰기");
                        else
                            spWord.put(TAG_REASON, "맞춤법");
                        //ArrayList에 추가합니다..
                        wordList.add(spWord);
                    } else {
                        try {
                            SQLiteDatabase ReadDB = this.openOrCreateDatabase("SPELL_WORD", MODE_PRIVATE, null);
                            //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
                            Cursor c = ReadDB.rawQuery("SELECT * FROM " + "SPELL_WORD WHERE spell_input = '" + arrInput[i] + "';", null);
                            if (c != null) {
                                if (c.moveToFirst()) {
                                    do {
                                        //테이블에서 두개의 컬럼값을 가져와서
                                        String strInput = c.getString(c.getColumnIndex("SPELL_INPUT"));
                                        String strOutput = c.getString(c.getColumnIndex("SPELL_OUTPUT"));
                                        int iFreq = c.getInt(c.getColumnIndex("SPELL_FREQ"));
                                        String strFreq = String.format(iFreq + "");
                                        String strAuto = c.getString(c.getColumnIndex("SPELL_AUTO"));
                                        //HashMap에 넣습니다.
                                        HashMap<String, String> spWord = new HashMap<String, String>();
                                        spWord.put(TAG_INPUT, strInput);
                                        spWord.put(TAG_OUTPUT, strOutput);
                                        spWord.put(TAG_FREQ, strFreq);
                                        spWord.put(TAG_AUTO, strAuto);
                                        //ArrayList에 추가합니다..
                                        if(strInput.contains(" "))
                                            spWord.put(TAG_REASON, "띄어쓰기");
                                        else
                                            spWord.put(TAG_REASON, "맞춤법");
                                        //ArrayList에 추가합니다..
                                        wordList.add(spWord);
                                        System.out.println("List<Perfect : "+strInput + " " + strOutput + " " + strFreq + " " + strAuto);

                                    } while (c.moveToNext());
                                }
                            }
                            ReadDB.close();
                        } catch (SQLiteException se) {
                            Toast.makeText(getApplicationContext(), se.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } // 1)
            }
        }
        //새로운 apapter를 생성하여 데이터를 넣은 후..
        adapter = new SimpleAdapter(
                this, wordList, R.layout.result_list_item,
                new String[]{TAG_INPUT,TAG_OUTPUT, TAG_FREQ,TAG_AUTO,TAG_REASON},
                new int[]{ R.id.sinput, R.id.soutput, R.id.sfreq, R.id.sauto, R.id.textWhy}
        );
        //화면에 보여주기 위해 Listview에 연결합니다.
        list.setAdapter(adapter);
    }
    public String checkDatabaseAndAutoReWriteText(String inputString, String perfectString)
    {
        String stm = inputString;
        String[] arrInput = reBuildSpellCheckSentenceReturnInput(inputString, perfectString);
        String[] arrPerfect = reBuildSpellCheckSentenceReturnOutput(inputString, perfectString);
        int req = 0;
        if (dbHelper == null) {
            dbHelper = new DBHelper(SpellCheckActivity.this, "SPELL_WORD", null, 1);
        }
        if (arrInput.length > arrPerfect.length)
        {
            req = arrPerfect.length -1;
            for (int i = 0; i < arrInput.length; i++) {
                if (i>=arrPerfect.length)
                {
                    if (arrInput[i].matches(arrPerfect[req])) {

                    }
                    else
                    {
                        if(dbHelper.findWordDataAndGetAuto(arrInput[i]).equals("yes"))
                        {
                            stm = stm.replace(arrInput[i],dbHelper.findWordDataAndGetResult(arrInput[i]));
                        }
                    }
                }
                else
                {
                    if (arrInput[i].matches(arrPerfect[i])) {

                    }
                    else
                    {
                        if(dbHelper.findWordDataAndGetAuto(arrInput[i]).equals("yes"))
                        {
                            stm = stm.replace(arrInput[i],dbHelper.findWordDataAndGetResult(arrInput[i]));
                        }
                    }
                }

            }
        }
        else
        {
            for (int i = 0; i < arrInput.length; i++) {
                if (arrInput[i].matches(arrPerfect[i])) {

                }
                else
                {
                    if(dbHelper.findWordDataAndGetAuto(arrInput[i]).equals("yes"))
                    {
                        stm = stm.replace(arrInput[i],dbHelper.findWordDataAndGetResult(arrInput[i]));
                    }
                }
            }
        }
        return stm;
    }
    public void checkWordAndAddDatabase(String inputString, String perfectString) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(SpellCheckActivity.this, "SPELL_WORD", null, 1);
        }
        if (dbHelper.ifAlreadyWordData(inputString)==0)
        {

            SpellWord spellWord = new SpellWord();
            spellWord.setWord_input(inputString);
            spellWord.setWord_output(perfectString);
            spellWord.setWord_freq(1);
            spellWord.setWord_auto("yes");
            dbHelper.insertWordData(spellWord);
        }
        else
        {
            //dbHelper.readAndCountWordDatabase();
            int tmp = dbHelper.findWordDataAndGetFreq(inputString);
            tmp ++;
            dbHelper.updateWordDataFreq(inputString, tmp);
        }
        /*
        String[] arrInput = reBuildSpellCheckSentenceReturnInput(inputString, perfectString);
        String[] arrPerfect = reBuildSpellCheckSentenceReturnOutput(inputString, perfectString);
        int req = 0;
        if (dbHelper == null) {
            dbHelper = new DBHelper(SpellCheckActivity.this, "SPELL_WORD", null, 1);
        }
        if (arrInput.length > arrPerfect.length)
        {
            req = arrPerfect.length -1;
            for (int i = 0; i < arrInput.length; i++)
            {
                if (i>=arrPerfect.length)
                {
                    if(arrInput[i].matches(arrPerfect[req]))
                    {

                    }
                    else
                    {
                        if (dbHelper.ifAlreadyWordData(arrInput[i])==0)
                        {

                            SpellWord spellWord = new SpellWord();
                            spellWord.setWord_input(arrInput[i]);
                            spellWord.setWord_output(arrPerfect[req]);
                            spellWord.setWord_freq(1);
                            spellWord.setWord_auto("yes");
                            dbHelper.insertWordData(spellWord);
                        }
                        else
                        {
                            //dbHelper.readAndCountWordDatabase();
                            int tmp = dbHelper.findWordDataAndGetFreq(arrInput[i]);
                            tmp ++;
                            dbHelper.updateWordDataFreq(arrInput[i], tmp);
                        }
                    }
                }
                else
                {
                    if(arrInput[i].matches(arrPerfect[i]))
                    {

                    }
                    else
                    {
                        if (dbHelper.ifAlreadyWordData(arrInput[i])==0)
                        {

                            SpellWord spellWord = new SpellWord();
                            spellWord.setWord_input(arrInput[i]);
                            spellWord.setWord_output(arrPerfect[i]);
                            spellWord.setWord_freq(1);
                            spellWord.setWord_auto("yes");
                            dbHelper.insertWordData(spellWord);
                        }
                        else
                        {
                            //dbHelper.readAndCountWordDatabase();
                            int tmp = dbHelper.findWordDataAndGetFreq(arrInput[i]);
                            tmp ++;
                            dbHelper.updateWordDataFreq(arrInput[i], tmp);
                        }
                    }
                }
            }
        }
        else if (arrPerfect.length > arrInput.length)
        {
            req = arrInput.length -1;

            for (int i = 0; i < arrInput.length; i++)
            {
                if (i>=arrInput.length)
                {
                    if(arrInput[req].matches(arrPerfect[i]))
                    {

                    }
                    else
                    {
                        if (dbHelper.ifAlreadyWordData(arrInput[req])==0)
                        {

                            SpellWord spellWord = new SpellWord();
                            spellWord.setWord_input(arrInput[req]);
                            spellWord.setWord_output(arrPerfect[i]);
                            spellWord.setWord_freq(1);
                            spellWord.setWord_auto("yes");

                            dbHelper.insertWordData(spellWord);
                        }
                        else
                        {
                            //dbHelper.readAndCountWordDatabase();
                            int tmp = dbHelper.findWordDataAndGetFreq(arrInput[req]);
                            tmp ++;
                            dbHelper.updateWordDataFreq(arrInput[req], tmp);
                        }
                    }
                }
                else
                {
                    if(arrInput[i].matches(arrPerfect[i]))
                    {

                    }
                    else
                    {
                        if (dbHelper.ifAlreadyWordData(arrInput[i])==0)
                        {

                            SpellWord spellWord = new SpellWord();
                            spellWord.setWord_input(arrInput[i]);
                            spellWord.setWord_output(arrPerfect[i]);
                            spellWord.setWord_freq(1);
                            spellWord.setWord_auto("yes");

                            dbHelper.insertWordData(spellWord);
                        }
                        else
                        {
                            //dbHelper.readAndCountWordDatabase();
                            int tmp = dbHelper.findWordDataAndGetFreq(arrInput[req]);
                            tmp ++;
                            dbHelper.updateWordDataFreq(arrInput[req], tmp);
                        }
                    }
                }
            }
        }
        else
        {
            for (int i = 0; i < arrInput.length; i++)
            {
                if(arrInput[i].matches(arrPerfect[i]))
                {

                }
                else
                {
                    if (dbHelper.ifAlreadyWordData(arrInput[i])==0)
                    {

                        SpellWord spellWord = new SpellWord();
                        spellWord.setWord_input(arrInput[i]);
                        spellWord.setWord_output(arrPerfect[i]);
                        spellWord.setWord_freq(1);
                        spellWord.setWord_auto("yes");

                        dbHelper.insertWordData(spellWord);
                    }
                    else
                    {
                        //dbHelper.readAndCountWordDatabase();
                        int tmp = dbHelper.findWordDataAndGetFreq(arrInput[i]);
                        tmp ++;
                        dbHelper.updateWordDataFreq(arrInput[i], tmp);
                    }
                }
            }
        }
*/
    }
    public String[] reBuildSpellCheckSentenceReturnInput(String strInput, String strOutput)
    {
        String[] arrInput = strInput.split(" ");
        String[] arrOutput = strOutput.split(" ");
        ArrayList<String> arrReinput = new ArrayList<String>();
        int safeCode =0;
        if (arrInput.length == arrOutput.length)
        {

        }
        if (arrInput.length > arrOutput.length)
        {
            safeCode = arrOutput.length;
            for (int i = 0; i < arrInput.length; i++)
            {
                //System.out.print(i);
                if(i>=arrOutput.length)
                {
                    //1)
                    if (arrInput[i].equals(arrOutput[safeCode-1]))
                    {
                        //System.out.println("!SAME : " + arrInput[i] + " = " + arrOutput[safeCode-1]);
                    }
                    else
                    {
                        for (int j = i; j < arrInput.length; j++)
                        {
                            if (arrInput[j].equals(arrOutput[safeCode-1]))
                            {
                                arrInput[j-2] = String.format(arrInput[j-2]+" "+arrInput[j-1]);
                                for(int k = j; k < arrInput.length; k++)
                                {
                                    arrInput[k-1] = arrInput[k];
                                }
                                arrInput[arrInput.length-1] = "";
                            }
                        }
                        //System.out.println("!DIFF : " + arrInput[i] + " => " + arrOutput[safeCode-1]);
                    }//1)
                }

                else
                {
                    //1)
                    if (arrInput[i].equals(arrOutput[i]))
                    {
                        //System.out.println("SAME : " + arrInput[i] + " = " + arrOutput[i]);
                    }
                    else
                    {
                        for (int j = i; j < arrInput.length; j++)
                        {
                            if (arrInput[j].equals(arrOutput[i]))
                            {
                                arrInput[j-2] = String.format(arrInput[j-2]+" "+arrInput[j-1]);
                                for(int k = j; k < arrInput.length; k++)
                                {
                                    arrInput[k-1] = arrInput[k];
                                }
                                arrInput[arrInput.length-1] = "";
                            }
                        }
                        //System.out.println("DIFF : " + arrInput[i] + " => " + arrOutput[i]);

                    }//1)
                }
            }

        }
        if (arrInput.length < arrOutput.length)
        {
            safeCode = arrInput.length;
            for (int i = 0; i < arrOutput.length; i++)
            {
                //System.out.print(i);
                if(i>=arrInput.length)
                {
                    //2)
                    if (arrInput[safeCode-1].equals(arrOutput[i]))
                    {
                        //System.out.println("!SAME : " + arrInput[safeCode-1] + " = " + arrOutput[i]);
                    }
                    else
                    {
                        for (int j = i; j < arrOutput.length; j++)
                        {
                            if (arrOutput[j].equals(arrInput[safeCode-1]))
                            {
                                arrOutput[j-2] = String.format(arrOutput[j-2]+" "+arrOutput[j-1]);
                                for(int k = j; k < arrOutput.length; k++)
                                {
                                    arrOutput[k-1] = arrOutput[k];
                                }
                                arrOutput[arrOutput.length-1] = "";
                            }
                        }
                        //System.out.println("!DIFF : " + arrInput[safeCode-1] + " => " + arrOutput[i]);
                    }//2)
                }

                else
                {
                    //2)
                    if (arrInput[i].equals(arrOutput[i]))
                    {
                        //System.out.println("!SAME : " + arrInput[i] + " = " + arrOutput[i]);
                    }
                    else
                    {
                        for (int j = i; j < arrOutput.length; j++)
                        {
                            if (arrOutput[j].equals(arrInput[i]))
                            {
                                arrOutput[j-2] = String.format(arrOutput[j-2]+" "+arrOutput[j-1]);
                                for(int k = j; k < arrOutput.length; k++)
                                {
                                    arrOutput[k-1] = arrOutput[k];
                                }
                                arrOutput[arrOutput.length-1] = "";
                            }
                        }
                        //System.out.println("!DIFF : " + arrInput[i] + " => " + arrOutput[i]);
                    }//2)
                }
            }
        }
        //결과 값
        for (int re = 0; re < arrInput.length; re++)
        {
            if(arrInput[re].equals(""))
            {

            }
            else
            {
                arrReinput.add(arrInput[re]);
            }
        }
        for (int x = 0; x < arrReinput.size(); x++)
        {
            System.out.println("input : 변환된 문장 : " + arrReinput.get(x));
        }
        String[] strData = arrReinput.toArray(new String[arrReinput.size()]);
        return strData;
    }
    public String[] reBuildSpellCheckSentenceReturnOutput(String strInput, String strOutput)
    {
        //System.out.println(strInput + " " + strOutput);
        String[] arrInput = strInput.split(" ");
        String[] arrOutput = strOutput.split(" ");
        ArrayList<String> arrReOutput = new ArrayList<String>();
        int safeCode =0;
        if (arrInput.length == arrOutput.length)
        {

        }
        if (arrInput.length > arrOutput.length)
        {
            safeCode = arrOutput.length;
            for (int i = 0; i < arrInput.length; i++)
            {
                //System.out.print(i);
                if(i>=arrOutput.length)
                {
                    //1)
                    if (arrInput[i].equals(arrOutput[safeCode-1]))
                    {
                        //System.out.println("!SAME : " + arrInput[i] + " = " + arrOutput[safeCode-1]);
                    }
                    else
                    {
                        for (int j = i; j < arrInput.length; j++)
                        {
                            if (arrInput[j].equals(arrOutput[safeCode-1]))
                            {
                                arrInput[j-2] = String.format(arrInput[j-2]+" "+arrInput[j-1]);
                                for(int k = j; k < arrInput.length; k++)
                                {
                                    arrInput[k-1] = arrInput[k];
                                }
                                arrInput[arrInput.length-1] = "";
                            }
                        }
                        //System.out.println("!DIFF : " + arrInput[i] + " => " + arrOutput[safeCode-1]);
                    }//1)
                }

                else
                {
                    //1)
                    if (arrInput[i].equals(arrOutput[i]))
                    {
                        //System.out.println("SAME : " + arrInput[i] + " = " + arrOutput[i]);
                    }
                    else
                    {
                        for (int j = i; j < arrInput.length; j++)
                        {
                            if (arrInput[j].equals(arrOutput[i]))
                            {
                                arrInput[j-2] = String.format(arrInput[j-2]+" "+arrInput[j-1]);
                                for(int k = j; k < arrInput.length; k++)
                                {
                                    arrInput[k-1] = arrInput[k];
                                }
                                arrInput[arrInput.length-1] = "";
                            }
                        }
                        //System.out.println("DIFF : " + arrInput[i] + " => " + arrOutput[i]);

                    }//1)
                }
            }

        }
        if (arrInput.length < arrOutput.length)
        {
            safeCode = arrInput.length;
            for (int i = 0; i < arrOutput.length; i++)
            {
                //System.out.print(i);
                if(i>=arrInput.length)
                {
                    //2)
                    if (arrInput[safeCode-1].equals(arrOutput[i]))
                    {
                        //System.out.println("!SAME : " + arrInput[safeCode-1] + " = " + arrOutput[i]);
                    }
                    else
                    {
                        for (int j = i; j < arrOutput.length; j++)
                        {
                            if (arrOutput[j].equals(arrInput[safeCode-1]))
                            {
                                arrOutput[j-2] = String.format(arrOutput[j-2]+" "+arrOutput[j-1]);
                                for(int k = j; k < arrOutput.length; k++)
                                {
                                    arrOutput[k-1] = arrOutput[k];
                                }
                                arrOutput[arrOutput.length-1] = "";
                            }
                        }
                        //System.out.println("!DIFF : " + arrInput[safeCode-1] + " => " + arrOutput[i]);
                    }//2)
                }

                else
                {
                    //2)
                    if (arrInput[i].equals(arrOutput[i]))
                    {
                        //System.out.println("!SAME : " + arrInput[i] + " = " + arrOutput[i]);
                    }
                    else
                    {
                        for (int j = i; j < arrOutput.length; j++)
                        {
                            if (arrOutput[j].equals(arrInput[i]))
                            {
                                arrOutput[j-2] = String.format(arrOutput[j-2]+" "+arrOutput[j-1]);
                                for(int k = j; k < arrOutput.length; k++)
                                {
                                    arrOutput[k-1] = arrOutput[k];
                                }
                                arrOutput[arrOutput.length-1] = "";
                            }
                        }
                        //System.out.println("!DIFF : " + arrInput[i] + " => " + arrOutput[i]);
                    }//2)
                }
            }
        }
        for (int re = 0; re < arrOutput.length; re++)
        {
            if(arrOutput[re].equals(""))
            {

            }
            else
            {
                arrReOutput.add(arrOutput[re]);
                //System.out.println("dd : "+arrReOutput.get(re));
            }
        }

        for (int x = 0; x < arrReOutput.size(); x++)
        {
            System.out.println(x+" output : 변환된 문장 : " + arrReOutput.get(x));
        }
        String[] strData = arrReOutput.toArray(new String[arrReOutput.size()]);
        return strData;
    }
    class UpdatesTask extends AsyncTask<String, Void, String> {
        String strPost, strGet;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://184.73.37.243:8080/updatewords.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                //strId,strPw,strName,strCar,strAddress,strEmail,strMemo,strTel,strBirth
                strPost = "sm_words="+strings[0]+"&sm_counts="+strings[1];
                osw.write(strPost);
                osw.flush();
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader bfr = new BufferedReader(inputStreamReader);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = bfr.readLine()) != null) {
                        buffer.append(str);
                    }
                    strGet = buffer.toString();

                } else {
                    //Error
                    strGet = "error";
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                strGet = "error";
            } catch (IOException e) {
                e.printStackTrace();
                strGet = "error";
            }
            strGet = "success";
            return strGet;
        }
    }
    class CreateTask extends AsyncTask<String, Void, String> {
        String strPost, strGet;
        @Override

        protected String doInBackground(String... strings) {
            System.out.println("DB작업시작");
            try {
                String str;
                URL url = new URL("http://184.73.37.243:8080/newwords.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                System.out.println("DB넣음 : "+ strings[0] + " "+strings[1]);
                strPost = "sm_words="+strings[0]+"&sm_counts="+strings[1];
                osw.write(strPost);
                System.out.println(strPost);
                osw.flush();
                if(conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader bfr = new BufferedReader(inputStreamReader);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = bfr.readLine()) != null) {
                        buffer.append(str);
                    }
                    strGet = buffer.toString();

                } else {
                    //Error
                    strGet = "error";
                    System.out.println("ERROR");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                strGet = "error";
                System.out.println("ERROR");
            } catch (IOException e) {
                e.printStackTrace();
                strGet = "error";
                System.out.println("ERROR");
            }
            strGet = "success";
            return strGet;
        }
    }
}