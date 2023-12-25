package net.asany.jfantasy.framework.util.common.toys;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 比较结果
 *
 * @author limaofeng
 */
@Data
@NoArgsConstructor
public class CompareResults<T> {
  /** B-A 多出的 */
  private List<T> exceptB = new ArrayList<>();

  /** 交集 */
  private List<T> intersect = new ArrayList<>();

  /** A - B 消失的 */
  private List<T> exceptA = new ArrayList<>();
}
