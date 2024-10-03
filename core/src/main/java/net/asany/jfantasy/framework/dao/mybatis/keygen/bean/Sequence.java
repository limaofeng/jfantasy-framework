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
package net.asany.jfantasy.framework.dao.mybatis.keygen.bean;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Getter;

/**
 * 序列
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-8-24 上午10:35:57
 */
@Getter
@Entity
@Table(name = "SYS_SEQUENCE")
public class Sequence implements Serializable {
  /** 序列名称 */
  @Id
  @Column(name = "GEN_NAME")
  private String key;

  /** 序列值 */
  @Column(name = "GEN_VALUE", nullable = false)
  private Long value = 0L;

  /** 原始值 */
  @Transient private Long originalValue;

  public Sequence() {}

  public Sequence(String key, long poolSize) {
    this.key = key;
    this.value = poolSize;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setValue(Long value) {
    this.value = value;
  }

  public void setOriginalValue(Long originalValue) {
    this.originalValue = originalValue;
  }
}
