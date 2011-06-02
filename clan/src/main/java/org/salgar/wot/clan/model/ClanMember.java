package org.salgar.wot.clan.model;

import java.util.ArrayList;

public class ClanMember {
	private String id;
	private String name;
	private String role;
	private ArrayList<MemberTank> memberTanks = new ArrayList<MemberTank>(5);

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder
				.reflectionToString(this);
	}

	public ArrayList<MemberTank> getMemberTanks() {
		return memberTanks;
	}

	public void setMemberTanks(ArrayList<MemberTank> memberTanks) {
		this.memberTanks = memberTanks;
	}
}
