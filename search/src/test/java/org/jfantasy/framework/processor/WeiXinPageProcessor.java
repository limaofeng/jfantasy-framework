package org.jfantasy.framework.processor;

import cn.asany.demo.bean.Article;
import cn.asany.demo.service.ArticleService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

public class WeiXinPageProcessor implements PageProcessor {

  public static final String URL = "https://mp.weixin.qq.com";

  private static final String BIZ = "MjM5NzMyMjAwMA==";

  private static final String COOKIES =
      "devicetype=iOS15.4.1; lang=zh_CN; pass_ticket=vkMnSrWh6FRBbIQSywJ4a6kKQT49pX3B31P+yDKuBAiQCpJw4scgiQtczZAy0JC9; version=18001435; wap_sid2=CJ+LiacFEooBeV9ISkRxbmJreFhjZnl4TGdHTTlpUktwTFAxTWJwRklBWUU4Y2ppbDQzTWFWajhwWERSVkRoUkNGNGlFUklUNnBoVnpHdFd6eTc1YWZKN2RrZndxalZuTWgxNGRIVGJCVENxVTEyQmlpVFlXX05Tb0tMelhSVC1CNlBDWHo0MUVkV2FXc1NBQUF+MKKyjZQGOA1AlU4=; wxuin=1424115103; wxtokenkey=777; appmsg_token=1165_%2FFPNV0WLbmVWldgQ4O5mxYilJaCIkM-6RxVDvXjuYDGEJ0U7y_kkECPqXM1AT5yHj8JBAXs6jcDEtr9Q; rewardsn=";

  public WeiXinPageProcessor(ArticleService articleService) {
    this.articleService = articleService;
  }

  @Override
  public void process(Page page) {
    page.addTargetRequests(
        page.getHtml().links().regex("(http://mp\\.weixin\\.qq\\.com\\/s\\?__biz=.*)").all());

    String title = page.getHtml().xpath("//*[@id=\"activity-name\"]/text()").toString();
    if (title == null) {
      page.setSkip(true);
      return;
    }
    String author = page.getHtml().xpath("//*[@id=\"profileBt\"]/a/text()").toString().trim();
    String context = page.getHtml().xpath("//*[@id=\"js_content\"]/section[2]").toString().trim();

    String url = page.getUrl().get();

    doArticle(url, title, author, context);
  }

  private void doArticle(String url, String title, String author, String context) {
    try {
      this.articleService.save(
          Article.builder().url(url).author(author).title(title).content(context).build());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public Site getSite() {
    return Site.me()
        .setRetryTimes(3)
        .setSleepTime(10)
        .setTimeOut(15000)
        .addHeader("Accept-Encoding", "identity")
        .setUserAgent(
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_4_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.20(0x18001435) NetType/WIFI Language/zh_CN");
  }

  private final ArticleService articleService;

  @BeforeEach
  void setUp() throws InterruptedException {
    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
  }

  @AfterEach
  void tearDown() throws InterruptedException {
    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
  }
}
