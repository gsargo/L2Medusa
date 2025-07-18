package net.sf.l2j.gameserver.model.actor.instance;

import java.util.concurrent.Future;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.enums.actors.NpcRace;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.olympiad.OlympiadGameManager;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SetSummonRemainTime;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.L2Skill;
import net.sf.l2j.gameserver.skills.l2skills.L2SkillSummon;
import net.sf.l2j.gameserver.taskmanager.DecayTaskManager;

public class Servitor extends Summon
{
	private float _expPenalty = 0;
	private int _itemConsumeId = 0;
	private int _itemConsumeCount = 0;
	private int _itemConsumeSteps = 0;
	private int _totalLifeTime = 1200000;
	private int _timeLostIdle = 1000;
	private int _timeLostActive = 1000;
	private int _timeRemaining;
	private int _nextItemConsumeTime;
	
	public int lastShowntimeRemaining;
	
	private Future<?> _summonLifeTask;
	
	public Servitor(int objectId, NpcTemplate template, Player owner, L2Skill skill)
	{
		super(objectId, template, owner);
		
		if (skill != null)
		{
			final L2SkillSummon summonSkill = (L2SkillSummon) skill;
			_itemConsumeId = summonSkill.getItemConsumeIdOT();
			_itemConsumeCount = summonSkill.getItemConsumeOT();
			_itemConsumeSteps = summonSkill.getItemConsumeSteps();
			_totalLifeTime = summonSkill.getTotalLifeTime();
			_timeLostIdle = summonSkill.getTimeLostIdle();
			_timeLostActive = summonSkill.getTimeLostActive();
		}
		_timeRemaining = _totalLifeTime;
		lastShowntimeRemaining = _totalLifeTime;
		
		if (_itemConsumeId == 0 || _itemConsumeSteps == 0)
			_nextItemConsumeTime = -1; // do not consume
		else
			_nextItemConsumeTime = _totalLifeTime - _totalLifeTime / (_itemConsumeSteps + 1);
		if (!isAgathion())
			_summonLifeTask = ThreadPool.scheduleAtFixedRate(new SummonLifetime(getOwner(), this), 1000, 1000);
	}
	
	@Override
	public int getSummonType()
	{
		return 1;
	}
	
	@Override
	public void sendDamageMessage(Creature target, int damage, boolean mcrit, boolean pcrit, boolean miss)
	{
		if (miss || getOwner() == null)
			return;
		
		// Prevents the double spam of system messages, if the target is the owning player.
		if (target.getObjectId() != getOwner().getObjectId())
		{
			if (pcrit || mcrit)
				sendPacket(SystemMessageId.CRITICAL_HIT_BY_SUMMONED_MOB);
			
			if (target.isInvul())
			{
				if (target.isParalyzed())
					sendPacket(SystemMessageId.OPPONENT_PETRIFIED);
				else
					sendPacket(SystemMessageId.ATTACK_WAS_BLOCKED);
			}
			else
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SUMMON_GAVE_DAMAGE_S1).addNumber(damage));
			
			if (getOwner().isInOlympiadMode() && target instanceof Player && ((Player) target).isInOlympiadMode() && ((Player) target).getOlympiadGameId() == getOwner().getOlympiadGameId())
				OlympiadGameManager.getInstance().notifyCompetitorDamage(getOwner(), damage);
		}
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		// Popup for summon if phoenix buff was on
		if (isPhoenixBlessed())
			getOwner().reviveRequest(getOwner(), null, true);
		
		DecayTaskManager.getInstance().add(this, getTemplate().getCorpseTime());
		
		if (_summonLifeTask != null)
		{
			_summonLifeTask.cancel(false);
			_summonLifeTask = null;
		}
		return true;
		
	}
	
	@Override
	public void unSummon(Player owner)
	{
		if (_summonLifeTask != null)
		{
			_summonLifeTask.cancel(false);
			_summonLifeTask = null;
		}
		super.unSummon(owner);
	}
	
	@Override
	public boolean destroyItem(String process, int objectId, int count, WorldObject reference, boolean sendMessage)
	{
		return getOwner().destroyItem(process, objectId, count, reference, sendMessage);
	}
	
	@Override
	public boolean destroyItemByItemId(String process, int itemId, int count, WorldObject reference, boolean sendMessage)
	{
		return getOwner().destroyItemByItemId(process, itemId, count, reference, sendMessage);
	}
	
	@Override
	public boolean isUndead()
	{
		return getTemplate().getRace() == NpcRace.UNDEAD;
	}
	
	public void setExpPenalty(float expPenalty)
	{
		_expPenalty = expPenalty;
	}
	
	public float getExpPenalty()
	{
		return _expPenalty;
	}
	
	public int getItemConsumeCount()
	{
		return _itemConsumeCount;
	}
	
	public int getItemConsumeId()
	{
		return _itemConsumeId;
	}
	
	public int getItemConsumeSteps()
	{
		return _itemConsumeSteps;
	}
	
	public int getNextItemConsumeTime()
	{
		return _nextItemConsumeTime;
	}
	
	public int getTotalLifeTime()
	{
		return _totalLifeTime;
	}
	
	public int getTimeLostIdle()
	{
		return _timeLostIdle;
	}
	
	public int getTimeLostActive()
	{
		return _timeLostActive;
	}
	
	public int getTimeRemaining()
	{
		return _timeRemaining;
	}
	
	public void setNextItemConsumeTime(int value)
	{
		_nextItemConsumeTime = value;
	}
	
	public void decNextItemConsumeTime(int value)
	{
		_nextItemConsumeTime -= value;
	}
	
	public void decTimeRemaining(int value)
	{
		_timeRemaining -= value;
	}
	
	public void addExpAndSp(int addToExp, int addToSp)
	{
		getOwner().addExpAndSp(addToExp, addToSp);
	}
	
	private static class SummonLifetime implements Runnable
	{
		private final Player _player;
		private final Servitor _summon;
		
		protected SummonLifetime(Player activeChar, Servitor summon)
		{
			_player = activeChar;
			_summon = summon;
		}
		
		@Override
		public void run()
		{
			double oldTimeRemaining = _summon.getTimeRemaining();
			int maxTime = _summon.getTotalLifeTime();
			double newTimeRemaining;
			
			// if pet is attacking
			if (_summon.isInCombat())
				_summon.decTimeRemaining(_summon.getTimeLostActive());
			else
				_summon.decTimeRemaining(_summon.getTimeLostIdle());
			
			newTimeRemaining = _summon.getTimeRemaining();
			
			// check if the summon's lifetime has ran out
			if (newTimeRemaining < 0)
				_summon.unSummon(_player);
			else if ((newTimeRemaining <= _summon.getNextItemConsumeTime()) && (oldTimeRemaining > _summon.getNextItemConsumeTime()))
			{
				_summon.decNextItemConsumeTime(maxTime / (_summon.getItemConsumeSteps() + 1));
				
				// check if owner has enought itemConsume, if requested
				if (_summon.getItemConsumeCount() > 0 && _summon.getItemConsumeId() != 0 && !_summon.isDead() && !_summon.destroyItemByItemId("Consume", _summon.getItemConsumeId(), _summon.getItemConsumeCount(), _player, true))
					_summon.unSummon(_player);
			}
			
			// prevent useless packet-sending when the difference isn't visible.
			if ((_summon.lastShowntimeRemaining - newTimeRemaining) > maxTime / 352)
			{
				_player.sendPacket(new SetSummonRemainTime(maxTime, (int) newTimeRemaining));
				_summon.lastShowntimeRemaining = (int) newTimeRemaining;
				_summon.updateEffectIcons();
			}
		}
	}
}