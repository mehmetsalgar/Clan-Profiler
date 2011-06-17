package org.salgar.wot.clan;

import java.util.ArrayList;
import java.util.HashMap;

import org.salgar.wot.clan.model.Clan;
import org.salgar.wot.clan.model.LandingZone;
import org.salgar.wot.clan.model.Vehicle;
import org.salgar.wot.clan.model.LandingZone.Region;
import org.salgar.wot.clan.model.Vehicle.Clazz;
import org.salgar.wot.clan.model.Vehicle.Nation;

public class Constants {
	private static volatile Constants INSTANCE;

	public final ArrayList<Vehicle> INTERESTED_VEHICLES = new ArrayList<Vehicle>();
	public final ArrayList<LandingZone> landingZones = new ArrayList<LandingZone>();
	public final HashMap<String, LandingZone> landingZoneMap = new HashMap<String, LandingZone>();

	public static Constants createInstance() {
		if (INSTANCE == null) {
			INSTANCE = createInternal();
		}

		return INSTANCE;
	}

	private static synchronized Constants createInternal() {
		if (INSTANCE == null) {
			INSTANCE = new Constants();
		}
		return INSTANCE;
	}

	private Constants() {
		// Soviet Arty
		INTERESTED_VEHICLES.add(new Vehicle("S-51", 6, Clazz.SPG, Nation.USSR,
				25));
		INTERESTED_VEHICLES.add(new Vehicle("SU-14", 6, Clazz.SPG, Nation.USSR,
				24));
		INTERESTED_VEHICLES.add(new Vehicle("Object 212", 7, Clazz.SPG,
				Nation.USSR, 21));
		INTERESTED_VEHICLES.add(new Vehicle("Object 261", 8, Clazz.SPG,
				Nation.USSR, 18));

		// Soviet Heavy
		INTERESTED_VEHICLES.add(new Vehicle("IS-7", 10, Clazz.HEAVY,
				Nation.USSR, 1));
		INTERESTED_VEHICLES.add(new Vehicle("IS-4", 9, Clazz.HEAVY,
				Nation.USSR, 4));

		// Soviet Medium
		INTERESTED_VEHICLES.add(new Vehicle("T-54", 9, Clazz.MED, Nation.USSR,
				7));
		INTERESTED_VEHICLES.add(new Vehicle("T-44", 8, Clazz.MED, Nation.USSR,
				12));

		// Soviet TD
		INTERESTED_VEHICLES.add(new Vehicle("Object 704", 9, Clazz.TD,
				Nation.USSR, 10));
		INTERESTED_VEHICLES.add(new Vehicle("ISU-152", 8, Clazz.TD,
				Nation.USSR, 16));

		// German Arty
		INTERESTED_VEHICLES.add(new Vehicle("GW Panther", 6, Clazz.SPG,
				Nation.GER, 26));
		INTERESTED_VEHICLES.add(new Vehicle("GW Tiger", 7, Clazz.SPG,
				Nation.GER, 22));
		INTERESTED_VEHICLES.add(new Vehicle("Gw typ E", 8, Clazz.SPG,
				Nation.GER, 19));

		// German Heavy
		INTERESTED_VEHICLES.add(new Vehicle("Maus", 10, Clazz.HEAVY,
				Nation.GER, 2));
		INTERESTED_VEHICLES.add(new Vehicle("VK4502 (P) Ausf. B", 9,
				Clazz.HEAVY, Nation.GER, 5));

		// German Medium
		INTERESTED_VEHICLES.add(new Vehicle("Panther II", 9, Clazz.MED,
				Nation.GER, 8));
		INTERESTED_VEHICLES.add(new Vehicle("Panther", 8, Clazz.MED,
				Nation.GER, 13));

		// German TD
		INTERESTED_VEHICLES.add(new Vehicle("Jagdtiger", 9, Clazz.TD,
				Nation.GER, 11));
		INTERESTED_VEHICLES.add(new Vehicle("Ferdinand", 8, Clazz.TD,
				Nation.GER, 16));

		// American Arty
		INTERESTED_VEHICLES
				.add(new Vehicle("M12", 6, Clazz.SPG, Nation.US, 27));
		INTERESTED_VEHICLES.add(new Vehicle("M40/M43", 7, Clazz.SPG, Nation.US,
				23));
		INTERESTED_VEHICLES
				.add(new Vehicle("T92", 8, Clazz.SPG, Nation.US, 20));

		// American Heavy
		INTERESTED_VEHICLES.add(new Vehicle("T30", 10, Clazz.HEAVY, Nation.US,
				3));
		INTERESTED_VEHICLES
				.add(new Vehicle("T34", 9, Clazz.HEAVY, Nation.US, 6));
		INTERESTED_VEHICLES.add(new Vehicle("T32", 8, Clazz.HEAVY, Nation.US,
				14));

		// American Medium
		INTERESTED_VEHICLES.add(new Vehicle("M26 Pershing", 9, Clazz.MED,
				Nation.US, 9));
		INTERESTED_VEHICLES
				.add(new Vehicle("T23", 8, Clazz.MED, Nation.US, 15));

		// Landing Zones
		landingZones
				.add(new LandingZone(
						"Bir Gandus",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/840/?type=dialog",
						Region.MED, 19, "EH_04", new ArrayList<Clan>()));
		landingZones
				.add(new LandingZone(
						"Canary Island",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/843/?type=dialog",
						Region.MED, 19, "ES_12", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Souss-Massa-Draa",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/855/?type=dialog",
						Region.MED, 19, "MA_02", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Balearic Islands",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/841/?type=dialog",
						Region.MED, 19, "ES_10", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Tlemsen",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/835/?type=dialog",
						Region.MED, 19, "DZ_01", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Tébessa",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/836/?type=dialog",
						Region.MED, 19, "DZ_04", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Sicily",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/852/?type=dialog",
						Region.MED, 19, "IT_13", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Malta",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/857/?type=dialog",
						Region.MED, 19, "MT_01", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"South Aegean",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/849/?type=dialog",
						Region.MED, 18, "GR_05", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Matrouh",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/837/?type=dialog",
						Region.MED, 18, "EG_01", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Beheira",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/838/?type=dialog",
						Region.MED, 18, "EG_03", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Duba",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/886/?type=dialog",
						Region.MED, 17, "SA_03", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Cyprus",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/833/?type=dialog",
						Region.MED, 18, "CY_01", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Maysan",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/850/?type=dialog",
						Region.MED, 17, "IQ_11", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Crimea",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/893/?type=dialog",
						Region.MED, 18, "UA_10", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Crotia",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/896/?type=dialog",
						Region.MED, 19, "YU_02", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Corsica",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/846/?type=dialog",
						Region.MED, 19, "FR_18", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Portugal",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/842/?type=dialog",
						Region.MED, 19, "ES_11", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"North Caucasus",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/864/?type=dialog",
						Region.MED, 17, "RU_05", new ArrayList<Clan>()));

		// Europa
		landingZones
				.add(new LandingZone(
						"Kanino-Timansky District",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/883/?type=dialog",
						Region.EU, 17, "RU_42", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Murmansk Region",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/863/?type=dialog",
						Region.EU, 17, "RU_04", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Troms",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/860/?type=dialog",
						Region.EU, 19, "NO_02", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Iceland",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/851/?type=dialog",
						Region.EU, 19, "IS_01", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Courland",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/854/?type=dialog",
						Region.EU, 18, "LV_02", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Kaliningrad Region",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/865/?type=dialog",
						Region.EU, 18, "RU_10", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Jutland",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/834/?type=dialog",
						Region.EU, 19, "DK_01", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Netherlands",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/859/?type=dialog",
						Region.EU, 19, "NL_01", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Republic of Ireland",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/894/?type=dialog",
						Region.EU, 19, "UK_07", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Brittany",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/845/?type=dialog",
						Region.EU, 19, "FR_15", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Croatia",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/896/?type=dialog",
						Region.EU, 19, "YU_02", new ArrayList<Clan>()));

		landingZones
				.add(new LandingZone(
						"Crimea",
						"http://uc.worldoftanks.eu/uc/clanwars/landing/893/?type=dialog",
						Region.EU, 18, "UA_10", new ArrayList<Clan>()));

		
		for (LandingZone landingZone : this.landingZones) {
			landingZoneMap.put(landingZone.getTag(), landingZone);
		}
	}
}
