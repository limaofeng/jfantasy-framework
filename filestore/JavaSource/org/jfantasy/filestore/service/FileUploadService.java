package org.jfantasy.filestore.service;

import org.jfantasy.filestore.FileManager;
import org.jfantasy.filestore.bean.Directory;
import org.jfantasy.filestore.bean.FileDetail;
import org.jfantasy.filestore.bean.FileDetailKey;
import org.jfantasy.filestore.bean.FilePart;
import org.jfantasy.framework.spring.SpELUtil;
import org.jfantasy.framework.util.common.*;
import org.jfantasy.framework.util.common.file.FileUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.framework.util.web.WebUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
@Transactional
public class FileUploadService {

    private static final Log LOG = LogFactory.getLog(FileUploadService.class);

    private final static String separator = "/";//NOSONAR
    @Autowired
    private transient FileService fileService;
    @Autowired
    private transient FilePartService filePartService;
    @Autowired
    private transient FileManagerFactory fileManagerFactory;

    private static boolean isPart(String entireFileHash, String partFileHash, String entireFileName, String entireFileDir, Integer total, Integer index) {
        return !(StringUtil.isBlank(entireFileHash) || StringUtil.isBlank(partFileHash)) && !(StringUtil.isBlank(entireFileName) || StringUtil.isBlank(entireFileDir)) && !(ObjectUtil.isNull(total) || StringUtil.isNull(index));
    }

    private FileDetail uploadPart(FilePart part, FilePart filePart, List<FilePart> fileParts, String contentType, String entireFileName, String entireFileDir, String entireFileHash, Integer total, FileManager fileManager) throws IOException {
        FileDetail fileDetail = null;
        if (part == null) {
            List<FilePart> joinFileParts = new ArrayList<>();
            ObjectUtil.join(joinFileParts, fileParts, "index");

            if (joinFileParts.size() == total) {
                //临时文件
                File tmp = FileUtil.tmp();
                //合并 Part 文件
                try (FileOutputStream out = new FileOutputStream(tmp)) {
                    for (FilePart filesPart : joinFileParts) {
                        InputStream in = fileManager.readFile(filesPart.getAbsolutePath());
                        StreamUtil.copy(in, out);
                        StreamUtil.closeQuietly(in);
                        fileManager.removeFile(filesPart.getAbsolutePath());
                        ObjectUtil.remove(fileParts, SpELUtil.getExpression(" absolutePath == #value.getAbsolutePath() and fileManagerId == #value.getFileManagerId() "), filePart);
                    }
                }

                //保存合并后的新文件
                fileDetail = this.upload(tmp, contentType, entireFileName, entireFileDir);

                //删除临时文件
                FileUtil.delFile(tmp);

                //删除 Part 文件
                for (FilePart filesPart : fileParts) {
                    fileManager.removeFile(filesPart.getAbsolutePath());
                }

                //在File_PART 表冗余一条数据 片段为 0
                filePartService.save(FileDetailKey.newInstance(fileDetail.getAbsolutePath(), fileDetail.getFileManagerId()), entireFileHash, entireFileHash, total, 0);
            }
        } else {
            //删除 Part 文件
            for (FilePart filesPart : fileParts) {
                fileManager.removeFile(filesPart.getAbsolutePath());
            }
        }
        return fileDetail;
    }

