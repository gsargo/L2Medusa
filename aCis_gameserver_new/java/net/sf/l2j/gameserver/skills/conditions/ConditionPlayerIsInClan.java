package net.sf.l2j.gameserver.skills.conditions;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.skills.L2Skill;

public final class ConditionPlayerIsInClan extends Condition
{
	private  boolean _val;
	
	public ConditionPlayerIsInClan(boolean val)
	{
		_val = val;
	}
	
	@Override
	public boolean testImpl(Creature effector, Creature effected, L2Skill skill, Item item)
	{
		if (!(effector instanceof Player))
			return false;
		
		final Clan clan = ((Player) effector).getClan();
		if (clan == null)
			_val = false;
		else
			_val = true;
		
		return _val;
		
	}
}