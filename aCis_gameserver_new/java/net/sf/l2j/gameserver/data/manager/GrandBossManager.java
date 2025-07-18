package net.sf.l2j.gameserver.data.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;

import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.model.actor.instance.GrandBoss;

/**
 * This class handles the status of all {@link GrandBoss}es.
 */
public class GrandBossManager
{
	private static final CLogger LOGGER = new CLogger(GrandBossManager.class.getName());
	
	private static final String SELECT_GRAND_BOSS_DATA = "SELECT * from grandboss_data ORDER BY boss_id";
	private static final String UPDATE_GRAND_BOSS_DATA = "UPDATE grandboss_data set loc_x = ?, loc_y = ?, loc_z = ?, heading = ?, respawn_time = ?, currentHP = ?, currentMP = ?, status = ? where boss_id = ?";
	private static final String UPDATE_GRAND_BOSS_DATA2 = "UPDATE grandboss_data set status = ? where boss_id = ?";
	
	private final Map<Integer, GrandBoss> _bosses = new HashMap<>();
	private final Map<Integer, StatSet> _sets = new HashMap<>();
	private final Map<Integer, Integer> _bossStatus = new HashMap<>();
	
	protected GrandBossManager()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_GRAND_BOSS_DATA);
			ResultSet rset = ps.executeQuery())
		{
			while (rset.next())
			{
				final int bossId = rset.getInt("boss_id");
				
				final StatSet set = new StatSet();
				set.set("loc_x", rset.getInt("loc_x"));
				set.set("loc_y", rset.getInt("loc_y"));
				set.set("loc_z", rset.getInt("loc_z"));
				set.set("heading", rset.getInt("heading"));
				set.set("respawn_time", rset.getLong("respawn_time"));
				set.set("currentHP", rset.getDouble("currentHP"));
				set.set("currentMP", rset.getDouble("currentMP"));
				
				_bossStatus.put(bossId, rset.getInt("status"));
				_sets.put(bossId, set);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load grandboss.", e);
		}
		LOGGER.info("Loaded {} GrandBosses instances.", _sets.size());
	}
	
	public int getBossStatus(int bossId)
	{
		return _bossStatus.get(bossId);
	}
	
	public Map<Integer, StatSet> getBosses()
	{
		return _sets;
	}
	
	public void setBossStatus(int bossId, int status)
	{
		_bossStatus.put(bossId, status);
		
		LOGGER.info("Updated {} (id: {}) status to {}.", NpcData.getInstance().getTemplate(bossId).getName(), bossId, status);
		
		updateDb(bossId, true);
	}
	
	/**
	 * Add a {@link GrandBoss} to the list of bosses.
	 * @param boss : The GrandBoss to add.
	 */
	public void addBoss(GrandBoss boss)
	{
		if (boss != null)
			_bosses.put(boss.getNpcId(), boss);
	}
	
	/**
	 * Add a {@link GrandBoss} to the list of bosses. Using this variant of addBoss, we can impose a npcId.
	 * @param npcId : The id to use for registration.
	 * @param boss : The GrandBoss to add.
	 */
	public void addBoss(int npcId, GrandBoss boss)
	{
		if (boss != null)
			_bosses.put(npcId, boss);
	}
	
	public GrandBoss getBoss(int bossId)
	{
		return _bosses.get(bossId);
	}
	
	public StatSet getStatSet(int bossId)
	{
		return _sets.get(bossId);
	}
	
	public void setStatSet(int bossId, StatSet info)
	{
		_sets.put(bossId, info);
		
		updateDb(bossId, false);
	}
	
	/**
	 * Save a single {@link GrandBoss} status, based on npcId.
	 * @param bossId : The id of the boss to save.
	 * @param statusOnly : If true, we only refresh its state.
	 */
	private void updateDb(int bossId, boolean statusOnly)
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			final StatSet set = _sets.get(bossId);
			final GrandBoss boss = _bosses.get(bossId);
			
			if (statusOnly || boss == null || set == null)
			{
				try (PreparedStatement ps = con.prepareStatement(UPDATE_GRAND_BOSS_DATA2))
				{
					ps.setInt(1, _bossStatus.get(bossId));
					ps.setInt(2, bossId);
					ps.executeUpdate();
				}
			}
			else
			{
				try (PreparedStatement ps = con.prepareStatement(UPDATE_GRAND_BOSS_DATA))
				{
					ps.setInt(1, boss.getX());
					ps.setInt(2, boss.getY());
					ps.setInt(3, boss.getZ());
					ps.setInt(4, boss.getHeading());
					ps.setLong(5, set.getLong("respawn_time"));
					ps.setDouble(6, (boss.isDead()) ? boss.getStatus().getMaxHp() : boss.getStatus().getHp());
					ps.setDouble(7, (boss.isDead()) ? boss.getStatus().getMaxMp() : boss.getStatus().getMp());
					ps.setInt(8, _bossStatus.get(bossId));
					ps.setInt(9, bossId);
					ps.executeUpdate();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't update grandbosses.", e);
		}
	}
	
	/**
	 * Saves all {@link GrandBoss}es info and then clears all info from memory, including all schedules.
	 */
	public void cleanUp()
	{
		// Store to database.
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps1 = con.prepareStatement(UPDATE_GRAND_BOSS_DATA2);
			PreparedStatement ps2 = con.prepareStatement(UPDATE_GRAND_BOSS_DATA))
		{
			for (Map.Entry<Integer, StatSet> entry : _sets.entrySet())
			{
				final int bossId = entry.getKey();
				final StatSet set = entry.getValue();
				final GrandBoss boss = _bosses.get(bossId);
				
				if (boss == null || set == null)
				{
					ps1.setInt(1, _bossStatus.get(bossId));
					ps1.setInt(2, bossId);
					ps1.addBatch();
				}
				else
				{
					ps2.setInt(1, boss.getX());
					ps2.setInt(2, boss.getY());
					ps2.setInt(3, boss.getZ());
					ps2.setInt(4, boss.getHeading());
					ps2.setLong(5, set.getLong("respawn_time"));
					ps2.setDouble(6, (boss.isDead()) ? boss.getStatus().getMaxHp() : boss.getStatus().getHp());
					ps2.setDouble(7, (boss.isDead()) ? boss.getStatus().getMaxMp() : boss.getStatus().getMp());
					ps2.setInt(8, _bossStatus.get(bossId));
					ps2.setInt(9, bossId);
					ps2.addBatch();
				}
			}
			ps1.executeBatch();
			ps2.executeBatch();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't store grandbosses.", e);
		}
		
		_bosses.clear();
		_sets.clear();
		_bossStatus.clear();
	}
	
	public static GrandBossManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GrandBossManager INSTANCE = new GrandBossManager();
	}
}