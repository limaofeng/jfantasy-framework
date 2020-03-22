package com.thuni.his.demo.web;

import com.thuni.his.demo.bean.User;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/users")
    public ResponseEntity<String> addUser(@Valid @RequestBody User user) {
        // persisting the user
        return ResponseEntity.ok("User is valid");
    }

}
