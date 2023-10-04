package net.sf.l2j.gameserver.instance.group;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.instance.AbstractInstance;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.model.spawn.SpawnTemplate;

public final class SpawnGroup extends AbstractGroup<Spawn>
{
	private final int instanceId;
	private final AbstractInstance gameInstance;
	
	public SpawnGroup(int instanceId, AbstractInstance gameInstance)
	{
		this.instanceId = instanceId;
		this.gameInstance = gameInstance;
	}
	
	public final int getInstanceId()
	{
		return instanceId;
	}
	
	public final void spawnAll()
	{
		findAll().forEach(spawn -> spawnMonster(spawn));
	}
	
	private final void spawnMonster(Spawn spawn)
	{
		try
		{
			final Npc npc = spawn.doSpawn();
			npc.setGameInstance(gameInstance);
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't spawn monster, ", e);
		}
	}
	
	public final void unspawnAll()
	{
		findAll().forEach(Spawn::deleteMe);
	}
	
	public final long getAliveMonstersCount()
	{
		return findAll().stream().map(spawn -> spawn.getNpc()).filter(Objects::nonNull).filter(Predicate.not(Npc::isDead)).count();
	}
	
	public final List<Spawn> getSpawns(Predicate<? super Spawn> filter)
	{
		return findAll().stream().filter(filter).collect(Collectors.toList());
	}
	
	public final void addSpawns(Collection<SpawnTemplate> spawnTemplates)
	{
		spawnTemplates.forEach(this::addSpawn);
	}
	
	public final void addSpawn(SpawnTemplate template)
	{
		try
		{
			final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(template.getNpcId());
			final Spawn spawn = new Spawn(npcTemplate);
			spawn.setRespawnState(template.getRespawnDelay() > 0);
			spawn.setRespawnDelay(template.getRespawnDelay());
			spawn.setLoc(template.getLocation());
			spawn.setInstanceId(instanceId);
			add(spawn);
		}
		catch (final Exception e)
		{
			LOGGER.error("{}: Couldn't add spawn with id {}", getClass().getSimpleName(), template.getNpcId(), e);
		}
	}
}