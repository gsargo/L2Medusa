package net.sf.l2j.gameserver.mods.antibot;

import net.sf.l2j.gameserver.enums.ZoneId;

/**
 * @author L2Medusa
 */
public class CaptchaZones
{
	ZoneId _zoneId = ZoneId.NO_LANDING;
	int _monstersKillCounter = 0;
	int _captchaTimer = 0;
	int _punishSelect = 0;
	int _punishParams = 0;
	
	public CaptchaZones(String id, int killCount, int time, int punishment, int punishParams)
	{
		this._monstersKillCounter = killCount;
		this._captchaTimer = time;
		this._zoneId = ZoneId.valueOf(id);
		this._punishParams = punishParams;
		this._punishSelect = punishment;
	}
}
