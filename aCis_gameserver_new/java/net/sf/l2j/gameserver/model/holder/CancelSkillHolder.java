package net.sf.l2j.gameserver.model.holder;

import java.util.concurrent.TimeUnit;

import net.sf.l2j.gameserver.skills.L2Skill;

/**
 * @author Giorgos
 */
public class CancelSkillHolder extends IntIntHolder
{
	private final static int TIME_IN_SEC = 6; // can change here the value in seconds
	private long _time;
	
	public CancelSkillHolder(L2Skill skill)
	{
		super(skill.getId(), skill.getLevel());
		_time = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(TIME_IN_SEC);
	}
	
	public long getTime()
	{
		return _time;
	}
	
}
