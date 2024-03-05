package net.sf.l2j.gameserver.network.clientpackets;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.items.CrystalType;
import net.sf.l2j.gameserver.enums.items.ItemLocation;
import net.sf.l2j.gameserver.enums.items.WeaponType;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;

public abstract class AbstractEnchantPacket extends L2GameClientPacket
{
	public static final Map<Integer, EnchantScroll> _scrolls = new HashMap<>();
	
	public static final class EnchantScroll
	{
		protected final boolean _isWeapon;
		protected final CrystalType _grade;
		private final boolean _isBlessed;
		private final boolean _isBlessed75;
		private final boolean _isCrystal;
		private final boolean _isImprovedNormal70;
		private final boolean _isImprovedNormal75;
		private final boolean _isImprovedNormal80;
		private final boolean _isImprovedBlessed;
		
		public EnchantScroll(boolean wep, boolean bless, boolean bless75, boolean improvednormal70, boolean improvednormal75, boolean improvednormal80, boolean improvedbless, boolean crystal, CrystalType type)
		{
			_isWeapon = wep;
			_grade = type;
			_isBlessed = bless;
			_isBlessed75 = bless75;
			_isCrystal = crystal;
			_isImprovedNormal70 =  improvednormal70;
			_isImprovedNormal75 =  improvednormal75;
			_isImprovedNormal80 =  improvednormal80;
			_isImprovedBlessed =  improvedbless;
		}
		
		/**
		 * @param enchantItem : The item to enchant.
		 * @return true if support item can be used for this item
		 */
		public final boolean isValid(ItemInstance enchantItem)
		{
			if (enchantItem == null)
				return false;
			
			// checking scroll type and configured maximum enchant level
			switch (enchantItem.getItem().getType2())
			{
				case Item.TYPE2_WEAPON:
					if (!_isWeapon || (Config.ENCHANT_MAX_WEAPON > 0 && enchantItem.getEnchantLevel() >= Config.ENCHANT_MAX_WEAPON))
						return false;
					break;
				
				case Item.TYPE2_SHIELD_ARMOR:
				case Item.TYPE2_ACCESSORY:
					if (_isWeapon || (Config.ENCHANT_MAX_ARMOR > 0 && enchantItem.getEnchantLevel() >= Config.ENCHANT_MAX_ARMOR))
						return false;
					break;
				
				default:
					return false;
			}
			
			// check for crystal type
			if (_grade != enchantItem.getItem().getCrystalType())
				return false;
			
			return true;
		}
		
		/**
		 * @return true if item is a blessed scroll.
		 */
		public final boolean isBlessed()
		{
			return _isBlessed;
		}
		
		public final boolean isBlessed75()
		{
			return _isBlessed75;
		}
		
		public final boolean isImprovedBlessed()
		{
			return _isImprovedBlessed;
		}
		
		public final boolean isNormal70()
		{
			return _isImprovedNormal70;
		}
		
		public final boolean isNormal75()
		{
			return _isImprovedNormal75;
		}
		
		public final boolean isNormal80()
		{
			return _isImprovedNormal80;
		}
		
		/**
		 * @return true if item is a crystal scroll.
		 */
		public final boolean isCrystal()
		{
			return _isCrystal;
		}
		
