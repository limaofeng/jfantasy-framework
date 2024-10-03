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
package net.asany.jfantasy.framework.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FTPServiceTest {

  private FTPService ftpService;

  @BeforeEach
  public void setUp() throws Exception {
    // ftpService = new FTPService();
    // ftpService.setSystemKey(FTPClientConfig.SYST_MACOS_PETER);
    // ftpService.setHostname("115.29.185.235");
    // ftpService.setUsername("root");
    // ftpService.setPassword("Li19881002");
  }

  @AfterEach
  public void tearDown() throws Exception {}

  @Test
  public void testListFiles() throws Exception {
    // FTPClient ftpClient = ftpService.login();
    // ftpService.closeConnection(ftpClient);
  }

  @Test
  public void testExist() throws Exception {}

  @Test
  public void testIsDir() throws Exception {}

  @Test
  public void testUploadFile() throws Exception {}

  @Test
  public void testUploadFolder() throws Exception {}

  @Test
  public void testDeleteRemoteFile() throws Exception {}

  @Test
  public void testDeleteRemoteFolder() throws Exception {}

  @Test
  public void testGetInputStream() throws Exception {}

  @Test
  public void testGetLastModified() throws Exception {}

  @Test
  public void testGetOutputStream() throws Exception {}
}
