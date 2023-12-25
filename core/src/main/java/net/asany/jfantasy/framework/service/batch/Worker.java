package net.asany.jfantasy.framework.service.batch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.concurrent.LinkedQueue;

/**
 * 工人
 *
 * @author limaofeng
 */
@Slf4j
public class Worker<T, R> implements Runnable {

  private String state = "ready";
  private final LinkedQueue<Cargo<T, R>> queue = new LinkedQueue<>();
  private int batchSize;
  private final Function<List<T>, List<R>> saver;

  private Thread thread;
  private final String actionName;

  public Worker(String actionName, Function<List<T>, List<R>> saver, int batchSize) {
    this.actionName = actionName;
    this.saver = saver;
    this.batchSize = batchSize;
  }

  public CompletableFuture<R> add(T o) {
    Cargo<T, R> item = Cargo.of(o);
    queue.add(item);
    return item.getHearthstone();
  }

  public List<Cargo<T, R>> getItems() {
    try {
      Cargo<T, R> item = queue.take();
      List<Cargo<T, R>> items = new ArrayList<>(batchSize);
      items.add(item);
      item = queue.poll();
      while (item != null) {
        items.add(item);

        if (items.size() >= batchSize) {
          return items;
        }

        item = queue.poll();
      }
      return items;
    } catch (InterruptedException e) {
      log.error("thread " + thread.getName() + " interrupted");
      return new ArrayList<>();
    }
  }

  @Override
  public void run() {
    this.thread = Thread.currentThread();
    //noinspection AlibabaUndefineMagicConstant
    while (!"shutdown".equals(state)) {
      List<Cargo<T, R>> items = getItems();
      if (items.isEmpty()) {
        continue;
      }
      save(items);
    }
  }

  private void save(List<Cargo<T, R>> items) {
    long start = System.currentTimeMillis();
    try {
      List<R> results =
          saver.apply(items.stream().map(Cargo::getContent).collect(Collectors.toList()));
      if (results == null) {
        items.forEach(item -> item.getHearthstone().complete(null));
      } else {
        Iterator<R> resultIterator = results.iterator();
        Iterator<Cargo<T, R>> cargoIterator = items.iterator();
        while (resultIterator.hasNext()) {
          cargoIterator.next().getHearthstone().complete(resultIterator.next());
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      items.forEach(item -> item.getHearthstone().obtrudeException(e));
    } finally {
      long end = System.currentTimeMillis();
      log.info(
          "执行批量操作:"
              + actionName
              + ",数量:"
              + items.size()
              + ",耗时:"
              + (end - start)
              + "毫秒,批次大小:"
              + batchSize
              + "条"
              + ",队列大小:"
              + queue.size());
    }
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public String getState() {
    return state;
  }

  public void shutdown() {
    this.state = "shutdown";
    while (!queue.isEmpty()) {
      try {
        //noinspection BusyWait
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        log.error(e.getMessage(), e);
      }
    }
    thread.interrupt();
  }
}
