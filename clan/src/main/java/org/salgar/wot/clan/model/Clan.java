package org.salgar.wot.clan.model;

import java.util.ArrayList;

public class Clan {
	private String name;
	private String nick;
	private String id;
	private ArrayList<ClanMember> clanMembers = new ArrayList<ClanMember>(100);
	private ArrayList<Battle> concurrentBattles = new ArrayList<Battle>();

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

	public ArrayList<Battle> getConcurrentBattles() {
		return concurrentBattles;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Clan)) {
			return false;
		}
		Clan tmp = (Clan) obj;

		return this.name.equals(tmp.getName());
	}

}
