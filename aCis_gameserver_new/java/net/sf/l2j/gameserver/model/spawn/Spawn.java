package net.sf.l2j.gameserver.model.spawn;

import java.lang.reflect.Constructor;
import java.util.Optional;

import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.instance.AbstractInstance;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.location.SpawnLocation;

/**
 * This class manages the spawn and respawn of a {@link Npc}.
 */
public final class Spawn implements Runnable
{
	private static final CLogger LOGGER = new CLogger(Spawn.class.getName());
	
	private final SpawnLocation _loc = new SpawnLocation(0, 0, 0, 0);
	
	private NpcTemplate _template;
	
	private Constructor<?> _constructor;
	
	private Npc _npc;
	
	private int _respawnDelay;
	private int _respawnRandom;
	private boolean _respawnEnabled;
	
	private int _respawnMinDelay;
	private int _respawnMaxDelay;
	
	private volatile AbstractInstance gameInstance;
	private volatile int instanceId;
	
	public Spawn(NpcTemplate template) throws SecurityException, ClassNotFoundException, NoSuchMethodException
	{
		if (template == null)
			return;
		
		// Set the template of the Spawn.
		_template = template;
		
		// Create the generic constructor.
		Class<?>[] parameters =
		{
			int.class,
			Class.forName("net.sf.l2j.gameserver.model.actor.template.NpcTemplate")
		};
		_constructor = Class.forName("net.sf.l2j.gameserver.model.actor.instance." + _template.getType()).getConstructor(parameters);
	}
	
	public Spawn(int id) throws SecurityException, ClassNotFoundException, NoSuchMethodException
	{
		final NpcTemplate template = NpcData.getInstance().getTemplate(id);
		if (template == null)
		{
			LOGGER.warn("Couldn't properly spawn with id {} ; the template is missing.", id);
			return;
		}
		
		// Set the template of the Spawn.
		_template = template;
		
		// Create the generic constructor.
		Class<?>[] parameters =
		{
			int.class,
			Class.forName("net.sf.l2j.gameserver.model.actor.template.NpcTemplate")
		};
		_constructor = Class.forName("net.sf.l2j.gameserver.model.actor.instance." + _template.getType()).getConstructor(parameters);
	}
	
	/**
	 * @return the {@link NpcTemplate} associated to this {@link Spawn}.
	 */
	public NpcTemplate getTemplate()
	{
		return _template;
	}
	
	/**
	 * @return the npc id of the {@link NpcTemplate}.
	 */
	public int getNpcId()
	{
		return _template.getNpcId();
	}
	
	/**
	 * @return the {@link Npc} instance of this {@link Spawn}.
	 */
	public Npc getNpc()
	{
		return _npc;
	}
	
	/**
	 * Sets the {@link SpawnLocation} of this {@link Spawn}.
	 * @param loc : The SpawnLocation to set.
	 */
	public void setLoc(SpawnLocation loc)
	{
		_loc.set(loc);
	}
	
	/**
	 * Sets the {@link SpawnLocation} of this {@link Spawn} using separate coordinates.
	 * @param x : X coordinate.
	 * @param y : Y coordinate.
	 * @param z : Z coordinate.
	 * @param heading : Heading.
	 */
	public void setLoc(int x, int y, int z, int heading)
	{
		_loc.set(x, y, z, heading);
	}
	
	/**
	 * @return the {@link SpawnLocation} of this {@link Spawn}.
	 */
	public SpawnLocation getLoc()
	{
		return _loc;
	}
	
	/**
	 * @return the X coordinate of the {@link SpawnLocation}.
	 */
	public int getLocX()
	{
		return _loc.getX();
	}
	
	/**
	 * @return the Y coordinate of the {@link SpawnLocation}.
	 */
	public int getLocY()
	{
		return _loc.getY();
	}
	
	/**
	 * @return the Z coordinate of the {@link SpawnLocation}.
	 */
	public int getLocZ()
	{
		return _loc.getZ();
	}
	
