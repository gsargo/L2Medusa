package net.sf.l2j.gameserver.model.actor.instance;

import java.util.List;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.ScriptEventType;
import net.sf.l2j.gameserver.enums.skills.Stats;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.model.actor.Attackable;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.ai.type.AttackableAI;
import net.sf.l2j.gameserver.model.actor.ai.type.CreatureAI;
import net.sf.l2j.gameserver.model.actor.ai.type.SiegeGuardAI;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.scripting.Quest;

/**
 * This class extends {@link Attackable} and manages all {@link Guard}s.<br>
 * <br>
 * A {@link Guard} is used to protect Players from Player Killers (PKs).
 */
public final class Guard extends Attackable
{
	public Guard(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onSpawn()
	{
		//setNoRndWalk(true);
		super.onSpawn();
	}
	
	@Override
	public CreatureAI getAI()
	{
		CreatureAI ai = _ai;
		if (ai == null)
		{
			synchronized (this)
			{
				ai = _ai;
				if (ai == null)
					_ai = ai = new AttackableAI(this);
			}
		}
		return ai;
	}
	
	/*public static boolean isAutoAttackable(Creature attacker)
	{
		return (attacker instanceof Monster) && ((Monster)attacker).isAggressive() || (attacker instanceof Player) || attacker.getActingPlayer().getKarma() > 0;	
	} */
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/guard/" + filename + ".htm";
	}
	
	@Override
	public void onInteract(Player player)
	{
		switch (getNpcId())
		{
			case 30733: // Guards in start villages
			case 31032:
			case 31033:
			case 31034:
			case 31035:
			case 31036:
			case 31671: // Patrols
			case 31672:
			case 31673:
			case 31674:
				return;
		}
		
		if (hasRandomAnimation())
			onRandomAnimation(Rnd.get(8));
		
		player.getQuestList().setLastQuestNpcObjectId(getObjectId());
		
		List<Quest> scripts = getTemplate().getEventQuests(ScriptEventType.ON_FIRST_TALK);
		if (scripts.size() == 1)
			scripts.get(0).notifyFirstTalk(this, player);
		else
			showChatWindow(player);
	}
	
	@Override
	public boolean isGuard()
	{
		return true;
	}
	
	@Override
	public int getDriftRange()
	{
		return 20;
	} 
	

	@Override
	public boolean returnHome()
	{
		if (isDead())
			return false;
		
		if (getSpawn() != null && !isInCombat() && !isIn2DRadius(getSpawn().getLoc(), 400))
		{
			getAggroList().cleanAllHate();
			
			setIsReturningToSpawnPoint(true);
			forceRunStance();
			getAI().tryToMoveTo(getSpawn().getLoc(), null);
			return true;
		}
		
		return false;
	}
	
/*	public static int getPhysicalAttackRange()
	{
		return 10000;
	}
	*/
	
	/*public static int getAggroRange()
	{
		return 15000;
	} */
	
	@Override
	public boolean canAutoAttack(Creature target)
	{
		final Player player = target.getActingPlayer();
		
		if (player == null || player.getKarma()<=0 || player.isAlikeDead())
			return false;	
		
		// Check if the target isn't GM on hide mode.
	//	if (player.isGM() && !player.getAppearance().isVisible())
			//return false;
		
		// Check if the target isn't in silent move mode AND too far
		if (player.isSilentMoving() && !isIn3DRadius(player, 250))
			return false;

		return player.isAttackableBy(this) && GeoEngine.getInstance().canSeeTarget(this, target);

		
	}
}