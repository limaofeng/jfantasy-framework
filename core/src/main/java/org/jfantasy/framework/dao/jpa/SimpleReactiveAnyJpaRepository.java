package org.jfantasy.framework.dao.jpa;

import jakarta.persistence.EntityManager;
import java.io.Serializable;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * TODO: 还不成熟, ReactiveCrudRepository 接口定义有问题, 比如：Flux<T> findAll(); 应该是 Flux<List<T>> findAll();
 * 才合理
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
  public <S extends T> Mono<S> save(S entity) {
    return Mono.fromCallable(() -> anyJpaRepository.save(entity)).subscribeOn(elastic);
  }

  @Override
  public <S extends T> Flux<S> saveAll(Iterable<S> entities) {
    return null;
  }

  @Override
  public <S extends T> Flux<S> saveAll(Publisher<S> entityStream) {
    return null;
  }

  @Override
  public Mono<T> findById(ID id) {
    return Mono.fromCallable(() -> anyJpaRepository.findById(id).orElse(null)).subscribeOn(elastic);
  }

  @Override
  public Mono<T> findById(Publisher<ID> id) {
    return null;
  }

  @Override
  public Mono<Boolean> existsById(ID id) {
    return Mono.fromCallable(() -> anyJpaRepository.existsById(id)).subscribeOn(elastic);
  }

  @Override
  public Mono<Boolean> existsById(Publisher<ID> id) {
    return null;
  }

  @Override
  public Flux<T> findAll() {
    return null;
  }

  @Override
  public Flux<T> findAllById(Iterable<ID> ids) {
    return null;
  }

  @Override
  public Flux<T> findAllById(Publisher<ID> idStream) {
    return null;
  }

  @Override
  public Mono<Long> count() {
    return null;
  }

  @Override
  public Mono<Void> deleteById(ID id) {
    return null;
  }

  @Override
  public Mono<Void> deleteById(Publisher<ID> id) {
    return null;
  }

  @Override
  public Mono<Void> delete(T entity) {
    return null;
  }

  @Override
  public Mono<Void> deleteAllById(Iterable<? extends ID> ids) {
    return null;
  }

  @Override
  public Mono<Void> deleteAll(Iterable<? extends T> entities) {
    return null;
  }

  @Override
  public Mono<Void> deleteAll(Publisher<? extends T> entityStream) {
    return null;
  }

  @Override
  public Mono<Void> deleteAll() {
    return null;
  }

  @Override
  public Flux<T> findAll(Sort sort) {
    return null;
  }
}
