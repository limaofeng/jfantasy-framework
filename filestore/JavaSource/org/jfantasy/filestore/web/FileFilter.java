package org.jfantasy.filestore.web;

import org.jfantasy.filestore.FileItem;
import org.jfantasy.filestore.FileManager;
import org.jfantasy.filestore.bean.FileDetail;
import org.jfantasy.filestore.service.FileManagerFactory;
import org.jfantasy.filestore.service.FileService;
import org.jfantasy.filestore.service.FileUploadService;
import org.jfantasy.framework.util.common.ImageUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StreamUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.common.file.FileUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.framework.util.web.ServletUtils;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.framework.util.web.WebUtil.Browser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;

@Component
public class FileFilter extends GenericFilterBean {

    @Autowired
    private FileService fileService;
    @Autowired
    private FileUploadService fileUploadService;

    private String[] allowHosts;

    private static final String regex = "_(\\d+)x(\\d+)[.]([^.]+)$";

    @Override
    protected void initFilterBean() throws ServletException {
//        this.addRequiredProperty("allowHosts");
        this.setAllowHosts("");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if(allowHosts.length != 0 && !ObjectUtil.exists(allowHosts,request.getHeader("host"))){
            chain.doFilter(request, response);
            return;
        }

        final String url = request.getRequestURI().replaceAll("^" + request.getContextPath(), "");
        FileManager webrootFileManager = FileManagerFactory.getInstance().getFileManager("WEBROOT");
        if (RegexpUtil.find(url, ".do$")) {
            chain.doFilter(request, response);
            return;
        }

        if (webrootFileManager.getFileItem(url) != null) {
            chain.doFilter(request, response);
            return;
        }
        FileDetail fileDetail = FileFilter.this.fileService.get(url);
        if (fileDetail != null) {
            FileManager fileManager = FileManagerFactory.getInstance().getFileManager(fileDetail.getNamespace());
            FileItem fileItem = fileManager.getFileItem(fileDetail.getRealPath());
            if (fileItem != null) {
                writeFile(request, response, fileItem);
                return;
            }
        }
        if (RegexpUtil.find(url, regex)) {
            final String srcUrl = RegexpUtil.replace(url, regex, ".$3");
            FileDetail srcFileDetail = FileFilter.this.fileService.get(srcUrl);
            if (srcFileDetail == null) {
                chain.doFilter(request, response);
                return;
            }
            // 查找源文件
            FileManager fileManager = FileManagerFactory.getInstance().getFileManager(srcFileDetail.getNamespace());
            FileItem fileItem = fileManager.getFileItem(srcFileDetail.getRealPath());
            if (fileItem == null) {
                chain.doFilter(request, response);
                return;
            }
            // 只自动缩放 image/jpeg 格式的图片
            if (!fileItem.getContentType().contains("image/")) {
                chain.doFilter(request, response);
                return;
            }
            RegexpUtil.Group group = RegexpUtil.parseFirstGroup(url, regex);
            // 图片缩放
            assert group != null;
            BufferedImage image = ImageUtil.reduce(fileItem.getInputStream(), Integer.valueOf(group.$(1)), Integer.valueOf(group.$(2)));
            // 创建临时文件
            File tmp = FileUtil.tmp();
            ImageUtil.write(image, tmp);
            fileDetail = fileUploadService.upload(tmp, url, "haolue-upload");
            // 删除临时文件
            FileUtil.delFile(tmp);
            writeFile(request, response, fileManager.getFileItem(fileDetail.getRealPath()));
        }else{
            chain.doFilter(request,response);
        }

    }

    private void writeFile(HttpServletRequest request, HttpServletResponse response, FileItem fileItem) throws IOException {
        if ("POST".equalsIgnoreCase(WebUtil.getMethod(request))) {
            response.setContentType(fileItem.getContentType());
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setContentLength((int) fileItem.getSize());

            String fileName = Browser.mozilla == WebUtil.browser(request) ? new String(fileItem.getName().getBytes("UTF-8"), "iso8859-1") : URLEncoder.encode(fileItem.getName(), "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        } else {
            ServletUtils.setExpiresHeader(response, 1000 * 60 * 5L);
            ServletUtils.setLastModifiedHeader(response, fileItem.lastModified().getTime());
        }
        if (fileItem.getContentType().startsWith("video/")) {
            response.addHeader("Accept-Ranges", "bytes");
            response.addHeader("Cneonction", "close");

            String range = StringUtil.defaultValue(request.getHeader("Range"), "bytes=0-");
            if ("keep-alive".equals(request.getHeader("connection"))) {
                long fileLength = fileItem.getSize();

                response.setStatus(206);
                String bytes = WebUtil.parseQuery(range).get("bytes")[0];
                String[] sf = bytes.split("-");
                int start = 0;
                int end = 0;
                if (sf.length == 2) {
                    start = Integer.valueOf(sf[0]);
                    end = Integer.valueOf(sf[1]);
                } else if (bytes.startsWith("-")) {
                    start = 0;
                    end = (int) (fileLength - 1);
                } else if (bytes.endsWith("-")) {
                    start = Integer.valueOf(sf[0]);
                    end = (int) (fileLength - 1);
                }
                int contentLength = end - start + 1;

                response.setHeader("Connection", "keep-alive");
                response.setHeader("Content-Type", fileItem.getContentType());
                response.setHeader("Cache-Control", "max-age=1024");
                ServletUtils.setLastModifiedHeader(response, fileItem.lastModified().getTime());
                response.setHeader("Content-Length", Long.toString(contentLength > fileLength ? fileLength : contentLength));
                response.setHeader("Content-Range", "bytes " + start + "-" + (end != 1 && end >= fileLength ? end - 1 : end) + "/" + fileLength);

                InputStream in = fileItem.getInputStream();
                OutputStream out = response.getOutputStream();

                int loadLength = contentLength, bufferSize = 2048;

                byte[] buf = new byte[bufferSize];

                int bytesRead = in.read(buf, 0, loadLength > bufferSize ? bufferSize : loadLength);
                while (bytesRead != -1 && loadLength > 0) {
                    loadLength -= bytesRead;
                    out.write(buf, 0, bytesRead);
                    bytesRead = in.read(buf, 0, loadLength > bufferSize ? bufferSize : loadLength);
                }
                StreamUtil.closeQuietly(in);
                out.flush();
            } else {
                try {
                    StreamUtil.copy(fileItem.getInputStream(), response.getOutputStream());
                } catch (FileNotFoundException var5) {
                    logger.error(var5.getMessage(),var5);
                    response.sendError(404);
                }
            }
        } else {
            if (ServletUtils.checkIfModifiedSince(request, response, fileItem.lastModified().getTime())) {
                try {
                    response.setHeader("Content-Type", fileItem.getContentType());
                    StreamUtil.copy(fileItem.getInputStream(), response.getOutputStream());
                } catch (FileNotFoundException e) {
                    logger.error(e.getMessage(),e);
                    response.sendError(404);
                }
            }
        }
    }

    public void setAllowHosts(String allowHosts) {
        this.allowHosts = StringUtil.tokenizeToStringArray(allowHosts);
    }

}