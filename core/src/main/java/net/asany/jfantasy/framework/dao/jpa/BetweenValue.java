package net.asany.jfantasy.framework.dao.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * BetweenValue
 *
 * @author limaofeng
 */
@Data
@AllArgsConstructor
public class BetweenValue<X, Y> {

  private X x;
  private Y y;
}
