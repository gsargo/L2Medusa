package net.sf.l2j.gameserver.instance;

public interface GameInstance
{
	void start();
	
	void end();
	
	void processRewards();
	
	InstanceType getInstanceType();
}