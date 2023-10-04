package net.sf.l2j.gameserver.factory;

public interface Factory<K, V>
{
	V createNewInstance(K key, Object... objects);
}