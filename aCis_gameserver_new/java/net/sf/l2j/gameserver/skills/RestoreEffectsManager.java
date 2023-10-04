package net.sf.l2j.gameserver.skills;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.gameserver.model.actor.Playable;

public class RestoreEffectsManager
{
	private final Map<Integer, RestoreEffects> _effects = new ConcurrentHashMap<>();
    
    public void savePlayerEffects(Playable player)
    {
        if (player.getAllEffects() == null)
            return;
        
        final RestoreEffects modEffects = new RestoreEffects(player);
        modEffects.startSavingEffects();
        _effects.put(player.getObjectId(), modEffects);
    }
    
    public void restorePlayerEffects(Playable player)
    {
        final RestoreEffects rEffects = getPlayerEffects(player.getObjectId());
        if (rEffects == null)
            return;
        
        rEffects.restoreEffects();
    }
    
    public RestoreEffects getPlayerEffects(int plrObjId)
    {
        return _effects.get(plrObjId);
    }
    
    public static final RestoreEffectsManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
    
    private static class SingletonHolder
    {
        protected static final RestoreEffectsManager INSTANCE = new RestoreEffectsManager();
    }
}