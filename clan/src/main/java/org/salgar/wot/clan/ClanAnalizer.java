package org.salgar.wot.clan;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

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
import org.salgar.wot.clan.model.Battle;
import org.salgar.wot.clan.model.Clan;
import org.salgar.wot.clan.model.ClanMember;
import org.salgar.wot.clan.model.LandingZone;
import org.salgar.wot.clan.model.MemberTank;
import org.salgar.wot.clan.model.Vehicle;

public class ClanAnalizer implements Runnable {
	private static final Logger log = Logger.getLogger(ClanAnalizer.class);
	private DefaultHttpClient httpClient = null;
	private LandingZone landingZone = null;
	private Clan clan = null;
	private Map<String, Clan> clanCache = null;
	private Collection<Vehicle> INTERESTED_VEHICLES = null;

	public void run() {
		try {
			analyzeClan();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
	}

	public ClanAnalizer(Map<String, Clan> clanCache,
			Collection<Vehicle> INTERESTED_VEHICLES, LandingZone landingZone,
			Clan clan) {
		this.clanCache = clanCache;
		this.landingZone = landingZone;
		this.clan = clan;
		this.INTERESTED_VEHICLES = INTERESTED_VEHICLES;

		initializeHttpClient();
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

	private void analyzeClan() {
		try {
			findMembers(clan);
		} catch (Throwable t) {
			log.error(
					"We can't get the correct clan name for " + clan.getName()
							+ " " + t.getMessage(), t);
		}

		for (ClanMember clanMember : clan.getClanMembers()) {
			parseMemberDetails(clanMember);
		}
		Battle battle = new Battle();
		battle.setProvince(landingZone.getName());
		//clan.getConcurrentBattles().add(battle);
		clanCache.put(clan.getName(), clan);

		findConcurrentBattles(clan);
	}

	private void parseMemberDetails(ClanMember clanMember) {
		HttpGet getClanMembersDetails = null;

		String sleep = System.getProperty("salgar.clanprofiler.sleep", "0");

		try {
			Thread.sleep(Long.valueOf(sleep));
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}

		getClanMembersDetails = new HttpGet(
				"http://uc.worldoftanks.eu/uc/accounts/" + clanMember.getId()
						+ "-" + clanMember.getName() + "/");
		getClanMembersDetails
				.setHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.17) Gecko/20110420 Firefox/3.6.17");

		try {
			HttpResponse response = httpClient.execute(getClanMembersDetails);

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

			parseTanks(result, clanMember);

			log.debug(result);
		} catch (ClientProtocolException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void parseTanks(String result, ClanMember clanMember) {
		String valueTag = "right value\">";
		String endTag = "</td>";
		for (Vehicle tank : INTERESTED_VEHICLES) {
			int startTank = result.indexOf(">" + tank.getName() + "<");

			if (startTank >= 0) {
				MemberTank memberTank = new MemberTank();

				memberTank.setVehicle(tank);
				log.info("Tank: " + memberTank.getVehicle().getName()
						+ " for clan mamber: " + clanMember.getName()
						+ " of the clan:" + clanMember.getClan() + " found ");

				int startBattle = result.indexOf(valueTag, startTank);
				int endBattle = result.indexOf(endTag, startBattle);

				String battle = result.substring(
						startBattle + valueTag.length(), endBattle);
				memberTank.setBattles(battle);

				startBattle++;

				int startVictories = result.indexOf(valueTag, startBattle);
				int endVictories = result.indexOf(endTag, startVictories);

				String victories = result.substring(
						startVictories + valueTag.length(), endVictories);
				memberTank.setVictories(victories);

				clanMember.getMemberTanks().add(memberTank);
			}
		}
	}

	private void findMembers(Clan clan) {
		HttpGet getClanMembers = null;

		getClanMembers = new HttpGet(
				"http://uc.worldoftanks.eu/uc/clans/"
						+ clan.getId()
						+ "/members/?type=table&offset=0&limit=100&order_by=name&search=&echo=1&id=clan_members_index");

		getClanMembers
				.setHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.17) Gecko/20110420 Firefox/3.6.17");
		getClanMembers.setHeader("X-Requested-With", "XMLHttpRequest");

		try {
			HttpResponse response = httpClient.execute(getClanMembers);

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

			parseMember(result, clan);

			log.debug(result);
		} catch (ClientProtocolException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	private void parseMember(String members, Clan clan) {
		String accountTag = "\"account_id\":";
		int startAccount = 0;
		while ((startAccount = members.indexOf(accountTag, startAccount)) != -1) {
			int endAccount = members.indexOf(",", startAccount);
			String account_id = members.substring(
					startAccount + accountTag.length(), endAccount);

			ClanMember clanMember = new ClanMember();
			clanMember.setClan(clan.getName());
			clanMember.setId(account_id);

			String roleTag = "\"role\":";
			int startRole = members.indexOf(roleTag, startAccount);
			int endRole = members.indexOf("\",", startRole);
			String role = members.substring(startRole + roleTag.length() + 1,
					endRole);
			clanMember.setRole(role);

			String nameTag = "\"name\":";
			int startName = members.indexOf(nameTag, startAccount);
			int endName = members.indexOf("\"}", startAccount);
			String name = members.substring(startName + nameTag.length() + 1,
					endName);
			clanMember.setName(name);

			clan.getClanMembers().add(clanMember);
			startAccount++;
			log.info("Member:" + clanMember.getName() + " found for clan: "
					+ clan.getName());
		}
	}

	private void findConcurrentBattles(Clan clan) {
		HttpGet getConcurrentBattles = null;

		getConcurrentBattles = new HttpGet(
				"http://uc.worldoftanks.eu/uc/clans/"
						+ clan.getId()
						+ "/battles/?type=table&offset=0&limit=1000&order_by=time&search=&echo=1&id=js-battles-table");

		getConcurrentBattles
				.setHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.17) Gecko/20110420 Firefox/3.6.17");
		getConcurrentBattles.setHeader("X-Requested-With", "XMLHttpRequest");
		// http://http://uc.worldoftanks.eu/uc/clans/500000083/battles/

		try {
			HttpResponse response = httpClient.execute(getConcurrentBattles);

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

			String provinceTagStart = "\"name\":\"";
			String provinceTagEnd = "\"";

			int tagStart = 0;

			while ((tagStart = result.indexOf(provinceTagStart, tagStart)) != -1) {

				int tagEnd = result.indexOf(provinceTagEnd, tagStart
						+ provinceTagStart.length());

				String province = result.substring(
						tagStart + provinceTagStart.length(), tagEnd);

				String idTagStart = "\"id\":\"";
				String idTagEnd = "\"}";

				int idStartIndex = result.indexOf(idTagStart, tagEnd);
				int idEndIndex = result.indexOf(idTagEnd, idStartIndex
						+ idTagStart.length());

				String id = result.substring(
						idStartIndex + idTagStart.length(), idEndIndex);

				String timeTagStart = "\"time\":";
				String timeTagEnd = "}";

				int timeStartIndex = result.indexOf(timeTagStart, tagEnd);
				int timeEndIndex = result.indexOf(timeTagEnd, timeStartIndex
						+ timeTagStart.length());

				String time = result.substring(
						timeStartIndex + timeTagStart.length(), timeEndIndex);
				Battle battle = new Battle();
				battle.setProvince(province);
				if ("0".equals(time)) {
					LandingZone landingZone = Constants.createInstance().landingZoneMap.get(battle.getProvince());
					if (landingZone != null) {
						String offset = System.getProperty("salgar.clanprofiler.gmt", "0");
						int gmt_offset = Integer.valueOf(offset);

						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(landingZone.getBattleStart()).intValue() + gmt_offset);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
						
						battle.setDate(cal.getTime());
					}
				} else {				
					battle.setDate(new Date(
						(Double.valueOf(time)).longValue() * 1000));
				}
				battle.setId(id);
				clan.getConcurrentBattles().add(battle);

				tagStart++;
			}

		} catch (ClientProtocolException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
