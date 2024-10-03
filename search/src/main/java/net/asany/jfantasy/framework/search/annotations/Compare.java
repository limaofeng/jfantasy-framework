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
package net.asany.jfantasy.framework.search.annotations;

/**
 * compare有多个枚举值
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-23 下午03:11:40
 */
public enum Compare {
  /** 等于。支持String、boolean、int、long、float、double、char。 */
  IS_EQUALS,
  /** 不等于。支持String、boolean、int、long、float、double、char。 */
  NOT_EQUALS,
  /** 大于。支持int、long、float、double。 */
  GREATER_THAN,
  /** 大于等于。支持int、long、float、double。 */
  GREATER_THAN_EQUALS,
  /** 小于。支持int、long、float、double。 */
  LESS_THAN,
  /** 小于等于。支持int、long、float、double。 */
  LESS_THAN_EQUALS,
  /** 为空。支持Object类型，包括String。这时不需要value参数。 */
  IS_NULL,
  /** 不为空。支持Object类型，包括String。这时不需要value参数。 */
  NOT_NULL
}