    public FileDetail upload(MultipartFile file, String dir, String entireFileName, String entireFileDir, String entireFileHash, String partFileHash, Integer total, Integer index) throws IOException {
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        try {
            //判断是否为分段上传
            boolean isPart = isPart(entireFileHash, partFileHash, entireFileName, entireFileDir, total, index);
            //生成分段上传的文件名
            if (isPart && "blob".equalsIgnoreCase(fileName)) {
                fileName = entireFileName + ".part" + StringUtil.addZeroLeft(index.toString(), total.toString().length());
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("上传文件参数:{fileName:" + contentType + ",contentType:" + fileName + ",dir:" + dir + ",isPart:" + isPart + "}");
            }

            //上传文件信息
            FileDetail fileDetail;

            if (isPart) {//如果为分段上传
                //获取文件上传目录的配置信息
                Directory directory = fileService.getDirectory(dir);
                FileManager fileManager = fileManagerFactory.getUploadFileManager(directory.getFileManager().getId());

                FilePart filePart = filePartService.findByPartFileHash(entireFileHash, partFileHash);
                if (filePart == null || (fileDetail = fileService.get(FileDetailKey.newInstance(filePart.getAbsolutePath(), filePart.getFileManagerId()))) == null) {//分段已上传信息
                    fileDetail = this.upload(file, dir);
                    filePartService.save(FileDetailKey.newInstance(fileDetail.getAbsolutePath(), fileDetail.getFileManagerId()), entireFileHash, partFileHash, total, index);
                }
                //查询上传的片段
                List<FilePart> fileParts = filePartService.find(entireFileHash);
                FilePart part = ObjectUtil.remove(fileParts, "index", 0);
                FileDetail entireFileDetail = this.uploadPart(part, filePart, fileParts, contentType, entireFileName, entireFileDir, entireFileHash, total, fileManager);
                if (entireFileDetail != null) {
                    fileDetail = entireFileDetail;
                }
            } else {
                fileDetail = this.upload(file, dir);
            }
            return fileDetail;
        } catch (RuntimeException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 文件上传方法
     * <br/>dir 往下为分段上传参数
     *
     * @param attach         附件信息
     * @param contentType    附件类型
     * @param fileName       附件名称
     * @param dir            附件上传目录Id
     * @param entireFileName 完整文件名称
     * @param entireFileDir  附件完整文件的上传目录信息
     * @param entireFileHash 文件hash值
     * @param partFileHash   分段文件的hash值
     * @param total          分段上传时的总段数
     * @param index          当前片段
     * @return FileDetail
     * @throws IOException
     */
    public FileDetail upload(File attach, String contentType, String fileName, String dir, String entireFileName, String entireFileDir, String entireFileHash, String partFileHash, Integer total, Integer index) throws IOException {
        try {
            //判断是否为分段上传
            boolean isPart = StringUtil.isNotBlank(entireFileHash) && StringUtil.isNotBlank(partFileHash) && StringUtil.isNotBlank(entireFileName) && StringUtil.isNotBlank(entireFileDir) && ObjectUtil.isNotNull(total) && StringUtil.isNotNull(index);
            //生成分段上传的文件名
            if (isPart && "blob".equalsIgnoreCase(fileName)) {
                fileName = entireFileName + ".part" + StringUtil.addZeroLeft(index.toString(), total.toString().length());
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("上传文件参数:{fileName:" + contentType + ",contentType:" + fileName + ",dir:" + dir + ",isPart:" + isPart + "}");
            }

            //上传文件信息
            FileDetail fileDetail;

            if (isPart) {//如果为分段上传
                //获取文件上传目录的配置信息
                Directory directory = fileService.getDirectory(dir);
                FileManager fileManager = fileManagerFactory.getUploadFileManager(directory.getFileManager().getId());

                FilePart filePart = filePartService.findByPartFileHash(entireFileHash, partFileHash);
                if (filePart == null || (fileDetail = fileService.get(FileDetailKey.newInstance(filePart.getAbsolutePath(), filePart.getFileManagerId()))) == null) {//分段已上传信息
                    fileDetail = this.upload(attach, contentType, fileName, dir);
                    filePartService.save(FileDetailKey.newInstance(fileDetail.getAbsolutePath(), fileDetail.getFileManagerId()), entireFileHash, partFileHash, total, index);
                }
                //查询上传的片段
                List<FilePart> fileParts = filePartService.find(entireFileHash);
                FilePart part = ObjectUtil.remove(fileParts, "index", 0);
                FileDetail entireFileDetail = this.uploadPart(part, filePart, fileParts, contentType, entireFileName, entireFileDir, entireFileHash, total, fileManager);
                if (entireFileDetail != null) {
                    fileDetail = entireFileDetail;
                }
            } else {
                fileDetail = this.upload(attach, contentType, fileName, dir);
            }
            return fileDetail;
        } catch (RuntimeException e) {
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    private FileDetail upload(MultipartFile file, String dir) throws IOException {
        InputStream input = file.getInputStream();
        if (input.markSupported()) {
            return upload(input, file.getContentType(), file.getOriginalFilename(), file.getSize(), dir);
        } else {
            File temp = null;
            try {
                temp = FileUtil.tmp();
                StreamUtil.copyThenClose(input, new FileOutputStream(temp));
                return this.upload(temp, file.getContentType(), file.getOriginalFilename(), dir);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                throw e;
            } finally {
                if (temp != null) {
                    FileUtil.delFile(temp);
                }
            }
        }
    }

    private Map<String, String> extensions() {
        return new HashMap<String, String>() {
            {
                this.put("image/jpeg", "jpg");
                this.put("image/gif", "gif");
                this.put("image/png", "png");
                this.put("mage/bmp", "bmp");
            }
        };
    }

    private FileDetail upload(InputStream input, String contentType, String fileName, long size, String dir) throws IOException {
        Directory directory = this.fileService.getDirectory(dir);
        // 设置 mask
        input.mark((int) size + 1);
        // 获取文件Md5码
        String md5 = DigestUtils.md5DigestAsHex(input);
        // 获取MimeType
        input.reset();
        String mimeType = FileUtil.getMimeType(input);
        // 通过 mimeType 纠正后缀名
        Map<String, String> extensions = extensions();
        // 获取虚拟目录
        String absolutePath = directory.getDirPath() + separator + DateUtil.format("yyyyMMdd") + separator + StringUtil.hexTo64("0" + UUID.randomUUID().toString().replaceAll("-", "")) + "." + StringUtil.defaultValue(extensions.get(mimeType), WebUtil.getExtension(fileName));
        // 文件类型
        FileDetail fileDetail;
        // 获取真实目录
        String realPath;

        String fileManagerId = directory.getFileManager().getId();

        FileManager fileManager = fileManagerFactory.getFileManager(fileManagerId);

        fileDetail = fileService.getFileDetailByMd5(md5, fileManagerId);
        if (fileDetail == null || fileManager.getFileItem(fileDetail.getRealPath()) == null) {
            realPath = separator + mimeType + separator + StringUtil.hexTo64("0" + md5) + "." + StringUtil.defaultValue(extensions.get(mimeType), WebUtil.getExtension(fileName));
            input.reset();
            fileManager.writeFile(realPath, input);
        } else {
            realPath = fileDetail.getRealPath();
        }
        return fileService.saveFileDetail(absolutePath, fileName, contentType, size, md5, realPath, fileManagerId, "");
    }

    public FileDetail upload(File attach, String contentType, String fileName, String dir) throws IOException {
        Directory directory = this.fileService.getDirectory(dir);
        //获取文件Md5码
        String md5 = DigestUtils.md5DigestAsHex(new FileInputStream(attach));// 获取文件MD5

        String mimeType = FileUtil.getMimeType(attach);

        //通过 mimeType 纠正后缀名
        Map<String, String> extensions = extensions();

        // 获取虚拟目录
        String absolutePath = directory.getDirPath() + separator + DateUtil.format("yyyyMMdd") + separator + StringUtil.hexTo64("0" + UUID.randomUUID().toString().replaceAll("-", "")) + "." + StringUtil.defaultValue(extensions.get(mimeType), WebUtil.getExtension(fileName));
        // 文件类型
        FileDetail fileDetail;
        // 获取真实目录
        String realPath;

        String fileManagerId = directory.getFileManager().getId();

        FileManager fileManager = fileManagerFactory.getFileManager(fileManagerId);

        fileDetail = fileService.getFileDetailByMd5(md5, fileManagerId);
        if (fileDetail == null || fileManager.getFileItem(fileDetail.getRealPath()) == null) {
            realPath = separator + mimeType + separator + StringUtil.hexTo64("0" + md5) + "." + StringUtil.defaultValue(extensions.get(mimeType), WebUtil.getExtension(fileName));
            fileManager.writeFile(realPath, attach);
        } else {
            realPath = fileDetail.getRealPath();
        }
        return fileService.saveFileDetail(absolutePath, fileName, contentType, attach.length(), md5, realPath, fileManagerId, "");
    }

    public FileDetail upload(File attach, String absolutePath, String fileManagerId) throws IOException {
        //获取文件Md5码
        String md5 = DigestUtils.md5DigestAsHex(new FileInputStream(attach));// 获取文件MD5

        String mimeType = FileUtil.getMimeType(attach);

        //通过 mimeType 纠正后缀名
        Map<String, String> extensions = extensions();
        // 文件类型
        FileDetail fileDetail;
        // 获取真实目录
        String realPath;

        String fileName = RegexpUtil.parseFirst(absolutePath, "[^/]+$");

        FileManager fileManager = fileManagerFactory.getFileManager(fileManagerId);

        fileDetail = fileService.getFileDetailByMd5(md5, fileManagerId);
        if (fileDetail == null || fileManager.getFileItem(fileDetail.getRealPath()) == null) {
            realPath = separator + mimeType + separator + StringUtil.hexTo64("0" + md5) + "." + StringUtil.defaultValue(extensions.get(mimeType), WebUtil.getExtension(fileName));
            fileManager.writeFile(realPath, attach);
        } else {
            realPath = fileDetail.getRealPath();
        }
        return fileService.saveFileDetail(absolutePath, fileName, mimeType, attach.length(), md5, realPath, fileManagerId, "");
    }

}
