package org.jfantasy.filestore.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.framework.dao.BaseBusEntity;

import javax.persistence.*;


@Entity
@Table(name = "FILE_FILEPART", uniqueConstraints = {@UniqueConstraint(columnNames = {"ENTIRE_FILE_HASH", "PART_FILE_HASH"})})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class FilePart extends BaseBusEntity {

    @Id
    @Column(name = "ABSOLUTE_PATH", nullable = false, updatable = false, length = 250)
    private String path;

    @Column(name = "FILE_MANAGER_CONFIG_ID", nullable = false, updatable = false, length = 50)
    private String namespace;
    /**
     * 完整文件的hash值
     */
    @Column(name = "ENTIRE_FILE_HASH")
    private String entireFileHash;
    /**
     * 片段文件的hash值
     */
    @Column(name = "PART_FILE_HASH")
    private String partFileHash;
    /**
     * 总的段数
     */
    @Column(name = "PAER_TOTAL")
    private Integer total;
    /**
     * 当前段数
     */
    @Column(name = "PAER_INDEX")
    private Integer index;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getEntireFileHash() {
        return entireFileHash;
    }

    public void setEntireFileHash(String entireFileHash) {
        this.entireFileHash = entireFileHash;
    }

    public String getPartFileHash() {
        return partFileHash;
    }

    public void setPartFileHash(String partFileHash) {
        this.partFileHash = partFileHash;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
