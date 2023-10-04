package net.sf.l2j.gameserver.instance.group;

import java.util.Collection;

import net.sf.l2j.commons.logging.CLogger;

public interface CollectionAdapter<T>
{
	CLogger LOGGER = new CLogger(CollectionAdapter.class.getName());
	
	Collection<T> findAll();
	
	void add(T value);
	
	void addAll(Collection<T> values);
	
	T remove(int id);
	
	T findById(int id);
	
	void clear();
	
	boolean isEmpty();
	
	int getSize();
}