package org.salgar.wot.clan;

import java.util.ArrayList;

import org.salgar.wot.clan.model.Vehicle;

public class Constants {
	public final ArrayList<Vehicle> INTERESTED_VEHICLES = new ArrayList<Vehicle>();
	
	public Constants() {
		//Soviet Arty
		INTERESTED_VEHICLES.add(new Vehicle("S-51", 6));
		INTERESTED_VEHICLES.add(new Vehicle("SU-14", 6));
		INTERESTED_VEHICLES.add(new Vehicle("Object 212", 7));
		INTERESTED_VEHICLES.add(new Vehicle("Object 261", 8));
		
		//Soviet Heavy
		INTERESTED_VEHICLES.add(new Vehicle("IS-7", 10));
		INTERESTED_VEHICLES.add(new Vehicle("IS-4", 9));
		
		//Soviet Medium
		INTERESTED_VEHICLES.add(new Vehicle("T-54", 9));
		INTERESTED_VEHICLES.add(new Vehicle("T-44", 9));
		
		//Soviet TD
		INTERESTED_VEHICLES.add(new Vehicle("Object 704", 9));
		INTERESTED_VEHICLES.add(new Vehicle("ISU-152", 8));
		
		//German Arty
		INTERESTED_VEHICLES.add(new Vehicle("GW Panther", 6));
		INTERESTED_VEHICLES.add(new Vehicle("GW Tiger", 7));
		INTERESTED_VEHICLES.add(new Vehicle("Gw typ E", 8));
		
		//German Heavy
		INTERESTED_VEHICLES.add(new Vehicle("Maus", 10));
		INTERESTED_VEHICLES.add(new Vehicle("VK4502", 9));
		
		//German Medium
		INTERESTED_VEHICLES.add(new Vehicle("Panther II", 9));
		INTERESTED_VEHICLES.add(new Vehicle("Panther", 8));
		
		//German TD
		INTERESTED_VEHICLES.add(new Vehicle("Jagdtiger", 9));
		INTERESTED_VEHICLES.add(new Vehicle("Ferdinand", 8));
		
		//American Arty
		INTERESTED_VEHICLES.add(new Vehicle("M12", 6));
		INTERESTED_VEHICLES.add(new Vehicle("M40/M43", 7));
		INTERESTED_VEHICLES.add(new Vehicle("T92", 8));
		
		//American Heavy
		INTERESTED_VEHICLES.add(new Vehicle("T30", 10));
		INTERESTED_VEHICLES.add(new Vehicle("T34", 9));
		INTERESTED_VEHICLES.add(new Vehicle("T32", 8));
		
		//American Medium
		INTERESTED_VEHICLES.add(new Vehicle("M26 Pershing", 9));
		INTERESTED_VEHICLES.add(new Vehicle("T23", 8));
		
	}
}
