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
package net.asany.jfantasy.framework.crypto;

import org.junit.jupiter.api.Test;

public class GzipEncodeTest {
  @Test
  public void encode() throws Exception {
    String ins = "测试GZIP编码";

    byte[] b = GzipEncode.gzip(ins);

    DESPlus desPlus = new DESPlus();

    DESPlus desPlus2 = new DESPlus("wangchongan");
    String e2 = desPlus2.encrypt("13588888888");
    System.out.println(e2);
    String d2 = desPlus2.decrypt(e2);
    System.out.println(d2);

    System.out.println(GzipEncode.JM(GzipEncode.KL(new String(b))));

    System.out.println(new String(GzipEncode.jUnZip(b)));
  }
}
