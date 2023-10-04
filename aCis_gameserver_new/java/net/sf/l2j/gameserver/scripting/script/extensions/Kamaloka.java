package net.sf.l2j.gameserver.scripting.script.extensions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.data.sql.SpawnTable;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;

public class Kamaloka extends Quest
{
	
	int[] npcId =
	{
		60035
	}; // which npc to use for teleport
	int itemId = 4037; // item to enter
	String uniqueId = "Kamaloka";
	long time = TimeUnit.MINUTES.toMillis(60);// time to close instance - 60 min
	long delay = TimeUnit.HOURS.toMillis(24);// player delay - 24 hours
	
	Map<Integer, List<Spawn>> spawns = new HashMap<>();
	Map<Integer, Boolean> kamalokaStatus = new HashMap<>();
	Map<Integer, List<Integer>> players = new HashMap<>();
	Map<Integer, List<SpawnMonsters>> monsters = new HashMap<>();
	Map<Integer, List<SpawnMonsters>> boss = new HashMap<>();
	Map<Integer, Location> kamaLoc = new HashMap<>();
	Map<Integer, Location> backLoc = new HashMap<>();
	
	public class SpawnMonsters
	{
		public int monsterId;
		Location loc;
		
		public SpawnMonsters(int _monsterId, Location _loc)
		{
			monsterId = _monsterId;
			loc = _loc;
		}
		
	}
	
	/* YOU NEED CHANGE ONLY THIS CODE, NO MORE */
	{// Kamaloka`s Unique Settings
		// Kamaloka 1 SETTINGS
		kamalokaStatus.put(1, true); // Set available kamaloka
		monsters.put(1, Arrays.asList(new SpawnMonsters(31227, new Location(1, 2, 3)), new SpawnMonsters(31227, new Location(1, 2, 3)))); // List of Spawn Monsters
		boss.put(1, Arrays.asList(new SpawnMonsters(31227, new Location(1, 2, 3)), new SpawnMonsters(31227, new Location(1, 2, 3)))); // List of Spawn Bosses
		kamaLoc.put(1, new Location(1, 2, 3)); // Kamaloka 1 Location
		backLoc.put(1, new Location(1, 2, 3)); // Kamaloka 1 back Location
		// Kamaloka 2 SETTINGS
		kamalokaStatus.put(2, true); // Set available kamaloka
		monsters.put(2, Arrays.asList(new SpawnMonsters(31227, new Location(1, 2, 3)), new SpawnMonsters(31227, new Location(1, 2, 3)))); // List of Spawn Monsters
		boss.put(2, Arrays.asList(new SpawnMonsters(31227, new Location(1, 2, 3)), new SpawnMonsters(31227, new Location(1, 2, 3)))); // List of Spawn Bosses
		kamaLoc.put(2, new Location(1, 2, 3)); // Kamaloka 2 Location
		backLoc.put(2, new Location(1, 2, 3)); // Kamaloka 2 back Location
		// Kamaloka 3 SETTINGS
		kamalokaStatus.put(3, true); // Set available kamaloka
		monsters.put(3, Arrays.asList(new SpawnMonsters(31227, new Location(1, 2, 3)), new SpawnMonsters(31227, new Location(1, 2, 3)))); // List of Spawn Monsters
		boss.put(3, Arrays.asList(new SpawnMonsters(31227, new Location(1, 2, 3)), new SpawnMonsters(31227, new Location(1, 2, 3)))); // List of Spawn Bosses
		kamaLoc.put(3, new Location(1, 2, 3)); // Kamaloka 3 Location
		backLoc.put(3, new Location(1, 2, 3)); // Kamaloka 3 back Location
		// Kamaloka 4 SETTINGS
		kamalokaStatus.put(4, true); // Set available kamaloka
		monsters.put(4, Arrays.asList(new SpawnMonsters(31227, new Location(1, 2, 3)), new SpawnMonsters(31227, new Location(1, 2, 3)))); // List of Spawn Monsters
		boss.put(4, Arrays.asList(new SpawnMonsters(31227, new Location(1, 2, 3)), new SpawnMonsters(31227, new Location(1, 2, 3)))); // List of Spawn Bosses
		kamaLoc.put(4, new Location(1, 2, 3)); // Kamaloka 4 Location
		backLoc.put(4, new Location(1, 2, 3)); // Kamaloka 4 back Location
		// Kamaloka 5 SETTINGS
		kamalokaStatus.put(5, true); // Set available kamaloka
		monsters.put(5, Arrays.asList(new SpawnMonsters(31227, new Location(1, 2, 3)), new SpawnMonsters(31227, new Location(1, 2, 3)))); // List of Spawn Monsters
		boss.put(5, Arrays.asList(new SpawnMonsters(31227, new Location(1, 2, 3)), new SpawnMonsters(31227, new Location(1, 2, 3)))); // List of Spawn Bosses
		kamaLoc.put(5, new Location(1, 2, 3)); // Kamaloka 5 Location
		backLoc.put(5, new Location(1, 2, 3)); // Kamaloka 5 back Location
	}
	/* YOU NEED CHANGE ONLY THIS CODE, NO MORE */
	