	/**
	 * @return the heading coordinate of the {@link SpawnLocation}.
	 */
	public int getHeading()
	{
		return _loc.getHeading();
	}
	
	/**
	 * Set the respawn delay, representing the respawn time of this {@link Spawn}. It can't be inferior to 0, it is automatically modified to 1 second.
	 * @param delay : Respawn delay in seconds.
	 */
	public void setRespawnDelay(int delay)
	{
		_respawnDelay = Math.max(1, delay);
	}
	
	/**
	 * @return the respawn delay of this {@link Spawn} in seconds.
	 */
	public int getRespawnDelay()
	{
		return _respawnDelay;
	}
	
	/**
	 * Set the respawn random delay of this {@link Spawn}. It can't be inferior to respawn delay.
	 * @param random : Random respawn delay in seconds.
	 */
	public void setRespawnRandom(int random)
	{
		_respawnRandom = Math.min(_respawnDelay, random);
	}
	
	/**
	 * @return the respawn delay of this {@link Spawn} in seconds.
	 */
	public int getRespawnRandom()
	{
		return _respawnRandom;
	}
	
	/**
	 * Calculate the new respawn time, based on respawn delay +- random respawn delay.
	 * @return the respawn time of this {@link Spawn}, in seconds.
	 */
	public int calculateRespawnTime()
	{
		int respawnTime = _respawnDelay;
		
		if (_respawnRandom > 0)
			respawnTime += Rnd.get(-_respawnRandom, _respawnRandom);
		
		return respawnTime;
	}
	
	/**
	 * Enables or disable respawn state of this {@link Spawn}.
	 * @param state : if true, we allow the respawn.
	 */
	public void setRespawnState(boolean state)
	{
		_respawnEnabled = state;
	}
	
	/**
	 * @return the minimum RaidBoss spawn delay.
	 */
	public int getRespawnMinDelay()
	{
		return _respawnMinDelay;
	}
	
	/**
	 * Set the minimum respawn delay.
	 * @param date
	 */
	public void setRespawnMinDelay(int date)
	{
		_respawnMinDelay = date;
	}
	
	/**
	 * @return the maximum RaidBoss spawn delay.
	 */
	public int getRespawnMaxDelay()
	{
		return _respawnMaxDelay;
	}
	
	/**
	 * Set Maximum respawn delay.
	 * @param date
	 */
	public void setRespawnMaxDelay(int date)
	{
		_respawnMaxDelay = date;
	}
	
	public Npc doSpawn()
	{
		return doSpawn(false);
	}
	
	/**
	 * Create the {@link Npc}, add it to the world and launch its onSpawn() action.<BR>
	 * <BR>
	 * <B><U> Concept</U> :</B><BR>
	 * <BR>
	 * Npc can be spawned to already defined {@link SpawnLocation}. If not defined, Npc is not spawned.<BR>
	 * <BR>
	 * <B><U> Actions sequence for each spawn</U> : </B><BR>
	 * <ul>
	 * <li>Get Npc initialize parameters and generate its object ID</li>
	 * <li>Call the constructor of the Npc</li>
	 * <li>Link the Npc to this {@link Spawn}</li>
	 * <li>Make SpawnLocation check, when exists spawn process continues</li>
	 * <li>Reset Npc parameters - for re-spawning of existing Npc</li>
	 * <li>Calculate position using SpawnLocation and geodata</li>
	 * <li>Set the HP and MP of the Npc to the max</li>
	 * <li>Set the position and heading of the Npc (random heading is calculated, if not defined : value -1)</li>
	 * <li>Spawn Npc to the world</li>
	 * </ul>
	 * @param isSummonSpawn When true, summon magic circle will appear.
	 * @return the newly created instance.
	 */
	public Npc doSpawn(boolean isSummonSpawn)
	{
		try
		{
			// Check if the template isn't a Pet.
			if (_template.isType("Pet"))
				return null;
			
			// Get parameters and generate an id.
			Object[] parameters =
			{
				IdFactory.getInstance().getNextId(),
				_template
			};
			
			// Call the constructor.
			Object tmp = _constructor.newInstance(parameters);
			
			if (isSummonSpawn && tmp instanceof Creature)
				((Creature) tmp).setShowSummonAnimation(isSummonSpawn);
			
			// Check if the instance is a Npc.
			if (!(tmp instanceof Npc))
				return null;
			
			// Create final instance.
			_npc = (Npc) tmp;
			
			// Assign Spawn to Npc instance.
			_npc.setSpawn(this);
			
			// Initialize Npc and spawn it.
			initializeAndSpawn();
			
			return _npc;
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't spawn properly {}.", e, _template.getName());
			return null;
		}
	}
	
