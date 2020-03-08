package org.jfantasy.storage.graphql.directives;

import graphql.Scalars;
import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.Base64;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.storage.FileObject;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @Author: fengmeng
 * @Date: 2019/5/7 20:45
 */
@Slf4j
public class FileObjectFormatDirective implements SchemaDirectiveWiring {

    @Value("${storage.url}")
    private String storageUrl;

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
        GraphQLFieldDefinition field = environment.getElement();
        DataFetcher originalDataFetcher = ClassUtil.call("getDataFetcher", field);
        DataFetcher dataFetcher = DataFetcherFactories.wrapDataFetcher(originalDataFetcher, ((dataFetchingEnvironment, value) -> {
            String type = dataFetchingEnvironment.getArgument("format");
            if (value instanceof FileObject && type != null && !"object".equals(type)) {
                return buildFileObject((FileObject) value, type);
            }
            return value;
        }));
        return field.transform(builder -> builder
            .argument(GraphQLArgument
                .newArgument()
                .name("format")
                .type(Scalars.GraphQLString)
                .type(GraphQLEnumType.newEnum()
                    .name("FileEnum").description("文件自定在格式")
                    .value("base64", "base64", "仅支持图片")
                    .value("url", "url", "自动添加上域名")
                    .value("object", "object", "默认格式")
                    .build())
                .description("文件自定在格式")
            )
            .dataFetcher(dataFetcher)
        );
    }

    private String buildFileObject(FileObject fileObject, String type) {
        String str = storageUrl + fileObject.getPath();
        if ("base64".equals(type)) {
            str = getImageBase64ByFileObject(fileObject, str);
        }
        return str;
    }


    /**
     * 通过FileObject获取图片base64位编码
     */
    public static String getImageBase64ByFileObject(FileObject fileObject, String url) {
        String base64;
        if (fileObject.getLength() > (1024 * 1014 * 5L)) {
            return null;
        }
        if (!"image".equals(fileObject.getMimeType().substring(0, 5))) {
            return null;
        }
        try {
            return "data:" + fileObject.getMimeType() + ";base64," + ImageToBase64ByOnline(url);
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 在线图片转换成base64字符串
     *
     * @param imgURL 图片线上路径
     * @return
     * @author ZHANGJL
     * @dateTime 2018-02-23 14:43:18
     */
    public static String ImageToBase64ByOnline(String imgURL) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        // 创建URL
        URL url = new URL(imgURL);
        byte[] by = new byte[1024];
        // 创建链接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        InputStream is = conn.getInputStream();
        // 将内容读取内存中
        int len = -1;
        while ((len = is.read(by)) != -1) {
            data.write(by, 0, len);
        }
        // 关闭流
        is.close();
        // 对字节数组Base64编码
        return new String(Base64.encodeBase64(data.toByteArray()));
    }
}
