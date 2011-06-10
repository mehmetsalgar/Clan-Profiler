package org.salgar.wot.clan.model;

import java.util.Date;

public class Battle {
	private String province;
	private Date date;
	private String id;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder
				.reflectionToString(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Battle)) {
			return false;
		}
		Battle tmp = (Battle) obj;
		
		return id.equals(tmp.getId());
	}
}
