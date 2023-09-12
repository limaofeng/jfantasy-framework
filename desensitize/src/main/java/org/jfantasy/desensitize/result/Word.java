package org.jfantasy.desensitize.result;

import lombok.Getter;

@Getter
public class Word {
  private int[] pos;
  private int startPos;
  private int endPos;
  private String word;
  private double level;

  public Word() {}

  public void setPos(int[] pos) {
    this.pos = pos;
  }

  public void setWord(String word) {
    this.word = word;
  }

  public void setLevel(double level) {
    this.level = level;
  }

  public void setStartPos(int startPos) {
    this.startPos = startPos;
  }

  public void setEndPos(int endPos) {
    this.endPos = endPos;
  }
}
