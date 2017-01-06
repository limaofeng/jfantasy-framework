package org.jfantasy.filestore.service;

import org.jfantasy.filestore.bean.*;
import org.jfantasy.filestore.dao.DirectoryDao;
import org.jfantasy.filestore.dao.FileDetailDao;
import org.jfantasy.filestore.dao.FolderDao;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.framework.util.web.WebUtil;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class FileService {

    @Autowired
    private FolderDao folderDao;
    @Autowired
    private FileDetailDao fileDetailDao;
    @Autowired
    private DirectoryDao directoryDao;

    public FileDetail saveFileDetail(String absolutePath, String fileName, String contentType, long length, String md5, String realPath, String fileManagerId, String description) {
        FileDetail fileDetail = new FileDetail();
        fileDetail.setPath(absolutePath);
        fileDetail.setNamespace(fileManagerId);
        fileDetail.setName(fileName);
        fileDetail.setExt(WebUtil.getExtension(fileName));
        fileDetail.setContentType(contentType);
        fileDetail.setSize(length);
        fileDetail.setMd5(md5);
        fileDetail.setFolder(createFolder(absolutePath.replaceFirst("[^\\/]+$", ""), fileManagerId));
        fileDetail.setRealPath(realPath);
        fileDetail.setDescription(description);
        this.fileDetailDao.save(fileDetail);
        return fileDetail;
    }

    public FileDetail update(FileDetail detail) {
        FileDetail fileDetail = this.get(detail.getPath());
        fileDetail.setName(detail.getName());
        fileDetail.setDescription(detail.getDescription());
        this.fileDetailDao.save(fileDetail);
        return fileDetail;
    }

    public Folder getFolder(String absolutePath, String managerId) {
        return this.folderDao.get(new FolderKey(absolutePath, managerId));
    }

    public void delete(String path) {
        this.fileDetailDao.delete(path);
    }

    /**
     * 获取 Folder 对象
     *
     * @param path 路径
     * @return {Folder}
     */
    public Folder createFolder(String path, String namespace) {
        Folder folder = this.folderDao.get(new FolderKey(path, namespace));
        if (ObjectUtil.isNull(folder)) {
            if ("/".equals(path)) {
                folder = createRootFolder(path, namespace);
            } else {
                folder = createFolder(path, createFolder(path.replaceFirst("[^\\/]+\\/$", ""), namespace), namespace);
            }
        }
        return folder;
    }

    public FileDetail findUniqueByMd5(String md5, String managerId) {
        List<FileDetail> fileDetails = this.fileDetailDao.find(Restrictions.eq("fileManagerId", managerId), Restrictions.eq("md5", md5));
        return fileDetails.isEmpty() ? null : fileDetails.get(0);
    }

    public FileDetail findUnique(String absolutePath, String md5, String managerId) {
        return this.fileDetailDao.findUnique(Restrictions.eq("fileManagerId", managerId), Restrictions.eq("absolutePath", absolutePath), Restrictions.eq("md5", md5));
    }

    public FileDetail get(String path) {
        return this.fileDetailDao.get(path);
    }

    private Folder createRootFolder(String absolutePath, String managerId) {
        Folder folder = new Folder();
        folder.setPath(absolutePath);
        folder.setNamespace(managerId);
        folder.setName(RegexpUtil.parseGroup(absolutePath, "([^/]+)\\/$", 1));
        this.folderDao.save(folder);
        return folder;
    }

    /**
     * 获取 Folder 对象
     *
     * @param absolutePath 路径
     * @param parent 上级文件夹
     * @param managerId 文件管理器
     * @return {Folder}
     */
    private Folder createFolder(String absolutePath, Folder parent, String managerId) {
        Folder folder = new Folder();
        folder.setPath(absolutePath);
        folder.setNamespace(managerId);
        folder.setName(RegexpUtil.parseGroup(absolutePath, "([^/]+)\\/$", 1));
        if (ObjectUtil.isNotNull(parent)) {
            folder.setParentFolder(parent);
        }
        this.folderDao.save(folder);
        return folder;
    }

    public List<FileDetail> findFileDetail(Criterion... criterions){
        return this.fileDetailDao.find(criterions);
    }

    public Pager<FileDetail> findFileDetailPager(Pager<FileDetail> pager, List<PropertyFilter> filters) {
        return this.fileDetailDao.findPager(pager, filters);
    }

    public List<Folder> listFolder(String path, String namespace, String orderBy) {
        return this.folderDao.find(new Criterion[]{Restrictions.eq("parentFolder.path", path), Restrictions.eq("parentFolder.namespace", namespace)}, orderBy, "asc");
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public List<FileDetail> listFileDetail(String path, String fileManagerId, String orderBy) {
        return this.fileDetailDao.find(new Criterion[]{Restrictions.eq("folder.path", path), Restrictions.eq("folder.namespace", fileManagerId)}, orderBy, "asc");
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public FileDetail getFileDetailByMd5(String md5, String fileManagerId) {
        List<FileDetail> fileDetails = this.fileDetailDao.find(new Criterion[]{Restrictions.eq("md5", md5), Restrictions.eq("namespace", fileManagerId)}, 0, 1);
        if (fileDetails.isEmpty()) {
            return null;
        }
        return fileDetails.get(0);
    }

    /**
     * TODO 用来转移文件目录<br/> 比如当文章发布时才将图片更新到 http server。一般来说 localization 对应的 FileManager 是不作为上传目录的。只做文件存错。不记录其文件信息
     *
     * @param absolutePath 虚拟目录
     */
    public void localization(String absolutePath) {
        // 1.获取文件源信息
        // 2.获取本地化配置信息
        // 3.开始转移文件
    }

    /**
     * TODO width、heigth只适用于图片
     *
     * @param absolutePath 虚拟目录
     * @param width        宽
     * @param heigth       高
     */
    public void localization(String absolutePath, int width, int heigth) {
        // 1.获取文件源信息
        // 2.获取本地化配置信息
        // 3.压缩图片
        // 4.开始转移文件
    }

    public Directory getDirectory(String dirKey) throws IOException {
        Directory directory = directoryDao.get(dirKey);
        if (directory == null) {
            throw new IOException("目录配置[key=" + dirKey + "]未找到!");
        }
        Hibernate.initialize(directory.getFileManager());
        return directory;
    }

    /**
     * 获取存放文件的绝对路径
     *
     * @param absolutePath  虚拟目录
     * @param fileManagerId 文件管理器Id
     * @return {String}
     */
    public String getAbsolutePath(String absolutePath, String fileManagerId) {
        String ext = WebUtil.getExtension(absolutePath);
        //absolutePath = RegexpUtil.replace(absolutePath, "[^/]+[.]{0}[^.]{0}$","");
        // 去掉后缀名称
        if (RegexpUtil.isMatch(absolutePath, "([/][^/]{1,})([.][^./]{1,})$")) {
            absolutePath = RegexpUtil.replace(absolutePath, "([/][^/]{1,})([.][^./]{1,})$", "$1");
        }
        //可能有效率问题
        List<FileDetail> details = this.fileDetailDao.find(new Criterion[]{Restrictions.like("absolutePath", absolutePath, MatchMode.START), Restrictions.eq("namespace", fileManagerId)}, "absolutePath", "asc");
        if (details.isEmpty() || ObjectUtil.find(details, "absolutePath", absolutePath + "." + ext) == null) {
            return absolutePath + "." + ext;
        }
        for (int i = 1; i <= details.size(); i++) {
            if (ObjectUtil.find(details, "absolutePath", absolutePath + "(" + i + ")." + ext) == null) {
                return absolutePath + "(" + i + ")." + ext;
            }
        }
        return absolutePath;
    }

}
