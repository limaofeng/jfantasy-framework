package org.jfantasy.autoconfigure;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan("org.jfantasy.member")
@Configuration
@EntityScan("org.jfantasy.member.bean")
public class MemberAutoConfiguration {

}
