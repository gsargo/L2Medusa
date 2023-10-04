package net.sf.l2j.gameserver.model;

import java.util.Vector;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.skills.L2Skill;

public class CustomCancelTask implements Runnable
{
	private Player _player = null;
	private Vector<L2Skill> _buffs = null;
	
	public CustomCancelTask(Player _player, Vector<L2Skill> _buffs)
	{
		this._player = _player;
		this._buffs = _buffs;
	}
	
	@Override
	public void run()
	{
		if ((_player == null) || !_player.isOnline())
		{
			return;
		}
		for (L2Skill s : _buffs)
		{
			if (s == null)
			{
				continue;
			}
			
			s.getEffects(_player, _player);
		}
	}
}