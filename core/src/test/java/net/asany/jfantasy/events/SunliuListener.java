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
package net.asany.jfantasy.events;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SunliuListener implements SmartApplicationListener {

  @Override
  public boolean supportsEventType(final Class<? extends ApplicationEvent> eventType) {
    return eventType == ContentEvent.class;
  }

  @Override
  public boolean supportsSourceType(final Class<?> sourceType) {
    return sourceType == String.class;
  }

  @Override
  public void onApplicationEvent(final ApplicationEvent event) {
    System.out.println("孙六在王五之后收到新的内容：" + event.getSource());
    throw new RuntimeException("测试异常");
  }

  @Override
  public int getOrder() {
    return 2;
  }
}
