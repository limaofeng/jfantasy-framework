package org.jfantasy.desensitize.result;

public class Word {
  private int[] pos;
  private int startPos;
  private int endPos;
  private String word;
  private double level;

  public Word() {}

  public int[] getPos() {
    return this.pos;
  }

  public void setPos(int[] pos) {
    this.pos = pos;
  }

  public String getWord() {
    return this.word;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public double getLevel() {
    return this.level;
  }

  public void setLevel(double level) {
    this.level = level;
  }

  public int getStartPos() {
    return this.startPos;
  }

  public void setStartPos(int startPos) {
    this.startPos = startPos;
  }

  public int getEndPos() {
    return this.endPos;
  }

  public void setEndPos(int endPos) {
    this.endPos = endPos;
  }
}
