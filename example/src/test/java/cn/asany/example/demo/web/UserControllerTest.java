package cn.asany.example.demo.web;

import cn.asany.example.demo.domain.User;
import cn.asany.example.demo.domain.UserSetting;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.FilteredMixinHolder;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;

@Slf4j
class UserControllerTest {

  @Test
  void users() {
    JSON.initialize();
    User user = User.builder().id(1L).username("123123").password("xxxx").build();

    Method method = ClassUtil.getMethod(UserController.class, "users");

    MethodParameter methodParameter = new MethodParameter(method, -1);

    FilterProvider filterProvider = FilteredMixinHolder.getFilterProvider(methodParameter);

    log.info(JSON.serialize(user, filterProvider));
  }

  @Test
  void user() {
    JSON.initialize();
    User user =
        User.builder()
            .id(1L)
            .username("123123")
            .password("xxxx")
            .setting(new UserSetting())
            .build();

    Method method = ClassUtil.getMethod(UserController.class, "user", Long.class);

    MethodParameter methodParameter = new MethodParameter(method, -1);

    FilterProvider filterProvider = FilteredMixinHolder.getFilterProvider(methodParameter);

    log.info(JSON.serialize(user, filterProvider));
  }
}
