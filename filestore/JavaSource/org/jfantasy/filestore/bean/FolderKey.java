package org.jfantasy.filestore.bean;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Column;
import java.io.Serializable;

public class FolderKey implements Serializable {

	private static final long serialVersionUID = 6090047858630400968L;

	@Column(name = "ABSOLUTE_PATH", nullable = false, insertable = true, updatable = false,length = 250)
	private String path;

	@Column(name = "FILE_MANAGER_CONFIG_ID", nullable = false, insertable = true, updatable = false,length = 50)
	private String namespace;

	public FolderKey() {
	}

	public FolderKey(String path, String namespace) {
		super();
		this.path = path;
		this.namespace = namespace;
	}

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

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(this.getNamespace()).append(this.getPath()).toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FolderKey) {
			FolderKey key = (FolderKey) o;
			return new EqualsBuilder().appendSuper(super.equals(o)).append(this.getNamespace(), key.getNamespace()).append(this.getPath(), key.getPath()).isEquals();
		}
		return false;
	}

}
