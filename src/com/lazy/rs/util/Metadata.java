package com.lazy.rs.util;

import com.lazy.rs.annotation.KeyType;

/**
 * 
 * This Class is used to cache the metadata for classes that are 
 * declared as @Table.
 * @author rahulnjs
 *
 */

public class Metadata {
	private String insertQuery;
	private String updateQuery;
	private String sequnce;
	private String suffix;
	private String prefix;
	private String tableName;
	private KeyType keyType;
	private String primaryKey;
	
	
	public String getPrimaryKey() {
		return primaryKey;
	}
	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}
	public KeyType getKeyType() {
		return keyType;
	}
	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}

	public String getInsertQuery() {
		return insertQuery;
	}
	public void setInsertQuery(String insertQuery) {
		this.insertQuery = insertQuery;
	}
	public String getUpdateQuery() {
		return updateQuery;
	}
	public void setUpdateQuery(String updateQuery) {
		this.updateQuery = updateQuery;
	}
	public String getSequnce() {
		return sequnce;
	}
	public void setSequnce(String sequnce) {
		this.sequnce = sequnce;
	}
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	public String toString() {
		return "Metadata [insertQuery=" + insertQuery + ", updateQuery="
				+ updateQuery + ", sequnce=" + sequnce + ", suffix=" + suffix
				+ ", prefix=" + prefix + ", tableName=" + tableName
				+ ", keyType=" + keyType + "]";
	}
	
}
