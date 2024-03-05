package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link ZoneType}, notably used for peace behavior (pvp related).
 */
public class PeaceZone extends ZoneType
{
	public PeaceZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		character.setInsideZone(ZoneId.PEACE, true);
		character.setIsInsideTownZone(true);
		
		if(character instanceof Player)
		{
			ThreadPool.schedule(() ->((Player) character).setPvpFlag(0),6*1000);
			((Player)character).setInNoLogoutArea(false); 
			((Player)character).setPartyPenalty(false);
			
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		character.setInsideZone(ZoneId.PEACE, false);
		character.setIsInsideTownZone(false);
	}
}