		public final String ScrollType()
		{
			if(isBlessed())
				return "Blessed Scroll";
			
			else if(isCrystal())
				return "Crystal Scroll";
			
			else
				return "Normal Scroll";
		}
		/**
		 * Regarding enchant system :<br>
		 * <br>
		 * <u>Weapons</u>
		 * <ul>
		 * <li>magic weapons has chance of 40% until +15 and 20% from +15 and higher. There is no upper limit, there is no dependance on current enchant level.</li>
		 * <li>non magic weapons has chance of 70% until +15 and 35% from +15 and higher. There is no upper limit, there is no dependance on current enchant level.</li>
		 * </ul>
		 * <u>Armors</u>
		 * <ul>
		 * <li>non fullbody armors (jewelry, upper armor, lower armor, boots, gloves, helmets and shirts) has chance of 2/3 for +4, 1/3 for +5, 1/4 for +6, ...., 1/18 +20. If you've made a +20 armor, chance to make it +21 will be equal to zero (0%).</li>
		 * <li>full body armors has a chance of 1/1 for +4, 2/3 for +5, 1/3 for +6, ..., 1/17 for +20. If you've made a +20 armor, chance to make it +21 will be equal to zero (0%).</li>
		 * </ul>
		 * @param enchantItem : The item to enchant.
		 * @return the enchant chance under double format (0.7 / 0.35 / 0.44324...).
		 */
		public final double getChance(ItemInstance enchantItem)
		{
			if (!isValid(enchantItem))
				return -1;
			
			boolean fullBody = enchantItem.getItem().getBodyPart() == Item.SLOT_FULL_ARMOR;
			if (enchantItem.getEnchantLevel() < Config.ENCHANT_SAFE_MAX || (fullBody && enchantItem.getEnchantLevel() < Config.ENCHANT_SAFE_MAX_FULL))
				return 1;
			
			double chance = 0;
			
			// Armor formula : 0.66^(current-2), chance is lower and lower for each enchant.
			if (enchantItem.isArmor()) //Chances for Armor Enchant
			{
				if (isBlessed()) // custom , calculate chance for blessed.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_BLESSED_ARMOR;
							break;
						}
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_BLESSED_ARMOR - 0.02;
							break;
						}
					}	
				}
				
				else if ( isBlessed75()) // custom , calculate chance for blessed 75%.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_BLESSED75_ARMOR;
							break;
						}
						
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_BLESSED75_ARMOR - 0.02;
							break;
						}
					}	
				}
				
				else if ( isImprovedBlessed()) // custom , calculate chance for improved blessed%.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_IMPROVEDBLESSED_ARMOR;
							break;
						}
					}	
				}
				
				
				else if (isNormal70()) // custom , calculate chance for normal 70%.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL70_ARMOR;
							break;
						}
						
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL70_ARMOR - 0.02;
							break;
						}
					}	
				}
				
				
				else if (isNormal75()) // custom , calculate chance for normal 75%.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL75_ARMOR;
							break;
						}
						
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL75_ARMOR - 0.02;
							break;
						}
					}	
				}
				
				
				else if (isNormal80()) // custom , calculate chance for normal 80%.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL80_ARMOR;
							break;
						}
						
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL80_ARMOR - 0.02;
							break;
						}
					}	
				}
				
				else if ( isCrystal()) // custom , calculate chance for crystal.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19:
	
						{
							chance = Config.ENCHANT_CHANCE_WEAPON_CRYSTAL;
							break;
						}
						
					}
					
				}
				
				
				else // calculate chance for normal
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL_ARMOR;
							break;
						}
						
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL_ARMOR - 0.02;
							break;
						}
					}
			
				}
			}
			
			else // isWeapon
			{
				if (isBlessed()) // custom , calculate chance for blessed.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_BLESSED_WEAPON;
							break;
						}
						
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_BLESSED_WEAPON - 0.02;
							break;
						}
					}	
				}
				
				else if ( isBlessed75()) // custom , calculate chance for blessed 75%.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_BLESSED75_WEAPON;
							
							break;
						}
						
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_BLESSED75_WEAPON - 0.02;
							
							break;
						}
					}	
				}
				
				else if ( isImprovedBlessed()) // custom , calculate chance for improved blessed%.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_IMPROVEDBLESSED_WEAPON;
							break;
						}
					}	
				}
				
				
				else if (isNormal70()) // custom , calculate chance for normal 70%.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL70_WEAPON;
							break;
						}
						
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL70_WEAPON - 0.02;
							break;
						}
					}	
				}
				
				
				else if (isNormal75()) // custom , calculate chance for normal 75%.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL75_WEAPON;
							break;
						}
						
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL75_WEAPON - 0.02;
							break;
						}
					}	
				}
				
				
				else if (isNormal80()) // custom , calculate chance for normal 80%.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL80_WEAPON;
							break;
						}
						
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL80_WEAPON - 0.02;
							break;
						}
					}	
				}
				
				else if ( isCrystal()) // custom , calculate chance for crystal.
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19:
	
						{
							chance = Config.ENCHANT_CHANCE_WEAPON_CRYSTAL;
							break;
						}
						
					}
					
				}
				
				
				else // calculate chance for normal
				{
					switch(enchantItem.getEnchantLevel())
					{
						case 3,4,5,6,7,8,9,10,11,12,13,14:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL_WEAPON;
							break;
						}
						
						case 15,16,17,18,19:
						{
							chance = Config.ENCHANT_CHANCE_NORMAL_WEAPON - 0.02;
							break;
						}
					}
			
				}
		}

			
		System.out.println("Chance is "+chance);
			
			return chance;
		}
	}
	
	/**
	 * Format : itemId, (isWeapon, isBlessed, isCrystal, grade)<br>
	 * Allowed items IDs must be sorted by ascending order.
	 */
	static
	{
		// Scrolls: Enchant Weapon
		_scrolls.put(729, new EnchantScroll(true, false, false, false, false, false, false, false, CrystalType.A));
		_scrolls.put(947, new EnchantScroll(true, false, false, false, false, false, false, false, CrystalType.B));
		_scrolls.put(951, new EnchantScroll(true, false, false, false, false, false, false, false, CrystalType.C));
		_scrolls.put(955, new EnchantScroll(true, false, false, false, false, false, false, false, CrystalType.D));
		_scrolls.put(959, new EnchantScroll(true, false, false, false, false, false, false, false, CrystalType.S));
		
		
		
		// Scrolls: Enchant Armor
		_scrolls.put(730, new EnchantScroll(false, false, false, false, false, false, false, false, CrystalType.A));
		_scrolls.put(948, new EnchantScroll(false, false, false, false, false, false, false, false, CrystalType.B));
		_scrolls.put(952, new EnchantScroll(false, false, false, false, false, false, false, false, CrystalType.C));
		_scrolls.put(956, new EnchantScroll(false, false, false, false, false, false, false, false, CrystalType.D));
		_scrolls.put(960, new EnchantScroll(false, false, false, false, false, false, false, false, CrystalType.S));
		
		//Custom Normal Scroll 70% : Enchant Weapon
		_scrolls.put(12603, new EnchantScroll(true, false, false, true, false, false, false, false, CrystalType.A)); //Custom 70% Scroll
		_scrolls.put(12593, new EnchantScroll(true, false, false, true, false, false, false, false, CrystalType.S)); //Custom 70% Scroll
		
		
		//Custom Normal Scroll 70%: Enchant Armor
		_scrolls.put(12604, new EnchantScroll(false, false, false, true ,false, false ,false, false, CrystalType.A)); //Custom 70% Scroll
		_scrolls.put(12594, new EnchantScroll(false, false, false, true ,false, false ,false, false, CrystalType.S)); //Custom 70% Scroll
		
		
		//Custom Normal Scroll 75% : Enchant Weapon
		_scrolls.put(12605, new EnchantScroll(true, false, false, false, true, false, false, false, CrystalType.A)); //Custom 75% Scroll
		_scrolls.put(12597, new EnchantScroll(true, false, false, false, true, false, false, false, CrystalType.S)); //Custom 75% Scroll
		
		//Custom Normal Scrolls 75%: Enchant Armor
		_scrolls.put(12606, new EnchantScroll(false, false, false, false ,true, false ,false, false, CrystalType.A)); //Custom 75% Scroll
		_scrolls.put(12598, new EnchantScroll(false, false, false, false ,true, false ,false, false, CrystalType.S)); //Custom 75% Scroll
		
		//Custom Normal Scroll 80% : Enchant Weapon
		_scrolls.put(12607, new EnchantScroll(true, false, false, false, false, true, false, false, CrystalType.A)); //Custom 80% Scroll
		_scrolls.put(12599, new EnchantScroll(true, false, false, false, false, true, false, false, CrystalType.S)); //Custom 80% Scroll
		
		//Custom Normal Scrolls 80%: Enchant Armor
		_scrolls.put(12608, new EnchantScroll(false, false, false, false ,false, true ,false, false, CrystalType.A)); //Custom 80% Scroll
		_scrolls.put(12600, new EnchantScroll(false, false, false, false ,false, true ,false, false, CrystalType.S)); //Custom 80% Scroll
		
		// Blessed Scrolls: Enchant Weapon
		_scrolls.put(6569, new EnchantScroll(true, true, false, false, false, false, false, false, CrystalType.A));
		_scrolls.put(6571, new EnchantScroll(true, true, false, false, false, false, false, false, CrystalType.B));
		_scrolls.put(6573, new EnchantScroll(true, true, false, false, false, false, false, false, CrystalType.C));
		_scrolls.put(6575, new EnchantScroll(true, true, false, false, false, false, false, false, CrystalType.D));
		_scrolls.put(6577, new EnchantScroll(true, true, false, false, false, false, false, false, CrystalType.S));

		
		// Blessed Scrolls: Enchant Armor
		_scrolls.put(6570, new EnchantScroll(false, true, false, false, false, false, false, false, CrystalType.A));
		_scrolls.put(6572, new EnchantScroll(false, true, false, false, false, false, false, false, CrystalType.B));
		_scrolls.put(6574, new EnchantScroll(false, true, false, false, false, false, false, false, CrystalType.C));
		_scrolls.put(6576, new EnchantScroll(false, true, false, false, false, false, false, false, CrystalType.D));
		_scrolls.put(6578, new EnchantScroll(false, true, false, false, false, false, false, false, CrystalType.S));
		
		//Custom Blessed Scroll 75% : Enchant Weapon
		_scrolls.put(12609, new EnchantScroll(true, false, false, false, true, false, false, false, CrystalType.A)); //Custom Blessed 75% Scroll
		_scrolls.put(12601, new EnchantScroll(true, false, false, false, true, false, false, false, CrystalType.S)); //Custom Blessed 75% Scroll
		
		//Custom Blessed Scrolls 75%: Enchant Armor
		_scrolls.put(12610, new EnchantScroll(false, false, false, false ,true, false ,false, false, CrystalType.A)); //Custom Blessed 75% Scroll
		_scrolls.put(12602, new EnchantScroll(false, false, false, false ,true, false ,false, false, CrystalType.S)); //Custom Blessed 75% Scroll
		
		//Improved Blessed Scrolls : Enchant Weapon
		_scrolls.put(12611, new EnchantScroll(true, false, false, false, false, false, true, false, CrystalType.A)); //Custom Improved Blessed 70% Scroll
		_scrolls.put(12595, new EnchantScroll(true, false, false, false, false, false, true, false, CrystalType.S)); //Custom Improved Blessed 70% Scroll
				
		//Improved Blessed Scrolls: Enchant Armor
		_scrolls.put(12612, new EnchantScroll(false, false, false, false, false, false ,true, false, CrystalType.A)); //Custom Improved Blessed 70% Scroll
		_scrolls.put(12596, new EnchantScroll(false, false, false, false, false, false ,true, false, CrystalType.S)); //Custom Improved Blessed 70% Scroll
		
		
		// Crystal Scrolls: Enchant Weapon
		_scrolls.put(731, new EnchantScroll(true, false, false, false, false, false, false, true, CrystalType.A));
		_scrolls.put(949, new EnchantScroll(true, false, false, false, false, false, false, true, CrystalType.B));
		_scrolls.put(953, new EnchantScroll(true, false, false, false, false, false, false, true, CrystalType.C));
		_scrolls.put(957, new EnchantScroll(true, false, false, false, false, false, false, true, CrystalType.D));
		_scrolls.put(961, new EnchantScroll(true, false, false, false, false, false, false, true, CrystalType.S));
		
		// Crystal Scrolls: Enchant Armor
		_scrolls.put(732, new EnchantScroll(false, false, false, false, false, false, false, true, CrystalType.A));
		_scrolls.put(950, new EnchantScroll(false, false, false, false, false, false, false, true, CrystalType.B));
		_scrolls.put(954, new EnchantScroll(false, false, false, false, false, false, false, true, CrystalType.C));
		_scrolls.put(958, new EnchantScroll(false, false, false, false, false, false, false, true, CrystalType.D));
		_scrolls.put(962, new EnchantScroll(false, false, false, false, false, false, false, true, CrystalType.S));
	}
	
	/**
	 * @param scroll The instance of item to make checks on.
	 * @return enchant template for scroll.
	 */
	protected static final EnchantScroll getEnchantScroll(ItemInstance scroll)
	{
		return _scrolls.get(scroll.getItemId());
	}
	
	/**
	 * @param item The instance of item to make checks on.
	 * @return true if item can be enchanted.
	 */
	protected static final boolean isEnchantable(ItemInstance item)
	{
		if (item.isHeroItem() || item.isShadowItem() || item.isEtcItem() || item.getItem().getItemType() == WeaponType.FISHINGROD)
			return false;
		
		// only equipped items or in inventory can be enchanted
		if (item.getLocation() != ItemLocation.INVENTORY && item.getLocation() != ItemLocation.PAPERDOLL)
			return false;
		
		return true;
	}
}