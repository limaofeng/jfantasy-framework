package org.jfantasy.framework.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.jfantasy.framework.util.common.DateUtil;
import org.junit.Test;

import java.util.Date;

public class LuceneSortTest {

    /**
     * 设置DOC boost 值影响查询排序结果
     *
     * @throws Exception
     */
    @Test
    public void testSort() throws Exception {
        System.out.println("日期排序");
        RAMDirectory ramDir = new RAMDirectory();
        Analyzer analyzer = null; //new IKAnalyzer();
        IndexWriter iw = new IndexWriter(ramDir, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);

        String[] nameList = {"you are my friend", "a are my wife", "I love you . m"};
        String[] addList = {"b", "you are my wife", "c"};
        Date[] dateList = {DateUtil.parse("2016-08-12","yyyyy-MM-dd"), DateUtil.parse("2016-06-12","yyyyy-MM-dd"), DateUtil.parse("2016-07-12","yyyyy-MM-dd")};

        for (int i = 0; i < nameList.length; i++) {
            Document doc = new Document();
            doc.add(new Field("name", nameList[i], Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new NumericField("date",  Field.Store.YES ,  true).setLongValue(dateList[i].getTime()));
            doc.add(new Field("address", addList[i], Field.Store.YES, Field.Index.ANALYZED));
            iw.addDocument(doc);
        }
        iw.close();

        IndexSearcher _searcher = new IndexSearcher(ramDir);
        Query query = BuguParser.parseTermPrefix("name","m");

        TopDocs topDocs = _searcher.search(query, _searcher.maxDoc(), new Sort(new SortField("date",SortField.LONG,true)));
        ScoreDoc[] hits = topDocs.scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            Document doc = _searcher.doc(hits[i].doc);
            System.out.println(doc.get("name") + " - " + DateUtil.format(new Date(Long.valueOf(doc.get("date"))),"yyyy-MM-dd") + "-" + hits[i].score);
        }
        _searcher.close();

    }

}
