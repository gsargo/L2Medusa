package net.sf.l2j.gameserver.mods.antibot;

import net.sf.l2j.gameserver.model.location.Location;

/**
 * @author L2Medusa
 */
public class CaptchaBox
{
	boolean _unlocked = false;
	Location _boxLoc;
	int _identity = 0;
	
	public CaptchaBox(boolean unlock, Location loc, int num)
	{
		this._boxLoc = loc;
		this._unlocked = unlock;
		this._identity = num;
	}
}
