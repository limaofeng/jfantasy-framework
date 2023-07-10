package org.jfantasy.graphql.util;

import org.jfantasy.framework.security.core.userdetails.UserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

class KitTest {

  @Test
  void connection() {
    Kit.typeName(new Object());

    Page<UserDetails> pagePage = Page.empty();

    UserConnection connection =
        Kit.connection(
            pagePage,
            UserConnection.class,
            (item) -> {
              return UserConnection.UserDetailsEdge.builder()
                  .cursor(item.getPassword())
                  .node(item.getUsername())
                  .build();
            });
  }
}
