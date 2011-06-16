package org.salgar.wot.clan.util;

import java.util.Comparator;

import org.salgar.wot.clan.model.Vehicle;

public class VehicleOrderComparator implements Comparator<Vehicle> {

	public int compare(Vehicle o1, Vehicle o2) {
		return o1.getOrder().compareTo(o2.getOrder());
	}

}
