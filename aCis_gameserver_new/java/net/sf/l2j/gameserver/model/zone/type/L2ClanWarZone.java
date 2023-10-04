package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.type.subtype.SpawnZoneType;
import net.sf.l2j.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link SpawnZoneType}, used by towns. A town zone is generally associated to a castle for taxes.
 */
public class L2ClanWarZone extends ZoneType
{
	
	public L2ClanWarZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		Player activeChar = character.getActingPlayer();
		if (character instanceof Player)
		{	
			if (activeChar.getClan() != null && activeChar.getStatus().getLevel() >= 75)
			{
				activeChar.setIsInsideClanwarZone(true);
				activeChar.sendMessage("You have entered a Clan War Zone. Prepare for fight.");
			}
			else
			{
				activeChar.sendMessage("This is a strict area for clan members. You will be teleported at the nearest town.");
				int x = 83290;
				int y = 148027;
				int z = -3400;
				activeChar.teleportTo(x, y, z, 50); // Teleport to nearest town if clan = null
				return;
			}
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		Player activeChar = character.getActingPlayer();
		if (character instanceof Player)
		{
			activeChar.setIsInsideClanwarZone(false);
		}
	}
	
	// @Override
	public void onDieInside(Creature character)
	{
		//
	}
	
	// @Override
	public void onReviveInside(Creature character)
	{
		onEnter(character);
	}
	
}