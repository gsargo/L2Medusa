package net.sf.l2j.gameserver.enums;

public enum SayType
{
	ALL,
	SHOUT, // !
	TELL, // "
	PARTY, // #
	CLAN, // @
	GM,
	PETITION_PLAYER,
	PETITION_GM,
	TRADE, // +
	ALLIANCE, // $
	ANNOUNCEMENT,
	BOAT,
	L2FRIEND,
	MSNCHAT,
	PARTYMATCH_ROOM,
	PARTYROOM_COMMANDER, // (Red)
	PARTYROOM_ALL, // (Yellow)
	HERO_VOICE,
	CRITICAL_ANNOUNCE;
	
	public static final SayType[] VALUES = values();
}