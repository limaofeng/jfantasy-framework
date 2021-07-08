package org.jfantasy.desensitize;


import org.jfantasy.desensitize.result.FilteredResult;
import org.jfantasy.desensitize.result.Word;
import org.jfantasy.desensitize.search.tree.Node;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordsFilterUtil {
    private static Node tree = new Node();
    private static Node positiveTree;
    private static Pattern p;

    public WordsFilterUtil() {
    }

    public static void addWords(WordsType type, InputStream input) {
        try {
            InputStreamReader reader = new InputStreamReader(input, "UTF-8");
            Properties prop = new Properties();
            prop.load(reader);
            Enumeration en = prop.propertyNames();

            while (en.hasMoreElements()) {
                String word = (String) en.nextElement();
                insertWord(type == WordsType.SENSITIVE ? tree : positiveTree, word, Double.valueOf(prop.getProperty(word)));
            }
        } catch (UnsupportedEncodingException var36) {
            var36.printStackTrace();
        } catch (IOException var37) {
            var37.printStackTrace();
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

    private static WordsFilterUtil.PunctuationOrHtmlFilteredResult filterPunctation(String originalString) {
        StringBuilder filteredString = new StringBuilder();
        ArrayList<Integer> charOffsets = new ArrayList();

        for (int i = 0; i < originalString.length(); ++i) {
            String c = String.valueOf(originalString.charAt(i));
            if (!isPunctuationChar(c)) {
                filteredString.append(c);
                charOffsets.add(i);
            }
        }

        WordsFilterUtil.PunctuationOrHtmlFilteredResult result = new WordsFilterUtil.PunctuationOrHtmlFilteredResult();
        result.setOriginalString(originalString);
        result.setFilteredString(filteredString);
        result.setCharOffsets(charOffsets);
        return result;
    }

    private static WordsFilterUtil.PunctuationOrHtmlFilteredResult filterPunctationAndHtml(String originalString) {
        StringBuilder filteredString = new StringBuilder();
        ArrayList<Integer> charOffsets = new ArrayList();
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

        WordsFilterUtil.PunctuationOrHtmlFilteredResult result = new WordsFilterUtil.PunctuationOrHtmlFilteredResult();
        result.setOriginalString(originalString);
        result.setFilteredString(filteredString);
        result.setCharOffsets(charOffsets);
        return result;
    }

    private static FilteredResult filter(WordsFilterUtil.PunctuationOrHtmlFilteredResult pohResult, char replacement) {
        StringBuilder sentence = pohResult.getFilteredString();
        StringBuilder sb = new StringBuilder(pohResult.getOriginalString());
        ArrayList<Integer> charOffsets = pohResult.getCharOffsets();
        List<Word> positiveWords = simpleFilter2DictFindWords(sentence, positiveTree);
        List<Word> sensitiveWords = simpleFilter2DictFindWords(sentence, tree);
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

                    if (pWord.getStartPos() < sWord.getStartPos() && pWord.getEndPos() >= sWord.getStartPos() && pWord.getLevel() > sWord.getLevel()) {
                        sIt.remove();
                        break;
                    }

                    if (pWord.getStartPos() <= sWord.getEndPos() && pWord.getEndPos() > sWord.getEndPos() && pWord.getLevel() > sWord.getLevel()) {
                        sIt.remove();
                        break;
                    }

                    if (pWord.getStartPos() <= sWord.getStartPos() && pWord.getEndPos() >= sWord.getEndPos() && pWord.getLevel() > sWord.getLevel()) {
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
                    sb.replace((Integer) charOffsets.get(word.getPos()[i]), (Integer) charOffsets.get(word.getPos()[i]) + 1, "" + replacement);
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
        List<Word> positiveWords = simpleFilter2DictFindWords(sb, positiveTree);
        List<Word> sensitiveWords = simpleFilter2DictFindWords(sb, tree);
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

                    if (pWord.getStartPos() < sWord.getStartPos() && pWord.getEndPos() >= sWord.getStartPos() && pWord.getLevel() > sWord.getLevel()) {
                        sIt.remove();
                        break;
                    }

                    if (pWord.getStartPos() <= sWord.getEndPos() && pWord.getEndPos() > sWord.getEndPos() && pWord.getLevel() > sWord.getLevel()) {
                        sIt.remove();
                        break;
                    }

                    if (pWord.getStartPos() <= sWord.getStartPos() && pWord.getEndPos() >= sWord.getEndPos() && pWord.getLevel() > sWord.getLevel()) {
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
        List<Word> foundWords = new LinkedList();
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

    public static void main(String[] args) {
//        FilteredResult result = filterTextWithPunctation("江阴毛纺厂和大江阴毛纺厂生产的毛巾都来自姗姗来迟地毯厂，但是，来*迟!地(毯)生产的毛巾不错", '*');
//        System.out.println("result:" + result.getFilteredContent());
//        System.out.println("bad words:" + result.getBadWords());
//        System.out.println("good words:" + result.getGoodWords());
//        System.out.println("\n\n\n");
//        result = filterTextWithPunctation("网■站黄■色■漫△画▲网站", '*');
//        System.out.println(result.getFilteredContent());
//        System.out.println(result.getBadWords());
//        System.out.println(result.getLevel());
//        result = filterHtml("网站<font>黄</font>.<色<script>,漫,画,网站", '*');
//        System.out.println(result.getFilteredContent());
//        System.out.println(result.getBadWords());
//        System.out.println(result.getLevel());
//        String str = "我#们#的社 会中  国是我们$多么和谐的一个中###国##人%de民和谐#社#会啊色 ：魔司法";
//        str = "<span stylt='color:red'>黄</span>色<span>网</span>站黄色网站黄,色，网。站，山东毛纺厂江阴毛纺厂黄色网站24口交换机全国政协十一届四次会议今天开幕。在充满希望的春天里，来自各党派团体、各族各界的全国政协委员汇聚京城，共商国是。我们对大会的召开表示热烈的祝贺！未来五年间，“十二五”蓝图如何铺展挥就？在大有作为的重要战略机遇期，中国怎样更加奋发有为？全国政协十一届四次会议，承载着亿万人民的殷切嘱托，肩负着承前启后的历史重任。2010年是我国发展历程中很不平凡的一年。面对国际金融危机冲击带来的严重影响和国际国内环境的深刻变化，以胡锦涛同志为总书记的中共中央团结带领全国各族人民，同心同德、攻坚克难，集中力量办好大事喜事，妥善处置急事难事，党和国家各项事业取得了新的显著成就。在全国人民的共同努力下，“十一五”规划确定的目标任务胜利完成，国家面貌发生新的历史性变化，为全面建成小康社会奠定了重要基础。在过去的一年里，人民政协高举爱国主义、社会主义旗帜，把握团结和民主两大主题，发挥协调关系、汇聚力量、建言献策、服务大局重要作用，谱写了人民政协事业发展的新篇章。一年来，人民政协加强思想理论建设，自觉坚持中国共产党的领导，坚定不移地走中国特色社会主义政治发展道路；深入开展协商议政，为编制“十二五”规划和保持经济平稳较快发展献计出力；围绕教育、卫生、安居、食品安全等民生问题，积极推动社会管理创新，帮助党和政府排忧解难；认真贯彻民族宗教政策，促进民族团结与宗教和睦；加强与港澳台侨同胞的团结联谊，为中华民族伟大复兴凝心聚力；推动公共外交、拓展对外交流，为我国发展营造良好外部环境；大力推进经常性工作创新，提高人民政协工作科学化水平。总结一年来的生动实践，我们深切地体会到，推进人民政协事业持续发展，必须牢固树立“五个意识”：牢固树立政治意识，在纷繁复杂形势下保持清醒头脑，在大是大非面前站稳坚定立场，在社会深刻变革中坚持正确方向；牢固树立大局意识，始终服从服务党和国家中心工作；牢固树立群众意识，情牵人民、心系群众，协助党和政府妥善处理各方面利益关系；牢固树立履职意识，把履行政治协商、民主监督、参政议政职能作为重大历史使命；牢固树立委员意识，支持帮助广大委员深入实际、走向基层、贴近群众，在报效国家、服务人民的实践中施展才华、建功立业。2011年是“十二五”开局之年，中国的发展进入全面建设小康社会的关键时期。面对世情、国情的深刻变化，面对难得的历史机遇和诸多风险挑战，我们要倍加珍惜来之不易的良好发展局面，深入贯彻中共十七届五中全会和中央经济工作会议精神，继续抓住和用好重要战略机遇期，紧紧围绕党和国家中心工作，同心协力搞好政治协商，积极稳妥推进民主监督，扎实有效开展参政议政，汇聚起促进科学发展的强大合力，加快转变经济发展方式，加强和创新社会管理，做好群众工作，促进社会和谐稳定，为实现“十二五”良好开局、夺取全面建设小康社会新胜利作出贡献。成就鼓舞人心，蓝图催人奋进。让我们更加紧密地团结在以胡锦涛同志为总书记的中共中央周围，以邓小平理论和“三个代表”重要思想为指导，深入贯彻落实科学发展观，再接再厉、锐意进取，为13亿人民谋取更多福祉，为中华民族赢得更大荣光。预祝大会圆满成功。我们对大会的召开表示热烈的祝贺！未来五年间，“十二五”蓝图如何铺展挥就？在大有作为的重要战略机遇期，中国怎样更加奋发有为？全国政协十一届四次会议，承载着亿万人民的殷切嘱托，肩负着承前启后的历史重任。2010年是我国发展历程中很不平凡的一年。面对国际金融危机冲击带来的严重影响和国际国内环境的深刻变化，以胡锦涛同志为总书记的中共中央团结带领全国各族人民，同心同德、攻坚克难，集中力量办好大事喜事，妥善处置急事难事，党和国家各项事业取得了新的显著成就。在全国人民的共同努力下，“十一五”规划确定的目标任务胜利完成，国家面貌发生新的历史性变化，为全面建成小康社会奠定了重要基础。在过去的一年里，人民政协高举爱国主义、社会主义旗帜，把握团结和民主两大主题，发挥协调关系、汇聚力量、建言献策、服务大局重要作用，谱写了人民政协事业发展的新篇章。一年来，人民政协加强思想理论建设，自觉坚持中国共产党的领导，坚定不移地走中国特色社会主义政治发展道路；深入开展协商议政，为编制“十二五”规划和保持经济平稳较快发展献计出力；围绕教育、卫生、安居、食品安全等民生问题，积极推动社会管理创新，帮助党和政府排忧解难；认真贯彻民族宗教政策，促进民族团结与宗教和睦；加强与港澳台侨同胞的团结联谊，为中华民族伟大复兴凝心聚力；推动公共外交、拓展对外交流，为我国发展营造良好外部环境；大力推进经常性工作创新，提高人民政协工作科学化水平。总结一年来的生动实践，我们深切地体会到，推进人民政协事业持续发展，必须牢固树立“五个意识”：牢固树立政治意识，在纷繁复杂形势下保持清醒头脑，在大是大非面前站稳坚定立场，在社会深刻变革中坚持正确方向；牢固树立大局意识，始终服从服务党和国家中心工作；牢固树立群众意识，情牵人民、心系群众，协助党和政府妥善处理各方面利益关系；牢固树立履职意识，把履行政治协商、民主监督、参政议政职能作为重大历史使命；牢固树立委员意识，支持帮助广大委员深入实际、走向基层、贴近群众，在报效国家、服务人民的实践中施展才华、建功立业。2011年是“十二五”开局之年，中国的发展进入全面建设小康社会的关键时期。面对世情、国情的深刻变化，面对难得的历史机遇和诸多风险挑战，我们要倍加珍惜来之不易的良好发展局面，深入贯彻中共十七届五中全会和中央经济工作会议精神，继续抓住和用好重要战略机遇期，紧紧围绕党和国家中心工作，同心协力搞好政治协商，积极稳妥推进民主监督，扎实有效开展参政议政，汇聚起促进科学发展的强大合力，加快转变经济发展方式，加强和创新社会管理，做好群众工作，促进社会和谐稳定，为实现“十二五”良好开局、夺取全面建设小康社会新胜利作出贡献。成就鼓舞人心，蓝图催人奋进。让我们更加紧密地团结在以胡锦涛同志为总书记的中共中央周围，以邓小平理论和“三个代表”重要思想为指导，深入贯彻落实科学发展观，再接再厉、锐意进取，为13亿人民谋取更多福祉，为中华民族赢得更大荣光。预祝大会圆满成功。我们对大会的召开表示热烈的祝贺！未来五年间，“十二五”蓝图如何铺展挥就？在大有作为的重要战略机遇期，中国怎样更加奋发有为？全国政协十一届四次会议，承载着亿万人民的殷切嘱托，肩负着承前启后的历史重任。2010年是我国发展历程中很不平凡的一年。面对国际金融危机冲击带来的严重影响和国际国内环境的深刻变化，以胡锦涛同志为总书记的中共中央团结带领全国各族人民，同心同德、攻坚克难，集中力量办好大事喜事，妥善处置急事难事，党和国家各项事业取得了新的显著成就。在全国人民的共同努力下，“十一五”规划确定的目标任务胜利完成，国家面貌发生新的历史性变化，为全面建成小康社会奠定了重要基础。在过去的一年里，人民政协高举爱国主义、社会主义旗帜，把握团结和民主两大主题，发挥协调关系、汇聚力量、建言献策、服务大局重要作用，谱写了人民政协事业发展的新篇章。一年来，人民政协加强思想理论建设，自觉坚持中国共产党的领导，坚定不移地走中国特色社会主义政治发展道路；深入开展协商议政，为编制“十二五”规划和保持经济平稳较快发展献计出力；围绕教育、卫生、安居、食品安全等民生问题，积极推动社会管理创新，帮助党和政府排忧解难；认真贯彻民族宗教政策，促进民族团结与宗教和睦；加强与港澳台侨同胞的团结联谊，为中华民族伟大复兴凝心聚力；推动公共外交、拓展对外交流，为我国发展营造良好外部环境；大力推进经常性工作创新，提高人民政协工作科学化水平。总结一年来的生动实践，我们深切地体会到，推进人民政协事业持续发展，必须牢固树立“五个意识”：牢固树立政治意识，在纷繁复杂形势下保持清醒头脑，在大是大非面前站稳坚定立场，在社会深刻变革中坚持正确方向；牢固树立大局意识，始终服从服务党和国家中心工作；牢固树立群众意识，情牵人民、心系群众，协助党和政府妥善处理各方面利益关系；牢固树立履职意识，把履行政治协商、民主监督、参政议政职能作为重大历史使命；牢固树立委员意识，支持帮助广大委员深入实际、走向基层、贴近群众，在报效国家、服务人民的实践中施展才华、建功立业。2011年是“十二五”开局之年，中国的发展进入全面建设小康社会的关键时期。面对世情、国情的深刻变化，面对难得的历史机遇和诸多风险挑战，我们要倍加珍惜来之不易的良好发展局面，深入贯彻中共十七届五中全会和中央经济工作会议精神，继续抓住和用好重要战略机遇期，紧紧围绕党和国家中心工作，同心协力搞好政治协商，积极稳妥推进民主监督，扎实有效开展参政议政，汇聚起促进科学发展的强大合力，加快转变经济发展方式，加强和创新社会管理，做好群众工作，促进社会和谐稳定，为实现“十二五”良好开局、夺取全面建设小康社会新胜利作出贡献。成就鼓舞人心，蓝图催人奋进。让我们更加紧密地团结在以胡锦涛同志为总书记的中共中央周围，以邓小平理论和“三个代表”重要思想为指导，深入贯彻落实科学发展观，再接再厉、锐意进取，为13亿人民谋取更多福祉，为中华民族赢得更大荣光。预祝大会圆满成功。我们对大会的召开表示热烈的祝贺！未来五年间，“十二五”蓝图如何铺展挥就？在大有作为的重要战略机遇期，中国怎样更加奋发有为？全国政协十一届四次会议，承载着亿万人民的殷切嘱托，肩负着承前启后的历史重任。2010年是我国发展历程中很不平凡的一年。面对国际金融危机冲击带来的严重影响和国际国内环境的深刻变化，以胡锦涛同志为总书记的中共中央团结带领全国各族人民，同心同德、攻坚克难，集中力量办好大事喜事，妥善处置急事难事，党和国家各项事业取得了新的显著成就。在全国人民的共同努力下，“十一五”规划确定的目标任务胜利完成，国家面貌发生新的历史性变化，为全面建成小康社会奠定了重要基础。在过去的一年里，人民政协高举爱国主义、社会主义旗帜，把握团结和民主两大主题，发挥协调关系、汇聚力量、建言献策、服务大局重要作用，谱写了人民政协事业发展的新篇章。一年来，人民政协加强思想理论建设，自觉坚持中国共产党的领导，坚定不移地走中国特色社会主义政治发展道路；深入开展协商议政，为编制“十二五”规划和保持经济平稳较快发展献计出力；围绕教育、卫生、安居、食品安全等民生问题，积极推动社会管理创新，帮助党和政府排忧解难；认真贯彻民族宗教政策，促进民族团结与宗教和睦；加强与港澳台侨同胞的团结联谊，为中华民族伟大复兴凝心聚力；推动公共外交、拓展对外交流，为我国发展营造良好外部环境；大力推进经常性工作创新，提高人民政协工作科学化水平。总结一年来的生动实践，我们深切地体会到，推进人民政协事业持续发展，必须牢固树立“五个意识”：牢固树立政治意识，在纷繁复杂形势下保持清醒头脑，在大是大非面前站稳坚定立场，在社会深刻变革中坚持正确方向；牢固树立大局意识，始终服从服务党和国家中心工作；牢固树立群众意识，情牵人民、心系群众，协助党和政府妥善处理各方面利益关系；牢固树立履职意识，把履行政治协商、民主监督、参政议政职能作为重大历史使命；牢固树立委员意识，支持帮助广大委员深入实际、走向基层、贴近群众，在报效国家、服务人民的实践中施展才华、建功立业。2011年是“十二五”开局之年，中国的发展进入全面建设小康社会的关键时期。面对世情、国情的深刻变化，面对难得的历史机遇和诸多风险挑战，我们要倍加珍惜来之不易的良好发展局面，深入贯彻中共十七届五中全会和中央经济工作会议精神，继续抓住和用好重要战略机遇期，紧紧围绕党和国家中心工作，同心协力搞好政治协商，积极稳妥推进民主监督，扎实有效开展参政议政，汇聚起促进科学发展的强大合力，加快转变经济发展方式，加强和创新社会管理，做好群众工作，促进社会和谐稳定，为实现“十二五”良好开局、夺取全面建设小康社会新胜利作出贡献。成就鼓舞人心，蓝图催人奋进。让我们更加紧密地团结在以胡锦涛同志为总书记的中共中央周围，以邓小平理论和“三个代表”重要思想为指导，深入贯彻落实科学发展观，再接再厉、锐意进取，为13亿人民谋取更多福祉，为中华民族赢得更大荣光。预祝大会圆满成功。黄色小说";
//        long start = System.currentTimeMillis();
//        result = filterHtml(str, '*');
//        long end = System.currentTimeMillis();
//        System.out.println("====filterHtml Time====" + (end - start));
//        System.out.println("original:" + result.getOriginalContent());
//        System.out.println("result:" + result.getFilteredContent());
//        System.out.println("badWords:" + result.getBadWords());
//        System.out.println("goodWords:" + result.getGoodWords());
//        System.out.println("level:" + result.getLevel());
//        System.out.println("hasSensiviWords:" + result.getHasSensiviWords());
//        start = System.currentTimeMillis();
//        result = filterTextWithPunctation(str, '*');
//        end = System.currentTimeMillis();
//        System.out.println("====filterTextWithPunctation Time====" + (end - start));
//        System.out.println("original:" + result.getOriginalContent());
//        System.out.println("result:" + result.getFilteredContent());
//        System.out.println("badWords:" + result.getBadWords());
//        System.out.println("goodWords:" + result.getGoodWords());
//        System.out.println("level:" + result.getLevel());
//        System.out.println("hasSensiviWords:" + result.getHasSensiviWords());
//        start = System.currentTimeMillis();
//        result = simpleFilter(str, '*');
//        end = System.currentTimeMillis();
//        System.out.println("====simpleFilter Time====" + (end - start));
//        System.out.println("original:" + result.getOriginalContent());
//        System.out.println("result:" + result.getFilteredContent());
//        System.out.println("badWords:" + result.getBadWords());
//        System.out.println("goodWords:" + result.getGoodWords());
//        System.out.println("level:" + result.getLevel());
//        System.out.println("hasSensiviWords:" + result.getHasSensiviWords());
//        start = System.currentTimeMillis();
//        result = simpleFilter("中国人民", '*');
//        end = System.currentTimeMillis();
//        System.out.println("====simpleFilter Time====" + (end - start));
//        System.out.println("original:" + result.getOriginalContent());
//        System.out.println("result:" + result.getFilteredContent());
//        System.out.println("badWords:" + result.getBadWords());
//        System.out.println("goodWords:" + result.getGoodWords());
//        System.out.println("level:" + result.getLevel());
//        System.out.println("hasSensiviWords:" + result.getHasSensiviWords());


//        WordsFilterUtil.addWords(WordsType.SENSITIVE, new ByteArrayInputStream("阴毛=1".getBytes()));

        FilteredResult strFilter3 = WordsFilterUtil.filterTextWithPunctation("江阴毛纺厂厂长的阴毛是", '*');
        System.out.println("4->FilteredContent>>> " + strFilter3.getFilteredContent());
    }

    static {
        InputStream is = WordsFilterUtil.class.getResourceAsStream("/sensitive-words.dict");
        if (is == null) {
            is = WordsFilterUtil.class.getClassLoader().getResourceAsStream("/sensitive-words.dict");
        }

        addWords(WordsType.SENSITIVE, is);

        String regex = "[\\pP\\pZ\\pS\\pM\\pC]";
        p = Pattern.compile(regex, 2);
        positiveTree = new Node();
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

        private PunctuationOrHtmlFilteredResult() {
        }

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