	/**
	 * Create a respawn task to be launched after the fixed + random delay. Respawn is only possible when respawn enabled.
	 */
	public void doRespawn()
	{
		// Check if respawn is possible to prevent multiple respawning caused by lag
		if (_respawnEnabled)
		{
			// Calculate the random time, if any.
			final int respawnTime = calculateRespawnTime() * 1000;
			
			// Schedule respawn of the NPC
			ThreadPool.schedule(this, respawnTime);
		}
	}
	
	/**
	 * Respawns the {@link Npc}.<br>
	 * <br>
	 * Refresh its id, and run {@link #initializeAndSpawn()}.
	 */
	@Override
	public void run()
	{
		if (_respawnEnabled)
		{
			_npc.refreshID();
			
			initializeAndSpawn();
		}
	}
	
	/**
	 * Initializes the {@link Npc} based on data of this {@link Spawn} and spawn it into the world.
	 */
	private void initializeAndSpawn()
	{
		// If location does not exist, there's a problem.
		if (_loc.equals(SpawnLocation.DUMMY_SPAWNLOC))
		{
			LOGGER.warn("{} misses location informations.", _template.getName());
			return;
		}
		
		// Check coordinates to be outside of world.
		final int locX = _loc.getX();
		final int locY = _loc.getY();
		
		if (World.isOutOfWorld(locX, locY))
		{
			LOGGER.error("{} spawn coords are outside of world.", _template.getName());
			return;
		}
		
		// reset effects and status
		_npc.stopAllEffects();
		_npc.setIsDead(false);
		
		// reset decay info
		_npc.setDecayed(false);
		
		// reset script value
		_npc.setScriptValue(0);
		
		// The Npc is spawned at the exact position (Lox, Locy, Locz)
		int locZ = GeoEngine.getInstance().getHeight(locX, locY, _loc.getZ());
		
		// FIXME temporarily fix: when the spawn Z and geo Z differs more than 200, use spawn Z coord
		if (Math.abs(locZ - _loc.getZ()) > 200)
			locZ = _loc.getZ();
		
		// Set the HP and MP of the Npc to the max
		_npc.getStatus().setMaxHpMp();
		
		// spawn NPC on new coordinates
		_npc.spawnMe(locX, locY, locZ, (_loc.getHeading() < 0) ? Rnd.get(65536) : _loc.getHeading());
	}
	
	@Override
	public String toString()
	{
		return "Spawn [id=" + _template.getNpcId() + ", loc=" + _loc.toString() + "]";
	}
	
	@Override
	public int hashCode()
	{
		return _loc.hashCode();
	}
	
	public void deleteMe()
	{
		setRespawnState(false);
		Optional.ofNullable(_npc).ifPresent(Npc::deleteMe);
		_npc = null;
	}
	
	public void setGameInstance(AbstractInstance gameInstance)
	{
		this.gameInstance = gameInstance;
	}
	
	public final AbstractInstance getGameInstance()
	{
		return gameInstance;
	}
	
	public final int getInstanceId()
	{
		return instanceId;
	}
	
	public void setInstanceId(int instanceId)
	{
		this.instanceId = instanceId;
	}
	
}