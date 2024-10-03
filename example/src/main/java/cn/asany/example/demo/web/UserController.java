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
import cn.asany.example.demo.service.UserService;
import jakarta.validation.Valid;
import net.asany.jfantasy.framework.dao.jpa.WebPropertyFilter;
import net.asany.jfantasy.framework.error.ValidationException;
import net.asany.jfantasy.framework.jackson.annotation.BeanFilter;
import net.asany.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * 用户接口
 *
 * @author limaofeng
 * @version V1.0
 */
@RestController
public class UserController {

  private final MessageSource messageSource;

  @Autowired private UserService userService;

  private final Scheduler elastic = Schedulers.newBoundedElastic(10, 100, "tenant-service");

  public UserController(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @GetMapping("/")
  @ResponseBody
  public Mono<ResponseEntity<String>> index() {
    return Mono.just(ResponseEntity.ok("你好,陌生人"));
  }

  @GetMapping("/users")
  @ResponseBody
  @JsonResultFilter({
    @BeanFilter(
        type = User.class,
        excludes = {"setting"}),
  })
  public Mono<Page<User>> users(WebPropertyFilter<User> filter) {
    return Mono.fromCallable(() -> userService.findPage(Pageable.ofSize(10), filter))
        .subscribeOn(elastic);
  }

  @GetMapping("/users/{id}")
  @ResponseBody
  @JsonResultFilter({
    @BeanFilter(
        type = User.class,
        excludes = {"setting"}),
  })
  public User user(@PathVariable Long id) {
    return userService.get(id).orElseThrow(() -> new ValidationException("user is null"));
  }

  @PostMapping("/users")
  public Mono<ResponseEntity<String>> addUser(@Valid @RequestBody User user) {
    // persisting the user
    return Mono.just(ResponseEntity.ok("User is valid"));
  }
}
