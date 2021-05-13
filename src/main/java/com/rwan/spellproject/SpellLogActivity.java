package com.rwan.spellproject;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;

public class SpellLogActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    ArrayList<HashMap<String, String>> wordList;
    public ListView list;
    private static final String TAG_INPUT = "strInput";
    private static final String TAG_OUTPUT ="strOutput";
    private static final String TAG_FREQ ="strFreq";
    private static final String TAG_AUTO ="strAuto";
    private static final String TAG_REASON ="strReason";

    public ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_log);
        list = (ListView) findViewById(R.id.listView);
        wordList = new ArrayList<HashMap<String,String>>();
        showList();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tm = String.format(wordList.get(position).get(TAG_AUTO));
                String ts = String.format(wordList.get(position).get(TAG_INPUT));
                System.out.println(tm + " " + String.format(wordList.get(position).get(TAG_INPUT)));
                if (dbHelper == null) {
                    dbHelper = new DBHelper(SpellLogActivity.this, "SPELL_WORD", null, 1);
                }
                if (tm.equals("yes"))
                {
                    dbHelper.updateWordDataAuto(ts, "no");
                    //Toast.makeText(SpellLogActivity.this ,wordList.get(position).get(TAG_INPUT)+"의 자동변환이 꺼졌습니다.",Toast.LENGTH_SHORT).show();
                    Snackbar.make(getWindow().getDecorView().getRootView(), wordList.get(position).get(TAG_INPUT)+"의 자동변환이 꺼졌습니다.", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    dbHelper.updateWordDataAuto(ts, "yes");
                    Snackbar.make(getWindow().getDecorView().getRootView(), wordList.get(position).get(TAG_INPUT)+"의 자동변환이 켜졌습니다.", Snackbar.LENGTH_SHORT).show();
                }
                wordList.clear();
                showList();
                //wordList.get(position).get(TAG_INPUT)
                //Toast.makeText(SpellLogActivity.this ,wordList.get(position).get(TAG_INPUT),Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void showList(){
        //arrAuto = dbHelper.findWordDataAndGetSpellAuto();
        //arrInput = dbHelper.findWordDataAndGetSpellInput();
        //arrFreq = dbHelper.findWordDataAndGetSpellFreq();
        //arrOutput = dbHelper.findWordDataAndGetSpellOutput();
        try {
            SQLiteDatabase ReadDB = this.openOrCreateDatabase("SPELL_WORD", MODE_PRIVATE, null);
            //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
            Cursor c = ReadDB.rawQuery("SELECT * FROM " + "SPELL_WORD", null);


            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        //테이블에서 두개의 컬럼값을 가져와서
                        String strInput = c.getString(c.getColumnIndex("SPELL_INPUT"));
                        String strOutput = c.getString(c.getColumnIndex("SPELL_OUTPUT"));
                        int iFreq = c.getInt(c.getColumnIndex("SPELL_FREQ"));
                        String strFreq = String.format(iFreq+"");
                        String strAuto = c.getString(c.getColumnIndex("SPELL_AUTO"));
                        //HashMap에 넣습니다.

                        HashMap<String,String> spWord = new HashMap<String,String>();
                        spWord.put(TAG_INPUT,strInput);
                        spWord.put(TAG_OUTPUT,strOutput);
                        spWord.put(TAG_FREQ,strFreq);
                        spWord.put(TAG_AUTO,strAuto);
                        if(strInput.contains(" "))
                            spWord.put(TAG_REASON, "띄어쓰기");
                        else
                            spWord.put(TAG_REASON, "맞춤법");
                        //ArrayList에 추가합니다..
                        wordList.add(spWord);
                        System.out.println(strInput + ", " + strOutput + ", " +strFreq + ", " +strAuto);

                    } while (c.moveToNext());
                }
            }

            ReadDB.close();


            //새로운 apapter를 생성하여 데이터를 넣은 후..
            adapter = new SimpleAdapter(
                    this, wordList, R.layout.log_list_item,
                    new String[]{TAG_INPUT,TAG_OUTPUT, TAG_FREQ,TAG_AUTO, TAG_REASON},
                    new int[]{ R.id.sinput, R.id.soutput, R.id.sfreq, R.id.sauto, R.id.txtWhy}
            );


            //화면에 보여주기 위해 Listview에 연결합니다.
            list.setAdapter(adapter);


        } catch (SQLiteException se) {
            Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

}
