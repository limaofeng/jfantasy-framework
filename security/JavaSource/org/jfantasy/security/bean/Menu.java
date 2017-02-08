package org.jfantasy.security.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.security.bean.databind.MenuDeserializer;
import org.jfantasy.security.bean.databind.MenuSerializer;
import org.jfantasy.security.bean.enums.MenuType;

import javax.persistence.*;
import java.util.List;

/**
 * 菜单
 */
@Entity
@Table(name = "AUTH_MENU")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler", "children"})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Menu extends BaseBusEntity {

    private static final long serialVersionUID = -3361634609328758218L;

    public static final String PATH_SEPARATOR = "/";// 树路径分隔符

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private String id;
    /**
     * 菜单名称
     */
    @Column(name = "NAME", length = 200)
    private String name;
    /**
     * 树路径
     */
    @Column(name = "PATH", nullable = false, length = 200)
    private String path;
    /**
     * 菜单值
     */
    @Column(name = "VALUE", length = 200)
    private String value;
    /**
     * 菜单类型
     */
    @Column(name = "TYPE", length = 20)
    @Enumerated(EnumType.STRING)
    private MenuType type;
    /**
     * 菜单对应的图标
     */
    @Column(name = "ICON", length = 50)
    private String icon;
    /**
     * 菜单描述
     */
    @Column(name = "DESCRIPTION", length = 2000)
    private String description;
    /**
     * 层级
     */
    @Column(name = "LAYER", nullable = false)
    private Integer layer;
    /**
     * 排序字段
     */
    @Column(name = "SORT")
    private Integer sort;
    /**
     * 下级菜单
     */
    @JsonInclude(content = JsonInclude.Include.NON_NULL)
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    @OrderBy("sort ASC")
    private List<Menu> children;
    /**
     * 上级菜单
     */
    @JsonProperty("parent_id")
    @JsonSerialize(using = MenuSerializer.class)
    @JsonDeserialize(using = MenuDeserializer.class)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "PID", foreignKey = @ForeignKey(name = "FK_AUTH_MENU_PID"))
    private Menu parent;

    public Menu() {
    }

    public Menu(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(MenuType type) {
        this.type = type;
    }

    public MenuType getType() {
        return this.type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getSort() {
        return this.sort;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public void setChildren(List<Menu> children) {
        this.children = children;
    }

    public Integer getLayer() {
        return layer;
    }

    public void setLayer(Integer layer) {
        this.layer = layer;
    }

    public void setParent(Menu parent) {
        this.parent = parent;
    }

    public Menu getParent() {
        return this.parent;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}