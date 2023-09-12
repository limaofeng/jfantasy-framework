package org.jfantasy.framework.dao.jpa;

import java.io.Serializable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface ReactiveAnyJpaRepository<T, PK extends Serializable>
    extends ReactiveCrudRepository<T, PK>, ReactiveSortingRepository<T, PK> {}
