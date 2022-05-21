package cn.asany.example.demo.web;

import cn.asany.example.demo.bean.User;
import javax.validation.Valid;
import org.jfantasy.framework.error.ErrorUtils;
import org.jfantasy.framework.security.core.SecurityMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
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

  public UserController(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @GetMapping("/")
  @ResponseBody
  public String index() {
    return "你好,陌生人";
  }

  @GetMapping("/users")
  public String users() {
    User user = User.builder().build();

    ErrorUtils.validate(user);

    MessageSourceAccessor accessor = SecurityMessageSource.getAccessor();
    return accessor.getMessage("ax", "12313");
  }

  @PostMapping("/users")
  public ResponseEntity<String> addUser(@Valid @RequestBody User user) {
    // persisting the user
    return ResponseEntity.ok("User is valid");
  }
}
