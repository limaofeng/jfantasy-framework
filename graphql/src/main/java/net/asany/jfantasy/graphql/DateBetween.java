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
package net.asany.jfantasy.graphql;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author liumeng @Description: (这里用一句话描述这个类的作用)
 * @date 9:23 2019-11-14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DateBetween implements Serializable {
  private Date startTime;
  private Date endTime;

  public static DateBetween newDateBetween(Date startTime, Date endTime) {
    return new DateBetween(startTime, endTime);
  }
}
