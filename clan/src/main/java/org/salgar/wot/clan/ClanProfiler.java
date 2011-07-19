package org.salgar.wot.clan;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.salgar.wot.clan.model.Battle;
import org.salgar.wot.clan.model.Clan;
import org.salgar.wot.clan.model.ClanMember;
import org.salgar.wot.clan.model.LandingZone;
import org.salgar.wot.clan.model.LandingZone.Region;
import org.salgar.wot.clan.model.MemberTank;
import org.salgar.wot.clan.model.TankPopulation;
import org.salgar.wot.clan.model.Vehicle;
import org.salgar.wot.clan.model.Vehicle.Clazz;
import org.salgar.wot.clan.model.Vehicle.Nation;
import org.salgar.wot.clan.util.VehicleOrderComparator;

public class ClanProfiler {
	private static Logger log = Logger
			.getLogger(org.salgar.wot.clan.ClanProfiler.class);
	private DefaultHttpClient httpClient = null;
	private ArrayList<LandingZone> landingZones = Constants.createInstance().landingZones;
	private Map<String, Clan> clanCache = Collections
			.synchronizedMap(new HashMap<String, Clan>());
	private ArrayList<Vehicle> INTERESTED_VEHICLES = Constants.createInstance().INTERESTED_VEHICLES;
	private ExecutorService pool = null;
	private int threadPoolCount = 1;

