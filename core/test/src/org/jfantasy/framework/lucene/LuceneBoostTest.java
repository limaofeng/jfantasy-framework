package org.jfantasy.framework.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneBoostTest {
    /**
     * 设置DOC boost 值影响查询排序结果
     *
     * @throws Exception
     */
    @Test
    public void testBoost1() throws Exception {
        System.out.println("设置DOC boost 值影响查询排序结果");
        RAMDirectory ramDir = new RAMDirectory();
        Analyzer analyzer = new IKAnalyzer();
        IndexWriter iw = new IndexWriter(ramDir, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);

        String[] nameList = {"you are my friend", "a are my wife", "I love you"};
        String[] addList = {"b", "you are my wife", "c"};
        String[] fileList = {"1,2", "1", "2"};

        for (int i = 0; i < nameList.length; i++) {
            Document doc = new Document();
            doc.add(new Field("name", nameList[i], Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("file", fileList[i], Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("address", addList[i], Field.Store.YES, Field.Index.ANALYZED));
            if (i == 2) {
                doc.setBoost(10002.1f);
            }
//          这里设置了第三个文档优先级最高，所以在搜索出来的结果中，该文档排在最前
            iw.addDocument(doc);
        }
        iw.close();

        IndexSearcher _searcher = new IndexSearcher(ramDir);
        String[] fields = new String[]{"name", "address"};
        //new WildcardQuery(new Term("name", "*" + "you" + "*"))
        Query query = new TermQuery(new Term("file", "2"));//IKQueryParser.parseMultiField(fields, "you");

        TopDocs topDocs = _searcher.search(query, _searcher.maxDoc());
        ScoreDoc[] hits = topDocs.scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            System.out.println(hits[i].score);

            Document doc = _searcher.doc(hits[i].doc);
            System.out.println("name:" + doc.get("name"));
            System.out.println("file:" + doc.get("file"));
        }
        _searcher.close();

    }


    /**
     * 设置query boost值影响排序结果,如果有排序sort，则完全按照sort结果进行
     *
     * @throws Exception
     */
    @Test
    public void testBoost2() throws Exception {
        System.out.println("设置query boost值影响排序结果");
        RAMDirectory ramDir = new RAMDirectory();
        Analyzer analyzer = new IKAnalyzer();
        IndexWriter iw = new IndexWriter(ramDir, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);

        String[] nameList = {"you are my friend", "a are my wife", "I love you"};
        String[] addList = {"b", "you are my wife", "c"};
        String[] fileList = {"1", "2", "3"};

        for (int i = 0; i < nameList.length; i++) {
            Document doc = new Document();
            doc.add(new Field("name", nameList[i], Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("file", fileList[i], Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("address", addList[i], Field.Store.YES, Field.Index.ANALYZED));
            iw.addDocument(doc);
        }
        iw.close();
        IndexSearcher _searcher = new IndexSearcher(ramDir);

        BooleanQuery bq = new BooleanQuery();
        QueryParser _parser = new QueryParser(Version.LUCENE_36, "name", analyzer);
        Query _query = _parser.parse("you");
        _query.setBoost(2f);

        QueryParser _parser1 = new QueryParser(Version.LUCENE_36, "address", analyzer);
        Query _query1 = _parser1.parse("you");
        _query1.setBoost(1f);

        bq.add(_query, BooleanClause.Occur.SHOULD);
        bq.add(_query1, BooleanClause.Occur.SHOULD);
//

//          for(int i=0;i<2;i++){
//              QueryParser parser = new MultiFieldQueryParser(Version.LUCENE_30,new String[] {"name", "address" }, analyzer);
//              Query q1 = parser.parse("you");
//              bq.add(q1, BooleanClause.Occur.MUST);
//          }
//
//         SortField[] sortFields = new SortField[1];
//         SortField sortField = new SortField("file", SortField.INT, true);//false升序，true降序
//         sortFields[0] = sortField;
//         Sort sort = new Sort(sortFields);
//         TopDocs topDocs = _searcher.search(bq,null,_searcher.maxDoc(),sort);
//

        TopDocs topDocs = _searcher.search(bq, _searcher.maxDoc());
        ScoreDoc[] hits = topDocs.scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            Document doc = _searcher.doc(hits[i].doc);
            System.out.println("name:" + doc.get("name"));
            System.out.println("file:" + doc.get("file"));
        }
        _searcher.close();

    }


    @Test
    public void reload() throws Exception {
        System.out.println("设置DOC boost 值影响查询排序结果");
        RAMDirectory ramDir = new RAMDirectory();
        Analyzer analyzer = new IKAnalyzer();
        IndexWriter iw = new IndexWriter(ramDir, analyzer, true, IndexWriter.MaxFieldLength.LIMITED);

        iw.commit();

        IndexSearcher _searcher = new IndexSearcher(ramDir);
        String[] fields = new String[]{"name", "address"};
        //new WildcardQuery(new Term("name", "*" + "you" + "*"))
        Query query = new TermQuery(new Term("file", "2"));//IKQueryParser.parseMultiField(fields, "you");

        TopDocs topDocs = _searcher.search(query, 1);
        ScoreDoc[] hits = topDocs.scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            System.out.println(hits[i].score);

            Document doc = _searcher.doc(hits[i].doc);
            System.out.println("name:" + doc.get("name"));
            System.out.println("file:" + doc.get("file"));
        }
//        _searcher.close();

        iw.deleteAll();

        String[] nameList = {"you are my friend", "a are my wife", "I love you"};
        String[] addList = {"b", "you are my wife", "c"};
        String[] fileList = {"1,2", "1", "2"};

        for (int i = 0; i < nameList.length; i++) {
            Document doc = new Document();
            doc.add(new Field("name", nameList[i], Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("file", fileList[i], Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("address", addList[i], Field.Store.YES, Field.Index.ANALYZED));
            if (i == 2) {
                doc.setBoost(10002.1f);
            }
//            这里设置了第三个文档优先级最高，所以在搜索出来的结果中，该文档排在最前
            iw.addDocument(doc);
        }

        iw.commit();

        System.out.println("=========删除索引后");

//        _searcher = new IndexSearcher(ramDir);
        fields = new String[]{"name", "address"};
        //new WildcardQuery(new Term("name", "*" + "you" + "*"))
        query = new TermQuery(new Term("file", "2"));//IKQueryParser.parseMultiField(fields, "you");

        topDocs = _searcher.search(query, _searcher.maxDoc());
        hits = topDocs.scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            System.out.println(hits[i].score);

            Document doc = _searcher.doc(hits[i].doc);
            System.out.println("name:" + doc.get("name"));
            System.out.println("file:" + doc.get("file"));
        }

    }

}