package net.sf.l2j.gameserver.data.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.data.sql.SpawnTable;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.enums.BossStatus;
import net.sf.l2j.gameserver.model.actor.instance.RaidBoss;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.spawn.BossSpawn;
import net.sf.l2j.gameserver.model.spawn.Spawn;

/**
 * Loads and store {@link RaidBoss}es informations, using {@link BossSpawn} holder.
 */
public class RaidBossManager
{
	protected static final CLogger LOGGER = new CLogger(RaidBossManager.class.getName());
	
	private static final String LOAD_RAIDBOSSES = "SELECT * from raidboss_spawnlist ORDER BY boss_id";
	private static final String INSERT_RAIDBOSS = "INSERT INTO raidboss_spawnlist (boss_id,loc_x,loc_y,loc_z,heading,respawn_time,currentHp,currentMp) values(?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE respawn_time=VALUES(respawn_time),currentHp=VALUES(currentHp),currentMp=VALUES(currentMp)";
	private static final String SAVE_RAIDBOSS = "UPDATE raidboss_spawnlist SET currentHP = ?, currentMP = ? WHERE boss_id = ?";
	
	protected final Map<Integer, BossSpawn> _spawns = new HashMap<>();
	
	public RaidBossManager()
	{
		load();
	}
	
	private void load()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_RAIDBOSSES);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final NpcTemplate template = NpcData.getInstance().getTemplate(rs.getInt("boss_id"));
				
				if (template == null || !template.isType("RaidBoss"))
				{
					LOGGER.warn("Couldn't load raidboss #{}.", rs.getInt("boss_id"));
					continue;
				}
				
				// Generate a Spawn.
				final Spawn spawn = new Spawn(template);
				spawn.setLoc(rs.getInt("loc_x"), rs.getInt("loc_y"), rs.getInt("loc_z"), rs.getInt("heading"));
				spawn.setRespawnMinDelay(rs.getInt("spawn_time"));
				spawn.setRespawnMaxDelay(rs.getInt("random_time"));
				
				addNewSpawn(spawn, rs.getLong("respawn_time"), rs.getDouble("currentHP"), rs.getDouble("currentMP"), false);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Error restoring raid bosses.", e);
		}
		LOGGER.info("Loaded {} raid bosses.", _spawns.size());
	}
	
	public void reload()
	{
		// Cancel running tasks and cleanup _spawns Map.
		cleanUp(false);
		
		// Load data.
		load();
	}
	
	/**
	 * @return a {@link Collection} holding all existing {@link BossSpawn}s.
	 */
	public Collection<BossSpawn> getBossSpawns()
	{
		return _spawns.values();
	}
	
	/**
	 * @param id : The id used as reference.
	 * @return an existing {@link BossSpawn} based on a id, or null otherwise.
	 */
	public BossSpawn getBossSpawn(int id)
	{
		return _spawns.get(id);
	}
	
	/**
	 * @param id : The id used as reference.
	 * @return the {@link BossStatus} of an existing {@link BossSpawn}, based on a id.
	 */
	public BossStatus getStatus(int id)
	{
		final BossSpawn bs = _spawns.get(id);
		return (bs == null) ? BossStatus.UNDEFINED : bs.getStatus();
	}
	
	/**
	 * Retrieve an existing {@link BossSpawn} based on a {@link RaidBoss} id, and activate it's {@link BossSpawn#onDeath()} method.
	 * @param boss : The RaidBoss instance used as reference.
	 */
	public void onDeath(RaidBoss boss)
	{
		final BossSpawn bs = _spawns.get(boss.getNpcId());
		if (bs != null)
			bs.onDeath();
	}
	
	/**
	 * Add a new {@link BossSpawn}, based on a {@link Spawn}. It is both used on server startup and admincommand.<br>
	 * <br>
	 * Database is either refreshed using forceSave boolean flag, or if a difference of status has been detected.
	 * @param spawn : The spawn used as reference.
	 * @param respawnTime : The respawn time to set.
	 * @param currentHP : The HP of the instance to set.
	 * @param currentMP : The MP of the instance to set.
	 * @param forceSave : If true, we force the insertion of this spawn into the database.
	 */
	public void addNewSpawn(Spawn spawn, long respawnTime, double currentHP, double currentMP, boolean forceSave)
	{
		if (spawn == null)
			return;
		
		final int id = spawn.getNpcId();
		if (_spawns.containsKey(id))
			return;
		
		final long time = System.currentTimeMillis();
		
		// Add the spawn.
		SpawnTable.getInstance().addSpawn(spawn, false);
		
		// We generate the BossSpawn.
		final BossSpawn bs = new BossSpawn();
		bs.setSpawn(spawn);
		
		// Boss is alive, spawn him.
		if (respawnTime == 0L || time > respawnTime)
		{
			final RaidBoss raidboss = (RaidBoss) spawn.doSpawn(false);
			
			currentHP = (currentHP == 0) ? raidboss.getStatus().getMaxHp() : currentHP;
			currentMP = (currentMP == 0) ? raidboss.getStatus().getMaxMp() : currentMP;
			
			// Set HP, MP.
			raidboss.getStatus().setHpMp(currentHP, currentMP);
			
			bs.setStatus(BossStatus.ALIVE);
			bs.setCurrentHp(currentHP);
			bs.setCurrentMp(currentMP);
			bs.setRespawnTime(0);
			
			// announce raid bosses spawn (of a list)
			// Announcement _an;
			// _an.Announcement("RaidBoss : " + raidboss.getName() + " has spawning!",true);
			
			//
			// Time passed by, or we force the database save ; save data on database.
			if (time > respawnTime || forceSave)
			{
				try (Connection con = ConnectionPool.getConnection();
					PreparedStatement ps = con.prepareStatement(INSERT_RAIDBOSS))
				{
					ps.setInt(1, spawn.getNpcId());
					ps.setInt(2, spawn.getLocX());
					ps.setInt(3, spawn.getLocY());
					ps.setInt(4, spawn.getLocZ());
					ps.setInt(5, spawn.getHeading());
					ps.setLong(6, respawnTime);
					ps.setDouble(7, currentHP);
					ps.setDouble(8, currentMP);
					ps.executeUpdate();
				}
				catch (Exception e)
				{
					LOGGER.error("Couldn't store raid boss #{}.", e, id);
				}
			}
		}
		// Boss isn't alive, we generate a scheduled task using its respawn time.
		else
		{
			long spawnTime = respawnTime - time;
			bs.setTask(ThreadPool.schedule(() -> bs.onSpawn(), spawnTime));
			
			bs.setStatus(BossStatus.DEAD);
			bs.setCurrentHp(0);
			bs.setCurrentMp(0);
			bs.setRespawnTime(respawnTime);
		}
		
		// Add the BossSpawn.
		_spawns.put(id, bs);
	}
	
	/**
	 * Delete an existing {@link BossSpawn} based on a {@link Spawn}. Drop it from {@link SpawnTable}.
	 * @param spawn : The spawn used as reference.
	 */
	public void deleteSpawn(Spawn spawn)
	{
		if (spawn == null)
			return;
		
		final int id = spawn.getNpcId();
		
		// Delete the entry. If we couldn't find it, return directly.
		final BossSpawn bs = _spawns.remove(id);
		if (bs == null)
			return;
		
		// Make actions related to despawn.
		bs.onDespawn();
		
		// Remove the spawn.
		SpawnTable.getInstance().deleteSpawn(spawn, false);
	}
	
	/**
	 * Cleanup data. Cancel all running tasks, save hp/mp and location if boolean flag is true, delete existing {@link BossSpawn} entries.
	 * @param saveOnDb : If true, we also save informations on database.
	 */
	public void cleanUp(boolean saveOnDb)
	{
		// Cancel all running tasks.
		for (BossSpawn bs : _spawns.values())
			bs.cancelTask();
		
		// Save HP/MP and locations if boolean flag is set to true.
		if (saveOnDb)
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(SAVE_RAIDBOSS))
			{
				for (Entry<Integer, BossSpawn> entry : _spawns.entrySet())
				{
					final BossSpawn bs = entry.getValue();
					
					// We only bother with living bosses.
					if (bs.getStatus() == BossStatus.ALIVE)
					{
						ps.setDouble(1, bs.getBoss().getStatus().getHp());
						ps.setDouble(2, bs.getBoss().getStatus().getMp());
						ps.setInt(3, entry.getKey());
						ps.addBatch();
					}
				}
				ps.executeBatch();
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't save raid bosses.", e);
			}
		}
		
		// Delete spawns entries.
		_spawns.clear();
	}
	
	public static RaidBossManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RaidBossManager INSTANCE = new RaidBossManager();
	}
}