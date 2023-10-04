package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;

/**
* This class trades Gold Bars for Adena and vice versa.
* 
* @author Ahmed
*/
public class BankingSystemCommand implements IVoicedCommandHandler
{
private static String[] _voicedCommands =
{
		"bank", "withdraw", "deposit"
};


@Override
public boolean useVoicedCommand(String command, Player activeChar, String target)
{

	if(command.equalsIgnoreCase("bank"))
	{
		activeChar.sendMessage(".deposit (" + Config.BANKING_SYSTEM_ADENA + " Adena = " + Config.BANKING_SYSTEM_GOLDBARS + " Gold bar) / .withdraw (" + Config.BANKING_SYSTEM_GOLDBARS + " Gold bar = " + Config.BANKING_SYSTEM_ADENA + " Adena)");
	}
	else if(command.equalsIgnoreCase("deposit"))
	{
		if(activeChar.getInventory().getItemCount(57, 0) >= Config.BANKING_SYSTEM_ADENA)
		{
			activeChar.getInventory().reduceAdena("Goldbar", Config.BANKING_SYSTEM_ADENA, activeChar, null);
			activeChar.getInventory().addItem("Goldbar", 3470, Config.BANKING_SYSTEM_GOLDBARS, activeChar, null);
			activeChar.getInventory().updateDatabase();
			activeChar.sendPacket(new ItemList(activeChar, true));
			activeChar.sendMessage("You have turned " + Config.BANKING_SYSTEM_ADENA + " Adena to "  + Config.BANKING_SYSTEM_GOLDBARS + " Gold bar!");
		}
		else
		{
			activeChar.sendMessage("You do not have enough Adena to convert to a Gold bar, " + Config.BANKING_SYSTEM_ADENA + " Adena needed.");
		}
	}
	else if(command.equalsIgnoreCase("withdraw"))
	{
		if(activeChar.getInventory().getItemCount(3470, 0) >= Config.BANKING_SYSTEM_GOLDBARS)
		{
			activeChar.getInventory().destroyItemByItemId("Adena", 3470, Config.BANKING_SYSTEM_GOLDBARS, activeChar, null);
			activeChar.getInventory().addAdena("Adena", Config.BANKING_SYSTEM_ADENA, activeChar, null);
			activeChar.getInventory().updateDatabase();
			activeChar.sendPacket(new ItemList(activeChar, true));
			activeChar.sendMessage("You have turned " + Config.BANKING_SYSTEM_GOLDBARS +" Gold bar to " + Config.BANKING_SYSTEM_ADENA + " Adena! ");
		}
		else
		{
			activeChar.sendMessage("You do not have enough Gold bars to turn into Adena.");
		}
	}
	return true;
}
@Override
public String[] getVoicedCommandList()
{
	return _voicedCommands;
}
}