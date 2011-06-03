package org.salgar.wot.clan;

import java.util.ArrayList;

import org.salgar.wot.clan.model.Vehicle;
import org.salgar.wot.clan.model.Vehicle.Clazz;
import org.salgar.wot.clan.model.Vehicle.Nation;

public class Constants {
	public final ArrayList<Vehicle> INTERESTED_VEHICLES = new ArrayList<Vehicle>();

	public Constants() {
		// Soviet Arty
		INTERESTED_VEHICLES.add(new Vehicle("S-51", 6, Clazz.SPG, Nation.USSR));
		INTERESTED_VEHICLES.add(new Vehicle("SU-14", 6, Clazz.SPG, Nation.USSR));
		INTERESTED_VEHICLES.add(new Vehicle("Object 212", 7, Clazz.SPG, Nation.USSR));
		INTERESTED_VEHICLES.add(new Vehicle("Object 261", 8, Clazz.SPG, Nation.USSR));

		// Soviet Heavy
		INTERESTED_VEHICLES.add(new Vehicle("IS-7", 10, Clazz.HEAVY, Nation.USSR));
		INTERESTED_VEHICLES.add(new Vehicle("IS-4", 9, Clazz.HEAVY, Nation.USSR));

		// Soviet Medium
		INTERESTED_VEHICLES.add(new Vehicle("T-54", 9, Clazz.MED, Nation.USSR));
		INTERESTED_VEHICLES.add(new Vehicle("T-44", 8, Clazz.MED, Nation.USSR));

		// Soviet TD
		INTERESTED_VEHICLES.add(new Vehicle("Object 704", 9, Clazz.TD, Nation.USSR));
		INTERESTED_VEHICLES.add(new Vehicle("ISU-152", 8, Clazz.TD, Nation.USSR));

		// German Arty
		INTERESTED_VEHICLES.add(new Vehicle("GW Panther", 6, Clazz.SPG, Nation.GER));
		INTERESTED_VEHICLES.add(new Vehicle("GW Tiger", 7, Clazz.SPG, Nation.GER));
		INTERESTED_VEHICLES.add(new Vehicle("Gw typ E", 8, Clazz.SPG, Nation.GER));

		// German Heavy
		INTERESTED_VEHICLES.add(new Vehicle("Maus", 10, Clazz.HEAVY, Nation.GER));
		INTERESTED_VEHICLES.add(new Vehicle("VK4502 (P) Ausf. B", 9,
				Clazz.HEAVY, Nation.GER));

		// German Medium
		INTERESTED_VEHICLES.add(new Vehicle("Panther II", 9, Clazz.MED, Nation.GER));
		INTERESTED_VEHICLES.add(new Vehicle("Panther", 8, Clazz.MED, Nation.GER));

		// German TD
		INTERESTED_VEHICLES.add(new Vehicle("Jagdtiger", 9, Clazz.TD, Nation.GER));
		INTERESTED_VEHICLES.add(new Vehicle("Ferdinand", 8, Clazz.TD, Nation.GER));

		// American Arty
		INTERESTED_VEHICLES.add(new Vehicle("M12", 6, Clazz.SPG, Nation.US));
		INTERESTED_VEHICLES.add(new Vehicle("M40/M43", 7, Clazz.SPG, Nation.US));
		INTERESTED_VEHICLES.add(new Vehicle("T92", 8, Clazz.SPG, Nation.US));

		// American Heavy
		INTERESTED_VEHICLES.add(new Vehicle("T30", 10, Clazz.HEAVY, Nation.US));
		INTERESTED_VEHICLES.add(new Vehicle("T34", 9, Clazz.HEAVY, Nation.US));
		INTERESTED_VEHICLES.add(new Vehicle("T32", 8, Clazz.HEAVY, Nation.US));

		// American Medium
		INTERESTED_VEHICLES.add(new Vehicle("M26 Pershing", 9, Clazz.MED, Nation.US));
		INTERESTED_VEHICLES.add(new Vehicle("T23", 8, Clazz.MED, Nation.US));

	}
}
