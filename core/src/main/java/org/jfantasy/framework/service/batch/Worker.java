package org.jfantasy.framework.service.batch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.concurrent.LinkedQueue;

@Slf4j
public class Worker<T, R> implements Runnable {
  private final LinkedQueue<Cargo<T, R>> queue = new LinkedQueue<>();
  private final int batchSize;
  private final Function<List<T>, List<R>> saver;

  public Worker(Function<List<T>, List<R>> saver, int batchSize) {
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
      log.error(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  public void run() {
    do {
      List<Cargo<T, R>> items = getItems();
      save(items);
    } while (true);
  }

  private void save(List<Cargo<T, R>> items) {
    System.out.println("批量保存:" + items.size() + "\tbatchSize:" + batchSize);
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
    }
  }
}
