package org.salgar.wot.clan.util;

import java.util.Comparator;

import org.salgar.wot.clan.model.Vehicle;
import org.salgar.wot.clan.model.Vehicle.Clazz;
import org.salgar.wot.clan.model.Vehicle.Nation;

public class ClanTankPopulationComparator implements Comparator<Vehicle> {

	public int compare(Vehicle o1, Vehicle o2) {
		if (o1.getName().equals(o2.getName())) {
			return 0;
		}
		if (o1.getTier().compareTo(o2.getTier()) == 0) {
			if (o1.getClazz().equals(Clazz.HEAVY) && !o2.getClazz().equals(Clazz.HEAVY)) {
				return 1;
			} else if (o1.getClazz().equals(Clazz.MED)
					&& (o2.getClazz().equals(Clazz.SPG) || o2.getClazz().equals(
							Clazz.TD))) {
				return 1;
			} else if (o1.getClazz().equals(Clazz.TD) && o2.getClazz().equals(Clazz.SPG)) {
				return 1;
			} else if (o1.getClazz().equals(Clazz.TD)
					&& (o2.getClazz().equals(Clazz.MED) || o2.getClazz()
							.equals(Clazz.HEAVY))) {
				return 1;
			} else if (o1.getClazz().equals(Clazz.SPG)
					&& !o2.getClazz().equals(Clazz.SPG)) {
				return 1;
			}

			if (o1.getNation().equals(Nation.USSR)) {
				return -1;
			} else if (o1.getNation().equals(Nation.GER)
					&& o2.getNation().equals(Nation.US)) {
				return -1;
			} else if (o1.getNation().equals(Nation.US)) {
				return 1;
			} else {
				return 0;
			}
		}
		return o1.getTier().compareTo(o2.getTier());
	}

}
