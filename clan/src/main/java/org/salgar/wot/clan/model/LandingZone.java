package org.salgar.wot.clan.model;

import java.util.ArrayList;

public class LandingZone {
	private String name;
	private String url;
	private Region region;
	private int battleStart;
	private ArrayList<Clan> clanList;
	
	public LandingZone(String name, String url, Region region, int battleStart, ArrayList<Clan> clanList) {
		this.name = name;
		this.url = url;
		this.region = region;
		this.battleStart = battleStart;
		this.clanList = clanList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ArrayList<Clan> getClanList() {
		return clanList;
	}

	public void setClanList(ArrayList<Clan> clanList) {
		this.clanList = clanList;
	}
	
	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder
				.reflectionToString(this);
	}
	
	public enum Region {
		EU("EU"), MED("MED");
		
		private String name;
		
		private Region(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public int getBattleStart() {
		return battleStart;
	}

	public void setBattleStart(int battleStart) {
		this.battleStart = battleStart;
	}
}


