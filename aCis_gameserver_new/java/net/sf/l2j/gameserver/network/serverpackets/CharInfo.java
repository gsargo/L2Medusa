package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.manager.CursedWeaponManager;
import net.sf.l2j.gameserver.enums.Paperdoll;
import net.sf.l2j.gameserver.enums.TeamType;
import net.sf.l2j.gameserver.enums.skills.AbnormalEffect;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.Summon;
import net.sf.l2j.gameserver.model.actor.instance.Cubic;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;

public class CharInfo extends L2GameServerPacket
{
	private final Player _player;
	
	public CharInfo(Player player)
	{
		_player = player;
	}
	
	@Override
	protected final void writeImpl()
	{
		boolean canSeeInvis = false;
		
		if (!_player.getAppearance().isVisible())
		{
			final Player tmp = getClient().getPlayer();
			if (tmp != null && tmp.isGM())
				canSeeInvis = true;
		}
		
		writeC(0x03);
		writeD(_player.getX());
		writeD(_player.getY());
		writeD(_player.getZ());
		writeD((_player.getBoat() == null) ? 0 : _player.getBoat().getObjectId());
		writeD(_player.getObjectId());
		//writeS(_player.getName());
		if(_player.isInsidePvPZone() && !_player.isInsideClanwarZone())
			{
			if(_player.isVIP())
				writeS("Incognito*");
			
			else
				writeS("Incognito");
			}
		else
			{
			if(_player.isVIP())
				writeS(_player.getName()+"*");
			else
				writeS(_player.getName());
			}
		//writeS((_player.isInsidePvPZone() && !_player.isInsideClanwarZone()) ? "Incognito" :  _player.getName());
		writeD(_player.getRace().ordinal());
		writeD(_player.getAppearance().getSex().ordinal());
		writeD((_player.getClassIndex() == 0) ? _player.getClassId().getId() : _player.getBaseClass());
		
		
		Inventory _inv = _player.getInventory();
		writeD(_inv.getItemIdFrom(Paperdoll.HAIRALL));
		writeD(_inv.getItemIdFrom(Paperdoll.HEAD));
		writeD(_inv.getItemIdFrom(Paperdoll.RHAND));
		writeD(_inv.getItemIdFrom(Paperdoll.LHAND));
		
		writeD(_player.getTempGloves() != 0 ? _player.getTempGloves() : _inv.getItemIdFrom(Paperdoll.GLOVES));
		writeD(_player.getTempChest() != 0 ? _player.getTempChest() : _inv.getItemIdFrom(Paperdoll.CHEST));
		writeD(_player.getTempLegs() != 0 ? _player.getTempLegs() : _inv.getItemIdFrom(Paperdoll.LEGS));
		writeD(_player.getTempFeet() != 0 ? _player.getTempFeet() : _inv.getItemIdFrom(Paperdoll.FEET));
		writeD(_inv.getItemIdFrom(Paperdoll.CLOAK));
		writeD(_inv.getItemIdFrom(Paperdoll.RHAND));
			if(_player.isInsidePvPZone() && !_player.isInsideClanwarZone())
				writeD(9946);
			else
				writeD(_player.getTempHair() != 0 ? _player.getTempHair() : _inv.getItemIdFrom(Paperdoll.HAIR));
		
		writeD(_player.getTempFace() != 0 ? _player.getTempFace() : _inv.getItemIdFrom(Paperdoll.FACE));
		
		// todo delete
		/*
		 * if (_player.getStatus().getHp() >= (_player.getStatus().getMaxHp() * 70 / 100f)) { writeD(_player.getTempGloves() != 0 ? _player.getTempGloves() : _inv.getItemIdFrom(Paperdoll.GLOVES)); writeD(_player.getTempChest() != 0 ? _player.getTempChest() : _inv.getItemIdFrom(Paperdoll.CHEST));
		 * writeD(_player.getTempLegs() != 0 ? _player.getTempLegs() : _inv.getItemIdFrom(Paperdoll.LEGS)); writeD(_player.getTempFeet() != 0 ? _player.getTempFeet() : _inv.getItemIdFrom(Paperdoll.FEET)); writeD(_inv.getItemIdFrom(Paperdoll.CLOAK)); writeD(_inv.getItemIdFrom(Paperdoll.RHAND));
		 * writeD(_player.getTempHair() != 0 ? _player.getTempHair() : _inv.getItemIdFrom(Paperdoll.HAIR)); writeD(_player.getTempFace() != 0 ? _player.getTempFace() : _inv.getItemIdFrom(Paperdoll.FACE)); } else { writeD(_inv.getItemIdFrom(Paperdoll.GLOVES));
		 * writeD(_inv.getItemIdFrom(Paperdoll.CHEST)); writeD(_inv.getItemIdFrom(Paperdoll.LEGS)); writeD(_inv.getItemIdFrom(Paperdoll.FEET)); writeD(_inv.getItemIdFrom(Paperdoll.CLOAK)); writeD(_inv.getItemIdFrom(Paperdoll.RHAND)); writeD(_inv.getItemIdFrom(Paperdoll.HAIR));
		 * writeD(_inv.getItemIdFrom(Paperdoll.FACE)); }
		 */
		_inv = null;
		
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_player.getInventory().getAugmentationIdFrom(Paperdoll.RHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_player.getInventory().getAugmentationIdFrom(Paperdoll.LHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		
		writeD(_player.getPvpFlag());
		writeD(_player.getKarma());
		writeD(_player.getStatus().getMAtkSpd());
		writeD(_player.getStatus().getPAtkSpd());
		writeD(_player.getPvpFlag());
		writeD(_player.getKarma());
		
		final int runSpd = _player.getStatus().getBaseRunSpeed();
		final int walkSpd = _player.getStatus().getBaseWalkSpeed();
		final int swimSpd = _player.getStatus().getBaseSwimSpeed();
		
		writeD(runSpd);
		writeD(walkSpd);
		writeD(swimSpd);
		writeD(swimSpd);
		writeD(runSpd);
		writeD(walkSpd);
		writeD((_player.isFlying()) ? runSpd : 0);
		writeD((_player.isFlying()) ? walkSpd : 0);
		
		writeF(_player.getStatus().getMovementSpeedMultiplier());
		writeF(_player.getStatus().getAttackSpeedMultiplier());
		
		final Summon summon = _player.getSummon();
		if (_player.isMounted() && summon != null)
		{
			writeF(summon.getCollisionRadius());
			writeF(summon.getCollisionHeight());
		}
		else
		{
			writeF(_player.getCollisionRadius());
			writeF(_player.getCollisionHeight());
		}
		
		writeD(_player.getAppearance().getHairStyle());
		writeD(_player.getAppearance().getHairColor());
		writeD(_player.getAppearance().getFace());
		
		if(_player.isGM())
			writeS((canSeeInvis) ? "Invisible" : _player.getTitle());
		else
			writeS((_player.isInsidePvPZone() && !_player.isInsideClanwarZone()) ? "Rank: " +_player.get_rank_by_text() : _player.getTitle());
		
		writeD((_player.isInsidePvPZone() && !_player.isInsideClanwarZone()) ? 0 :_player.getClanId());
		//writeD(_player.getClanCrestId());
		writeD((_player.isInsidePvPZone() && !_player.isInsideClanwarZone()) ? 0 : _player.getClanCrestId());
		
		//writeD(_player.getAllyId());
		writeD((_player.isInsidePvPZone() && !_player.isInsideClanwarZone()) ? 0 : _player.getAllyId());
		writeD((_player.isInsidePvPZone() && !_player.isInsideClanwarZone()) ? 0 :_player.getAllyCrestId());
		
		writeD(0);
		
		writeC((_player.isSitting()) ? 0 : 1);
		writeC((_player.isRunning()) ? 1 : 0);
		writeC((_player.isInCombat()) ? 1 : 0);
		writeC((_player.isAlikeDead()) ? 1 : 0);
		writeC((!canSeeInvis && !_player.getAppearance().isVisible()) ? 1 : 0);
		
		writeC(_player.getMountType());
		writeC(_player.getOperateType().getId());
		
		writeH(_player.getCubicList().size());
		for (final Cubic cubic : _player.getCubicList())
			writeH(cubic.getId());
		
		writeC((_player.isInPartyMatchRoom()) ? 1 : 0);
		writeD((canSeeInvis) ? (_player.getAbnormalEffect() | AbnormalEffect.STEALTH.getMask()) : _player.getAbnormalEffect());
		writeC(_player.getRecomLeft());
		writeH(_player.getRecomHave());
		writeD(_player.getClassId().getId());
		writeD(_player.getStatus().getMaxCp());
		writeD((int) _player.getStatus().getCp());
		writeC((_player.isMounted()) ? 0 : _player.getEnchantEffect());
		writeC((Config.PLAYER_SPAWN_PROTECTION > 0 && _player.isSpawnProtected()) ? TeamType.BLUE.getId() : _player.getTeam().getId());
		writeD((_player.isInsidePvPZone() && !_player.isInsideClanwarZone()) ? 0 :_player.getClanCrestLargeId());
		writeC((_player.isNoble()) ? 1 : 0);
		writeC((_player.isHero() || (_player.isGM() && Config.GM_HERO_AURA)) ? 1 : 0);
		writeC((_player.isFishing()) ? 1 : 0);
		writeLoc(_player.getFishingStance().getLoc());
		writeD(_player.getAppearance().getNameColor());
		writeD(_player.getHeading());
		writeD((_player.isInsidePvPZone() && !_player.isInsideClanwarZone()) ? 0 :_player.getPledgeClass());
		writeD((_player.isInsidePvPZone() && !_player.isInsideClanwarZone()) ? 0 :_player.getPledgeType());
		writeD(_player.getAppearance().getTitleColor());
		writeD(CursedWeaponManager.getInstance().getCurrentStage(_player.getCursedWeaponEquippedId()));
		writeC((_player.isVIP()) ? 1 : 0);
	}
}