package net.sf.l2j.gameserver.skills.effects;

import net.sf.l2j.gameserver.enums.skills.EffectType;
import net.sf.l2j.gameserver.enums.skills.SkillType;
import net.sf.l2j.gameserver.model.actor.Creature;
//import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.skills.*;



public final class EffectDeflectBuff extends AbstractEffect
{
	
	public EffectDeflectBuff(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}


@Override
public EffectType getEffectType()
{
	return EffectType.BUFF_PREVENTION;
}

@Override
public boolean onActionTime()
{
	// Only cont skills shouldn't end
	if(getSkill().getSkillType() != SkillType.CONT)
		return false;

	double manaDam = 2;

	if(manaDam > ( getEffected()).getStatus().getMp())
	{	
		getEffected().sendMessage("Anti-Buff Lock has been deactivated due to lack of MP.");
		return false;
	}

	getEffected().getStatus().reduceMp(manaDam) ;
	return true;
}


	@Override
	public boolean onStart()
	{
		getEffected().setIsBuffProtected(true);
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().setIsBuffProtected(false);
	}
}