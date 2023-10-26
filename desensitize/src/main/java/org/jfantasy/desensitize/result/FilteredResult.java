package org.jfantasy.desensitize.result;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import lombok.Getter;

@Getter
public class FilteredResult {
  private Double level;
  private String filteredContent;
  private String badWords;
  private String goodWords;
  private String originalContent;
  private Boolean hasSensiviWords = false;

  public void setBadWords(String badWords) {
    this.badWords = badWords;
  }

  public FilteredResult() {}

  public FilteredResult(
      String originalContent, String filteredContent, Double level, String badWords) {
    this.originalContent = originalContent;
    this.filteredContent = filteredContent;
    this.level = level;
    this.badWords = badWords;
  }

  public void setLevel(Double level) {
    this.level = level;
  }

  public void setFilteredContent(String filteredContent) {
    this.filteredContent = filteredContent;
  }

  public void setOriginalContent(String originalContent) {
    this.originalContent = originalContent;
  }

  public void setGoodWords(String goodWords) {
    this.goodWords = goodWords;
  }

  public void setHasSensiviWords(Boolean hasSensiviWords) {
    this.hasSensiviWords = hasSensiviWords;
  }
}
