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
