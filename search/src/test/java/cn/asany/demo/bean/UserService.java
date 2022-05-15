package cn.asany.demo.bean;

import java.util.ArrayList;
import java.util.List;
import org.jfantasy.framework.search.backend.EntityChangedListener;
import org.jfantasy.framework.search.dao.DataFetcher;

public class UserService implements DataFetcher {

  public List<User> users = new ArrayList<>();

  //    EntityChangedListener changedListener = new EntityChangedListener(User.class);

  UserService() {
    users.add(User.builder().id(1L).name("limaofeng").age(38).build());
    users.add(User.builder().id(2L).name("huangli").age(34).build());
    users.add(User.builder().id(3L).name("likaixin").age(6).build());
    users.add(User.builder().id(4L).name("litianle").age(3).build());
  }

  @Override
  public long count() {
    return users.size();
  }

  @Override
  public <T> List<T> find(int start, int size) {
    return (List<T>) users.subList(start, Math.min(users.size(), start + size));
  }

  @Override
  public <T> List<T> findByField(String fieldName, String fieldValue) {
    return null;
  }

  @Override
  public <T> T getById(String id) {
    return (T)
        users.stream().filter(item -> item.getId().equals(Long.valueOf(id))).findAny().orElse(null);
  }

  @Override
  public EntityChangedListener getListener() {
    return null;
  }
}
