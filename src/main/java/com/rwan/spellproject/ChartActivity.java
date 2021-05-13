package com.rwan.spellproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

    public class ChartActivity extends AppCompatActivity {
        BarChart chart2;
        TextView txtInfo;
        Switch swAll;
        MyAsyncTask mTask;
        static String query = "select * from words order by wordscount desc";
        ArrayList<String> wrdLst = new ArrayList<String>();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chart);
            chart2 = findViewById(R.id.tab1_chart_2);
            txtInfo = findViewById(R.id.txtInfo);
            swAll = findViewById(R.id.sw_all);
            swAll.setOnCheckedChangeListener(new userSwitchListener());
            getListOfUserDataAndSort();

        }
        class userSwitchListener implements CompoundButton.OnCheckedChangeListener{
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    txtInfo.setText("서버에서 데이터를 받아오는 중..");
                    chart2.clearChart();
                    mTask = new MyAsyncTask();
                    mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");

                }
                else
                {
                    txtInfo.setText("데이터를 처리하는 중..");
                    getListOfUserDataAndSort();
                    //String[] strData = wrdLst.toArray(new String[wrdLst.size()]);
                }
            }
        }
        public void getListOfUserDataAndSort()
        {
            ArrayList spLog = new ArrayList<>();
            try {
                SQLiteDatabase ReadDB = this.openOrCreateDatabase("SPELL_WORD", MODE_PRIVATE, null);
                //SELECT문을 사용하여 테이블에 있는 데이터를 가져옵니다..
                Cursor c = ReadDB.rawQuery("SELECT * FROM " + "SPELL_WORD", null);
                if (c != null) {
                    if (c.moveToFirst()) {
                        do {
                            String strInput = c.getString(c.getColumnIndex("SPELL_INPUT"));
                            String strOutput = c.getString(c.getColumnIndex("SPELL_OUTPUT"));
                            int iFreq = c.getInt(c.getColumnIndex("SPELL_FREQ"));
                            String strFreq = String.format(iFreq+"");
                            String strAuto = c.getString(c.getColumnIndex("SPELL_AUTO"));

                            //spellWordLog.add(new SpellWordLog(String.format(strInput),iFreq));
                            spLog.add(new SpellWordLog(iFreq, strInput));
                            System.out.println(strInput + ", " + strOutput + ", " +strFreq + ", " +strAuto);
                        } while (c.moveToNext());
                    }
                }
                ReadDB.close();
            } catch (SQLiteException se) {
                Toast.makeText(getApplicationContext(),  se.getMessage(), Toast.LENGTH_LONG).show();
            }

            System.out.println("정렬 전");
            for (int i = 0; i < spLog.size(); i++)
                System.out.println(spLog.get(i).toString());
            // 내림차순 정렬
            spLog.sort(new Comparator<SpellWordLog>() {
                @Override
                public int compare(SpellWordLog arg0, SpellWordLog arg1) {
                    // TODO Auto-generated method stub
                    int age0 = arg0.getAge();
                    int age1 = arg1.getAge();
                    if (age0 == age1)
                        return 0;
                    else if (age1 > age0)
                        return 1;
                    else
                        return -1;
                }
            });
            if (spLog.size() > 5)
            {
                chart2.clearChart();
                System.out.println("내림차순 정렬");
                int tm = spLog.size()-5;
                txtInfo.setText("  가장 많이 틀린 순으로 정렬된 결과입니다. ("+tm+"건 생략 됨)");
                for (int i = 0; i < 5; i++)
                {

                    String strTmp = String.format(spLog.get(i).toString());

                    String[] arrInput = strTmp.split("<1>");
                    System.out.println(strTmp + " " + arrInput.length);
                    chart2.addBar(new BarModel(String.format(arrInput[1]+""), Float.parseFloat(arrInput[0]) , 0xFF1586d5));
                }
                chart2.startAnimation();
            }
            else
            {
                chart2.clearChart();
                System.out.println("내림차순 정렬");
                txtInfo.setText("가장 많이 틀린 순으로 정렬된 결과입니다.");
                for (int i = 0; i < spLog.size(); i++)
                {
                    String strTmp = String.format(spLog.get(i).toString());

                    String[] arrInput = strTmp.split("<1>");
                    System.out.println(strTmp + " " + arrInput.length);
                    chart2.addBar(new BarModel(String.format(arrInput[1]+""), Float.parseFloat(arrInput[0]) , 0xFF1586d5));
                }
                chart2.startAnimation();
            }
        }

        public void getListOfAllUserDataAndSort()
        {
            chart2.clearChart();
            System.out.println("정렬 전" + wrdLst.size());
            for (int i = 0; i< wrdLst.size(); i++)
            {
                System.out.println(wrdLst);
            }
            if (wrdLst.size() > 5)
            {
                for (int i = 0; i< 5; i++)
                {
                    String strTmp = String.format(wrdLst.get(i).toString());
                    String[] arrInput = strTmp.split("<1>");
                    chart2.addBar(new BarModel(String.format(arrInput[0]+""), Float.parseFloat(arrInput[1]) , 0xFFd5157d));
                }
                int td = wrdLst.size()-5;
                txtInfo.setText("  가장 많이 틀린 순으로 정렬된 결과입니다. ("+td+"건 생략 됨)");
            }
            else
            {
                for (int i = 0; i< wrdLst.size(); i++)
                {
                    String strTmp = String.format(wrdLst.get(i).toString());
                    String[] arrInput = strTmp.split("<1>");
                    chart2.addBar(new BarModel(String.format(arrInput[0]+""), Float.parseFloat(arrInput[1]) , 0xFFd5157d));
                }
                txtInfo.setText("  가장 많이 틀린 순으로 정렬된 결과입니다.");
            }
            chart2.startAnimation();
        }

        class MyAsyncTask extends AsyncTask<String, Void, ArrayList<String>>
        {

            @Override
            protected void onPreExecute(){
                super.onPreExecute();
            }


            @Override
            protected ArrayList<String> doInBackground( String... params){
                System.out.println(">>>>>>>");
                wrdLst.clear();
                ResultSet reset = null;
                Connection conn = null;
                try {
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    conn = DriverManager.getConnection("jdbc:oracle:thin:@orclelec.cfvmyazpemfk.us-east-1.rds.amazonaws.com:1521:orcl","rywn34","myelectric");
                    Statement stmt = conn.createStatement();
                    reset = stmt.executeQuery(query);
                    while(reset.next()){
                        if ( isCancelled() ) break;
                        final String str = reset.getString(1)+"<1>"+reset.getString(2);
                        wrdLst.add(str);
                        System.out.println(">>>>>>>"+str);
                    }
                    conn.close();
                }
                catch (Exception e)
                {
                    Log.w("111Error connection", "" + e.getMessage());
                }
                return wrdLst;
            }

            @Override
            protected void onPostExecute(ArrayList<String> list){
                getListOfAllUserDataAndSort();
            }

            @Override
            protected void onCancelled(){
                super.onCancelled();
            }
        }
    }
