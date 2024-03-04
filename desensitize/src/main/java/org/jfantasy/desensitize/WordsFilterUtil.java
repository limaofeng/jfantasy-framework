package org.jfantasy.desensitize;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jfantasy.desensitize.result.FilteredResult;
import org.jfantasy.desensitize.result.Word;
import org.jfantasy.desensitize.search.tree.Node;

/**
 * 单词过滤
 *
 * @author limaofeng
 */
public class WordsFilterUtil {
  private static final Node TREE = new Node();
  private static final Node POSITIVE_TREE;
  private static final Pattern p;

  public WordsFilterUtil() {}

  public static void addWords(WordsType type, InputStream input) {
    try {
      InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
      Properties prop = new Properties();
      prop.load(reader);
      Enumeration<?> en = prop.propertyNames();

      while (en.hasMoreElements()) {
        String word = (String) en.nextElement();
        insertWord(
            type == WordsType.SENSITIVE ? TREE : POSITIVE_TREE,
            word,
            Double.parseDouble(prop.getProperty(word)));
      }
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException var35) {
          var35.printStackTrace();
        }
      }
    }
  }

  private static void insertWord(Node tree, String word, double level) {
    word = word.toLowerCase();
    Node node = tree;

    for (int i = 0; i < word.length(); ++i) {
      node = node.addChar(word.charAt(i));
    }

    node.setEnd(true);
    node.setLevel(level);
    node.setWord(word);
  }

  private static boolean isPunctuationChar(String c) {
    Matcher m = p.matcher(c);
    return m.find();
  }

  private static WordsFilterUtil.PunctuationOrHtmlFilteredResult filterPunctation(
      String originalString) {
    StringBuilder filteredString = new StringBuilder();
    ArrayList<Integer> charOffsets = new ArrayList<>();

    for (int i = 0; i < originalString.length(); ++i) {
      String c = String.valueOf(originalString.charAt(i));
      if (!isPunctuationChar(c)) {
        filteredString.append(c);
        charOffsets.add(i);
      }
    }

    WordsFilterUtil.PunctuationOrHtmlFilteredResult result =
        new WordsFilterUtil.PunctuationOrHtmlFilteredResult();
    result.setOriginalString(originalString);
    result.setFilteredString(filteredString);
    result.setCharOffsets(charOffsets);
    return result;
  }

  private static WordsFilterUtil.PunctuationOrHtmlFilteredResult filterPunctationAndHtml(
      String originalString) {
    StringBuilder filteredString = new StringBuilder();
    ArrayList<Integer> charOffsets = new ArrayList<>();
    int i = 0;

    for (boolean var4 = false; i < originalString.length(); ++i) {
      String c = String.valueOf(originalString.charAt(i));
      if (originalString.charAt(i) != '<') {
        if (!isPunctuationChar(c)) {
          filteredString.append(c);
          charOffsets.add(i);
        }
      } else {
        int k;
        for (k = i + 1; k < originalString.length(); ++k) {
          if (originalString.charAt(k) == '<') {
            k = i;
            break;
          }

          if (originalString.charAt(k) == '>') {
            break;
          }
        }

        i = k;
      }
    }

    WordsFilterUtil.PunctuationOrHtmlFilteredResult result =
        new WordsFilterUtil.PunctuationOrHtmlFilteredResult();
    result.setOriginalString(originalString);
    result.setFilteredString(filteredString);
    result.setCharOffsets(charOffsets);
    return result;
  }

  private static FilteredResult filter(
      WordsFilterUtil.PunctuationOrHtmlFilteredResult pohResult, char replacement) {
    StringBuilder sentence = pohResult.getFilteredString();
    StringBuilder sb = new StringBuilder(pohResult.getOriginalString());
    ArrayList<Integer> charOffsets = pohResult.getCharOffsets();
    List<Word> positiveWords = simpleFilter2DictFindWords(sentence, POSITIVE_TREE);
    List<Word> sensitiveWords = simpleFilter2DictFindWords(sentence, TREE);
    Iterator<Word> sIt = sensitiveWords.iterator();

    while (true) {
      while (sIt.hasNext()) {
        Word sWord = sIt.next();

        int i;
        Word pWord;
        for (i = 0; i < positiveWords.size(); ++i) {
          pWord = positiveWords.get(i);
          if (pWord.getEndPos() >= sWord.getStartPos()) {
            break;
          }
        }

        while (i < positiveWords.size()) {
          pWord = positiveWords.get(i);
          if (pWord.getStartPos() > sWord.getEndPos()) {
            break;
          }

          if (pWord.getStartPos() < sWord.getStartPos()
              && pWord.getEndPos() >= sWord.getStartPos()
              && pWord.getLevel() > sWord.getLevel()) {
            sIt.remove();
            break;
          }

          if (pWord.getStartPos() <= sWord.getEndPos()
              && pWord.getEndPos() > sWord.getEndPos()
              && pWord.getLevel() > sWord.getLevel()) {
            sIt.remove();
            break;
          }

          if (pWord.getStartPos() <= sWord.getStartPos()
              && pWord.getEndPos() >= sWord.getEndPos()
              && pWord.getLevel() > sWord.getLevel()) {
            sIt.remove();
            break;
          }

          ++i;
        }
      }

      Double maxLevel = 0.0D;
      StringBuilder badWords = new StringBuilder();
      Iterator var16 = sensitiveWords.iterator();

      while (var16.hasNext()) {
        Word word = (Word) var16.next();
        badWords.append(word.getWord()).append(",");
        if (word.getLevel() > maxLevel) {
          maxLevel = word.getLevel();
        }
      }

      StringBuilder goodWords = new StringBuilder();
      Iterator var18 = positiveWords.iterator();

      Word word;
      while (var18.hasNext()) {
        word = (Word) var18.next();
        goodWords.append(word.getWord()).append(",");
      }

      var18 = sensitiveWords.iterator();

      while (var18.hasNext()) {
        word = (Word) var18.next();

        for (int i = 0; i < word.getPos().length; ++i) {
          sb.replace(
              (Integer) charOffsets.get(word.getPos()[i]),
              (Integer) charOffsets.get(word.getPos()[i]) + 1,
              "" + replacement);
        }
      }

      FilteredResult result = new FilteredResult();
      result.setBadWords(badWords.toString());
      result.setGoodWords(goodWords.toString());
      result.setFilteredContent(sb.toString());
      result.setOriginalContent(pohResult.getOriginalString());
      result.setLevel(maxLevel);
      result.setHasSensiviWords(!sensitiveWords.isEmpty());
      return result;
    }
  }

  public static FilteredResult simpleFilter(String sentence, char replacement) {
    StringBuilder sb = new StringBuilder(sentence);
    List<Word> positiveWords = simpleFilter2DictFindWords(sb, POSITIVE_TREE);
    List<Word> sensitiveWords = simpleFilter2DictFindWords(sb, TREE);
    Iterator sIt = sensitiveWords.iterator();

    while (true) {
      while (sIt.hasNext()) {
        Word sWord = (Word) sIt.next();

        int i;
        Word pWord;
        for (i = 0; i < positiveWords.size(); ++i) {
          pWord = (Word) positiveWords.get(i);
          if (pWord.getEndPos() >= sWord.getStartPos()) {
            break;
          }
        }

        while (i < positiveWords.size()) {
          pWord = (Word) positiveWords.get(i);
          if (pWord.getStartPos() > sWord.getEndPos()) {
            break;
          }

          if (pWord.getStartPos() < sWord.getStartPos()
              && pWord.getEndPos() >= sWord.getStartPos()
              && pWord.getLevel() > sWord.getLevel()) {
            sIt.remove();
            break;
          }

          if (pWord.getStartPos() <= sWord.getEndPos()
              && pWord.getEndPos() > sWord.getEndPos()
              && pWord.getLevel() > sWord.getLevel()) {
            sIt.remove();
            break;
          }

          if (pWord.getStartPos() <= sWord.getStartPos()
              && pWord.getEndPos() >= sWord.getEndPos()
              && pWord.getLevel() > sWord.getLevel()) {
            sIt.remove();
            break;
          }

          ++i;
        }
      }

      Double maxLevel = 0.0D;
      StringBuilder badWords = new StringBuilder();
      Iterator var14 = sensitiveWords.iterator();

      while (var14.hasNext()) {
        Word word = (Word) var14.next();
        badWords.append(word.getWord()).append(",");
        if (word.getLevel() > maxLevel) {
          maxLevel = word.getLevel();
        }
      }

      StringBuilder goodWords = new StringBuilder();
      Iterator var16 = positiveWords.iterator();

      Word word;
      while (var16.hasNext()) {
        word = (Word) var16.next();
        goodWords.append(word.getWord()).append(",");
      }

      var16 = sensitiveWords.iterator();

      while (var16.hasNext()) {
        word = (Word) var16.next();

        for (int i = 0; i < word.getPos().length; ++i) {
          sb.replace(word.getPos()[i], word.getPos()[i] + 1, "" + replacement);
        }
      }

      FilteredResult result = new FilteredResult();
      result.setBadWords(badWords.toString());
      result.setGoodWords(goodWords.toString());
      result.setFilteredContent(sb.toString());
      result.setOriginalContent(sentence);
      result.setLevel(maxLevel);
      result.setHasSensiviWords(!sensitiveWords.isEmpty());
      return result;
    }
  }

  private static List<Word> simpleFilter2DictFindWords(StringBuilder sentence, Node dictTree) {
    List<Word> foundWords = new LinkedList<>();
    int start = 0;
    int end = 0;

    for (int i = 0; i < sentence.length(); ++i) {
      start = i;
      end = i;
      Node node = dictTree;
      Node lastFoundNode = null;

      for (int j = i; j < sentence.length(); ++j) {
        node = node.findChar(toLowerCase(sentence.charAt(j)));
        if (node == null) {
          break;
        }

        if (node.isEnd()) {
          end = j;
          lastFoundNode = node;
        }
      }

      if (end > i) {
        int[] pos = new int[end - i + 1];

        for (int j = 0; j < pos.length; ++j) {
          pos[j] = start + j;
        }

        Word word = new Word();
        word.setPos(pos);
        word.setStartPos(start);
        word.setEndPos(end);
        word.setLevel(lastFoundNode.getLevel());
        word.setWord(lastFoundNode.getWord());
        foundWords.add(word);
      }
    }

    return foundWords;
  }

  public static FilteredResult filterTextWithPunctation(String originalString, char replacement) {
    return filter(filterPunctation(originalString), replacement);
  }

  public static FilteredResult filterHtml(String originalString, char replacement) {
    return filter(filterPunctationAndHtml(originalString), replacement);
  }

  public static char toLowerCase(char c) {
    return c >= 'A' && c <= 'Z' ? (char) (c + 32) : c;
  }

  static {
    InputStream is = WordsFilterUtil.class.getResourceAsStream("/sensitive-words.dict");
    if (is == null) {
      is = WordsFilterUtil.class.getClassLoader().getResourceAsStream("/sensitive-words.dict");
    }

    addWords(WordsType.SENSITIVE, is);

    String regex = "[\\pP\\pZ\\pS\\pM\\pC]";
    p = Pattern.compile(regex, 2);
    POSITIVE_TREE = new Node();
    is = WordsFilterUtil.class.getResourceAsStream("/positive-words.dict");
    if (is == null) {
      is = WordsFilterUtil.class.getClassLoader().getResourceAsStream("/positive-words.dict");
    }

    addWords(WordsType.POSITIVE, is);
  }

  private static class PunctuationOrHtmlFilteredResult {
    private String originalString;
    private StringBuilder filteredString;
    private ArrayList<Integer> charOffsets;

    private PunctuationOrHtmlFilteredResult() {}

    public String getOriginalString() {
      return this.originalString;
    }

    public void setOriginalString(String originalString) {
      this.originalString = originalString;
    }

    public StringBuilder getFilteredString() {
      return this.filteredString;
    }

    public void setFilteredString(StringBuilder filteredString) {
      this.filteredString = filteredString;
    }

    public ArrayList<Integer> getCharOffsets() {
      return this.charOffsets;
    }

    public void setCharOffsets(ArrayList<Integer> charOffsets) {
      this.charOffsets = charOffsets;
    }
  }
}
