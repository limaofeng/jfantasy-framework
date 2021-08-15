package org.jfantasy.framework.lucene.cluster;

import java.io.Serializable;

public class MessageFactory {
  private MessageFactory() {}

  public static EntityMessage createInsertMessage(Serializable entity) {
    EntityMessage message = new EntityMessage();
    message.setType(EntityMessage.TYPE_INSERT);
    message.setEntity(entity);
    return message;
  }

  public static EntityMessage createUpdateMessage(Serializable entity) {
    EntityMessage message = new EntityMessage();
    message.setType(EntityMessage.TYPE_UPDATE);
    message.setEntity(entity);
    return message;
  }

  public static ClassIdMessage createRemoveMessage(Class<?> clazz, String id) {
    ClassIdMessage message = new ClassIdMessage();
    message.setType(EntityMessage.TYPE_REMOVE);
    message.setClazz(clazz);
    message.setId(id);
    return message;
  }

  public static ClassIdMessage createRefByMessage(Class<?> clazz, String id) {
    ClassIdMessage message = new ClassIdMessage();
    message.setType(EntityMessage.TYPE_REF_BY);
    message.setClazz(clazz);
    message.setId(id);
    return message;
  }
}
