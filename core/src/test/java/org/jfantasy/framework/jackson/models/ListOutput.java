package org.jfantasy.framework.jackson.models;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.util.List;
import lombok.Data;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-03-07 10:43
 */
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
