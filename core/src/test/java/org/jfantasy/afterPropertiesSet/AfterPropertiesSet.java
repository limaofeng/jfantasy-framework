// package org.jfantasy.afterPropertiesSet;
//
// import org.jfantasy.attr.storage.bean.AttributeType;
// import org.jfantasy.attr.storage.bean.Converter;
// import org.jfantasy.attr.storage.dao.AttributeTypeDao;
// import org.jfantasy.attr.storage.dao.ConverterDao;
// import org.jfantasy.attr.framework.converter.PrimitiveTypeConverter;
// import org.jfantasy.common.bean.FtpConfig;
// import org.jfantasy.common.dao.AreaDao;
// import org.jfantasy.common.service.AreaService;
// import org.jfantasy.common.service.FtpConfigService;
// import org.jfantasy.file.bean.FileManagerConfig;
// import org.jfantasy.framework.spring.SpringContextUtil;
// import org.jfantasy.framework.util.common.ObjectUtil;
// import org.jfantasy.framework.util.common.StringUtil;
// import org.jfantasy.framework.util.common.file.FileUtil;
// import org.jfantasy.system.bean.DataDictionaryType;
// import org.jfantasy.system.bean.Setting;
// import org.jfantasy.system.bean.Website;
// import org.apache.commons.logging.Log;
// import org.apache.commons.logging.LogFactory;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.transaction.TransactionDefinition;
// import org.springframework.transaction.TransactionStatus;
// import org.springframework.transaction.support.DefaultTransactionDefinition;
//
// import java.io.InputStream;
// import java.math.BigDecimal;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.List;
//
/// **
// * Created by hebo on 2014/9/19.
// */
// public class AfterPropertiesSet {
//
// private static final Log logger =
// LogFactory.getLog(AfterPropertiesSet.class);
//
// private AttributeTypeDao attributeTypeDao;
//
// public void AttributeTypeService() throws Exception {
// PlatformTransactionManager transactionManager =
// SpringContextUtil.getBean("transactionManager",
// PlatformTransactionManager.class);
// DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
// TransactionStatus status = transactionManager.getTransaction(def);
// try {
// // ?????????????????????
// Class<?>[] defaultClass = new Class[] { int.class, long.class, double.class,
// Date.class, BigDecimal.class, String.class, int[].class, long[].class,
// double[].class,
// Date[].class, BigDecimal[].class, String[].class };
// for (Class<?> clazz : defaultClass) {
// AttributeType attributeType = attributeTypeDao.findUniqueBy("dataType",
// clazz.getName());
// if (attributeType == null) {
// StringBuffer log = new StringBuffer("?????????????????????:" + clazz);
// attributeType = new AttributeType();
// attributeType.setName(clazz.getSimpleName());
// attributeType.setDataType(clazz.getName());
// attributeType.setConverter(null);
// attributeType.setDescription("??????????????????:" + clazz);
// attributeTypeDao.save(attributeType);
// logger.debug(log);
// }
// }
// // ???????????????????????????
// } finally {
// transactionManager.commit(status);
// }
// }
//
// private ConverterDao converterDao;
//
// public void ConverterService() throws Exception {
// PlatformTransactionManager transactionManager =
// SpringContextUtil.getBean("transactionManager",
// PlatformTransactionManager.class);
// DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
// TransactionStatus status = transactionManager.getTransaction(def);
// try {
// // ?????????????????????
// Class<?>[] defaultClass = new Class[] { PrimitiveTypeConverter.class };
// for (Class<?> clazz : defaultClass) {
// Converter converter = converterDao.findUniqueBy("typeConverter",
// clazz.getName());
// if (converter == null) {
// StringBuffer log = new StringBuffer("????????????????????????????????????:" + clazz);
// converter = new Converter();
// converter.setName("int converter");
// converter.setTypeConverter(clazz.getName());
// converter.setDescription("???????????????????????????:" + clazz);
// converterDao.save(converter);
// logger.debug(log);
// }
// }
// } finally {
// transactionManager.commit(status);
// }
// }
//
// private AreaDao areaDao;
//
// public void AreaService() throws Exception {
// PlatformTransactionManager transactionManager =
// SpringContextUtil.getBean("transactionManager",
// PlatformTransactionManager.class);
// DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
// TransactionStatus status = transactionManager.getTransaction(def);
// try {
// int count = areaDao.count();
// if (count <= 0) {
// // ????????????sql??????
// InputStream is = AreaService.class.getResourceAsStream("/database/area.sql");
// FileUtil.readFile(is, new FileUtil.ReadLineCallback() {
// @Override
// public boolean readLine(String line) {
// for (String sql : StringUtil.tokenizeToStringArray(line, "\n")) {
// areaDao.batchSQLExecute(sql);
// }
// return true;
// }
// });
// transactionManager.commit(status);
// }
// } catch (RuntimeException e) {
// transactionManager.rollback(status);
// }
// }
//
// private FtpConfigService ftpConfigService;
//
// /**
// * ??????????????????????????????????????????FtpService
// *
// * @throws Exception
// */
// public void FtpServiceFactory() throws Exception {
// PlatformTransactionManager transactionManager =
// SpringContextUtil.getBean("transactionManager",
// PlatformTransactionManager.class);
// DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// def.setReadOnly(true);
// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
// TransactionStatus status = transactionManager.getTransaction(def);
// try {
// for (FtpConfig config : ftpConfigService.getAll()) {
// this.initialize(config);
// }
// } finally {
// transactionManager.commit(status);
// }
// }
//
// private void initialize(FtpConfig config) {
//
// }
//
// private MenuDao menuDao;
//
// /**
// * ???????????????
// */
// public void MenuService() throws Exception {
// List<Menu> menus = menuDao.find();
// if (menus == null || menus.size() <= 0) {
// PlatformTransactionManager transactionManager =
// SpringContextUtil.getBean("transactionManager",
// PlatformTransactionManager.class);
// DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
// TransactionStatus status = transactionManager.getTransaction(def);
// try {
// // ????????????sql??????
// InputStream is =
// MenuService.class.getResourceAsStream("/database/auth_menu.sql");
// FileUtil.readFile(is, new FileUtil.ReadLineCallback() {
// @Override
// public boolean readLine(String line) {
// for (String sql : StringUtil.tokenizeToStringArray(line, "\n")) {
// menuDao.batchSQLExecute(sql);
// }
// return true;
// }
// });
// transactionManager.commit(status);
// } catch (RuntimeException e) {
// transactionManager.rollback(status);
// }
// }
// }
//
//
// public void RoleService() throws Exception {
// StringBuffer log = new StringBuffer("????????????????????????????????????????????????");
// PlatformTransactionManager transactionManager =
// SpringContextUtil.getBean("transactionManager",
// PlatformTransactionManager.class);
// DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
// TransactionStatus status = transactionManager.getTransaction(def);
// try {
// Role role = null;//get("SYSTEM");
// if (role == null) {
// role = new Role();
// role.setCode("SYSTEM");
// role.setName("???????????????");
// role.setEnabled(true);
// role.setDescription("?????????????????????");
// //save(role);
// }
// } finally {
// transactionManager.commit(status);
// }
// logger.debug(log);
// }
//
// public void PageService() throws Exception {
// PlatformTransactionManager transactionManager =
// SpringContextUtil.getBean("transactionManager",
// PlatformTransactionManager.class);
// DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
// TransactionStatus status = transactionManager.getTransaction(def);
// try {
// /*
// //????????????
// Template template = new Template();
// template.setName("????????????");
// template.setType(Template.Type.freeMarker);
// template.setFilePath("template/test.ftl");
// template.setDescription("????????????");
// //??????????????????
// List<DataInferface> dataInferfaces = new ArrayList<DataInferface>();
// DataInferface dataInferface = new DataInferface();
// dataInferface.setKey("testData");
// dataInferface.setName("????????????");
// dataInferface.setDefaultValue("xxxxx");
// dataInferface.setJavaType(String.class.getName());
// dataInferfaces.add(dataInferface);
// template.setDataInferfaces(dataInferfaces);
// //????????????
// Page page = new Page();
// page.setTemplate(template);
// page.setName("?????????");
// //??????????????????
// List<Data> datas = new ArrayList<Data>();
// Data data = new Data();
// data.setDataInferface(dataInferface);
// data.setScope(Data.Scope.page);
// data.setCacheInterval(10000l);
// datas.add(data);
// page.setDatas(datas);
//
// //?????????????????????
// DataAnalyzer dataAnalyzer = new DataAnalyzer();
// dataAnalyzer.setName("???????????????");
// dataAnalyzer.setClassName("xxxx");
// data.setDataAnalyzer(dataAnalyzer);
//
// //?????????????????????
// PageAnalyzer pageAnalyzer = new PageAnalyzer();
// pageAnalyzer.setName("???????????????");
// pageAnalyzer.setClassName("xxxxx");
// page.setPageAnalyzer(pageAnalyzer);
// */
// transactionManager.commit(status);
// } catch (RuntimeException e) {
// transactionManager.rollback(status);
// }
// }
//
//
// public void DataDictionaryService() throws Exception {
// PlatformTransactionManager transactionManager =
// SpringContextUtil.getBean("transactionManager",
// PlatformTransactionManager.class);
// DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
// TransactionStatus status = transactionManager.getTransaction(def);
// try {
// DataDictionaryType ddt = null;//getDataDictionaryType("root");
// if (ddt == null) {
// StringBuffer log = new StringBuffer("????????????????????????????????????");
// ddt = new DataDictionaryType();
// ddt.setCode("root");
// ddt.setName("??????????????????");
// //save(ddt);
// logger.debug(log);
// }
// } finally {
// transactionManager.commit(status);
// }
// }
//
//
//
// public void WebsiteService() throws Exception {
// PlatformTransactionManager transactionManager =
// SpringContextUtil.getBean("transactionManager",
// PlatformTransactionManager.class);
// DefaultTransactionDefinition def = new DefaultTransactionDefinition();
// def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
// TransactionStatus status = transactionManager.getTransaction(def);
// try {
// // ????????????????????????
// StringBuffer log = new StringBuffer("?????????????????????");
// Website webSite = null;//this.findUniqueByKey("haolue");
// if (webSite == null) {
// webSite = new Website();
// webSite.setKey("haolue");
// webSite.setName("????????????????????????????????????");
// webSite.setWeb("http://haolue.jfantasy.org");
// }
// // ???????????????????????????
// FileManagerConfig config = null;//fileManagerService.get(webSite.getKey() +
// "-default");
// if (config == null) {
// //fileManagerService.save(config =
// FileManagerConfig.newInstance(webSite.getKey()
// + "-default", "?????????????????????", "/home/" + webSite.getKey(), "?????????????????????,????????????"));
// }
// webSite.setDefaultFileManager(config);
//
// // ???????????????????????????
// FileManagerConfig ufm = null;//fileManagerService.get(webSite.getKey() +
// "-upload");
// if (ufm == null) {
// //fileManagerService.save(ufm =
// FileManagerConfig.newInstance(webSite.getKey() +
// "-upload", "???????????????????????????", FileManagerConfig.newInstance(webSite.getKey() +
// "-default"),
// "???????????????????????????"));
// }
// webSite.setDefaultUploadFileManager(ufm);
//
//// this.websiteDao.save(webSite);
// // ???????????????
// List<Setting> settings = new ArrayList<Setting>();
// settings.add(Setting.newInstance(webSite, "??????????????????", "title", "????????????????????????????????????",
// ""));
// settings.add(Setting.newInstance(webSite, "?????????", "copyright", "2014???????????? All
// Right
// Reserved", ""));
// settings.add(Setting.newInstance(webSite, "??????????????????", "wel_tle",
// "???????????????????????????????????????-????????????????????????", ""));
// settings.add(Setting.newInstance(webSite, "??????????????????", "wel_des",
// "??????-????????????????????????***************************", ""));
// settings.add(Setting.newInstance(webSite, "??????", "wel_ver", "V1.0.1", "?????????"));
// settings.add(Setting.newInstance(webSite, "?????????", "wel_copyr", "@copyright
// 2014",
// "?????????"));
// settings.add(Setting.newInstance(webSite, "????????????", "icon",
// "/images/rex04.ico",
// "????????????icon??????"));
// settings.add(Setting.newInstance(webSite, "????????????", "serverUrl",
// "http://localhost:8080", ""));
//
// for (Setting setting : settings) {
// if (webSite.getSettings() == null || ObjectUtil.find(webSite.getSettings(),
// "key",
// setting.getKey()) == null) {
//// settingDao.save(setting);
// }
// }
//
// logger.debug(log);
// } finally {
// transactionManager.commit(status);
// }
// }
//
//
//
//
// }
