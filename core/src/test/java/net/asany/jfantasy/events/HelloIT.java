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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:testconfig/spring/applicationContext.xml"})
public class HelloIT {

  @Autowired private ApplicationContext applicationContext;

  @Test
  public void testPublishEvent() {
    applicationContext.publishEvent(new ContentEvent("今年是龙年的博客更新了"));
    /*
     * User user = new User(); user.setUsername("测试用户名");
     * applicationContext.publishEvent(new RegisterEvent(user));
     * applicationContext.publishEvent(new ContentOrderEvent(new Order() {
     *
     * @Override public String getSN() { return "123456"; }
     *
     * @Override public String getType() { return "test"; }
     *
     * @Override public String getSubject() { return "subject"; }
     *
     * @Override public BigDecimal getTotalFee() { return BigDecimal.ONE; }
     *
     * @Override public BigDecimal getPayableFee() { return BigDecimal.ONE; }
     *
     * @Override public boolean isPayment() { return false; }
     *
     * @Override public List<OrderItem> getOrderItems() { return new
     * ArrayList<OrderItem>(); }
     *
     * @Override public ShipAddress getShipAddress() { return new ShipAddress(); }
     * }));
     */
  }
}
