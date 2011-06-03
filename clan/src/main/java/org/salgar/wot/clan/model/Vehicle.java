package org.salgar.wot.clan.model;

public class Vehicle implements Comparable<Vehicle> {
	private String name;
	private Integer tier;
	private Clazz clazz;
	private Nation nation;
	

	public Nation getNation() {
		return nation;
	}

	public void setNation(Nation nation) {
		this.nation = nation;
	}

	public Clazz getClazz() {
		return clazz;
	}

	public void setClazz(Clazz clazz) {
		this.clazz = clazz;
	}

	public Vehicle(String name, int tier, Clazz clazz, Nation nation) {
		super();
		this.name = name;
		this.tier = tier;
		this.clazz = clazz;
		this.nation = nation;
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

	public enum Clazz {
		HEAVY("Heavy"), MED("Med"), LIGHT("Light"), SPG("SPG"), TD("TD");

		private String name;

		private Clazz(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}
	
	public enum Nation {
		USSR("ussr"), US("us"), GER("ger");
		
		private String name;
		
		private Nation(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
	}
}
