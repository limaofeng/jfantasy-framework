package org.jfantasy.framework.lucene;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;
import org.jfantasy.framework.lucene.exception.PropertyException;

public class BuguHighlighter {
	private String keywords;
	private String[] fields;
	private Formatter formatter = new SimpleHTMLFormatter("<font color=\"#FF0000\">", "</font>");
	private int maxFragments = 3;

	public BuguHighlighter() {
	}

	public BuguHighlighter(String field, String keywords) {
		this.fields = new String[] { field };
		this.keywords = keywords;
	}

	public BuguHighlighter(String[] fields, String keywords) {
		this.fields = fields;
		this.keywords = keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String[] getFields() {
		return this.fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	public void setMaxFragments(int maxFragments) {
		this.maxFragments = maxFragments;
	}

	public String getResult(String fieldName, String fieldValue) {
		BuguIndex index = BuguIndex.getInstance();
		QueryParser parser = new QueryParser(index.getVersion(), fieldName, index.getAnalyzer());
		try {
			Query query = parser.parse(this.keywords);
			TokenStream tokens = index.getAnalyzer().tokenStream(fieldName, new StringReader(fieldValue));
			QueryScorer scorer = new QueryScorer(query, fieldName);
			Highlighter highlighter = new Highlighter(this.formatter, scorer);
			highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer));
			return highlighter.getBestFragments(tokens, fieldValue, this.maxFragments, "...");
		}catch (ParseException | IOException | InvalidTokenOffsetsException e){
			throw new ResultParseException(e);
		}
	}

	class ResultParseException extends RuntimeException{

        public ResultParseException(Exception e) {
            super(e);
        }
    }
}
