package net.sf.l2j.gameserver.model.actor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.commons.math.MathUtil;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;
import net.sf.l2j.commons.util.ArraysUtil;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.LoginServerThread;
import net.sf.l2j.gameserver.communitybbs.CommunityBoard;
import net.sf.l2j.gameserver.communitybbs.model.Forum;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.manager.CoupleManager;
import net.sf.l2j.gameserver.data.manager.CursedWeaponManager;
import net.sf.l2j.gameserver.data.manager.DimensionalRiftManager;
import net.sf.l2j.gameserver.data.manager.FestivalOfDarknessManager;
import net.sf.l2j.gameserver.data.manager.HeroManager;
import net.sf.l2j.gameserver.data.manager.PartyMatchRoomManager;
import net.sf.l2j.gameserver.data.manager.SevenSignsManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.data.xml.IconsData;
import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.data.xml.MapRegionData;
import net.sf.l2j.gameserver.data.xml.MapRegionData.TeleportType;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.data.xml.PlayerData;
import net.sf.l2j.gameserver.data.xml.PlayerLevelData;
import net.sf.l2j.gameserver.enums.AiEventType;
import net.sf.l2j.gameserver.enums.CabalType;
import net.sf.l2j.gameserver.enums.GaugeColor;
import net.sf.l2j.gameserver.enums.LootRule;
import net.sf.l2j.gameserver.enums.MessageType;
import net.sf.l2j.gameserver.enums.Paperdoll;
import net.sf.l2j.gameserver.enums.PunishmentType;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.enums.ShortcutType;
import net.sf.l2j.gameserver.enums.SpawnType;
import net.sf.l2j.gameserver.enums.StatusType;
import net.sf.l2j.gameserver.enums.TeamType;
import net.sf.l2j.gameserver.enums.TeleportMode;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.enums.actors.ClassRace;
import net.sf.l2j.gameserver.enums.actors.ClassType;
import net.sf.l2j.gameserver.enums.actors.MoveType;
import net.sf.l2j.gameserver.enums.actors.OperateType;
import net.sf.l2j.gameserver.enums.actors.Sex;
import net.sf.l2j.gameserver.enums.actors.WeightPenalty;
import net.sf.l2j.gameserver.enums.bbs.ForumAccess;
import net.sf.l2j.gameserver.enums.bbs.ForumType;
import net.sf.l2j.gameserver.enums.items.ActionType;
import net.sf.l2j.gameserver.enums.items.CrystalType;
import net.sf.l2j.gameserver.enums.items.EtcItemType;
import net.sf.l2j.gameserver.enums.items.ItemLocation;
import net.sf.l2j.gameserver.enums.items.ItemState;
import net.sf.l2j.gameserver.enums.items.ShotType;
import net.sf.l2j.gameserver.enums.items.WeaponType;
import net.sf.l2j.gameserver.enums.skills.EffectFlag;
import net.sf.l2j.gameserver.enums.skills.EffectType;
import net.sf.l2j.gameserver.enums.skills.Stats;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.handler.skillhandlers.SummonFriend;
import net.sf.l2j.gameserver.instance.InstanceType;
import net.sf.l2j.gameserver.model.AccessLevel;
import net.sf.l2j.gameserver.model.PetDataEntry;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.ai.type.CreatureAI;
import net.sf.l2j.gameserver.model.actor.ai.type.PlayerAI;
import net.sf.l2j.gameserver.model.actor.attack.PlayerAttack;
import net.sf.l2j.gameserver.model.actor.cast.PlayerCast;
import net.sf.l2j.gameserver.model.actor.container.npc.RewardInfo;
import net.sf.l2j.gameserver.model.actor.container.player.Appearance;
import net.sf.l2j.gameserver.model.actor.container.player.BlockList;
import net.sf.l2j.gameserver.model.actor.container.player.CubicList;
import net.sf.l2j.gameserver.model.actor.container.player.FishingStance;
import net.sf.l2j.gameserver.model.actor.container.player.HennaList;
import net.sf.l2j.gameserver.model.actor.container.player.MacroList;
//import net.sf.l2j.gameserver.model.actor.container.player.PlayerPvp;
import net.sf.l2j.gameserver.model.actor.container.player.Punishment;
import net.sf.l2j.gameserver.model.actor.container.player.QuestList;
import net.sf.l2j.gameserver.model.actor.container.player.RadarList;
import net.sf.l2j.gameserver.model.actor.container.player.RecipeBook;
import net.sf.l2j.gameserver.model.actor.container.player.Request;
import net.sf.l2j.gameserver.model.actor.container.player.ShortcutList;
import net.sf.l2j.gameserver.model.actor.container.player.SubClass;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.actor.instance.FestivalMonster;
import net.sf.l2j.gameserver.model.actor.instance.Folk;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.model.actor.instance.Servitor;
import net.sf.l2j.gameserver.model.actor.instance.StaticObject;
import net.sf.l2j.gameserver.model.actor.instance.TamedBeast;
import net.sf.l2j.gameserver.model.actor.instance.WeddingManagerNpc;
import net.sf.l2j.gameserver.model.actor.move.PlayerMove;
import net.sf.l2j.gameserver.model.actor.status.PlayerStatus;
import net.sf.l2j.gameserver.model.actor.template.PetTemplate;
import net.sf.l2j.gameserver.model.actor.template.PlayerTemplate;
import net.sf.l2j.gameserver.model.craft.ManufactureList;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.Duel.DuelState;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;
import net.sf.l2j.gameserver.model.entity.instance.Instance;
import net.sf.l2j.gameserver.model.group.CommandChannel;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.model.group.PartyMatchRoom;
import net.sf.l2j.gameserver.model.holder.CancelSkillHolder;
import net.sf.l2j.gameserver.model.holder.Timestamp;
import net.sf.l2j.gameserver.model.holder.skillnode.GeneralSkillNode;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.itemcontainer.ItemContainer;
import net.sf.l2j.gameserver.model.itemcontainer.PcFreight;
import net.sf.l2j.gameserver.model.itemcontainer.PcInventory;
import net.sf.l2j.gameserver.model.itemcontainer.PcWarehouse;
import net.sf.l2j.gameserver.model.itemcontainer.PetInventory;
import net.sf.l2j.gameserver.model.itemcontainer.listeners.ItemPassiveSkillsListener;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.model.memo.PlayerMemo;
import net.sf.l2j.gameserver.model.memo.PlayerMemo1;
import net.sf.l2j.gameserver.model.multisell.PreparedListContainer;
import net.sf.l2j.gameserver.model.olympiad.OlympiadGameManager;
import net.sf.l2j.gameserver.model.olympiad.OlympiadGameTask;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.pledge.ClanMember;
import net.sf.l2j.gameserver.model.trade.TradeList;
import net.sf.l2j.gameserver.model.zone.type.BossZone;
import net.sf.l2j.gameserver.model.zone.type.PvPFlagZone;
import net.sf.l2j.gameserver.network.GameClient;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.AbstractNpcInfo;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ChairSit;
import net.sf.l2j.gameserver.network.serverpackets.ChangeWaitType;
import net.sf.l2j.gameserver.network.serverpackets.CharInfo;
import net.sf.l2j.gameserver.network.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.DeleteObject;
import net.sf.l2j.gameserver.network.serverpackets.Earthquake;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ExAutoSoulShot;
import net.sf.l2j.gameserver.network.serverpackets.ExOlympiadMode;
import net.sf.l2j.gameserver.network.serverpackets.ExRedSky;
import net.sf.l2j.gameserver.network.serverpackets.ExServerPrimitive;
import net.sf.l2j.gameserver.network.serverpackets.ExSetCompassZoneCode;
import net.sf.l2j.gameserver.network.serverpackets.ExStorageMaxCount;
import net.sf.l2j.gameserver.network.serverpackets.GetOnVehicle;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.L2FriendStatus;
import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;
import net.sf.l2j.gameserver.network.serverpackets.LeaveWorld;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.MyTargetSelected;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.ObservationMode;
import net.sf.l2j.gameserver.network.serverpackets.ObservationReturn;
import net.sf.l2j.gameserver.network.serverpackets.PartySmallWindowUpdate;
import net.sf.l2j.gameserver.network.serverpackets.PetInventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreListBuy;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreListSell;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreManageListBuy;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreManageListSell;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreMsgSell;
import net.sf.l2j.gameserver.network.serverpackets.RecipeShopManageList;
import net.sf.l2j.gameserver.network.serverpackets.RecipeShopMsg;
import net.sf.l2j.gameserver.network.serverpackets.RecipeShopSellList;
import net.sf.l2j.gameserver.network.serverpackets.RelationChanged;
import net.sf.l2j.gameserver.network.serverpackets.Revive;
import net.sf.l2j.gameserver.network.serverpackets.Ride;
import net.sf.l2j.gameserver.network.serverpackets.SendTradeDone;
import net.sf.l2j.gameserver.network.serverpackets.ServerClose;
import net.sf.l2j.gameserver.network.serverpackets.SetupGauge;
import net.sf.l2j.gameserver.network.serverpackets.ShortBuffStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ShortCutInit;
import net.sf.l2j.gameserver.network.serverpackets.SkillCoolTime;
import net.sf.l2j.gameserver.network.serverpackets.SkillList;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.StaticObjectInfo;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.network.serverpackets.TargetSelected;
import net.sf.l2j.gameserver.network.serverpackets.TargetUnselected;
import net.sf.l2j.gameserver.network.serverpackets.TitleUpdate;
import net.sf.l2j.gameserver.network.serverpackets.TradePressOtherOk;
import net.sf.l2j.gameserver.network.serverpackets.TradePressOwnOk;
import net.sf.l2j.gameserver.network.serverpackets.TradeStart;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.network.serverpackets.ValidateLocation;
import net.sf.l2j.gameserver.scripting.QuestState;
import net.sf.l2j.gameserver.skills.AbstractEffect;
import net.sf.l2j.gameserver.skills.Formulas;
import net.sf.l2j.gameserver.skills.L2Skill;
import net.sf.l2j.gameserver.skills.effects.EffectTemplate;
import net.sf.l2j.gameserver.skills.funcs.FuncHenna;
import net.sf.l2j.gameserver.skills.funcs.FuncMaxCpMul;
import net.sf.l2j.gameserver.skills.funcs.FuncRegenCpMul;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import net.sf.l2j.gameserver.taskmanager.CancelBuffRestoreTaskManager;
import net.sf.l2j.gameserver.taskmanager.GameTimeTaskManager;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;
import net.sf.l2j.gameserver.taskmanager.ShadowItemTaskManager;
import net.sf.l2j.gameserver.taskmanager.WaterTaskManager;
//import net.sf.l2j.util.Mysql;
import net.sf.l2j.util.PlayerVar;
import net.sf.l2j.util.PlayerVariables;
import net.sf.l2j.gameserver.model.actor.Creature.*;

import Base.Dungeon.Dungeon;
import Base.Dungeon.Instance1;
import Base.Manager.NewCharTaskManager;
import Base.Util.Mysql;
import c1c0s.VoteManagerAPI.VoteDay;
import c1c0s.VoteManagerAPI.VoteSites;
import net.sf.l2j.gameserver.data.xml.IconsData;

/**
 * This class represents a player in the world.<br>
 * There is always a client-thread connected to this (except if a player-store is activated upon logout).
 */
public final class Player extends Playable
{
	private static final String RESTORE_SKILLS_FOR_CHAR = "SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND class_index=?";
	private static final String ADD_OR_UPDATE_SKILL = "INSERT INTO character_skills (char_obj_id,skill_id,skill_level,class_index) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE skill_level=VALUES(skill_level)";
	private static final String DELETE_SKILL_FROM_CHAR = "DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND class_index=?";
	private static final String DELETE_CHAR_SKILLS = "DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?";
	
	private static final String ADD_SKILL_SAVE = "INSERT INTO character_skills_save (char_obj_id,skill_id,skill_level,effect_count,effect_cur_time,reuse_delay,systime,restore_type,class_index,buff_index) VALUES (?,?,?,?,?,?,?,?,?,?)";
	private static final String RESTORE_SKILL_SAVE = "SELECT skill_id,skill_level,effect_count,effect_cur_time, reuse_delay, systime, restore_type FROM character_skills_save WHERE char_obj_id=? AND class_index=? ORDER BY buff_index ASC";
	private static final String DELETE_SKILL_SAVE = "DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?";
	
	private static final String INSERT_CHARACTER = "INSERT INTO characters (account_name,obj_Id,char_name,level,maxHp,curHp,maxCp,curCp,maxMp,curMp,face,hairStyle,hairColor,sex,exp,sp,karma,pvpkills,pkkills,clanid,race,classid,deletetime,cancraft,title,accesslevel,online,isin7sdungeon,clan_privs,wantspeace,base_class,nobless,power_grade,vip) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String UPDATE_CHARACTER = "UPDATE characters SET level=?,maxHp=?,curHp=?,maxCp=?,curCp=?,maxMp=?,curMp=?,face=?,hairStyle=?,hairColor=?,sex=?,heading=?,x=?,y=?,z=?,exp=?,expBeforeDeath=?,sp=?,karma=?,pvpkills=?,pkkills=?,clanid=?,race=?,classid=?,deletetime=?,title=?,accesslevel=?,online=?,isin7sdungeon=?,clan_privs=?,wantspeace=?,base_class=?,onlinetime=?,punish_level=?,punish_timer=?,nobless=?,power_grade=?,subpledge=?,lvl_joined_academy=?,apprentice=?,sponsor=?,varka_ketra_ally=?,clan_join_expiry_time=?,clan_create_expiry_time=?,char_name=?,death_penalty_level=?,vip=?,vipuntil=? WHERE obj_id=?";
	private static final String RESTORE_CHARACTER = "SELECT * FROM characters WHERE obj_id=?";
	
	private static final String RESTORE_CHAR_SUBCLASSES = "SELECT class_id,exp,sp,level,class_index FROM character_subclasses WHERE char_obj_id=? ORDER BY class_index ASC";
	private static final String ADD_CHAR_SUBCLASS = "INSERT INTO character_subclasses (char_obj_id,class_id,exp,sp,level,class_index) VALUES (?,?,?,?,?,?)";
	private static final String UPDATE_CHAR_SUBCLASS = "UPDATE character_subclasses SET exp=?,sp=?,level=?,class_id=? WHERE char_obj_id=? AND class_index =?";
	private static final String DELETE_CHAR_SUBCLASS = "DELETE FROM character_subclasses WHERE char_obj_id=? AND class_index=?";
	
	private static final String DELETE_CHAR_HENNAS = "DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?";
	private static final String DELETE_CHAR_SHORTCUTS = "DELETE FROM character_shortcuts WHERE char_obj_id=? AND class_index=?";
	
	private static final String RESTORE_CHAR_RECOMS = "SELECT char_id,target_id FROM character_recommends WHERE char_id=?";
	private static final String ADD_CHAR_RECOM = "INSERT INTO character_recommends (char_id,target_id) VALUES (?,?)";
	private static final String UPDATE_TARGET_RECOM_HAVE = "UPDATE characters SET rec_have=? WHERE obj_Id=?";
	private static final String UPDATE_CHAR_RECOM_LEFT = "UPDATE characters SET rec_left=? WHERE obj_Id=?";
	
	private static final String UPDATE_NOBLESS = "UPDATE characters SET nobless=? WHERE obj_Id=?";
	
	private static final String CHECK_HERO = "SELECT * FROM permanent_hero WHERE char_obj_id=?";
	
	public static final int REQUEST_TIMEOUT = 15;
	// Killing Spree System
	public int PLAYER_KILLS = 0;
	// pvp color system
	public static final Map<Integer, Integer> _pvpcolor = new HashMap<>();
	//pvp zone , name holders
	public static final Map<Integer, String> _pvpzoneNames = new HashMap<>();
	String original_name;
	String original_title;
	int original_clancrest;
	int original_allycrest;
	
	private static final Comparator<GeneralSkillNode> COMPARE_SKILLS_BY_MIN_LVL = Comparator.comparing(GeneralSkillNode::getMinLvl);
	private static final Comparator<GeneralSkillNode> COMPARE_SKILLS_BY_LVL = Comparator.comparing(GeneralSkillNode::getValue);
	
	private GameClient _client;
	private final Map<Integer, String> _chars = new HashMap<>();
	
	private final String _accountName;
	private long _deleteTimer;
	
	private boolean _isOnline;
	private long _onlineTime;
	private long _onlineBeginTime;
	private long _lastAccess;
	private long _uptime;
	
	protected int _baseClass;
	protected int _activeClass;
	protected int _classIndex;
	
	private final Map<Integer, SubClass> _subClasses = new ConcurrentSkipListMap<>();
	private final ReentrantLock _subclassLock = new ReentrantLock();
	
	private final Appearance _appearance;
	
	private long _expBeforeDeath;
	private int _karma;
	private int _pvpKills;
	private long VIPUntil = 0;
	private int _pkKills;
	private byte _pvpFlag;
	private int _siegeState;
	private WeightPenalty _weightPenalty = WeightPenalty.NONE;
	
	private int _lastCompassZone; // the last compass zone update send to the client
	
	private boolean _isIn7sDungeon;
	
	private final Punishment _punishment = new Punishment(this);
	private final RecipeBook _recipeBook = new RecipeBook(this);
	
	private boolean _isInOlympiadMode;
	private boolean _isInOlympiadStart;
	private int _olympiadGameId = -1;
	private int _olympiadSide = -1;
	
	private DuelState _duelState = DuelState.NO_DUEL;
	private int _duelId;
	private SystemMessageId _noDuelReason = SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL;
	
	private Boat _boat;
	private final SpawnLocation _boatPosition = new SpawnLocation(0, 0, 0, 0);
	
	private boolean _canFeed;
	protected PetTemplate _petTemplate;
	private PetDataEntry _petData;
	private int _controlItemId;
	private int _curFeed;
	protected Future<?> _mountFeedTask;
	private ScheduledFuture<?> _dismountTask;
	
	private int _mountType;
	private int _mountNpcId;
	private int _mountLevel;
	private int _mountObjectId;
	
	protected int _throneId;
	
	private TeleportMode _teleportMode = TeleportMode.NONE;
	private boolean _isCrystallizing;
	private boolean _isCrafting;
	
	private boolean _isSitting;
	private boolean _isStanding;
	private boolean _isSittingNow;
	private boolean _isStandingNow;
	private boolean _HaveHeroItem;
	
	private final Location _savedLocation = new Location(0, 0, 0);
	
	private int _recomHave;
	private int _recomLeft;
	private final List<Integer> _recomChars = new ArrayList<>();
	
	private final PcInventory _inventory = new PcInventory(this);
	private PcWarehouse _warehouse;
	private PcFreight _freight;
	private final List<PcFreight> _depositedFreight = new ArrayList<>();
	
	private OperateType _operateType = OperateType.NONE;
	
	private TradeList _activeTradeList;
	private ItemContainer _activeWarehouse;
	
	private final TradeList _buyList = new TradeList(this);
	private final TradeList _sellList = new TradeList(this);
	private final ManufactureList _manufactureList = new ManufactureList();
	
	private PreparedListContainer _currentMultiSell;
	
	private boolean _isNoble;
	private boolean _isHero;
	private boolean _isVIP;
	
	private Folk _currentFolk;
	
	//Auto Pots
	private Map<Integer, Future<?>> _autoPotTasks = new HashMap<>();
	
	
	private final PlayerMemo _memos = new PlayerMemo(getObjectId());
	private final PlayerMemo1 _memos1 = new PlayerMemo1(getObjectId());
	
	private final FishingStance _fishingStance = new FishingStance(this);
	private final ShortcutList _shortcutList = new ShortcutList(this);
	private final MacroList _macroList = new MacroList(this);
	private final HennaList _hennaList = new HennaList(this);
	private final RadarList _radarList = new RadarList(this);
	private final CubicList _cubicList = new CubicList(this);
	private final BlockList _blockList = new BlockList(this);
	private final QuestList _questList = new QuestList(this);
	
	private Summon _summon;
	private TamedBeast _tamedBeast;
	
	// Pet agathion = World.getInstance().getAgat(getObjectId());
	
	private int _partyRoom;
	
	private int _clanId;
	private Clan _clan;
	private int _apprentice;
	private int _sponsor;
	private long _clanJoinExpiryTime;
	private long _clanCreateExpiryTime;
	private int _powerGrade;
	private int _clanPrivileges;
	private int _pledgeClass;
	private int _pledgeType;
	private int _lvlJoinedAcademy;
	
	private boolean _wantsPeace;
	
	private int _deathPenaltyBuffLevel;
	
	private final AtomicInteger _charges = new AtomicInteger();
	private ScheduledFuture<?> _chargeTask;
	
	private AccessLevel _accessLevel;
	
	private Location _enterWorld;
	private final Map<String, ExServerPrimitive> _debug = new HashMap<>();
	
	private Party _party;
	private LootRule _lootRule;
	
	private Player _activeRequester;
	private long _requestExpireTime;
	private final Request _request = new Request(this);
	
	private ScheduledFuture<?> _protectTask;
	
	private long _recentFakeDeathEndTime;
	private boolean _isFakeDeath;
	
	private int _armorGradePenalty;
	private boolean _weaponGradePenalty;
	
	private ItemInstance _activeEnchantItem;
	
	protected boolean _inventoryDisable;
	
	private final Set<Integer> _activeSoulShots = ConcurrentHashMap.newKeySet(1);
	
	private final int[] _loto = new int[5];
	private final int[] _race = new int[2];
	
	private TeamType _team = TeamType.NONE;
	
	private int _alliedVarkaKetra; // lvl of alliance with ketra orcs or varka silenos, used in quests and aggro checks [-5,-1] varka, 0 neutral, [1,5] ketra
	
	private final List<String> _validBypass = new ArrayList<>();
	private final List<String> _validBypass2 = new ArrayList<>();
	
	private Forum _forumMemo;
	
	private final Map<Integer, L2Skill> _skills = new ConcurrentSkipListMap<>();
	private final Map<Integer, Timestamp> _reuseTimeStamps = new ConcurrentHashMap<>();
	
	private int _cursedWeaponEquippedId;
	
	private int _reviveRequested;
	private double _revivePower = .0;
	private boolean _revivePet;
	
	private int _mailPosition;
	
	private static final int FALLING_VALIDATION_DELAY = 10000;
	private volatile long _fallingTimestamp;
	
	private ScheduledFuture<?> _shortBuffTask;
	private int _shortBuffTaskSkillId;
	
	private int _coupleId;
	private boolean _isUnderMarryRequest;
	private int _requesterId;
	
	private final List<Integer> _friendList = new ArrayList<>(); // Related to CB.
	private final List<Integer> _selectedFriendList = new ArrayList<>(); // Related to CB.
	private final List<Integer> _selectedBlocksList = new ArrayList<>(); // Related to CB.
	
	private Player _summonTargetRequest;
	private L2Skill _summonSkillRequest;
	
	private Door _requestedGate;
	
	//private PlayerPvp _pvpStats = new PlayerPvp(this);

	// Permanent Hero
	private boolean _permanent_hero = false;
	
	private List<Integer> _ignored = new ArrayList<>();
	
	private Set<CancelSkillHolder> _restore = ConcurrentHashMap.newKeySet(); // mycode
	
	private final int[] _isdarkpanther = {14799,14800,14801,14802,14803,14804,14805,14806,14807,14808,14809,14810,14811,14812,14813,14814,14815,14816,
		14817,14818,14819,14820,14821,14822,14823,14824,14825,14826,14827,14828,14829,14830,14831,14832,14833,14834,14835};
	
	boolean _contains;
	/**
	 * Constructor of Player (use Creature constructor).
	 * <ul>
	 * <li>Call the Creature constructor to create an empty _skills slot and copy basic Calculator set to this Player</li>
	 * <li>Set the name of the Player</li>
	 * </ul>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method SET the level of the Player to 1</B></FONT>
	 * @param objectId Identifier of the object to initialized
	 * @param template The L2PcTemplate to apply to the Player
	 * @param accountName The name of the account including this Player
	 * @param app The PcAppearance of the Player
	 */
	private Player(int objectId, PlayerTemplate template, String accountName, Appearance app)
	{
		super(objectId, template);
		
		getStatus().initializeValues();
		
		_accountName = accountName;
		_appearance = app;
		
		// Create an AI
		_ai = new PlayerAI(this);
		
		// Retrieve from the database all items of this Player and add them to _inventory
		getInventory().restore();
		getWarehouse();
		getFreight();
		
	}
	
	static // feed the map
	{
		// PvPcolor System Initialization
		_pvpcolor.put(500, 0xA2C4C9);//Iron - Light Brown
		_pvpcolor.put(1500, 0x76B947);//Bronze - Lime Green
		_pvpcolor.put(2500, 0x00FFFF);//Silver - Yellow
		_pvpcolor.put(3500, 0x1c46f1);//Gold - Cool Orange
		_pvpcolor.put(4500, 0xD0DF26);//Platinum - Aqua
		_pvpcolor.put(10000, 0x373585);//Diamond - Brown
		_pvpcolor.put(20000, 0xA06AB4);//Legendary - Purple 
		//_pvpcolor.put(25000, 13762547);
		//_pvpcolor.put(30000, 13762547);
		//_pvpcolor.put(35000, 13762547);
		//_pvpcolor.put(40000, 13762547);	
	}
	
	public void ignored(Integer itemId)
	{
		if (_ignored.contains(itemId))
			_ignored.remove(itemId);
		else
			_ignored.add(itemId);
	}
	
	public boolean ignoredDropContain(int itemId)
	{
		return _ignored.contains(itemId);
	}
	
	public void addBuffToRestore(L2Skill skill) // my code
	{
		_restore.add(new CancelSkillHolder(skill));
		CancelBuffRestoreTaskManager.getInstance().add(getObjectId());
	}
	
	public void restoreBuff(CancelSkillHolder holder)
	{
		if (_restore.contains(holder))
		{
			holder.getSkill().getEffects(this, this);
			_restore.remove(holder);
		}
	}
	
	/*public final void setIsBuffProtected(boolean value) //block_buff
	  {
		antibuff_activated = value;
	  }

	  	public boolean isBuffProtected()//block_buff
	   {
	      return antibuff_activated;	
	   }
	*/
	
	public boolean isAutoPot(int id)
	{
		return _autoPotTasks.keySet().contains(id);
	}

	 

	public void setAutoPot(int id,Future<?> task, boolean add)
	{
		if (add)
			_autoPotTasks.put(id, task);
		else
	  	{
			_autoPotTasks.get(id).cancel(true);
			_autoPotTasks.remove(id);
	  	}
	}


	public Set<CancelSkillHolder> getBuffToRestore()
	{
		return _restore;
	}
	
