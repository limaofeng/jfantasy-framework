package cn.asany.example.demo.web;

import cn.asany.example.demo.domain.User;
import cn.asany.example.demo.service.UserService;
import javax.validation.Valid;
import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public String index() {
    return "你好,陌生人";
  }

  @GetMapping("/users")
  @ResponseBody
  public Page<User> users() {
    return userService.findPage(Pageable.ofSize(10), PropertyFilter.newFilter());
  }

  @PostMapping("/users")
  public ResponseEntity<String> addUser(@Valid @RequestBody User user) {
    // persisting the user
    return ResponseEntity.ok("User is valid");
  }
}
