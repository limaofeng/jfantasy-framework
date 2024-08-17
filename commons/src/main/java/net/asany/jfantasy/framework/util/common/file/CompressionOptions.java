package net.asany.jfantasy.framework.util.common.file;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompressionOptions {
  /** 编码 */
  @Builder.Default private String encoding = "utf-8";

  /** 注释 */
  @Builder.Default private String comment = "";
  //  /** 地址转换 */
  //  private  PathForward<T> forward;
  //
  //  public interface PathForward<T> {
  //    String exec(T file);
  //  }
}
