package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.jackson.models.DefaultOutput;
import org.jfantasy.framework.jackson.models.ListOutput;
import org.jfantasy.framework.jackson.models.Output;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.security.core.GrantedAuthority;
import org.jfantasy.framework.security.core.SimpleGrantedAuthority;
import org.jfantasy.framework.util.asm.AnnotationDescriptor;
import org.jfantasy.framework.util.asm.AsmUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

@Slf4j
public class JSONTest {

  private Article article;
  private ArticleCategory category;

  @BeforeEach
  public void setUp() throws Exception {
    article = TestDataBuilder.build(Article.class, "JSONTest");
    assert article != null;
    category = article.getCategory();
    //
    // ObjectMapper objectMapper = JSON.getObjectMapper();
    // ThreadJacksonMixInHolder.scan(Article.class, ArticleCategory.class);
    // objectMapper.setMixIns(ThreadJacksonMixInHolder.getSourceMixins());
  }

  @AfterEach
  public void tearDown() throws Exception {}

  @Test
  public void serialize() throws Exception {
    // ThreadJacksonMixInHolder holder = ThreadJacksonMixInHolder.getMixInHolder();
    // holder.addIgnorePropertyNames(Article.class, "articles");
    log.debug(JSON.serialize(category, builder -> builder.excludes("articles")));

    // holder = ThreadJacksonMixInHolder.getMixInHolder();
    // holder.addIgnorePropertyNames(ArticleCategory.class, "articles");
    log.debug(JSON.serialize(article, builder -> builder.excludes("category", "content")));
  }

  @Test
  public void serializeWithFilter() throws Exception {
    log.debug(JSON.serialize(category, builder -> builder.excludes("articles")));
  }

  @Test
  public void filter() throws IOException {
    article = TestDataBuilder.build(Article.class, "JSONTest");
    assert article != null;
    category = article.getCategory();

    ObjectMapper objectMapper = JSON.getObjectMapper();
    objectMapper.addMixIn(Article.class, ArticleFilterMixIn.class);

    log.debug("mixInCount = " + objectMapper.mixInCount());

    log.debug("findMixInClassFor = " + objectMapper.findMixInClassFor(Article.class));

    SimpleFilterProvider filter = new SimpleFilterProvider().setFailOnUnknownId(false);
    filter.addFilter("article", SimpleBeanPropertyFilter.serializeAllExcept("category"));

    ObjectWriter objectWriter = objectMapper.writer(filter);

    String json = objectWriter.writeValueAsString(category);

    log.debug(json);

    if (objectMapper.findMixInClassFor(ArticleCategory.class) == null) {
      objectMapper = objectMapper.copy().addMixIn(ArticleCategory.class, CategoryFilterMixIn.class);
    }

    filter = new SimpleFilterProvider().setFailOnUnknownId(false);
    filter.addFilter("category", SimpleBeanPropertyFilter.serializeAllExcept("articles"));

    objectWriter = objectMapper.writer(filter);

    json = objectWriter.writeValueAsString(article);

    log.debug(json);
  }

  @Test
  public void newFilter() throws JsonProcessingException {
    ColorsSerializer colorsSerializer = new ColorsSerializer();
    colorsSerializer.filter(ArticleCategory.class, "", "articles");
    colorsSerializer.filter(Article.class, "title,category", "");
    System.out.println(colorsSerializer.toJson(this.article));
  }

  @Test
  public void addDynamicMixIn() throws IOException {
    // 动态生成 MixIn 接口
    Class newInterface =
        AsmUtil.makeInterface(
            "org.jfantasy.framework.jackson.mixin.TestMixIn",
            AnnotationDescriptor.builder(JsonFilter.class).setValue("value", "x").build(),
            FilterMixIn.class);
    Assert.isTrue(newInterface.isInterface());

    Assert.notNull(newInterface.getAnnotation(JsonFilter.class));

    // 测试接口
    ObjectMapper objectMapper = JSON.getObjectMapper();

    ByteArrayOutputStream output = new ByteArrayOutputStream();
    JsonGenerator generator = objectMapper.getFactory().createGenerator(output, JsonEncoding.UTF8);

    objectMapper.addMixIn(Article.class, newInterface);

    log.debug("mixInCount = " + objectMapper.mixInCount());

    log.debug("findMixInClassFor = " + objectMapper.findMixInClassFor(Article.class));

    SimpleFilterProvider filter = new SimpleFilterProvider().setFailOnUnknownId(false);
    filter.addFilter("x", SimpleBeanPropertyFilter.filterOutAllExcept("title"));

    ObjectWriter objectWriter = objectMapper.writer(filter);

    objectWriter.writeValue(generator, article);
    generator.flush();

    String json = output.toString("utf-8");

    log.debug(json);
  }

