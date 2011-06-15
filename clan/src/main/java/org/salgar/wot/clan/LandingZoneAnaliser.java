package org.salgar.wot.clan;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

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
import org.salgar.wot.clan.model.Battle;
import org.salgar.wot.clan.model.Clan;
import org.salgar.wot.clan.model.LandingZone;
import org.salgar.wot.clan.model.Vehicle;

public class LandingZoneAnaliser implements Runnable {
	private static final Logger log = Logger
			.getLogger(LandingZoneAnaliser.class);
	private DefaultHttpClient httpClient = null;
	private Map<String, Clan> clanCache = null;
	private ExecutorService pool = null;
	private Collection<Vehicle> INTERESTED_VEHICLES = null;
	private LandingZone landingZone;
	private boolean finalRun = false;

	public void run() {
		profileClan(landingZone);
	}

	public LandingZoneAnaliser(Map<String, Clan> clanCache,
			Collection<Vehicle> INTERESTED_VEHICLES, LandingZone landingZone,
			ExecutorService pool, boolean finalRun) {
		this.clanCache = clanCache;
		this.pool = pool;
		this.INTERESTED_VEHICLES = INTERESTED_VEHICLES;
		this.landingZone = landingZone;
		this.finalRun = finalRun;
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

	public void profileClan(LandingZone landingZone) {
		findClansFromWebsite(landingZone);
		analyzeClans(landingZone);

		log.debug(landingZone.getClanList());
	}

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
		if (finalRun) {
			try {
				Thread.sleep(60 * 1000L);
			} catch (InterruptedException e) {
			}
			pool.shutdown();
		}
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

	private void findClansFromWebsite(LandingZone landingZone) {
		HttpGet getMethod = new HttpGet(landingZone.getUrl());

		getMethod
				.setHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.17) Gecko/20110420 Firefox/3.6.17");

		getMethod.setHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");

		try {
			HttpResponse response = httpClient.execute(getMethod);

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

			int startIndex = result
					.indexOf("table class=\"t-table t-landing-clan-names\"");
			int endIndex = result.indexOf("/table", startIndex);

			if (startIndex == -1 || endIndex == -1) {
				log.info("Most probably there is no landing to this landing zone: "
						+ landingZone.getName()
						+ " : "
						+ landingZone.getUrl()
						+ "!");
			} else {

				String clans = result.substring(startIndex, endIndex);

				log.debug("clans");

				parseClans(clans, landingZone);
			}
			findOwner(result, landingZone);
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		} finally {

		}
	}

	private void parseClans(String clans, LandingZone landingZone) {
		int clanStart = 0;

		String clanTag = "<div class=\"b-ellipsis\">";
		while ((clanStart = clans.indexOf(clanTag, clanStart)) != -1) {
			int clanEndIndex = clans.indexOf("</div>", clanStart);
			String clanName = clans.substring(clanStart + clanTag.length(),
					clanEndIndex);
			Clan clan = parseClanName(clanName);
			Clan cachedClan = null;
			if ((cachedClan = clanCache.get(clan.getName())) != null) {
				clan = cachedClan;
				Battle battle = new Battle();
				battle.setProvince(landingZone.getName());
				clan.getConcurrentBattles().add(battle);
			}
			landingZone.getClanList().add(clan);
			clanStart++;
			log.info("Clan found: " + clan.getName());
		}
		log.debug(landingZone.getClanList());
	}

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

	private void findOwner(String result, LandingZone landingZone) {
		String ownerIdentifier = "Battle with owner";
		String ownerTag = "<div style=\"display:none;\" class=\"js-landing-tooltip-content\">";
		String ownerEndTag = "<div class=\"landing-hint-msg\" style=\"color:#ffc56b;\"> Winner </div>";

		int startOwnerPacker = result.indexOf(ownerIdentifier);
		int endOwnerPacker = result.indexOf(ownerEndTag, startOwnerPacker);

		String ownerPacker = result.substring(startOwnerPacker
				+ ownerIdentifier.length(), endOwnerPacker);

		String owner = ownerPacker.substring(
				ownerPacker.lastIndexOf(ownerTag, endOwnerPacker)
						+ ownerTag.length(), ownerPacker.length());

		Clan ownerClan = parseClanName(owner);

		landingZone.getClanList().add(0, ownerClan);
	}
}
