package net.sf.l2j.gameserver.data.xml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.enums.TeamType;
import net.sf.l2j.gameserver.instance.InstanceType;
import net.sf.l2j.gameserver.instance.model.InstanceMap;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.spawn.SpawnTemplate;
import net.sf.l2j.gameserver.reward.RewardType;

import org.w3c.dom.Document;

public class InstanceData implements IXmlReader
{
	private final List<InstanceMap> maps = new ArrayList<>();
	private final Map<InstanceType, List<SpawnTemplate>> spawnTemplates = new HashMap<>();
	private final List<InstanceType> availableEventTypes = new ArrayList<>();
	
	private InstanceData()
	{
		load();
		LOGGER.info("Loaded {} instance maps.", maps.size());
		LOGGER.info("Loaded {} spawn templates.", getSpawnTemplatesCount());
		LOGGER.info("Loaded {} available event types.", availableEventTypes.size());
	}
	
	@Override
	public void load()
	{
		parseFile("./data/xml/instance/");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "rewards", rewardsNode ->
			{
				final StatSet rewardsSet = parseAttributes(rewardsNode);
				final InstanceType instanceType = rewardsSet.getEnum("instance", InstanceType.class);
				
				forEach(rewardsNode, "reward", rewardNode ->
				{
					final StatSet rewardSet = parseAttributes(rewardNode);
					final RewardType rewardType = rewardSet.getEnum("type", RewardType.class, RewardType.ITEM);
				});
			});
			
			forEach(listNode, "spawns", spawnsNode ->
			{
				final StatSet set = parseAttributes(spawnsNode);
				final InstanceType type = set.getEnum("instance", InstanceType.class, InstanceType.RAID);
				final List<SpawnTemplate> templates = new ArrayList<>();
				
				forEach(spawnsNode, "spawn", spawnNode -> templates.add(new SpawnTemplate(parseAttributes(spawnNode))));
				
				spawnTemplates.put(type, templates);
			});
			
			forEach(listNode, "maps", mapsNode ->
			{
				forEach(mapsNode, "map", mapNode ->
				{
					final StatSet set = parseAttributes(mapNode);
					final Map<TeamType, List<Location>> locations = new HashMap<>();
					final List<InstanceType> instanceTypes = new ArrayList<>();
					
					forEach(mapNode, "location", locationNode ->
					{
						final StatSet locationSet = parseAttributes(locationNode);
						final TeamType teamType = locationSet.getEnum("team", TeamType.class, TeamType.NONE);
						locations.computeIfAbsent(teamType, x -> new ArrayList<>()).add(new Location(locationSet));
					});
					
					forEach(mapNode, "instance", instanceNode ->
					{
						final StatSet instanceSet = parseAttributes(instanceNode);
						final InstanceType instanceType = instanceSet.getEnum("type", InstanceType.class, InstanceType.RAID);
						instanceTypes.add(instanceType);
					});
					
					set.set("locations", locations);
					set.set("instanceTypes", instanceTypes);
					
					maps.add(new InstanceMap(set));
				});
			});
			
			forEach(listNode, "events", eventsNode ->
			{
				forEach(eventsNode, "event", eventNode ->
				{
					final StatSet set = parseAttributes(eventNode);
					availableEventTypes.add(set.getEnum("type", InstanceType.class));
				});
			});
		});
	}
	
	public final List<SpawnTemplate> getSpawns(InstanceType type)
	{
		return spawnTemplates.getOrDefault(type, Collections.emptyList());
	}
	
	private final int getSpawnTemplatesCount()
	{
		return spawnTemplates.values().stream().mapToInt(List::size).sum();
	}
	
	public final InstanceMap getRandomInstanceMap(InstanceType type)
	{
		return Rnd.get(getMaps(type));
	}
	
	private final List<InstanceMap> getMaps(InstanceType type)
	{
		return maps.stream().filter(m -> m.isAvailableForInstanceType(type)).collect(Collectors.toList());
	}
	
	public final List<InstanceType> getAvailableEventTypes()
	{
		return availableEventTypes;
	}
	
	public static InstanceData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final InstanceData INSTANCE = new InstanceData();
	}
}