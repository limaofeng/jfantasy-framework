package cn.asany.example.demo.web;

import cn.asany.example.demo.domain.User;
import cn.asany.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.BeanFilter;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2020/3/21 11:18 下午
 */
@RestController
public class UserController {

  private final MessageSource messageSource;

  @Autowired private UserService userService;

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
        excludes = {"createdAt", "createdBy"}),
  })
  public Mono<Page<User>> users() {
    return Mono.just(userService.findPage(Pageable.ofSize(10), PropertyFilter.newFilter()));
  }

  @PostMapping("/users")
  public Mono<ResponseEntity<String>> addUser(@Valid @RequestBody User user) {
    // persisting the user
    return Mono.just(ResponseEntity.ok("User is valid"));
  }
}
