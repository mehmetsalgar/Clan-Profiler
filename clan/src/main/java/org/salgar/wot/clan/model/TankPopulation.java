package org.salgar.wot.clan.model;

public class TankPopulation {
	private int topTank = 0;
	private int alternativeTank = 0;

	public int getTopTank() {
		return topTank;
	}

	public void setTopTank(int topTank) {
		this.topTank = topTank;
	}

	public int getAlternativeTank() {
		return alternativeTank;
	}

	public void setAlternativeTank(int alternativeTank) {
		this.alternativeTank = alternativeTank;
	}
	
	@Override
	public String toString() {
		return org.apache.commons.lang.builder.ToStringBuilder
				.reflectionToString(this);
	}
}
