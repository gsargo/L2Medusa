package net.sf.l2j.gameserver.model.actor.instance;

/**
 * This class manages all classic raid bosses.<br>
 * <br>
 * Raid Bosses (RB) are mobs which are supposed to be defeated by a party of several players. It extends most of {@link Monster} aspects.<br>
 * <br>
 * They automatically teleport if out of their initial spawn area, and can randomly attack a Player from their Hate List once attacked.<br>
 * <br>
 * Their looting rights are affected by {@link CommandChannel}s. The first who attacks got the priority over loots. Those rights are lost if no attack has been done for 900sec.
 */
/*
 * public class RaidBoss2 extends Monster { private ScheduledFuture<?> _maintenanceTask; public RaidBoss2(int objectId, NpcTemplate template) { super(objectId, template); setRaid(true); } //public int [] bosses_id = {60010,25325,60024,60007,25163,25019,25126}; //desired ids to announce
 * @Override public void onSpawn() { // No random walk allowed. setNoRndWalk(true); // Basic behavior. super.onSpawn(); //my code // Version 2 if( getNpcId() == 60010 || getNpcId() == 25325 || getNpcId() == 60024 || getNpcId() == 60007 || getNpcId() == 25163 || getNpcId() == 25019 || getNpcId() ==
 * 25126 ) { World.announceToOnlinePlayers(getName() + " has spawned in the world.", true); } // "AI task" for regular bosses. _maintenanceTask = ThreadPool.scheduleAtFixedRate(() -> { // Don't bother with dead bosses. if (!isDead()) { // The boss isn't in combat, check the teleport possibility. if
 * (!isInCombat()) { // Gordon is excluded too. if (getNpcId() != 29095 && Rnd.nextBoolean()) { // Spawn must exist. final Spawn spawn = getSpawn(); if (spawn == null) return; // If the boss is above drift range (or 200 minimum), teleport him on his spawn. if (!isIn3DRadius(spawn.getLoc(),
 * Math.max(Config.MAX_DRIFT_RANGE, 200))) teleportTo(spawn.getLoc(), 0); } } // Randomized attack if the boss is already attacking. else if (Rnd.get(5) == 0) getAggroList().randomizeAttack(); } // For each minion (if any), randomize the attack. if (hasMinions()) { for (Monster minion :
 * getMinionList().getSpawnedMinions()) { // Don't bother with dead minions. if (minion.isDead() || !minion.isInCombat()) return; // Randomized attack if the boss is already attacking. if (Rnd.get(3) == 0) minion.getAggroList().randomizeAttack(); } } }, 1000, 60000); }
 * @Override public boolean doDie(Creature killer) { if (!super.doDie(killer)) return false; if (_maintenanceTask != null) { _maintenanceTask.cancel(false); _maintenanceTask = null; } if (killer != null) { final Player player = killer.getActingPlayer(); if (player != null) { if(getNpcId() == 60010
 * || getNpcId() == 25325 || getNpcId() == 60024 || getNpcId() == 60007 || getNpcId() == 25163 || getNpcId() == 25019 || getNpcId() == 25126 ) { World.announceToOnlinePlayers(getName() + " has been killed by Clan: " + player.getClan().getName(), true); } //custom code
 * broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL)); broadcastPacket(new PlaySound("systemmsg_e.1209")); final Party party = player.getParty(); if (party != null) { for (Player member : party.getMembers()) { RaidPointManager.getInstance().addPoints(member,
 * getNpcId(), (getStatus().getLevel() / 2) + Rnd.get(-5, 5)); if (member.isNoble()) HeroManager.getInstance().setRBkilled(member.getObjectId(), getNpcId()); } } else { RaidPointManager.getInstance().addPoints(player, getNpcId(), (getStatus().getLevel() / 2) + Rnd.get(-5, 5)); if (player.isNoble())
 * HeroManager.getInstance().setRBkilled(player.getObjectId(), getNpcId()); } } } RaidBossManager.getInstance().onDeath(this); return true; }
 * @Override public void deleteMe() { if (_maintenanceTask != null) { _maintenanceTask.cancel(false); _maintenanceTask = null; } super.deleteMe(); } }
 */