	public Kamaloka()
	{
		super(-1, "Kamaloka");
		addStartNpc(npcId);
		addTalkId(npcId);
		monsters.forEach((K, V) ->
		{
			V.forEach(D ->
			{
				addKillId(D.monsterId);
			});
		});
		boss.forEach((K, V) ->
		{
			V.forEach(D ->
			{
				addKillId(D.monsterId);
			});
		});
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestList().getQuestState(uniqueId);
		if (st == null)
		{
			st = new QuestState(player, this);
		}
		if (player.getClassId().getId() < 88) // can we set it by class , not by lvl? i mean 3rd class only
		{
			player.sendMessage("You do not meet the requirements , you need to reach 3rd class in order to access.");
			return super.onTalk(npc, player);
		}
		Party pt = player.getParty();
		if (pt == null)
		{
			player.sendMessage("PARTY");
			return super.onTalk(npc, player);
		}
		if (pt.getLeader() != player)
		{
			player.sendMessage("You are not a party leader");
			return super.onTalk(npc, player);
		}
		if (player.getInventory().getItemCount(itemId) == 0)
		{
			player.sendMessage("You need item X to enter into Kamaloka.");
			return super.onTalk(npc, player);
		}
		long cd;
		for (Player player1 : pt.getMembers())
		{
			QuestState qs = player1.getQuestList().getQuestState(uniqueId);
			if (qs == null)
				st = new QuestState(player, this);
			cd = Long.parseLong(st.get("delay"));
			if (System.currentTimeMillis() < cd)
			{
				player.sendMessage("Incorrect condition, player: " + player1.getName() + " Kamaloka delay");
				return super.onTalk(npc, player);
			}
		}
		player.destroyItemByItemId("kamaloka", itemId, 1, player, true);
		
		Integer kamaId = 0;
		int id = 0;
		for (boolean status : kamalokaStatus.values())
		{
			id++;
			if (status)
			{
				kamaId = id;
				break;
			}
		}
		if (kamaId == 0)
		{
			player.sendMessage("All instance zones are now full, please try again later.");
			return super.onTalk(npc, player);
		}
		
		for (Player player1 : pt.getMembers())
		{
			if (player1.isAlikeDead() || !player1.isOnline())
				continue;
			QuestState qs = player1.getQuestList().getQuestState(uniqueId);
			if (qs == null)
				qs = new QuestState(player, this);
			qs.set("delay", System.currentTimeMillis() + delay);
			player1.teleToLocation(kamaLoc.get(kamaId));
			List<Integer> oldList = new ArrayList<>(players.get(kamaId));
			oldList.add(player1.getObjectId());
			players.put(kamaId, oldList);
		}
		spawnMonsters(kamaId);
		
		return super.onTalk(npc, player);
	}
	
	// NOT WORKING NOW
	// @Override
	// public String onKill(Npc npc, Creature killer) {
	// Spawn spawn = npc.getSpawn();
	// spawns.remove(spawn);
	// return super.onKill(npc, killer);
	// }
	
	void spawnMonsters(Integer kamaId)
	{
		kamalokaStatus.put(kamaId, false);
		List<SpawnMonsters> spawnsByKama = monsters.get(kamaId);
		for (SpawnMonsters sp : spawnsByKama)
		{
			final Spawn newSpawn;
			try
			{
				Integer K = sp.monsterId;
				Location V = sp.loc;
				newSpawn = new Spawn(K);
				newSpawn.setLoc(V.getX(), V.getY(), V.getZ(), 0);
				SpawnTable.getInstance().addSpawn(newSpawn, false);
				newSpawn.setRespawnState(false);
				newSpawn.doSpawn(false);
				List<Spawn> oldList = new ArrayList<>(spawns.get(kamaId));
				oldList.add(newSpawn);
				spawns.put(kamaId, oldList);
			}
			catch (ClassNotFoundException | NoSuchMethodException e)
			{
				e.printStackTrace();
			}
		}
		spawnsByKama = boss.get(kamaId);
		for (SpawnMonsters sp : spawnsByKama)
		{
			final Spawn newSpawn;
			try
			{
				Integer K = sp.monsterId;
				Location V = sp.loc;
				newSpawn = new Spawn(K);
				newSpawn.setLoc(V.getX(), V.getY(), V.getZ(), 0);
				SpawnTable.getInstance().addSpawn(newSpawn, false);
				newSpawn.setRespawnState(false);
				newSpawn.doSpawn(false);
				List<Spawn> oldList = new ArrayList<>(spawns.get(kamaId));
				oldList.add(newSpawn);
				spawns.put(kamaId, oldList);
			}
			catch (ClassNotFoundException | NoSuchMethodException e)
			{
				e.printStackTrace();
			}
		}
		ThreadPool.schedule(() -> close(kamaId), time);
	}
	
	void close(Integer kamaId)
	{
		Location back = backLoc.get(kamaId);
		for (int id : players.get(kamaId))
		{
			Player player = World.getInstance().getPlayer(id);
			if (player != null)
				player.teleToLocation(back);
			else
				tele(id, back);
			
		}
		players.get(kamaId).clear();
		spawns.get(kamaId).forEach((spawn) ->
		{
			// TODO CHECK THIS FOR NULL POINT EXCEPTION, THANKS
			if (spawn != null && spawn.getNpc() != null)
			{
				spawn.getNpc().deleteMe();
			}
		});
		spawns.get(kamaId).clear();
		kamalokaStatus.put(kamaId, true);
	}
	
	void tele(Integer objId, Location loc)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE characters SET x=? y=? z=? WHERE obj_id=?"))
		{
			ps.setInt(1, loc.getX());
			ps.setInt(2, loc.getY());
			ps.setInt(3, loc.getZ());
			ps.setInt(4, objId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to teleport character.", e);
		}
	}
}
