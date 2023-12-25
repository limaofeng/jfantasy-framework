package net.asany.jfantasy.framework.search;

import net.asany.jfantasy.framework.search.backend.IndexRebuildTask;
import org.springframework.core.task.TaskExecutor;

public class IndexRebuilder {
  private final Class<?> clazz;
  private final TaskExecutor executor;
  private int batchSize;

  public IndexRebuilder(Class<?> clazz, TaskExecutor executor, int batchSize) {
    this.clazz = clazz;
    this.executor = executor;
    this.batchSize = batchSize;
  }

  public void rebuild() {
    IndexRebuildTask task = new IndexRebuildTask(this.clazz, this.batchSize);
    this.executor.execute(task);
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }
}
