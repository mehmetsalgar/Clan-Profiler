package org.salgar.wot.clan.model;

public class Vehicle implements Comparable<Vehicle> {
	private String name;
	private Integer tier;
	private Clazz clazz;
	private Nation nation;
	private Integer order;

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

	public Vehicle(String name, int tier, Clazz clazz, Nation nation, Integer order) {
		super();
		this.name = name;
		this.tier = tier;
		this.clazz = clazz;
		this.nation = nation;
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTier() {
		return tier;
	}

	public void setTier(Integer tier) {
		this.tier = tier;
	}

	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder
				.reflectionToString(this);
	}

	public int compareTo(Vehicle o) {
//		if (tier.compareTo(o.getTier()) == 0) {
//			if (clazz.equals(Clazz.HEAVY) && !o.getClazz().equals(Clazz.HEAVY)) {
//				return -1;
//			} else if (clazz.equals(Clazz.MED) && (o.getClazz().equals(Clazz.SPG) || o.getClazz().equals(Clazz.TD))) {
//				return -1;
//			}  else if (clazz.equals(Clazz.TD) && o.getClazz().equals(Clazz.SPG)) {
//				return -1;
//			} else if (clazz.equals(Clazz.TD) && (o.getClazz().equals(Clazz.MED) || o.getClazz().equals(Clazz.HEAVY))) {
//				return 1;
//			} else if (clazz.equals(Clazz.SPG) && !o.getClazz().equals(Clazz.SPG)) {
//				return 1;
//			} 
//			
//			
//			if(nation.equals(Nation.USSR)) {
//				return -1;
//			} else if (nation.equals(Nation.GER) && o.getNation().equals(Nation.US)) {
//				return -1;
//			} else if (nation.equals(Nation.US)){
//				return 1;
//			} else {
//				return 0;
//			}
//		}
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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vehicle)) {
			return false;
		}
		Vehicle tmp = (Vehicle) obj;
		return tmp.getName().equals(this.name);
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}