	/**
	 * salgar.clanprofiler.landing.region salgar.clanprofiler.landing.clan
	 * salgar.clanprofiler.landing.zones
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ClanProfiler clanProfiler = new ClanProfiler();
		clanProfiler.initializeHttpClient();

		List<LandingZone> actualLandingZones = null;

		String clan = System.getProperty("salgar.clanprofiler.landing.clan");
		// String clan = "Beerpanzer_LV_άλφα";
		String zones = System.getProperty("salgar.clanprofiler.landing.zones");

		if (clan != null && !"".equals(clan)) {
			Clan selectedClan = clanProfiler.parseClanName("[EXP] " + clan);
			clanProfiler.landingZones.get(0).getClanList().add(selectedClan);
			clanProfiler.analyzeClans(clanProfiler.landingZones.get(0));
			actualLandingZones = new ArrayList<LandingZone>();
			actualLandingZones.add(clanProfiler.landingZones.get(0));
		} else {
			if (zones != null && !"".equals(zones)) {
				actualLandingZones = clanProfiler.findLandingZones(zones);
			} else {
				actualLandingZones = clanProfiler.findLandingZones();
			}
			for (int i = 0, n = actualLandingZones.size(); i < n; i++) {
				LandingZone landingZone = actualLandingZones.get(i);
				log.info("We are working Landing Zone: "
						+ landingZone.getName() + " : " + landingZone.getUrl());
				clanProfiler
						.profileClan(landingZone, i == n - 1 ? true : false);
			}
		}

		clanProfiler.poolShutdown();
		clanProfiler.writeExcel(actualLandingZones);
	}

	public void poolShutdown() {
		try {
			while (pool.awaitTermination(5000L, TimeUnit.MILLISECONDS) == false) {

			}
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}

	public List<LandingZone> findLandingZones(String zones) {
		String offset = System.getProperty("salgar.clanprofiler.gmt", "0");
		int gmt_offset = Integer.valueOf(offset);
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		
		StringTokenizer tokenizer = new StringTokenizer(zones, ",");

		List<LandingZone> choosenZones = new ArrayList<LandingZone>();
		while (tokenizer.hasMoreTokens()) {
			String landingZone = tokenizer.nextToken();

			for (LandingZone zone : landingZones) {
				if (landingZone.equals(zone.getName()) && zone.getBattleStart() + gmt_offset > hour) {
					choosenZones.add(zone);
					break;
				}
			}
		}
		return choosenZones;
	}

	public List<LandingZone> findLandingZones() {
		String region = System.getProperty(
				"salgar.clanprofiler.landing.region", "ALL");

		String offset = System.getProperty("salgar.clanprofiler.gmt", "0");
		int gmt_offset = Integer.valueOf(offset);
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		if ("ALL".equals(region)) {
			return landingZones;
		} else if (Region.MED.getName().equals(region) || Region.MED_EU.equals(region)) {
			List<LandingZone> meditereanZones = new ArrayList<LandingZone>();
			for (LandingZone landingZone : landingZones) {
				if (Region.MED.equals(landingZone.getRegion())
						&& landingZone.getBattleStart() + gmt_offset > hour) {
					meditereanZones.add(landingZone);
				}
			}
			return meditereanZones;
		} else if (Region.EU.getName().equals(region) || Region.MED_EU.equals(region)) {
			List<LandingZone> europeanZones = new ArrayList<LandingZone>();
			for (LandingZone landingZone : landingZones) {
				if (Region.EU.equals(landingZone.getRegion())
						&& landingZone.getBattleStart() + gmt_offset > hour) {
					europeanZones.add(landingZone);
				}
			}
			return europeanZones;
		} else {
			return landingZones;
		}
	}

	public ClanProfiler() {
		String threads = System.getProperty(
				"salgar.clanprofiler.landing.threads", "10");

		try {
			threadPoolCount = Integer.valueOf(threads);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}

		pool = Executors.newFixedThreadPool(threadPoolCount);

	}

	// public void profileClan(LandingZone landingZone) {
	// findClansFromWebsite(landingZone);
	// analyzeClans(landingZone);
	//
	// log.debug(landingZone.getClanList());
	// }

	public void profileClan(LandingZone landingZone, boolean finalRun) {
		FutureTask<?> taskLandingZone = new FutureTask<Object>(
				new LandingZoneAnaliser(clanCache, INTERESTED_VEHICLES,
						landingZone, pool, finalRun), null);

		pool.submit(taskLandingZone);
	}

	private void initializeHttpClient() {
		httpClient = new DefaultHttpClient();
		Boolean isProxy = true;

		try {
			isProxy = Boolean.valueOf(System.getProperty(
					"salgar.clanprofiler.proxy", "true"));
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}

		if (isProxy) {
			HttpHost proxy = new HttpHost("proxy-bn.bn.detemobil.de", 3128);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
			UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
					"salgar", "nanimemo0809");
			httpClient.getCredentialsProvider().setCredentials(
					new AuthScope("proxy-bn.bn.detemobil.de", 3128),
					credentials);
		}
	}

	// private void findClansFromWebsite(LandingZone landingZone) {
	// HttpGet getMethod = new HttpGet(landingZone.getUrl());
	//
	// getMethod
	// .setHeader(
	// "User-Agent",
	// "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.17) Gecko/20110420 Firefox/3.6.17");
	//
	// getMethod.setHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
	//
	// try {
	// HttpResponse response = httpClient.execute(getMethod);
	//
	// HttpEntity entity = response.getEntity();
	//
	// BufferedInputStream bis = new BufferedInputStream(
	// entity.getContent());
	// int length = 0;
	// byte[] buff = new byte[1024];
	// StringBuffer sb = new StringBuffer(1024);
	// while ((length = bis.read(buff)) != -1) {
	// sb.append(new String(buff, 0, length, "UTF-8"));
	// }
	//
	// String result = sb.toString();
	// log.debug(result);
	//
	// int startIndex = result
	// .indexOf("table class=\"t-table t-landing-clan-names\"");
	// int endIndex = result.indexOf("/table", startIndex);
	//
	// if (startIndex == -1 || endIndex == -1) {
	// log.info("Most probably there is no landing to this landing zone: "
	// + landingZone.getName()
	// + " : "
	// + landingZone.getUrl()
	// + "!");
	// } else {
	//
	// String clans = result.substring(startIndex, endIndex);
	//
	// log.debug("clans");
	//
	// parseClans(clans, landingZone);
	// }
	// findOwner(result, landingZone);
	// } catch (Throwable t) {
	// log.error(t.getMessage(), t);
	// } finally {
	//
	// }
	// }

	// private void findOwner(String result, LandingZone landingZone) {
	// String ownerIdentifier = "Battle with owner";
	// String ownerTag =
	// "<div style=\"display:none;\" class=\"js-landing-tooltip-content\">";
	// String ownerEndTag =
	// "<div class=\"landing-hint-msg\" style=\"color:#ffc56b;\"> Winner </div>";
	//
	// int startOwnerPacker = result.indexOf(ownerIdentifier);
	// int endOwnerPacker = result.indexOf(ownerEndTag, startOwnerPacker);
	//
	// String ownerPacker = result.substring(startOwnerPacker
	// + ownerIdentifier.length(), endOwnerPacker);
	//
	// String owner = ownerPacker.substring(
	// ownerPacker.lastIndexOf(ownerTag, endOwnerPacker)
	// + ownerTag.length(), ownerPacker.length());
	//
	// Clan ownerClan = parseClanName(owner);
	//
	// landingZone.getClanList().add(0, ownerClan);
	// }

	// private void analyzeClans(LandingZone landingZone) {
	// for(int i = 0, n = landingZone.getClanList().size(); i < n; i++) {
	// Clan clan = landingZone.getClanList().get(i);
	// if (clanCache.get(clan.getName()) != null) {
	// landingZone.getClanList().set(i, clanCache.get(clan.getName()));
	// continue;
	// }
	// if (clan.getName() != null && !"".equals(clan.getName())) {
	// findClanId(clan);
	// } else {
	// log.warn("We have an empty clan name!");
	// }
	// }
	// for(int i = 0, n = landingZone.getClanList().size(); i < n; i++) {
	// Clan clan = landingZone.getClanList().get(i);
	// if (clan.getName() != null && !"".equals(clan.getName())) {
	// if (clanCache.get(clan.getName()) != null) {
	// //landingZone.getClanList().set(i, clanCache.get(clan.getName()));
	// continue;
	// }
	// try {
	// findMembers(clan);
	// } catch (Throwable t) {
	// log.error(
	// "We can't get the correct clan name for "
	// + clan.getName() + " " + t.getMessage(), t);
	// }
	//
	// for (ClanMember clanMember : clan.getClanMembers()) {
	// parseMemberDetails(clanMember);
	// }
	// Battle battle = new Battle();
	// battle.setProvince(landingZone.getName());
	// clan.getConcurrentBattles().add(battle);
	// clanCache.put(clan.getName(), clan);
	// } else {
	// log.warn("We have an empty clan name!");
	// }
	// findConcurrentBattles(clan);
	// }
	// }

	private void analyzeClans(LandingZone landingZone) {
		for (int i = 0, n = landingZone.getClanList().size(); i < n; i++) {
			Clan clan = landingZone.getClanList().get(i);
			if (clanCache.get(clan.getName()) != null) {
				landingZone.getClanList().set(i, clanCache.get(clan.getName()));
				continue;
			}
			if (clan.getName() != null && !"".equals(clan.getName())) {
				findClanId(clan);
			} else {
				log.warn("We have an empty clan name!");
			}
		}
		pool = Executors.newFixedThreadPool(threadPoolCount);
		for (int i = 0, n = landingZone.getClanList().size(); i < n; i++) {
			Clan clan = landingZone.getClanList().get(i);
			if (clan.getName() != null && !"".equals(clan.getName())) {
				if (clanCache.get(clan.getName()) != null) {
					// landingZone.getClanList().set(i,
					// clanCache.get(clan.getName()));
					continue;
				}
				try {
					FutureTask<?> task = new FutureTask<Object>(
							new ClanAnalizer(this.clanCache,
									this.INTERESTED_VEHICLES, landingZone, clan),
							null);
					pool.submit(task);
				} catch (Throwable t) {
					log.error(
							"We can't get the correct clan name for "
									+ clan.getName() + " " + t.getMessage(), t);
				}
			}
		}
		pool.shutdown();
		try {
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}

	}

	// private void findConcurrentBattles(Clan clan) {
	// HttpGet getConcurrentBattles = null;
	//
	// getConcurrentBattles = new HttpGet(
	// "http://uc.worldoftanks.eu/uc/clans/"
	// + clan.getId()
	// +
	// "/battles/?type=table&offset=0&limit=1000&order_by=time&search=&echo=1&id=js-battles-table");
	//
	// getConcurrentBattles
	// .setHeader(
	// "User-Agent",
	// "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.17) Gecko/20110420 Firefox/3.6.17");
	// getConcurrentBattles.setHeader("X-Requested-With", "XMLHttpRequest");
	// // http://http://uc.worldoftanks.eu/uc/clans/500000083/battles/
	//
	// try {
	// HttpResponse response = httpClient.execute(getConcurrentBattles);
	//
	// HttpEntity entity = response.getEntity();
	//
	// BufferedInputStream bis = new BufferedInputStream(
	// entity.getContent());
	// int length = 0;
	// byte[] buff = new byte[1024];
	// StringBuffer sb = new StringBuffer(1024);
	// while ((length = bis.read(buff)) != -1) {
	// sb.append(new String(buff, 0, length, "UTF-8"));
	// }
	//
	// String result = sb.toString();
	// log.debug(result);
	//
	// String provinceTagStart = "\"name\":\"";
	// String provinceTagEnd = "\"";
	//
	// int tagStart = 0;
	//
	// while ((tagStart = result.indexOf(provinceTagStart, tagStart)) != -1) {
	//
	// int tagEnd = result.indexOf(provinceTagEnd, tagStart
	// + provinceTagStart.length());
	//
	// String province = result.substring(
	// tagStart + provinceTagStart.length(), tagEnd);
	//
	// String idTagStart = "\"id\":\"";
	// String idTagEnd = "\"}";
	//
	// int idStartIndex = result.indexOf(idTagStart, tagEnd);
	// int idEndIndex = result.indexOf(idTagEnd, idStartIndex
	// + idTagStart.length());
	//
	// String id = result.substring(
	// idStartIndex + idTagStart.length(), idEndIndex);
	//
	// String timeTagStart = "\"time\":";
	// String timeTagEnd = "}";
	//
	// int timeStartIndex = result.indexOf(timeTagStart, tagEnd);
	// int timeEndIndex = result.indexOf(timeTagEnd, timeStartIndex
	// + timeTagStart.length());
	//
	// String time = result.substring(
	// timeStartIndex + timeTagStart.length(), timeEndIndex);
	//
	// Battle battle = new Battle();
	// battle.setProvince(province);
	// battle.setDate(new Date(
	// (Double.valueOf(time)).longValue() * 1000));
	// battle.setId(id);
	// clan.getConcurrentBattles().add(battle);
	//
	// tagStart++;
	// }
	//
	// } catch (ClientProtocolException e) {
	// log.error(e.getMessage(), e);
	// } catch (IOException e) {
	// log.error(e.getMessage(), e);
	// }
	// }

	// private void parseClans(String clans, LandingZone landingZone) {
	// int clanStart = 0;
	//
	// String clanTag = "<div class=\"b-ellipsis\">";
	// while ((clanStart = clans.indexOf(clanTag, clanStart)) != -1) {
	// int clanEndIndex = clans.indexOf("</div>", clanStart);
	// String clanName = clans.substring(clanStart + clanTag.length(),
	// clanEndIndex);
	// Clan clan = parseClanName(clanName);
	// Clan cachedClan = null;
	// if ((cachedClan = clanCache.get(clan.getName())) != null) {
	// clan = cachedClan;
	// Battle battle = new Battle();
	// battle.setProvince(landingZone.getName());
	// clan.getConcurrentBattles().add(battle);
	// }
	// landingZone.getClanList().add(clan);
	// clanStart++;
	// log.info("Clan found: " + clan.getName());
	// }
	// log.debug(landingZone.getClanList());
	// }

	private Clan parseClanName(String clanName) {
		Clan clan = new Clan();
		int startNick = clanName.indexOf("[");
		int endNick = clanName.indexOf("]");

		String nick = clanName.substring(startNick, endNick + 1);
		clan.setNick(nick);

		int startName = endNick + 2;
		String name = clanName.substring(startName, clanName.length());
		clan.setName(name.trim());

		return clan;
	}

	private void findClanId(Clan clan) {
		HttpGet getClanId = null;
		try {
			getClanId = new HttpGet(
					"http://uc.worldoftanks.eu/uc/clans/?type=table&offset=0&limit=10&order_by=name&search="
							+ URLEncoder.encode(StringEscapeUtils
									.unescapeHtml(clan.getName()), "UTF-8")
							+ "&echo=2&id=clans_index");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
		}
		getClanId
				.setHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.17) Gecko/20110420 Firefox/3.6.17");
		getClanId.setHeader("X-Requested-With", "XMLHttpRequest");

		try {
			HttpResponse response = httpClient.execute(getClanId);

			HttpEntity entity = response.getEntity();

			BufferedInputStream bis = new BufferedInputStream(
					entity.getContent());
			int length = 0;
			byte[] buff = new byte[1024];
			StringBuffer sb = new StringBuffer(1024);
			while ((length = bis.read(buff)) != -1) {
				sb.append(new String(buff, 0, length, "UTF-8"));
			}

			String result = sb.toString();
			log.debug(result);

			String idTag = "\"id\":";
			int startId = result.indexOf(idTag);
			int endId = result.indexOf(",", startId);

			String id = result.substring(startId + idTag.length(), endId);

			clan.setId(id);
			log.info("Clan id: " + clan.getId() + " for clan: "
					+ clan.getName() + " is found!");
		} catch (ClientProtocolException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	// private void findMembers(Clan clan) {
	// HttpGet getClanMembers = null;
	//
	// getClanMembers = new HttpGet(
	// "http://uc.worldoftanks.eu/uc/clans/"
	// + clan.getId()
	// +
	// "/members/?type=table&offset=0&limit=100&order_by=name&search=&echo=1&id=clan_members_index");
	//
	// getClanMembers
	// .setHeader(
	// "User-Agent",
	// "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.17) Gecko/20110420 Firefox/3.6.17");
	// getClanMembers.setHeader("X-Requested-With", "XMLHttpRequest");
	//
	// try {
	// HttpResponse response = httpClient.execute(getClanMembers);
	//
	// HttpEntity entity = response.getEntity();
	//
	// BufferedInputStream bis = new BufferedInputStream(
	// entity.getContent());
	// int length = 0;
	// byte[] buff = new byte[1024];
	// StringBuffer sb = new StringBuffer(1024);
	// while ((length = bis.read(buff)) != -1) {
	// sb.append(new String(buff, 0, length, "UTF-8"));
	// }
	//
	// String result = sb.toString();
	//
	// parseMember(result, clan);
	//
	// log.debug(result);
	// } catch (ClientProtocolException e) {
	// log.error(e.getMessage(), e);
	// } catch (IOException e) {
	// log.error(e.getMessage(), e);
	// }
	// }

	// private void parseMember(String members, Clan clan) {
	// String accountTag = "\"account_id\":";
	// int startAccount = 0;
	// while ((startAccount = members.indexOf(accountTag, startAccount)) != -1)
	// {
	// int endAccount = members.indexOf(",", startAccount);
	// String account_id = members.substring(
	// startAccount + accountTag.length(), endAccount);
	//
	// ClanMember clanMember = new ClanMember();
	// clanMember.setId(account_id);
	//
	// String roleTag = "\"role\":";
	// int startRole = members.indexOf(roleTag, startAccount);
	// int endRole = members.indexOf("\",", startRole);
	// String role = members.substring(startRole + roleTag.length() + 1,
	// endRole);
	// clanMember.setRole(role);
	//
	// String nameTag = "\"name\":";
	// int startName = members.indexOf(nameTag, startAccount);
	// int endName = members.indexOf("\"}", startAccount);
	// String name = members.substring(startName + nameTag.length() + 1,
	// endName);
	// clanMember.setName(name);
	//
	// clan.getClanMembers().add(clanMember);
	// startAccount++;
	// log.info("Member:" + clanMember.getName() + " found for clan: "
	// + clan.getName());
	// }
	// }

	// private void parseMemberDetails(ClanMember clanMember) {
	// HttpGet getClanMembersDetails = null;
	//
	// String sleep = System.getProperty("salgar.clanprofiler.sleep", "0");
	//
	// try {
	// Thread.sleep(Long.valueOf(sleep));
	// } catch (InterruptedException e) {
	// log.error(e.getMessage(), e);
	// }
	//
	// getClanMembersDetails = new HttpGet(
	// "http://uc.worldoftanks.eu/uc/accounts/" + clanMember.getId()
	// + "-" + clanMember.getName() + "/");
	// getClanMembersDetails
	// .setHeader(
	// "User-Agent",
	// "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.17) Gecko/20110420 Firefox/3.6.17");
	//
	// try {
	// HttpResponse response = httpClient.execute(getClanMembersDetails);
	//
	// HttpEntity entity = response.getEntity();
	//
	// BufferedInputStream bis = new BufferedInputStream(
	// entity.getContent());
	// int length = 0;
	// byte[] buff = new byte[1024];
	// StringBuffer sb = new StringBuffer(1024);
	// while ((length = bis.read(buff)) != -1) {
	// sb.append(new String(buff, 0, length, "UTF-8"));
	// }
	//
	// String result = sb.toString();
	//
	// parseTanks(result, clanMember);
	//
	// log.debug(result);
	// } catch (ClientProtocolException e) {
	// log.error(e.getMessage(), e);
	// } catch (IOException e) {
	// log.error(e.getMessage(), e);
	// }
	// }

	// private void parseTanks(String result, ClanMember clanMember) {
	// String valueTag = "right value\">";
	// String endTag = "</td>";
	// for (Vehicle tank : INTERESTED_VEHICLES) {
	// int startTank = result.indexOf(">" + tank.getName() + "<");
	//
	// if (startTank >= 0) {
	// MemberTank memberTank = new MemberTank();
	//
	// memberTank.setVehicle(tank);
	// log.info("Tank: " + memberTank.getVehicle().getName()
	// + " for clan mamber: " + clanMember.getName()
	// + " found ");
	//
	// int startBattle = result.indexOf(valueTag, startTank);
	// int endBattle = result.indexOf(endTag, startBattle);
	//
	// String battle = result.substring(
	// startBattle + valueTag.length(), endBattle);
	// memberTank.setBattles(battle);
	//
	// startBattle++;
	//
	// int startVictories = result.indexOf(valueTag, startBattle);
	// int endVictories = result.indexOf(endTag, startVictories);
	//
	// String victories = result.substring(
	// startVictories + valueTag.length(), endVictories);
	// memberTank.setVictories(victories);
	//
	// clanMember.getMemberTanks().add(memberTank);
	// }
	// }
	// }

	private void writeExcel(List<LandingZone> landingZones) {
		String offset = System.getProperty("salgar.clanprofiler.gmt", "0");
		int gmt_offset = Integer.valueOf(offset);
		Workbook wb = new XSSFWorkbook();
		SimpleDateFormat sdf = new SimpleDateFormat("MM_dd_HH_mm_ss");

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream("clan_battle_landing_analyzes_"
					+ sdf.format(new Date()) + ".xlsx");
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		}

		List<Clan> clansWithConcurrentBattles = new ArrayList<Clan>();
		for (LandingZone landingZone : landingZones) {
			Sheet sh = wb.createSheet(landingZone.getName()+ " (" + (landingZone.getBattleStart() + gmt_offset) + ")");
			writeExcel(landingZone, sh, wb);

			for (Clan clan : landingZone.getClanList()) {
				if (clan.getConcurrentBattles().isEmpty() == false) {
					clansWithConcurrentBattles.add(clan);
				}
			}
		}

		if (clansWithConcurrentBattles.isEmpty() == false) {
			Sheet sh = wb.createSheet("concurrent battles");

			int i = 0;
			for (Clan clan : clansWithConcurrentBattles) {
				Row row = sh.createRow(i);
				i++;

				Cell clanNameCell = row.createCell(0);
				clanNameCell.setCellValue(clan.getName());

				Cell concurrentBattlesCell = row.createCell(1);
				concurrentBattlesCell
						.setCellValue(calculateLandingZoneNames(clan
								.getConcurrentBattles()));
			}
		}

		try {
			wb.write(fos);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		try {
			fos.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	public String calculateLandingZoneNames(List<Battle> conccurentBattles) {
		StringBuffer sb = new StringBuffer();

		for (Battle concurrentBattle : conccurentBattles) {
			sb.append(concurrentBattle.getProvince());
			sb.append(" (");
			sb.append(concurrentBattle.getDate());
			sb.append(" , ");
			sb.append(concurrentBattle.getId());
			sb.append("),");
		}

		return sb.toString();
	}

	public void writeExcel(LandingZone landingZone, Sheet sh, Workbook wb) {
		int i = 0;
		for (Clan clan : landingZone.getClanList()) {
			Map<Vehicle, TankPopulation> clanTankPopulation = new HashMap<Vehicle, TankPopulation>();
			Row clanRow = sh.createRow(i);
			Cell clanCell = clanRow.createCell(0);
			clanCell.setCellValue(clan.getName() + " ( "
					+ calculateLandingZoneNames(clan.getConcurrentBattles())
					+ " )");
			i++;

			for (ClanMember clanMember : clan.getClanMembers()) {
				boolean topUsFound = false;
				boolean topGerFound = false;
				boolean topUssrFound = false;
				if (clanMember.getMemberTanks().isEmpty() == false) {
					Row memberRow = sh.createRow(i);
					Cell memberCell = memberRow.createCell(1);
					memberCell.setCellValue(clanMember.getName() + " ("
							+ clanMember.getRole() + ")");

					boolean firstTime = true;
					Collections.sort(clanMember.getMemberTanks());
					for (MemberTank memberTank : clanMember.getMemberTanks()) {
						if (memberTank.getVehicle().getTier() >= 8
								|| (Clazz.SPG.equals(memberTank.getVehicle()
										.getClazz()))
								&& memberTank.getVehicle().getTier() >= 6) {
							TankPopulation tankPopulation = null;
							if ((Nation.US.equals(memberTank.getVehicle()
									.getNation()) && topUsFound == false)
									|| (Nation.GER.equals(memberTank
											.getVehicle().getNation()) && topGerFound == false)
									|| (Nation.USSR.equals(memberTank
											.getVehicle().getNation()) && topUssrFound == false)) {
								tankPopulation = clanTankPopulation
										.get(memberTank.getVehicle());
								if (tankPopulation == null) {
									tankPopulation = new TankPopulation();
								}
								tankPopulation.setTopTank(tankPopulation
										.getTopTank() + 1);
								if (Nation.US.equals(memberTank.getVehicle()
										.getNation())) {
									topUsFound = true;
								} else if (Nation.GER.equals(memberTank
										.getVehicle().getNation())) {
									topGerFound = true;
								} else if (Nation.USSR.equals(memberTank
										.getVehicle().getNation())) {
									topUssrFound = true;
								}
								clanTankPopulation.put(memberTank.getVehicle(),
										tankPopulation);
							} else {
								tankPopulation = clanTankPopulation
										.get(memberTank.getVehicle());

								if (tankPopulation == null) {
									tankPopulation = new TankPopulation();
								}

								tankPopulation
										.setAlternativeTank(tankPopulation
												.getAlternativeTank() + 1);
								clanTankPopulation.put(memberTank.getVehicle(),
										tankPopulation);
							}
						}
						if (firstTime == true) {
							CellStyle style = wb.createCellStyle();
							style.setFillBackgroundColor(IndexedColors.RED.getIndex());
							Cell tanksCell = memberRow.createCell(2);
							tanksCell.setCellValue(memberTank.getVehicle()
									.getName());
							tanksCell.setCellStyle(style);
							Cell battlesCell = memberRow.createCell(3);
							battlesCell.setCellValue(memberTank.getBattles());
							Cell victoriesCell = memberRow.createCell(4);
							victoriesCell.setCellValue(memberTank
									.getVictories());
							Cell porcentCell = memberRow.createCell(5,
									Cell.CELL_TYPE_NUMERIC);
							Double vict = Double.valueOf(memberTank
									.getVictories().replaceAll("&nbsp;", ""));
							Double batt = Double.valueOf(memberTank
									.getBattles().replaceAll("&nbsp;", ""));
							double porcent = (vict.doubleValue() / batt
									.doubleValue()) * 100;
							porcentCell.setCellValue(porcent);
							firstTime = false;
						} else {
							Row extraTankRow = sh.createRow(i);
							Cell extraTankCell = extraTankRow.createCell(2);
							extraTankCell.setCellValue(memberTank.getVehicle()
									.getName());
							Cell extrabattlesCell = extraTankRow.createCell(3);
							extrabattlesCell.setCellValue(memberTank
									.getBattles());
							Cell extravictoriesCell = extraTankRow
									.createCell(4);
							extravictoriesCell.setCellValue(memberTank
									.getVictories());
							Cell extraporcentCell = extraTankRow.createCell(5,
									Cell.CELL_TYPE_NUMERIC);
							Double vict = Double.valueOf(memberTank
									.getVictories().replaceAll("&nbsp;", ""));
							Double batt = Double.valueOf(memberTank
									.getBattles().replaceAll("&nbsp;", ""));
							double percent = (vict.doubleValue() / batt
									.doubleValue()) * 100;
							extraporcentCell.setCellValue(percent);
						}
						i++;
					}
					firstTime = true;

				}
			}
			if (clanTankPopulation.isEmpty() == false) {
				ArrayList<Vehicle> orderedList = new ArrayList<Vehicle>(
						INTERESTED_VEHICLES);
				Collections.sort(orderedList, new VehicleOrderComparator());
				i++;
				Row tankPopulationRow = sh.createRow(i);
				int y = 1;

				for (Vehicle vehicle : orderedList) {
					Cell tankPopulation = tankPopulationRow.createCell(y);
					if (clanTankPopulation.get(vehicle) != null) {
						tankPopulation.setCellValue(vehicle.getName()
								+ ": "
								+ clanTankPopulation.get(vehicle).getTopTank()
								+ (clanTankPopulation.get(vehicle)
										.getAlternativeTank() != 0 ? "( "
										+ clanTankPopulation.get(vehicle)
												.getAlternativeTank() + ")"
										: ""));
						y++;
					}
				}
				i++;
				i++;
			}
		}
	}
}
