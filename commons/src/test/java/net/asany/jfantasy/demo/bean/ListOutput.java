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
package net.asany.jfantasy.demo.bean;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-03-07 10:43
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ListOutput extends Output<ListOutput.ChannelList> {

  @Data
  public static class ChannelList {

    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Information> informationChannel;

    private int recordCount;
  }

  @Data
  public static class Information {
    private Long channelId;
  }
}
