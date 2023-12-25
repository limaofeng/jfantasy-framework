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