  @Test
  public void deserialize() {
    ObjectMapper mapper = JSON.getObjectMapper();
    GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
    Set<GrantedAuthority> authorities = new HashSet<>();
    authorities.add(authority);
    Map<String, Object> data = new HashMap<>();
    LoginUser loginUser = new LoginUser();
    loginUser.setAuthorities(authorities);
    data.put("principal", loginUser);
    data.put(
        "authorities",
        authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
    String json = JSON.serialize(data);
    ReadContext context = JsonPath.parse(json);

    LoginUser principal = mapper.convertValue(context.read("$.principal"), LoginUser.class);

    List<? extends GrantedAuthority> _authorities =
        mapper.convertValue(
            context.read("$.authorities"), new TypeReference<List<SimpleGrantedAuthority>>() {});
    log.debug(_authorities.toString());
  }

  @Test
  public void xmlToJson() {
    String xml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><output><message><result>1</result><description>正常</description></message><data><informationChannel><channelId>312</channelId><channelName><![CDATA[通知通告]]></channelName><channelLevel>1</channelLevel><channelIdString>506250$312$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>322</channelId><channelName><![CDATA[培训通知]]></channelName><channelLevel>2</channelLevel><channelIdString>506250$312$_502500$322$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>324</channelId><channelName><![CDATA[人事通知]]></channelName><channelLevel>2</channelLevel><channelIdString>506250$312$_503750$324$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>323</channelId><channelName><![CDATA[考勤通知]]></channelName><channelLevel>2</channelLevel><channelIdString>506250$312$_505312$323$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>313</channelId><channelName><![CDATA[新闻中心]]></channelName><channelLevel>1</channelLevel><channelIdString>507500$313$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>325</channelId><channelName><![CDATA[制度规范]]></channelName><channelLevel>1</channelLevel><channelIdString>510312$325$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>326</channelId><channelName><![CDATA[管理制度]]></channelName><channelLevel>2</channelLevel><channelIdString>510312$325$_500000$326$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>316</channelId><channelName><![CDATA[产品信息]]></channelName><channelLevel>1</channelLevel><channelIdString>518750$316$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>1</isCanAdd></informationChannel><informationChannel><channelId>321</channelId><channelName><![CDATA[市场专栏]]></channelName><channelLevel>1</channelLevel><channelIdString>521250$321$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>314</channelId><channelName><![CDATA[项目经验技巧分享]]></channelName><channelLevel>1</channelLevel><channelIdString>523750$314$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>1</isCanAdd></informationChannel><informationChannel><channelId>315</channelId><channelName><![CDATA[培训实施心得]]></channelName><channelLevel>1</channelLevel><channelIdString>526250$315$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>1</isCanAdd></informationChannel><informationChannel><channelId>317</channelId><channelName><![CDATA[会议纪要]]></channelName><channelLevel>1</channelLevel><channelIdString>528750$317$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>320</channelId><channelName><![CDATA[管理会议纪要]]></channelName><channelLevel>2</channelLevel><channelIdString>528750$317$_490000$320$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>318</channelId><channelName><![CDATA[项目部会议纪要]]></channelName><channelLevel>2</channelLevel><channelIdString>528750$317$_500000$318$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><informationChannel><channelId>319</channelId><channelName><![CDATA[售后部会议纪要]]></channelName><channelLevel>2</channelLevel><channelIdString>528750$317$_510000$319$_</channelIdString><channelNeedCheckup>0</channelNeedCheckup><isCanAdd>0</isCanAdd></informationChannel><recordCount>15</recordCount></data></output>";

    Output out = JSON.xml().deserialize(xml, ListOutput.class);

    Assert.notNull(out);

    out = JSON.xml().deserialize(xml, DefaultOutput.class);

    Assert.notNull(out);
  }

  @Test
  public void jsonAny() {
    Department department = new Department("技术部");
    department.setPm("test");
    department.set("id", "1");
    department.set("age", 1);
    department.setUserName("limaofeng");

    String json = JSON.serialize(department);
    log.debug(json);

    Assert.isTrue(
        "{\"name\":\"技术部\",\"user_name\":\"limaofeng\",\"project_manager\":\"test\",\"id\":\"1\",\"age\":1}"
            == json);

    department = JSON.deserialize(json, Department.class);

    assert department != null;
    Assert.isTrue(department.get("id") == "1");
  }

  public static class TestDataBuilder {

    public static <T> T build(Class<T> clazz, String keywords) {
      if (ArticleCategory.class.isAssignableFrom(clazz)) {
        ArticleCategory category = new ArticleCategory();
        category.setCode("test");
        category.setName("测试");
        category.setLayer(0);
        category.setArticles(new ArrayList<Article>());
        return (T) category;
      } else if (Article.class.isAssignableFrom(clazz)) {
        Article article = new Article();
        article.setCategory(build(ArticleCategory.class, keywords));
        article.setAuthor(keywords);
        article.setTitle(keywords + " 测试");
        article.setSummary(keywords + " Summary UUID = " + UUID.randomUUID());
        article.setContent(new Content(keywords + " Content"));
        article.getCategory().getArticles().add(article);
        return (T) article;
      }
      try {
        return clazz.newInstance();
      } catch (InstantiationException e) {
        e.printStackTrace(System.err);
        return null;
      } catch (IllegalAccessException e) {
        e.printStackTrace(System.err);
        return null;
      }
    }
  }

  @JsonIgnoreProperties
  public static class Article {

    private Long id;
    /** 文章标题 */
    private String title;
    /** 摘要 */
    private String summary;
    /** 关键词 */
    private String keywords;
    /** 文章正文 */
    private Content content;
    /** 作者 */
    private String author;
    /** 发布日期 */
    private String releaseDate;
    /** 文章对应的栏目 */
    private ArticleCategory category;
    /** 发布标志 */
    private Boolean issue;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getSummary() {
      return summary;
    }

    public void setSummary(String summary) {
      this.summary = summary;
    }

    public ArticleCategory getCategory() {
      return category;
    }

    public void setCategory(ArticleCategory category) {
      this.category = category;
    }

    public Content getContent() {
      return content;
    }

    public void setContent(Content content) {
      this.content = content;
    }

    public String getReleaseDate() {
      return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
      this.releaseDate = releaseDate;
    }

    public String getAuthor() {
      return author;
    }

    public void setAuthor(String author) {
      this.author = author;
    }

    public String getKeywords() {
      return keywords;
    }

    public void setKeywords(String keywords) {
      this.keywords = keywords;
    }

    public Boolean getIssue() {
      return issue;
    }

    public void setIssue(Boolean issue) {
      this.issue = issue;
    }

    public static class ContentSerialize extends JsonSerializer<Content> {

      @Override
      public void serialize(Content content, JsonGenerator jgen, SerializerProvider provider)
          throws IOException {
        jgen.writeString(content.toString());
      }
    }

    @Override
    public String toString() {
      return "Article{"
          + "id="
          + id
          + ", title='"
          + title
          + '\''
          + ", summary='"
          + summary
          + '\''
          + ", keywords='"
          + keywords
          + '\''
          + ", author='"
          + author
          + '\''
          + ", releaseDate='"
          + releaseDate
          + '\''
          + ", issue="
          + issue
          + '}';
    }
  }

  @JsonIgnoreProperties
  public static class ArticleCategory {

    private String code;
    /** 栏目名称 */
    private String name;
    /** 层级 */
    private Integer layer;
    // 树路径
    private String path;
    /** 描述 */
    private String description;
    /** 排序字段 */
    private Integer sort;
    /** 上级栏目 */
    private ArticleCategory parent;
    /** 下级栏目 */
    private List<ArticleCategory> children;
    /** 文章 */
    private List<Article> articles;

    public ArticleCategory() {}

    public ArticleCategory(String code) {
      this.setCode(code);
    }

    public String getCode() {
      return code;
    }

    public void setCode(String code) {
      this.code = code;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Integer getLayer() {
      return layer;
    }

    public void setLayer(Integer layer) {
      this.layer = layer;
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public Integer getSort() {
      return sort;
    }

    public void setSort(Integer sort) {
      this.sort = sort;
    }

    public ArticleCategory getParent() {
      return parent;
    }

    public void setParent(ArticleCategory parent) {
      this.parent = parent;
    }

    public List<ArticleCategory> getChildren() {
      return children;
    }

    public void setChildren(List<ArticleCategory> children) {
      this.children = children;
    }

    public List<Article> getArticles() {
      return articles;
    }

    public void setArticles(List<Article> articles) {
      this.articles = articles;
    }
  }

  public class Department {
    private String name;
    private String pm;
    private String userName;
    private Map<String, Object> otherProperties =
        new HashMap<>(); // otherProperties用来存放Department中未定义的json字段

    // 指定json反序列化创建Department对象时调用此构造函数
    @JsonCreator
    public Department(@JsonProperty("name") String name) {
      this.name = name;
    }

    // 将company.json中projectManager字段关联到getPm方法
    @JsonProperty("project_manager")
    public String getPm() {
      return pm;
    }

    public void setPm(String pm) {
      this.pm = pm;
    }

    public String getName() {
      return name;
    }

    public Object get(String key) {
      return otherProperties.get(key);
    }

    // 得到所有Department中未定义的json字段的
    @JsonAnyGetter
    public Map<String, Object> any() {
      return otherProperties;
    }

    @JsonAnySetter
    public void set(String key, Object value) {
      otherProperties.put(key, value);
    }

    public String getUserName() {
      return userName;
    }

    public void setUserName(String userName) {
      this.userName = userName;
    }
  }

  public static class Content {

    private Long id;
    /** 正文内容 */
    private String text;

    public Content() {}

    public Content(String text) {
      this.setText(text);
    }

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    @Override
    public String toString() {
      return this.text == null ? "" : this.text;
    }

    public class ContentSerialize extends JsonSerializer<Content> {

      @Override
      public void serialize(Content content, JsonGenerator jgen, SerializerProvider provider)
          throws IOException {
        if (content == null) {
          jgen.writeString("");
        } else {
          jgen.writeString(content.toString());
        }
      }
    }
  }
}
