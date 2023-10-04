package net.sf.l2j.gameserver.skills;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.l2j.gameserver.model.actor.Creature;

public class RestoreEffects
{
    private final Map<Integer, AbstractEffect> _effects = new ConcurrentHashMap<>();
    private Creature _player;
    private final AtomicInteger _intcounter = new AtomicInteger();
    
    public RestoreEffects(Creature player)
    {
        _player = player;
    }
    
    public void startSavingEffects()
    {
        for (AbstractEffect effect : _player.getAllEffects())
        {
            final int counter = _intcounter.incrementAndGet();
            if (effect.getSkill().isToggle())
                _effects.put(counter, effect);
        }
    }
    
    public void restoreEffects()
    {
        for (AbstractEffect effect : _effects.values())
            _player.getCast().doToggleCast(effect.getSkill(), _player);
    }
    
    public int effectsSize()
    {
        return _effects.size();
    }
    
    public Collection<AbstractEffect> getModEffects()
    {
        return _effects.values();
    }
}