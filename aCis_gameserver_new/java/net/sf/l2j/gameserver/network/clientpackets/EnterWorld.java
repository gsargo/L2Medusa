package net.sf.l2j.gameserver.network.clientpackets;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Map.Entry;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.manager.MailBBSManager;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.manager.ClanHallManager;
import net.sf.l2j.gameserver.data.manager.CoupleManager;
import net.sf.l2j.gameserver.data.manager.DimensionalRiftManager;
import net.sf.l2j.gameserver.data.manager.PetitionManager;
import net.sf.l2j.gameserver.data.manager.SevenSignsManager;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.data.xml.AnnouncementData;
import net.sf.l2j.gameserver.data.xml.MapRegionData.TeleportType;
import net.sf.l2j.gameserver.enums.CabalType;
import net.sf.l2j.gameserver.enums.SealType;
import net.sf.l2j.gameserver.enums.SiegeSide;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.actors.ClassRace;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.clanhall.ClanHall;
import net.sf.l2j.gameserver.model.clanhall.SiegableHall;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.pledge.SubPledge;
import net.sf.l2j.gameserver.network.GameClient.GameClientState;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.Die;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ExMailArrived;
import net.sf.l2j.gameserver.network.serverpackets.ExStorageMaxCount;
import net.sf.l2j.gameserver.network.serverpackets.FriendList;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListAll;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.PledgeSkillList;
import net.sf.l2j.gameserver.network.serverpackets.QuestList;
import net.sf.l2j.gameserver.network.serverpackets.ShortCutInit;
import net.sf.l2j.gameserver.network.serverpackets.SkillCoolTime;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.skills.L2Skill;
import net.sf.l2j.gameserver.taskmanager.GameTimeTaskManager;

