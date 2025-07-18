package net.sf.l2j.gameserver.model.actor.move;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.enums.IntentionType;
import net.sf.l2j.gameserver.enums.actors.MoveType;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.location.Location;

public class SummonMove extends CreatureMove<Summon>
{
	private static final int AVOID_RADIUS = 70;
	
	public SummonMove(Summon actor)
	{
		super(actor);
	}
	
	@Override
	public void avoidAttack(Creature attacker)
	{
		final Player owner = _actor.getOwner();
		
		if (owner == null || owner == attacker || !owner.isIn3DRadius(_actor, 2 * AVOID_RADIUS) || !owner.isInCombat())
			return;
		
		if (_actor.getAI().getCurrentIntention().getType() != IntentionType.ACTIVE && _actor.getAI().getCurrentIntention().getType() != IntentionType.FOLLOW)
			return;
		
		if (_actor.isMoving() || _actor.isDead() || _actor.isMovementDisabled())
			return;
		
		final int ownerX = owner.getX();
		final int ownerY = owner.getY();
		final double angle = Math.toRadians(Rnd.get(-90, 90)) + Math.atan2(ownerY - _actor.getY(), ownerX - _actor.getX());
		
		final int targetX = ownerX + (int) (AVOID_RADIUS * Math.cos(angle));
		final int targetY = ownerY + (int) (AVOID_RADIUS * Math.sin(angle));
		
		maybeMoveToLocation(new Location(targetX, targetY, _actor.getZ()), 0, true, false);
	}
	
	@Override
	
	protected void offensiveFollowTask(Creature target, int offset)
	{
		// No follow task, return.
		if (_followTask == null)
			return;
		
		// Invalid pawn to follow, or the pawn isn't registered on knownlist.
		if (!_actor.knows(target))
		{
			_actor.getAI().setFollowStatus(false);
			_actor.getAI().tryToActive();
			return;
		}
		
		Location targetLoc = _actor.getPosition().clone();
		targetLoc.setLocationMinusOffset(target.getPosition(), target.getStatus().getMoveSpeed() / -2);
		
		// Don't bother moving if already in radius.
		if (getMoveType() == MoveType.GROUND ? _actor.isIn2DRadius(targetLoc, offset) : _actor.isIn3DRadius(targetLoc, offset))
			return;
		
		_pawn = target;
		_offset = offset;
		
		moveToLocation(targetLoc, true);
	}
	
	@Override
	protected void friendlyFollowTask(Creature target, int offset)
	{
		// No follow task, return.
		if (_followTask == null)
			return;
		
		// Invalid pawn to follow, or the pawn isn't registered on knownlist.
		if (!_actor.knows(target))
		{
			_actor.getAI().setFollowStatus(false);
			_actor.getAI().tryToActive();
			return;
		}
		
		Location targetLoc = _actor.getPosition().clone();
		// custom check , if is agathion , follow the player from a closer distance
		// Pet agathion = World.getInstance().getAgat(getObjectId());
		// if(_actor.getSummon()._isAgathion)
		// {
		// targetLoc.setLocationMinusOffset(target.getPosition(),(int) (offset + target.getCollisionRadius() + 1));
		// }
		// else
			targetLoc.setLocationMinusOffset(target.getPosition(), (int) (offset + target.getCollisionRadius() + _actor.getCollisionRadius()));
		
		// Don't bother moving if already in radius.
		if (getMoveType() == MoveType.GROUND ? _actor.isIn2DRadius(targetLoc, offset) : _actor.isIn3DRadius(targetLoc, offset))
			return;
		
		_pawn = null;
		_offset = 0;
		
		moveToLocation(targetLoc, true);
	}
}