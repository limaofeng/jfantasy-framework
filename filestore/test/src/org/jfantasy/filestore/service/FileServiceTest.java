package org.jfantasy.filestore.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.filestore.ApplicationTest;
import org.jfantasy.filestore.bean.FileManagerConfig;
import org.jfantasy.filestore.bean.Folder;
import org.jfantasy.framework.dao.Pager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ApplicationTest.class)
public class FileServiceTest {

    private final static Log LOG = LogFactory.getLog(FileServiceTest.class);

    @Autowired
    private FileService fileService;
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private DirectoryService directoryService;
    @Autowired
    private FileManagerService fileManagerService;

    @Before
    public void setUp() throws Exception {
        /*
        try {
            File file = new File(FileServiceTest.class.getResource("files/t5.jpg").getFile());
            String mimeType = FileUtil.getMimeType(file);
            FileDetail fileDetail = fileUploadService.upload(file, mimeType, file.getName(), "test");
            this.fileDetailKey = FileDetailKey.newInstance(fileDetail.getAbsolutePath(), fileDetail.getFileManagerId());

            File[] files = new File(FileServiceTest.class.getResource("files/").getFile()).listFiles();
            if(files == null){
                return;
            }
            for (File _file : files) {
                fileUploadService.upload(_file, FileUtil.getMimeType(_file), _file.getName(), "test");
            }

        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }*/
    }

    @After
    public void tearDown() throws Exception {
        /*
        Directory directory = directoryService.get("test");
        for(FileDetail fileDetail : fileService.findFileDetail(Restrictions.eq("fileManagerId",directory.getFileManager().getId()),Restrictions.like("folder.path",directory.getDirPath(), MatchMode.START))){
            this.fileService.delete(FileDetailKey.newInstance(fileDetail.getAbsolutePath(), fileDetail.getFileManagerId()));
        }*/
    }

    @Test
    public void testUpdate() throws Exception {
        /*
        FileDetail fileDetail = this.fileService.get(fileDetailKey);
        fileDetail.setFileName("替换原生的文件名");
        this.fileService.update(fileDetail);
        Assert.assertEquals("替换原生的文件名", this.fileService.get(fileDetailKey).getFileName());
        */
    }

    @Test
    public void testGetFolder() throws Exception {
        /*
        Folder folder = this.fileService.getFolder("/", "haolue-upload");
        Assert.assertNotNull(folder);
        */
    }

    @Test
    public void testDelete() throws Exception {

    }

    @Test
    public void testCreateFolder() throws Exception {

    }

    @Test
    public void testFindUniqueByMd5() throws Exception {

    }

    @Test
    public void testFindUnique() throws Exception {

    }

    @Test
    public void testGetFileDetail() throws Exception {

    }

    @Test
    public void testFindFileDetailPager() throws Exception {
        /*
        logger.debug("> Dao findPager 方法缓存测试");

        long min, max, total = 0;
        long start = System.currentTimeMillis();
        logger.debug(" 开始第一次查 >> ");
        Pager<FileDetail> pager = this.fileService.findFileDetailPager(new Pager<FileDetail>(15), new ArrayList<PropertyFilter>());
        long _temp = System.currentTimeMillis() - start;
        total += _temp;
        max = min = _temp;
        logger.debug(" 第一次查询耗时：" + _temp + "ms");

        for (int i = 2; i < 250; i++) {
            start = System.currentTimeMillis();
            logger.debug(" 开始第" + NumberUtil.toChinese(i) + "次查 >> ");
            Pager<FileDetail> _pager = this.fileService.findFileDetailPager(new Pager<FileDetail>(15), new ArrayList<PropertyFilter>());

            Assert.assertEquals(pager.getTotalCount(), _pager.getTotalCount());
            Assert.assertEquals(pager.getPageItems().size(), _pager.getPageItems().size());

            _temp = System.currentTimeMillis() - start;
            total += _temp;
            if (_temp >= max) {
                max = _temp;
            }
            if (_temp <= min) {
                min = _temp;
            }
            logger.debug(" 第" + NumberUtil.toChinese(i) + "次查询耗时：" + _temp + "ms");
        }
        logger.debug("查询耗共耗时：" + total + "ms\t平均:" + total / 250 + "ms\t最大:" + max + "ms\t最小:" + min + "ms");
        */
    }

    @Test
    public void testListFolder() throws Exception {
        Pager<FileManagerConfig> pager = fileManagerService.findPager(new Pager<>(),new ArrayList<>());
        FileManagerConfig config = pager.getPageItems().isEmpty() ? null : pager.getPageItems().get(0);
        assert config != null;
        List<Folder> folders = this.fileService.listFolder("/", config.getId(), "path");
        LOG.debug(" result size : " + folders.size());
    }

    @Test
    public void testListFileDetail() throws Exception {

    }

    @Test
    public void testGetFileDetailByMd5() throws Exception {

    }

    @Test
    public void testLocalization() throws Exception {

    }

    @Test
    public void testGetDirectory() throws Exception {

    }

    @Test
    public void testGetAbsolutePath() throws Exception {

    }

}