public class EnterWorld extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
		{
			getClient().closeNow();
			return;
		}
		
		// Set NewChar
		switch (player.getClassId().getId())
		{
			case 0:
			case 10:
			case 18:
			case 25:
			case 31:
			case 38:
			case 44:
			case 49:
			case 53:
				Player.doNewChar(player, 1);
				break;
		}
		
		getClient().setState(GameClientState.IN_GAME);
		
		final int objectId = player.getObjectId();
		

		
		
		if (player.isGM())
		{
			if (Config.GM_STARTUP_INVULNERABLE && AdminData.getInstance().hasAccess("admin_invul", player.getAccessLevel()))
				player.setInvul(true);
			
			if (Config.GM_STARTUP_INVISIBLE && AdminData.getInstance().hasAccess("admin_hide", player.getAccessLevel()))
				player.getAppearance().setVisible(false);
			
			if (Config.GM_STARTUP_BLOCK_ALL)
				player.getBlockList().setInBlockingAll(true);
			
			if (Config.GM_STARTUP_AUTO_LIST && AdminData.getInstance().hasAccess("admin_gmlist", player.getAccessLevel()))
				AdminData.getInstance().addGm(player, false);
			else
				AdminData.getInstance().addGm(player, true);
		}
		
		// Set dead status if applies
		if (player.getStatus().getHp() < 0.5 && player.isMortal())
			player.setIsDead(true);
		
		player.getMacroList().sendUpdate();
		player.sendPacket(new ExStorageMaxCount(player));
		player.sendPacket(new HennaInfo(player));
		player.updateEffectIcons();
		player.sendPacket(new EtcStatusUpdate(player));
		
		// Clan checks.
		final Clan clan = player.getClan();
		if (clan != null)
		{
			player.sendPacket(new PledgeSkillList(clan));
			
			// Refresh player instance.
			clan.getClanMember(objectId).setPlayerInstance(player);
			
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN).addCharName(player);
			final PledgeShowMemberListUpdate psmlu = new PledgeShowMemberListUpdate(player);
			
			// Send packets to others members.
			for (Player member : clan.getOnlineMembers())
			{
				if (member == player)
					continue;
				
				member.sendPacket(sm);
				member.sendPacket(psmlu);
			}
			
			// Send a login notification to sponsor or apprentice, if logged.
			if (player.getSponsor() != 0)
			{
				final Player sponsor = World.getInstance().getPlayer(player.getSponsor());
				if (sponsor != null)
					sponsor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN).addCharName(player));
			}
			else if (player.getApprentice() != 0)
			{
				final Player apprentice = World.getInstance().getPlayer(player.getApprentice());
				if (apprentice != null)
					apprentice.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_S1_HAS_LOGGED_IN).addCharName(player));
			}
			
			// Add message at connexion if clanHall not paid.
			final ClanHall ch = ClanHallManager.getInstance().getClanHallByOwner(clan);
			if (ch != null && !ch.getPaid())
				player.sendPacket(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
			
			for (Castle castle : CastleManager.getInstance().getCastles())
			{
				final Siege siege = castle.getSiege();
				if (!siege.isInProgress())
					continue;
				
				final SiegeSide type = siege.getSide(clan);
				if (type == SiegeSide.ATTACKER)
					player.setSiegeState((byte) 1);
				else if (type == SiegeSide.DEFENDER || type == SiegeSide.OWNER)
					player.setSiegeState((byte) 2);
			}
			
			for (SiegableHall hall : ClanHallManager.getInstance().getSiegableHalls())
			{
				if (hall.isInSiege() && hall.isRegistered(clan))
					player.setSiegeState((byte) 1);
			}
			
			player.sendPacket(new PledgeShowMemberListUpdate(player));
			player.sendPacket(new PledgeShowMemberListAll(clan, 0));
			
			for (SubPledge sp : clan.getAllSubPledges())
				player.sendPacket(new PledgeShowMemberListAll(clan, sp.getId()));
			
			player.sendPacket(new UserInfo(player));
		}
		
		// Updating Seal of Strife Buff/Debuff
		if (SevenSignsManager.getInstance().isSealValidationPeriod() && SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE) != CabalType.NORMAL)
		{
			CabalType cabal = SevenSignsManager.getInstance().getPlayerCabal(objectId);
			if (cabal != CabalType.NORMAL)
			{
				if (cabal == SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
					player.addSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill(), false);
				else
					player.addSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill(), false);
			}
		}
		else
		{
			player.removeSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill().getId(), false);
			player.removeSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill().getId(), false);
		}
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			player.setSpawnProtection(true);
		
		if (player.getMemos().getLong("newEndTime", 0) > 0)
			onEnterNewChar(player);
		
		player.spawnMe();
		
		// Set the location of debug packets.
		player.setEnterWorldLoc(player.getX(), player.getY(), -16000);
		
		// Engage and notify partner.
		if (Config.ALLOW_WEDDING)
		{
			for (Entry<Integer, IntIntHolder> coupleEntry : CoupleManager.getInstance().getCouples().entrySet())
			{
				final IntIntHolder couple = coupleEntry.getValue();
				if (couple.getId() == objectId || couple.getValue() == objectId)
				{
					player.setCoupleId(coupleEntry.getKey());
					break;
				}
			}
		}
		
		// Announcements, welcome & Seven signs period messages.
		player.sendPacket(SystemMessageId.WELCOME_TO_LINEAGE);
		player.sendPacket(SevenSignsManager.getInstance().getCurrentPeriod().getMessageId());
		AnnouncementData.getInstance().showAnnouncements(player, false);
		
		// If the Player is a Dark Elf, check for Shadow Sense at night.
		if (player.getRace() == ClassRace.DARK_ELF && player.hasSkill(L2Skill.SKILL_SHADOW_SENSE))
			player.sendPacket(SystemMessage.getSystemMessage((GameTimeTaskManager.getInstance().isNight()) ? SystemMessageId.NIGHT_S1_EFFECT_APPLIES : SystemMessageId.DAY_S1_EFFECT_DISAPPEARS).addSkillName(L2Skill.SKILL_SHADOW_SENSE));
		
		// Notify quest for enterworld event, if quest allows it.
		player.getQuestList().getQuests(Quest::isTriggeredOnEnterWorld).forEach(q -> q.notifyEnterWorld(player));
		
		player.sendPacket(new QuestList(player));
		player.sendSkillList();
		player.sendPacket(new FriendList(player));
		player.sendPacket(new UserInfo(player));
		player.sendPacket(new ItemList(player, false));
		player.sendPacket(new ShortCutInit(player));
		
		// No broadcast needed since the player will already spawn dead to others.
		if (player.isAlikeDead())
			player.sendPacket(new Die(player));
		
		// Unread mails make a popup appears.
		if (Config.ENABLE_COMMUNITY_BOARD && MailBBSManager.getInstance().checkIfUnreadMail(player))
		{
			player.sendPacket(SystemMessageId.NEW_MAIL);
			player.sendPacket(new PlaySound("systemmsg_e.1233"));
			player.sendPacket(ExMailArrived.STATIC_PACKET);
		}
		
		// Clan notice, if active.
		if (Config.ENABLE_COMMUNITY_BOARD && clan != null && clan.isNoticeEnabled())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/clan_notice.htm");
			html.replace("%clan_name%", clan.getName());
			html.replace("%notice_text%", clan.getNotice().replaceAll("\r\n", "<br>").replaceAll("action", "").replaceAll("bypass", ""));
			sendPacket(html);
		}
		else if (Config.SERVER_NEWS)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/servnews.htm");
			sendPacket(html);
		}
		
		PetitionManager.getInstance().checkActivePetition(player);
		
		player.onPlayerEnter();
		
		sendPacket(new SkillCoolTime(player));
		
		if (player.getVIPUntil() > System.currentTimeMillis())
		{
			long remainingTimeInMilliseconds = player.getVIPUntil() - System.currentTimeMillis();
			int remainingTimeInHours = (int) (remainingTimeInMilliseconds / (1000 * 60 * 60));
			String message = "Your Premium status will expire in " + remainingTimeInHours + " hours.";
			player.sendMessage(message);
			player.setVIP(true);
			player.broadcastUserInfo();
			ThreadPool.schedule(new Runnable()
			{
				@Override
				public void run()
				{
					if (player.isOnline() && player.isVIP())
					{
						player.setVIP(false);
						player.setVIPUntil(0);
						player.store();
						player.broadcastUserInfo();
						player.sendMessage("Your premium status has expired.");
					}
				}
			}, player.getVIPUntil() - System.currentTimeMillis());
		}
		else
		{
			player.setVIPUntil(0);
		}
		
		// If player logs back in a stadium, port him in nearest town.
		if (Olympiad.getInstance().playerInStadia(player))
			player.teleportTo(TeleportType.TOWN);
		
		if (DimensionalRiftManager.getInstance().checkIfInRiftZone(player.getX(), player.getY(), player.getZ(), false))
			DimensionalRiftManager.getInstance().teleportToWaitingRoom(player);
		
		if (player.getClanJoinExpiryTime() > System.currentTimeMillis())
			player.sendPacket(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED);
		
		// Attacker or spectator logging into a siege zone will be ported at town.
		if (player.isInsideZone(ZoneId.SIEGE) && player.getSiegeState() < 2)
			player.teleportTo(TeleportType.TOWN);
		
		// Announce Hero on Login (If player is a castleLord, do not announce him as a hero)
		if (player.isHero() && !player.isCastleLord(3) && !player.isCastleLord(5) && !player.isGM())
		{
			World.announceToOnlinePlayers("Hero " + player.getName() + " is now online!");
		}
		
		// Announce Castle Lord on Login
		if(player.getClan()!=null && player.isClanLeader() && player.getClan().hasCastle())
		{
			// Lord of Gludio
			if (player.isCastleLord(1))
			{
			World.announceToOnlinePlayers("Lord of Gludio, " + player.getName() + " is now online!");
			player.sendMessage("Welcome back Lord " + player.getName());
			}
			// Lord of Giran
			else if (player.isCastleLord(3))
			{
				World.announceToOnlinePlayers("Lord of Giran, " + player.getName() + " is now online!");
				player.sendMessage("Welcome back Lord " + player.getName());
			}
			
			// Lord of Aden
			else if (player.isCastleLord(5))
			{
				World.announceToOnlinePlayers("Lord of Aden, " + player.getName() + " is now online!");
				player.sendMessage("Welcome back Lord " + player.getName());
			}
		}
		
		
		// Triger the clan buff , if leader is online
		if (player.getClan() != null && player.getClan().getLeader().isOnline())
		{
			SkillTable.getInstance().getInfo(7095, 1).getEffects(player, player);
		}
		else
		{
			player.stopSkillEffects(7095);
		}
		
		/// Buff for clan members, owning a castle (castle buff)
		if (player.getClan() != null && player.getClan().hasCastle())
		{
			SkillTable.getInstance().getInfo(7096, 1).getEffects(player, player);
		}
		else
		{
			player.stopSkillEffects(7096);
		}
		
		//Inform about current server time
		player.sendMessage("Server Local Time: "+FormatDate(System.currentTimeMillis()));
		
		
		// If player has item X in inventory , set him hero
		/*
		 * if(player.getInventory().hasItems(12572) && !player.isHero()) { player.setHero(true); World.announceToOnlinePlayers("Hero " + player.getName()+" is now online"); }
		 */
		// Tutorial
		final QuestState qs = player.getQuestList().getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("UC", null, player);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private String FormatDate(long timeInMilis)
    {
        String lastPostDate = "-";

        if (timeInMilis!=0)
        {
            Calendar currentDate = Calendar.getInstance();
            Calendar topicDate = Calendar.getInstance();
            topicDate.setTimeInMillis(timeInMilis);

            if (currentDate.get(Calendar.YEAR)==topicDate.get(Calendar.YEAR) && 
                    currentDate.get(Calendar.DAY_OF_YEAR)==topicDate.get(Calendar.DAY_OF_YEAR))
            {
                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
                lastPostDate = format1.format(topicDate.getTime());
            }
            else
            {
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                lastPostDate = format1.format(topicDate.getTime());
            }
        }
        return lastPostDate;
    }
	
	private static void onEnterNewChar(Player activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getMemos().getLong("newEndTime");
		
		if (now > endDay)
			Player.removeNewChar(activeChar);
		else
		{
			activeChar.setNewChar(true);
			activeChar.broadcastUserInfo();
		}
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}