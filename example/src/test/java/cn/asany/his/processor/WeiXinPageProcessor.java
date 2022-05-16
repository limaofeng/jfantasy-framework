package cn.asany.his.processor;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.Selectable;

@Slf4j
public class WeiXinPageProcessor implements PageProcessor {

  public static final String URL = "https://mp.weixin.qq.com";

  private static final String BIZ = "MzA4NDgzMjk3Ng==";

  private static final String TOKEN = "1085_xLw3V6iHo6a7nfDTT-RsU1MTBRdZgHxuTQXm1w~~";

  private static final String HISTORY_URL =
      "https://mp.weixin.qq.com/mp/profile_ext?action=getmsg&__biz="
          + BIZ
          + "&f=json&offset=890&count=10&appmsg_token="
          + TOKEN;

  private static final String COOKIES =
      "pgv_pvid=4938437714; ua_id=g9STgoMJlzsvh4uPAAAAACz0DM3OpWV6Kxhdfm44Z34=; pgv_pvi=28995584; openid2ticket_oCbgjwVNebMQ8lrGXiYZwNIV0QqY=nNj1nSSHeq/W4of4T0AvD6iEQ0S7Nh4jjtB5vD5s5gU=; mm_lang=zh_CN; pac_uid=0_7d9d0fcae8992; xid=4473e5b90b63665e022be2e6baa9eb18; openid2ticket_ovyym5KTEDS6NDFX0gk2ROp-ftPg=; iip=0; rewardsn=; wxtokenkey=777; devicetype=Windows10x64; version=6300002f; lang=zh_CN; pgv_si=s8002643968; uuid=4c2e7e76ce8762394b1c0451996f1b6c; pgv_info=ssid=s4832459656; tvfe_boss_uuid=86f391240fda4c98; wxuin=60924477; pass_ticket=YB8k7a2Plkb3ZEI6O3axfOANWBo1Xc9UrlU1i1C7mL84ZzEwRYk7eaUdYGVAJI7F; wap_sid2=CL3Ehh0SigF5X0hBVjJUc2wxLS1GOFZZb2NqT0twX3A3SzZPbXVMaEpndXRFZWVjemNSWS1ZcG95R3JvYXdlVlN3STVnRTcydTRiRmNQRVAxamZKS2thbmdiY1piYVpSVmZxTkI1dEE2bTV3M29FTnNnZkFMY3RIOHN4WFBVODdyS2FTWWpRaFZUcnhvU0FBQX4w6Zru/AU4DUAB";

  @Override
  public void process(Page page) {
    String url = page.getUrl().get();
    if (URL.equals(url)) {
      page.addTargetRequest(HISTORY_URL);
    } else {
      // 由于微信公众的限制，只读取历史数据，具体文章写一个定时器，定时读取
      doHistory(page);
    }
  }

  /**
   * 文章处理
   *
   * @param page
   */
  public void doArticle(Page page) {
    Html html = page.getHtml();
    try {
      Selectable xpath = html.xpath("//div[@id='js_content']");
      List<String> list = xpath.xpath("//img[contains(@data-src,*)]").all();
      Document parse = Jsoup.parse(xpath.get());
      String s = null;
      for (int i = 0; i < list.size(); i++) {
        s = parse.select("img[data-src]").get(i).attr("data-src");
        parse.select("img[data-src]").get(i).addClass("src").attr("src", s);
      }
      List<String> iframe = xpath.xpath("//iframe").all();
      String str = null;
      for (int i = 0; i < iframe.size(); i++) {
        str = parse.select("iframe[data-src]").get(i).attr("data-src");
        if (str.contains("width=500")) {
          str = str.substring(0, str.indexOf("width")) + str.substring(str.indexOf("auto"));
        }
        parse.select("iframe[data-src]").get(i).addClass("src").attr("src", str);
        parse.select("iframe[data-src]").get(i).addClass("width").attr("width", "100%");
        parse.select("iframe[data-src]").get(i).addClass("height").attr("height", "500");
      }
      // 去除style属性
      Elements elements = parse.select("div[style]");
      if (!elements.isEmpty()) {
        parse.select("div[style]").get(0).removeAttr("style");
      }
      String strHtml = parse.body().html();
      //      Article article = Article.builder().mark(strHtml).link(page.getUrl().get()).build();
      //            articleMapper.updateMark(article);
    } catch (IllegalArgumentException e) {
      log.error("this is null");
    }
  }

  /**
   * 处理历史列表
   *
   * @param page
   */
  private void doHistory(Page page) {
    Json json = page.getJson();
    String msg = json.jsonPath("errmsg").get();
    if (!"ok".equals(msg)) {
      log.error("数据没有正常返回,错误信息：" + msg);
      return;
    }
    String list = json.jsonPath("general_msg_list").get();
    String nextNum = json.jsonPath("next_offset").get();
    String url = page.getUrl().get();
    String url_2 = url.substring(url.indexOf("&count"));
    String url_1 = url.substring(0, url.indexOf("offset=") + 7);
    // 下一个url
    if (!StringUtils.isBlank(nextNum)) {
      String next_url = url_1 + nextNum + url_2;
      page.addTargetRequest(next_url);
    }
    Json json1 = new Json(list);
    List<String> wXInfo = json1.jsonPath("list").all();
    //    Article article;
    //    AppMsgExtInfo appMsgExtInfo;
    //    WXInfo wxInfo;
    //    Date publishTime;
    //        List<Article> articles = new ArrayList<>();
    //    for (String s : wXInfo) {
    //      wxInfo = new Json(s).toObject(WXInfo.class);
    //      if (wxInfo == null) {
    //        continue;
    //      }
    //      publishTime = new Date(wxInfo.getCommMsgInfo().getDatetime() * 1000);
    //      appMsgExtInfo = wxInfo.getAppMsgExtInfo();
    //      if (appMsgExtInfo == null) {
    //        continue;
    //      }
    //      article =
    //          Article.builder()
    //              .digest(appMsgExtInfo.getDigest())
    //              .publishTime(publishTime)
    //              .link(appMsgExtInfo.getContentUrl())
    //              .nick(appMsgExtInfo.getAuthor())
    //              .platform("公众号")
    //              .title(appMsgExtInfo.getTitle())
    //              .cover(appMsgExtInfo.getCover())
    //              .build();
    //      insert(article);
    //      //            articles.add(article);
    //      for (AppMsgExtInfo a : appMsgExtInfo.getMultiAppMsgItemList()) {
    //        article =
    //            Article.builder()
    //                .digest(a.getDigest())
    //                .publishTime(publishTime)
    //                .link(a.getContentUrl())
    //                .nick(a.getAuthor())
    //                .platform("公众号")
    //                .title(a.getTitle())
    //                .cover(a.getCover())
    //                .build();
    //        //                articles.add(article);
    //        insert(article);
    //      }
    //            List<String> urls =
    // articles.stream().map(Article::getLink).collect(Collectors.toList());
    //            page.addTargetRequests(urls);
    //            //添加到数据库
    //            articleMapper.addBatch(articles);
    //    }
  }

  //  private void insert(Article article) {
  //    int count = articleMapper.countLink(article.getLink());
  //    if (count == 0) {
  //      try {
  //        articleMapper.add(article);
  //      } catch (Exception e) {
  //        logger.error("insert is error::", e);
  //      }
  //    }
  //  }

  @Override
  public Site getSite() {
    return Site.me()
        .setRetryTimes(3)
        .setSleepTime(1000 * 60 * 5)
        .addCookie("cookie", COOKIES)
        .setUserAgent(
            "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
  }
}
