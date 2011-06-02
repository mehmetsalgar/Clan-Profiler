package org.salgar.wot.clan.model;

import java.util.ArrayList;

public class Clan {
	private String name;
	private String nick;
	private String id;
	private ArrayList<ClanMember> clanMembers = new ArrayList<ClanMember>(100);
	private ArrayList<String> concurrentBattles = new ArrayList<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
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

	public ArrayList<ClanMember> getClanMembers() {
		return clanMembers;
	}

	public void setClanMembers(ArrayList<ClanMember> clanMembers) {
		this.clanMembers = clanMembers;
	}

	public ArrayList<String> getConcurrentBattles() {
		return concurrentBattles;
	}

	public void setConcurrentBattles(ArrayList<String> concurrentBattles) {
		this.concurrentBattles = concurrentBattles;
	}
}
