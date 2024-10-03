/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
