package net.sf.l2j.gameserver.network.serverpackets;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.manager.ClanHallManager;
import net.sf.l2j.gameserver.enums.SiegeSide;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.ClanHallSiege;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.commons.lang.StringUtil;

public class Die extends L2GameServerPacket
{
	private final Creature _creature;
	private final int _objectId;
	private final boolean _fake;
	
	private boolean _sweepable;
	private boolean _allowFixedRes;
	private Clan _clan;
	
	private boolean _canTeleport ;
	final NpcHtmlMessage html_3 = new NpcHtmlMessage(0);
	
	public Die(Creature creature)
	{
		_creature = creature;
		_objectId = creature.getObjectId();
		_fake = !creature.isDead();
		
		if (creature instanceof Player)
		{
			
			_canTeleport = !creature.getActingPlayer().isInTournamentMatch() && !creature.getActingPlayer().isInsidePvPZone() && creature.getActingPlayer().getDungeon()==null;
			Player player = (Player) creature;
			_allowFixedRes = player.getAccessLevel().allowFixedRes();
			_clan = player.getClan();
			
			if(player.isInsideSharkZone())
			{
				html_3.setFile("data/html/ondie/pvparea_no_killer.htm");
				player.sendPacket(html_3);
			}
			
		}
		else if (creature instanceof Monster)
			_sweepable = ((Monster) creature).getSpoilState().isSweepable();
		
		
	}
		
	@Override
	protected final void writeImpl()
	{
		if (_fake)
			return;
			
		writeC(0x06);	
		writeD(_objectId);
		writeD(_canTeleport ? 0x01 : 0x00); // to nearest village
		// writeD(0x01); // to nearest village
		
		if(_creature instanceof Player && (_creature.isInsidePvPZone() || _creature.getActingPlayer().getDungeon()!=null))
			return;
		
		if (_clan != null)
		{
			final Siege siege = CastleManager.getInstance().getActiveSiege(_creature);
			final ClanHallSiege chs = ClanHallManager.getInstance().getActiveSiege(_creature);
			
			// Check first if an active Siege is under process.
			if (siege != null)
			{
				final SiegeSide side = siege.getSide(_clan);
				
				writeD((_clan.hasClanHall()) ? 0x01 : 0x00); // to clanhall
				writeD((_clan.hasCastle() || side == SiegeSide.OWNER || side == SiegeSide.DEFENDER) ? 0x01 : 0x00); // to castle
				writeD((side == SiegeSide.ATTACKER && _clan.getFlag() != null) ? 0x01 : 0x00); // to siege HQ
			}
			// If no Siege, check ClanHallSiege.
			else if (chs != null)
			{
				writeD((_clan.hasClanHall()) ? 0x01 : 0x00); // to clanhall
				writeD((_clan.hasCastle()) ? 0x01 : 0x00); // to castle
				writeD((chs.checkSide(_clan, SiegeSide.ATTACKER) && _clan.getFlag() != null) ? 0x01 : 0x00); // to siege HQ
			}
			// We're in peace mode, activate generic teleports.
			else
			{
				writeD((_clan.hasClanHall()) ? 0x01 : 0x00); // to clanhall
				writeD((_clan.hasCastle()) ? 0x01 : 0x00); // to castle
				writeD(0x00); // to siege HQ
			}
		}
		else
		{
			
			writeD(0x00); // to clanhall
			writeD(0x00); // to castle
			writeD(0x00); // to siege HQ
		}
		
		writeD((_sweepable) ? 0x01 : 0x00); // sweepable (blue glow)
		writeD((_allowFixedRes) ? 0x01 : 0x00); // Retail FIXED
		

	}
}