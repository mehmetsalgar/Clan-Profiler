package org.salgar.wot.clan.model;

public class MemberTank implements Comparable<MemberTank> {
	private Vehicle vehicle;
	private String battles;
	private String victories;

	public String getBattles() {
		return battles;
	}

	public void setBattles(String battles) {
		this.battles = battles;
	}

	public String getVictories() {
		return victories;
	}

	public void setVictories(String victories) {
		this.victories = victories;
	}

	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder
				.reflectionToString(this);
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public int compareTo(MemberTank o) {
		return vehicle.compareTo(o.getVehicle());
	}
}