	/**
	 * Create a new Player and add it in the characters table of the database.
	 * <ul>
	 * <li>Create a new Player with an account name</li>
	 * <li>Set the name, the Hair Style, the Hair Color and the Face type of the Player</li>
	 * <li>Add the player in the characters table of the database</li>
	 * </ul>
	 * @param objectId Identifier of the object to initialized
	 * @param template The L2PcTemplate to apply to the Player
	 * @param accountName The name of the Player
	 * @param name The name of the Player
	 * @param hairStyle The hair style Identifier of the Player
	 * @param hairColor The hair color Identifier of the Player
	 * @param face The face type Identifier of the Player
	 * @param sex The sex type Identifier of the Player
	 * @return The Player added to the database or null
	 */
	public static Player create(int objectId, PlayerTemplate template, String accountName, String name, byte hairStyle, byte hairColor, byte face, Sex sex)
	{
		// Create a new Player with an account name
		final Appearance app = new Appearance(face, hairColor, hairStyle, sex);
		final Player player = new Player(objectId, template, accountName, app);
		
		// Set the name of the Player
		player.setName(name);
		
		// Set access level
		player.setAccessLevel(Config.DEFAULT_ACCESS_LEVEL);
		
		// Cache few informations into CharNameTable.
		PlayerInfoTable.getInstance().addPlayer(objectId, accountName, name, player.getAccessLevel().getLevel());
		
		// Set the base class ID to that of the actual class ID.
		player.setBaseClass(player.getClassId());
		
		// Add the player in the characters table of the database
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_CHARACTER))
		{
			ps.setString(1, accountName);
			ps.setInt(2, player.getObjectId());
			ps.setString(3, player.getName());
			ps.setInt(4, player.getStatus().getLevel());
			ps.setInt(5, player.getStatus().getMaxHp());
			ps.setDouble(6, player.getStatus().getHp());
			ps.setInt(7, player.getStatus().getMaxCp());
			ps.setDouble(8, player.getStatus().getCp());
			ps.setInt(9, player.getStatus().getMaxMp());
			ps.setDouble(10, player.getStatus().getMp());
			ps.setInt(11, player.getAppearance().getFace());
			ps.setInt(12, player.getAppearance().getHairStyle());
			ps.setInt(13, player.getAppearance().getHairColor());
			ps.setInt(14, player.getAppearance().getSex().ordinal());
			ps.setLong(15, player.getStatus().getExp());
			ps.setInt(16, player.getStatus().getSp());
			ps.setInt(17, player.getKarma());
			ps.setInt(18, player.getPvpKills());
			ps.setInt(19, player.getPkKills());
			ps.setInt(20, player.getClanId());
			ps.setInt(21, player.getRace().ordinal());
			ps.setInt(22, player.getClassId().getId());
			ps.setLong(23, player.getDeleteTimer());
			ps.setInt(24, player.hasDwarvenCraft() ? 1 : 0);
			ps.setString(25, player.getTitle());
			ps.setInt(26, player.getAccessLevel().getLevel());
			ps.setInt(27, player.isOnlineInt());
			ps.setInt(28, player.isIn7sDungeon() ? 1 : 0);
			ps.setInt(29, player.getClanPrivileges());
			ps.setInt(30, player.wantsPeace() ? 1 : 0);
			ps.setInt(31, player.getBaseClass());
			ps.setInt(32, player.isNoble() ? 1 : 0);
			ps.setInt(33, player.isVIP() ? 1 : 0);
			ps.setLong(34, 0);
			ps.executeUpdate();
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't create player {} for {} account.", e, name, accountName);
			return null;
		}
		
		return player;
	}
	
	@Override
	public void addFuncsToNewCharacter()
	{
		// Add Creature functionalities.
		super.addFuncsToNewCharacter();
		
		addStatFunc(FuncMaxCpMul.getInstance());
		addStatFunc(FuncRegenCpMul.getInstance());
		
		addStatFunc(FuncHenna.getSTR());
		addStatFunc(FuncHenna.getCON());
		addStatFunc(FuncHenna.getDEX());
		addStatFunc(FuncHenna.getINT());
		addStatFunc(FuncHenna.getMEN());
		addStatFunc(FuncHenna.getWIT());
	}
	
	@Override
	public PlayerStatus getStatus()
	{
		return (PlayerStatus) _status;
	}
	
	@Override
	public void setStatus()
	{
		_status = new PlayerStatus(this);
	}
	
	public final Appearance getAppearance()
	{
		return _appearance;
	}
	
	/**
	 * @return the {@link PlayerTemplate} linked to this {@link Player} base class.
	 */
	public final PlayerTemplate getBaseTemplate()
	{
		return PlayerData.getInstance().getTemplate(_baseClass);
	}
	
	@Override
	public final PlayerTemplate getTemplate()
	{
		return (PlayerTemplate) super.getTemplate();
	}
	
	@Override
	public void setWalkOrRun(boolean value)
	{
		super.setWalkOrRun(value);
		
		broadcastUserInfo();
	}
	
	public void setTemplate(ClassId newclass)
	{
		super.setTemplate(PlayerData.getInstance().getTemplate(newclass));
	}
	
	/**
	 * Return the AI of the Player (create it if necessary).
	 */
	@Override
	public CreatureAI getAI()
	{
		CreatureAI ai = _ai;
		if (ai == null)
		{
			synchronized (this)
			{
				ai = _ai;
				if (ai == null)
					_ai = ai = new PlayerAI(this);
			}
		}
		return ai;
	}
	
	@Override
	public boolean denyAiAction()
	{
		return super.denyAiAction() || isInStoreMode() || _operateType == OperateType.OBSERVE;
	}
	
	@Override
	public PlayerMove getMove()
	{
		return (PlayerMove) _move;
	}
	
	@Override
	public void setMove()
	{
		_move = new PlayerMove(this);
	}
	
	@Override
	public PlayerAttack getAttack()
	{
		return (PlayerAttack) _attack;
	}
	
	@Override
	public void setAttack()
	{
		_attack = new PlayerAttack(this);
	}
	
	@Override
	public PlayerCast getCast()
	{
		return (PlayerCast) _cast;
	}
	
	@Override
	public void setCast()
	{
		_cast = new PlayerCast(this);
	}
	
	@Override
	public boolean canBeHealed()
	{
		return super.canBeHealed() && !isCursedWeaponEquipped();
	}
	
	/**
	 * A newbie is a {@link Player} between level 6 and 25 which didn't yet acquired first occupation change.<br>
	 * <br>
	 * <b>Since IL, Newbie statut isn't anymore the first character of an account reaching that state, but any.</b>
	 * @param checkLowLevel : If true, check also low level requirement.
	 * @return True if this {@link Player} can be considered a Newbie.
	 */
	public boolean isNewbie(boolean checkLowLevel)
	{
		return (checkLowLevel) ? (getClassId().getLevel() <= 1 && getStatus().getLevel() >= 6 && getStatus().getLevel() <= 25) : (getClassId().getLevel() <= 1 && getStatus().getLevel() <= 25);
	}
	
	public void setBaseClass(int baseClass)
	{
		_baseClass = baseClass;
	}
	
	public void setBaseClass(ClassId classId)
	{
		_baseClass = classId.ordinal();
	}
	
	/**
	 * @return True if the state of {@link OperateType} of this {@link Player} is different than NONE.
	 */
	@Override
	public boolean isOperating()
	{
		return _operateType != OperateType.NONE;
	}
	
	public boolean isCrafting()
	{
		return _isCrafting;
	}
	
	public void setCrafting(boolean state)
	{
		_isCrafting = state;
	}
	
	/**
	 * @return The {@link PlayerMemo} of the current {@link Player}.
	 */
	public PlayerMemo getMemos()
	{
		return _memos;
	}
	
	public PlayerMemo1 getMemos1()
	{
		return _memos1;
	}
	
	/**
	 * @return The {@link ShortcutList} of the current {@link Player}.
	 */
	public ShortcutList getShortcutList()
	{
		return _shortcutList;
	}
	
	/**
	 * @return The {@link MacroList} of the current {@link Player}.
	 */
	public MacroList getMacroList()
	{
		return _macroList;
	}
	
	/**
	 * 0 = not involved, 1 = attacker, 2 = defender
	 * @return The siege state of the {@link Player}.
	 */
	public int getSiegeState()
	{
		return _siegeState;
	}
	
	/**
	 * Set the siege state of the {@link Player}.
	 * @param siegeState : The new value to set.
	 */
	public void setSiegeState(int siegeState)
	{
		_siegeState = siegeState;
	}
	
	@Override
	public byte getPvpFlag()
	{
		return _pvpFlag;
	}
	
	/**
	 * Set the PvP flag of the {@link Player}.
	 * @param pvpFlag : 0 or 1.
	 */
	public void setPvpFlag(int pvpFlag)
	{
		_pvpFlag = (byte) pvpFlag;
	}
	
	public void updatePvPFlag(int value)
	{
		if (getPvpFlag() == value)
			return;
		
		setPvpFlag(value);
		sendPacket(new UserInfo(this));
		
		if (_summon != null)
			sendPacket(new RelationChanged(_summon, getRelation(this), false));
		
		broadcastRelationsChanges();
	}
	
	public int getRelation(Player target)
	{
		int result = 0;
		
		// karma and pvp may not be required
		if (getPvpFlag() != 0)
			result |= RelationChanged.RELATION_PVP_FLAG;
		if (getKarma() > 0)
			result |= RelationChanged.RELATION_HAS_KARMA;
		
		if (isClanLeader())
			result |= RelationChanged.RELATION_LEADER;
		
		if (getSiegeState() != 0)
		{
			result |= RelationChanged.RELATION_INSIEGE;
			if (getSiegeState() != target.getSiegeState())
				result |= RelationChanged.RELATION_ENEMY;
			else
				result |= RelationChanged.RELATION_ALLY;
			if (getSiegeState() == 1)
				result |= RelationChanged.RELATION_ATTACKER;
		}
		
		if (getClan() != null && target.getClan() != null)
		{
			if (target.getPledgeType() != Clan.SUBUNIT_ACADEMY && getPledgeType() != Clan.SUBUNIT_ACADEMY && target.getClan().isAtWarWith(getClan().getClanId()))
			{
				result |= RelationChanged.RELATION_1SIDED_WAR;
				if (getClan().isAtWarWith(target.getClan().getClanId()))
					result |= RelationChanged.RELATION_MUTUAL_WAR;
			}
		}
		return result;
	}
	
	@Override
	public void revalidateZone(boolean force)
	{
		super.revalidateZone(force);
		
		if (Config.ALLOW_WATER)
		{
			if (isInWater())
				WaterTaskManager.getInstance().add(this);
			else
				WaterTaskManager.getInstance().remove(this);
		}
		
		if (isInsideZone(ZoneId.SIEGE))
		{
			if (_lastCompassZone == ExSetCompassZoneCode.SIEGEWARZONE2)
				return;
			
			_lastCompassZone = ExSetCompassZoneCode.SIEGEWARZONE2;
			sendPacket(new ExSetCompassZoneCode(ExSetCompassZoneCode.SIEGEWARZONE2));
		}
		else if (isInsideZone(ZoneId.PVP))
		{
			if (_lastCompassZone == ExSetCompassZoneCode.PVPZONE)
				return;
			
			_lastCompassZone = ExSetCompassZoneCode.PVPZONE;
			sendPacket(new ExSetCompassZoneCode(ExSetCompassZoneCode.PVPZONE));
		}
		else if (isIn7sDungeon())
		{
			if (_lastCompassZone == ExSetCompassZoneCode.SEVENSIGNSZONE)
				return;
			
			_lastCompassZone = ExSetCompassZoneCode.SEVENSIGNSZONE;
			sendPacket(new ExSetCompassZoneCode(ExSetCompassZoneCode.SEVENSIGNSZONE));
		}
		else if (isInsideZone(ZoneId.PEACE))
		{
			if (_lastCompassZone == ExSetCompassZoneCode.PEACEZONE)
				return;
			
			_lastCompassZone = ExSetCompassZoneCode.PEACEZONE;
			sendPacket(new ExSetCompassZoneCode(ExSetCompassZoneCode.PEACEZONE));
		}
		else
		{
			if (_lastCompassZone == ExSetCompassZoneCode.GENERALZONE)
				return;
			
			if (_lastCompassZone == ExSetCompassZoneCode.SIEGEWARZONE2)
				updatePvPStatus();
			
			_lastCompassZone = ExSetCompassZoneCode.GENERALZONE;
			sendPacket(new ExSetCompassZoneCode(ExSetCompassZoneCode.GENERALZONE));
		}
	}
	
	/**
	 * @return the PK counter of the Player.
	 */
	public int getPkKills()
	{
		return _pkKills;
	}
	
	/**
	 * Set the PK counter of the Player.
	 * @param pkKills A number.
	 */
	public void setPkKills(int pkKills)
	{
		_pkKills = pkKills;
	}
	
	/**
	 * @return The _deleteTimer of the Player.
	 */
	public long getDeleteTimer()
	{
		return _deleteTimer;
	}
	
	/**
	 * Set the _deleteTimer of the Player.
	 * @param deleteTimer Time in ms.
	 */
	public void setDeleteTimer(long deleteTimer)
	{
		_deleteTimer = deleteTimer;
	}
	
	/**
	 * @return The current weight of the Player.
	 */
	public int getCurrentWeight()
	{
		return _inventory.getTotalWeight();
	}
	
	/**
	 * @return The number of recommandation obtained by the Player.
	 */
	public int getRecomHave()
	{
		return _recomHave;
	}
	
	public boolean getHaveHeroItem() // Permanent Hero item status check.
	{
		return _HaveHeroItem;
	}
	
	/*
	 * static void setPermanentHero(Playable playable, ItemInstance item, boolean forceUse) { int itemId = item.getItemId(); if (itemId == 129) //Permanent Hero Item Check - Hero on Login { ((Player)playable).setHero(true); } }
	 */
	
	/**
	 * Set the number of recommandations obtained by the Player (Max : 255).
	 * @param value Number of recommandations obtained.
	 */
	public void setRecomHave(int value)
	{
		_recomHave = MathUtil.limit(value, 0, 255);
	}
	
	/**
	 * Edit the number of recommandation obtained by the Player (Max : 255).
	 * @param value : The value to add or remove.
	 */
	public void editRecomHave(int value)
	{
		_recomHave = MathUtil.limit(_recomHave + value, 0, 255);
	}
	
	/**
	 * @return The number of recommandation that the Player can give.
	 */
	public int getRecomLeft()
	{
		return _recomLeft;
	}
	
	/**
	 * Set the number of givable recommandations by the {@link Player} (Max : 9).
	 * @param value : The number of recommendations a player can give.
	 */
	public void setRecomLeft(int value)
	{
		_recomLeft = MathUtil.limit(value, 0, 9);
	}
	
	/**
	 * Increment the number of recommandation that the Player can give.
	 */
	protected void decRecomLeft()
	{
		if (_recomLeft > 0)
			_recomLeft--;
	}
	
	public List<Integer> getRecomChars()
	{
		return _recomChars;
	}
	
	public void giveRecom(Player target)
	{
		target.editRecomHave(1);
		decRecomLeft();
		
		_recomChars.add(target.getObjectId());
		
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(ADD_CHAR_RECOM))
			{
				ps.setInt(1, getObjectId());
				ps.setInt(2, target.getObjectId());
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(UPDATE_TARGET_RECOM_HAVE))
			{
				ps.setInt(1, target.getRecomHave());
				ps.setInt(2, target.getObjectId());
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(UPDATE_CHAR_RECOM_LEFT))
			{
				ps.setInt(1, getRecomLeft());
				ps.setInt(2, getObjectId());
				ps.execute();
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't update player recommendations.", e);
		}
	}
	
	public boolean canRecom(Player target)
	{
		return !_recomChars.contains(target.getObjectId());
	}
	
	/**
	 * Set the exp of the Player before a death
	 * @param exp
	 */
	public void setExpBeforeDeath(long exp)
	{
		_expBeforeDeath = exp;
	}
	
	public long getExpBeforeDeath()
	{
		return _expBeforeDeath;
	}
	
	/**
	 * Return the Karma of the Player.
	 */
	@Override
	public int getKarma()
	{
		return _karma;
	}
	
	/**
	 * Set the Karma of the Player and send StatusUpdate (broadcast).
	 * @param karma A value.
	 */
	public void setKarma(int karma)
	{
		if (karma < 0)
			karma = 0;
		
		if (_karma > 0 && karma == 0)
		{
			sendPacket(new UserInfo(this));
			broadcastRelationsChanges();
		}
		
		// send message with new karma value
		sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_KARMA_HAS_BEEN_CHANGED_TO_S1).addNumber(karma));
		
		_karma = karma;
		broadcastKarma();
	}
	
	/**
	 * Note: Base weight limit value is 69000.
	 */
	@Override
	public int getWeightLimit()
	{
		return (int) getStatus().calcStat(Stats.WEIGHT_LIMIT, 69000 * Formulas.CON_BONUS[getStatus().getCON()] * Config.WEIGHT_LIMIT, this, null);
	}
	
	public int getArmorGradePenalty()
	{
		return _armorGradePenalty;
	}
	
	public boolean getWeaponGradePenalty()
	{
		return _weaponGradePenalty;
	}
	
	public WeightPenalty getWeightPenalty()
	{
		return _weightPenalty;
	}
	
	/**
	 * Update the overloaded status of the Player.
	 */
	public void refreshWeightPenalty()
	{
		final int weightLimit = getWeightLimit();
		if (weightLimit <= 0)
			return;
		
		final double ratio = (getCurrentWeight() - getStatus().calcStat(Stats.WEIGHT_PENALTY, 0, this, null)) / weightLimit;
		
		final WeightPenalty newWeightPenalty;
		if (ratio < 0.5)
			newWeightPenalty = WeightPenalty.NONE;
		else if (ratio < 0.666)
			newWeightPenalty = WeightPenalty.LEVEL_1;
		else if (ratio < 0.8)
			newWeightPenalty = WeightPenalty.LEVEL_2;
		else if (ratio < 1)
			newWeightPenalty = WeightPenalty.LEVEL_3;
		else
			newWeightPenalty = WeightPenalty.LEVEL_4;
		
		if (_weightPenalty != newWeightPenalty)
		{
			_weightPenalty = newWeightPenalty;
			
			sendPacket(new UserInfo(this));
			sendPacket(new EtcStatusUpdate(this));
			broadcastCharInfo();
		}
	}
	
	/**
	 * Refresh expertise level ; weapon got one rank, when armor got 4 ranks.<br>
	 */
	public void refreshExpertisePenalty()
	{
		final int expertiseLevel = getSkillLevel(L2Skill.SKILL_EXPERTISE);
		
		int armorPenalty = 0;
		boolean weaponPenalty = false;
		
		for (final ItemInstance item : getInventory().getPaperdollItems())
		{
			if (item.getItemType() != EtcItemType.ARROW && item.getItem().getCrystalType().getId() > expertiseLevel)
			{
				if (item.isWeapon())
					weaponPenalty = true;
				else
					armorPenalty += (item.getItem().getBodyPart() == Item.SLOT_FULL_ARMOR) ? 2 : 1;
			}
		}
		
		armorPenalty = Math.min(armorPenalty, 4);
		
		// Found a different state than previous ; update it.
		if (_weaponGradePenalty != weaponPenalty || _armorGradePenalty != armorPenalty)
		{
			_weaponGradePenalty = weaponPenalty;
			_armorGradePenalty = armorPenalty;
			
			// Passive skill "Grade Penalty" is either granted or dropped.
			if (_weaponGradePenalty || _armorGradePenalty > 0)
				addSkill(SkillTable.getInstance().getInfo(4267, 1), false);
			else
				removeSkill(4267, false);
			
			sendSkillList();
			sendPacket(new EtcStatusUpdate(this));
			
			// Activate / desactivate weapon effects.
			final ItemInstance item = getActiveWeaponInstance();
			if (item != null)
			{
				if (_weaponGradePenalty)
					ItemPassiveSkillsListener.getInstance().onUnequip(Paperdoll.NULL, item, this);
				else
					ItemPassiveSkillsListener.getInstance().onEquip(Paperdoll.NULL, item, this);
			}
		}
	}
	
	/**
	 * Equip or unequip the item.
	 * <UL>
	 * <LI>If item is equipped, shots are applied if automation is on.</LI>
	 * <LI>If item is unequipped, shots are discharged.</LI>
	 * </UL>
	 * @param item The item to charge/discharge.
	 * @param abortAttack If true, the current attack will be aborted in order to equip the item.
	 */
	public void useEquippableItem(ItemInstance item, boolean abortAttack)
	{
		ItemInstance[] items = null;
		final boolean isEquipped = item.isEquipped();
		final int oldInvLimit = getStatus().getInventoryLimit();
		
		if (item.getItem() instanceof Weapon)
			item.unChargeAllShots();
		
		if (getOlympiadGameId() != -1 ||  isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(this) ) // Custom Code Oly Restriction +6 A grade . 
			//|| getActingPlayer().isOlympiadStart()
		{
			if(item.getEnchantLevel() > 6 || (item.getItem().getCrystalType() == CrystalType.S))
			{
				sendMessage("You are not allowed to use an item of this grade or enchant level on the Olympiad!");
				return;
			}
		}
		
		if (isEquipped)
		{
			if (item.getEnchantLevel() > 0)
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED).addNumber(item.getEnchantLevel()).addItemName(item));
			else
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISARMED).addItemName(item));
			
			items = getInventory().unequipItemInBodySlotAndRecord(item);
		}
		
		else
		{
			items = getInventory().equipItemAndRecord(item);
			
			if (item.isEquipped())
			{
				if (item.getEnchantLevel() > 0)
					sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_S2_EQUIPPED).addNumber(item.getEnchantLevel()).addItemName(item));
				else
					sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_EQUIPPED).addItemName(item));
				
				if ((item.getItem().getBodyPart() & Item.SLOT_ALLWEAPON) != 0)
					rechargeShots(true, true);
			}
			else
				sendPacket(SystemMessageId.CANNOT_EQUIP_ITEM_DUE_TO_BAD_CONDITION);
		}
		refreshExpertisePenalty();
		broadcastUserInfo();
		
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addItems(Arrays.asList(items));
		sendPacket(iu);
		
		if (abortAttack)
			getAttack().stop();
		
		if (getStatus().getInventoryLimit() != oldInvLimit)
			sendPacket(new ExStorageMaxCount(this));
	}
	
	/**
	 * @return PvP Kills of the Player (number of player killed during a PvP).
	 */
	public int getPvpKills()
	{
		return _pvpKills;
	}
	
	/**
	 * Set PvP Kills of the Player (number of player killed during a PvP).
	 * @param pvpKills A value.
	 */
	public void setPvpKills(int pvpKills)
	{
		_pvpKills = pvpKills;
	}
	
	/**
	 * @return The ClassId object of the Player contained in L2PcTemplate.
	 */
	public ClassId getClassId()
	{
		return getTemplate().getClassId();
	}
	
	/**
	 * Set the template of this {@link Player}.
	 * @param id : The id of the {@link ClassId} to set.
	 */
	public void setClassId(int id)
	{
		if (!_subclassLock.tryLock())
			return;
		
		try
		{
			if (getLvlJoinedAcademy() != 0 && _clan != null && ClassId.VALUES[id].getLevel() == 2)
			{
				// Calculate points to add on Clan Reputation score.
				int points;
				if (getLvlJoinedAcademy() <= 16)
					points = 150; //default 400
				else if (getLvlJoinedAcademy() >= 39)
					points = 70; //default 170
				else
					points = 150 - (getLvlJoinedAcademy() - 16) * 10; //default 400 - formula
				
				// Add the points.
				_clan.addReputationScore(points);
				
				// Broadcast to all members, included future ousted member.
				_clan.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_GRADUATED_FROM_ACADEMY).addString(getName()).addNumber(points));
				
				// Send static messages to the ousted member.
				sendPacket(SystemMessageId.ACADEMY_MEMBERSHIP_TERMINATED);
				sendPacket(SystemMessageId.YOU_HAVE_WITHDRAWN_FROM_CLAN);
				
				setLvlJoinedAcademy(0);
				
				// Refresh existing members, and send last message.
				_clan.broadcastToMembersExcept(this, new PledgeShowMemberListDelete(getName()), SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_WITHDRAWN_FROM_THE_CLAN).addString(getName()));
				
				// Oust pledge member from the academy.
				_clan.removeClanMember(getObjectId(), 0);
				
				// Reward the ousted members with graduation gift : academy circlet
				addItem("Gift", 8181, 1, this, true);
			}
			
			if (isSubClassActive())
				_subClasses.get(_classIndex).setClassId(id);
			
			broadcastPacket(new MagicSkillUse(this, this, 5103, 1, 1000, 0));
			setClassTemplate(id);
			
			if (getClassId().getLevel() == 3)
				sendPacket(SystemMessageId.THIRD_CLASS_TRANSFER);
			else
				sendPacket(SystemMessageId.CLASS_TRANSFER);
			
			// Update class icon in party and clan
			if (_party != null)
				_party.broadcastPacket(new PartySmallWindowUpdate(this));
			
			if (_clan != null)
				_clan.broadcastToMembers(new PledgeShowMemberListUpdate(this));
			
			if (Config.AUTO_LEARN_SKILLS)
				rewardSkills();
		}
		finally
		{
			_subclassLock.unlock();
		}
	}
	
	public void setActiveEnchantItem(ItemInstance scroll)
	{
		_activeEnchantItem = scroll;
	}
	
	public ItemInstance getActiveEnchantItem()
	{
		return _activeEnchantItem;
	}
	
	/**
	 * @return The Race object of the Player.
	 */
	public ClassRace getRace()
	{
		return (isSubClassActive()) ? getBaseTemplate().getRace() : getTemplate().getRace();
	}
	
	public RadarList getRadarList()
	{
		return _radarList;
	}
	
	public CubicList getCubicList()
	{
		return _cubicList;
	}
	
	/**
	 * @param castleId The castle to check.
	 * @return True if this Player is a clan leader in ownership of the passed castle.
	 */
	public boolean isCastleLord(int castleId)
	{
		final Clan clan = getClan();
		if (clan != null && clan.getLeader().getPlayerInstance() == this)
		{
			final Castle castle = CastleManager.getInstance().getCastleByOwner(clan);
			return castle != null && castle.getCastleId() == castleId;
		}
		return false;
	}
	
	/**
	 * @return The Clan Identifier of the Player.
	 */
	public int getClanId()
	{
		return _clanId;
	}
	
	/**
	 * @return The Clan Crest Identifier of the Player or 0.
	 */
	public int getClanCrestId()
	{
		return (_clan != null) ? _clan.getCrestId() : 0;
	}
	
	/**
	 * @return The Clan CrestLarge Identifier or 0
	 */
	public int getClanCrestLargeId()
	{
		return (_clan != null) ? _clan.getCrestLargeId() : 0;
	}
	
	public long getClanJoinExpiryTime()
	{
		return _clanJoinExpiryTime;
	}
	
	public void setClanJoinExpiryTime(long time)
	{
		_clanJoinExpiryTime = time;
	}
	
	public long getClanCreateExpiryTime()
	{
		return _clanCreateExpiryTime;
	}
	
	public void setClanCreateExpiryTime(long time)
	{
		_clanCreateExpiryTime = time;
	}
	
	public void setOnlineTime(long time)
	{
		_onlineTime = time;
		_onlineBeginTime = System.currentTimeMillis();
	}
	
	/**
	 * Return the PcInventory Inventory of the Player contained in _inventory.
	 */
	@Override
	public PcInventory getInventory()
	{
		return _inventory;
	}
	
	@Override
	public boolean isSitting()
	{
		return _isSitting;
	}
	
	@Override
	public boolean isStanding()
	{
		return _isStanding;
	}
	
	@Override
	public boolean isSittingNow()
	{
		return _isSittingNow;
	}
	
	@Override
	public boolean isStandingNow()
	{
		return _isStandingNow;
	}
	
	/**
	 * Sit down the {@link Player}. The player retrieves control after a 2.5s delay.
	 * <ul>
	 * <li>Set the AI Intention to IDLE</li>
	 * <li>Broadcast {@link ChangeWaitType} packet</li>
	 * </ul>
	 * @return true if the Player could successfully sit down, false otherwise.
	 */
	public boolean sitDown()
	{
		_isSittingNow = true;
		_isStanding = false;
		
		ThreadPool.schedule(() ->
		{
			_isSittingNow = false;
			_isSitting = true;
			
			getAI().notifyEvent(AiEventType.SAT_DOWN, null, null);
			
		}, 2500);
		
		// Broadcast the packet.
		broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_SITTING));
		
		// Tutorial
		final QuestState qs = _questList.getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("CE8388608", null, this);
		
		return true;
	}
	
	/**
	 * Stand the {@link Player} up. The player retrieves control after a 2.5s delay.
	 * <ul>
	 * <li>Schedules the STOOD_UP event</li>
	 * <li>Broadcast {@link ChangeWaitType} packet</li>
	 * </ul>
	 */
	public void standUp()
	{
		_isStandingNow = true;
		_isSitting = false;
		
		// Schedule a stand up task to wait for the animation to finish
		ThreadPool.schedule(() ->
		{
			_isStandingNow = false;
			_isStanding = true;
			
			getAI().notifyEvent(AiEventType.STOOD_UP, null, null);
			
		}, 2500);
		
		// Broadcast the packet.
		broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_STANDING));
	}
	
	/**
	 * @return The PcWarehouse object of the Player.
	 */
	public PcWarehouse getWarehouse()
	{
		if (_warehouse == null)
		{
			_warehouse = new PcWarehouse(this);
			_warehouse.restore();
		}
		return _warehouse;
	}
	
	/**
	 * Free memory used by Warehouse
	 */
	public void clearWarehouse()
	{
		if (_warehouse != null)
			_warehouse.deleteMe();
		
		_warehouse = null;
	}
	
	/**
	 * @return The PcFreight object of the Player.
	 */
	public PcFreight getFreight()
	{
		if (_freight == null)
		{
			_freight = new PcFreight(this);
			_freight.restore();
		}
		return _freight;
	}
	
	/**
	 * Free memory used by Freight
	 */
	public void clearFreight()
	{
		if (_freight != null)
			_freight.deleteMe();
		
		_freight = null;
	}
	
	/**
	 * @param objectId The id of the owner.
	 * @return deposited PcFreight object for the objectId or create new if not existing.
	 */
	public PcFreight getDepositedFreight(int objectId)
	{
		for (final PcFreight freight : _depositedFreight)
		{
			if (freight != null && freight.getOwnerId() == objectId)
				return freight;
		}
		
		final PcFreight freight = new PcFreight(null);
		freight.doQuickRestore(objectId);
		_depositedFreight.add(freight);
		return freight;
	}
	
	/**
	 * Clear memory used by deposited freight
	 */
	public void clearDepositedFreight()
	{
		for (final PcFreight freight : _depositedFreight)
		{
			if (freight != null)
				freight.deleteMe();
		}
		_depositedFreight.clear();
	}
	
	/**
	 * @return The Adena amount of the Player.
	 */
	public int getAdena()
	{
		return _inventory.getAdena();
	}
	
	/**
	 * @return The Ancient Adena amount of the Player.
	 */
	public int getAncientAdena()
	{
		return _inventory.getAncientAdena();
	}
	
	/**
	 * Add adena to Inventory of the Player and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param count int Quantity of adena to be added
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 */
	public void addAdena(String process, int count, WorldObject reference, boolean sendMessage)
	{
		if (sendMessage)
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S1_ADENA).addNumber(count));
		
		if (count > 0)
		{
			_inventory.addAdena(process, count, this, reference);
			
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addItem(_inventory.getAdenaInstance());
			sendPacket(iu);
		}
	}
	
	/**
	 * Reduce adena in Inventory of the Player and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param count int Quantity of adena to be reduced
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successfull
	 */
	public boolean reduceAdena(String process, int count, WorldObject reference, boolean sendMessage)
	{
		if (count > getAdena())
		{
			if (sendMessage)
				sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
			
			return false;
		}
		
		if (count > 0)
		{
			final ItemInstance adenaItem = _inventory.getAdenaInstance();
			if (!_inventory.reduceAdena(process, count, this, reference))
				return false;
			
			// Send update packet
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addItem(adenaItem);
			sendPacket(iu);
			
			if (sendMessage)
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED_ADENA).addNumber(count));
		}
		return true;
	}
	
	/**
	 * Add ancient adena to Inventory of the Player and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param count int Quantity of ancient adena to be added
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 */
	public void addAncientAdena(String process, int count, WorldObject reference, boolean sendMessage)
	{
		if (sendMessage)
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(PcInventory.ANCIENT_ADENA_ID).addNumber(count));
		
		if (count > 0)
		{
			_inventory.addAncientAdena(process, count, this, reference);
			
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addItem(_inventory.getAncientAdenaInstance());
			sendPacket(iu);
		}
	}
	
	/**
	 * Reduce ancient adena in Inventory of the Player and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param count int Quantity of ancient adena to be reduced
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successfull
	 */
	public boolean reduceAncientAdena(String process, int count, WorldObject reference, boolean sendMessage)
	{
		if (count > getAncientAdena())
		{
			if (sendMessage)
				sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
			
			return false;
		}
		
		if (count > 0)
		{
			final ItemInstance ancientAdenaItem = _inventory.getAncientAdenaInstance();
			if (!_inventory.reduceAncientAdena(process, count, this, reference))
				return false;
			
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addItem(ancientAdenaItem);
			sendPacket(iu);
			
			if (sendMessage)
			{
				if (count > 1)
					sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED).addItemName(PcInventory.ANCIENT_ADENA_ID).addItemNumber(count));
				else
					sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(PcInventory.ANCIENT_ADENA_ID));
			}
		}
		return true;
	}
	
	/**
	 * Adds item to inventory and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param item ItemInstance to be added
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 */
	public void addItem(String process, ItemInstance item, WorldObject reference, boolean sendMessage)
	{
		if (item.getCount() > 0)
		{
			// Sends message to client if requested
			if (sendMessage)
			{
				if (item.getCount() > 1)
					sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S2_S1).addItemName(item).addNumber(item.getCount()));
				else if (item.getEnchantLevel() > 0)
					sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_A_S1_S2).addNumber(item.getEnchantLevel()).addItemName(item));
				else
					sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S1).addItemName(item));
			}
			
			// Add the item to inventory
			final ItemInstance newitem = _inventory.addItem(process, item, this, reference);
			
			// Send inventory update packet
			final InventoryUpdate playerIU = new InventoryUpdate();
			playerIU.addItem(newitem);
			sendPacket(playerIU);
			
			// Update current load as well
			final StatusUpdate su = new StatusUpdate(this);
			su.addAttribute(StatusType.CUR_LOAD, getCurrentWeight());
			sendPacket(su);
			
			// Cursed Weapon
			if (CursedWeaponManager.getInstance().isCursed(newitem.getItemId()))
				CursedWeaponManager.getInstance().activate(this, newitem);
			// If you pickup arrows and a bow is equipped, try to equip them if no arrows is currently equipped.
			else if (item.getItem().getItemType() == EtcItemType.ARROW && getAttackType() == WeaponType.BOW && !getInventory().hasItemIn(Paperdoll.LHAND))
				checkAndEquipArrows();
		}
	}
	
	/**
	 * Adds item to Inventory and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param itemId int Item Identifier of the item to be added
	 * @param count int Quantity of items to be added
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 * @return The created ItemInstance.
	 */
	public ItemInstance addItem(String process, int itemId, int count, WorldObject reference, boolean sendMessage)
	{
		if (count > 0)
		{
			// Retrieve the template of the item.
			final Item item = ItemData.getInstance().getTemplate(itemId);
			if (item == null)
				return null;
			
			// Sends message to client if requested. Since Herbs are directly assimilated, they don't send any "You have earned X" message, only "The effects of X flow through you".
			if (sendMessage && item.getItemType() != EtcItemType.HERB)
			{
				if (count > 1)
				{
					if (process.equalsIgnoreCase("Sweep") || process.equalsIgnoreCase("Quest"))
						sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(itemId).addItemNumber(count));
					else
						sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S2_S1).addItemName(itemId).addItemNumber(count));
				}
				else
				{
					if (process.equalsIgnoreCase("Sweep") || process.equalsIgnoreCase("Quest"))
						sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(itemId));
					else
						sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_PICKED_UP_S1).addItemName(itemId));
				}
			}
			
			// If the item is herb type, dont add it to inventory.
			if (item.getItemType() == EtcItemType.HERB)
			{
				final ItemInstance herb = new ItemInstance(0, itemId);
				
				final IItemHandler handler = ItemHandler.getInstance().getHandler(herb.getEtcItem());
				if (handler != null)
					handler.useItem(this, herb, false);
			}
			else
			{
				// Add the item to inventory
				final ItemInstance createdItem = _inventory.addItem(process, itemId, count, this, reference);
				
				// Cursed Weapon
				if (CursedWeaponManager.getInstance().isCursed(createdItem.getItemId()))
					CursedWeaponManager.getInstance().activate(this, createdItem);
				// If you pickup arrows and a bow is equipped, try to equip them if no arrows is currently equipped.
				else if (item.getItemType() == EtcItemType.ARROW && getAttackType() == WeaponType.BOW && !getInventory().hasItemIn(Paperdoll.LHAND))
					checkAndEquipArrows();
				
				return createdItem;
			}
		}
		return null;
	}
	
	/**
	 * Destroy item from inventory and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param item ItemInstance to be destroyed
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successfull
	 */
	public boolean destroyItem(String process, ItemInstance item, WorldObject reference, boolean sendMessage)
	{
		return destroyItem(process, item, item.getCount(), reference, sendMessage);
	}
	
	/**
	 * Destroy item from inventory and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param item ItemInstance to be destroyed
	 * @param count int Quantity of ancient adena to be reduced
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successfull
	 */
	public boolean destroyItem(String process, ItemInstance item, int count, WorldObject reference, boolean sendMessage)
	{
		item = _inventory.destroyItem(process, item, count, this, reference);
		if (item == null)
		{
			if (sendMessage)
				sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			
			return false;
		}
		
		// Send inventory update packet
		final InventoryUpdate iu = new InventoryUpdate();
		if (item.getCount() == 0)
			iu.addRemovedItem(item);
		else
			iu.addModifiedItem(item);
		sendPacket(iu);
		
		// Update current load as well
		final StatusUpdate su = new StatusUpdate(this);
		su.addAttribute(StatusType.CUR_LOAD, getCurrentWeight());
		sendPacket(su);
		
		// Sends message to client if requested
		if (sendMessage)
		{
			if (count > 1)
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED).addItemName(item).addItemNumber(count));
			else
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(item));
		}
		return true;
	}
	
	/**
	 * Destroys item from inventory and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param objectId int Item Instance identifier of the item to be destroyed
	 * @param count int Quantity of items to be destroyed
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successfull
	 */
	@Override
	public boolean destroyItem(String process, int objectId, int count, WorldObject reference, boolean sendMessage)
	{
		final ItemInstance item = _inventory.getItemByObjectId(objectId);
		if (item == null)
		{
			if (sendMessage)
				sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			
			return false;
		}
		
		return destroyItem(process, item, count, reference, sendMessage);
	}
	
	/**
	 * Destroys shots from inventory without logging and only occasional saving to database. Sends InventoryUpdate packet to the Player.
	 * @param objectId int Item Instance identifier of the item to be destroyed
	 * @param count int Quantity of items to be destroyed
	 * @return boolean informing if the action was successfull
	 */
	public boolean destroyItemWithoutTrace(int objectId, int count)
	{
		final ItemInstance item = _inventory.getItemByObjectId(objectId);
		if (item == null || item.getCount() < count)
			return false;
		
		return destroyItem(null, item, count, null, false);
	}
	
	/**
	 * Destroy item from inventory by using its <B>itemId</B> and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param itemId int Item identifier of the item to be destroyed
	 * @param count int Quantity of items to be destroyed
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successfull
	 */
	@Override
	public boolean destroyItemByItemId(String process, int itemId, int count, WorldObject reference, boolean sendMessage)
	{
		if (itemId == 57)
			return reduceAdena(process, count, reference, sendMessage);
		
		final ItemInstance item = _inventory.getItemByItemId(itemId);
		
		if (item == null || item.getCount() < count || _inventory.destroyItemByItemId(process, itemId, count, this, reference) == null)
		{
			if (sendMessage)
				sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			
			return false;
		}
		
		// Send inventory update packet
		final InventoryUpdate playerIU = new InventoryUpdate();
		playerIU.addItem(item);
		sendPacket(playerIU);
		
		// Update current load as well
		final StatusUpdate su = new StatusUpdate(this);
		su.addAttribute(StatusType.CUR_LOAD, getCurrentWeight());
		sendPacket(su);
		
		// Sends message to client if requested
		if (sendMessage)
		{
			if (count > 1)
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED).addItemName(itemId).addItemNumber(count));
			else
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(itemId));
		}
		return true;
	}
	
	/**
	 * Transfers item to another ItemContainer and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param objectId int Item Identifier of the item to be transfered
	 * @param count int Quantity of items to be transfered
	 * @param target Inventory the Inventory target.
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @return ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public ItemInstance transferItem(String process, int objectId, int count, Inventory target, WorldObject reference)
	{
		final ItemInstance oldItem = checkItemManipulation(objectId, count);
		if (oldItem == null)
			return null;
		
		final ItemInstance newItem = getInventory().transferItem(process, objectId, count, target, this, reference);
		if (newItem == null)
			return null;
		
		// Send inventory update packet
		final InventoryUpdate playerIU = new InventoryUpdate();
		
		if (oldItem.getCount() > 0 && oldItem != newItem)
			playerIU.addModifiedItem(oldItem);
		else
			playerIU.addRemovedItem(oldItem);
		
		sendPacket(playerIU);
		
		// Update current load as well
		StatusUpdate playerSU = new StatusUpdate(this);
		playerSU.addAttribute(StatusType.CUR_LOAD, getCurrentWeight());
		sendPacket(playerSU);
		
		// Send target update packet
		if (target instanceof PcInventory)
		{
			final Player targetPlayer = ((PcInventory) target).getOwner();
			
			final InventoryUpdate playerIU2 = new InventoryUpdate();
			if (newItem.getCount() > count)
				playerIU2.addModifiedItem(newItem);
			else
				playerIU2.addNewItem(newItem);
			targetPlayer.sendPacket(playerIU2);
			
			// Update current load as well
			playerSU = new StatusUpdate(targetPlayer);
			playerSU.addAttribute(StatusType.CUR_LOAD, targetPlayer.getCurrentWeight());
			targetPlayer.sendPacket(playerSU);
		}
		else if (target instanceof PetInventory)
		{
			final PetInventoryUpdate petIU = new PetInventoryUpdate();
			if (newItem.getCount() > count)
				petIU.addModifiedItem(newItem);
			else
				petIU.addNewItem(newItem);
			((PetInventory) target).getOwner().getOwner().sendPacket(petIU);
		}
		return newItem;
	}
	
	/**
	 * Drop item from inventory and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param item ItemInstance to be dropped
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 * @return boolean informing if the action was successfull
	 */
	public boolean dropItem(String process, ItemInstance item, WorldObject reference, boolean sendMessage)
	{
		item = _inventory.dropItem(process, item, this, reference);
		
		if (item == null)
		{
			if (sendMessage)
				sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			
			return false;
		}
		
		item.dropMe(this, 25);
		
		// Send inventory update packet
		final InventoryUpdate playerIU = new InventoryUpdate();
		playerIU.addItem(item);
		sendPacket(playerIU);
		
		// Update current load as well
		final StatusUpdate su = new StatusUpdate(this);
		su.addAttribute(StatusType.CUR_LOAD, getCurrentWeight());
		sendPacket(su);
		
		// Sends message to client if requested
		if (sendMessage)
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_DROPPED_S1).addItemName(item));
		
		return true;
	}
	
	/**
	 * Drop item from inventory by using its <B>objectID</B> and send InventoryUpdate packet to the Player.
	 * @param process String Identifier of process triggering this action
	 * @param objectId int Item Instance identifier of the item to be dropped
	 * @param count int Quantity of items to be dropped
	 * @param x int coordinate for drop X
	 * @param y int coordinate for drop Y
	 * @param z int coordinate for drop Z
	 * @param reference WorldObject Object referencing current action like NPC selling item or previous item in transformation
	 * @param sendMessage boolean Specifies whether to send message to Client about this action
	 * @return ItemInstance corresponding to the new item or the updated item in inventory
	 */
	public ItemInstance dropItem(String process, int objectId, int count, int x, int y, int z, WorldObject reference, boolean sendMessage)
	{
		final ItemInstance invItem = _inventory.getItemByObjectId(objectId);
		final ItemInstance item = _inventory.dropItem(process, objectId, count, this, reference);
		
		if (item == null)
		{
			if (sendMessage)
				sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			
			return null;
		}
		
		item.setInstance(getInstance(), true); // True because Drop me will spawn it
		item.dropMe(this, x, y, z);
		
		// Send inventory update packet
		final InventoryUpdate playerIU = new InventoryUpdate();
		playerIU.addItem(invItem);
		sendPacket(playerIU);
		
		// Update current load as well
		final StatusUpdate su = new StatusUpdate(this);
		su.addAttribute(StatusType.CUR_LOAD, getCurrentWeight());
		sendPacket(su);
		
		// Sends message to client if requested
		if (sendMessage)
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_DROPPED_S1).addItemName(item));
		
		return item;
	}
	
	public ItemInstance checkItemManipulation(int objectId, int count)
	{
		if (World.getInstance().getObject(objectId) == null)
			return null;
		
		final ItemInstance item = getInventory().getItemByObjectId(objectId);
		
		if (item == null || item.getOwnerId() != getObjectId())
			return null;
		
		if (count < 1 || (count > 1 && !item.isStackable()))
			return null;
		
		if (count > item.getCount())
			return null;

		// Pet is summoned and not the item that summoned the pet AND not the buggle from strider you're mounting
		if (_summon != null && _summon.getControlItemId() == objectId || _mountObjectId == objectId)
			return null;
		
		var agathion = World.getInstance().getAgat(this.getObjectId());

		// Agathion whom item you try to manipulate is summoned/mounted.
		if (agathion != null && agathion.getControlItemId() == objectId)
			return null;
		
		//Check Dressme
		if (item.getItem().getTemplate() && getCostumeitemObjId() == objectId)
		{
			return null;
		}
		//new is the live server , last is the one that will be live , we make changes on last? on _new
		//Check Dressme Hair
		if (item.getItem().isTemplateHair() && getCostumeHeaditemObjId() == objectId)
		{
			return null;
		}
		
		
		if (getActiveEnchantItem() != null && getActiveEnchantItem().getObjectId() == objectId)
			return null;
		
		// We cannot put a Weapon with Augmention in WH while casting (Possible Exploit)
		if (item.isAugmented() && getCast().isCastingNow())
			return null;
		
		return item;
	}
	
	/**
	 * Launch a task corresponding to Config time.
	 * @param protect boolean Drop timer or activate it.
	 */
	public void setSpawnProtection(boolean protect)
	{
		if (protect)
		{
			if (_protectTask == null)
				_protectTask = ThreadPool.schedule(() ->
				{
					setSpawnProtection(false);
					sendMessage("The spawn protection has ended.");
				}, Config.PLAYER_SPAWN_PROTECTION * 1000L);
		}
		else
		{
			_protectTask.cancel(true);
			_protectTask = null;
		}
		broadcastUserInfo();
	}
	
	public boolean isSpawnProtected()
	{
		return _protectTask != null;
	}
	
	/**
	 * Set protection from agro mobs when getting up from fake death, according settings.
	 */
	public void setRecentFakeDeath()
	{
		_recentFakeDeathEndTime = System.currentTimeMillis() + Config.PLAYER_FAKEDEATH_UP_PROTECTION * 1000;
	}
	
	public void clearRecentFakeDeath()
	{
		_recentFakeDeathEndTime = 0;
	}
	
	public boolean isRecentFakeDeath()
	{
		return _recentFakeDeathEndTime > System.currentTimeMillis();
	}
	
	@Override
	public final boolean isFakeDeath()
	{
		return _isFakeDeath;
	}
	
	@Override
	public final boolean isAlikeDead()
	{
		if (super.isAlikeDead())
			return true;
		
		return isFakeDeath();
	}
	
	/**
	 * @return The client owner of this char.
	 */
	public GameClient getClient()
	{
		return _client;
	}
	
	public void setClient(GameClient client)
	{
		_client = client;
	}
	
	public String getAccountName()
	{
		return _accountName;
	}
	
	public Map<Integer, String> getAccountChars()
	{
		return _chars;
	}
	
	/**
	 * Close the active connection with the {@link GameClient} linked to this {@link Player}.
	 * @param closeClient : If true, the client is entirely closed. Otherwise, the client is sent back to login.
	 */
	public void logout(boolean closeClient)
	{
		final GameClient client = _client;
		if (client == null)
			return;
		
		if (client.isDetached())
			client.cleanMe(true);
		else if (!client.getConnection().isClosed())
			client.close((closeClient) ? LeaveWorld.STATIC_PACKET : ServerClose.STATIC_PACKET);
	}
	
	@Override
	public void enableSkill(L2Skill skill)
	{
		super.enableSkill(skill);
		_reuseTimeStamps.remove(skill.getReuseHashCode());
	}
	
	@Override
	public void onAction(Player player, boolean isCtrlPressed, boolean isShiftPressed)
	{
		if (player.getTarget() != this)
			player.setTarget(this);
		else
		{
			if (isAttackableWithoutForceBy(player) || (isCtrlPressed && isAttackableBy(player)))
				player.getAI().tryToAttack(this, isCtrlPressed, isShiftPressed);
			else if (isOperating())
				player.getAI().tryToInteract(this, isCtrlPressed, isShiftPressed);
			else
				player.getAI().tryToFollow(this, isShiftPressed);
		}
	}
	
	@Override
	public void broadcastPacket(L2GameServerPacket packet, boolean selfToo)
	{
		if (selfToo)
			sendPacket(packet);
		
		super.broadcastPacket(packet, selfToo);
	}
	
	@Override
	public void broadcastPacketInRadius(L2GameServerPacket packet, int radius)
	{
		sendPacket(packet);
		
		super.broadcastPacketInRadius(packet, radius);
	}
	
	/**
	 * Broadcast informations from a user to himself and his knownlist.<BR>
	 * If player is morphed, it sends informations from the template the player is using.
	 * <ul>
	 * <li>Send a UserInfo packet (public and private data) to this Player.</li>
	 * <li>Send a CharInfo packet (public data only) to Player's knownlist.</li>
	 * </ul>
	 */
	public final void broadcastUserInfo()
	{
		sendPacket(new UserInfo(this));
		
		if (getPolymorphTemplate() != null)
			broadcastPacket(new AbstractNpcInfo.PcMorphInfo(this, getPolymorphTemplate()), false);
		else
			broadcastCharInfo();
	}
	
	public final void broadcastCharInfo()
	{
		for (final Player player : getKnownType(Player.class))
		{
			player.sendPacket(new CharInfo(this));
			
			final int relation = getRelation(player);
			// TODO check this!!!
			final boolean isAutoAttackable = isAttackableWithoutForceBy(player);
			
			player.sendPacket(new RelationChanged(this, relation, isAutoAttackable));
			if (_summon != null)
				player.sendPacket(new RelationChanged(_summon, relation, isAutoAttackable));
		}
	}
	
	/**
	 * Broadcast player title information.
	 */
	public final void broadcastTitleInfo()
	{
		sendPacket(new UserInfo(this));
		broadcastPacket(new TitleUpdate(this));
	}
	
	/**
	 * @return the Alliance Identifier of the Player.
	 */
	public int getAllyId()
	{
		if (_clan == null)
			return 0;
		
		return _clan.getAllyId();
	}
	
	public int getAllyCrestId()
	{
		if (getClanId() == 0)
			return 0;
		
		if (getClan().getAllyId() == 0)
			return 0;
		
		return getClan().getAllyCrestId();
	}
	
	@Override
	public void sendPacket(L2GameServerPacket packet)
	{
		if (_client != null)
			_client.sendPacket(packet);
	}
	
	@Override
	public void sendPacket(SystemMessageId id)
	{
		sendPacket(SystemMessage.getSystemMessage(id));
	}
	
	/**
	 * Check if this {@link Player} can open any type of private shop (buy, sell, manufacture). Show him the error message, if any.
	 * @param cancelActiveTrade : if true, active trade will also be canceled.
	 * @return true if all conditions are met, false otherwise.
	 */
	public boolean canOpenPrivateStore(boolean cancelActiveTrade)
	{
		// If under shop mode, don't check any conditions except death.
		if (isInStoreMode())
			return !isAlikeDead();
		
		// Cancel active trade, if parameter is on.
		if (cancelActiveTrade && getActiveTradeList() != null)
			cancelActiveTrade();
		
		// Under fight conditions.
		if (isInDuel() || AttackStanceTaskManager.getInstance().isInAttackStance(this) || getPvpFlag() > 0 || getAttack().isAttackingNow())
		{
			setOperateType(OperateType.NONE);
			sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return false;
		}
		
		// Under cast.
		if (getCast().isCastingNow())
		{
			setOperateType(OperateType.NONE);
			sendPacket(SystemMessageId.PRIVATE_STORE_NOT_WHILE_CASTING);
			return false;
		}
		
		// No store zones.
		if (isInsideZone(ZoneId.NO_STORE) || isInOlympiadMode())
		{
			setOperateType(OperateType.NONE);
			sendPacket(SystemMessageId.NO_PRIVATE_STORE_HERE);
			return false;
		}
		
		// Misc conditions.
		if (isAlikeDead() || isMounted() || isProcessingRequest() || isOutOfControl())
		{
			setOperateType(OperateType.NONE);
			return false;
		}
		
		// Can't open a private store if already sat (isInStoreMode() is checked in order toggle off works).
		if ((isSitting() && !isInStoreMode()) || isStandingNow())
		{
			setOperateType(OperateType.NONE);
			return false;
		}
		
		return true;
	}
	
	public void tryOpenPrivateBuyStore()
	{
		// Check multiple conditions. Message is sent directly from the method.
		if (!canOpenPrivateStore(true))
			return;
		
		if (getOperateType() == OperateType.NONE || getOperateType() == OperateType.BUY)
		{
			if (isSittingNow())
				return;
			
			// Stand up only if operate type is BUY.
			if (getOperateType() == OperateType.BUY)
				standUp();
			
			setOperateType(OperateType.BUY_MANAGE);
			sendPacket(new PrivateStoreManageListBuy(this));
		}
	}
	
	public void tryOpenPrivateSellStore(boolean isPackageSale)
	{
		// Check multiple conditions. Message is sent directly from the method.
		if (!canOpenPrivateStore(true))
			return;
		
		if (getOperateType() == OperateType.NONE || getOperateType() == OperateType.SELL || getOperateType() == OperateType.PACKAGE_SELL)
		{
			if (isSittingNow())
				return;
			
			// Stand up only if operate type is SELL or PACKAGE_SELL.
			if (getOperateType() == OperateType.SELL || getOperateType() == OperateType.PACKAGE_SELL)
				standUp();
			
			setOperateType(OperateType.SELL_MANAGE);
			sendPacket(new PrivateStoreManageListSell(this, isPackageSale));
		}
	}
	
	public void tryOpenWorkshop(boolean isDwarven)
	{
		// Check multiple conditions. Message is sent directly from the method.
		if (!canOpenPrivateStore(true))
			return;
		
		if (getOperateType() == OperateType.NONE || getOperateType() == OperateType.MANUFACTURE)
		{
			if (isSittingNow())
				return;
			
			// Stand up only if operate type is MANUFACTURE.
			if (getOperateType() == OperateType.MANUFACTURE)
				standUp();
			
			setOperateType(OperateType.MANUFACTURE_MANAGE);
			sendPacket(new RecipeShopManageList(this, isDwarven));
		}
	}
	
	public final PreparedListContainer getMultiSell()
	{
		return _currentMultiSell;
	}
	
	public final void setMultiSell(PreparedListContainer list)
	{
		_currentMultiSell = list;
	}
	
	@Override
	public void setTarget(WorldObject newTarget)
	{
		// Unset new target following specific conditions.
		if (newTarget != null)
		{
			// Check if the new target is visible.
			if (!newTarget.isVisible() && !(newTarget instanceof Player && isInParty() && _party.containsPlayer(newTarget)))
				newTarget = null;
			
			// Can't target and attack festival monsters if not participant.
			if (newTarget instanceof FestivalMonster && !isFestivalParticipant())
				newTarget = null;
			
			// Can't target and attack rift invaders if not in the same room.
			if (isInParty() && getParty().isInDimensionalRift() && !getParty().getDimensionalRift().isInCurrentRoomZone(newTarget))
				newTarget = null;
		}
		
		// Get the current target
		final WorldObject oldTarget = getTarget();
		
		if (oldTarget != null)
		{
			// No target change, return.
			if (oldTarget == newTarget)
				return;
			
			// Remove the Player from the _statusListener of the old target if it was a Creature.
			if (oldTarget instanceof Creature)
				((Creature) oldTarget).getStatus().removeStatusListener(this);
		}
		
		// Verify if it's a static object.
		if (newTarget instanceof StaticObject)
		{
			sendPacket(new MyTargetSelected(newTarget.getObjectId(), 0));
			sendPacket(new StaticObjectInfo((StaticObject) newTarget));
		}
		// Add the Player to the _statusListener of the new target if it's a Creature
		else if (newTarget instanceof Creature)
		{
			final Creature target = (Creature) newTarget;
			
			// Validate location of the new target.
			if (newTarget.getObjectId() != getObjectId())
				sendPacket(new ValidateLocation(target));
			
			// Show the client his new target.
			sendPacket(new MyTargetSelected(target.getObjectId(), (target.isAttackableBy(this) || target instanceof Summon) ? getStatus().getLevel() - target.getStatus().getLevel() : 0));
			
			target.getStatus().addStatusListener(this);
			
			// Send max/current hp.
			final StatusUpdate su = new StatusUpdate(target);
			su.addAttribute(StatusType.MAX_HP, target.getStatus().getMaxHp());
			su.addAttribute(StatusType.CUR_HP, (int) target.getStatus().getHp());
			sendPacket(su);
			
			broadcastPacket(new TargetSelected(getObjectId(), newTarget.getObjectId(), getX(), getY(), getZ()), false);
		}
		
		if (newTarget instanceof Folk)
			setCurrentFolk((Folk) newTarget);
		else if (newTarget == null)
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			
			if (getTarget() != null)
			{
				broadcastPacket(new TargetUnselected(this));
				setCurrentFolk(null);
			}
		}
		
		// Target the new WorldObject
		super.setTarget(newTarget);
	}
	
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return getInventory().getItemFrom(Paperdoll.RHAND);
	}
	
	@Override
	public Weapon getActiveWeaponItem()
	{
		final ItemInstance item = getActiveWeaponInstance();
		return (item == null) ? getTemplate().getFists() : (Weapon) item.getItem();
	}
	
	@Override
	public WeaponType getAttackType()
	{
		return getActiveWeaponItem().getItemType();
	}
	
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return getInventory().getItemFrom(Paperdoll.LHAND);
	}
	
	@Override
	public Item getSecondaryWeaponItem()
	{
		final ItemInstance item = getSecondaryWeaponInstance();
		return (item == null) ? null : item.getItem();
	}
	
	public boolean isUnderMarryRequest()
	{
		return _isUnderMarryRequest;
	}
	
	public void setUnderMarryRequest(boolean state)
	{
		_isUnderMarryRequest = state;
	}
	
	public int getCoupleId()
	{
		return _coupleId;
	}
	
	public void setCoupleId(int coupleId)
	{
		_coupleId = coupleId;
	}
	
	public void setRequesterId(int requesterId)
	{
		_requesterId = requesterId;
	}
	
	public void engageAnswer(int answer)
	{
		if (!_isUnderMarryRequest || _requesterId == 0)
			return;
		
		final Player requester = World.getInstance().getPlayer(_requesterId);
		if (requester != null)
		{
			if (answer == 1)
			{
				// Create the couple
				CoupleManager.getInstance().addCouple(requester, this);
				
				// Then "finish the job"
				WeddingManagerNpc.justMarried(requester, this);
			}
			else
			{
				setUnderMarryRequest(false);
				sendMessage("You declined your partner's marriage request.");
				
				requester.setUnderMarryRequest(false);
				requester.sendMessage("Your partner declined your marriage request.");
			}
		}
	}
	

	
	public void killerpanel(Creature killer)
	{
		StringBuilder killer_panel = new StringBuilder("");
		final Player _player = killer.getActingPlayer();
		killer_panel.append("<table border=0 width=\"245\">");
		killer_panel.append("<tr><td width=50 align=left></td>");
		killer_panel.append("<td align=center>"+board_getRankColor(_player.getPvpKills())+"-=" + _player.getName() + "=-"+ "</font></td>");
		killer_panel.append("<td width=30 align=right></td></tr></table>");
		
		killer_panel.append("<table border=0 width=\"285\">");
		//killer_panel.append("<tr><td width=100 align=left>"+getRank() +"<font color=\"B87F5F\">   Rank: </font>"+get_rank_by_text()+"</td>");
		killer_panel.append("<tr><td width=60 align=left>"+getRank()+"</td>");
		killer_panel.append("<td width=100 align=center>");
		killer_panel.append("<font color=\"FFA500\">CP:</font><font color=\"FFB52E\"> "+ (int)_player.getStatus().getCp() + "<font color=\"FFA500\">"+"/" + _player.getStatus().getMaxCp() + "</font><br>");//CP
		killer_panel.append("<font color=\"D60000\">HP:</font><font color=\"FF5C5C\"> "+ (int)_player.getStatus().getHp() + "<font color=\"D60000\">"+"/"+ _player.getStatus().getMaxHp() + "</font><br>");//HP
		killer_panel.append("<font color=\"006FFF\">MP:</font><font color=\"85B5E1\">"+ (int)_player.getStatus().getMp() + "<font color=\"006FFF\">"+"/"+ _player.getStatus().getMaxMp() + "</font>");//MP
		killer_panel.append("</td><td width=65 align=left>" + _player.isVIP_panel() +"</td>");
		killer_panel.append("</tr><tr></tr><tr></tr><tr></tr></table>");
		killer_panel.append("<table border=0 width=\"295\"><tr>");	
		killer_panel.append("<td width=50 align=left><font color=B87F5F>   Rank: </font></td>");//Rank
		killer_panel.append("<td width=150 align=left><font color=D9EAD3>"+get_rank_by_text() +"</font>");
		killer_panel.append("<td width=25 align=right></td>");
		killer_panel.append("</tr>");
		killer_panel.append("</table>");
		killer_panel.append("<table border=0 width=\"295\"><tr>");	
		killer_panel.append("<td width=57 align=left><font color=\"f1c0b9\">   Class: </font></td>");//Class
		killer_panel.append("<td width=150 align=left>" + get_PlayerClassColor(_player) + _player.getClassId().toString()+"</font></td>");
		killer_panel.append("<td width=25 align=right></td>");
		killer_panel.append("</tr>");
		killer_panel.append("</table>");
		killer_panel.append("<table border=0 width=\"295\"><tr>");
		killer_panel.append("<td width=45 align=left><font color=\"D4B793\">   Weapon: </font></td>");//Weapon
		if(_player.getActiveWeaponItem()!=null)
			killer_panel.append("<td width=32 align=left><font color=\"e1cfb9\">"+"<img width=32 height=32 src=" + IconsData.getInstance().getIcon(_player.getActiveWeaponItem().getItemId()) +"></td><td align=left>"+ _player.getActiveWeaponItem().getName() +"<font color=B45F06>" + " +"+_player.getActiveWeaponInstance().getEnchantLevel() + "</td><td align=left></font></td>");
		else			
			killer_panel.append("<td width=150 align=center><font color=\"e1cfb9\"> - </font></td>");
		killer_panel.append("</tr></table>");
		killer_panel.append("<font color=\"a3ebb1\">______________________________________________</font>");
	
		final NpcHtmlMessage html_0 = new NpcHtmlMessage(0);
		final NpcHtmlMessage html_1 = new NpcHtmlMessage(0);
		
		if(_player.isInsideClanwarZone())
		{
			html_1.setFile("data/html/ondie/clanarena.htm");
			html_1.replace("%killer_panel%", killer_panel.toString());
			//html_1.replace("%killer_rank%", getRank().toString());
			sendPacket(html_1);
		}
		else if(_player.isInsidePvPZone())
		{
			html_0.setFile("data/html/ondie/pvparea.htm");
			html_0.replace("%killer_panel%", killer_panel.toString());
			//html_0.replace("%killer_rank%", getRank().toString());
			sendPacket(html_0);		
		}
		

		killer_panel = null;
	}
	
	/**
	 * Kill the Creature, Apply Death Penalty, Manage gain/loss Karma and Item Drop.
	 * <ul>
	 * <li>Reduce the Experience of the Player in function of the calculated Death Penalty</li>
	 * <li>If necessary, unsummon the Pet of the killed Player</li>
	 * <li>Manage Karma gain for attacker and Karam loss for the killed Player</li>
	 * <li>If the killed Player has Karma, manage Drop Item</li>
	 * <li>Kill the Player</li>
	 * </ul>
	 * @param killer The Creature who attacks
	 */
	@Override
	public boolean doDie(Creature killer)
	{
		// Kill the Player
		if (!super.doDie(killer))
			return false;
		
		
		if (isMounted())
			stopFeed();
		
		// Clean player charges on death.
		clearCharges();
		
		synchronized (this)
		{
			if (isFakeDeath())
				stopFakeDeath(true);
		}
			
		if (killer != null)
		{
			final Player pk = killer.getActingPlayer();
			
			TournamentManager.getInstance().onKill(killer, this);
			// Clear resurrect xp calculation
			setExpBeforeDeath(0);
			
			if (isCursedWeaponEquipped())
				CursedWeaponManager.getInstance().drop(_cursedWeaponEquippedId, killer);
			else
			{
				if (pk == null || !pk.isCursedWeaponEquipped())
				{
					onDieDropItem(killer); // Check if any item should be dropped
					
					// if the area isn't an arena
					if (!isInArena())
					{
						// if both victim and attacker got clans & aren't academicians
						if (pk != null && pk.getClan() != null && getClan() != null && !isAcademyMember() && !pk.isAcademyMember())
						{
							// if clans got mutual war, then use the reputation calcul
							if (_clan.isAtWarWith(pk.getClanId()) && pk.getClan().isAtWarWith(_clan.getClanId()))
							{
								// when your reputation score is 0 or below, the other clan cannot acquire any reputation points
								if (getClan().getReputationScore() > 0)
									pk.getClan().addReputationScore(1);
								// when the opposing sides reputation score is 0 or below, your clans reputation score doesn't decrease
								if (pk.getClan().getReputationScore() > 0)
									_clan.takeReputationScore(1);
							}
						}
					}
					
					// Reduce player's xp and karma.
					if (Config.ALLOW_DELEVEL && (!hasSkill(L2Skill.SKILL_LUCKY) || getStatus().getLevel() > 9))
						applyDeathPenalty(pk != null && getClan() != null && pk.getClan() != null && (getClan().isAtWarWith(pk.getClanId()) || pk.getClan().isAtWarWith(getClanId())), pk != null);
				}
				
				
			}
			
			// Killing Spree System
			if (PLAYER_KILLS >= 4)
			{
				World.announceToOnlinePlayers(killer.getName() + " has shut down " + getName());
				//stopSkillEffects(9911);
			}
			PLAYER_KILLS = 0; // initialize killing spree back to 0 after a player dies.
			
			if(killer.isInsidePvPZone() || isInsidePvPZone()) // Killer panel if player or killer is inside pvp or clan war zone
				killerpanel(killer);
			
			if(isInsideClanwarZone() && getClan()!=null && killer.getActingPlayer().getClan()!= null && ( killer.getActingPlayer().getClan() != getClan())) // if player is killed by a player of another clan
			{
				//getClan().takeReputationScore(3);
				sendMessage("You have been killed by a rival.");
			}
				
			
		}
		
		
		
		// Unsummon Cubics
		_cubicList.stopCubics(false);
		
		if (getFusionSkill() != null)
			getCast().stop();
		
		for (final Creature creature : getKnownType(Creature.class))
			if (creature.getFusionSkill() != null && creature.getFusionSkill().getTarget() == this)
				creature.getCast().stop();
			
		// calculate death penalty buff
		calculateDeathPenaltyBuffLevel(killer);
		
		WaterTaskManager.getInstance().remove(this);
		
		if (getDungeon() != null) 
		{
			getDungeon().onPlayerDeath(this);
		}
		
		if (isPhoenixBlessed())
			reviveRequest(this, null, false);
		
		// Icons update in order to get retained buffs list
		updateEffectIcons();
		
		return true;
	}
	
	private void onDieDropItem(Creature killer)
	{
		if (killer == null)
			return;
		
		PvPFlagZone pvpZone = ZoneManager.getInstance().getZone(getX(), getY(), PvPFlagZone.class);
		if (pvpZone != null && killer.getActingPlayer() != null)
		{
			if (pvpZone.getIp() && getClient().getConnection().getInetAddress().equals(killer.getActingPlayer().getClient().getConnection().getInetAddress()))
			{
				return;
			}
			
			// GUARD SUPPORT
			/*
			 * if (pvpZone.getHwid() && getHwid().equals(killer.getActingPlayer().getHwid())) { return; }
			 */
			
			if (pvpZone.getClan() && (killer.getActingPlayer().getClan() != null && getClan() != null && killer.getActingPlayer().getClanId() == getClanId()))
			{
				return;
			}
			
			if (pvpZone.getParty() && (killer.isInParty() && killer.getParty().getMembers().contains(this)))
			{
				return;
			}
			pvpZone.giveRewards(killer.getActingPlayer());
		}
		
		final Player pk = killer.getActingPlayer();
		if (getKarma() <= 0 && pk != null && pk.getClan() != null && getClan() != null && pk.getClan().isAtWarWith(getClanId()))
			return;
		
		if ((!isInsideZone(ZoneId.PVP) || pk == null) && (!isGM() || Config.KARMA_DROP_GM))
		{
			final boolean isKillerNpc = (killer instanceof Npc);
			final int pkLimit = Config.KARMA_PK_LIMIT;
			
			int dropEquip = 0;
			int dropEquipWeapon = 0;
			int dropItem = 0;
			int dropLimit = 0;
			int dropPercent = 0;
			
			if (getKarma() > 0 && getPkKills() >= pkLimit)
			{
				dropPercent = Config.KARMA_RATE_DROP;
				dropEquip = Config.KARMA_RATE_DROP_EQUIP;
				dropEquipWeapon = Config.KARMA_RATE_DROP_EQUIP_WEAPON;
				dropItem = Config.KARMA_RATE_DROP_ITEM;
				dropLimit = Config.KARMA_DROP_LIMIT;
			}
			else if (isKillerNpc && getStatus().getLevel() > 4 && !isFestivalParticipant())
			{
				dropPercent = Config.PLAYER_RATE_DROP;
				dropEquip = Config.PLAYER_RATE_DROP_EQUIP;
				dropEquipWeapon = Config.PLAYER_RATE_DROP_EQUIP_WEAPON;
				dropItem = Config.PLAYER_RATE_DROP_ITEM;
				dropLimit = Config.PLAYER_DROP_LIMIT;
			}
			
			if (dropPercent > 0 && Rnd.get(100) < dropPercent)
			{
				int dropCount = 0;
				int itemDropPercent = 0;
				
				for (final ItemInstance itemDrop : getInventory().getItems())
				{
					// Don't drop those following things
					if (!itemDrop.isDropable() || itemDrop.isShadowItem() || itemDrop.getItemId() == 57 || itemDrop.getItem().getType2() == Item.TYPE2_QUEST || (_summon != null && _summon.getControlItemId() == itemDrop.getItemId()) || ArraysUtil.contains(Config.KARMA_NONDROPPABLE_ITEMS, itemDrop.getItemId()) || ArraysUtil.contains(Config.KARMA_NONDROPPABLE_PET_ITEMS, itemDrop.getItemId()))
						continue;
					
					if (itemDrop.isEquipped())
					{
						// Set proper chance according to Item type of equipped Item
						itemDropPercent = itemDrop.getItem().getType2() == Item.TYPE2_WEAPON ? dropEquipWeapon : dropEquip;
						getInventory().unequipItemInSlot(itemDrop.getLocationSlot());
					}
					else
						itemDropPercent = dropItem; // Item in inventory
						
					// NOTE: Each time an item is dropped, the chance of another item being dropped gets lesser (dropCount * 2)
					if (Rnd.get(100) < itemDropPercent)
					{
						dropItem("DieDrop", itemDrop, killer, true);
						
						if (++dropCount >= dropLimit)
							break;
					}
				}
			}
		}
	}
	
	public void updateKarmaLoss(long exp)
	{
		if (!isCursedWeaponEquipped() && getKarma() > 0)
		{
			final int karmaLost = Formulas.calculateKarmaLost(getStatus().getLevel(), exp);
			if (karmaLost > 0)
				setKarma(getKarma() - karmaLost);
		}
	}
	
	/**
	 * This method is used to update PvP counter, or PK counter / add Karma if necessary.<br>
	 * It also updates clan kills/deaths counters on siege.
	 * @param target The L2Playable victim.
	 */
	public void onKillUpdatePvPKarma(Playable target)
	{
		if (target == null)
			return;
		
		final Player targetPlayer = target.getActingPlayer();
		if (targetPlayer == null || targetPlayer == this)
			return;
			
		Pet agathion = World.getInstance().getAgat(getObjectId()); 
		

		if(agathion!=null) //Restore 550 CP on PvP kill if killer has agathion. 
			{	
			
			//1305 Honor of Paagrio Effect? ?
			MagicSkillUse mgc = new MagicSkillUse(agathion,this, 1305, 1, 0, 0);// (Creature cha, int skillId, int skillLevel, int hitTime, int reuseDelay)
			//MagicSkillUse mgc2 = new MagicSkillUse(this, 9976, 1, 5, 0);// (Creature cha, int skillId, int skillLevel, int hitTime, int reuseDelay)
			sendPacket(mgc);
			getStatus().setCp(getStatus().getCp() + 550);
			}
		
		
		// Killing Spree System
		// isInsideZone(ZoneId.PVP)
		if (isInsidePvPZone())
			PLAYER_KILLS++;
		
		switch (PLAYER_KILLS)
		{
			case 4:
				World.announceToOnlinePlayers(getName() + " is a Slasher on the Loose!");
				break;
			case 8:
				World.announceToOnlinePlayers(getName() + " is on Fire!");
				//MagicSkillUse mgc = new MagicSkillUse(this, 9911, 1, 0, 0);// (Creature cha, int skillId, int skillLevel, int hitTime, int reuseDelay)
				//sendPacket(mgc);
				break;
			case 12:
				World.announceToOnlinePlayers(getName() + " is Going Postal!");
				for (Player players : World.getInstance().getPlayers())
				{
					players.sendPacket(new ExRedSky(6));
				}
				break;
			case 15:
				World.announceToOnlinePlayers(getName() + " is a Legendary Slasher!");
				for (Player players : World.getInstance().getPlayers())
				{
					players.sendPacket(new Earthquake(this, 7, 5, false)); // earthquake(object,intensity,duration,isNpc)				
				}
				break;
		}
		
		// Clan War System
		if (isInsideClanwarZone() && targetPlayer.isInsideClanwarZone() && (getClan() != null) && (targetPlayer.getClan() != null) && (getClanId() != targetPlayer.getClanId()) && getClan().getLevel() >= 5 && (targetPlayer.getClan().getLevel() >= 5) && (targetPlayer.getStatus().getLevel() >= 76))
		{
			getClan().addReputationScore(12);
			sendMessage("You have killed a rival. Your clan has been rewarded with 12 reputation points!");
		}
		
		// Don't rank up the CW if it was a summon.
		if (isCursedWeaponEquipped() && target instanceof Player)
		{
			CursedWeaponManager.getInstance().increaseKills(_cursedWeaponEquippedId);
			return;
		}
		
		// If in duel and you kill (only can kill l2summon), do nothing
		if (isInDuel() && targetPlayer.isInDuel())
			return;
		
		if (target instanceof Player)
		{
			setPvpKills(getPvpKills() + 1);
			sendMessage("You have killed " + target.getName());
			broadcastUserInfo();
		}
		
		// If in pvp zone, do nothing.
		if (isInsideZone(ZoneId.PVP) && targetPlayer.isInsideZone(ZoneId.PVP))
		{
			// setPvpKills(getPvpKills() + 1);
			// Until the zone was a siege zone. Check also if victim was a player. Randomers aren't counted.
			if (target instanceof Player && getSiegeState() > 0 && targetPlayer.getSiegeState() > 0 && getSiegeState() != targetPlayer.getSiegeState())
			{
				// Now check clan relations.
				final Clan killerClan = getClan();
				if (killerClan != null)
					killerClan.setSiegeKills(killerClan.getSiegeKills() + 1);
				
				final Clan targetClan = targetPlayer.getClan();
				if (targetClan != null)
					targetClan.setSiegeDeaths(targetClan.getSiegeDeaths() + 1);
			}
			return;
		}
		
		// Check if it's pvp (cases : regular, wars, victim is PKer)
		// if (checkIfPvP(target) && target.checkIfPvP(targetPlayer) || (target!=null && target.getKarma()>0))
		/*
		 * if (checkIfPvP(target) || (targetPlayer.getClan() != null && getClan() != null && getClan().isAtWarWith(targetPlayer.getClanId()) && targetPlayer.getClan().isAtWarWith(getClanId()) && targetPlayer.getPledgeType() != Clan.SUBUNIT_ACADEMY && getPledgeType() != Clan.SUBUNIT_ACADEMY) ||
		 * (targetPlayer.getKarma() > 0 && Config.KARMA_AWARD_PK_KILL)) { // Assuming this code is executed: all above conditions satisfied, it should work. if (target instanceof Player) setPvpKills(getPvpKills() + 1); //target.getActingPlayer().getPvpStats().check(targetPlayer); }
		 */
		// Otherwise, killer is considered as a PKer.
		// else if (targetPlayer.getKarma() == 0 && targetPlayer.getPvpFlag() == 0)
		if (targetPlayer.getKarma() == 0 && targetPlayer.getPvpFlag() == 0)
		{
			// PK Points are increased only if you kill a player.
			if (target instanceof Player)
				setPkKills(getPkKills() + 1);
			
			// Calculate new karma.
			setKarma(getKarma() + Formulas.calculateKarmaGain(getPkKills(), target instanceof Summon));
			
			// Unequip adventurer items.
			checkItemRestriction();
			
			// Stop Pvp task if running.
			PvpFlagTaskManager.getInstance().remove(this, true);
		}
		
		if (_pvpcolor.containsKey(getPvpKills()))
		{
			getMemos().set("name_color", _pvpcolor.get(getPvpKills()));
			getAppearance().setNameColor(_pvpcolor.get(getPvpKills()));
			// getMemos().set("name_color",Integer.decode("6528"));
			// getAppearance().setNameColor(Integer.decode("6528"));
			broadcastUserInfo();
		}
		
	}
	
	/*
	 * private PlayerPvp getPvpStats() { return _pvpStats; }
	 */
	
	public void updatePvPStatus()
	{
		if (isInsideZone(ZoneId.PVP))
			return;
		
		PvpFlagTaskManager.getInstance().add(this, Config.PVP_NORMAL_TIME);
		
		if (getPvpFlag() == 0)
			updatePvPFlag(1);
	}
	
	public void updatePvPStatus(Creature target)
	{
		final Player player = target.getActingPlayer();
		if (player == null)
			return;
		
		if (isInDuel() && player.getDuelId() == getDuelId())
			return;
		
		if ((!isInsideZone(ZoneId.PVP) || !target.isInsideZone(ZoneId.PVP)) && player.getKarma() == 0)
		{
			PvpFlagTaskManager.getInstance().add(this, checkIfPvP(player) ? Config.PVP_PVP_TIME : Config.PVP_NORMAL_TIME);
			
			if (getPvpFlag() == 0)
				updatePvPFlag(1);
		}
	}
	
	/**
	 * Restore the experience this Player has lost and sends StatusUpdate packet.
	 * @param restorePercent The specified % of restored experience.
	 */
	public void restoreExp(double restorePercent)
	{
		if (getExpBeforeDeath() > 0)
		{
			getStatus().addExp((int) Math.round((getExpBeforeDeath() - getStatus().getExp()) * restorePercent / 100));
			setExpBeforeDeath(0);
		}
	}
	
	/**
	 * Calculate the xp loss and the karma decrease.
	 * @param atWar : If true, use clan war penalty system instead of regular system.
	 * @param killedByPlayable : If true, we use specific rules if killed by playable only.
	 */
	public void applyDeathPenalty(boolean atWar, boolean killedByPlayable)
	{
		if (isInsideZone(ZoneId.PVP))
		{
			// No xp loss inside siege zone if a Charm of Courage is active.
			if (isInsideZone(ZoneId.SIEGE))
			{
				if (isAffected(EffectFlag.CHARM_OF_COURAGE))
				{
					stopEffects(EffectType.CHARM_OF_COURAGE);
					return;
				}
			}
			// No xp loss for arenas participants killed by Playable.
			else if (killedByPlayable)
				return;
		}
		
		// Get the level of the Player.
		final int lvl = getStatus().getLevel();
		
		// The death steal you some xp.
		double percentLost = PlayerLevelData.getInstance().getPlayerLevel(lvl).getExpLossAtDeath();
		
		if (getKarma() > 0)
			percentLost *= Config.RATE_KARMA_EXP_LOST;
		
		if (isFestivalParticipant() || atWar || isInsideZone(ZoneId.SIEGE))
			percentLost /= 4.0;
		
		// Calculate the xp loss.
		long lostExp = 0;
		
		final int maxLevel = PlayerLevelData.getInstance().getMaxLevel();
		if (lvl < maxLevel)
			lostExp = Math.round((getStatus().getExpForLevel(lvl + 1) - getStatus().getExpForLevel(lvl)) * percentLost / 100);
		else
			lostExp = Math.round((getStatus().getExpForLevel(maxLevel) - getStatus().getExpForLevel(maxLevel - 1)) * percentLost / 100);
		
		// Get the xp before applying penalty.
		setExpBeforeDeath(getStatus().getExp());
		
		// Set new karma.
		updateKarmaLoss(lostExp);
		
		// Set the new xp value of the Player.
		getStatus().addExp(-lostExp);
	}
	
	public int getPartyRoom()
	{
		return _partyRoom;
	}
	
	public boolean isInPartyMatchRoom()
	{
		return _partyRoom > 0;
	}
	
	public void setPartyRoom(int id)
	{
		_partyRoom = id;
	}
	
	/**
	 * Remove the {@link Player} from both waiting list and any potential {@link PartyMatchRoom}.
	 */
	public void removeMeFromPartyMatch()
	{
		// Remove waiting Player.
		PartyMatchRoomManager.getInstance().removeWaitingPlayer(this);
		
		// Remove from existing PartyMatchRoom, if any.
		if (_partyRoom > 0)
		{
			final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(_partyRoom);
			if (room != null)
				room.removeMember(this);
		}
	}
	
	@Override
	public Summon getSummon()
	{
		return _summon;
	}
	
	/**
	 * Set the {@link Summon} of this {@link Player}.
	 * @param summon : The Summon to set.
	 */
	public void setSummon(Summon summon)
	{
		_summon = summon;
	}
	
	/**
	 * @return true if this {@link Player} has a {@link Pet}, false otherwise.
	 */
	public boolean hasPet()
	{
		return _summon instanceof Pet;
	}
	
	/**
	 * @return true if this {@link Player} has a {@link Servitor}, false otherwise.
	 */
	public boolean hasServitor()
	{
		return _summon instanceof Servitor;
	}
	
	/**
	 * @return the {@link TamedBeast} of this {@link Player}, null otherwise.
	 */
	public TamedBeast getTamedBeast()
	{
		return _tamedBeast;
	}
	
	/**
	 * Set the {@link TamedBeast} of this {@link Player}.
	 * @param tamedBeast : The TamedBeast to set.
	 */
	public void setTamedBeast(TamedBeast tamedBeast)
	{
		_tamedBeast = tamedBeast;
	}
	
	/**
	 * @return the current {@link Request}.
	 */
	public Request getRequest()
	{
		return _request;
	}
	
	/**
	 * Set the Player requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
	 * @param requester
	 */
	public void setActiveRequester(Player requester)
	{
		_activeRequester = requester;
	}
	
	/**
	 * @return the Player requester of a transaction (ex : FriendInvite, JoinAlly, JoinParty...).
	 */
	public Player getActiveRequester()
	{
		if (_activeRequester != null && _activeRequester.isRequestExpired() && _activeTradeList == null)
			_activeRequester = null;
		
		return _activeRequester;
	}
	
	/**
	 * @return True if a request is in progress, or false otherwise.
	 */
	public boolean isProcessingRequest()
	{
		return getActiveRequester() != null || _requestExpireTime > System.currentTimeMillis();
	}
	
	/**
	 * @return True if a transaction <B>(trade OR request)</B> is in progress.
	 */
	public boolean isProcessingTransaction()
	{
		return getActiveRequester() != null || _activeTradeList != null || _requestExpireTime > System.currentTimeMillis();
	}
	
	/**
	 * Set the request expire time of that {@link Player}, and set his {@link Player} partner as the active requester.
	 * @param partner : The {@link Player} partner to test.
	 */
	public void onTransactionRequest(Player partner)
	{
		_requestExpireTime = System.currentTimeMillis() + REQUEST_TIMEOUT * 1000;
		partner.setActiveRequester(this);
	}
	
	/**
	 * @return True if last request is expired, or false otherwise.
	 */
	public boolean isRequestExpired()
	{
		return _requestExpireTime <= System.currentTimeMillis();
	}
	
	public void onTransactionResponse()
	{
		_requestExpireTime = 0;
	}
	
	public ItemContainer getActiveWarehouse()
	{
		return _activeWarehouse;
	}
	
	public void setActiveWarehouse(ItemContainer warehouse)
	{
		_activeWarehouse = warehouse;
	}
	
	public TradeList getActiveTradeList()
	{
		return _activeTradeList;
	}
	
	public void setActiveTradeList(TradeList tradeList)
	{
		_activeTradeList = tradeList;
	}
	
	public void onTradeStart(Player partner)
	{
		_activeTradeList = new TradeList(this);
		_activeTradeList.setPartner(partner);
		
		sendPacket(SystemMessage.getSystemMessage(SystemMessageId.BEGIN_TRADE_WITH_S1).addString(partner.getName()));
		sendPacket(new TradeStart(this));
	}
	
	public void onTradeConfirm(Player partner)
	{
		sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CONFIRMED_TRADE).addString(partner.getName()));
		
		partner.sendPacket(TradePressOwnOk.STATIC_PACKET);
		sendPacket(TradePressOtherOk.STATIC_PACKET);
	}
	
	public void onTradeCancel(Player partner)
	{
		if (_activeTradeList == null)
			return;
		
		_activeTradeList.lock();
		_activeTradeList = null;
		
		sendPacket(SendTradeDone.FAIL_STATIC_PACKET);
		sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANCELED_TRADE).addString(partner.getName()));
	}
	
	public void onTradeFinish(boolean isSuccessful)
	{
		_activeTradeList = null;
		
		if (isSuccessful)
		{
			sendPacket(SendTradeDone.SUCCESS_STATIC_PACKET);
			sendPacket(SystemMessageId.TRADE_SUCCESSFUL);
		}
		else
		{
			sendPacket(SendTradeDone.FAIL_STATIC_PACKET);
			sendPacket(SystemMessageId.EXCHANGE_HAS_ENDED);
		}
	}
	
	public void startTrade(Player partner)
	{
		onTradeStart(partner);
		partner.onTradeStart(this);
	}
	
	public void cancelActiveTrade()
	{
		if (_activeTradeList == null)
			return;
		
		final Player partner = _activeTradeList.getPartner();
		if (partner != null)
			partner.onTradeCancel(this);
		
		onTradeCancel(this);
	}
	
	/**
	 * @return The Buy {@link TradeList} of this {@link Player}.
	 */
	public TradeList getBuyList()
	{
		return _buyList;
	}
	
	/**
	 * @return The Sell {@link TradeList} of this {@link Player}.
	 */
	public TradeList getSellList()
	{
		return _sellList;
	}
	
	/**
	 * @return The {@link ManufactureList} of this {@link Player}.
	 */
	public ManufactureList getManufactureList()
	{
		return _manufactureList;
	}
	
	/**
	 * @return The {@link OperateType} of this {@link Player}.
	 */
	public OperateType getOperateType()
	{
		return _operateType;
	}
	
	/**
	 * Set the {@link OperateType} of this {@link Player}.
	 * @param type : The new {@link OperateType} state to set.
	 */
	public void setOperateType(OperateType type)
	{
		_operateType = type;
	}
	
	/**
	 * @return True if this {@link Player} is set on any store mode, or false otherwise.
	 */
	public boolean isInStoreMode()
	{
		return _operateType == OperateType.BUY || _operateType == OperateType.SELL || _operateType == OperateType.PACKAGE_SELL || _operateType == OperateType.MANUFACTURE;
	}
	
	/**
	 * @return True if this {@link Player} is set on any store manage mode, or false otherwise.
	 */
	public boolean isInManageStoreMode()
	{
		return _operateType == OperateType.BUY_MANAGE || _operateType == OperateType.SELL_MANAGE || _operateType == OperateType.MANUFACTURE_MANAGE;
	}
	
	/**
	 * @return True if this {@link Player} can use dwarven recipes.
	 */
	public boolean hasDwarvenCraft()
	{
		return hasSkill(L2Skill.SKILL_CREATE_DWARVEN);
	}
	
	/**
	 * @return True if this {@link Player} can use common recipes.
	 */
	public boolean hasCommonCraft()
	{
		return hasSkill(L2Skill.SKILL_CREATE_COMMON);
	}
	
	/**
	 * Method used by regular leveling system.<br>
	 * Reward the {@link Player} with autoGet skills only, or if Config.AUTO_LEARN_SKILLS is activated, with all available skills.
	 */
	public void giveSkills()
	{
		if (Config.AUTO_LEARN_SKILLS)
			rewardSkills();
		else
		{
			// We reward all autoGet skills to this player, but don't store any on database.
			for (final GeneralSkillNode skill : getAvailableAutoGetSkills())
				addSkill(skill.getSkill(), false);
			
			// Remove the Lucky skill if level superior to 10.
			if (getStatus().getLevel() >= 10 && hasSkill(L2Skill.SKILL_LUCKY))
				removeSkill(L2Skill.SKILL_LUCKY, false);
			
			// Remove invalid skills.
			removeInvalidSkills();
			
			sendSkillList();
		}
	}
	
	/**
	 * Method used by admin commands, Config.AUTO_LEARN_SKILLS or class master.<br>
	 * Reward the {@link Player} with all available skills, being autoGet or general skills.
	 */
	public void rewardSkills()
	{
		// We reward all skills to the players, but don't store autoGet skills on the database.
		for (final GeneralSkillNode skill : getAllAvailableSkills())
			addSkill(skill.getSkill(), skill.getCost() != 0);
		
		// Remove the Lucky skill if level superior to 10.
		if (getStatus().getLevel() >= 10 && hasSkill(L2Skill.SKILL_LUCKY))
			removeSkill(L2Skill.SKILL_LUCKY, false);
		
		// Remove invalid skills.
		removeInvalidSkills();
		
		sendSkillList();
	}
	
	/**
	 * Delete all invalid {@link L2Skill}s for this {@link Player}.<br>
	 * <br>
	 * A skill is considered invalid when the level of obtention of the skill is superior to 9 compared to player level (expertise skill obtention level is compared to player level without any penalty).<br>
	 * <br>
	 * It is then either deleted, or level is refreshed.
	 */
	private void removeInvalidSkills()
	{
		if (getSkills().isEmpty())
			return;
		
		// Retrieve the player template skills, based on actual level (+9 for regular skills, unchanged for expertise).
		final Map<Integer, Optional<GeneralSkillNode>> availableSkills = getTemplate().getSkills().stream().filter(s -> s.getMinLvl() <= getStatus().getLevel() + ((s.getId() == L2Skill.SKILL_EXPERTISE) ? 0 : 9)).collect(Collectors.groupingBy(s -> s.getId(), Collectors.maxBy(COMPARE_SKILLS_BY_LVL)));
		
		for (final L2Skill skill : getSkills().values())
		{
			// Bother only with skills existing on template (spare temporary skills, items skills, etc).
			if (getTemplate().getSkills().stream().filter(s -> s.getId() == skill.getId()).count() == 0)
				continue;
			
			// The known skill doesn't exist on available skills ; we drop existing skill.
			final Optional<GeneralSkillNode> tempSkill = availableSkills.get(skill.getId());
			if (tempSkill == null)
			{
				removeSkill(skill.getId(), true);
				continue;
			}
			
			// Retrieve the skill and max level for enchant scenario.
			final GeneralSkillNode availableSkill = tempSkill.get();
			final int maxLevel = SkillTable.getInstance().getMaxLevel(skill.getId());
			
			// Case of enchanted skills.
			if (skill.getLevel() > maxLevel)
			{
				// Player level is inferior to 76, or available skill is a good candidate.
				if ((getStatus().getLevel() < 76 || availableSkill.getValue() < maxLevel) && skill.getLevel() > availableSkill.getValue())
					addSkill(availableSkill.getSkill(), true);
			}
			// We check if current known skill level is bigger than available skill level. If it's true, we override current skill with available skill.
			else if (skill.getLevel() > availableSkill.getValue())
				addSkill(availableSkill.getSkill(), true);
		}
	}
	
	/**
	 * Regive all skills which aren't saved to database, like Noble, Hero, Clan Skills.<br>
	 * <b>Do not call this on enterworld or char load.</b>.
	 */
	private void regiveTemporarySkills()
	{
		// Add noble skills if noble.
		if (isNoble())
			setNoble(true, false);
		
		// Add Hero skills if hero.
		if (isHero())
			setHero(true);
		
		if (isVIP())
			setVIP(true);
		
		// Add Clan skills.
		if (getClan() != null)
		{
			getClan().checkAndAddClanSkills(this);
			
			if (getClan().getLevel() >= Config.MINIMUM_CLAN_LEVEL && isClanLeader())
				addSiegeSkills();
		}
		
		// Reload passive skills from armors / jewels / weapons.
		getInventory().reloadEquippedItems();
		
		// Add Death Penalty Buff Level.
		if (_deathPenaltyBuffLevel > 0)
			addSkill(SkillTable.getInstance().getInfo(5076, _deathPenaltyBuffLevel), false);
	}
	
	public void addSiegeSkills()
	{
		for (final L2Skill sk : SkillTable.getInstance().getSiegeSkills(isNoble()))
			addSkill(sk, false);
	}
	
	public void removeSiegeSkills()
	{
		for (final L2Skill sk : SkillTable.getInstance().getSiegeSkills(isNoble()))
			removeSkill(sk.getId(), false);
	}
	
	/**
	 * @return a {@link List} of all available autoGet {@link GeneralSkillNode}s <b>of maximal level</b> for this {@link Player}.
	 */
	public List<GeneralSkillNode> getAvailableAutoGetSkills()
	{
		final List<GeneralSkillNode> result = new ArrayList<>();
		
		getTemplate().getSkills().stream().filter(s -> s.getMinLvl() <= getStatus().getLevel() && s.getCost() == 0).collect(Collectors.groupingBy(s -> s.getId(), Collectors.maxBy(COMPARE_SKILLS_BY_LVL))).forEach((i, s) ->
		{
			if (getSkillLevel(i) < s.get().getValue())
				result.add(s.get());
		});
		return result;
	}
	
	/**
	 * @return a {@link List} of available {@link GeneralSkillNode}s (only general) for this {@link Player}.
	 */
	public List<GeneralSkillNode> getAvailableSkills()
	{
		final List<GeneralSkillNode> result = new ArrayList<>();
		
		getTemplate().getSkills().stream().filter(s -> s.getMinLvl() <= getStatus().getLevel() && s.getCost() != 0).forEach(s ->
		{
			if (getSkillLevel(s.getId()) == s.getValue() - 1)
				result.add(s);
		});
		return result;
	}
	
	/**
	 * @return a {@link List} of all available {@link GeneralSkillNode}s (being general or autoGet) <b>of maximal level</b> for this {@link Player}.
	 */
	public List<GeneralSkillNode> getAllAvailableSkills()
	{
		final List<GeneralSkillNode> result = new ArrayList<>();
		
		getTemplate().getSkills().stream().filter(s -> s.getMinLvl() <= getStatus().getLevel()).collect(Collectors.groupingBy(s -> s.getId(), Collectors.maxBy(COMPARE_SKILLS_BY_LVL))).forEach((i, s) ->
		{
			if (getSkillLevel(i) < s.get().getValue())
				result.add(s.get());
		});
		return result;
	}
	
	/**
	 * Retrieve next lowest level skill to learn, based on current player level and skill sp cost.
	 * @return the required level for next {@link GeneralSkillNode} to learn for this {@link Player}.
	 */
	public int getRequiredLevelForNextSkill()
	{
		return getTemplate().getSkills().stream().filter(s -> s.getMinLvl() > getStatus().getLevel() && s.getCost() != 0).min(COMPARE_SKILLS_BY_MIN_LVL).map(s -> s.getMinLvl()).orElse(0);
	}
	
	/**
	 * Set the _clan object, _clanId, _clanLeader Flag and title of the Player.
	 * @param clan The Clan object which is used to feed Player values.
	 */
	public void setClan(Clan clan)
	{
		_clan = clan;
		setTitle("");
		
		if (clan == null)
		{
			_clanId = 0;
			_clanPrivileges = 0;
			_pledgeType = 0;
			_powerGrade = 0;
			_lvlJoinedAcademy = 0;
			_apprentice = 0;
			_sponsor = 0;
			return;
		}
		
		if (!clan.isMember(getObjectId()))
		{
			// char has been kicked from clan
			setClan(null);
			return;
		}
		
		_clanId = clan.getClanId();
	}
	
	/**
	 * @return The _clan object of the Player.
	 */
	public Clan getClan()
	{
		return _clan;
	}
	
	/**
	 * @return True if the Player is the leader of its clan.
	 */
	public boolean isClanLeader()
	{
		return _clan != null && getObjectId() == _clan.getLeaderId();
	}
	
	/**
	 * Reduce the number of arrows owned by the Player and send InventoryUpdate or ItemList (to unequip if the last arrow was consummed).
	 */
	@Override
	public void reduceArrowCount() // TODO: replace with a simple player.destroyItem...
	{
		final ItemInstance arrows = getSecondaryWeaponInstance();
		if (arrows == null)
			return;
		
		final InventoryUpdate iu = new InventoryUpdate();
		
		if (arrows.getCount() > 1)
		{
			synchronized (arrows)
			{
				arrows.changeCount(null, -1, this, null);
				arrows.setLastChange(ItemState.MODIFIED);
				
				iu.addModifiedItem(arrows);
				
				// could do also without saving, but let's save approx 1 of 10
				if (Rnd.get(10) < 1)
					arrows.updateDatabase();
				
				_inventory.refreshWeight();
			}
		}
		else
		{
			iu.addRemovedItem(arrows);
			
			// Destroy entire item and save to database
			_inventory.destroyItem("Consume", arrows, this, null);
		}
		sendPacket(iu);
	}
	
	/**
	 * Check if the arrow item exists on inventory and is already slotted ; if not, equip it.
	 */
	@Override
	public boolean checkAndEquipArrows()
	{
		// Retrieve arrows instance on player inventory.
		final ItemInstance arrows = getInventory().findArrowForBow(getActiveWeaponItem());
		if (arrows == null)
			return false;
		
		// Arrows are already equiped, don't bother.
		if (arrows.getLocation() == ItemLocation.PAPERDOLL)
			return true;
		
		// Equip arrows in left hand.
		getInventory().setPaperdollItem(Paperdoll.LHAND, arrows);
		
		// Send ItemList to this player to update left hand equipement
		sendPacket(new ItemList(this, false));
		
		return true;
	}
	
	/**
	 * Disarm the {@link Player}'s weapon.
	 * @param leftHandIncluded : If set to True, the left hand item is also disarmed.
	 * @return True if successful, false otherwise.
	 */
	public boolean disarmWeapon(boolean leftHandIncluded)
	{
		// Don't allow disarming a cursed weapon.
		if (isCursedWeaponEquipped())
			return false;
		
		// Cancel current attack, no matter what.
		getAttack().stop();
		
		InventoryUpdate iu = null;
		
		// Unequip the weapon.
		ItemInstance[] unequipped = getInventory().unequipItemInBodySlotAndRecord(Item.SLOT_R_HAND);
		if (!ArraysUtil.isEmpty(unequipped))
		{
			iu = new InventoryUpdate();
			for (final ItemInstance itm : unequipped)
				iu.addModifiedItem(itm);
			
			SystemMessage sm;
			if (unequipped[0].getEnchantLevel() > 0)
				sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED).addNumber(unequipped[0].getEnchantLevel()).addItemName(unequipped[0]);
			else
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISARMED).addItemName(unequipped[0]);
			
			sendPacket(sm);
		}
		
		// Unequip the shield.
		if (leftHandIncluded)
		{
			unequipped = getInventory().unequipItemInBodySlotAndRecord(Item.SLOT_L_HAND);
			if (!ArraysUtil.isEmpty(unequipped))
			{
				if (iu == null)
					iu = new InventoryUpdate();
				
				for (final ItemInstance itm : unequipped)
					iu.addModifiedItem(itm);
				
				SystemMessage sm;
				if (unequipped[0].getEnchantLevel() > 0)
					sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED).addNumber(unequipped[0].getEnchantLevel()).addItemName(unequipped[0]);
				else
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISARMED).addItemName(unequipped[0]);
				
				sendPacket(sm);
			}
		}
		
		// If InventoryUpdate packet is filled, it means either weapon or sidearm has been disarmed. An update is needed.
		if (iu != null)
		{
			sendPacket(iu);
			broadcastUserInfo();
		}
		
		return true;
	}
	
	public boolean mount(Summon pet)
	{
		if (!disarmWeapon(true))
			return false;
		
		forceRunStance();
		stopAllToggles();
		
		final Ride mount = new Ride(getObjectId(), Ride.ACTION_MOUNT, pet.getTemplate().getNpcId());
		setMount(pet.getNpcId(), pet.getStatus().getLevel(), mount.getMountType());
		
		_petTemplate = (PetTemplate) pet.getTemplate();
		_petData = _petTemplate.getPetDataEntry(pet.getStatus().getLevel());
		_mountObjectId = pet.getControlItemId();
		
		startFeed(pet.getNpcId());
		broadcastPacket(mount);
		
		// Notify self and others about speed change
		broadcastUserInfo();
		
		pet.unSummon(this);
		return true;
	}
	
	public boolean mount(int npcId, int controlItemId)
	{
		if (!disarmWeapon(true))
			return false;
		
		forceRunStance();
		stopAllToggles();
		
		final Ride mount = new Ride(getObjectId(), Ride.ACTION_MOUNT, npcId);
		
		setMount(npcId, getStatus().getLevel(), mount.getMountType());
		
		_petTemplate = (PetTemplate) NpcData.getInstance().getTemplate(npcId);
		_petData = _petTemplate.getPetDataEntry(getStatus().getLevel());
		_mountObjectId = controlItemId;
		
		broadcastPacket(mount);
		
		// Notify self and others about speed change
		broadcastUserInfo();
		
		startFeed(npcId);
		return true;
	}
	
	/**
	 * Test if this {@link Player} can mount the selected {@link Summon} or dismount if already mounted, and act accordingly.<br>
	 * <br>
	 * This method is used by both "Actions" panel "Mount/Dismount" button, and /mount /dismount usercommands.
	 * @param summon : The Summon to check.
	 */
	public void mountPlayer(Summon summon)
	{
		if (dismountPenalty)
			return;
		if (summon instanceof Pet && summon.isMountable() && !isMounted() && !isBetrayed())
		{
			// A strider cannot be ridden when dead.
			if (isDead())
			{
				sendPacket(SystemMessageId.STRIDER_CANT_BE_RIDDEN_WHILE_DEAD);
				return;
			}
			
			// A dead strider cannot be ridden.
			if (summon.isDead())
			{
				sendPacket(SystemMessageId.DEAD_STRIDER_CANT_BE_RIDDEN);
				return;
			}
			
			// A strider in battle cannot be ridden.
			if (summon.isInCombat() || summon.isRooted())
			{
				sendPacket(SystemMessageId.STRIDER_IN_BATLLE_CANT_BE_RIDDEN);
				return;
			}
			
			// A strider cannot be ridden while in battle.
			if (isInCombat() || isCursedWeaponEquipped())
			{
				sendPacket(SystemMessageId.STRIDER_CANT_BE_RIDDEN_WHILE_IN_BATTLE);
				return;
			}
			
			// A strider can be ridden only when standing.
			if (isSitting())
			{
				sendPacket(SystemMessageId.STRIDER_CAN_BE_RIDDEN_ONLY_WHILE_STANDING);
				return;
			}
			
			// You can't mount while fishing.
			if (isFishing())
			{
				sendPacket(SystemMessageId.CANNOT_DO_WHILE_FISHING_2);
				return;
			}
			
			// Distance check.
			if (!MathUtil.checkIfInRange(200, this, summon, true))
			{
				sendPacket(SystemMessageId.TOO_FAR_AWAY_FROM_STRIDER_TO_MOUNT);
				return;
			}
			
			// Pet is hungry.
			if (((Pet) summon).checkHungryState())
			{
				sendPacket(SystemMessageId.HUNGRY_STRIDER_NOT_MOUNT);
				return;
			}
			
			if (!summon.isDead() && !isMounted())
				mount(summon);
		}
		else if (isMounted())
		{
			// Wyvern mounted.
			if (getMountType() == 2)
			{
				// Invalid zone.
				if (isInsideZone(ZoneId.NO_LANDING))
				{
					sendPacket(SystemMessageId.NO_DISMOUNT_HERE);
					return;
				}
				
				// Unsafe fall height.
				if (Math.abs(getZ() - GeoEngine.getInstance().getHeight(getPosition())) > getTemplate().getSafeFallHeight(getAppearance().getSex()))
				{
					sendPacket(SystemMessageId.CANNOT_DISMOUNT_FROM_ELEVATION);
					return;
				}
			}
			
			// Hungry state.
			if (checkFoodState(_petTemplate.getHungryLimit()))
			{
				sendPacket(SystemMessageId.HUNGRY_STRIDER_NOT_MOUNT);
				return;
			}
			
			dismount();
		}
	}
	
	public void dismount()
	{
		sendPacket(new SetupGauge(GaugeColor.GREEN, 0));
		
		final int petId = _mountNpcId;
		
		setMount(0, 0, 0);
		stopFeed();
		
		broadcastPacket(new Ride(getObjectId(), Ride.ACTION_DISMOUNT, 0));
		
		_petTemplate = null;
		_petData = null;
		_mountObjectId = 0;
		
		storePetFood(petId);
		
		// Notify self and others about speed change
		broadcastUserInfo();
	}
	
	public void storePetFood(int petId)
	{
		if (_controlItemId != 0 && petId != 0)
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement("UPDATE pets SET fed=? WHERE item_obj_id = ?"))
			{
				ps.setInt(1, getCurrentFeed());
				ps.setInt(2, _controlItemId);
				ps.executeUpdate();
				
				_controlItemId = 0;
			}
			catch (final Exception e)
			{
				LOGGER.error("Couldn't store pet food data for {}.", e, _controlItemId);
			}
		}
	}
	
	public boolean summonPenalty = false;
	public boolean logoutnPenalty = false;
	public boolean dismountPenalty = false;
	public boolean alonePenalty = false;
	public boolean partyPenalty = false;
	public List<Integer> _forbItems = new ArrayList<>();
	public List<Integer> _forbSkills = new ArrayList<>();
	
	public String getHwid()
	{
		return null;
	}
	
	public void setForbItems(List<Integer> _items)
	{
		_forbItems = _items;
	}
	
	public void setForbSkills(List<Integer> _items)
	{
		_forbSkills = _items;
	}
	
	public void setSummonPenalty(boolean b)
	{
		summonPenalty = b;
	}
	
	public void setInNoLogoutArea(boolean b)
	{
		logoutnPenalty = b;
	}
	
	public void setInDismountZone(boolean b)
	{
		dismountPenalty = b;
	}
	
	public void setAlone(boolean b)
	{
		alonePenalty = b;
	}
	
	public void setPartyPenalty(boolean b)
	{
		partyPenalty = b;
	}
	
	protected class FeedTask implements Runnable
	{
		@Override
		public void run()
		{
			if (!isMounted())
			{
				stopFeed();
				return;
			}
			
			// Eat or return to pet control item.
			if (getCurrentFeed() > getFeedConsume())
				setCurrentFeed(getCurrentFeed() - getFeedConsume());
			else
			{
				// Keep temporary track of current fly state for future usage.
				final boolean wasFlying = isFlying();
				
				setCurrentFeed(0);
				stopFeed();
				dismount();
				sendPacket(SystemMessageId.OUT_OF_FEED_MOUNT_CANCELED);
				
				// If Player was wyvern mounted, then he is teleported to nearest town to avoid the falling damage.
				if (wasFlying)
					teleportTo(TeleportType.TOWN);
				
				return;
			}
			
			ItemInstance food = getInventory().getItemByItemId(_petTemplate.getFood1());
			if (food == null)
				food = getInventory().getItemByItemId(_petTemplate.getFood2());
			
			if (food != null && checkFoodState(_petTemplate.getAutoFeedLimit()))
			{
				final IItemHandler handler = ItemHandler.getInstance().getHandler(food.getEtcItem());
				if (handler != null)
				{
					handler.useItem(Player.this, food, false);
					sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY).addItemName(food));
				}
			}
		}
	}
	
	protected synchronized void startFeed(int npcId)
	{
		return;
		/*
		 * _canFeed = npcId > 0; if (!isMounted()) return; if (_summon != null) { setCurrentFeed(((Pet) _summon).getCurrentFed()); _controlItemId = _summon.getControlItemId(); sendPacket(new SetupGauge(GaugeColor.GREEN, getCurrentFeed() * 10000 / getFeedConsume(), _petData.getMaxMeal() * 10000 /
		 * getFeedConsume())); if (!isDead()) _mountFeedTask = ThreadPool.scheduleAtFixedRate(new FeedTask(), 10000, 10000); } else if (_canFeed) { setCurrentFeed(_petData.getMaxMeal()); sendPacket(new SetupGauge(GaugeColor.GREEN, getCurrentFeed() * 10000 / getFeedConsume(), _petData.getMaxMeal() *
		 * 10000 / getFeedConsume())); if (!isDead()) _mountFeedTask = ThreadPool.scheduleAtFixedRate(new FeedTask(), 10000, 10000); }
		 */
	}
	
	protected synchronized void stopFeed()
	{
		if (_mountFeedTask != null)
		{
			_mountFeedTask.cancel(false);
			_mountFeedTask = null;
		}
	}
	
	public PetTemplate getPetTemplate()
	{
		return _petTemplate;
	}
	
	public PetDataEntry getPetDataEntry()
	{
		return _petData;
	}
	
	public int getCurrentFeed()
	{
		return _curFeed;
	}
	
	protected int getFeedConsume()
	{
		return (isInCombat()) ? _petData.getMountMealInBattle() : _petData.getMountMealInNormal();
	}
	
	public void setCurrentFeed(int num)
	{
		_curFeed = Math.min(num, _petData.getMaxMeal());
		
		sendPacket(new SetupGauge(GaugeColor.GREEN, getCurrentFeed() * 10000 / getFeedConsume(), _petData.getMaxMeal() * 10000 / getFeedConsume()));
	}
	
	/**
	 * @param state : The state to check (can be autofeed, hungry or unsummon).
	 * @return true if the limit is reached, false otherwise or if there is no need to feed.
	 */
	public boolean checkFoodState(double state)
	{
		return _canFeed && getCurrentFeed() < _petData.getMaxMeal() * state;
	}
	
	public void setUptime(long time)
	{
		_uptime = time;
	}
	
	public long getUptime()
	{
		return System.currentTimeMillis() - _uptime;
	}
	
	/**
	 * Return True if the Player is invulnerable.
	 */
	@Override
	public boolean isInvul()
	{
		return super.isInvul() || isSpawnProtected();
	}
	
	/**
	 * Return True if the Player has a Party in progress.
	 */
	@Override
	public boolean isInParty()
	{
		return _party != null;
	}
	
	/**
	 * Set the _party object of the Player (without joining it).
	 * @param party The object.
	 */
	public void setParty(Party party)
	{
		_party = party;
	}
	
	/**
	 * Return the _party object of the Player.
	 */
	@Override
	public Party getParty()
	{
		return _party;
	}
	
	public LootRule getLootRule()
	{
		return _lootRule;
	}
	
	public void setLootRule(LootRule lootRule)
	{
		_lootRule = lootRule;
	}
	
	/**
	 * Return True if the Player is a GM.
	 */
	@Override
	public boolean isGM()
	{
		return getAccessLevel().isGm();
	}
	
	/**
	 * Set the {@link AccessLevel} of this {@link Player}.
	 * <ul>
	 * <li>If invalid, set the default user access level 0.</li>
	 * <li>If superior to 0, it means it's a special access.</li>
	 * </ul>
	 * @param level : The level to set.
	 */
	public void setAccessLevel(int level)
	{
		// Retrieve the AccessLevel. Even if not existing, it returns user level.
		AccessLevel accessLevel = AdminData.getInstance().getAccessLevel(level);
		if (accessLevel == null)
		{
			LOGGER.warn("An invalid access level {} has been granted for {}, therefore it has been reset.", level, toString());
			accessLevel = AdminData.getInstance().getAccessLevel(0);
		}
		
		_accessLevel = accessLevel;
		
		if (level > 0)
		{
			// For level lower or equal to user, we don't apply AccessLevel name as title.
			setTitle(accessLevel.getName());
			
			// We log master access.
			if (level == AdminData.getInstance().getMasterAccessLevel())
				LOGGER.info("{} has logged in with Master access level.", getName());
		}
		
		// We refresh GMList if the access level is GM.
		if (accessLevel.isGm())
		{
			// A little hack to avoid Enterworld config to be replaced.
			if (!AdminData.getInstance().isRegisteredAsGM(this))
				AdminData.getInstance().addGm(this, false);
		}
		else
			AdminData.getInstance().deleteGm(this);
		
		// getAppearance().setNameColor(accessLevel.getNameColor());
		getAppearance().setNameColor(getMemos().getInteger("name_color", accessLevel.getNameColor()));
		// getAppearance().setTitleColor(accessLevel.getTitleColor());
		getAppearance().setTitleColor(getMemos().getInteger("title_color", accessLevel.getTitleColor()));
		// getStatus().setLevel(getMemos().getInteger("player_lvl",accessLevel.getLevel()));
		broadcastUserInfo();
		
		PlayerInfoTable.getInstance().updatePlayerData(this, true);
	}
	
	public void setAccountAccesslevel(int level)
	{
		LoginServerThread.getInstance().sendAccessLevel(getAccountName(), level);
	}
	
	/**
	 * @return the _accessLevel of the Player.
	 */
	public AccessLevel getAccessLevel()
	{
		return _accessLevel;
	}
	
	public final void setEnterWorldLoc(int x, int y, int z)
	{
		_enterWorld = new Location(x, y, z);
	}
	
	public final ExServerPrimitive getDebugPacket(String name)
	{
		return _debug.computeIfAbsent(name, p -> new ExServerPrimitive(name, _enterWorld));
	}
	
	public final void clearDebugPackets()
	{
		_debug.values().stream().peek(ExServerPrimitive::reset).forEach(esp -> esp.sendTo(this));
	}
	
	/**
	 * Update Stats of the Player client side by sending UserInfo/StatusUpdate to this Player and CharInfo/StatusUpdate to all Player in its _KnownPlayers (broadcast).
	 * @param broadcastType
	 */
	public void updateAndBroadcastStatus(int broadcastType)
	{
		refreshWeightPenalty();
		refreshExpertisePenalty();
		
		if (broadcastType == 1)
			sendPacket(new UserInfo(this));
		else if (broadcastType == 2)
			broadcastUserInfo();
	}
	
	/**
	 * Send StatusUpdate packet with Karma to the Player and all Player to inform (broadcast).
	 */
	public void broadcastKarma()
	{
		final StatusUpdate su = new StatusUpdate(this);
		su.addAttribute(StatusType.KARMA, getKarma());
		sendPacket(su);
		
		if (_summon != null)
			sendPacket(new RelationChanged(_summon, getRelation(this), false));
		
		broadcastRelationsChanges();
	}
	
	/**
	 * Set the online Flag to True or False and update the characters table of the database with online status and lastAccess (called when login and logout).
	 * @param isOnline
	 * @param updateInDb
	 */
	public void setOnlineStatus(boolean isOnline, boolean updateInDb)
	{
		if (_isOnline != isOnline)
			_isOnline = isOnline;
		
		// Update the characters table of the database with online status and lastAccess (called when login and logout)
		if (updateInDb)
			updateOnlineStatus();
	}
	
	public void setIsIn7sDungeon(boolean isIn7sDungeon)
	{
		_isIn7sDungeon = isIn7sDungeon;
	}
	
	/**
	 * Update the characters table of the database with online status and lastAccess of this Player (called when login and logout).
	 */
	public void updateOnlineStatus()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE obj_id=?"))
		{
			ps.setInt(1, isOnlineInt());
			ps.setLong(2, System.currentTimeMillis());
			ps.setInt(3, getObjectId());
			ps.execute();
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't set player online status.", e);
		}
	}
	
	/**
	 * Retrieve a Player from the characters table of the database.
	 * <ul>
	 * <li>Retrieve the Player from the characters table of the database</li>
	 * <li>Set the x,y,z position of the Player and make it invisible</li>
	 * <li>Update the overloaded status of the Player</li>
	 * </ul>
	 * @param objectId Identifier of the object to initialized
	 * @return The Player loaded from the database
	 */
	public static Player restore(int objectId)
	{
		Player player = null;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_CHARACTER))
		{
			ps.setInt(1, objectId);
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final int activeClassId = rs.getInt("classid");
					final PlayerTemplate template = PlayerData.getInstance().getTemplate(activeClassId);
					final Appearance app = new Appearance(rs.getByte("face"), rs.getByte("hairColor"), rs.getByte("hairStyle"), Sex.VALUES[rs.getInt("sex")]);
					
					player = new Player(objectId, template, rs.getString("account_name"), app);
					player.setName(rs.getString("char_name"));
					player._lastAccess = rs.getLong("lastAccess");
					
					player.getStatus().setExp(rs.getLong("exp"));
					player.getStatus().setLevel(rs.getByte("level"));
					player.getStatus().setSp(rs.getInt("sp"));
					
					player.setExpBeforeDeath(rs.getLong("expBeforeDeath"));
					player.setWantsPeace(rs.getInt("wantspeace") == 1);
					player.setKarma(rs.getInt("karma"));
					player.setPvpKills(rs.getInt("pvpkills"));
					player.setPkKills(rs.getInt("pkkills"));
					player.setOnlineTime(rs.getLong("onlinetime"));
					player.setNoble(rs.getInt("nobless") == 1, false);
					player.setVIPUntil(rs.getLong("vipuntil"));
					
					player.setClanJoinExpiryTime(rs.getLong("clan_join_expiry_time"));
					if (player.getClanJoinExpiryTime() < System.currentTimeMillis())
						player.setClanJoinExpiryTime(0);
					
					player.setClanCreateExpiryTime(rs.getLong("clan_create_expiry_time"));
					if (player.getClanCreateExpiryTime() < System.currentTimeMillis())
						player.setClanCreateExpiryTime(0);
					
					player.setPowerGrade(rs.getInt("power_grade"));
					player.setPledgeType(rs.getInt("subpledge"));
					
					final int clanId = rs.getInt("clanid");
					if (clanId > 0)
						player.setClan(ClanTable.getInstance().getClan(clanId));
					
					if (player.getClan() != null)
					{
						if (player.getClan().getLeaderId() != player.getObjectId())
						{
							if (player.getPowerGrade() == 0)
								player.setPowerGrade(5);
							
							player.setClanPrivileges(player.getClan().getPrivilegesByRank(player.getPowerGrade()));
						}
						else
						{
							player.setClanPrivileges(Clan.CP_ALL);
							player.setPowerGrade(1);
						}
					}
					else
						player.setClanPrivileges(Clan.CP_NOTHING);
					
					player.setDeleteTimer(rs.getLong("deletetime"));
					player.setTitle(rs.getString("title"));
					player.setAccessLevel(rs.getInt("accesslevel"));
					player.setUptime(System.currentTimeMillis());
					player.setRecomHave(rs.getInt("rec_have"));
					player.setRecomLeft(rs.getInt("rec_left"));
					
					player._classIndex = 0;
					try
					{
						player.setBaseClass(rs.getInt("base_class"));
					}
					catch (final Exception e)
					{
						player.setBaseClass(activeClassId);
					}
					
					// Restore Subclass Data (cannot be done earlier in function)
					if (restoreSubClassData(player) && activeClassId != player.getBaseClass())
					{
						for (final SubClass subClass : player.getSubClasses().values())
							if (subClass.getClassId() == activeClassId)
								player._classIndex = subClass.getClassIndex();
					}
					
					// Subclass in use but doesn't exist in DB - a possible subclass cheat has been attempted. Switching to base class.
					if (player.getClassIndex() == 0 && activeClassId != player.getBaseClass())
						player.setClassId(player.getBaseClass());
					else
						player._activeClass = activeClassId;
					
					player.setApprentice(rs.getInt("apprentice"));
					player.setSponsor(rs.getInt("sponsor"));
					player.setLvlJoinedAcademy(rs.getInt("lvl_joined_academy"));
					player.setIsIn7sDungeon(rs.getInt("isin7sdungeon") == 1);
					
					player.getPunishment().load(rs.getInt("punish_level"), rs.getLong("punish_timer"));
					
					CursedWeaponManager.getInstance().checkPlayer(player);
					
					player.setAllianceWithVarkaKetra(rs.getInt("varka_ketra_ally"));
					
					player.setDeathPenaltyBuffLevel(rs.getInt("death_penalty_level"));
					
					// Set the position of the Player.
					player.getPosition().set(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"), rs.getInt("heading"));
					
					// Set Hero status if it applies
					if (HeroManager.getInstance().isActiveHero(objectId))
						player.setHero(true);
					
					// Set pledge class rank.
					player.setPledgeClass(ClanMember.calculatePledgeClass(player));
					
					// Retrieve from the database all secondary data of this Player and reward expertise/lucky skills if necessary.
					// Note that Clan, Noblesse and Hero skills are given separately and not here.
					player.restoreCharData();
					player.giveSkills();
					
					// buff and status icons
					if (Config.STORE_SKILL_COOLTIME)
						player.restoreEffects();
					
					// Restore current CP, HP and MP values
					final double currentHp = rs.getDouble("curHp");
					
					player.getStatus().setCpHpMp(rs.getDouble("curCp"), currentHp, rs.getDouble("curMp"));
					
					if (currentHp < 0.5)
					{
						player.setIsDead(true);
						player.getStatus().stopHpMpRegeneration();
					}
					
					PlayerVariables.loadVariables(player);
					TournamentManager.getInstance().onPlayerEnter(player);
					
					// Restore pet if it exists in the world.
					final Pet pet = World.getInstance().getPet(player.getObjectId());
					if (pet != null)
					{
						player.setSummon(pet);
						pet.setOwner(player);
					}
					
					player.refreshWeightPenalty();
					player.refreshExpertisePenalty();
					player.refreshHennaList();
					
					player.restoreFriendList();
					
					// Retrieve the name and ID of the other characters assigned to this account.
					try (PreparedStatement ps2 = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id<>?"))
					{
						ps2.setString(1, player._accountName);
						ps2.setInt(2, objectId);
						
						try (ResultSet rs2 = ps2.executeQuery())
						{
							while (rs2.next())
								player.getAccountChars().put(rs2.getInt("obj_Id"), rs2.getString("char_name"));
						}
					}
					break;
				}
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't restore player data.", e);
		}
		
		return player;
	}
	
	public Forum getMemo()
	{
		if (_forumMemo == null)
			_forumMemo = CommunityBoard.getInstance().getOrCreateForum(ForumType.MEMO, ForumAccess.ALL, getObjectId());
		
		return _forumMemo;
	}
	
	/**
	 * Restores sub-class data for the Player, used to check the current class index for the character.
	 * @param player The player to make checks on.
	 * @return true if successful.
	 */
	private static boolean restoreSubClassData(Player player)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_CHAR_SUBCLASSES))
		{
			ps.setInt(1, player.getObjectId());
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final SubClass subClass = new SubClass(rs.getInt("class_id"), rs.getInt("class_index"), rs.getLong("exp"), rs.getInt("sp"), rs.getByte("level"));
					
					// Enforce the correct indexing of _subClasses against their class indexes.
					player.getSubClasses().put(subClass.getClassIndex(), subClass);
				}
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't restore subclasses for {}.", e, player.getName());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Restores secondary data for the Player, based on the current class index.
	 */
	private void restoreCharData()
	{
		// Retrieve from the database all skills of this Player and add them to _skills.
		restoreSkills();
		
		// Retrieve from the database all macroses of this Player and add them to _macroses.
		_macroList.restore();
		
		// Retrieve from the database the recipe book of this Player.
		if (!isSubClassActive())
			_recipeBook.restore();
		
		// Retrieve from the database all shortCuts of this Player and add them to _shortCuts.
		_shortcutList.restore();
		
		// Retrieve from the database all henna of this Player and add them to _henna.
		_hennaList.restore();
		
		// Retrieve from the database all recom data of this Player and add them to _recomChars.
		restoreRecom();
		
		// Retrieve from the database all quest states and variables for this Player and add them to _quests.
		_questList.restore();
	}
	
	/**
	 * Update Player stats in the characters table of the database.
	 * @param storeActiveEffects
	 */
	public synchronized void store(boolean storeActiveEffects)
	{
		storeCharBase();
		storeCharSub();
		storeEffect(storeActiveEffects);
	}
	
	public void store()
	{
		store(true);
	}
	
	public void storeCharBase()
	{
		// Get the exp, level, and sp of base class to store in base table
		final int currentClassIndex = getClassIndex();
		
		_classIndex = 0;
		
		final long exp = getStatus().getExp();
		final int level = getStatus().getLevel();
		final int sp = getStatus().getSp();
		
		_classIndex = currentClassIndex;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CHARACTER))
		{
			ps.setInt(1, level);
			ps.setInt(2, getStatus().getMaxHp());
			ps.setDouble(3, getStatus().getHp());
			ps.setInt(4, getStatus().getMaxCp());
			ps.setDouble(5, getStatus().getCp());
			ps.setInt(6, getStatus().getMaxMp());
			ps.setDouble(7, getStatus().getMp());
			ps.setInt(8, getAppearance().getFace());
			ps.setInt(9, getAppearance().getHairStyle());
			ps.setInt(10, getAppearance().getHairColor());
			ps.setInt(11, getAppearance().getSex().ordinal());
			ps.setInt(12, getHeading());
			
			if (!isInObserverMode())
			{
				ps.setInt(13, getX());
				ps.setInt(14, getY());
				ps.setInt(15, getZ());
			}
			else
			{
				ps.setInt(13, _savedLocation.getX());
				ps.setInt(14, _savedLocation.getY());
				ps.setInt(15, _savedLocation.getZ());
			}
			
			ps.setLong(16, exp);
			ps.setLong(17, getExpBeforeDeath());
			ps.setInt(18, sp);
			ps.setInt(19, getKarma());
			ps.setInt(20, getPvpKills());
			ps.setInt(21, getPkKills());
			ps.setInt(22, getClanId());
			ps.setInt(23, getRace().ordinal());
			ps.setInt(24, getClassId().getId());
			ps.setLong(25, getDeleteTimer());
			ps.setString(26, getTitle());
			ps.setInt(27, getAccessLevel().getLevel());
			ps.setInt(28, isOnlineInt());
			ps.setInt(29, isIn7sDungeon() ? 1 : 0);
			ps.setInt(30, getClanPrivileges());
			ps.setInt(31, wantsPeace() ? 1 : 0);
			ps.setInt(32, getBaseClass());
			
			long totalOnlineTime = _onlineTime;
			if (_onlineBeginTime > 0)
				totalOnlineTime += (System.currentTimeMillis() - _onlineBeginTime) / 1000;
			
			ps.setLong(33, totalOnlineTime);
			ps.setInt(34, _punishment.getType().ordinal());
			ps.setLong(35, _punishment.getTimer());
			ps.setInt(36, isNoble() ? 1 : 0);
			ps.setLong(37, getPowerGrade());
			ps.setInt(38, getPledgeType());
			ps.setInt(39, getLvlJoinedAcademy());
			ps.setLong(40, getApprentice());
			ps.setLong(41, getSponsor());
			ps.setInt(42, getAllianceWithVarkaKetra());
			ps.setLong(43, getClanJoinExpiryTime());
			ps.setLong(44, getClanCreateExpiryTime());
			ps.setString(45, getName());
			ps.setLong(46, getDeathPenaltyBuffLevel());
			ps.setInt(47, isVIP() ? 1 : 0);
			ps.setLong(48, getVIPUntil());
			ps.setInt(49, getObjectId());
			
			ps.execute();
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't store player base data.", e);
		}
	}
	
	private void storeCharSub()
	{
		if (_subClasses.isEmpty())
			return;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CHAR_SUBCLASS))
		{
			for (final SubClass subClass : _subClasses.values())
			{
				ps.setLong(1, subClass.getExp());
				ps.setInt(2, subClass.getSp());
				ps.setInt(3, subClass.getLevel());
				ps.setInt(4, subClass.getClassId());
				ps.setInt(5, getObjectId());
				ps.setInt(6, subClass.getClassIndex());
				ps.addBatch();
			}
			ps.executeBatch();
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't store subclass data.", e);
		}
	}
	
	private void storeEffect(boolean storeEffects)
	{
		if (!Config.STORE_SKILL_COOLTIME)
			return;
		
		try (Connection con = ConnectionPool.getConnection())
		{
			// Delete all stored effects.
			try (PreparedStatement ps = con.prepareStatement(DELETE_SKILL_SAVE))
			{
				ps.setInt(1, getObjectId());
				ps.setInt(2, getClassIndex());
				ps.executeUpdate();
			}
			
			int index = 0;
			final List<Integer> storedSkills = new ArrayList<>();
			
			try (PreparedStatement ps = con.prepareStatement(ADD_SKILL_SAVE))
			{
				// Store all effects with their remaining reuse delays. 'restore_type'= 0.
				if (storeEffects)
				{
					for (final AbstractEffect effect : getAllEffects())
					{
						// Don't bother with HoT effects.
						if (effect.getEffectType() == EffectType.HEAL_OVER_TIME)
							continue;
						
						// Don't bother to reprocess the same skill id/level pair.
						final L2Skill skill = effect.getSkill();
						if (storedSkills.contains(skill.getReuseHashCode()))
							continue;
						
						// Store the skill, to avoid to process it twice.
						storedSkills.add(skill.getReuseHashCode());
						
						// Don't bother about herbs and toggles.
						if (effect.isHerbEffect() || skill.isToggle())
							continue;
						
						ps.setInt(1, getObjectId());
						ps.setInt(2, skill.getId());
						ps.setInt(3, skill.getLevel());
						ps.setInt(4, effect.getCount());
						ps.setInt(5, effect.getTime());
						
						final Timestamp timestamp = _reuseTimeStamps.get(skill.getReuseHashCode());
						if (timestamp != null && timestamp.hasNotPassed())
						{
							ps.setLong(6, timestamp.getReuse());
							ps.setDouble(7, timestamp.getStamp());
						}
						else
						{
							ps.setLong(6, 0);
							ps.setDouble(7, 0);
						}
						
						ps.setInt(8, 0);
						ps.setInt(9, getClassIndex());
						ps.setInt(10, ++index);
						ps.addBatch();
					}
				}
				
				// Store the leftover reuse delays. 'restore_type' 1.
				for (final Map.Entry<Integer, Timestamp> entry : _reuseTimeStamps.entrySet())
				{
					// Don't bother to reprocess the same skill id/level pair.
					final int hash = entry.getKey();
					if (storedSkills.contains(hash))
						continue;
					
					final Timestamp timestamp = entry.getValue();
					if (timestamp != null && timestamp.hasNotPassed())
					{
						// Store the skill, to avoid to process it twice.
						storedSkills.add(hash);
						
						ps.setInt(1, getObjectId());
						ps.setInt(2, timestamp.getId());
						ps.setInt(3, timestamp.getValue());
						ps.setInt(4, -1);
						ps.setInt(5, -1);
						ps.setLong(6, timestamp.getReuse());
						ps.setDouble(7, timestamp.getStamp());
						ps.setInt(8, 1);
						ps.setInt(9, getClassIndex());
						ps.setInt(10, ++index);
						ps.addBatch();
					}
				}
				
				ps.executeBatch();
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't store player effects.", e);
		}
	}
	
	/**
	 * @return True if the Player is online.
	 */
	public boolean isOnline()
	{
		return _isOnline;
	}
	
	/**
	 * @return an int interpretation of online status.
	 */
	public int isOnlineInt()
	{
		if (_isOnline && getClient() != null)
			return getClient().isDetached() ? 2 : 1;
		
		return 0;
	}
	
	public boolean isIn7sDungeon()
	{
		return _isIn7sDungeon;
	}
	
	/**
	 * Add a {@link L2Skill} and its Func objects to the calculator set of the {@link Player}. Don't refresh shortcuts.
	 * @param newSkill : The skill to add.
	 * @param store : If true, we save the skill on database.
	 * @return true if the skill has been successfully added.
	 * @see Player#addSkill(L2Skill, boolean, boolean)
	 */
	public boolean addSkill(L2Skill newSkill, boolean store)
	{
		return addSkill(newSkill, store, false);
	}
	
	/**
	 * Add a {@link L2Skill} and its Func objects to the calculator set of the {@link Player}.<BR>
	 * <ul>
	 * <li>Replace or add oldSkill by newSkill (only if oldSkill is different than newSkill)</li>
	 * <li>If an old skill has been replaced, remove all its Func objects of Creature calculator set</li>
	 * <li>Add Func objects of newSkill to the calculator set of the Creature</li>
	 * </ul>
	 * @param newSkill : The skill to add.
	 * @param store : If true, we save the skill on database.
	 * @param updateShortcuts : If true, we refresh all shortcuts associated to that skill (should be only called when skill upgrades, either manually or by trainer).
	 * @return true if the skill has been successfully added.
	 */
	public boolean addSkill(L2Skill newSkill, boolean store, boolean updateShortcuts)
	{
		// New skill is null, abort.
		if (newSkill == null)
			return false;
		
		// Search the old skill. We test if it's different than the new one. If yes, we abort the operation.
		final L2Skill oldSkill = getSkills().get(newSkill.getId());
		if (oldSkill != null && oldSkill.equals(newSkill))
			return false;
		
		// The 2 skills were different (or old wasn't existing). We can refresh the map.
		getSkills().put(newSkill.getId(), newSkill);
		
		// If an old skill has been replaced, remove all its Func objects
		if (oldSkill != null)
		{
			// if skill came with another one, we should delete the other one too.
			if (oldSkill.triggerAnotherSkill())
				removeSkill(oldSkill.getTriggeredId(), false);
			
			removeStatsByOwner(oldSkill);
		}
		
		// Add Func objects of newSkill to the calculator set of the Creature
		addStatFuncs(newSkill.getStatFuncs(this));
		
		// Test and delete chance skill if found.
		if (oldSkill != null && getChanceSkills() != null)
			removeChanceSkill(oldSkill.getId());
		
		// If new skill got a chance, trigger it.
		if (newSkill.isChance())
			addChanceTrigger(newSkill);
		
		// Add or update the skill in the database.
		if (store)
			storeSkill(newSkill, -1);
		
		// Update shortcuts.
		if (updateShortcuts)
			getShortcutList().refreshShortcuts(newSkill.getId(), newSkill.getLevel(), ShortcutType.SKILL);
		
		return true;
	}
	
	/**
	 * Remove a {@link L2Skill} from this {@link Player}. If parameter store is true, we also remove it from database and update shortcuts.
	 * @param skillId : The skill identifier to remove.
	 * @param store : If true, we delete the skill from database.
	 * @return the L2Skill removed or null if it couldn't be removed.
	 */
	public L2Skill removeSkill(int skillId, boolean store)
	{
		return removeSkill(skillId, store, true);
	}
	
	/**
	 * Remove a {@link L2Skill} from this {@link Player}. If parameter store is true, we also remove it from database and update shortcuts.
	 * @param skillId : The skill identifier to remove.
	 * @param store : If true, we delete the skill from database.
	 * @param removeEffect : If true, we remove the associated effect if existing.
	 * @return the L2Skill removed or null if it couldn't be removed.
	 */
	public L2Skill removeSkill(int skillId, boolean store, boolean removeEffect)
	{
		// Remove the skill from the Creature _skills
		final L2Skill oldSkill = getSkills().remove(skillId);
		if (oldSkill == null)
			return null;
		
		// this is just a fail-safe againts buggers and gm dummies...
		if (oldSkill.triggerAnotherSkill() && oldSkill.getTriggeredId() > 0)
			removeSkill(oldSkill.getTriggeredId(), false);
		
		// Stop casting if this skill is used right now
		if (getCast().getCurrentSkill() != null && skillId == getCast().getCurrentSkill().getId())
			getCast().stop();
		
		// Remove all its Func objects from the Creature calculator set
		if (removeEffect)
		{
			removeStatsByOwner(oldSkill);
			stopSkillEffects(skillId);
		}
		
		if (oldSkill.isChance() && getChanceSkills() != null)
			removeChanceSkill(skillId);
		
		if (store)
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(DELETE_SKILL_FROM_CHAR))
			{
				ps.setInt(1, skillId);
				ps.setInt(2, getObjectId());
				ps.setInt(3, getClassIndex());
				ps.execute();
			}
			catch (final Exception e)
			{
				LOGGER.error("Couldn't delete player skill.", e);
			}
			
			// Don't busy testing shortcuts if skill was a passive skill.
			if (!oldSkill.isPassive())
				getShortcutList().deleteShortcuts(skillId, ShortcutType.SKILL);
		}
		return oldSkill;
	}
	
	/**
	 * Insert or update a {@link Player} skill in the database.<br>
	 * If newClassIndex > -1, the skill will be stored with that class index, not the current one.
	 * @param skill : The skill to add or update (if updated, only the level is refreshed).
	 * @param classIndex : The current class index to set, or current if none is found.
	 */
	private void storeSkill(L2Skill skill, int classIndex)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(ADD_OR_UPDATE_SKILL))
		{
			ps.setInt(1, getObjectId());
			ps.setInt(2, skill.getId());
			ps.setInt(3, skill.getLevel());
			ps.setInt(4, (classIndex > -1) ? classIndex : _classIndex);
			ps.executeUpdate();
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't store player skill.", e);
		}
	}
	
	/**
	 * Restore all skills from database for this {@link Player} and feed getSkills().
	 */
	private void restoreSkills()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_SKILLS_FOR_CHAR))
		{
			ps.setInt(1, getObjectId());
			ps.setInt(2, getClassIndex());
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
					addSkill(SkillTable.getInstance().getInfo(rs.getInt("skill_id"), rs.getInt("skill_level")), false);
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't restore player skills.", e);
		}
	}
	
	/**
	 * Restore {@link L2Skill} effects of this {@link Player} from the database.
	 */
	public void restoreEffects()
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(RESTORE_SKILL_SAVE))
			{
				ps.setInt(1, getObjectId());
				ps.setInt(2, getClassIndex());
				
				try (ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
					{
						final int effectCount = rs.getInt("effect_count");
						final int effectCurTime = rs.getInt("effect_cur_time");
						final long reuseDelay = rs.getLong("reuse_delay");
						final long systime = rs.getLong("systime");
						final int restoreType = rs.getInt("restore_type");
						
						final L2Skill skill = SkillTable.getInstance().getInfo(rs.getInt("skill_id"), rs.getInt("skill_level"));
						if (skill == null)
							continue;
						
						final long remainingTime = systime - System.currentTimeMillis();
						if (remainingTime > 10)
						{
							disableSkill(skill, remainingTime);
							addTimeStamp(skill, reuseDelay, systime);
						}
						
						// Restore Type 1 : The remaning skills lost effect upon logout but were still under a high reuse delay.
						if (restoreType > 0)
							continue;
						
						// Restore Type 0 : These skills were still in effect on the character upon logout. Some of which were self casted and might still have a long reuse delay which also is restored.
						if (skill.hasEffects())
						{
							for (final EffectTemplate template : skill.getEffectTemplates())
							{
								final AbstractEffect effect = template.getEffect(this, this, skill);
								if (effect != null)
								{
									effect.setCount(effectCount);
									effect.setTime(effectCurTime);
									effect.scheduleEffect();
								}
							}
						}
					}
				}
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_SKILL_SAVE))
			{
				ps.setInt(1, getObjectId());
				ps.setInt(2, getClassIndex());
				ps.executeUpdate();
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't restore effects.", e);
		}
	}
	
	/**
	 * Restore Recommendation data of this {@link Player} from the database.
	 */
	private void restoreRecom()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_CHAR_RECOMS))
		{
			ps.setInt(1, getObjectId());
			
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
					_recomChars.add(rset.getInt("target_id"));
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't restore recommendations.", e);
		}
	}
	
	/**
	 * @return the {@link HennaList} of this {@link Player}.
	 */
	public HennaList getHennaList()
	{
		return _hennaList;
	}
	
	/**
	 * Refresh the {@link HennaList} of this {@link Player}.
	 */
	public void refreshHennaList()
	{
		_hennaList.recalculateStats();
		
		sendPacket(new HennaInfo(this));
	}
	
	/**
	 * @param objectId : The looter object to make checks on.
	 * @return true if the active player is the looter or in the same party or command channel than looter objectId.
	 */
	public boolean isLooterOrInLooterParty(int objectId)
	{
		if (objectId == getObjectId())
			return true;
		
		final Player looter = World.getInstance().getPlayer(objectId);
		if (looter == null)
			return false;
		
		if (_party == null)
			return false;
		
		final CommandChannel channel = _party.getCommandChannel();
		return (channel != null) ? channel.containsPlayer(looter) : _party.containsPlayer(looter);
	}
	
	public boolean canCastBeneficialSkillOnPlayable(Playable target, L2Skill skill, boolean isCtrlPressed)
	{
		// You can do beneficial skills on yourself anytime
		if (this == target)
			return true;
		
		final Player targetPlayer = target.getActingPlayer();
		// No checks for players in Arena
		if (isInsideZone(ZoneId.PVP) && targetPlayer.isInsideZone(ZoneId.PVP))
			return true;
		
		// No checks for players in Olympiad
		if (isInOlympiadMode() && targetPlayer.isInOlympiadMode() && getOlympiadGameId() == targetPlayer.getOlympiadGameId())
			return true;
		
		// No checks for players in Duel
		if (isInDuel() && targetPlayer.isInDuel() && getDuelId() == targetPlayer.getDuelId())
			return true;
		
		final boolean sameParty = (isInParty() && targetPlayer.isInParty() && getParty().getLeader() == targetPlayer.getParty().getLeader());
		final boolean sameCommandChannel = (isInParty() && targetPlayer.isInParty() && getParty().getCommandChannel() != null && getParty().getCommandChannel().containsPlayer(targetPlayer));
		final boolean sameClan = (getClanId() > 0 && getClanId() == targetPlayer.getClanId());
		final boolean sameAlliance = (getAllyId() > 0 && getAllyId() == targetPlayer.getAllyId());
		if (sameParty || sameCommandChannel || sameClan || sameAlliance)
			return true;
		
		// If the target not from the same CC/party/alliance/clan is flagged / PK, you can buff with isCtrlPressed.
		if (targetPlayer.getPvpFlag() > 0 || targetPlayer.getKarma() > 0)
			return isCtrlPressed;
		
		// If the target not from the same CC/party/alliance/clan is white, it may be freely buffed
		return true;
	}
	
	/**
	 * @return True if the Player is a Mage (based on class templates).
	 */
	public boolean isMageClass()
	{
		return getClassId().getType() != ClassType.FIGHTER;
	}
	
	public boolean isMounted()
	{
		return _mountType > 0;
	}
	
	/**
	 * This method allows to :
	 * <ul>
	 * <li>change isRiding/isFlying flags</li>
	 * <li>gift player with Wyvern Breath skill if mount is a wyvern</li>
	 * <li>send the skillList (faded icons update)</li>
	 * </ul>
	 * @param npcId the npcId of the mount
	 * @param npcLevel The level of the mount
	 * @param mountType 0, 1 or 2 (dismount, strider or wyvern).
	 */
	public void setMount(int npcId, int npcLevel, int mountType)
	{
		switch (mountType)
		{
			case 0: // Dismounted
				if (isFlying())
					removeSkill(FrequentSkill.WYVERN_BREATH.getSkill().getId(), false);
			case 1:
				getMove().removeMoveType(MoveType.FLY);
				break;
			
			case 2: // Flying Wyvern
				addSkill(FrequentSkill.WYVERN_BREATH.getSkill(), false);
				getMove().addMoveType(MoveType.FLY);
				break;
		}
		
		_mountNpcId = npcId;
		_mountType = mountType;
		_mountLevel = npcLevel;
		
		// Update faded icons && eventual added skills.
		sendSkillList();
	}
	
	@Override
	public boolean isSeated()
	{
		return _throneId > 0;
	}
	
	public int getThroneId()
	{
		return _throneId;
	}
	
	public void setThroneId(int id)
	{
		_throneId = id;
	}
	
	@Override
	public boolean isRiding()
	{
		return _mountType == 1;
	}
	
	@Override
	public boolean isFlying()
	{
		return _mountType == 2;
	}
	
	/**
	 * @return the type of Pet mounted (0 : none, 1 : Strider, 2 : Wyvern).
	 */
	public int getMountType()
	{
		return _mountType;
	}
	
	@Override
	public final void stopAllEffects()
	{
		super.stopAllEffects();
		updateAndBroadcastStatus(2);
	}
	
	@Override
	public final void stopAllEffectsExceptThoseThatLastThroughDeath()
	{
		super.stopAllEffectsExceptThoseThatLastThroughDeath();
		updateAndBroadcastStatus(2);
	}
	
	/**
	 * Stop all toggle-type effects
	 */
	public final void stopAllToggles()
	{
		_effects.stopAllToggles();
	}
	
	/**
	 * Send UserInfo to this Player and CharInfo to all Player in its _KnownPlayers.<BR>
	 * <ul>
	 * <li>Send UserInfo to this Player (Public and Private Data)</li>
	 * <li>Send CharInfo to all Player in _KnownPlayers of the Player (Public data only)</li>
	 * </ul>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : DON'T SEND UserInfo packet to other players instead of CharInfo packet. Indeed, UserInfo packet contains PRIVATE DATA as MaxHP, STR, DEX...</B></FONT><BR>
	 * <BR>
	 */
	@Override
	public void updateAbnormalEffect()
	{
		broadcastUserInfo();
	}
	
	/**
	 * Disable the Inventory and create a new task to enable it after 1.5s.
	 */
	public void tempInventoryDisable()
	{
		_inventoryDisable = true;
		
		ThreadPool.schedule(() -> _inventoryDisable = false, 1500);
	}
	
	/**
	 * @return True if the Inventory is disabled.
	 */
	public boolean isInventoryDisabled()
	{
		return _inventoryDisable;
	}
	
	@Override
	public String toString()
	{
		return getName() + " (" + getObjectId() + ")";
	}
	
	/**
	 * @return the modifier corresponding to the Enchant Effect of the Active Weapon (Min : 127).
	 */
	public int getEnchantEffect()
	{
		final ItemInstance wpn = getActiveWeaponInstance();
		return (wpn == null) ? 0 : Math.min(127, wpn.getEnchantLevel());
	}
	
	/**
	 * Remember the current {@link Folk} of the {@link Player}, used notably for integrity check.
	 * @param folk : The Folk to remember.
	 */
	public void setCurrentFolk(Folk folk)
	{
		_currentFolk = folk;
	}
	
	/**
	 * @return the current {@link Folk} of the {@link Player}.
	 */
	public Folk getCurrentFolk()
	{
		return _currentFolk;
	}
	
	/**
	 * @return True if Player is a participant in the Festival of Darkness.
	 */
	public boolean isFestivalParticipant()
	{
		return FestivalOfDarknessManager.getInstance().isParticipant(this);
	}
	
	public void addAutoSoulShot(int itemId)
	{
		_activeSoulShots.add(itemId);
	}
	
	public boolean removeAutoSoulShot(int itemId)
	{
		return _activeSoulShots.remove(itemId);
	}
	
	public Set<Integer> getAutoSoulShot()
	{
		return _activeSoulShots;
	}
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		final ItemInstance weapon = getActiveWeaponInstance();
		return weapon != null && weapon.isChargedShot(type);
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		final ItemInstance weapon = getActiveWeaponInstance();
		if (weapon != null)
			weapon.setChargedShot(type, charged);
	}
	
	@Override
	public void rechargeShots(boolean physical, boolean magic)
	{
		if (_activeSoulShots.isEmpty())
			return;
		
		for (final int itemId : _activeSoulShots)
		{
			final ItemInstance item = getInventory().getItemByItemId(itemId);
			if (item != null)
			{
				if (magic && item.getItem().getDefaultAction() == ActionType.spiritshot)
				{
					final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
					if (handler != null)
						handler.useItem(this, item, false);
				}
				
				if (physical && item.getItem().getDefaultAction() == ActionType.soulshot)
				{
					final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
					if (handler != null)
						handler.useItem(this, item, false);
				}
			}
			else
				removeAutoSoulShot(itemId);
		}
	}
	
	/**
	 * Cancel autoshot use for shot itemId
	 * @param itemId int id to disable
	 * @return true if canceled.
	 */
	public boolean disableAutoShot(int itemId)
	{
		if (_activeSoulShots.contains(itemId))
		{
			removeAutoSoulShot(itemId);
			sendPacket(new ExAutoSoulShot(itemId, 0));
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AUTO_USE_OF_S1_CANCELLED).addItemName(itemId));
			return true;
		}
		
		if (isAutoPot(728))
		{
			sendPacket(new ExAutoSoulShot(728, 0));
			setAutoPot(728, null, false);
		}
		if (isAutoPot(1539))
		{
			sendPacket(new ExAutoSoulShot(1539, 0));
			setAutoPot(1539, null, false);
		}
		if (isAutoPot(5592))
		{
			sendPacket(new ExAutoSoulShot(5592, 0));
			setAutoPot(5592, null, false);
		}
		
		return false;
	}
	
	/**
	 * Cancel all autoshots for player
	 */
	public void disableAutoShotsAll()
	{
		for (final int itemId : _activeSoulShots)
		{
			sendPacket(new ExAutoSoulShot(itemId, 0));
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AUTO_USE_OF_S1_CANCELLED).addItemName(itemId));
		}
		_activeSoulShots.clear();
	}
	
	public int getClanPrivileges()
	{
		return _clanPrivileges;
	}
	
	public void setClanPrivileges(int privs)
	{
		_clanPrivileges = privs;
	}
	
	public boolean hasClanPrivileges(int priv)
	{
		return (_clanPrivileges & priv) == priv;
	}
	
	public int getPledgeClass()
	{
		return _pledgeClass;
	}
	
	public void setPledgeClass(int classId)
	{
		_pledgeClass = classId;
	}
	
	public int getPledgeType()
	{
		return _pledgeType;
	}
	
	public void setPledgeType(int typeId)
	{
		_pledgeType = typeId;
	}
	
	public int getApprentice()
	{
		return _apprentice;
	}
	
	public void setApprentice(int id)
	{
		_apprentice = id;
	}
	
	public int getSponsor()
	{
		return _sponsor;
	}
	
	public void setSponsor(int id)
	{
		_sponsor = id;
	}
	
	@Override
	public void sendMessage(String message)
	{
		sendPacket(SystemMessage.sendString(message));
	}
	
	@Override
	public void teleportTo(int x, int y, int z, int randomOffset)
	{
		// Set the Boat as null upon teleport.
		setBoat(null);
		
		super.teleportTo(x, y, z, randomOffset);
	}
	
	public final boolean isDarkPanther(int _npcid) 
	{
		_contains = IntStream.of(_isdarkpanther).anyMatch(x -> x == _npcid);
		if(_contains)		
			return true;
		
		return false;
	}
	
	
	/**
	 * Unsummon all types of summons : pets, cubics, normal summons and trained beasts.
	 */
	
	public void dropAllAgathions()
	{

		Pet agathion = World.getInstance().getAgat(getObjectId());
		if (agathion != null)
			agathion.unSummon(this);
	}
	
	public void dropAllSummons()
	{
		// Delete summons and pets
		if (_summon != null)
			_summon.unSummon(this);
		
		Pet agathion = World.getInstance().getAgat(getObjectId());
		if (agathion != null)
			agathion.unSummon(this);
		
		// Delete trained beasts
		if (_tamedBeast != null)
			_tamedBeast.deleteMe();
		
		// Delete any form of cubics
		_cubicList.stopCubics(true);
	}
	
	public void dropAllSummonsExceptAgathions()
	{
		// Delete summons and pets
		if (_summon != null)
			_summon.unSummon(this);
		
		// Delete trained beasts
		if (_tamedBeast != null)
			_tamedBeast.deleteMe();
		
		// Delete any form of cubics
		_cubicList.stopCubics(true);
	}
	
	public void enterObserverMode(int x, int y, int z)
	{
		dropAllSummons();
		
		if (getParty() != null)
			getParty().removePartyMember(this, MessageType.EXPELLED);
		
		standUp();
		
		_savedLocation.set(getPosition());
		
		setInvul(true);
		getAppearance().setVisible(false);
		setIsParalyzed(true);
		
		// Abort attack, cast and move.
		abortAll(true);
		
		teleportTo(x, y, z, 0);
		sendPacket(new ObservationMode(x, y, z));
	}
	
	public void enterOlympiadObserverMode(int id)
	{
		final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(id);
		if (task == null)
			return;
		
		dropAllSummons();
		
		if (getParty() != null)
			getParty().removePartyMember(this, MessageType.EXPELLED);
		
		_olympiadGameId = id;
		
		standUp();
		
		// Don't override saved location if we jump from stadium to stadium.
		if (!isInObserverMode())
			_savedLocation.set(getPosition());
		
		setTarget(null);
		setInvul(true);
		getAppearance().setVisible(false);
		
		teleportTo(task.getZone().getSpawns(SpawnType.NORMAL).get(2), 0);
		sendPacket(new ExOlympiadMode(3));
	}
	
	public void leaveObserverMode()
	{
		getAI().tryToIdle();
		
		setTarget(null);
		getAppearance().setVisible(true);
		setInvul(false);
		setIsParalyzed(false);
		
		sendPacket(new ObservationReturn(_savedLocation));
		teleportTo(_savedLocation, 0);
		
		// Clear the location.
		_savedLocation.clean();
	}
	
	public void leaveOlympiadObserverMode()
	{
		if (_olympiadGameId == -1)
			return;
		
		_olympiadGameId = -1;
		
		getAI().tryToIdle();
		
		setTarget(null);
		getAppearance().setVisible(true);
		setInvul(false);
		
		sendPacket(new ExOlympiadMode(0));
		teleportTo(_savedLocation, 0);
		
		// Clear the location.
		_savedLocation.clean();
	}
	
	public int getOlympiadSide()
	{
		return _olympiadSide;
	}
	
	public void setOlympiadSide(int i)
	{
		_olympiadSide = i;
	}
	
	public int getOlympiadGameId()
	{
		return _olympiadGameId;
	}
	
	public void setOlympiadGameId(int id)
	{
		_olympiadGameId = id;
	}
	
	public Location getSavedLocation()
	{
		return _savedLocation;
	}
	
	public boolean isInObserverMode()
	{
		return !_isInOlympiadMode && !_savedLocation.equals(Location.DUMMY_LOC);
	}
	
	public TeleportMode getTeleportMode()
	{
		return _teleportMode;
	}
	
	public void setTeleportMode(TeleportMode mode)
	{
		_teleportMode = mode;
	}
	
	public int getLoto(int i)
	{
		return _loto[i];
	}
	
	public void setLoto(int i, int val)
	{
		_loto[i] = val;
	}
	
	public int getRace(int i)
	{
		return _race[i];
	}
	
	public void setRace(int i, int val)
	{
		_race[i] = val;
	}
	
	public BlockList getBlockList()
	{
		return _blockList;
	}
	
	public QuestList getQuestList()
	{
		return _questList;
	}
	
	public boolean isHero()
	{
		return _isHero;
	}
	
	public boolean isVIP()
	{
		return _isVIP;
	}
	
	public void setHero(boolean hero)
	{
		// if (hero && _baseClass == _activeClass)
		if (hero) // hero skills on subclass
		{
			for (final L2Skill skill : SkillTable.getHeroSkills())
				addSkill(skill, false);
		}
		else
		{
			for (final L2Skill skill : SkillTable.getHeroSkills())
				removeSkill(skill.getId(), false);
		}
		_isHero = hero;
		
		sendSkillList();
	}
	
	public void setVIP(boolean vip)
	{
		_isVIP = vip;
		
	}
	
	public boolean isOlympiadStart()
	{
		return _isInOlympiadStart;
	}
	
	public void setOlympiadStart(boolean b)
	{
		_isInOlympiadStart = b;
	}
	
	public boolean isInOlympiadMode()
	{
		return _isInOlympiadMode;
	}
	
	public void setOlympiadMode(boolean b)
	{
		_isInOlympiadMode = b;
	}
	
	public boolean isInDuel()
	{
		return _duelId > 0;
	}
	
	public int getDuelId()
	{
		return _duelId;
	}
	
	public void setDuelState(DuelState state)
	{
		_duelState = state;
	}
	
	public DuelState getDuelState()
	{
		return _duelState;
	}
	
	/**
	 * Sets up the duel state using a non 0 duelId.
	 * @param duelId 0=not in a duel
	 */
	public void setInDuel(int duelId)
	{
		if (duelId > 0)
		{
			_duelState = DuelState.ON_COUNTDOWN;
			_duelId = duelId;
		}
		else
		{
			if (_duelState == DuelState.DEAD)
			{
				enableAllSkills();
				getStatus().startHpMpRegeneration();
			}
			_duelState = DuelState.NO_DUEL;
			_duelId = 0;
		}
	}
	
	/**
	 * This returns a SystemMessage stating why the player is not available for duelling.
	 * @return S1_CANNOT_DUEL... message
	 */
	public SystemMessage getNoDuelReason()
	{
		// Prepare the message with the good reason.
		final SystemMessage sm = SystemMessage.getSystemMessage(_noDuelReason).addCharName(this);
		
		// Reinitialize the reason.
		_noDuelReason = SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL;
		
		// Send stored reason.
		return sm;
	}
	
	/**
	 * Checks if this player might join / start a duel. To get the reason use getNoDuelReason() after calling this function.
	 * @return true if the player might join/start a duel.
	 */
	public boolean canDuel()
	{
		if (isInCombat() || _punishment.getType() == PunishmentType.JAIL)
			_noDuelReason = SystemMessageId.S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_ENGAGED_IN_BATTLE;
		else if (isDead() || isAlikeDead() || getStatus().getHpRatio() < 0.5 || getStatus().getMpRatio() < 0.5)
			_noDuelReason = SystemMessageId.S1_CANNOT_DUEL_BECAUSE_S1_HP_OR_MP_IS_BELOW_50_PERCENT;
		else if (isInDuel())
			_noDuelReason = SystemMessageId.S1_CANNOT_DUEL_BECAUSE_S1_IS_ALREADY_ENGAGED_IN_A_DUEL;
		else if (isInOlympiadMode())
			_noDuelReason = SystemMessageId.S1_CANNOT_DUEL_BECAUSE_S1_IS_PARTICIPATING_IN_THE_OLYMPIAD;
		else if (isCursedWeaponEquipped() || getKarma() != 0)
			_noDuelReason = SystemMessageId.S1_CANNOT_DUEL_BECAUSE_S1_IS_IN_A_CHAOTIC_STATE;
		else if (isOperating())
			_noDuelReason = SystemMessageId.S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE;
		else if (isMounted() || isInBoat())
			_noDuelReason = SystemMessageId.S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_RIDING_A_BOAT_WYVERN_OR_STRIDER;
		else if (isFishing())
			_noDuelReason = SystemMessageId.S1_CANNOT_DUEL_BECAUSE_S1_IS_CURRENTLY_FISHING;
		else if (isInsideZone(ZoneId.PVP) || isInsideZone(ZoneId.PEACE) || isInsideZone(ZoneId.SIEGE))
			_noDuelReason = SystemMessageId.S1_CANNOT_MAKE_A_CHALLANGE_TO_A_DUEL_BECAUSE_S1_IS_CURRENTLY_IN_A_DUEL_PROHIBITED_AREA;
		else
			return true;
		
		return false;
	}
	
	public boolean isNoble()
	{
		return _isNoble;
	}
	
	/**
	 * Set Noblesse status, and reward with nobles' {@link L2Skill}s.
	 * @param isNoble : If true, add {@link L2Skill}s ; otherwise remove them.
	 * @param storeInDb : If true, store directly the data in the db.
	 */
	public void setNoble(boolean isNoble, boolean storeInDb)
	{
		if (isNoble)
		{
			for (final L2Skill skill : SkillTable.getNobleSkills())
				addSkill(skill, false);
		}
		else
		{
			for (final L2Skill skill : SkillTable.getNobleSkills())
				removeSkill(skill.getId(), false);
		}
		
		_isNoble = isNoble;
		
		sendSkillList();
		
		if (storeInDb)
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_NOBLESS))
			{
				ps.setBoolean(1, isNoble);
				ps.setInt(2, getObjectId());
				ps.executeUpdate();
			}
			catch (final Exception e)
			{
				LOGGER.error("Couldn't update nobless status for {}.", e, getName());
			}
		}
	}
	
	public void setLvlJoinedAcademy(int lvl)
	{
		_lvlJoinedAcademy = lvl;
	}
	
	public int getLvlJoinedAcademy()
	{
		return _lvlJoinedAcademy;
	}
	
	public boolean isAcademyMember()
	{
		return _lvlJoinedAcademy > 0;
	}
	
	public void setTeam(TeamType team)
	{
		_team = team;
	}
	
	public TeamType getTeam()
	{
		return _team;
	}
	
	public void setWantsPeace(boolean wantsPeace)
	{
		_wantsPeace = wantsPeace;
	}
	
	public boolean wantsPeace()
	{
		return _wantsPeace;
	}
	
	public boolean isFishing()
	{
		return _fishingStance.isUnderFishCombat() || _fishingStance.isLookingForFish();
	}
	
	public void setAllianceWithVarkaKetra(int sideAndLvlOfAlliance)
	{
		_alliedVarkaKetra = sideAndLvlOfAlliance;
	}
	
	/**
	 * [-5,-1] varka, 0 neutral, [1,5] ketra
	 * @return the side faction.
	 */
	public int getAllianceWithVarkaKetra()
	{
		return _alliedVarkaKetra;
	}
	
	public boolean isAlliedWithVarka()
	{
		return _alliedVarkaKetra < 0;
	}
	
	public boolean isAlliedWithKetra()
	{
		return _alliedVarkaKetra > 0;
	}
	
	public void sendSkillList()
	{
		final boolean isWearingFormalWear = isWearingFormalWear();
		final boolean isClanDisabled = getClan() != null && getClan().getReputationScore() < 0;
		
		final SkillList sl = new SkillList();
		for (final L2Skill skill : getSkills().values())
			sl.addSkill(skill.getId(), skill.getLevel(), skill.isPassive(), isWearingFormalWear || (skill.isClanSkill() && isClanDisabled));
		
		sendPacket(sl);
	}
	
	/**
	 * 1. Add the specified class ID as a subclass (up to the maximum number of <b>three</b>) for this character.<BR>
	 * 2. This method no longer changes the active _classIndex of the player. This is only done by the calling of setActiveClass() method as that should be the only way to do so.
	 * @param classId
	 * @param classIndex
	 * @return boolean subclassAdded
	 */
	public boolean addSubClass(int classId, int classIndex)
	{
		if (!_subclassLock.tryLock())
			return false;
		
		try
		{
			if (_subClasses.size() == 5 || classIndex == 0 || _subClasses.containsKey(classIndex))
				return false;
			
			final SubClass subclass = new SubClass(classId, classIndex);
			
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(ADD_CHAR_SUBCLASS))
			{
				ps.setInt(1, getObjectId());
				ps.setInt(2, subclass.getClassId());
				ps.setLong(3, subclass.getExp());
				ps.setInt(4, subclass.getSp());
				ps.setInt(5, subclass.getLevel());
				ps.setInt(6, subclass.getClassIndex());
				ps.execute();
			}
			catch (final Exception e)
			{
				LOGGER.error("Couldn't add subclass for {}.", e, getName());
				return false;
			}
			
			_subClasses.put(subclass.getClassIndex(), subclass);
			
			PlayerData.getInstance().getTemplate(classId).getSkills().stream().filter(s -> s.getMinLvl() <= 40).collect(Collectors.groupingBy(s -> s.getId(), Collectors.maxBy(COMPARE_SKILLS_BY_LVL))).forEach((i, s) -> storeSkill(s.get().getSkill(), classIndex));
			
			return true;
		}
		finally
		{
			_subclassLock.unlock();
		}
	}
	
	/**
	 * 1. Completely erase all existance of the subClass linked to the classIndex.<BR>
	 * 2. Send over the newClassId to addSubClass()to create a new instance on this classIndex.<BR>
	 * 3. Upon Exception, revert the player to their BaseClass to avoid further problems.<BR>
	 * @param classIndex
	 * @param newClassId
	 * @return boolean subclassAdded
	 */
	public boolean modifySubClass(int classIndex, int newClassId)
	{
		if (!_subclassLock.tryLock())
			return false;
		
		try
		{
			try (Connection con = ConnectionPool.getConnection())
			{
				// Remove all henna info stored for this sub-class.
				try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_HENNAS))
				{
					ps.setInt(1, getObjectId());
					ps.setInt(2, classIndex);
					ps.execute();
				}
				
				// Remove all shortcuts info stored for this sub-class.
				try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_SHORTCUTS))
				{
					ps.setInt(1, getObjectId());
					ps.setInt(2, classIndex);
					ps.execute();
				}
				
				// Remove all effects info stored for this sub-class.
				try (PreparedStatement ps = con.prepareStatement(DELETE_SKILL_SAVE))
				{
					ps.setInt(1, getObjectId());
					ps.setInt(2, classIndex);
					ps.execute();
				}
				
				// Remove all skill info stored for this sub-class.
				try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_SKILLS))
				{
					ps.setInt(1, getObjectId());
					ps.setInt(2, classIndex);
					ps.execute();
				}
				
				// Remove all basic info stored about this sub-class.
				try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_SUBCLASS))
				{
					ps.setInt(1, getObjectId());
					ps.setInt(2, classIndex);
					ps.execute();
				}
			}
			catch (final Exception e)
			{
				LOGGER.error("Couldn't modify subclass for {} to class index {}.", e, getName(), classIndex);
				
				// This must be done in order to maintain data consistency.
				_subClasses.remove(classIndex);
				return false;
			}
			
			_subClasses.remove(classIndex);
		}
		finally
		{
			_subclassLock.unlock();
		}
		
		return addSubClass(newClassId, classIndex);
	}
	
	public boolean isSubClassActive()
	{
		return _classIndex > 0;
	}
	
	public Map<Integer, SubClass> getSubClasses()
	{
		return _subClasses;
	}
	
	public int getBaseClass()
	{
		return _baseClass;
	}
	
	public int getActiveClass()
	{
		return _activeClass;
	}
	
	public int getClassIndex()
	{
		return _classIndex;
	}
	
	private void setClassTemplate(int classId)
	{
		_activeClass = classId;
		
		// Set the template of the Player
		setTemplate(PlayerData.getInstance().getTemplate(classId));
	}
	
	/**
	 * Changes the character's class based on the given class index. <BR>
	 * <BR>
	 * An index of zero specifies the character's original (base) class, while indexes 1-3 specifies the character's sub-classes respectively.
	 * @param classIndex
	 * @return true if successful.
	 */
	public boolean setActiveClass(int classIndex)
	{
		if (!_subclassLock.tryLock())
			return false;
		
		SubClass subclass = null;
		if (classIndex != 0)
		{
			subclass = _subClasses.get(classIndex);
			if (subclass == null)
				return false;
		}
		
		try
		{
			// Remove active item skills before saving char to database because next time when choosing this class, worn items can be different
			for (final ItemInstance item : getInventory().getAugmentedItems())
			{
				if (item != null && item.isEquipped())
					item.getAugmentation().removeBonus(this);
			}
			
			// abort any kind of cast.
			getCast().stop();
			
			// Stop casting for any player that may be casting a force buff on this l2pcinstance.
			for (final Creature creature : getKnownType(Creature.class))
				if (creature.getFusionSkill() != null && creature.getFusionSkill().getTarget() == this)
					creature.getCast().stop();
				
			store();
			_reuseTimeStamps.clear();
			
			// clear charges
			_charges.set(0);
			stopChargeTask();
			
			setClassTemplate((subclass == null) ? getBaseClass() : subclass.getClassId());
			
			_classIndex = classIndex;
			
			if (_party != null)
				_party.recalculateLevel();
			
			if (_summon instanceof Servitor)
				_summon.unSummon(this);
			
			for (final L2Skill skill : getSkills().values())
				removeSkill(skill.getId(), false);
			
			stopAllEffectsExceptThoseThatLastThroughDeath();
			_cubicList.stopCubics(true);
			
			if (isSubClassActive())
				_recipeBook.clear();
			else
				_recipeBook.restore();
			
			_hennaList.restore();
			
			restoreSkills();
			giveSkills();
			regiveTemporarySkills();
			
			// Prevents some issues when changing between subclases that shares skills
			getDisabledSkills().clear();
			
			restoreEffects();
			updateEffectIcons();
			sendPacket(new EtcStatusUpdate(this));
			
			// If player has quest "Repent Your Sins", remove it
			final QuestState st = _questList.getQuestState("Q422_RepentYourSins");
			if (st != null)
				st.exitQuest(true);
			
			int max = getStatus().getMaxHp();
			if (getStatus().getHp() > max)
				getStatus().setHp(max);
			
			max = getStatus().getMaxMp();
			if (getStatus().getMp() > max)
				getStatus().setMp(max);
			
			max = getStatus().getMaxCp();
			if (getStatus().getCp() > max)
				getStatus().setCp(max);
			
			refreshWeightPenalty();
			refreshExpertisePenalty();
			refreshHennaList();
			broadcastUserInfo();
			
			// Clear resurrect xp calculation
			setExpBeforeDeath(0);
			
			// Remove shot automation
			disableAutoShotsAll();
			
			// Discharge any active shots
			final ItemInstance item = getActiveWeaponInstance();
			if (item != null)
				item.unChargeAllShots();
			
			_shortcutList.restore();
			sendPacket(new ShortCutInit(this));
			
			broadcastPacket(new SocialAction(this, 15));
			sendPacket(new SkillCoolTime(this));
			return true;
		}
		finally
		{
			_subclassLock.unlock();
		}
	}
	
	public boolean isLocked()
	{
		return _subclassLock.isLocked();
	}
	
	public void onPlayerEnter()
	{
		if (isCursedWeaponEquipped())
			CursedWeaponManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()).cursedOnLogin();
		
		// Add to the GameTimeTask to keep inform about activity time.
		GameTimeTaskManager.getInstance().add(this);
		
		// Teleport player if the Seven Signs period isn't the good one, or if the player isn't in a cabal.
		if (isIn7sDungeon() && !isGM())
		{
			if (SevenSignsManager.getInstance().isSealValidationPeriod() || SevenSignsManager.getInstance().isCompResultsPeriod())
			{
				if (SevenSignsManager.getInstance().getPlayerCabal(getObjectId()) != SevenSignsManager.getInstance().getWinningCabal())
				{
					teleportTo(TeleportType.TOWN);
					setIsIn7sDungeon(false);
				}
			}
			else if (SevenSignsManager.getInstance().getPlayerCabal(getObjectId()) == CabalType.NORMAL)
			{
				teleportTo(TeleportType.TOWN);
				setIsIn7sDungeon(false);
			}
		}
		
		// Jail task
		_punishment.handle();
		
		if (isGM())
		{
			if (isInvul())
				sendMessage("Entering world in Invulnerable mode.");
			
			if (!getAppearance().isVisible())
				sendMessage("Entering world in Invisible mode.");
			
			if (getBlockList().isBlockingAll())
				sendMessage("Entering world in Refusal mode.");
		}
		
		//Send siege info
		sendSiegeInfo();
		 	
		
		/* HANDLED BY EnterWorld.java
		/// Buff for clan members, owning a castle (castle buff)
		if (getClan() != null && getClan().hasCastle())
		{
			SkillTable.getInstance().getInfo(7096, 1).getEffects(this, this);
		}
		else
		{
			stopSkillEffects(7096);
		}
		
		/// Buff for clan members, when leader is active
		if (getClan() != null && getClan().getLeader().isOnline())
		{
			SkillTable.getInstance().getInfo(7095, 1).getEffects(this, this);
		}
		else
		{
			stopSkillEffects(7095);
		}
		*/
		revalidateZone(true);
		notifyFriends(true);
		
		// Check if player is hero
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(CHECK_HERO))
		{
			ps.setInt(1, getObjectId()); 
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final long check_expired_date = rs.getLong("expired_date");
					if(check_expired_date >= System.currentTimeMillis()) //if expired date on db is 'higher' in value than current time
						{
						setHero(true);
						_permanent_hero = true;
						}
					else
						_permanent_hero = false;
				}
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't check if the player is hero {}.", e, getName());
		}
	}
	
	public long getLastAccess()
	{
		return _lastAccess;
	}
	
	@Override
	public void doRevive()
	{
		super.doRevive();
		
		stopEffects(EffectType.CHARM_OF_COURAGE);
		sendPacket(new EtcStatusUpdate(this));
		
		_reviveRequested = 0;
		_revivePower = 0;
		
		if (isMounted())
			startFeed(_mountNpcId);
	}
	
	@Override
	public void doRevive(double revivePower)
	{
		// Restore the player's lost experience, depending on the % return of the skill used (based on its power).
		restoreExp(revivePower);
		doRevive();
	}
	
	public void reviveRequest(Player reviver, L2Skill skill, boolean isPet)
	{
		if (_reviveRequested == 1)
		{
			// Resurrection has already been proposed.
			if (_revivePet == isPet)
				reviver.sendPacket(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED);
			else
			{
				if (isPet)
					// A pet cannot be resurrected while it's owner is in the process of resurrecting.
					reviver.sendPacket(SystemMessageId.CANNOT_RES_PET2);
				else
					// While a pet is attempting to resurrect, it cannot help in resurrecting its master.
					reviver.sendPacket(SystemMessageId.MASTER_CANNOT_RES);
			}
			return;
		}
		
		if ((isPet && _summon != null && _summon.isDead()) || (!isPet && isDead()))
		{
			_reviveRequested = 1;
			_revivePower = (isPhoenixBlessed()) ? 100 : Formulas.calculateSkillResurrectRestorePercent(skill.getPower(), reviver);
			_revivePet = isPet;
			
			sendPacket(new ConfirmDlg(SystemMessageId.RESSURECTION_REQUEST_BY_S1).addCharName(reviver));
		}
	}
	
	public void reviveAnswer(int answer)
	{
		if (_reviveRequested != 1 || (!isDead() && !_revivePet) || (_revivePet && _summon != null && !_summon.isDead()))
			return;
		
		if (answer == 0 && isPhoenixBlessed())
			stopPhoenixBlessing(null);
		else if (answer == 1)
		{
			if (!_revivePet)
			{
				if (_revivePower != 0)
					doRevive(_revivePower);
				else
					doRevive();
			}
			else if (_summon != null)
			{
				if (_revivePower != 0)
					_summon.doRevive(_revivePower);
				else
					_summon.doRevive();
			}
		}
		_reviveRequested = 0;
		_revivePower = 0;
	}
	
	public boolean isReviveRequested()
	{
		return (_reviveRequested == 1);
	}
	
	public boolean isRevivingPet()
	{
		return _revivePet;
	}
	
	public void removeReviving()
	{
		_reviveRequested = 0;
		_revivePower = 0;
	}
	
	public void onActionRequest()
	{
		if (isSpawnProtected())
		{
			sendMessage("As you acted, you are no longer under spawn protection.");
			setSpawnProtection(false);
		}
	}
	
	@Override
	public final void onTeleported()
	{
		super.onTeleported();
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			setSpawnProtection(true);
		
		// Modify the position of the tamed beast if necessary
		if (_tamedBeast != null) 
		{
			_tamedBeast.setInstanceId(getInstanceId());
			_tamedBeast.teleportTo(getPosition(), 0);
		}
		
		// Modify the position of the pet if necessary
		if (_summon != null) 
		{
			_summon.setInstanceId(getInstanceId());
			_summon.teleportTo(getPosition(), 0);
		}
		
		// Teleport Agathion to the player's position
		Pet agathion = World.getInstance().getAgat(getObjectId());
		if (agathion != null)
		{
			agathion.setInstanceId(getInstanceId());
			agathion.teleportTo(getPosition(), 0);
		}
		
		// If under shop mode, cancel it. Leave the Player sat down.
		if (isInStoreMode())
			setOperateType(OperateType.NONE);
	}
	
	@Override
	public void addExpAndSp(long addToExp, int addToSp)
	{
		getStatus().addExpAndSp(addToExp, addToSp);
	}
	
	public void addExpAndSp(long addToExp, int addToSp, Map<Creature, RewardInfo> rewards)
	{
		getStatus().addExpAndSp(addToExp, addToSp, rewards);
	}
	
	public void removeExpAndSp(long removeExp, int removeSp)
	{
		getStatus().removeExpAndSp(removeExp, removeSp);
	}
	
	@Override
	public void reduceCurrentHp(double value, Creature attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
		if (skill != null)
			getStatus().reduceHp(value, attacker, awake, isDOT, skill.isToggle(), skill.getDmgDirectlyToHP());
		else
			getStatus().reduceHp(value, attacker, awake, isDOT, false, false);
	}
	
	public synchronized void addBypass(String bypass)
	{
		if (bypass == null)
			return;
		
		_validBypass.add(bypass);
	}
	
	public synchronized void addBypass2(String bypass)
	{
		if (bypass == null)
			return;
		
		_validBypass2.add(bypass);
	}
	
	public synchronized boolean validateBypass(String cmd)
	{
		for (final String bp : _validBypass)
		{
			if (bp == null)
				continue;
			
			if (bp.equals(cmd))
				return true;
		}
		
		for (final String bp : _validBypass2)
		{
			if (bp == null)
				continue;
			
			if (cmd.startsWith(bp))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Test cases (player drop, trade item) where the item shouldn't be able to manipulate.
	 * @param objectId : The item objectId.
	 * @return true if it the item can be manipulated, false ovtherwise.
	 */
	public ItemInstance validateItemManipulation(int objectId)
	{
		final ItemInstance item = getInventory().getItemByObjectId(objectId);
		
		// You don't own the item, or item is null.
		if (item == null || item.getOwnerId() != getObjectId())
			return null;
		
		// Pet whom item you try to manipulate is summoned/mounted.
		if (_summon != null && _summon.getControlItemId() == objectId || _mountObjectId == objectId)
			return null;
		
		var agathion = World.getInstance().getAgat(this.getObjectId());

		// Agathion whom item you try to manipulate is summoned/mounted.
		if (agathion != null && agathion.getControlItemId() == objectId)
			return null;
		
		
		//Check Dressme
		if (item.getItem().getTemplate() && getCostumeitemObjId() == objectId) 
		{
			return null;
		}
		
		//Check Dressme Hair
		if (item.getItem().isTemplateHair() && getCostumeHeaditemObjId() == objectId)
		{
			return null;
		}
		
		
		// Item is under enchant process.
		if (getActiveEnchantItem() != null && getActiveEnchantItem().getObjectId() == objectId)
			return null;
		
		// Can't trade a cursed weapon.
		if (CursedWeaponManager.getInstance().isCursed(item.getItemId()))
			return null;
		
		return item;
	}
	
	public synchronized void clearBypass()
	{
		_validBypass.clear();
		_validBypass2.clear();
	}
	
	/**
	 * @return true if the current {@link Player} is linked to a {@link Boat}.
	 */
	public boolean isInBoat()
	{
		return _boat != null;
	}
	
	/**
	 * @return the {@link Boat} linked to the current {@link Player}.
	 */
	public Boat getBoat()
	{
		return _boat;
	}
	
	/**
	 * Set the {@link Boat} for the current {@link Player}.<br>
	 * <br>
	 * If the parameter is null but Player is registered into a Boat, we delete the passenger from the Boat.
	 * @param boat : The Boat to set, or null to clean it.
	 */
	public void setBoat(Boat boat)
	{
		if (boat == null && _boat != null)
		{
			// Remove passenger out from the Boat.
			_boat.getPassengers().remove(this);
			
			// Clear the boat position.
			_boatPosition.clean();
		}
		_boat = boat;
	}
	
	/**
	 * @return the {@link SpawnLocation} related to Boat.
	 */
	public SpawnLocation getBoatPosition()
	{
		return _boatPosition;
	}
	
	public void setCrystallizing(boolean mode)
	{
		_isCrystallizing = mode;
	}
	
	public boolean isCrystallizing()
	{
		return _isCrystallizing;
	}
	
	/**
	 * Manage the delete task of a Player (Leave Party, Unsummon pet, Save its inventory in the database, Remove it from the world...).
	 * <ul>
	 * <li>If the Player is in observer mode, set its position to its position before entering in observer mode</li>
	 * <li>Set the online Flag to True or False and update the characters table of the database with online status and lastAccess</li>
	 * <li>Stop the HP/MP/CP Regeneration task</li>
	 * <li>Cancel Crafting, Attak or Cast</li>
	 * <li>Remove the Player from the world</li>
	 * <li>Stop Party and Unsummon Pet</li>
	 * <li>Update database with items in its inventory and remove them from the world</li>
	 * <li>Remove the object from region</li>
	 * <li>Close the connection with the client</li>
	 * </ul>
	 */
	@Override
	public void deleteMe()
	{
		super.deleteMe();
		
		TournamentManager.getInstance().storeTournamentData(this);
		
		cleanup();
		store();
	}
	
	private synchronized void cleanup()
	{
		try
		{
			// Put the online status to false
			setOnlineStatus(false, true);
			
			// Abort attack, cast and move.
			abortAll(true);
			
			removeMeFromPartyMatch();
			
			if (isFlying())
				removeSkill(FrequentSkill.WYVERN_BREATH.getSkill().getId(), false);
			
			// Dismount the player.
			if (isMounted())
				dismount();
			// If the Player has a summon, unsummon it.
			else if (_summon != null)
				_summon.unSummon(this);
			
			Pet agathion = World.getInstance().getAgat(getObjectId());
			if (agathion != null)
				agathion.unSummon(this);
			
			// Stop all scheduled tasks.
			stopChargeTask();
			
			_punishment.stopTask(true);
			
			// Stop all timers associated to that Player.
			WaterTaskManager.getInstance().remove(this);
			AttackStanceTaskManager.getInstance().remove(this);
			PvpFlagTaskManager.getInstance().remove(this, false);
			GameTimeTaskManager.getInstance().remove(this);
			ShadowItemTaskManager.getInstance().remove(this);
			
			// Cancel the cast of eventual fusion skill users on this target.
			for (final Creature creature : getKnownType(Creature.class))
				if (creature.getFusionSkill() != null && creature.getFusionSkill().getTarget() == this)
					creature.getCast().stop();
				
			// Stop signets & toggles effects.
			for (final AbstractEffect effect : getAllEffects())
			{
				if (effect.getSkill().isToggle())
				{
					effect.exit();
					continue;
				}
				
				switch (effect.getEffectType())
				{
					case SIGNET_GROUND:
					case SIGNET_EFFECT:
						effect.exit();
						break;
				}
			}
			
			// Remove the Player from the world
			decayMe();
			
			// If a party is in progress, leave it
			if (_party != null)
				_party.removePartyMember(this, MessageType.DISCONNECTED);
			
			// Handle removal from olympiad game
			if (OlympiadManager.getInstance().isRegistered(this) || getOlympiadGameId() != -1)
				OlympiadManager.getInstance().removeDisconnectedCompetitor(this);
			
			// set the status for pledge member list to OFFLINE
			if (getClan() != null)
			{
				final ClanMember clanMember = getClan().getClanMember(getObjectId());
				if (clanMember != null)
					clanMember.setPlayerInstance(null);
			}
			
			// deals with sudden exit in the middle of transaction
			if (getActiveRequester() != null)
			{
				setActiveRequester(null);
				cancelActiveTrade();
			}
			
			// If the Player is a GM, remove it from the GM List
			if (isGM())
				AdminData.getInstance().deleteGm(this);
			
			// Check if the Player is in observer mode to set its position to its position before entering in observer mode
			if (isInObserverMode())
				setXYZInvisible(_savedLocation);
			
			// Oust player from boat
			if (_boat != null)
				_boat.oustPlayer(this, MapRegionData.getInstance().getLocationToTeleport(this, TeleportType.TOWN));
			
			// Update inventory and remove them from the world
			getInventory().deleteMe();
			
			// Update warehouse and remove them from the world
			clearWarehouse();
			
			// Update freight and remove them from the world
			clearFreight();
			clearDepositedFreight();
			
			if (isCursedWeaponEquipped())
				CursedWeaponManager.getInstance().getCursedWeapon(_cursedWeaponEquippedId).setPlayer(null);
			
			if (_clan != null)
				_clan.broadcastToMembersExcept(this, new PledgeShowMemberListUpdate(this));
			
			if (isSeated())
			{
				final WorldObject object = World.getInstance().getObject(_throneId);
				if (object instanceof StaticObject)
					((StaticObject) object).setBusy(false);
			}
			
			World.getInstance().removePlayer(this); // force remove in case of crash during teleport
			
			// friends & blocklist update
			notifyFriends(false);
			getBlockList().playerLogout();
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't disconnect correctly the player.", e);
		}
	}
	
	public FishingStance getFishingStance()
	{
		return _fishingStance;
	}
	
	public int getMountNpcId()
	{
		return _mountNpcId;
	}
	
	public int getMountLevel()
	{
		return _mountLevel;
	}
	
	public void setMountObjectId(int id)
	{
		_mountObjectId = id;
	}
	
	public int getMountObjectId()
	{
		return _mountObjectId;
	}
	
	@Override
	public Map<Integer, L2Skill> getSkills()
	{
		return _skills;
	}
	
	public Punishment getPunishment()
	{
		return _punishment;
	}
	
	public RecipeBook getRecipeBook()
	{
		return _recipeBook;
	}
	
	/**
	 * @return true if the {@link Player} is jailed, false otherwise.
	 */
	public boolean isInJail()
	{
		return _punishment.getType() == PunishmentType.JAIL;
	}
	
	/**
	 * @return true if the {@link Player} is chat banned, false otherwise.
	 */
	public boolean isChatBanned()
	{
		return _punishment.getType() == PunishmentType.CHAT;
	}
	
	public int getPowerGrade()
	{
		return _powerGrade;
	}
	
	public void setPowerGrade(int power)
	{
		_powerGrade = power;
	}
	
	public boolean isCursedWeaponEquipped()
	{
		return _cursedWeaponEquippedId != 0;
	}
	
	public void setCursedWeaponEquippedId(int value)
	{
		_cursedWeaponEquippedId = value;
	}
	
	public int getCursedWeaponEquippedId()
	{
		return _cursedWeaponEquippedId;
	}
	
	public void shortBuffStatusUpdate(int magicId, int level, int time)
	{
		if (_shortBuffTask != null)
		{
			_shortBuffTask.cancel(false);
			_shortBuffTask = null;
		}
		
		_shortBuffTask = ThreadPool.schedule(() ->
		{
			sendPacket(new ShortBuffStatusUpdate(0, 0, 0));
			setShortBuffTaskSkillId(0);
		}, time * 1000L);
		setShortBuffTaskSkillId(magicId);
		
		sendPacket(new ShortBuffStatusUpdate(magicId, level, time));
	}
	
	public int getShortBuffTaskSkillId()
	{
		return _shortBuffTaskSkillId;
	}
	
	public void setShortBuffTaskSkillId(int id)
	{
		_shortBuffTaskSkillId = id;
	}
	
	public int getDeathPenaltyBuffLevel()
	{
		return _deathPenaltyBuffLevel;
	}
	
	public void setDeathPenaltyBuffLevel(int level)
	{
		_deathPenaltyBuffLevel = level;
	}
	
	/**
	 * Check and calculate if a new Death Penalty buff level needs to be added. If Death Penalty already applies, raise its level by 1.
	 * @param killer : The {@link Creature} who killed this {@link Player}.
	 */
	public void calculateDeathPenaltyBuffLevel(Creature killer)
	{
		if (_deathPenaltyBuffLevel >= 15) // maximum level reached
			return;
		
		if ((getKarma() > 0 || Rnd.get(1, 100) <= Config.DEATH_PENALTY_CHANCE) && !(killer instanceof Player) && !(getCharmOfLuck() && (killer == null || killer.isRaidRelated())) && !isPhoenixBlessed() && !(isInsideZone(ZoneId.PVP) || isInsideZone(ZoneId.SIEGE)))
		{
			if (_deathPenaltyBuffLevel != 0)
				removeSkill(5076, false);
			
			_deathPenaltyBuffLevel++;
			
			addSkill(SkillTable.getInstance().getInfo(5076, _deathPenaltyBuffLevel), false);
			
			sendPacket(new EtcStatusUpdate(this));
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DEATH_PENALTY_LEVEL_S1_ADDED).addNumber(_deathPenaltyBuffLevel));
		}
	}
	
	/**
	 * Reduce the Death Penalty buff effect from this {@link Player} of 1. If it reaches 0, remove it entirely.
	 */
	public void reduceDeathPenaltyBuffLevel()
	{
		if (_deathPenaltyBuffLevel <= 0)
			return;
		
		removeSkill(5076, false);
		
		_deathPenaltyBuffLevel--;
		
		if (_deathPenaltyBuffLevel > 0)
		{
			addSkill(SkillTable.getInstance().getInfo(5076, _deathPenaltyBuffLevel), false);
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DEATH_PENALTY_LEVEL_S1_ADDED).addNumber(_deathPenaltyBuffLevel));
		}
		else
			sendPacket(SystemMessageId.DEATH_PENALTY_LIFTED);
		
		sendPacket(new EtcStatusUpdate(this));
	}
	
	/**
	 * Remove the Death Penalty buff effect from this {@link Player}.
	 */
	public void removeDeathPenaltyBuffLevel()
	{
		if (_deathPenaltyBuffLevel <= 0)
			return;
		
		removeSkill(5076, false);
		
		_deathPenaltyBuffLevel = 0;
		
		sendPacket(SystemMessageId.DEATH_PENALTY_LIFTED);
		sendPacket(new EtcStatusUpdate(this));
	}
	
	public Collection<Timestamp> getReuseTimeStamps()
	{
		return _reuseTimeStamps.values();
	}
	
	public Map<Integer, Timestamp> getReuseTimeStamp()
	{
		return _reuseTimeStamps;
	}
	
	/**
	 * Index according to skill id the current timestamp of use.
	 * @param skill
	 * @param reuse delay
	 */
	@Override
	public void addTimeStamp(L2Skill skill, long reuse)
	{
		_reuseTimeStamps.put(skill.getReuseHashCode(), new Timestamp(skill, reuse));
	}
	
	/**
	 * Index according to skill this TimeStamp instance for restoration purposes only.
	 * @param skill
	 * @param reuse
	 * @param systime
	 */
	public void addTimeStamp(L2Skill skill, long reuse, long systime)
	{
		_reuseTimeStamps.put(skill.getReuseHashCode(), new Timestamp(skill, reuse, systime));
	}
	
	@Override
	public Player getActingPlayer()
	{
		return this;
	}
	
	@Override
	public final void sendDamageMessage(Creature target, int damage, boolean mcrit, boolean pcrit, boolean miss)
	{
		// Check if hit is missed
		if (miss)
		{
			sendPacket(SystemMessageId.MISSED_TARGET);
			return;
		}
		
		// Check if hit is critical
		if (pcrit)
			sendPacket(SystemMessageId.CRITICAL_HIT);
		if (mcrit)
			sendPacket(SystemMessageId.CRITICAL_HIT_MAGIC);
		
		if (target.isInvul())
		{
			if (target.isParalyzed())
				sendPacket(SystemMessageId.OPPONENT_PETRIFIED);
			else
				sendPacket(SystemMessageId.ATTACK_WAS_BLOCKED);
		}
		else
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_DID_S1_DMG).addNumber(damage));
		
		if (isInOlympiadMode() && target instanceof Player && ((Player) target).isInOlympiadMode() && ((Player) target).getOlympiadGameId() == getOlympiadGameId())
			OlympiadGameManager.getInstance().notifyCompetitorDamage(this, damage);
		
		if (isInTournamentMatch() && target instanceof Player && ((Player) target).isInTournamentMatch() && ((Player) target).getTournamentFightId() == getTournamentFightId())
			addTournamentMatchDamage(damage);
	}
	
	public void checkItemRestriction()
	{
		for (final ItemInstance item : getInventory().getPaperdollItems())
		{
			if (item.getItem().checkCondition(this, this, false))
				continue;
			
			/*
			 * if(isInOlympiadMode() && item.getEnchantLevel()>6)// MAX +6 in oly continue;
			 */
			
			useEquippableItem(item, item.isWeapon());
		}
	}
	
	/**
	 * A method used to test player entrance on no landing zone.<br>
	 * <br>
	 * If a player is mounted on a Wyvern, it launches a dismount task after 5 seconds, and a warning message.
	 */
	public void enterOnNoLandingZone()
	{
		if (getMountType() == 2)
		{
			if (_dismountTask == null)
				_dismountTask = ThreadPool.schedule(this::dismount, 5000);
			
			sendPacket(SystemMessageId.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN);
		}
	}
	
	/**
	 * A method used to test player leave on no landing zone.<br>
	 * <br>
	 * If a player is mounted on a Wyvern, it cancels the dismount task, if existing.
	 */
	public void exitOnNoLandingZone()
	{
		if (getMountType() == 2 && _dismountTask != null)
		{
			_dismountTask.cancel(true);
			_dismountTask = null;
		}
	}
	
	/**
	 * Remove player from BossZones (used on char logout/exit)
	 */
	public void removeFromBossZone()
	{
		for (final BossZone zone : ZoneManager.getInstance().getAllZones(BossZone.class))
			zone.removePlayer(this);
	}
	
	/**
	 * @return the number of charges this Player got.
	 */
	public int getCharges()
	{
		return _charges.get();
	}
	
	public void increaseCharges(int count, int max)
	{
		if (_charges.get() >= max)
		{
			sendPacket(SystemMessageId.FORCE_MAXLEVEL_REACHED);
			return;
		}
		
		restartChargeTask();
		
		if (_charges.addAndGet(count) >= max)
		{
			_charges.set(max);
			sendPacket(SystemMessageId.FORCE_MAXLEVEL_REACHED);
		}
		else
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FORCE_INCREASED_TO_S1).addNumber(_charges.get()));
		
		sendPacket(new EtcStatusUpdate(this));
	}
	
	public boolean decreaseCharges(int count)
	{
		if (_charges.get() < count)
			return false;
		
		if (_charges.addAndGet(-count) == 0)
			stopChargeTask();
		else
			restartChargeTask();
		
		sendPacket(new EtcStatusUpdate(this));
		return true;
	}
	
	public void clearCharges()
	{
		if (_charges.get() > 0)
		{
			_charges.set(0);
			sendPacket(new EtcStatusUpdate(this));
		}
	}
	
	/**
	 * Starts/Restarts the ChargeTask to Clear Charges after 10 Mins.
	 */
	private void restartChargeTask()
	{
		if (_chargeTask != null)
		{
			_chargeTask.cancel(false);
			_chargeTask = null;
		}
		
		_chargeTask = ThreadPool.schedule(this::clearCharges, 600000);
	}
	
	/**
	 * Stops the Charges Clearing Task.
	 */
	public void stopChargeTask()
	{
		if (_chargeTask != null)
		{
			_chargeTask.cancel(false);
			_chargeTask = null;
		}
	}
	
	public int getMailPosition()
	{
		return _mailPosition;
	}
	
	public void setMailPosition(int mailPosition)
	{
		_mailPosition = mailPosition;
	}
	
	/**
	 * @param z
	 * @return true if character falling now On the start of fall return false for correct coord sync !
	 */
	public final boolean isFalling(int z)
	{
		if (isDead() || getMove().getMoveType() != MoveType.GROUND)
			return false;
		
		if (System.currentTimeMillis() < _fallingTimestamp)
			return true;
		
		final int deltaZ = getZ() - z;
		if (deltaZ <= getBaseTemplate().getSafeFallHeight(getAppearance().getSex()))
			return false;
		
		final int damage = (int) Formulas.calcFallDam(this, deltaZ);
		if (damage > 0)
		{
			reduceCurrentHp(Math.min(damage, getStatus().getHp() - 1), null, false, true, null);
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FALL_DAMAGE_S1).addNumber(damage));
		}
		
		setFalling();
		
		return false;
	}
	
	/**
	 * Set falling timestamp
	 */
	public final void setFalling()
	{
		_fallingTimestamp = System.currentTimeMillis() + FALLING_VALIDATION_DELAY;
	}
	
	public boolean isAllowedToEnchantSkills()
	{
		if (isLocked())
			return false;
		
		if (AttackStanceTaskManager.getInstance().isInAttackStance(this))
			return false;
		
		if (getCast().isCastingNow())
			return false;
		
		if (isInBoat())
			return false;
		
		return true;
	}
	
	public List<Integer> getFriendList()
	{
		return _friendList;
	}
	
	public void selectFriend(Integer friendId)
	{
		if (!_selectedFriendList.contains(friendId))
			_selectedFriendList.add(friendId);
	}
	
	public void deselectFriend(Integer friendId)
	{
		if (_selectedFriendList.contains(friendId))
			_selectedFriendList.remove(friendId);
	}
	
	public List<Integer> getSelectedFriendList()
	{
		return _selectedFriendList;
	}
	
	private void restoreFriendList()
	{
		_friendList.clear();
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT friend_id FROM character_friends WHERE char_id = ? AND relation = 0"))
		{
			ps.setInt(1, getObjectId());
			
			try (ResultSet rset = ps.executeQuery())
			{
				while (rset.next())
				{
					final int friendId = rset.getInt("friend_id");
					if (friendId == getObjectId())
						continue;
					
					_friendList.add(friendId);
				}
			}
		}
		catch (final Exception e)
		{
			LOGGER.error("Couldn't restore {}'s friendlist.", e, getName());
		}
	}
	
	private void notifyFriends(boolean isOnline)
	{
		for (final int id : _friendList)
		{
			final Player friend = World.getInstance().getPlayer(id);
			if (friend != null)
			{
				friend.sendPacket(new L2FriendStatus(this, isOnline));
				
				if (isOnline)
					friend.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.FRIEND_S1_HAS_LOGGED_IN).addCharName(this));
			}
		}
	}
	
	public void selectBlock(Integer friendId)
	{
		if (!_selectedBlocksList.contains(friendId))
			_selectedBlocksList.add(friendId);
	}
	
	public void deselectBlock(Integer friendId)
	{
		if (_selectedBlocksList.contains(friendId))
			_selectedBlocksList.remove(friendId);
	}
	
	public List<Integer> getSelectedBlocksList()
	{
		return _selectedBlocksList;
	}
	
	@Override
	public void broadcastRelationsChanges()
	{
		for (final Player player : getKnownType(Player.class))
		{
			final int relation = getRelation(player);
			final boolean isAutoAttackable = isAttackableWithoutForceBy(player);
			
			player.sendPacket(new RelationChanged(this, relation, isAutoAttackable));
			if (_summon != null)
				player.sendPacket(new RelationChanged(_summon, relation, isAutoAttackable));
		}
	}
	
	@Override
	public void sendInfo(Player player)
	{
		if (_boat != null)
			getPosition().set(_boat.getPosition());
		
		if (getPolymorphTemplate() != null)
			player.sendPacket(new AbstractNpcInfo.PcMorphInfo(this, getPolymorphTemplate()));
		else
		{
			player.sendPacket(new CharInfo(this));
			
			if (isSeated())
			{
				final WorldObject object = World.getInstance().getObject(_throneId);
				if (object instanceof StaticObject)
					player.sendPacket(new ChairSit(getObjectId(), ((StaticObject) object).getStaticObjectId()));
			}
		}
		
		int relation = getRelation(player);
		boolean isAutoAttackable = isAttackableWithoutForceBy(player);
		
		player.sendPacket(new RelationChanged(this, relation, isAutoAttackable));
		if (_summon != null)
			player.sendPacket(new RelationChanged(_summon, relation, isAutoAttackable));
		
		relation = player.getRelation(this);
		isAutoAttackable = player.isAttackableWithoutForceBy(this);
		
		sendPacket(new RelationChanged(player, relation, isAutoAttackable));
		if (player.getSummon() != null)
			sendPacket(new RelationChanged(player.getSummon(), relation, isAutoAttackable));
		
		if (_boat != null)
			player.sendPacket(new GetOnVehicle(getObjectId(), _boat.getObjectId(), getBoatPosition()));
		
		switch (getOperateType())
		{
			case SELL:
			case PACKAGE_SELL:
				player.sendPacket(new PrivateStoreMsgSell(this));
				break;
			
			case BUY:
				player.sendPacket(new PrivateStoreMsgBuy(this));
				break;
			
			case MANUFACTURE:
				player.sendPacket(new RecipeShopMsg(this));
				break;
		}
	}
	
	@Override
	public double getCollisionRadius()
	{
		return (isMounted()) ? NpcData.getInstance().getTemplate(_mountNpcId).getCollisionRadius() : getBaseTemplate().getCollisionRadiusBySex(getAppearance().getSex());
	}
	
	@Override
	public double getCollisionHeight()
	{
		return (isMounted()) ? NpcData.getInstance().getTemplate(_mountNpcId).getCollisionHeight() : getBaseTemplate().getCollisionHeightBySex(getAppearance().getSex());
	}
	
	public boolean teleportRequest(Player requester, L2Skill skill)
	{
		if (_summonTargetRequest != null && requester != null)
			return false;
		
		_summonTargetRequest = requester;
		_summonSkillRequest = skill;
		return true;
	}
	
	public void teleportAnswer(int answer, int requesterId)
	{
		if (_summonTargetRequest == null)
			return;
		
		if (answer == 1 && _summonTargetRequest.getObjectId() == requesterId)
			SummonFriend.teleportTo(this, _summonTargetRequest, _summonSkillRequest);
		
		_summonTargetRequest = null;
		_summonSkillRequest = null;
	}
	
	public void activateGate(int answer, int type)
	{
		if (_requestedGate == null)
			return;
		
		if (answer == 1 && getTarget() == _requestedGate && _requestedGate.getClanHall() != null && getClan() != null && getClanId() == _requestedGate.getClanHall().getOwnerId() && hasClanPrivileges(Clan.CP_CH_OPEN_DOOR))
		{
			if (type == 1)
				_requestedGate.openMe();
			else if (type == 0)
				_requestedGate.closeMe();
		}
		
		_requestedGate = null;
	}
	
	public void setRequestedGate(Door door)
	{
		_requestedGate = door;
	}
	
	@Override
	public boolean polymorph(int npcId)
	{
		if (super.polymorph(npcId))
		{
			sendPacket(new UserInfo(this));
			return true;
		}
		return false;
	}
	
	@Override
	public void unpolymorph()
	{
		super.unpolymorph();
		sendPacket(new UserInfo(this));
	}
	
	@Override
	public void addKnownObject(WorldObject object)
	{
		sendInfoFrom(object);
	}
	
	@Override
	public void removeKnownObject(WorldObject object)
	{
		super.removeKnownObject(object);
		
		// send Server-Client Packet DeleteObject to the Player
		sendPacket(new DeleteObject(object, (object instanceof Player) && ((Player) object).isSeated()));
	}
	
	public final void refreshInfos()
	{
		for (final WorldObject object : getKnownType(WorldObject.class))
		{
			if (object instanceof Player && ((Player) object).isInObserverMode())
				continue;
			
			sendInfoFrom(object);
		}
	}
	
	/**
	 * teleToLocation method without Dimensional Rift check.
	 * @param loc : The Location to teleport.
	 */
	public final void teleToLocation(Location loc)
	{
		super.teleportTo(loc, 0);
	}
	
	@Override
	public final void teleportTo(Location loc, int randomOffset)
	{
		if (DimensionalRiftManager.getInstance().checkIfInRiftZone(getX(), getY(), getZ(), true))
		{
			sendMessage("You have been sent to the waiting room.");
			
			if (isInParty() && getParty().isInDimensionalRift())
				getParty().getDimensionalRift().usedTeleport(this);
			
			loc = DimensionalRiftManager.getInstance().getRoom((byte) 0, (byte) 0).getTeleportLoc();
		}
		super.teleportTo(loc, randomOffset);
	}
	
	private final void sendInfoFrom(WorldObject object)
	{
		// Send object info to player.
		object.sendInfo(this);
		
		if (object instanceof Creature)
		{
			// Send the state of the Creature to the Player.
			final Creature obj = (Creature) object;
			if (obj.hasAI())
				obj.getAI().describeStateToPlayer(this);
		}
	}
	
	/**
	 * @return true if this {@link Player} is currently wearing a Formal Wear.
	 */
	public boolean isWearingFormalWear()
	{
		final ItemInstance formal = getInventory().getItemFrom(Paperdoll.CHEST);
		return formal != null && formal.getItem().getBodyPart() == Item.SLOT_ALLDRESS;
	}
	
	public final void startFakeDeath()
	{
		_isFakeDeath = true;
		_isSittingNow = true;
		_isStanding = false;
		
		ThreadPool.schedule(() ->
		{
			_isSittingNow = false;
			_isSitting = true;
			
			getAI().notifyEvent(AiEventType.SAT_DOWN, null, null);
			
		}, (int) (3000 / getStatus().getMovementSpeedMultiplier()));
		
		broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_START_FAKEDEATH));
	}
	
	public final void stopFakeDeath(boolean removeEffects)
	{
		if (removeEffects)
			stopEffects(EffectType.FAKE_DEATH);
		
		// Start the grace period for this character (grace from mobs only)!
		setRecentFakeDeath();
		
		_isStandingNow = true;
		_isSitting = false;
		
		ThreadPool.schedule(() ->
		{
			_isStandingNow = false;
			_isStanding = true;
			_isFakeDeath = false;
			
			getAI().notifyEvent(AiEventType.STOOD_UP, null, null);
		}, (int) (2500 / getStatus().getMovementSpeedMultiplier()));
		
		broadcastPacket(new ChangeWaitType(this, ChangeWaitType.WT_STOP_FAKEDEATH));
		broadcastPacket(new Revive(this));
	}
	
	@Override
	public void onInteract(Player player)
	{
		switch (getOperateType())
		{
			case SELL:
			case PACKAGE_SELL:
				player.sendPacket(new PrivateStoreListSell(player, this));
				break;
			
			case BUY:
				player.sendPacket(new PrivateStoreListBuy(player, this));
				break;
			
			case MANUFACTURE:
				player.sendPacket(new RecipeShopSellList(player, this));
				break;
		}
	}
	
	public String getIP()
	{
		if (getClient().getConnection() == null)
			return "N/A IP";
		
		return getClient().getConnection().getInetAddress().getHostAddress();
	}
	
	public static void doNewChar(Player player, int time)
	{
		player.setNewChar(true);
		NewCharTaskManager.getInstance().add(player);
		long remainingTime = player.getMemos1().getLong("newEndTime", 0);
		if (remainingTime > 0)
		{
			player.getMemos1().set("newEndTime", remainingTime + TimeUnit.MINUTES.toMillis(time));
		}
		else
		{
			player.getMemos1().set("newEndTime", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(time));
			player.broadcastUserInfo();
		}
	}
	
	public static void removeNewChar(Player player)
	{
		NewCharTaskManager.getInstance().remove(player);
		player.getMemos1().set("newEndTime", 0);
		player.setNewChar(false);
		player.broadcastUserInfo();
		
	}
	
	private boolean _isnewChar;
	
	public boolean isNewChar()
	{
		return _isnewChar;
	}
	
	public void setNewChar(boolean b)
	{
		_isnewChar = b;
	}
	
	private Dungeon dungeon = null;
	
	public void setDungeon(Dungeon val)
	{
		dungeon = val;
	}
	
	public Dungeon getDungeon()
	{
		return dungeon;
	}
	
	private boolean _isInDungeonZone;
	
	public boolean isInDungeonZone()
	{
		return _isInDungeonZone;
	}
	
	public void deleteTempItem(int itemObjectID)
	{
		boolean destroyed = false;
		if (getInventory().getItemByObjectId(itemObjectID) != null)
		{
			sendMessage("Your " + ItemData.getInstance().getTemplate(getInventory().getItemByObjectId(itemObjectID).getItemId()).getName() + " has expired.");
			destroyItem("tempItemDestroy", itemObjectID, 1, this, true);
			getInventory().updateDatabase();
			sendPacket(new ItemList(this, true));
			
			destroyed = true;
		}
		
		if (!destroyed)
		{
			Connection con = null;
			PreparedStatement statement = null;
			ResultSet rset = null;
			try
			{
				con = ConnectionPool.getConnection();
				statement = con.prepareStatement("DELETE FROM items WHERE object_id=?");
				statement.setInt(1, itemObjectID);
				statement.execute();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				Mysql.closeQuietly(con, statement, rset);
			}
		}
	}
	
	private boolean _isInClanDungeonZone;
	
	public boolean isInClanDungeonZone()
	{
		return _isInClanDungeonZone;
	}
	
	public void setClanDungeonZone(boolean isInClanDungeonZone)
	{
		_isInClanDungeonZone = isInClanDungeonZone;
	}
	
	/**
	 * @param instance
	 * @param b
	 */
	/*public static void setInstance(Instance instance, boolean b)
	{
		return;
		
	} */
	
	/**
	 * Added to other GMs, test also this {@link Player} instance. If GM, set it.
	 */
	@Override
	public List<Player> getSurroundingGMs()
	{
		final List<Player> gms = super.getSurroundingGMs();
		if (isGM())
			gms.add(this);
		
		return gms;
	}
	
	public final long getPremiumExpire()
	{
		return getMemos().getLong("premium_expire", 0L);
	}
	
	public final void setPremiumExpire(long value)
	{
		getMemos().set("premium_expire", value);
	}
	
	public final boolean isPremium()
	{
		return getPremiumExpire() > System.currentTimeMillis();
	}
	
	@Override
	public void setInstanceId(int instanceId)
	{
		super.setInstanceId(instanceId);
		
		final Summon summon = getSummon();
		if (summon != null)
			summon.setInstanceId(instanceId);
	}
	
	@Override
	public void setInstance(Instance instance, boolean silent)
	{
		super.setInstance(instance, silent);
		
		final Summon summon = getSummon();
		if (summon != null)
			summon.setInstance(instance, silent);
		
		Pet agathion = World.getInstance().getAgat(getObjectId());
		if (agathion != null)
			agathion.setInstance(instance, silent);
		
		// Delete trained beasts
		if (_tamedBeast != null)
			_tamedBeast.setInstance(instance, silent);
	}
	
	
	public final int getInstanceJoins(InstanceType instanceType)
	{
		return getMemos().getInteger("instance_joins_" + instanceType.toString(), 0);
	}
	
	public final void setInstanceJoins(InstanceType instanceType, int instanceJoins)
	{
		getMemos().set("instance_joins_" + instanceType.toString(), instanceJoins);
	}
	
	// PaNtHeR fix Daily Reward
	/**
	 * @return the nextRewardTime
	 */
	public long getNextRewardTime()
	{
		return nextRewardTime;
	}
	
	/**
	 * @param nextRewardTime the nextRewardTime to set
	 */
	public void setNextRewardTime(long nextRewardTime)
	{
		this.nextRewardTime = nextRewardTime;
	}
	
	private long nextRewardTime;
	
	// Tournament
	
	private boolean tournamentTeamBeingInvited;
	private int tournamentFightId;
	private TournamentFightType tournamentFightType = TournamentFightType.NONE;
	private boolean inTournamentMatch;
	private int lastX;
	private int lastY;
	private int lastZ;
	private int tournamentPoints;
	private int tournamentMatchDamage;
	private Map<TournamentFightType, Integer> tournamentKills = new HashMap<>();
	private Map<TournamentFightType, Integer> tournamentVictories = new HashMap<>();
	private Map<TournamentFightType, Integer> tournamentDefeats = new HashMap<>();
	private Map<TournamentFightType, Integer> tournamentTies = new HashMap<>();
	private Map<TournamentFightType, Integer> tournamentDamage = new HashMap<>();
	
	public int getTournamentTotalDamage()
	{
		int count = 0;
		for (Map.Entry<TournamentFightType, Integer> entry : tournamentDamage.entrySet())
		{
			count += entry.getValue();
		}
		return count;
	}
	
	public int getTournamentTotalDamage(TournamentFightType type)
	{
		return tournamentDamage.get(type);
	}
	
	public int getTotalVictories()
	{
		int count = 0;
		for (Map.Entry<TournamentFightType, Integer> entry : tournamentVictories.entrySet())
		{
			count += entry.getValue();
		}
		return count;
	}
	
	public int getTotalDefeats()
	{
		int count = 0;
		for (Map.Entry<TournamentFightType, Integer> entry : tournamentDefeats.entrySet())
		{
			count += entry.getValue();
		}
		return count;
	}
	
	public int getTotalTies()
	{
		int count = 0;
		for (Map.Entry<TournamentFightType, Integer> entry : tournamentTies.entrySet())
		{
			count += entry.getValue();
		}
		return count;
	}
	
	/**
	 * @return Total of fights done by player
	 */
	public int getTotalTournamentFightsDone()
	{
		return getTournamentFightsDone(TournamentFightType.F1X1) + getTournamentFightsDone(TournamentFightType.F2X2) + getTournamentFightsDone(TournamentFightType.F3X3) + getTournamentFightsDone(TournamentFightType.F4X4) + getTournamentFightsDone(TournamentFightType.F5X5) + getTournamentFightsDone(TournamentFightType.F9X9);
	}
	
	/**
	 * @param type
	 * @return Total of fights of type done by player
	 */
	public int getTournamentFightsDone(TournamentFightType type)
	{
		return tournamentVictories.get(type) + tournamentDefeats.get(type) + tournamentTies.get(type);
	}
	
	/**
	 * @return if player is in a registered team o tournament
	 */
	public boolean isInTournamentMode()
	{
		return TournamentManager.getInstance().isInTournamentMode(this);
	}
	
	public TournamentTeam getTournamentTeam()
	{
		return tournamentTeam;
	}
	
	public void setTournamentTeam(TournamentTeam tournamentTeam)
	{
		this.tournamentTeam = tournamentTeam;
	}
	
	private TournamentTeam tournamentTeam;
	
	public boolean isInTournamentTeam()
	{
		return getTournamentTeam() != null;
	}
	
	/**
	 * @return if player received a invitation to a tour team
	 */
	public boolean isTournamentTeamBeingInvited()
	{
		return tournamentTeamBeingInvited;
	}
	
	public void setTournamentTeamBeingInvited(boolean tournamentTeamBeingInvited)
	{
		this.tournamentTeamBeingInvited = tournamentTeamBeingInvited;
	}
	
	public int getTournamentFightId()
	{
		return tournamentFightId;
	}
	
	public void setTournamentFightId(int tournamentFightId)
	{
		this.tournamentFightId = tournamentFightId;
	}
	
	public TournamentFightType getTournamentFightType()
	{
		return tournamentFightType;
	}
	
	public void setTournamentFightType(TournamentFightType tournamentFightType)
	{
		this.tournamentFightType = tournamentFightType;
	}
	
	/**
	 * @return the inTournamentMatch
	 */
	public boolean isInTournamentMatch()
	{
		return inTournamentMatch;
	}
	
	/**
	 * @param inTournamentMatch the inTournamentMatch to set
	 */
	public void setInTournamentMatch(boolean inTournamentMatch)
	{
		this.inTournamentMatch = inTournamentMatch;
	}
	
	/**
	 * @return the lastX
	 */
	public int getLastX()
	{
		return lastX;
	}
	
	/**
	 * @param lastX the lastX to set
	 */
	public void setLastX(int lastX)
	{
		this.lastX = lastX;
	}
	
	/**
	 * @return the lastY
	 */
	public int getLastY()
	{
		return lastY;
	}
	
	/**
	 * @param lastY the lastY to set
	 */
	public void setLastY(int lastY)
	{
		this.lastY = lastY;
	}
	
	/**
	 * @return the lastZ
	 */
	public int getLastZ()
	{
		return lastZ;
	}
	
	/**
	 * @param lastZ the lastZ to set
	 */
	public void setLastZ(int lastZ)
	{
		this.lastZ = lastZ;
	}
	
	/**
	 * @return the tournamentMatchDamage
	 */
	public int getTournamentMatchDamage()
	{
		return tournamentMatchDamage;
	}
	
	public void addTournamentMatchDamage(int damage)
	{
		tournamentMatchDamage += damage;
	}
	
	/**
	 * @param tournamentMatchDamage the tournamentMatchDamage to set
	 */
	public void setTournamentMatchDamage(int tournamentMatchDamage)
	{
		this.tournamentMatchDamage = tournamentMatchDamage;
	}
	
	/**
	 * @return the tournamentKills
	 */
	public Map<TournamentFightType, Integer> getTournamentKills()
	{
		return tournamentKills;
	}
	
	/**
	 * @param tournamentKills the tournamentKills to set
	 */
	public void setTournamentKills(Map<TournamentFightType, Integer> tournamentKills)
	{
		this.tournamentKills = tournamentKills;
	}
	
	public void addTournamentTie(TournamentFightType type)
	{
		increment(tournamentTies, type);
	}
	
	public void addTournamentDefeat(TournamentFightType type)
	{
		increment(tournamentDefeats, type);
	}
	
	public void addTournamentVictory(TournamentFightType type)
	{
		increment(tournamentVictories, type);
	}
	
	public void addTournamentKill(TournamentFightType type)
	{
		increment(tournamentKills, type);
	}
	
	public static <K> void increment(Map<K, Integer> map, K key)
	{
		map.merge(key, 1, Integer::sum);
		
	}
	
	public void addTournamentDamage(TournamentFightType type, int damage)
	{
		increment(tournamentDamage, type, damage);
	}
	
	public static <K> void increment(Map<K, Integer> map, K key, int toIncrement)
	{
		
		int val = map.get(key);
		map.put(key, val + toIncrement);
	}
	
	/**
	 * @return all kills of plaer in tournament
	 */
	public int getTotalTournamentKills()
	{
		int kills = 0;
		for (Map.Entry<TournamentFightType, Integer> entry : tournamentKills.entrySet())
		{
			kills += entry.getValue();
		}
		return kills;
	}
	
	/**
	 * @return the tournamentVictories
	 */
	public Map<TournamentFightType, Integer> getTournamentVictories()
	{
		return tournamentVictories;
	}
	
	/**
	 * @param tournamentVictories the tournamentVictories to set
	 */
	public void setTournamentVictories(Map<TournamentFightType, Integer> tournamentVictories)
	{
		this.tournamentVictories = tournamentVictories;
	}
	
	/**
	 * @return the tournamentDefeats
	 */
	public Map<TournamentFightType, Integer> getTournamentDefeats()
	{
		return tournamentDefeats;
	}
	
	/**
	 * @param tournamentDefeats the tournamentDefeats to set
	 */
	public void setTournamentDefeats(Map<TournamentFightType, Integer> tournamentDefeats)
	{
		this.tournamentDefeats = tournamentDefeats;
	}
	
	/**
	 * @return the tournamentTies
	 */
	public Map<TournamentFightType, Integer> getTournamentTies()
	{
		return tournamentTies;
	}
	
	/**
	 * @param tournamentTies the tournamentTies to set
	 */
	public void setTournamentTies(Map<TournamentFightType, Integer> tournamentTies)
	{
		this.tournamentTies = tournamentTies;
	}
	
	public void storeTournament()
	{
		
	}
	
	/**
	 * @return the tournamentDamage
	 */
	public Map<TournamentFightType, Integer> getTournamentDamage()
	{
		return tournamentDamage;
	}
	
	/**
	 * @param tournamentDamage the tournamentDamage to set
	 */
	public void setTournamentDamage(Map<TournamentFightType, Integer> tournamentDamage)
	{
		this.tournamentDamage = tournamentDamage;
	}
	
	/**
	 * @return the tournamentPoints
	 */
	public int getTournamentPoints()
	{
		return tournamentPoints;
	}
	
	/**
	 * @param tournamentPoints the tournamentPoints to set
	 */
	public void setTournamentPoints(int tournamentPoints)
	{
		this.tournamentPoints = tournamentPoints;
	}
	
	/**
	 * @return the tournamentTeamRequesterId
	 */
	public int getTournamentTeamRequesterId()
	{
		return tournamentTeamRequesterId;
	}
	
	/**
	 * @param tournamentTeamRequesterId the tournamentTeamRequesterId to set
	 */
	public void setTournamentTeamRequesterId(int tournamentTeamRequesterId)
	{
		this.tournamentTeamRequesterId = tournamentTeamRequesterId;
	}
	
	private int tournamentTeamRequesterId;
	
	// player variables
	private final Map<String, PlayerVar> vars = new ConcurrentHashMap<>();
	
	/**
	 * @return player memos.
	 */
	public Map<String, PlayerVar> getVariables()
	{
		return vars;
	}
	
	public void sendChatMessage(int objectId, SayType messageType, String charName, String text)
	{
		sendPacket(new CreatureSay(objectId, messageType, charName, text));
	}
	
	private int _hairTepmlate = 0;
	private int _faceTepmlate = 0;
	private int _chestTepmlate = 0;
	private int _legsTepmlate = 0;
	private int _glovesTepmlate = 0;
	private int _feetTepmlate = 0;
	private int _costumeItemObjId = 0;
	private int _costumeHeadItemObjId = 0;
	
	public int getCostumeitemObjId()
	{
		return _costumeItemObjId;
	}
	
	public int getCostumeHeaditemObjId()
	{
		return _costumeHeadItemObjId;
	}
	
	public int getTempHair()
	{
		return _hairTepmlate;
	}
	
	public int getTempFace()
	{
		return _faceTepmlate;
	}
	
	public int getTempChest()
	{
		return _chestTepmlate;
	}
	
	public int getTempLegs()
	{
		return _legsTepmlate;
	}
	
	public int getTempGloves()
	{
		return _glovesTepmlate;
	}
	
	public int getTempFeet()
	{
		return _feetTepmlate;
	}
	
	public void setTempHair(int itemId)
	{
		_hairTepmlate = itemId;
	}
	
	public void setTempFace(int itemId)
	{
		_faceTepmlate = itemId;
	}
	
	public void setTempChest(int itemId)
	{
		_chestTepmlate = itemId;
	}
	
	public void setTempLegs(int itemId)
	{
		_legsTepmlate = itemId;
	}
	
	public void setTempGloves(int itemId)
	{
		_glovesTepmlate = itemId;
	}
	
	public void setTempFeet(int itemId)
	{
		_feetTepmlate = itemId;
	}
	
	public void setCostumeitemObjId(int itemObjId)
	{
		_costumeItemObjId = itemObjId;
	}
	
	public void setCostumeHeaditemObjId(int HeaditemObjId)
	{
		_costumeHeadItemObjId = HeaditemObjId;
	}
	
	
	boolean autoLoot = false;
	
	public boolean getAutoLoot()
	{
		return autoLoot;
	}
	
	public void setAutoLoot(boolean auto)
	{
		autoLoot = auto;
	}
	
	public long _costumeInterval = 0;
	
	public long _costumeIntervalTime = 1000;
	
	public boolean getPermanentHero()
	{
		return _permanent_hero;
	}
	
	public void setPermanentHero()
	{
		if (_permanent_hero == false)
		{
			_permanent_hero = true;
		}
		else
		{
			_permanent_hero = false;
		}
	}
	
	public String getHWID()
	{
		return getClient().getHWID() != null ? getClient().getHWID() : this.getName();
	}
	
	// c1c0s vote manager start
	private boolean _isVoting1;
	
	public boolean isVoting1()
	{
		return _isVoting1;
	}
	
	public void setIsVoting1(boolean isVoting1)
	{
		_isVoting1 = isVoting1;
	}
	
	@SuppressWarnings("serial")
	private Map<VoteSites, Long> _lastVoteTimestamps = new HashMap<>()
	{
		{
			put(VoteSites.L2JBRASIL, -1L);
			put(VoteSites.L2NETWORK, -1L);
			put(VoteSites.L2TOPCO, -1L);
			put(VoteSites.L2TOPZONE, -1L);
			put(VoteSites.L2HOPZONE, -1L);
			put(VoteSites.L2TOPSERVERS, -1L);
			put(VoteSites.L2TOPGAMESERVER, -1L);
			put(VoteSites.L2VOTESCOM, -1L);
			put(VoteSites.L2SERVERSCOM, -1L);
			put(VoteSites.L2ITOPZ, -1L);
			put(VoteSites.L2JTOPCOM, -1L);
			put(VoteSites.GAMETOPSEU, -1L);
			put(VoteSites.TOP100ARENA, -1L);
			put(VoteSites.L2NET, -1L);
			put(VoteSites.L2TOPEU, -1L);
		}
	};
	
	public long getLastVotedTimestamp(VoteSites voteSite)
	{
		return _lastVoteTimestamps.get(voteSite);
	}
	
	public void setLastVotedTimestamp(VoteSites voteSite, long timestamp)
	{
		_lastVoteTimestamps.put(voteSite, timestamp);
	}
	
	public boolean isEligibleToVote(VoteSites voteSite)
	{
		if (getLastVotedTimestamp(voteSite) == -1)
		{
			return VoteDay.canVoteOnSite(this, voteSite);
		}
		
		return (getLastVotedTimestamp(voteSite) + 43200000) < System.currentTimeMillis();
	}
	
	public String getVoteCountdown(VoteSites voteSite)
	{
		if (getLastVotedTimestamp(voteSite) == -1)
		{
			if (VoteDay.canVoteOnSite(this, voteSite))
				return "You can now vote";
		}
		
		long youCanVote = getLastVotedTimestamp(voteSite) - (System.currentTimeMillis() - 43200000);
		if (youCanVote <= 0)
			return "You can now vote";
		return StringUtil.convertLongToCountdownN(youCanVote);
	}
	
	public String getIpAddress()
	{
		return getClient().getConnection().getInetAddress().getHostAddress();
	}
	
	private int voteDoneToSites = 0;
	
	public void setVotesToSites(int x)
	{
		voteDoneToSites += x;
	}
	
	public int getVotesToSites()
	{
		return voteDoneToSites;
	}
	
	public void initVotes()
	{
		voteDoneToSites = 0;
	}
	
	private Map<String, Integer> hwidVotesDone = new HashMap<>();
	
	public void addHwidVotes(String hwid, int x)
	{
		hwidVotesDone.put(hwid, voteDoneToSites + x);
	}
	
	public void repHwidVotes(String hwid, int x)
	{
		hwidVotesDone.replace(hwid, x);
	}
	
	public void remHwidVotes(String hwid)
	{
		if (hwidVotesContain(hwid))
			hwidVotesDone.remove(hwid);
	}
	
	public int getHwidVotes(String hwid)
	{
		return hwidVotesDone.get(hwid);
	}
	
	public boolean hwidVotesContain(String hwid)
	{
		return hwidVotesDone.containsKey(hwid);
	}
	
	public String gethwid()
	{
		if (getClient().getHWID() != null)
			return getClient().getHWID();
		else if (getClient().getHWID() != null)
			return getClient().getHWID();
		else
			return getIP();
	}
	
	// c1c0s vote manager end
	public void setVIPUntil(long val)
	{
		VIPUntil = val;
	}
	
	public long getVIPUntil()
	{
		return VIPUntil;
	}
	
	public String getRank()
	{
		if(getPvpKills()>=0 && getPvpKills() <500)
			return 
				"<img src=\"town_map.wooden\" width=80 height=80>";

		else if(getPvpKills()>=500 && getPvpKills() <1500)
			return 
				"<img src=\"town_map.iron\" width=80 height=80>";

		else if(getPvpKills()>=1500 && getPvpKills() <2500)
			return 
				 "<img src=\"town_map.bronze\" width=80 height=80>";

		else if(getPvpKills()>=2500 && getPvpKills() <3500)
			return 
				 "<img src=\"town_map.silver\" width=80 height=80>";
				
		else if(getPvpKills()>=3500 && getPvpKills() <4500)
			return 
				"<img src=\"town_map.gold\" width=80 height=80>";

		else if(getPvpKills()>=4500 && getPvpKills() <10000)
			return 
				 "<img src=\"town_map.platinum\" width=80 height=80>";

		else if(getPvpKills()>=10000 && getPvpKills() <20000)
			return 
				"<img src=\"town_map.diamond\" width=80 height=80>";

			return 
			"<img src=\"town_map.legendary\" width=80 height=80>";
// more than 20K PvPs
		
	}
	
	
	public String getRankColor_panel()
	{
		if(getPvpKills()>=0 && getPvpKills() <500)
			return 
				"<font color=\"B06311\">";

		else if(getPvpKills()>=500 && getPvpKills() <1500)
			return 
				"<font color=\"999999\">";

		else if(getPvpKills()>=1500 && getPvpKills() <2500)
			return 
				"<font color=\"D2AA7F\">";

		else if(getPvpKills()>=2500 && getPvpKills() <3500)
			return 
				"<font color=\"BDB2B2\">";
				
		else if(getPvpKills()>=3500 && getPvpKills() <4500)
			return 
				"<font color=\"F1B032\">";

		else if(getPvpKills()>=4500 && getPvpKills() <10000)
			return 
				"<font color=\"BDB2B2\">";

		else if(getPvpKills()>=10000 && getPvpKills() <20000)
			return 
				"<font color=\"A7A3DF\">";

			return 
				"<font color=\"FF0000\">";
// more than 20K PvPs
	}
	
	public String getRankColor()
	{
		if(getPvpKills()>=0 && getPvpKills() <500)
			return 
				"<font color=FFFFFF>";

		else if(getPvpKills()>=500 && getPvpKills() <1500)
			return 
				"<font color=C9C4A2>";

		else if(getPvpKills()>=1500 && getPvpKills() <2500)
			return 
				"<font color=47B976>";

		else if(getPvpKills()>=2500 && getPvpKills() <3500)
			return 
				"<font color=FFFF00>";
				
		else if(getPvpKills()>=3500 && getPvpKills() <4500)
			return 
				"<font color=F1461C>";

		else if(getPvpKills()>=4500 && getPvpKills() <10000)
			return 
				"<font color=26DFD0>";

		else if(getPvpKills()>=10000 && getPvpKills() <20000)
			return 
				"<font color=853537>";

			return 
				"<font color=B46AA0>";
// more than 20K PvPs
	}
	
	public String get_rank_by_text() //Text 
	{
		if(getPvpKills()>=0 && getPvpKills() <500)
			return "Wooden";
		else if(getPvpKills()>=500 && getPvpKills() <1500)
			return "Iron";
		else if(getPvpKills()>=1500 && getPvpKills() <2500)
			return "";
		else if(getPvpKills()>=2500 && getPvpKills() <3500)
			return "";
		else if(getPvpKills()>=3500 && getPvpKills() <4500)
			return "";
		else if(getPvpKills()>=4500 && getPvpKills() <10000)
			return "";
		else if(getPvpKills()>=10000 && getPvpKills() <20000)
			return "";

			return "";// more than 20K PvPs
	}
	
	/*public String get_rank_by_text() //Text + color
	{
		if(getPvpKills()>=0 && getPvpKills() <500)
			return "<font color=\"FFFFFF\">Wooden";
		else if(getPvpKills()>=500 && getPvpKills() <1500)
			return "<font color=\"C9C4A2\">Iron";
		else if(getPvpKills()>=1500 && getPvpKills() <2500)
			return "<font color=\"47B976\">Bronze";
		else if(getPvpKills()>=2500 && getPvpKills() <3500)
			return "<font color=\"FFFF00\">Silver";
		else if(getPvpKills()>=3500 && getPvpKills() <4500)
			return "<font color=\"F1461C\">Gold";
		else if(getPvpKills()>=4500 && getPvpKills() <10000)
			return "<font color=\"26DFD0\">Platinum";
		else if(getPvpKills()>=10000 && getPvpKills() <20000)
			return "<font color=\"853537\">Diamond";

			return "<font color=\"B46AA0\">Legendary";// more than 20K PvPs
	}*/
	
	
	
	public String isVIP_panel()
	{
		if(isVIP())
			return "<img src=\"CustomIconPack01.premium-member\" width=60 height=50>";
		
		return " ";
	}
	
	public String isOrNotVIP_panel()
	{
		if(isVIP())
			return "<img src=\"CustomIconPack01.premium-member\" width=60 height=50>";
		
		return "<img src=\"CustomIconPack01.no-premium-member\" width=60 height=56>";
	}
	
	/*public String getRace_byIcon()
	{
		if(getAppearance().getSex() == Sex.MALE )
			//if(isMageClass())//MALE MAGE
			//{
				switch(getRace())
				{
				case ELF:
					return "<img src=\"CustomIconPack01.MElf_h02_m005_i00\" width=64 height=64>";
				case DARK_ELF:
					return "<img src=\"CustomIconPack01.MDarkElf_h05_m005_v_i00\" width=64 height=64>";
				case DWARF:
					return  "<img src=\"CustomIconPack01.Mdwarf_h09_m005_i00\" width=64 height=64>";
				case HUMAN:
					return "<img src=\"CustomIconPack01.MFighter_h02_m005_i00\" width=64 height=64>";
				case ORC:
					return  "<img src=\"CustomIconPack01.MShaman_h02_m005_v_i00\" width=64 height=64>";
				}	
		
		else if(getAppearance().getSex() == Sex.FEMALE)
			//if(isMageClass())
				switch(getRace())//FEMALE MAGE
				{
				case ELF:
					return "<img src=\"CustomIconPack01.FElf_h10_m005_i00\" width=64 height=64>";
				case DARK_ELF:
					return "<img src=\"CustomIconPack01.FDarkelf_h04_m005_i00\" width=64 height=64>";
				case DWARF:
					return  "<img src=\"CustomIconPack01.Fdwarf_h06_m005_v_i00\" width=64 height=64>";
				case HUMAN:
					return "<img src=\"CustomIconPack01.FMagic_h02_m005_i00\" width=64 height=64>";
				case ORC:
					return  "<img src=\"CustomIconPack01.FOrc_h02_m005_i00\" width=64 height=64>";
				}					
		return "<img src=\"\" width=1 height=1>";		
	}*/
	
	public String getClass_byIcon()
	{
		if(getAppearance().getSex() == Sex.MALE )
				switch(getBaseClass()) 
				{
				case 18:
				case 19:
				case 20:
				case 21:
				case 99:
				case 100:
					return "<img src=\"CustomIconPack01.MElf_h01_m005_i00\" width=64 height=64>";//ELF Tanks
				case 22:
				case 23:
				case 101:
					return "<img src=\"CustomIconPack01.MElf_h04_m005_i00\" width=64 height=64>";//ELF Dagger	
				case 24:
				case 102:
					return "<img src=\"CustomIconPack01.Melf_h09_m005_i00\" width=64 height=64>";//Elf Archer
				case 25:
				case 26:
				case 27:
				case 103:
					return  "<img src=\"CustomIconPack01.MElf_h02_m005_v_i00\" width=64 height=64>"; // Elf Mage;	
				case 28:
				case 29:
				case 30:
				case 104:
				case 105:
					return  "<img src=\"CustomIconPack01.MElf_h10_m005_i00\" width=64 height=64>"; // Elf Mage Support
				case 0:
				case 1:
				case 2:
				case 3:
				case 88:
				case 89:
					return "<img src=\"CustomIconPack01.MFighter_h02_m005_i00\" width=64 height=64>"; // Human Fighter Classes(Warlord/Duelist)
				case 4:
				case 5:
				case 6:
				case 90:
				case 91:
					return  "<img src=\"CustomIconPack01.MFighter_h02_m005_v_i00\" width=64 height=64>";//Human Tanks
				case 7:
				case 8:
				case 93:
					return  "<img src=\"CustomIconPack01.MFighter_h04_m005_i00\" width=64 height=64>";//Human Dagger
				case 9:
				case 92:
					return  "<img src=\"CustomIconPack01.MFighter_h01_m005_i00\" width=64 height=64>";//Human Archer
				case 10:
				case 11:
				case 12:
				case 13:
				case 14:	
				case 94:
				case 95:
				case 96:
					return  "<img src=\"CustomIconPack01.MMagic_h10_m005_i00\" width=64 height=64>";//Human Mage Support
				case 15:
				case 16:
				case 17:
				case 97:
				case 98:	
					return  "<img src=\"CustomIconPack01.MMagic_h06_m005_v_i00\" width=64 height=64>";//Human Mage
				case 31:
				case 32:
				case 33:
				case 34:
				case 106:
				case 107:
					return "<img src=\"CustomIconPack01.MDarkElf_h09_m005_i00\" width=64 height=64>";//Dark ELF Tanks
				case 35:
				case 36:
				case 108:
					return "<img src=\"CustomIconPack01.MDarkElf_h04_m005_i00\" width=64 height=64>";//Dark ELF Dagger	
				case 37:
				case 109:
					return "<img src=\"CustomIconPack01.MDarkElf_h06_m005_v_i00\" width=64 height=64>";// Dark Elf Archer
				case 38:
				case 39:
				case 40:
				case 110:
					return  "<img src=\"CustomIconPack01.MDarkElf_h05_m005_i00\" width=64 height=64>"; //Dark Elf Mage;	
				case 41:
				case 42:
				case 43:
				case 111:
				case 112:	
					return  "<img src=\"CustomIconPack01.MDarkElf_h02_m005_v_i00\" width=64 height=64>"; //Dark Elf Mage Support
				case 47:
				case 48:
				case 114:
					return  "<img src=\"CustomIconPack01.MOrc_h12_m005_i00\" width=64 height=64>"; //Orc Tyrant;	
				case 44:
				case 45:
				case 46:
				case 113:
					return  "<img src=\"CustomIconPack01.Morc_h09_m005_i00\" width=64 height=64>"; //Orc Titan
				case 49:
				case 50:
				case 51:
				case 115:
					return  "<img src=\"CustomIconPack01.MOrc_h05_m005_i00\" width=64 height=64>"; //Dominator	
				case 52:
				case 116:
					return  "<img src=\"CustomIconPack01.MOrc_h01_m005_v_i00\" width=64 height=64>"; //Doomcryer
				case 56:
				case 57:
				case 118:
					return  "<img src=\"CustomIconPack01.MDwarf_h01_m005_i00\" width=64 height=64>"; //Dwarf Maestro	
				case 53:
				case 54:
				case 55:
				case 117:
					return  "<img src=\"CustomIconPack01.Mdwarf_h09_m005_i00\" width=64 height=64>"; //Dwarf Fortune Seeker
				}	
		
		else if(getAppearance().getSex() == Sex.FEMALE)
				switch(getBaseClass())//FEMALE
				{
					case 18:
					case 19:
					case 20:
					case 21:
					case 99:
					case 100:
						return "<img src=\"CustomIconPack01.Felf_h02_m005_i00\" width=64 height=64>";//ELF Tanks
					case 22:
					case 23:
					case 101:
						return "<img src=\"CustomIconPack01.Felf_h04_m005_i00\" width=64 height=64>";//ELF Dagger	
					case 24:
					case 102:
						return "<img src=\"CustomIconPack01.Felf_h01_m005_i00\" width=64 height=64>";//Elf Archer
					case 25:
					case 26:
					case 27:
					case 103:
						return  "<img src=\"CustomIconPack01.Felf_h06_m005_v_i00\" width=64 height=64>"; // Elf Mage;	
					case 28:
					case 29:
					case 30:
					case 104:
					case 105:
						return  "<img src=\"CustomIconPack01.FElf_h10_m005_i00\" width=64 height=64>"; // Elf Mage Support
					case 0:
					case 1:
					case 2:
					case 3:
					case 88:
					case 89:
						return "<img src=\"CustomIconPack01.FFighter_h02_m005_i00\" width=64 height=64>"; // Human Fighter Classes
					case 4:
					case 5:
					case 6:
					case 90:
					case 91:
						return  "<img src=\"CustomIconPack01.FFighter_h05_m005_i00\" width=64 height=64>";//Human Tanks
					case 7:
					case 8:
					case 93:
						return  "<img src=\"CustomIconPack01.FFighter_h04_m005_i00\" width=64 height=64>";//Human Dagger
					case 9:
					case 92:
						return  "<img src=\"CustomIconPack01.FFighter_h01_m005_v_i00\" width=64 height=64>";//Human Archer
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:	
					case 94:
					case 95:
					case 96:
						return  "<img src=\"CustomIconPack01.FMagic_h01_m005_v_i00\" width=64 height=64>";//Human Mage Support
					case 15:
					case 16:
					case 17:
					case 97:
					case 98:	
						return  "<img src=\"CustomIconPack01.FMagic_h02_m005_v_i00\" width=64 height=64>";//Human Mage
					case 31:
					case 32:
					case 33:
					case 34:
					case 106:
					case 107:
						return "<img src=\"CustomIconPack01.FDarkelf_h05_m005_v_i00\" width=64 height=64>";//Dark ELF Tanks
					case 35:
					case 36:
					case 108:
						return "<img src=\"CustomIconPack01.FDarkelf_h04_m005_i00\" width=64 height=64>";//Dark ELF Dagger	
					case 37:
					case 109:
						return "<img src=\"CustomIconPack01.FDarkElf_h10_m005_i00\" width=64 height=64>";// Dark Elf Archer
					case 38:
					case 39:
					case 40:
					case 110:
						return  "<img src=\"CustomIconPack01.FDarkelf_h02_m005_i00\" width=64 height=64>"; //Dark Elf Mage;	
					case 41:
					case 42:
					case 43:
					case 111:
					case 112:	
						return  "<img src=\"CustomIconPack01.FDarkelf_h02_m005_v_i00\" width=64 height=64>"; //Dark Elf Mage Support
					case 47:
					case 48:
					case 114:
						return  "<img src=\"CustomIconPack01.FOrc_h06_m005_i00\" width=64 height=64>"; //Orc Tyrant;	
					case 44:
					case 45:
					case 46:
					case 113:
						return  "<img src=\"CustomIconPack01.FOrc_h12_m005_i00\" width=64 height=64>"; //Orc Titan
					case 49:
					case 50:
					case 51:
					case 115:
						return  "<img src=\"CustomIconPack01.FOrc_h01_m005_i00\" width=64 height=64>"; //Dominator	
					case 52:
					case 116:
						return  "<img src=\"CustomIconPack01.FOrc_h02_m005_v_i00\" width=64 height=64>"; //Doomcryer
					case 56:
					case 57:
					case 118:
						return  "<img src=\"CustomIconPack01.Fdwarf_h06_m005_v_i00\" width=64 height=64>"; //Dwarf Maestro	
					case 53:
					case 54:
					case 55:
					case 117:
						return  "<img src=\"CustomIconPack01.Fdwarf_h05_m005_v_i00\" width=64 height=64>"; //Dwarf Fortune Seeker
				}					
		return "<img src=\"\" width=1 height=1>";		
	}
	
	public String getRaceIcon()
	{
				switch(getRace())
				{
				case ELF:
					return "<img src=\"CustomIconPack01.MElf_h02_m005_i00\" width=64 height=64>";
				case DARK_ELF:
					return "<img src=\"CustomIconPack01.MDarkElf_h05_m005_v_i00\" width=64 height=64>";
				case DWARF:
					return  "<img src=\"CustomIconPack01.Mdwarf_h09_m005_i00\" width=64 height=64>";
				case HUMAN:
					return "<img src=\"CustomIconPack01.MFighter_h02_m005_i00\" width=64 height=64>";
				case ORC:
					return  "<img src=\"CustomIconPack01.MShaman_h02_m005_v_i00\" width=64 height=64>";
				default:
					return " ";
				}
				
	}
	public static String get_PlayerClassColor(Player color_player)
	{
		if(color_player.isMageClass())
			return "<font color=\"88B4DD\">";
		
		return "<font color=\"C35053\">";
				
	}
	
