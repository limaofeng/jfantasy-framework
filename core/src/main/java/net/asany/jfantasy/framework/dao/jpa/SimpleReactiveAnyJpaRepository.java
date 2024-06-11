package net.asany.jfantasy.framework.dao.jpa;

import jakarta.persistence.EntityManager;
import java.io.Serializable;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Reactive JPA Repository
 *
 * @param <T> 实体
 * @param <ID> 主键
 */
public class SimpleReactiveAnyJpaRepository<T, ID extends Serializable>
    implements ReactiveAnyJpaRepository<T, ID> {

  private final SimpleAnyJpaRepository<T, ID> anyJpaRepository;

  private final Scheduler elastic = Schedulers.newBoundedElastic(10, 100, "tenant-service");

  public SimpleReactiveAnyJpaRepository(Class<T> domainClass, EntityManager entityManager) {
    anyJpaRepository = new SimpleAnyJpaRepository<>(domainClass, entityManager);
  }

  @Override
  public <S extends T> @NotNull Mono<S> save(@NotNull S entity) {
    return Mono.fromCallable(() -> anyJpaRepository.save(entity)).subscribeOn(elastic);
  }

  @Override
  public <S extends T> @NotNull Flux<S> saveAll(@NotNull Iterable<S> entities) {
    return null;
  }

  @Override
  public <S extends T> @NotNull Flux<S> saveAll(@NotNull Publisher<S> entityStream) {
    return null;
  }

  @Override
  public @NotNull Mono<T> findById(@NotNull ID id) {
    return Mono.fromCallable(() -> anyJpaRepository.findById(id).orElse(null)).subscribeOn(elastic);
  }

  @Override
  public @NotNull Mono<T> findById(@NotNull Publisher<ID> id) {
    return null;
  }

  @Override
  public @NotNull Mono<Boolean> existsById(@NotNull ID id) {
    return Mono.fromCallable(() -> anyJpaRepository.existsById(id)).subscribeOn(elastic);
  }

  @Override
  public @NotNull Mono<Boolean> existsById(@NotNull Publisher<ID> id) {
    return null;
  }

  @Override
  public @NotNull Flux<T> findAll() {
    return null;
  }

  @Override
  public @NotNull Flux<T> findAllById(@NotNull Iterable<ID> ids) {
    return null;
  }

  @Override
  public @NotNull Flux<T> findAllById(@NotNull Publisher<ID> idStream) {
    return null;
  }

  @Override
  public @NotNull Mono<Long> count() {
    return null;
  }

  @Override
  public @NotNull Mono<Void> deleteById(@NotNull ID id) {
    return null;
  }

  @Override
  public @NotNull Mono<Void> deleteById(@NotNull Publisher<ID> id) {
    return null;
  }

  @Override
  public @NotNull Mono<Void> delete(@NotNull T entity) {
    return null;
  }

  @Override
  public @NotNull Mono<Void> deleteAllById(@NotNull Iterable<? extends ID> ids) {
    return null;
  }

  @Override
  public @NotNull Mono<Void> deleteAll(@NotNull Iterable<? extends T> entities) {
    return null;
  }

  @Override
  public @NotNull Mono<Void> deleteAll(@NotNull Publisher<? extends T> entityStream) {
    return null;
  }

  @Override
  public @NotNull Mono<Void> deleteAll() {
    return null;
  }

  @Override
  public @NotNull Flux<T> findAll(@NotNull Sort sort) {
    return null;
  }
}
