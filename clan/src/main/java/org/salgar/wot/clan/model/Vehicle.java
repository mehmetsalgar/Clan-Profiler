package org.salgar.wot.clan.model;

public class Vehicle implements Comparable<Vehicle>{
	private String name;
	private Integer tier;

	public Vehicle(String name, int tier) {
		super();
		this.name = name;
		this.tier = tier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTier() {
		return tier;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}
	
	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder
				.reflectionToString(this);
	}

	public int compareTo(Vehicle o) {
		return (tier.compareTo(o.getTier())) * -1;
	}
}
