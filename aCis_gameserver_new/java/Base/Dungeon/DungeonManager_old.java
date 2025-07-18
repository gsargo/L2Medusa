package Base.Dungeon;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import Base.XML.XMLDocumentFactory;

public class DungeonManager_old
{
	private static Logger log = Logger.getLogger(DungeonManager_old.class.getName());
	
	private Map<Integer, DungeonTemplate> templates;
	private List<Dungeon> running;
	private List<Integer> dungeonParticipants;
	private boolean reloading = false;
	private Map<String, Long[]> dungeonPlayerData;
	
	protected DungeonManager_old()
	{
		templates = new ConcurrentHashMap<>();
		running = new CopyOnWriteArrayList<>();
		dungeonParticipants = new CopyOnWriteArrayList<>();
		dungeonPlayerData = new ConcurrentHashMap<>();
		
		load();
		// ThreadPool.scheduleAtFixedRate(() -> updateDatabase(), 1000*60*30, 1000*60*60);
		ThreadPool.scheduleAtFixedRate(() -> updateDatabase(), 1000 * 60 * 1, 1000 * 60 * 2);
		
	}
	
	@SuppressWarnings("resource")
	private void updateDatabase()
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement stm = con.prepareStatement("DELETE FROM dungeon");
			stm.execute();
			
			for (String ip : dungeonPlayerData.keySet())
			{
				for (int i = 1; i < dungeonPlayerData.get(ip).length; i++)
				{
					PreparedStatement stm2 = con.prepareStatement("INSERT INTO dungeon VALUES (?,?,?)");
					stm2.setInt(1, i);
					stm2.setString(2, ip);
					stm2.setLong(3, dungeonPlayerData.get(ip)[i]);
					stm2.execute();
					stm2.close();
				}
			}
			
			stm.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean reload()
	{
		if (!running.isEmpty())
		{
			reloading = true;
			return false;
		}
		
		templates.clear();
		load();
		return true;
	}
	
	@SuppressWarnings("resource")
	private void load()
	{
		try
		{
			File f = new File("./events/Dungeon.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equals("dungeon"))
				{
					int id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
					String name = d.getAttributes().getNamedItem("name").getNodeValue();
					int players = Integer.parseInt(d.getAttributes().getNamedItem("players").getNodeValue());
					Map<Integer, Integer> rewards = new HashMap<>();
					String rewardHtm = d.getAttributes().getNamedItem("rewardHtm").getNodeValue();
					Map<Integer, DungeonStage> stages = new HashMap<>();
					
					String rewards_data = d.getAttributes().getNamedItem("rewards").getNodeValue();
					if (!rewards_data.isEmpty()) // If config is empty do not feed the rewards
					{
						String[] rewards_data_split = rewards_data.split(";");
						for (String reward : rewards_data_split)
						{
							String[] reward_split = reward.split(",");
							rewards.put(Integer.parseInt(reward_split[0]), Integer.parseInt(reward_split[1]));
						}
					}
					
					for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
					{
						NamedNodeMap attrs = cd.getAttributes();
						
						if (cd.getNodeName().equals("stage"))
						{
							int order = Integer.parseInt(attrs.getNamedItem("order").getNodeValue());
							String loc_data = attrs.getNamedItem("loc").getNodeValue();
							String[] loc_data_split = loc_data.split(",");
							Location loc = new Location(Integer.parseInt(loc_data_split[0]), Integer.parseInt(loc_data_split[1]), Integer.parseInt(loc_data_split[2]));
							boolean teleport = Boolean.parseBoolean(attrs.getNamedItem("teleport").getNodeValue());
							int minutes = Integer.parseInt(attrs.getNamedItem("minutes").getNodeValue());
							Map<Integer, List<Location>> mobs = new HashMap<>();
							
							for (Node ccd = cd.getFirstChild(); ccd != null; ccd = ccd.getNextSibling())
							{
								NamedNodeMap attrs2 = ccd.getAttributes();
								
								if (ccd.getNodeName().equals("mob"))
								{
									int npcId = Integer.parseInt(attrs2.getNamedItem("npcId").getNodeValue());
									List<Location> locs = new ArrayList<>();
									
									String locs_data = attrs2.getNamedItem("locs").getNodeValue();
									String[] locs_data_split = locs_data.split(";");
									for (String locc : locs_data_split)
									{
										String[] locc_data = locc.split(",");
										locs.add(new Location(Integer.parseInt(locc_data[0]), Integer.parseInt(locc_data[1]), Integer.parseInt(locc_data[2])));
									}
									
									mobs.put(npcId, locs);
								}
							}
							
							stages.put(order, new DungeonStage(order, loc, teleport, minutes, mobs));
						}
					}
					
					templates.put(id, new DungeonTemplate(id, name, players, rewards, rewardHtm, stages));
				}
			}
		}
		catch (Exception e)
		{
			log.log(Level.WARNING, "DungeonManager: Error loading dungeons.xml", e);
			e.printStackTrace();
		}
		
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT * FROM dungeon");
			ResultSet rset = stm.executeQuery();
			
			while (rset.next())
			{
				int dungid = rset.getInt("dungid");
				String ipaddr = rset.getString("ipaddr");
				long lastjoin = rset.getLong("lastjoin");
				
				if (!dungeonPlayerData.containsKey(ipaddr))
				{
					Long[] times = new Long[templates.size() + 1];
					for (int i = 0; i < times.length; i++)
						times[i] = 0L;
					times[dungid] = lastjoin;
					
					dungeonPlayerData.put(ipaddr, times);
				}
				else
					dungeonPlayerData.get(ipaddr)[dungid] = lastjoin;
			}
			
			rset.close();
			stm.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		log.info("DungeonManager: Loaded " + templates.size() + " dungeon templates");
	}
	
