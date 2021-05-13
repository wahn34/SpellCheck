package com.rwan.spellproject;

public class SpellWordLog  {
    private int mCount;
    private String mName;

    public SpellWordLog(int count, String name) {
        mCount = count;
        mName = name;
    }

    public int getAge() { return mCount; }

    public String getName() { return mName;  }

    public String toString() {
        return mCount + "<1>" +mName;
    }
}
