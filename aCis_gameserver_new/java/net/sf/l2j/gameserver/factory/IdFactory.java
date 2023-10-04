package net.sf.l2j.gameserver.factory;

public interface IdFactory<T>
{
	T createNextId();
}