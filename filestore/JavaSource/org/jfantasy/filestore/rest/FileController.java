package org.jfantasy.filestore.rest;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.jfantasy.filestore.bean.FileDetail;
import org.jfantasy.filestore.bean.FilePart;
import org.jfantasy.filestore.service.FilePartService;
import org.jfantasy.filestore.service.FileService;
import org.jfantasy.filestore.service.FileUploadService;
import org.jfantasy.filestore.service.Info;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传接口
 **/
@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;
    private final FileUploadService fileUploadService;
    private final FilePartService filePartService;

    @Autowired
    public FileController(FileService fileService, FileUploadService fileUploadService, FilePartService filePartService) {
        this.fileService = fileService;
        this.fileUploadService = fileUploadService;
        this.filePartService = filePartService;
    }

    /**
     * 上传文件<br/>
     * 单独的文件上传接口，返回 FileDetail 对象
     *
     * @param file 要上传的文件
     * @param info 上传的目录标识
     * @return {String} 返回文件信息
     * @throws IOException 文件上传异常
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public FileDetail upload(@RequestParam(value = "attach", required = false) MultipartFile file, @Validated(RESTful.POST.class) Info info) throws IOException {
        if (StringUtil.isBlank(info.getUrl()) && file == null) {
            throw new ValidationException("attach 与 url 必须指定其中一个参数");
        }
        if (StringUtil.isBlank(info.getUrl())) {
            return fileUploadService.upload(file, info);
        } else {
            try {
                HttpResponse<InputStream> response = Unirest.get(info.getUrl()).asBinary();
                Long length = Long.valueOf(response.getHeaders().getFirst("Content-Length"));
                String contentType = response.getHeaders().getFirst("Content-Type");
                return fileUploadService.upload(response.getBody(), contentType, StringUtil.defaultValue(info.getName(), ""), length, info.getDir());
            } catch (UnirestException e) {
                throw new IOException(e.getMessage(), e);
            }
        }
    }

    /**
     * 分段上传查询
     *
     * @param hash hash
     * @return Map<Object>
     */
    @RequestMapping(value = "/{hash}/pass", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> pass(@PathVariable("hash") String hash) {
        List<FilePart> parts = filePartService.find(hash);
        Map<String, Object> data = new HashMap<>();
        FilePart part = ObjectUtil.remove(parts, "index", 0);
        if (part != null) {
            data.put("fileDetail", fileService.get(part.getPath()));
        }
        data.put("parts", parts);
        return data;
    }

    /**
     * 查询文件信息
     **/
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public FileDetail view(@RequestParam("path") String path) {
        return fileService.get(path.contains(":") ? path.substring(path.indexOf(":") + 1) : path);
    }

}
