package org.jfantasy.framework.search.elastic;

import java.io.IOException;
import java.io.Serializable;
import org.jfantasy.framework.search.Document;

/**
 * 索引 Writer
 *
 * @author limaofeng
 */
public interface IndexWriter {

  /**
   * 提交
   *
   * @throws IOException 异常
   */
  void commit() throws IOException;

  /**
   * 删除所有
   *
   * @throws IOException 异常
   */
  void deleteAll() throws IOException;

  /**
   * 添加文档
   *
   * @param doc Document
   * @throws IOException 异常
   */
  void addDocument(Document doc) throws IOException;

  /**
   * 更新文档
   *
   * @param id 文档ID
   * @param doc 文档
   * @throws IOException 异常
   */
  void updateDocument(Serializable id, Document doc) throws IOException;

  /**
   * 删除文档
   *
   * @param id 文档ID
   * @throws IOException 异常
   */
  void deleteDocument(Serializable id) throws IOException;
}
