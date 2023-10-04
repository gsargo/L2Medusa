package net.sf.l2j.gameserver.instance.group;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractGroup<T> implements CollectionAdapter<T>
{
	private final Map<Integer, T> entries = new ConcurrentHashMap<>();
	
	@Override
	public Collection<T> findAll()
	{
		return entries.values();
	}
	
	@Override
	public void add(T value)
	{
		entries.put(value.hashCode(), value);
	}
	
	@Override
	public void addAll(Collection<T> values)
	{
		values.forEach(this::add);
	}
	
	@Override
	public T remove(int id)
	{
		return entries.remove(id);
	}
	
	@Override
	public T findById(int id)
	{
		return entries.get(id);
	}
	
	@Override
	public void clear()
	{
		entries.clear();
	}
	
	@Override
	public boolean isEmpty()
	{
		return entries.isEmpty();
	}
	
	@Override
	public int getSize()
	{
		return entries.size();
	}
}