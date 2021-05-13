package com.rwan.spellproject;

public class SpellWord {

    private int _id = 0;

    private String word_input;
    private String word_output;
    private int word_freq;
    private String word_auto;

    public int get_id() {
        return _id;
    }

    public String getWord_input() {
        return word_input;
    }

    public String getWord_output() {
        return word_output;
    }

    public String getWord_auto() {
        return word_auto;
    }

    public int getWord_freq() {
        return  word_freq;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setWord_freq(int word_freq)
    {
        this.word_freq = word_freq;
    }

    public void setWord_input(String word_input) {
        this.word_input = word_input;
    }

    public void setWord_output(String word_output) {
        this.word_output = word_output;
    }

    public void setWord_auto(String word_auto) {
        this.word_auto = word_auto;
    }
}