/*	public void store_pvpzone_name(Player pl) // store original name
	{
	if(pl.isInsidePvPZone() && !pl.isInsideClanwarZone())
		if(pl.getName()!="*Enemy*")
			original_name=pl.getName();	
	}
	
	public void store_pvpzone_title(Player pl) // store original title
	{
	if(pl.isInsidePvPZone() && !pl.isInsideClanwarZone())
		if(pl.getName()!="*Enemy*")
			original_title=pl.getTitle();	
	}
	
	public void store_pvpzone_clan_and_ally_crest(Player pl) // store original clan and ally crest id
	{
	if(pl.isInsidePvPZone() && !pl.isInsideClanwarZone())
		if(pl.getName()!="*Enemy*")
		{
			original_clancrest=pl.getClan().getCrestId();	
			original_allycrest=pl.getClan().getAllyCrestId();
		}
	}
	
	public static void set_pvpzone_name(Player pl) // set custom name
	{
	if(pl.isInsidePvPZone() && !pl.isInsideClanwarZone())
		if(pl.getName()!="*Enemy*")
			pl.setName("*Enemy*");
	}
	
	public void set_pvpzone_title(Player pl) // set custom title
	{
	if(pl.isInsidePvPZone() && !pl.isInsideClanwarZone())
			pl.setTitle("Rank: "+get_rank_by_text());
	}
	
	public static void set_pvpzone_clan_and_ally_crest(Player pl) // set custom clan and ally crest
	{
	if(pl.isInsidePvPZone() && !pl.isInsideClanwarZone())
		{
		pl.getClan().changeClanCrest(0);
		//pl.getClan().changeAllyCrest(0, true);.
		}
	}
	
	public void restore_original_name(Player pl) // restore original name
	{
	if(!pl.isInsidePvPZone() && !pl.isInsideClanwarZone())
		if(pl.getName()!=original_name)
			pl.setName(original_name);	
	}
	

	public void restore_original_title(Player pl) // restore original title
	{
	if(!pl.isInsidePvPZone() && !pl.isInsideClanwarZone())
		if(pl.getTitle()!=original_title)
			pl.setTitle(original_title);	
	}

	
	public void announceme_pvpzone_setoriginalname(Player pl) //store a pair of object_id and original name
		{
			_pvpzoneNames.put(pl.getObjectId(), original_name);
		}
	
	public static void announce_pvpzone_originalname(Player pl) //get original player's name
	{
		_pvpzoneNames.containsKey(pl)
	}

*/
	public static String getRank_miniicons(int pvps)
	{
		if(pvps>=0 && pvps <500)
			return 
				"<img src=\"CustomIconPack01.mini_wooden\" width=24 height=24>";
		else if(pvps>=500 && pvps <1500)
			return 
				"<img src=\"CustomIconPack01.mini_iron\" width=24 height=24>";
		else if(pvps>=1500 && pvps <2500)
			return 
				 "<img src=\"CustomIconPack01.mini_bronze\" width=24 height=24>";
		else if(pvps>=2500 && pvps <3500)
			return 
				 "<img src=\"CustomIconPack01.mini_silver\" width=24 height=24>";			
		else if(pvps>=3500 && pvps <4500)
			return 
				"<img src=\"CustomIconPack01.mini_gold\" width=24 height=24";
		else if(pvps>=4500 && pvps <10000)
			return 
				 "<img src=\"CustomIconPack01.mini_platinum\" width=24 height=24>";
		else if(pvps>=10000 && pvps<20000)
			return 
				"<img src=\"CustomIconPack01.mini_diamond\" width=24 height=24>";
		
			return 
			"<img src=\"CustomIconPack01.mini_legendary\" width=24 height=24>";
// more than 20K PvPs
		
	}
	
	 public String board_getRankColor(int pvps)
		{
			if(pvps>=0 && pvps <500)
				return 
					"<font color=\"FFFFFF\">";

			else if(pvps>=500 && pvps <1500)
				return 
					"<font color=\"C9C4A2\">";

			else if(pvps>=1500 && pvps <2500)
				return 
					"<font color=\"47B976\">";

			else if(pvps>=2500 && pvps <3500)
				return 
					"<font color=\"FFFF00\">";
					
			else if(pvps>=3500 && pvps <4500)
				return 
					"<font color=\"F1461C\">";

			else if(pvps>=4500 && pvps <10000)
				return 
					"<font color=\"26DFD0\">";

			else if(pvps>=10000 && pvps <20000)
				return 
					"<font color=\"853537\">";

				return 
					"<font color=\"B46AA0\">";
	// more than 20K PvPs
		}
	
	//@SuppressWarnings("deprecation")
	private void sendSiegeInfo()
	{
		TimeZone timeZone = TimeZone.getTimeZone("GMT+3");

		for (Castle castle : CastleManager.getInstance().getCastles())
			if(castle.getCastleId() == 5 || castle.getCastleId() == 3 || castle.getCastleId() == 1)
			{
				//sendPacket(new CreatureSay(0, SayType.ALLIANCE, "[Siege System]" ,castle.getName() +" Castle siege on: "+DateFormat.getDateTimeInstance().format(castle.getSiegeDate().getTime())));
	    		sendPacket(new CreatureSay(0, SayType.ALLIANCE, "["+ castle.getName() +"]" ," Castle siege on: "+formatTime(castle.getSiegeDate().getTime(),timeZone)+ " GMT+3"));
	    		   //sendMessage(String.format("%s Castle siege on: %s", castle.getName(), castle.getSiegeDate().getTime())); 	 
			}       
	}
	
	public static String formatTime(Date date, TimeZone timeZone) 
	{   
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        sdf.setTimeZone(timeZone);
        
        return sdf.format(date);
    }
	
	@Override
	public void handleShiftClickAction(Player player)
	{
		if(! (player.getTarget() instanceof Player) || player.getTarget().getActingPlayer().isInsidePvPZone() )
		{
		//player.sendMessage("You can not inspect a player inside this area.");
		return;
		}
			player.sendPlayerInfo(player.getTarget()); //player info panel
	}

		public void sendPlayerInfo(WorldObject target_)
		{
			if (target_ == null)
				return;
			
		
		// Set the target_ of the Player activeChar
		StringBuilder player_panel = new StringBuilder("");	
		
		
			player_panel.append("<center><table border=0 width=310 cellspacing=3 cellpadding=0><tr><td align=left width=10></td><td align=center>"+Player.getRank_miniicons(target_.getActingPlayer().getPvpKills())+"</td>");
			player_panel.append("<td align=left><font color=\"93C47D\">["+board_getRankColor(target_.getActingPlayer().getPvpKills())+ target_.getName() +"</font>"+" Inspection]</font></td></tr></table><br>");
			player_panel.append("<font color=D9EAD3><table border=0 width=280>");
			//player_panel.append("<tr><td><font color=ECC39A>Name:</font> </td><td <font color=666666>");
			//player_panel.append(String.valueOf((target_).getActingPlayer().getName()));
			//player_panel.append("</font></td></tr>");
			player_panel.append("<tr><td><font color=ECC39A>Clan:</font> </td><td <font color=999999>");
			player_panel.append(String.valueOf(target_.getActingPlayer().getClan() != null ? target_.getActingPlayer().getClan().getName() : "-"));
			player_panel.append("</font></td></tr>");
			player_panel.append("<tr><td><font color=ECC39A>Level:</font> </td><td <font color=666666>");
			player_panel.append(String.valueOf(target_.getActingPlayer().getStatus().getLevel()));
			player_panel.append("</font></td></tr>");
			
			player_panel.append("<tr><td><font color=ECC39A>Rank:</font> </td><td <font color=FFE599>");
			player_panel.append(String.valueOf(target_.getActingPlayer().get_rank_by_text()));
			player_panel.append("</font></td></tr>");
			
			player_panel.append("<tr><td><font color=ECC39A>Class:</font> </td><td>");
			player_panel.append(target_.getActingPlayer().isMageClass() ? "<font color=6FA8DC>" +  target_.getActingPlayer().getClassId().toString() +"</font>" : "<font color=EA9999>"  +  target_.getActingPlayer().getClassId().toString() +"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("<tr><td><font color=ECC39A>Base Class:</font> </td><td>");
			player_panel.append(target_.getActingPlayer().getBaseTemplate().getClassId().getType() != ClassType.FIGHTER ?   "<font color=6FA8DC>" +  target_.getActingPlayer().getBaseTemplate().getClassId().toString() : "<font color=EA9999>" + target_.getActingPlayer().getBaseTemplate().getClassId().toString()); 
			player_panel.append("</td></tr>");
			/*
			 * player_panel.append("<tr><td><font color=ECC39A>CP:</font> </td><td <font color=FCE5CD>");
			player_panel.append(String.valueOf((int) (target_).getActingPlayer().getStatus().getCp()));
			player_panel.append("</font>/<font color=FCE5CD>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getStatus().getMaxCp()));
			player_panel.append("</font></td></tr>");
			player_panel.append("<tr><td><font color=ECC39A>HP:</font> </td><td <font color=999999>");
			player_panel.append(String.valueOf((int) (target_).getActingPlayer().getStatus().getHp()));
			player_panel.append("</font>/<font color=999999>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getStatus().getMaxHp()));
			player_panel.append("</font></td></tr>");
			player_panel.append("<tr><td><font color=ECC39A>MP:</font> </td><td <font color=999999>");
			player_panel.append(String.valueOf((int) (target_).getActingPlayer().getStatus().getMp()));
			player_panel.append("</font>/<font color=999999>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getStatus().getMaxMp()));
			player_panel.append("</font></td><td width=20></td></tr>");
			*/

			player_panel.append("<tr><td><font color=ECC39A>PvP Kills:</font> </td><td <font color=C27BA0>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getPvpKills()));
			player_panel.append("</font></td></tr>");
			player_panel.append("<tr><td><font color=ECC39A>PK Kills:</font> </td><td <font color=C71B1B>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getPkKills()));
			player_panel.append("</font></td></tr>");
			
			player_panel.append("<tr><td><img height=2><font color=ECC39A>Premium:</font> </td><td><img height=2>");
			player_panel.append(target_.getActingPlayer().isVIP() ? "<img src=CustomIconPack01.premium-member-extra-mini width=16 height=16>" :  "<font color=CCCCCC>-</font>" );
			player_panel.append("</td></tr>");
			
			player_panel.append("<br>");
			player_panel.append("</table></font><br>");
			//player_panel.append("<br><br><center><font color=009900>[Character Items]</font></center>");

			player_panel.append("<font color=E69138><table border=0><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.RHAND) ? "<img width=32 height=32 src=" + IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.RHAND).getItemId())  : "<img src=UpdUI1.Weapon width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>"); 
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.RHAND)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.RHAND).getItemName() +"<font color=B45F06> +"+(target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.RHAND).getEnchantLevel() : " ")+"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");

			player_panel.append("<table><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.LHAND)  ? "<img width=32 height=32 src="+IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.LHAND).getItemId())  : "<img src=UpdUI1.Shield width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>");
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.LHAND)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.LHAND).getItemName()  : " "));
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");

			player_panel.append("<table><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.HEAD)  ? "<img width=32 height=32 src="+IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.HEAD).getItemId()) : "<img src=UpdUI1.Helmet width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>");
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.HEAD)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.HEAD).getItemName() +"<font color=B45F06> +"+(target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.HEAD).getEnchantLevel() : " ")+"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");
							
			player_panel.append("<table><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.CHEST)  ? "<img width=32 height=32 src="+IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.CHEST).getItemId()) : "<img src=UpdUI1.upper_main width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>");
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.CHEST)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.CHEST).getItemName() +"<font color=B45F06> +"+(target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.CHEST).getEnchantLevel() : " ")+"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");
		
			player_panel.append("<table><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.LEGS)  ? "<img width=32 height=32 src="+IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.LEGS).getItemId()) : "<img src=UpdUI1.lower_main width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>");
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.LEGS)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.LEGS).getItemName() +"<font color=B45F06> +"+(target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.LEGS).getEnchantLevel() : " ")+"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");

			player_panel.append("<table><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.GLOVES)  ? "<img width=32 height=32 src="+IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.GLOVES).getItemId()) : "<img src=UpdUI1.gloves width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>");
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.GLOVES)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.GLOVES).getItemName() +"<font color=B45F06> +"+(target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.GLOVES).getEnchantLevel() : " ")+"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");
	
			player_panel.append("<table><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.FEET)  ? "<img width=32 height=32 src="+IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.FEET).getItemId()) : "<img src=UpdUI1.boots width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>");
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.FEET)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.FEET).getItemName() +"<font color=B45F06> +"+(target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.FEET).getEnchantLevel() : " ")+"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");	
				
			player_panel.append("<table><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.NECK)  ? "<img width=32 height=32 src="+IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.NECK).getItemId()) : "<img src=UpdUI1.necklace width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>");
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.NECK)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.NECK).getItemName() +"<font color=B45F06> +"+(target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.NECK).getEnchantLevel() : " ")+"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");		
		
			player_panel.append("<table><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.LEAR)  ? "<img width=32 height=32 src="+IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.LEAR).getItemId()) : "<img src=UpdUI1.earring width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>");
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.LEAR)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.LEAR).getItemName() +"<font color=B45F06> +"+(target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.LEAR).getEnchantLevel() : " ")+"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");			
		
			player_panel.append("<table><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.REAR)  ? "<img width=32 height=32 src="+IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.REAR).getItemId()) : "<img src=UpdUI1.earring width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>");
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.REAR)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.REAR).getItemName() +"<font color=B45F06> +"+(target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.REAR).getEnchantLevel() : " ")+"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");			
							
			player_panel.append("<table><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.LFINGER)  ? "<img width=32 height=32 src="+IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.LFINGER).getItemId()) : "<img src=UpdUI1.ring width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>");
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.LFINGER)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.LFINGER).getItemName() +"<font color=B45F06> +"+(target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.LFINGER).getEnchantLevel() : " ")+"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");		
		
			player_panel.append("<table><tr><td height=39 width=45>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.RFINGER)  ? "<img width=32 height=32 src="+IconsData.getInstance().getIcon((target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.RFINGER).getItemId()) : "<img src=UpdUI1.ring width=32 height=32>"));
			player_panel.append("</td><td width=220 height=39><table>");
			player_panel.append("<tr><td>");
			player_panel.append(String.valueOf((target_).getActingPlayer().getInventory().hasItemIn(Paperdoll.RFINGER)  ? (target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.RFINGER).getItemName() +"<font color=B45F06> +"+(target_).getActingPlayer().getInventory().getItemFrom(Paperdoll.RFINGER).getEnchantLevel() : " ")+"</font>");
			player_panel.append("</td></tr>");
			player_panel.append("</table></td></tr></table>");		

				NpcHtmlMessage html_playerinfo = new NpcHtmlMessage(0);	
				html_playerinfo.setFile("data/html/playerinfo.htm");
				html_playerinfo.replace("%player_panel%", player_panel.toString());
				sendPacket(html_playerinfo);
				
				html_playerinfo = null;
							
	}
	
	
}