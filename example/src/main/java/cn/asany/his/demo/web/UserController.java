package cn.asany.his.demo.web;

import cn.asany.his.demo.bean.User;
import org.jfantasy.framework.error.ErrorUtils;
import org.jfantasy.framework.security.core.SecurityMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2020/3/21 11:18 下午
 */
@RestController
public class UserController {

    @Autowired
    private MessageSource messageSource;

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
