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
package net.asany.jfantasy.framework.jackson;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import net.asany.jfantasy.framework.jackson.models.DefaultOutput;
import net.asany.jfantasy.framework.jackson.models.ListOutput;
import net.asany.jfantasy.framework.jackson.models.Output;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class XMLTest {

  @BeforeEach
  public void setUp() throws Exception {
    XML.initialize();
  }

  @Test
  public void xmlToJson() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><output><message><result>1</result><description>正常</description></message><data><informationChannel><channelId>312</channelId><channelName><![CDATA[通知通告]]></channelName><channelLevel>1</channelLevel><channelIdString>506250$312$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>322</channelId><channelName><![CDATA[培训通知]]></channelName><channelLevel>2</channelLevel><channelIdString>506250$312$_502500$322$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>324</channelId><channelName><![CDATA[人事通知]]></channelName><channelLevel>2</channelLevel><channelIdString>506250$312$_503750$324$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>323</channelId><channelName><![CDATA[考勤通知]]></channelName><channelLevel>2</channelLevel><channelIdString>506250$312$_505312$323$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>313</channelId><channelName><![CDATA[新闻中心]]></channelName><channelLevel>1</channelLevel><channelIdString>507500$313$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>325</channelId><channelName><![CDATA[制度规范]]></channelName><channelLevel>1</channelLevel><channelIdString>510312$325$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>326</channelId><channelName><![CDATA[管理制度]]></channelName><channelLevel>2</channelLevel><channelIdString>510312$325$_500000$326$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>316</channelId><channelName><![CDATA[产品信息]]></channelName><channelLevel>1</channelLevel><channelIdString>518750$316$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>1</isCanAdd></informationChannel><informationChannel><channelId>321</channelId><channelName><![CDATA[市场专栏]]></channelName><channelLevel>1</channelLevel><channelIdString>521250$321$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>314</channelId><channelName><![CDATA[项目经验技巧分享]]></channelName><channelLevel>1</channelLevel><channelIdString>523750$314$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>1</isCanAdd></informationChannel><informationChannel><channelId>315</channelId><channelName><![CDATA[培训实施心得]]></channelName><channelLevel>1</channelLevel><channelIdString>526250$315$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>1</isCanAdd></informationChannel><informationChannel><channelId>317</channelId><channelName><![CDATA[会议纪要]]></channelName><channelLevel>1</channelLevel><channelIdString>528750$317$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>320</channelId><channelName><![CDATA[管理会议纪要]]></channelName><channelLevel>2</channelLevel><channelIdString>528750$317$_490000$320$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>318</channelId><channelName><![CDATA[项目部会议纪要]]></channelName><channelLevel>2</channelLevel><channelIdString>528750$317$_500000$318$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>319</channelId><channelName><![CDATA[售后部会议纪要]]></channelName><channelLevel>2</channelLevel><channelIdString>528750$317$_510000$319$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><recordCount>15</recordCount></data></output>";

    Output out = XML.deserialize(xml, ListOutput.class);

    assertNotNull(out);

    out = XML.deserialize(xml, DefaultOutput.class);

    assertNotNull(out);
  }
}
