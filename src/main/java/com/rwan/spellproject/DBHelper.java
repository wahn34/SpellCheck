package com.rwan.spellproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    //SQLite를 사용하기 위해 extends SQLiteOpenHelper

    private Context context;

    public DBHelper(Context context, String spell_Input, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, spell_Input, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE SPELL_WORD (");
        sb.append(" _ID INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" SPELL_INPUT TEXT, ");
        sb.append(" SPELL_OUTPUT TEXT, ");
        sb.append(" SPELL_FREQ INT, ");
        sb.append(" SPELL_AUTO TEXT ) ");

        db.execSQL(sb.toString());

        Toast.makeText(context, "초기 데이터베이스 생성완료!", Toast.LENGTH_SHORT).show();
    }

    // 버전이 수정되어 Table의 요소가 변경되었을 경우
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(context, "데이터베이스가 업데이트 되었습니다", Toast.LENGTH_SHORT).show();
    }

    public void insertWordData(SpellWord spellWord) {
        //데이터 삽입

        SQLiteDatabase db = getWritableDatabase();
        StringBuffer sb = new StringBuffer();
        sb.append(" INSERT INTO SPELL_WORD ( ");
        sb.append(" SPELL_INPUT, SPELL_OUTPUT, SPELL_FREQ, SPELL_AUTO ) ");
        sb.append(" VALUES ( ?, ?, ?, ? ) ");

        db.execSQL(sb.toString(),
                new Object[]{
                        spellWord.getWord_input(),
                        spellWord.getWord_output(),
                        spellWord.getWord_freq(),
                        spellWord.getWord_auto()});
    }

    public void updateWordDataAuto(String spell_Input, String spell_auto) {
        SQLiteDatabase db = getWritableDatabase();
    // 입력한 항목과 일치하는 자동 수정여부 수정
        db.execSQL("UPDATE SPELL_WORD SET spell_auto='" + spell_auto + "' WHERE spell_Input='" + spell_Input + "';");
        db.close();
}
    public void updateWordDataFreq(String spell_Input, int spell_freq) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 자동 수정여부 수정
        db.execSQL("UPDATE SPELL_WORD SET spell_freq =" + spell_freq + " WHERE spell_Input='" + spell_Input + "';");
        db.close();
    }

    public void deleteWordData(String spell_Input) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행 삭제
        db.execSQL("DELETE FROM SPELL_WORD WHERE spell_Input='" + spell_Input + "';");
        db.close();
    }

    public int ifAlreadyWordData(String spell_input) {
        int x = 0;
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + "SPELL_WORD" + " WHERE spell_Input = '" + spell_input + "';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SpellWord spellWord = null;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                spellWord = new SpellWord();
                spellWord.set_id(cursor.getInt(0));
                spellWord.setWord_input(cursor.getString(1));
                spellWord.setWord_output(cursor.getString(2));
                spellWord.setWord_freq(cursor.getInt(3));
                spellWord.setWord_auto(cursor.getString(4));

                x+=1;
            } while (cursor.moveToNext());
        }
        return x;
    }
    public int findWordDataAndGetFreq(String spell_input) {
        int x = 0;
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + "SPELL_WORD" + " WHERE spell_Input = '" + spell_input + "';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SpellWord spellWord = null;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                spellWord = new SpellWord();
                spellWord.set_id(cursor.getInt(0));
                spellWord.setWord_input(cursor.getString(1));
                spellWord.setWord_output(cursor.getString(2));
                spellWord.setWord_freq(cursor.getInt(3));
                spellWord.setWord_auto(cursor.getString(4));

                x =spellWord.getWord_freq();
            } while (cursor.moveToNext());
        }
        return x;
    }
    public String findWordDataAndGetAuto(String spell_input) {
        String strm = "";
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + "SPELL_WORD" + " WHERE spell_Input = '" + spell_input + "';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SpellWord spellWord = null;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                spellWord = new SpellWord();
                spellWord.set_id(cursor.getInt(0));
                spellWord.setWord_input(cursor.getString(1));
                spellWord.setWord_output(cursor.getString(2));
                spellWord.setWord_freq(cursor.getInt(3));
                spellWord.setWord_auto(cursor.getString(4));

                strm =spellWord.getWord_auto();
            } while (cursor.moveToNext());
        }
        return strm;
    }
    public String findWordDataAndGetResult(String spell_input) {
        String strm = "";
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + "SPELL_WORD" + " WHERE spell_Input = '" + spell_input + "';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SpellWord spellWord = null;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                spellWord = new SpellWord();
                spellWord.set_id(cursor.getInt(0));
                spellWord.setWord_input(cursor.getString(1));
                spellWord.setWord_output(cursor.getString(2));
                spellWord.setWord_freq(cursor.getInt(3));
                spellWord.setWord_auto(cursor.getString(4));

                strm =spellWord.getWord_output();
            } while (cursor.moveToNext());
        }
        return strm;
    }
    public ArrayList<String> findWordDataAndGetSpellOutput() {
        ArrayList<String> strOutput = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + "SPELL_WORD;";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SpellWord spellWord = null;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                spellWord = new SpellWord();
                spellWord.set_id(cursor.getInt(0));
                spellWord.setWord_input(cursor.getString(1));
                spellWord.setWord_output(cursor.getString(2));
                spellWord.setWord_freq(cursor.getInt(3));
                spellWord.setWord_auto(cursor.getString(4));

                strOutput.add(spellWord.getWord_output());
            } while (cursor.moveToNext());
        }
        return strOutput;
    }
    public ArrayList<String> findWordDataAndGetSpellInput() {
        ArrayList<String> strInput = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + "SPELL_WORD;";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SpellWord spellWord = null;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                spellWord = new SpellWord();
                spellWord.set_id(cursor.getInt(0));
                spellWord.setWord_input(cursor.getString(1));
                spellWord.setWord_output(cursor.getString(2));
                spellWord.setWord_freq(cursor.getInt(3));
                spellWord.setWord_auto(cursor.getString(4));

                strInput.add(spellWord.getWord_input());
            } while (cursor.moveToNext());
        }
        return strInput;
    }
    public ArrayList<String> findWordDataAndGetSpellAuto() {
        ArrayList<String> strSpellAuto = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + "SPELL_WORD;";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SpellWord spellWord = null;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                spellWord = new SpellWord();
                spellWord.set_id(cursor.getInt(0));
                spellWord.setWord_input(cursor.getString(1));
                spellWord.setWord_output(cursor.getString(2));
                spellWord.setWord_freq(cursor.getInt(3));
                spellWord.setWord_auto(cursor.getString(4));

                strSpellAuto.add(spellWord.getWord_auto());
            } while (cursor.moveToNext());
        }
        return strSpellAuto;
    }
    public ArrayList<Integer> findWordDataAndGetSpellFreq() {
        ArrayList<Integer> strSpellFreq = new ArrayList<Integer>();
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + "SPELL_WORD;";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SpellWord spellWord = null;
        int it = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                spellWord = new SpellWord();
                spellWord.set_id(cursor.getInt(0));
                spellWord.setWord_input(cursor.getString(1));
                spellWord.setWord_output(cursor.getString(2));
                spellWord.setWord_freq(cursor.getInt(3));
                spellWord.setWord_auto(cursor.getString(4));

                it = spellWord.getWord_freq();
                Integer iInteger = new Integer(it);

                strSpellFreq.add(iInteger);
            } while (cursor.moveToNext());
        }
        return strSpellFreq;
    }

    public String searchWordData(String spell_Input) {
        String result = "";
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT * SPELL_WORD WHERE SPELL_INPUT" + spell_Input + ";");

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sb.toString(), null);

        SpellWord spellWord = null;
        int i = 0;
        while (cursor.moveToNext()) {
            spellWord = new SpellWord();
            spellWord.set_id(cursor.getInt(0));
            spellWord.setWord_input(cursor.getString(1));
            spellWord.setWord_output(cursor.getString(2));
            spellWord.setWord_freq(cursor.getInt(3));
            spellWord.setWord_auto(cursor.getString(4));
            result = String.format(spellWord.getWord_input()+ " -> " + spellWord.getWord_output()+" type : "+spellWord.getWord_auto() + " freq :" +spellWord.getWord_freq());
        }
        return result;
    }
    public String getResultData(String spell_Input) {
        String result = "";
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT * SPELL_WORD WHERE SPELL_INPUT" + spell_Input + ";");

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sb.toString(), null);

        SpellWord spellWord = null;
        int i = 0;
        while (cursor.moveToNext()) {
            spellWord = new SpellWord();
            spellWord.set_id(cursor.getInt(0));
            spellWord.setWord_input(cursor.getString(1));
            spellWord.setWord_output(cursor.getString(2));
            spellWord.setWord_freq(cursor.getInt(3));
            spellWord.setWord_auto(cursor.getString(4));
            result = String.format(spellWord.getWord_output());
        }
        return result;
    }
    public int readAndCountWordDatabase() {
        int x = 0;
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + "SPELL_WORD";// + " WHERE " + "DETAIL";
        Cursor cursor = db.rawQuery(selectQuery, null);
        SpellWord spellWord = null;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                spellWord = new SpellWord();
                spellWord.set_id(cursor.getInt(0));
                spellWord.setWord_input(cursor.getString(1));
                spellWord.setWord_output(cursor.getString(2));
                spellWord.setWord_freq(cursor.getInt(3));
                spellWord.setWord_auto(cursor.getString(4));
                x+=1;
            } while (cursor.moveToNext());

        }
        return x;
    }

}