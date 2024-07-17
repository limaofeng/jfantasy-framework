package net.asany.jfantasy.framework.security.authorization.policy;

import java.util.List;
import java.util.Set;

public interface ResourceAction {

  String getId();

  String getDescription();

  ResourceActionType getType();

  List<String> getOperations();

  Set<String> getArn();
}
