package org.jfantasy.filestore.manager;

import org.jfantasy.filestore.FileItem;
import org.jfantasy.filestore.FileItemFilter;
import org.jfantasy.filestore.FileItemSelector;
import org.jfantasy.filestore.FileManager;
import org.jfantasy.filestore.bean.FileDetail;
import org.jfantasy.filestore.bean.FileManagerConfig;
import org.jfantasy.filestore.bean.Folder;
import org.jfantasy.filestore.service.FilePartService;
import org.jfantasy.filestore.service.FileService;
import org.jfantasy.framework.error.IgnoreException;

import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 上传文件管理器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-10-31 下午5:12:36
 */
public class UploadFileManager implements FileManager {

    private FileManagerConfig config;
    /**
     * 对应的真实文件管理器
     */
    protected FileManager source;

    @Autowired
    private FileService fileService;
    @Autowired
    private FilePartService filePartService;

    public FileManagerConfig getConfig() {
        return config;
    }

    public void setConfig(FileManagerConfig config) {
        this.config = config;
    }

    public FileManager getSource() {
        return source;
    }

    public void setSource(FileManager source) {
        this.source = source;
    }

    public void readFile(String remotePath, String localPath) throws IOException {
        this.getSource().readFile(getRealPath(remotePath), localPath);
    }

    public void readFile(String remotePath, OutputStream out) throws IOException {
        this.getSource().readFile(getRealPath(remotePath), out);
    }

    public InputStream readFile(String remotePath) throws IOException {
        return this.getSource().readFile(getRealPath(remotePath));
    }

    public void writeFile(String remotePath, File file) throws IOException {
        FileItem fileItem = this.getSource().getFileItem(remotePath);
        if (fileItem == null || fileItem.getSize() != file.length()) {
            this.getSource().writeFile(remotePath, file);
        }
    }

    @Deprecated
    public void writeFile(String remotePath, InputStream in) throws IOException {
        this.getSource().writeFile(remotePath, in);
    }

    @Deprecated
    public OutputStream writeFile(String remotePath) throws IOException {
        return this.getSource().writeFile(remotePath);
    }

    private String getRealPath(String remotePath) {
        FileDetail fileDetail = this.fileService.get(remotePath);
        if (fileDetail == null) {
            return "";
        }
        return fileDetail.getRealPath();
    }

    public List<FileItem> listFiles() {
        return this.getSource().listFiles();
    }

    @SuppressWarnings("unchecked")
    public List<FileItem> listFiles(String remotePath) {
        FileDetail fileDetail = this.fileService.get(remotePath);
        FileItem fileItem = retrieveFileItem(remotePath, fileDetail == null);
        return fileItem == null ? Collections.EMPTY_LIST : fileItem.listFileItems();
    }

    private FileItem retrieveFileItem(final String absolutePath, final boolean dir) {
        if (dir) {
            Folder folder = this.fileService.getFolder(absolutePath, this.config.getId());
            if (folder == null) {
                return null;
            }
            return new UploadFileItem(folder, this);
        } else {
            FileDetail fileDetail = this.fileService.get(absolutePath);
            if (fileDetail == null) {
                return null;
            }
            return new UploadFileItem(fileDetail, this);
        }
    }

    protected static class UploadFileItem implements FileItem {
        private UploadFileManager uploadFileManager;
        private boolean dir = false;
        private FileDetail fileDetail;
        private Folder folder;

        public UploadFileItem(FileDetail fileDetail, UploadFileManager uploadFileManager) {
            this.fileDetail = fileDetail;
            this.uploadFileManager = uploadFileManager;
        }

        public UploadFileItem(Folder folder, UploadFileManager uploadFileManager) {
            this.folder = folder;
            this.dir = true;
            this.uploadFileManager = uploadFileManager;
        }

        public List<FileItem> listFileItems(FileItemSelector selector) {
            if (!this.isDirectory()) {
                return new ArrayList<>();
            }
            return FileItem.Util.flat(this.listFileItems(), selector);
        }

        @Override
        public Metadata getMetadata() {
            return null;
        }

        public List<FileItem> listFileItems(FileItemFilter filter) {
            List<FileItem> fileItems = new ArrayList<FileItem>();
            if (!this.isDirectory()) {
                return fileItems;
            }
            for (FileItem item : listFileItems()) {
                if (filter.accept(item)) {
                    fileItems.add(item);
                }
            }
            return fileItems;
        }

        public List<FileItem> listFileItems() {
            List<FileItem> fileItems = new ArrayList<FileItem>();
            if (!this.isDirectory()) {
                return fileItems;
            }
            for (Folder folder : uploadFileManager.fileService.listFolder(this.folder.getPath(), this.folder.getNamespace(), "name")) {
                fileItems.add(new UploadFileItem(folder, uploadFileManager));
            }
            for (FileDetail folder : uploadFileManager.fileService.listFileDetail(this.folder.getPath(), this.folder.getNamespace(), "fileName")) {
                fileItems.add(new UploadFileItem(folder, uploadFileManager));
            }
            return fileItems;
        }

        public Date lastModified() {
            return this.isDirectory() ? this.folder.getModifyTime() : this.fileDetail.getModifyTime();
        }

        public boolean isDirectory() {
            return dir;
        }

        public long getSize() {
            return this.isDirectory() ? 0 : this.fileDetail.getSize();
        }

        public FileItem getParentFileItem() {
            if (this.isDirectory()) {
                return uploadFileManager.retrieveFileItem(this.folder.getParentFolder().getPath(), true);
            } else {
                return uploadFileManager.retrieveFileItem(this.fileDetail.getFolder().getPath(), true);
            }
        }

        public String getName() {
            if (this.isDirectory()) {
                return folder.getName();
            } else {
                return fileDetail.getPath();
            }
        }

        public String getContentType() {
            return this.isDirectory() ? "" : fileDetail.getContentType();
        }

        public String getAbsolutePath() {
            return this.isDirectory() ? this.folder.getPath() : this.fileDetail.getPath();
        }

        public InputStream getInputStream() throws IOException {
            if (this.isDirectory()) {
                throw new IgnoreException("当前对象为一个目录,不能获取 InputStream ");
            }
            return uploadFileManager.source.readFile(this.fileDetail.getRealPath());
        }

    }

    public FileItem getFileItem(String remotePath) {
        return retrieveFileItem(remotePath, false);
    }

    public List<FileItem> listFiles(FileItemSelector selector) {
        return getSource().listFiles(selector);
    }

    public List<FileItem> listFiles(String remotePath, FileItemSelector selector) {
        return getSource().listFiles(remotePath, selector);
    }

    public List<FileItem> listFiles(FileItemFilter filter) {
        return getSource().listFiles(filter);
    }

    public List<FileItem> listFiles(String remotePath, FileItemFilter filter) {
        return getSource().listFiles(remotePath, filter);
    }

    public void removeFile(String remotePath) {
        FileDetail detail = fileService.get(remotePath);
        if (detail == null){
            return;
        }
        fileService.delete(detail.getPath());
        filePartService.delete(detail.getPath());
        FileDetail other = fileService.findUniqueByMd5(detail.getMd5(), this.getConfig().getId());
        if (other == null) {
            getSource().removeFile(detail.getRealPath());
        }
    }

}
