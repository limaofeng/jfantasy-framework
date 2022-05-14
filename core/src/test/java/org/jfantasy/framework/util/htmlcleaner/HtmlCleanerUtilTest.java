package org.jfantasy.framework.util.htmlcleaner;

import lombok.extern.slf4j.Slf4j;
import org.htmlcleaner.TagNode;
import org.jfantasy.framework.httpclient.HttpClientUtil;
import org.jfantasy.framework.httpclient.Request;
import org.jfantasy.framework.httpclient.Response;
import org.junit.jupiter.api.Test;

@Slf4j
public class HtmlCleanerUtilTest {

  @Test
  public void testFindTagNodes() throws Exception {}

  @Test
  public void testFindFristTagNode() throws Exception {}

  @Test
  public void testHtmlCleaner() throws Exception {}

  @Test
  public void testFindByAttValue() throws Exception {}

  @Test
  public void testEvaluateXPath() throws Exception {}

  @Test
  public void testGetBrowserCompactXmlSerializer() throws Exception {}

  @Test
  public void testGetAsString() throws Exception {
    Request request = new Request();
    request.addRequestHeader(
        "Cookie",
        "JSESSIONID=698C806A296F510824E5C26218BAC733; OASESSIONID=3EE2AC922CBF05273D24EE57F68CF565; LocLan=zh_CN; ezofficeDomainAccount=whir; ezofficeUserPortal=; ezofficePortal3=1; ezofficePortal59=1; ezofficePortal47=1; empLivingPhoto=; ezofficeUserName=sys; JSESSIONID=3EE2AC922CBF05273D24EE57F68CF565; OASESSIONID=3EE2AC922CBF05273D24EE57F68CF565");
    Response response =
        HttpClientUtil.doGet(
            "http://whir.f3322.net:7008/defaultroot/Information!view.action?informationId=375&informationType=1&userChannelName=信息管理&channelId=316&userDefine=0&channelType=0&gdType=infomation&checkdepart=&index=0&recordCount=42",
            request);
    String body = response.text("utf-8");
    log.debug(body);
    TagNode root = HtmlCleanerUtil.htmlCleaner(body);
    TagNode nodes =
        HtmlCleanerUtil.findFristTagNode(
            root, "//*[@id=\"info-view-body\"]/div[2]/div/div[2]/div/div[1]/div");
    log.debug(nodes.getName());
  }
}
