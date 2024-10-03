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
