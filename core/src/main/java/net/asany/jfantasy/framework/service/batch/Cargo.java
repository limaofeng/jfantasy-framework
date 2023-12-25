package net.asany.jfantasy.framework.service.batch;

import java.util.concurrent.CompletableFuture;
import lombok.Getter;

@Getter
public class Cargo<T, R> {
  private final CompletableFuture<R> hearthstone = new CompletableFuture<>();
  private final T content;

  private Cargo(T o) {
    this.content = o;
  }

  public static <T, R> Cargo<T, R> of(T o) {
    return new Cargo<>(o);
  }
}
