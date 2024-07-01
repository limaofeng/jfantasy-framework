package net.asany.jfantasy.framework.security.authorization.policy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermissionPolicyTest {

  @BeforeEach
  void setUp() {}

  @AfterEach
  void tearDown() {}

  @Test
  void subjectMatchesPattern() {
    assertTrue(PermissionPolicy.subjectMatchesPattern("role:admin", "role:*"));
    assertTrue(PermissionPolicy.subjectMatchesPattern("role:admin", "role:admin"));
  }
}
