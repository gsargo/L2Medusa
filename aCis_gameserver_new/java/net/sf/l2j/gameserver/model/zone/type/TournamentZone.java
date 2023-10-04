package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.SpawnZone;
import net.sf.l2j.gameserver.network.SystemMessageId;

/**
 * @author Rouxy
 */
public class TournamentZone extends SpawnZone
{
	
	@Override
	public void setParameter(String name, String value)
	{
		
		super.setParameter(name, value);
	}
	
	public TournamentZone(int id)
	{
		super(id);
		
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.TOURNAMENT, true);
		if (character instanceof Player)
			((Player) character).sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);
		
		character.setInsideZone(ZoneId.PVP, true);
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.TOURNAMENT, false);
		character.setInsideZone(ZoneId.PVP, false);
		character.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		
		if (character instanceof Player)
			((Player) character).sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
		
	}
	
}