	public synchronized void removeDungeon(Dungeon dungeon)
	{
		running.remove(dungeon);
		
		if (reloading && running.isEmpty())
		{
			reloading = false;
			reload();
		}
	}
	
	private boolean validateSoloDungeon(Player player, DungeonTemplate template)
	{
		if (player.isInParty())
		{
			player.getParty().disband();
		}
		
		String playerIP = player.getIP();
		if (dungeonPlayerData.containsKey(playerIP) && (System.currentTimeMillis() - dungeonPlayerData.get(playerIP)[template.getId()] < 1000 * 60 * 60 * 12))
		{
			player.sendMessage("12 hours have not passed since you last entered this Dungeon.");
			//return false;
		} 
		
		
		if (player.getStatus().getLevel() < 76)
		{
			player.sendMessage("Your level is less than 76.");
			return false;
		}
		
		if (player.isInTournamentMatch() || player.isInTournamentMode()|| player.getTournamentTeam() != null) 
		{
			player.sendMessage("You are already participating in tournament.");
			return false;
		} 

		if(player.getOlympiadGameId() != -1 || player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendMessage("You can not proceed because you are participating in Olympiad Games!");
			return false;
		}
		
		
		return true;
	}
	
	private boolean validatePartyDungeon(Player player, DungeonTemplate template) 
	{
		/*if (!player.isInParty() || player.getParty().getMembersCount() < template.getPlayers())
		{
			player.sendMessage("You need a party of " + template.getPlayers() + " or more players to enter this Dungeon.");
			return false;
		}
		*/
		if (!player.getParty().isLeader(player))
		{
			player.sendMessage("The party leader can only apply entrance.");
			return false;
		}
		
		for (Player pm : player.getParty().getMembers())
		{
			if (pm.getStatus().getLevel() < 76)
			{
				player.sendMessage("Player " + pm.getName() + " level is less than 76.");
				return false;
			}
			
			if (pm.isInTournamentMatch() || pm.isInTournamentMode() || pm.getTournamentTeam() != null) 
			{
				player.sendMessage("Player "+ pm.getName() + " is participating in tournament.");
				//return false;
			} 

			if(pm.getOlympiadGameId() != -1 || pm.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(pm))
			{
				player.sendMessage("You can not participate because player "  + pm.getName() + " of your team is participating in Olympiad Games!");
				return false;
			}
			

			String partyMemberIP = pm.getIP();
			
			if (dungeonPlayerData.containsKey(partyMemberIP) && (System.currentTimeMillis() - dungeonPlayerData.get(partyMemberIP)[template.getId()] < 1000 * 60 * 60 * 12))
			{
				player.sendMessage("Player " + pm.getName() + " cannot join this Dungeon because 12 hours have not passed since he last joined.");
				//return false;
			}
			
		}
		

		
		return true;
	}
	
	
	public synchronized void enterDungeon(int id, Player player)
	{
		if (reloading)
		{
			player.sendMessage("The Dungeon system is reloading, please try again in a few minutes.");
			return;
		}
		
		
		DungeonTemplate template = templates.get(id);
		
		if (template == null)
			return; //should never happen
		
		var canProceed = false;
		
		if (template.getPlayers() == 1) 
		{
			canProceed = validateSoloDungeon(player, template);
		}
		else 
		{
			canProceed = validatePartyDungeon(player, template);
		}
		
		
		if (!canProceed) 
			return;
		
		List<Player> players = new ArrayList<>();
		if (player.isInParty())
		{
			for (Player pm : player.getParty().getMembers())
			{
				String pmip = pm.getIP();
				
				dungeonParticipants.add(pm.getObjectId());
				players.add(pm);
				if (dungeonPlayerData.containsKey(pmip))
					dungeonPlayerData.get(pmip)[template.getId()] = System.currentTimeMillis();
				else
				{
					Long[] times = new Long[templates.size() + 1];
					for (int i = 0; i < times.length; i++)
						times[i] = 0L;
					times[template.getId()] = System.currentTimeMillis();
					dungeonPlayerData.put(pmip, times);
				}
			}
		}
		else
		{
			String pmip = player.getIP();

			dungeonParticipants.add(player.getObjectId());
			players.add(player);
			if (dungeonPlayerData.containsKey(pmip))
				dungeonPlayerData.get(pmip)[template.getId()] = System.currentTimeMillis();
			else
			{
				Long[] times = new Long[templates.size() + 1];
				for (int i = 0; i < times.length; i++)
					times[i] = 0L;
				times[template.getId()] = System.currentTimeMillis();
				dungeonPlayerData.put(pmip, times);
			}
		}
		
		running.add(new Dungeon(template, players));
	}
	
	public boolean isReloading()
	{
		return reloading;
	}
	
	public boolean isInDungeon(Player player)
	{
		for (Dungeon dungeon : running)
			for (Player p : dungeon.getPlayers())
				if (p == player)
					return true;
				
		return false;
	}
	
	public Map<String, Long[]> getPlayerData()
	{
		return dungeonPlayerData;
	}
	
	public List<Integer> getDungeonParticipants()
	{
		return dungeonParticipants;
	}
	
	public static DungeonManager_old getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DungeonManager_old instance = new DungeonManager_old();
	}
}