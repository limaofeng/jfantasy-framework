package org.jfantasy.filestore.manager;

import org.jfantasy.filestore.FileItem;
import org.jfantasy.filestore.builders.OSSFileManagerBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by 30648 on 2017/3/17.
 */
public class OSSFileManagerTest {

    private OSSFileManager ossFileManager = new OSSFileManager("oss-cn-hangzhou.aliyuncs.com",new OSSFileManager.AccessKey("LTAIwxASPg44JBfm","QsBNcRCAaq2GXp6GC5LsPRFHfvIoVb"),"zbsgfiles");
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void writeFile() throws Exception {
        File file = new File("D:/test.txt");
        if (file.isFile()&&file.exists()){
            InputStreamReader read = new InputStreamReader(new FileInputStream(file),"GBK");//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
                System.out.println(lineTxt);
            }
            read.close();
        }

        ossFileManager.writeFile("/a1/test.txt",file);
    }

    @Test
    public void readFile() throws Exception {
        InputStream inputStream = ossFileManager.readFile("/a1/test.txt");
        byte[] bytes = new byte[1024];
        int length = 0;
        while ((length=inputStream.read(bytes))!=-1){
            System.out.println(new String(bytes,"GBK"));
        }

    }

    @Test
    public void listFiles() throws Exception {
        List<FileItem> fileItems = ossFileManager.listFiles();
        for (FileItem fileItem:fileItems){
            System.out.println(fileItem);
        }

    }

    @Test
    public void getFileItem() throws Exception {

    }

}