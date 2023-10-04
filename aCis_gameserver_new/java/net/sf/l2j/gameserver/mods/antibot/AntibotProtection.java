package net.sf.l2j.gameserver.mods.antibot;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.data.xml.IXmlReader;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;

import org.w3c.dom.Document;

/**
 * @author L2Medusa
 */
public class AntibotProtection implements IXmlReader
{
	final Map<Integer, CaptchaZones> _zones = new ConcurrentHashMap<>();
	private final AtomicInteger _simCounter = new AtomicInteger();
	protected static Map<Integer, Integer> _monsterscounter = new ConcurrentHashMap<>();
	
	public AntibotProtection()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("./data/antibot/antibot.xml");
		LOGGER.info("Antibot Protection: loaded {} in total", _zones.size());
		
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		
		forEach(doc, "list", listNode ->
		{
			
			forEach(listNode, "abot", abot ->
			{
				final int counter = _simCounter.incrementAndGet();
				final StatSet set = parseAttributes(abot);
				CaptchaZones zone = new CaptchaZones(set.getString("zoneId"), set.getInteger("monsterKillCount"), set.getInteger("captchaTimer"), set.getInteger("punishment"), set.getInteger("punishDuration"));
				_zones.put(counter, zone);
			});
			
		});
	}
	
	public void updateMonsterCounter(Creature player, Creature monster)
	{
		if ((player instanceof Player) && (monster instanceof Monster))
		{
			int count = 1;
			Player killer = (Player) player;
			CaptchaZones zone = getCaptchaZone(killer);
			
			if (!isInsideCaptchaZone(killer))
				return;
			
			if (_monsterscounter.get(killer.getObjectId()) != null)
			{
				count = _monsterscounter.get(killer.getObjectId()) + 1;
			}
			
			if (count >= zone._monstersKillCounter)
			{
				@SuppressWarnings("unused")
				Captcha captch = new Captcha(killer, zone._captchaTimer);
				_monsterscounter.remove(killer.getObjectId());
			}
			else
			{
				_monsterscounter.put(killer.getObjectId(), count);
			}
		}
	}
	
	public CaptchaZones getCaptchaZone(Player player)
	{
		CaptchaZones tempHold = null;
		
		for (CaptchaZones checkzone : _zones.values())
		{
			if (player.isInsideZone(checkzone._zoneId))
				tempHold = checkzone;
		}
		
		return tempHold;
	}
	
	public boolean isInsideCaptchaZone(Player player)
	{
		boolean tempHol = false;
		for (CaptchaZones checkzone : _zones.values())
		{
			if (player.isInsideZone(checkzone._zoneId))
				tempHol = true;
		}
		
		return tempHol;
	}
	
	public static AntibotProtection getInstance()
	{
		return AntibotProtection.SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final AntibotProtection _instance = new AntibotProtection();
	}
	
}
