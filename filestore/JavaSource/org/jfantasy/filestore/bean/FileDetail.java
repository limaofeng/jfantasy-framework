package org.jfantasy.filestore.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.util.common.ObjectUtil;

import javax.persistence.*;

/**
 * 文件信息表
 *
 * @author 软件
 */
@Entity
@Table(name = "FILE_FILEDETAIL")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "folder", "real_path", "namespace", "md5"})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FileDetail extends BaseBusEntity implements Cloneable {

    private static final long serialVersionUID = -3377507500960127984L;

    /**
     * 虚拟文件路径
     */
    @Id
    @Column(name = "ABSOLUTE_PATH", nullable = false, updatable = false, length = 250)
    private String path;

    @Column(name = "FILE_MANAGER_CONFIG_ID", nullable = false, updatable = false, length = 50)
    private String namespace;
    /**
     * 文件名称
     */
    @JsonProperty("name")
    @Column(name = "FILE_NAME", length = 150)
    private String name;
    /**
     * 文件后缀名
     */
    @Column(name = "EXT", length = 20)
    private String ext;
    /**
     * 文件类型
     */
    @Column(name = "CONTENT_TYPE", length = 50)
    private String contentType;
    /**
     * 描述
     */
    @Column(name = "DESCRIPTION", length = 250)
    private String description;
    /**
     * 文件长度
     */
    @Column(name = "LENGTH")
    private Long size;
    /**
     * 文件MD5码
     */
    @Column(name = "MD5", length = 50)
    private String md5;
    /**
     * 文件真实路径
     */
    @Column(name = "REAL_PATH", length = 250)
    private String realPath;
    /**
     * 文件夹
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(value = {@JoinColumn(name = "FOLDER_PATH", referencedColumnName = "ABSOLUTE_PATH"), @JoinColumn(name = "FOLDER_MANAGER_CONFIG_ID", referencedColumnName = "FILE_MANAGER_CONFIG_ID")})
    private Folder folder;

    /**
     * 设置 文件路径(文件系统中的路径，非虚拟路径)
     *
     * @param path 文件路径
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取 文件路径(文件系统中的路径，非虚拟路径)
     *
     * @return java.lang.String
     */
    public String getPath() {
        return this.path;
    }

    /**
     * 设置 文件名称
     *
     * @param name 文件名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取 文件名称
     *
     * @return java.lang.String
     */
    public String getName() {
        return this.name;
    }

    /**
     * 设置 文件类型
     *
     * @param contentType 文件类型
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * 获取 文件类型
     *
     * @return java.lang.String
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * 设置 描述
     *
     * @param description 文件描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取 描述
     *
     * @return java.lang.String
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * 设置 文件长度
     *
     * @param size 文件大小
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * 获取 文件长度
     *
     * @return java.lang.Long
     */
    public Long getSize() {
        return this.size;
    }

    /**
     * 设置 文件夹ID
     *
     * @param folder 文件夹
     */
    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    /**
     * 获取 文件夹ID
     *
     * @return Folder
     */
    public Folder getFolder() {
        return this.folder;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getExt() {
        return this.ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    @Transient
    public String getKey() {
        return this.namespace + ":" + this.path;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return ObjectUtil.clone(this);
    }

    @Override
    public String toString() {
        return "FileDetail{" +
                "path='" + path + '\'' +
                ", namespace='" + namespace + '\'' +
                ", name='" + name + '\'' +
                ", ext='" + ext + '\'' +
                ", contentType='" + contentType + '\'' +
                ", description='" + description + '\'' +
                ", size=" + size +
                ", md5='" + md5 + '\'' +
                ", realPath='" + realPath + '\'' +
                '}';
    }

}