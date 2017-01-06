package org.jfantasy.filestore.rest;


import org.jfantasy.filestore.bean.FileDetail;
import org.jfantasy.filestore.bean.FilePart;
import org.jfantasy.filestore.service.FilePartService;
import org.jfantasy.filestore.service.FileService;
import org.jfantasy.filestore.service.FileUploadService;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 文件上传接口 **/
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
     * @param file           要上传的文件
     * @param dir            上传的目录标识
     * @param entireFileName 完整文件名
     * @param entireFileDir  完整文件的上传目录标识
     * @param entireFileHash 完整文件Hash值
     * @param partFileHash   分段文件Hash值
     * @param total          总段数
     * @param index          分段序号
     * @return {String} 返回文件信息
     * @throws IOException 文件上传异常
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public FileDetail upload(@RequestParam(value = "attach") MultipartFile file, String dir,  String entireFileName, String entireFileDir, String entireFileHash,  String partFileHash,  Integer total, Integer index) throws IOException {
        return fileUploadService.upload(file, dir, entireFileName, entireFileDir, entireFileHash, partFileHash, total, index);
    }

    /**
     * 分段上传查询
     * @param hash
     * @return
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

    /** 查询文件信息 **/
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public FileDetail view(@RequestParam("key") String path) {
        return fileService.get(path);
    }

}
