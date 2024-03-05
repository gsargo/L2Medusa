package net.sf.l2j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import net.sf.l2j.commons.config.ExProperties;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.math.MathUtil;

import net.sf.l2j.gameserver.enums.GeoType;
import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.util.RewardHolder;

/**
 * This class contains global server configuration.<br>
 * It has static final fields initialized from configuration files.
 */
public final class Config
{
	private static final CLogger LOGGER = new CLogger(Config.class.getName());
	
	public static final String CLANS_FILE = "./config/clans.properties";
	public static final String EVENTS_FILE = "./config/events.properties";
	public static final String GEOENGINE_FILE = "./config/geoengine.properties";
	public static final String HEXID_FILE = "./config/hexid.txt";
	public static final String LOGINSERVER_FILE = "./config/loginserver.properties";
	public static final String NPCS_FILE = "./config/npcs.properties";
	public static final String PLAYERS_FILE = "./config/players.properties";
	public static final String SERVER_FILE = "./config/server.properties";
	public static final String SIEGE_FILE = "./config/siege.properties";
	public static final String TOURNAMENT = "./config/Mods/Tournament.ini";
	public static final String NEWS = "./config/news.properties";
	
	public static final String CUSTOM_FILE = "./config/custom.properties";
	
	public static final String AutoReward_System = "./config/c1c0s/AutoRewardSystem.properties";
	
	public static final String VoteManager_Personal = "./config/c1c0s/VoteManagerAPI.properties";
	
	public static final String DUNGEON_REWARD_FILE = "./events/Dungeon_Html_Reward.properties";
	
	public static final String Premium_System = "./config/c1c0s/PremiumSystem.properties";
	
	public static int AUGMENTATION_NG_SKILL_CHANCE_FORVIP;
	public static int AUGMENTATION_NG_GLOW_CHANCE_FORVIP;
	public static int AUGMENTATION_MID_SKILL_CHANCE_FORVIP;
	public static int AUGMENTATION_MID_GLOW_CHANCE_FORVIP;
	public static int AUGMENTATION_HIGH_SKILL_CHANCE_FORVIP;
	public static int AUGMENTATION_HIGH_GLOW_CHANCE_FORVIP;
	public static int AUGMENTATION_TOP_SKILL_CHANCE_FORVIP;
	public static int AUGMENTATION_TOP_GLOW_CHANCE_FORVIP;
	
	public static int DUNGEON_COIN_ID;
	public static int PARTY_DUNGEON_COIN_ID;
	public static int CONT_DUNGEON_ITEM;
	public static int DUNGEON_SPAWN_X;
	public static int DUNGEON_SPAWN_Y;
	public static int DUNGEON_SPAWN_Z;
	public static int DUNGEON_SPAWN_RND;
	
	public static int DUNGEON_ITEM_RENEWAL0;
	public static int DUNGEON_ITEM_RENEWAL1;
	public static int DUNGEON_ITEM_RENEWAL2;
	public static int DUNGEON_ITEM_RENEWAL3;
	public static int DUNGEON_ITEM_RENEWAL4;
	public static int DUNGEON_ITEM_RENEWAL5;
	public static int DUNGEON_ITEM_RENEWAL6;
	public static int DUNGEON_ITEM_RENEWAL7;
	public static int DUNGEON_ITEM_RENEWAL8;
	public static int DUNGEON_ITEM_RENEWAL9;
	public static int DUNGEON_ITEM_RENEWAL10;
	
	public static int DUNGEON_PARTY_ITEM_RENEWAL0;
	public static int DUNGEON_PARTY_ITEM_RENEWAL1;
	public static int DUNGEON_PARTY_ITEM_RENEWAL2;
	public static int DUNGEON_PARTY_ITEM_RENEWAL3;
	public static int DUNGEON_PARTY_ITEM_RENEWAL4;
	
	// c1c0s vote manager start
	public static String API_KEYTOPZONE;
	public static int SERVERID_KEYTOPZONE;
	public static String APIKEY_HOPZONE;
	public static String SERVERID_NETWORK;
	public static int VoteManagerApi_REWARD_ID;
	public static int VoteManagerApi_REWARD_COUNT;
	public static String APIKEY_L2JTOPCOM;
	public static String APIKEY_L2TOPSERVERS;
	public static String APIKEY_L2TOPGAMESERVER;
	public static String APIKEY_ITOPZ;
	public static int SERVERID_ITOPZ;
	public static String APIKEY_L2VOTESCOM;
	public static int SERVERID_L2TOPCO;
	public static String HASH_L2SERVERSCOM;
	public static int SERVERID_L2SERVERSCOM;
	public static String APIKEY_L2JBRASIL;
	public static int SERVERID_GAMETOPSEU;
	public static int SERVERID_TOP100ARENA;
	public static int SERVERID_L2NET;
	public static int SERVERID_L2TOPEU;
	// c1c0s vote manager end
	
	// c1c0s auto vote reward system start
	public static boolean ENABLE_VOTE_SYSTEM;
	public static int HOPZONE_REWARD_ID;
	public static int HOPZONE_REWARD_COUNT;
	public static String VOTES_SITE_HOPZONE_URL1;
	public static int NETWORK_REWARD_ID;
	public static int NETWORK_REWARD_COUNT;
	public static String VOTES_SITE_NETWORK_URL1;
	public static int TOPZONE_REWARD_ID;
	public static int TOPZONE_REWARD_COUNT;
	public static String VOTES_SITE_TOPZONE_URL1;
	public static String VOTES_SITE_TOPZONE_URL_API;
	public static int MMOTOP_REWARD_ID;
	public static int MMOTOP_REWARD_COUNT;
	public static String VOTES_SITE_MMOTOP_URL1;
	public static int L2TOPSERVERS_REWARD_ID;
	public static int L2TOPSERVERS_REWARD_COUNT;
	public static String VOTES_SITE_L2TOPSERVERS_URL1;
	public static int L2TOPGSNET_REWARD_ID;
	public static int L2TOPGSNET_REWARD_COUNT;
	public static String VOTES_SITE_L2TOPGSNET_URL1;
	public static int L2TOPCO_REWARD_ID;
	public static int L2TOPCO_REWARD_COUNT;
	public static String VOTES_SITE_L2TOPCO_URL1;
	public static int L2JBrasil_REWARD_ID;
	public static int L2JBrasil_REWARD_COUNT;
	public static String VOTES_SITE_L2JBrasil_URL1;
	public static int L2VOTES_REWARD_ID;
	public static int L2VOTES_REWARD_COUNT;
	public static String VOTES_SITE_L2VOTES_URL1;
	public static int itopz_REWARD_ID;
	public static int itopz_REWARD_COUNT;
	public static String VOTES_SITE_itopz_URL1;
	public static int mmotopru_REWARD_ID;
	public static int mmotopru_REWARD_COUNT;
	public static String VOTES_SITE_mmotopru_URL1;
	public static int l2serverscom_REWARD_ID;
	public static int l2serverscom_REWARD_COUNT;
	public static String VOTES_SITE_l2serverscom_URL1;
	public static int l2serversVOTES_FOR_REWARD;
	public static int l2jtopcom_REWARD_ID;
	public static int l2jtopcom_REWARD_COUNT;
	public static String VOTES_SITE_l2jtopcom_URL1;
	public static int L2TOPGR_REWARD_ID;
	public static int L2TOPGR_REWARD_COUNT;
	public static String VOTES_SITE_L2TOPGR_URL1;
	public static int Gametopseu_REWARD_ID;
	public static int Gametopseu_REWARD_COUNT;
	public static String VOTES_SITE_Gametopseu_URL1;
	public static int GAMETOPSEUVOTES_FOR_REWARD;
	public static int VOTES_FOR_REWARDHOPZONE;
	public static int VOTES_FOR_REWARDITOPZ;
	public static int VOTES_FOR_REWARDL2JBRASIL;
	public static int VOTES_FOR_REWARDL2JTOP;
	public static int VOTES_FOR_REWARDL2TOPCO;
	public static int VOTES_FOR_REWARDL2TOPGR;
	public static int VOTES_FOR_REWARDL2TOPGSNET;
	public static int VOTES_FOR_REWARDL2TOPSERVERS;
	public static int VOTES_FOR_REWARDL2VOTES;
	public static int VOTES_FOR_REWARDMMOTOPEU;
	public static int VOTES_FOR_REWARDMMOTOPRU;
	public static int VOTES_FOR_REWARDNETWORK;
	public static int VOTES_FOR_REWARDTOPZONE;
	public static int VOTES_SYSTEM_INITIAL_DELAYGAMETOPSEU;
	public static int VOTES_SYSTEM_STEP_DELAYGAMETOPSEU;
	public static int VOTES_SYSTEM_INITIAL_DELAYHOPZONE;
	public static int VOTES_SYSTEM_STEP_DELAYHOPZONE;
	public static int VOTES_SYSTEM_INITIAL_DELAYITOPZ;
	public static int VOTES_SYSTEM_STEP_DELAYITOPZ;
	public static int VOTES_SYSTEM_INITIAL_DELAYL2JBRASIL;
	public static int VOTES_SYSTEM_STEP_DELAYL2JBRASIL;
	public static int VOTES_SYSTEM_INITIAL_DELAYL2JTOP;
	public static int VOTES_SYSTEM_STEP_DELAYL2JTOP;
	public static int VOTES_SYSTEM_INITIAL_DELAYL2SERVERS;
	public static int VOTES_SYSTEM_STEP_DELAYL2SERVERS;
	public static int VOTES_SYSTEM_INITIAL_DELAYL2TOPCO;
	public static int VOTES_SYSTEM_STEP_DELAYL2TOPCO;
	public static int VOTES_SYSTEM_INITIAL_DELAYL2TOPGR;
	public static int VOTES_SYSTEM_STEP_DELAYL2TOPGR;
	public static int VOTES_SYSTEM_INITIAL_DELAYL2TOPGSNET;
	public static int VOTES_SYSTEM_STEP_DELAYL2TOPGSNET;
	public static int VOTES_SYSTEM_INITIAL_DELAYL2TOPSERVERS;
	public static int VOTES_SYSTEM_STEP_DELAYL2TOPSERVERS;
	public static int VOTES_SYSTEM_INITIAL_DELAYL2VOTES;
	public static int VOTES_SYSTEM_STEP_DELAYL2VOTES;
	public static int VOTES_SYSTEM_INITIAL_DELAYMMOTOPEU;
	public static int VOTES_SYSTEM_STEP_DELAYMMOTOPEU;
	public static int VOTES_SYSTEM_INITIAL_DELAYMMOTOPRU;
	public static int VOTES_SYSTEM_STEP_DELAYMMOTOPRU;
	public static int VOTES_SYSTEM_INITIAL_DELAYNETWORK;
	public static int VOTES_SYSTEM_STEP_DELAYNETWORK;
	public static int VOTES_SYSTEM_INITIAL_DELAYTOPZONE;
	public static int VOTES_SYSTEM_STEP_DELAYTOPZONE;
	public static int VOTES_SYSTEM_INITIAL_DELAYSERVERTOP;
	public static int VOTES_SYSTEM_STEP_DELAYSERVERTOP;
	public static int VOTES_FOR_REWARDSERVERTOP;
	public static int SERVERTOP_REWARD_ID;
	public static int SERVERTOP_REWARD_COUNT;
	public static String VOTES_SITE_servertop_URL1;
	public static int VOTES_SYSTEM_INITIAL_DELAYL2NET;
	public static int VOTES_SYSTEM_STEP_DELAYL2NET;
	public static int VOTES_FOR_REWARDL2NET;
	public static int L2NET_REWARD_ID;
	public static int L2NET_REWARD_COUNT;
	public static String VOTES_SITE_L2NET_URL1;
	public static int VOTES_SYSTEM_INITIAL_DELAYL2TOPEU;
	public static int VOTES_SYSTEM_STEP_DELAYL2TOPEU;
	public static int VOTES_FOR_REWARDL2TOPEU;
	public static int L2TOPEU_REWARD_ID;
	public static int L2TOPEU_REWARD_COUNT;
	public static String VOTES_SITE_L2TOPEU_URL1;
	// c1c0s auto vote reward system end
	/** News */
	public static String News_1;
	public static String News_2;
	public static String News_3;
	public static String News_4;
	public static String News_5;
	public static String News_6;
	public static String News_7;
	public static String News_8;
	public static String News_9;
	public static String News_10;
	public static String News_11;
	public static String News_12;
	public static String News_13;
	public static String News_14;
	public static String News_15;
	/** Tournament */
	public static final boolean DEBUG = false;
	
	public static int TOURNAMENT_EVENT_DURATION;
	
	public static String[] TOURNAMENT_EVENT_INTERVAL_BY_TIME_OF_DAY;
	public static int TOURNAMENT_NPC_ID;
	public static Location TOURNAMENT_NPC_LOCATION;
	public static List<Integer> TOURNAMENT_RESTRICTED_SKILL_LIST = new ArrayList<>();
	public static List<Integer> TOURNAMENT_RESTRICTED_ITEM_LIST = new ArrayList<>();
	public static int TOURNAMENT_TIME_SEARCH_FIGHTS;
	public static List<RewardHolder> TOURNAMENT_FIGHT_REWARD_WINNER = new ArrayList<>();
	public static List<RewardHolder> TOURNAMENT_FIGHT_REWARD_LOOSER = new ArrayList<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_FIGHT_START_TIME = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_FIGHT_DURATION = new HashMap<>();
	public static int TOURNAMENT_TIME_TO_TELEPORT;
	public static boolean TOURNAMENT_DEBUG;
	
	public static Map<TournamentFightType, Integer> TOURNAMENT_DUELIST_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_DREADNOUGHT_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_TANKER_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_DAGGER_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_ARCHER_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_HEALER_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_ARCHMAGE_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_SOULTAKER_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_MYSTICMUSE_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_STORMSCREAMER_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_TITAN_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_DOMINATOR_ALLOWED = new HashMap<>();
	public static Map<TournamentFightType, Integer> TOURNAMENT_DOOMCRYER_ALLOWED = new HashMap<>();
	
	/** Premium Settings */
	public static double PREMIUM_RATE_DROP;
	public static double PREMIUM_RATE_ADENA;
	public static double PREMIUM_RATE_SPOIL;
	public static double PREMIUM_RATE_DROP_RAID;
	public static double PREMIUM_RATE_XP;
	
	// --------------------------------------------------
	// Clans settings
	// --------------------------------------------------
	
	/** Clans */
	public static int CLAN_JOIN_DAYS;
	public static int CLAN_CREATE_DAYS;
	public static int CLAN_DISSOLVE_DAYS;
	public static int ALLY_JOIN_DAYS_WHEN_LEAVED;
	public static int ALLY_JOIN_DAYS_WHEN_DISMISSED;
	public static int ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
	public static int CREATE_ALLY_DAYS_WHEN_DISSOLVED;
	public static int MAX_NUM_OF_CLANS_IN_ALLY;
	public static int CLAN_MEMBERS_FOR_WAR;
	public static int CLAN_WAR_PENALTY_WHEN_ENDED;
	public static boolean MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
	
	/** Manor */
	public static int MANOR_REFRESH_TIME;
	public static int MANOR_REFRESH_MIN;
	public static int MANOR_APPROVE_TIME;
	public static int MANOR_APPROVE_MIN;
	public static int MANOR_MAINTENANCE_MIN;
	public static int MANOR_SAVE_PERIOD_RATE;
	
	/** Clan Hall function */
	public static long CH_TELE_FEE_RATIO;
	public static int CH_TELE1_FEE;
	public static int CH_TELE2_FEE;
	public static long CH_SUPPORT_FEE_RATIO;
	public static int CH_SUPPORT1_FEE;
	public static int CH_SUPPORT2_FEE;
	public static int CH_SUPPORT3_FEE;
	public static int CH_SUPPORT4_FEE;
	public static int CH_SUPPORT5_FEE;
	public static int CH_SUPPORT6_FEE;
	public static int CH_SUPPORT7_FEE;
	public static int CH_SUPPORT8_FEE;
	public static long CH_MPREG_FEE_RATIO;
	public static int CH_MPREG1_FEE;
	public static int CH_MPREG2_FEE;
	public static int CH_MPREG3_FEE;
	public static int CH_MPREG4_FEE;
	public static int CH_MPREG5_FEE;
	public static long CH_HPREG_FEE_RATIO;
	public static int CH_HPREG1_FEE;
	public static int CH_HPREG2_FEE;
	public static int CH_HPREG3_FEE;
	public static int CH_HPREG4_FEE;
	public static int CH_HPREG5_FEE;
	public static int CH_HPREG6_FEE;
	public static int CH_HPREG7_FEE;
	public static int CH_HPREG8_FEE;
	public static int CH_HPREG9_FEE;
	public static int CH_HPREG10_FEE;
	public static int CH_HPREG11_FEE;
	public static int CH_HPREG12_FEE;
	public static int CH_HPREG13_FEE;
	public static long CH_EXPREG_FEE_RATIO;
	public static int CH_EXPREG1_FEE;
	public static int CH_EXPREG2_FEE;
	public static int CH_EXPREG3_FEE;
	public static int CH_EXPREG4_FEE;
	public static int CH_EXPREG5_FEE;
	public static int CH_EXPREG6_FEE;
	public static int CH_EXPREG7_FEE;
	public static long CH_ITEM_FEE_RATIO;
	public static int CH_ITEM1_FEE;
	public static int CH_ITEM2_FEE;
	public static int CH_ITEM3_FEE;
	public static long CH_CURTAIN_FEE_RATIO;
	public static int CH_CURTAIN1_FEE;
	public static int CH_CURTAIN2_FEE;
	public static long CH_FRONT_FEE_RATIO;
	public static int CH_FRONT1_FEE;
	public static int CH_FRONT2_FEE;
	
	// --------------------------------------------------
	// Events settings
	// --------------------------------------------------
	
	/** Olympiad */
	public static int OLY_START_TIME;
	public static int OLY_MIN;
	public static long OLY_CPERIOD;
	public static long OLY_BATTLE;
	public static long OLY_WPERIOD;
	public static long OLY_VPERIOD;
	public static int OLY_WAIT_TIME;
	public static int OLY_WAIT_BATTLE;
	public static int OLY_WAIT_END;
	public static int OLY_START_POINTS;
	public static int OLY_WEEKLY_POINTS;
	public static int OLY_MIN_MATCHES;
	public static int OLY_CLASSED;
	public static int OLY_NONCLASSED;
	public static IntIntHolder[] OLY_CLASSED_REWARD;
	public static IntIntHolder[] OLY_NONCLASSED_REWARD;
	public static int OLY_GP_PER_POINT;
	public static int OLY_HERO_POINTS;
	public static int OLY_RANK1_POINTS;
	public static int OLY_RANK2_POINTS;
	public static int OLY_RANK3_POINTS;
	public static int OLY_RANK4_POINTS;
	public static int OLY_RANK5_POINTS;
	public static int OLY_MAX_POINTS;
	public static int OLY_DIVIDER_CLASSED;
	public static int OLY_DIVIDER_NON_CLASSED;
	public static boolean OLY_ANNOUNCE_GAMES;
	
	/** SevenSigns Festival */
	public static boolean SEVEN_SIGNS_BYPASS_PREREQUISITES;
	public static int FESTIVAL_MIN_PLAYER;
	public static int MAXIMUM_PLAYER_CONTRIB;
	public static long FESTIVAL_MANAGER_START;
	public static long FESTIVAL_LENGTH;
	public static long FESTIVAL_CYCLE_LENGTH;
	public static long FESTIVAL_FIRST_SPAWN;
	public static long FESTIVAL_FIRST_SWARM;
	public static long FESTIVAL_SECOND_SPAWN;
	public static long FESTIVAL_SECOND_SWARM;
	public static long FESTIVAL_CHEST_SPAWN;
	
	/** Four Sepulchers */
	public static int FS_TIME_ENTRY;
	public static int FS_TIME_END;
	public static int FS_PARTY_MEMBER_COUNT;
	
	/** dimensional rift */
	public static int RIFT_MIN_PARTY_SIZE;
	public static int RIFT_SPAWN_DELAY;
	public static int RIFT_MAX_JUMPS;
	public static int RIFT_AUTO_JUMPS_TIME_MIN;
	public static int RIFT_AUTO_JUMPS_TIME_MAX;
	public static int RIFT_ENTER_COST_RECRUIT;
	public static int RIFT_ENTER_COST_SOLDIER;
	public static int RIFT_ENTER_COST_OFFICER;
	public static int RIFT_ENTER_COST_CAPTAIN;
	public static int RIFT_ENTER_COST_COMMANDER;
	public static int RIFT_ENTER_COST_HERO;
	public static double RIFT_BOSS_ROOM_TIME_MULTIPLY;
	
	/** Wedding system */
	public static boolean ALLOW_WEDDING;
	public static int WEDDING_PRICE;
	public static boolean WEDDING_SAMESEX;
	public static boolean WEDDING_FORMALWEAR;
	
	/** Lottery */
	public static int LOTTERY_PRIZE;
	public static int LOTTERY_TICKET_PRICE;
	public static double LOTTERY_5_NUMBER_RATE;
	public static double LOTTERY_4_NUMBER_RATE;
	public static double LOTTERY_3_NUMBER_RATE;
	public static int LOTTERY_2_AND_1_NUMBER_PRIZE;
	
	/** Fishing tournament */
	public static boolean ALLOW_FISH_CHAMPIONSHIP;
	public static int FISH_CHAMPIONSHIP_REWARD_ITEM;
	public static int FISH_CHAMPIONSHIP_REWARD_1;
	public static int FISH_CHAMPIONSHIP_REWARD_2;
	public static int FISH_CHAMPIONSHIP_REWARD_3;
	public static int FISH_CHAMPIONSHIP_REWARD_4;
	public static int FISH_CHAMPIONSHIP_REWARD_5;
	
	// --------------------------------------------------
	// GeoEngine
	// --------------------------------------------------
	
	/** Geodata */
	public static String GEODATA_PATH;
	public static GeoType GEODATA_TYPE;
	
	/** Path checking */
	public static int PART_OF_CHARACTER_HEIGHT;
	public static int MAX_OBSTACLE_HEIGHT;
	
	/** Path finding */
	public static String PATHFIND_BUFFERS;
	public static int MOVE_WEIGHT;
	public static int MOVE_WEIGHT_DIAG;
	public static int OBSTACLE_WEIGHT;
	public static int OBSTACLE_WEIGHT_DIAG;
	public static int HEURISTIC_WEIGHT;
	public static int HEURISTIC_WEIGHT_DIAG;
	public static int MAX_ITERATIONS;
	
	// --------------------------------------------------
	// HexID
	// --------------------------------------------------
	
	public static int SERVER_ID;
	public static byte[] HEX_ID;
	
	// --------------------------------------------------
	// Loginserver
	// --------------------------------------------------
	
	public static String LOGINSERVER_HOSTNAME;
	public static int LOGINSERVER_PORT;
	
	public static int LOGIN_TRY_BEFORE_BAN;
	public static int LOGIN_BLOCK_AFTER_BAN;
	public static boolean ACCEPT_NEW_GAMESERVER;
	
	public static boolean SHOW_LICENCE;
	
	public static boolean AUTO_CREATE_ACCOUNTS;
	
	public static boolean FLOOD_PROTECTION;
	public static int FAST_CONNECTION_LIMIT;
	public static int NORMAL_CONNECTION_TIME;
	public static int FAST_CONNECTION_TIME;
	public static int MAX_CONNECTION_PER_IP;
	
	// --------------------------------------------------
	// NPCs / Monsters
	// --------------------------------------------------
	
	/** Buffer */
	public static int BUFFER_MAX_SCHEMES;
	public static int BUFFER_STATIC_BUFF_COST;
	
	/** Class Master */
	public static boolean ALLOW_CLASS_MASTERS;
	public static boolean ALLOW_ENTIRE_TREE;
	public static ClassMasterSettings CLASS_MASTER_SETTINGS;
	
	/** Misc */
	public static boolean FREE_TELEPORT;
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	public static boolean MOB_AGGRO_IN_PEACEZONE;
	public static boolean SHOW_NPC_LVL;
	public static boolean SHOW_NPC_CREST;
	public static boolean SHOW_SUMMON_CREST;
	
	/** Wyvern Manager */
	public static boolean WYVERN_ALLOW_UPGRADER;
	public static int WYVERN_REQUIRED_LEVEL;
	public static int WYVERN_REQUIRED_CRYSTALS;
	
	/** Raid Boss */
	public static double RAID_HP_REGEN_MULTIPLIER;
	public static double RAID_MP_REGEN_MULTIPLIER;
	public static double RAID_DEFENCE_MULTIPLIER;
	public static int RAID_MINION_RESPAWN_TIMER;
	
	public static boolean RAID_DISABLE_CURSE;
	
	/** Grand Boss */
	public static int SPAWN_INTERVAL_AQ;
	public static int RANDOM_SPAWN_TIME_AQ;
	
	public static int SPAWN_INTERVAL_ANTHARAS;
	public static int RANDOM_SPAWN_TIME_ANTHARAS;
	public static int WAIT_TIME_ANTHARAS;
	
	public static int SPAWN_INTERVAL_BAIUM;
	public static int RANDOM_SPAWN_TIME_BAIUM;
	
	public static int SPAWN_INTERVAL_CORE;
	public static int RANDOM_SPAWN_TIME_CORE;
	
	public static int SPAWN_INTERVAL_FRINTEZZA;
	public static int RANDOM_SPAWN_TIME_FRINTEZZA;
	public static int WAIT_TIME_FRINTEZZA;
	
	public static int SPAWN_INTERVAL_ORFEN;
	public static int RANDOM_SPAWN_TIME_ORFEN;
	
	public static int SPAWN_INTERVAL_SAILREN;
	public static int RANDOM_SPAWN_TIME_SAILREN;
	public static int WAIT_TIME_SAILREN;
	
	public static int SPAWN_INTERVAL_VALAKAS;
	public static int RANDOM_SPAWN_TIME_VALAKAS;
	public static int WAIT_TIME_VALAKAS;
	
	public static int SPAWN_INTERVAL_ZAKEN;
	public static int RANDOM_SPAWN_TIME_ZAKEN;
	
	/** AI */
	public static boolean GUARD_ATTACK_AGGRO_MOB;
	public static int RANDOM_WALK_RATE;
	public static int MAX_DRIFT_RANGE;
	public static int MIN_NPC_ANIMATION;
	public static int MAX_NPC_ANIMATION;
	public static int MIN_MONSTER_ANIMATION;
	public static int MAX_MONSTER_ANIMATION;
	public static Map<Integer, String> RAID_BOSS_RESPAWN_TIME;
	public static int RAID_BOSS_RANGE;
	public static List<Integer> AGATHION_NPC_ID;
	
	// --------------------------------------------------
	// Players
	// --------------------------------------------------
	
	/** Misc */
	public static boolean EFFECT_CANCELING;
	public static double HP_REGEN_MULTIPLIER;
	public static double MP_REGEN_MULTIPLIER;
	public static double CP_REGEN_MULTIPLIER;
	public static int PLAYER_SPAWN_PROTECTION;
	public static int PLAYER_FAKEDEATH_UP_PROTECTION;
	public static double RESPAWN_RESTORE_HP;
	public static int MAX_PVTSTORE_SLOTS_DWARF;
	public static int MAX_PVTSTORE_SLOTS_OTHER;
	public static boolean DEEPBLUE_DROP_RULES;
	public static boolean ALLOW_DELEVEL;
	public static int DEATH_PENALTY_CHANCE;
	
	/** Inventory & WH */
	public static int INVENTORY_MAXIMUM_NO_DWARF;
	public static int INVENTORY_MAXIMUM_DWARF;
	public static int INVENTORY_MAXIMUM_PET;
	public static int MAX_ITEM_IN_PACKET;
	public static double WEIGHT_LIMIT;
	public static int WAREHOUSE_SLOTS_NO_DWARF;
	public static int WAREHOUSE_SLOTS_DWARF;
	public static int WAREHOUSE_SLOTS_CLAN;
	public static int FREIGHT_SLOTS;
	public static boolean REGION_BASED_FREIGHT;
	public static int FREIGHT_PRICE;
	
	/** Enchant */
	public static double ENCHANT_CHANCE_NORMAL_WEAPON;
	public static double ENCHANT_CHANCE_NORMAL70_WEAPON;
	public static double ENCHANT_CHANCE_NORMAL75_WEAPON;
	public static double ENCHANT_CHANCE_NORMAL80_WEAPON;
	
	public static double ENCHANT_CHANCE_NORMAL_ARMOR;
	public static double ENCHANT_CHANCE_NORMAL70_ARMOR;
	public static double ENCHANT_CHANCE_NORMAL75_ARMOR;
	public static double ENCHANT_CHANCE_NORMAL80_ARMOR;
	
	public static double ENCHANT_CHANCE_BLESSED_WEAPON;
	public static double ENCHANT_CHANCE_BLESSED75_WEAPON;
	
	public static double ENCHANT_CHANCE_BLESSED_ARMOR;
	public static double ENCHANT_CHANCE_BLESSED75_ARMOR;
	
	public static double ENCHANT_CHANCE_IMPROVEDBLESSED_WEAPON;
	public static double ENCHANT_CHANCE_IMPROVEDBLESSED_ARMOR;
	
	
	public static double ENCHANT_CHANCE_WEAPON;
	public static double ENCHANT_CHANCE_WEAPON_10PLUS;
	public static double ENCHANT_CHANCE_WEAPON_13PLUS;
	public static double ENCHANT_CHANCE_WEAPON_16PLUS;
	
	public static double ENCHANT_CHANCE_WEAPON_BLESSED;
	public static double ENCHANT_CHANCE_WEAPON_BLESSED_10PLUS;
	public static double ENCHANT_CHANCE_WEAPON_BLESSED_13PLUS;
	public static double ENCHANT_CHANCE_WEAPON_BLESSED_16PLUS;
	
	public static double ENCHANT_CHANCE_WEAPON_CRYSTAL;
	public static double ENCHANT_CHANCE_WEAPON_CRYSTAL_10PLUS;
	public static double ENCHANT_CHANCE_WEAPON_CRYSTAL_13PLUS;
	public static double ENCHANT_CHANCE_WEAPON_CRYSTAL_16PLUS;
	//public static double ENCHANT_CHANCE_WEAPON_NONMAGIC;
	//public static double ENCHANT_CHANCE_WEAPON_NONMAGIC_12PLUS;
	public static double ENCHANT_CHANCE_ARMOR;
	public static double ENCHANT_CHANCE_ARMOR_12PLUS;
	public static double ENCHANT_CHANCE_ARMOR_16PLUS;
	
	public static int ENCHANT_MAX_WEAPON;
	public static int ENCHANT_MAX_ARMOR;
	public static int ENCHANT_SAFE_MAX;
	public static int ENCHANT_SAFE_MAX_FULL;
	
	/** Augmentations */
	public static int AUGMENTATION_NG_SKILL_CHANCE;
	public static int AUGMENTATION_NG_GLOW_CHANCE;
	public static int AUGMENTATION_MID_SKILL_CHANCE;
	public static int AUGMENTATION_MID_GLOW_CHANCE;
	public static int AUGMENTATION_HIGH_SKILL_CHANCE;
	public static int AUGMENTATION_HIGH_GLOW_CHANCE;
	public static int AUGMENTATION_TOP_SKILL_CHANCE;
	public static int AUGMENTATION_TOP_GLOW_CHANCE;
	public static int AUGMENTATION_BASESTAT_CHANCE;
	
	/** Karma & PvP */
	public static boolean KARMA_PLAYER_CAN_SHOP;
	public static boolean KARMA_PLAYER_CAN_USE_GK;
	public static boolean KARMA_PLAYER_CAN_TELEPORT;
	public static boolean KARMA_PLAYER_CAN_TRADE;
	public static boolean KARMA_PLAYER_CAN_USE_WH;
	
	public static boolean KARMA_DROP_GM;
	public static boolean KARMA_AWARD_PK_KILL;
	public static int KARMA_PK_LIMIT;
	
	public static int[] KARMA_NONDROPPABLE_PET_ITEMS;
	public static int[] KARMA_NONDROPPABLE_ITEMS;
	
	public static int PVP_NORMAL_TIME;
	public static int PVP_PVP_TIME;
	
	/** Party */
	public static String PARTY_XP_CUTOFF_METHOD;
	public static int PARTY_XP_CUTOFF_LEVEL;
	public static double PARTY_XP_CUTOFF_PERCENT;
	public static int PARTY_RANGE;
	
	/** GMs & Admin Stuff */
	public static int DEFAULT_ACCESS_LEVEL;
	public static boolean GM_HERO_AURA;
	public static boolean GM_STARTUP_INVULNERABLE;
	public static boolean GM_STARTUP_INVISIBLE;
	public static boolean GM_STARTUP_BLOCK_ALL;
	public static boolean GM_STARTUP_AUTO_LIST;
	
	/** petitions */
	public static boolean PETITIONING_ALLOWED;
	public static int MAX_PETITIONS_PER_PLAYER;
	public static int MAX_PETITIONS_PENDING;
	
	/** Crafting **/
	public static boolean IS_CRAFTING_ENABLED;
	public static int DWARF_RECIPE_LIMIT;
	public static int COMMON_RECIPE_LIMIT;
	public static boolean BLACKSMITH_USE_RECIPES;
	
	/** Skills & Classes **/
	public static boolean AUTO_LEARN_SKILLS;
	public static boolean MAGIC_FAILURES;
	public static int PERFECT_SHIELD_BLOCK_RATE;
	public static boolean LIFE_CRYSTAL_NEEDED;
	public static boolean SP_BOOK_NEEDED;
	public static boolean ES_SP_BOOK_NEEDED;
	public static boolean DIVINE_SP_BOOK_NEEDED;
	public static boolean SUBCLASS_WITHOUT_QUESTS;
	
	/** Buffs */
	public static boolean STORE_SKILL_COOLTIME;
	public static int MAX_BUFFS_AMOUNT;
	public static int MAX_CUSTOM_BUFFS;
	
	// --------------------------------------------------
	// Sieges
	// --------------------------------------------------
	
	public static int SIEGE_LENGTH;
	public static int MINIMUM_CLAN_LEVEL;
	public static int MAX_ATTACKERS_NUMBER;
	public static int MAX_DEFENDERS_NUMBER;
	public static int ATTACKERS_RESPAWN_DELAY;
	
	public static int CH_MINIMUM_CLAN_LEVEL;
	public static int CH_MAX_ATTACKERS_NUMBER;
	
	// --------------------------------------------------
	// Server
	// --------------------------------------------------
	
	public static String HOSTNAME;
	public static String GAMESERVER_HOSTNAME;
	public static int GAMESERVER_PORT;
	public static String GAMESERVER_LOGIN_HOSTNAME;
	public static int GAMESERVER_LOGIN_PORT;
	public static int REQUEST_ID;
	public static boolean ACCEPT_ALTERNATE_ID;
	public static boolean USE_BLOWFISH_CIPHER;
	
	/** Access to database */
	public static String DATABASE_URL;
	public static String DATABASE_LOGIN;
	public static String DATABASE_PASSWORD;
	public static int DATABASE_MAX_CONNECTIONS;
	
	/** serverList & Test */
	public static boolean SERVER_LIST_BRACKET;
	public static boolean SERVER_LIST_CLOCK;
	public static int SERVER_LIST_AGE;
	public static boolean SERVER_LIST_TESTSERVER;
	public static boolean SERVER_LIST_PVPSERVER;
	public static boolean SERVER_GMONLY;
	
	/** clients related */
	public static int DELETE_DAYS;
	public static int MAXIMUM_ONLINE_USERS;
	
	/** Auto-loot */
	public static boolean AUTO_LOOT;
	public static boolean AUTO_LOOT_HERBS;
	public static boolean AUTO_LOOT_RAID;
	public static boolean AUTO_LOOT_DUNGEON;
	
	/** Items Management */
	public static boolean ALLOW_DISCARDITEM;
	public static boolean MULTIPLE_ITEM_DROP;
	public static int HERB_AUTO_DESTROY_TIME;
	public static int ITEM_AUTO_DESTROY_TIME;
	public static int EQUIPABLE_ITEM_AUTO_DESTROY_TIME;
	public static Map<Integer, Integer> SPECIAL_ITEM_DESTROY_TIME;
	public static int PLAYER_DROPPED_ITEM_MULTIPLIER;
	
	/** Rate control */
	public static double RATE_XP;
	public static double RATE_SP;
	public static double RATE_PARTY_XP;
	public static double RATE_PARTY_SP;
	public static double RATE_DROP_ADENA;
	public static double RATE_DROP_ITEMS;
	public static double RATE_DROP_ITEMS_BY_RAID;
	public static double RATE_DROP_SPOIL;
	public static int RATE_DROP_MANOR;
	
	public static double RATE_QUEST_DROP;
	public static double RATE_QUEST_REWARD;
	public static double RATE_QUEST_REWARD_XP;
	public static double RATE_QUEST_REWARD_SP;
	public static double RATE_QUEST_REWARD_ADENA;
	
	public static double RATE_KARMA_EXP_LOST;
	public static double RATE_SIEGE_GUARDS_PRICE;
	
	public static int PLAYER_DROP_LIMIT;
	public static int PLAYER_RATE_DROP;
	public static int PLAYER_RATE_DROP_ITEM;
	public static int PLAYER_RATE_DROP_EQUIP;
	public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
	
	public static int KARMA_DROP_LIMIT;
	public static int KARMA_RATE_DROP;
	public static int KARMA_RATE_DROP_ITEM;
	public static int KARMA_RATE_DROP_EQUIP;
	public static int KARMA_RATE_DROP_EQUIP_WEAPON;
	
	public static double PET_XP_RATE;
	public static int PET_FOOD_RATE;
	public static double SINEATER_XP_RATE;
	
	public static double RATE_DROP_COMMON_HERBS;
	public static double RATE_DROP_HP_HERBS;
	public static double RATE_DROP_MP_HERBS;
	public static double RATE_DROP_SPECIAL_HERBS;
	
	/** Allow types */
	public static boolean ALLOW_FREIGHT;
	public static boolean ALLOW_WAREHOUSE;
	public static boolean ALLOW_WEAR;
	public static int WEAR_DELAY;
	public static int WEAR_PRICE;
	public static boolean ALLOW_LOTTERY;
	public static boolean ALLOW_WATER;
	public static boolean ALLOW_BOAT;
	public static boolean ALLOW_CURSED_WEAPONS;
	public static boolean ALLOW_MANOR;
	public static boolean ENABLE_FALLING_DAMAGE;
	
	/** Debug & Dev */
	public static boolean NO_SPAWNS;
	public static boolean DEVELOPER;
	public static boolean PACKET_HANDLER_DEBUG;
	
	/** Deadlock Detector */
	public static boolean DEADLOCK_DETECTOR;
	public static int DEADLOCK_CHECK_INTERVAL;
	public static boolean RESTART_ON_DEADLOCK;
	
	/** Logs */
	public static boolean LOG_CHAT;
	public static boolean LOG_ITEMS;
	public static boolean GMAUDIT;
	
	/** Community Board */
	public static boolean ENABLE_COMMUNITY_BOARD;
	public static String BBS_DEFAULT;
	
	/** Flood Protectors */
	public static int ROLL_DICE_TIME;
	public static int HERO_VOICE_TIME;
	public static int SUBCLASS_TIME;
	public static int DROP_ITEM_TIME;
	public static int SERVER_BYPASS_TIME;
	public static int MULTISELL_TIME;
	public static int MANUFACTURE_TIME;
	public static int MANOR_TIME;
	public static int SENDMAIL_TIME;
	public static int CHARACTER_SELECT_TIME;
	public static int GLOBAL_CHAT_TIME;
	public static int TRADE_CHAT_TIME;
	public static int SOCIAL_TIME;
	public static int MOVE_TIME;
	
	/** ThreadPool */
	public static int SCHEDULED_THREAD_POOL_COUNT;
	public static int THREADS_PER_SCHEDULED_THREAD_POOL;
	public static int INSTANT_THREAD_POOL_COUNT;
	public static int THREADS_PER_INSTANT_THREAD_POOL;
	
	/** Misc */
	public static boolean L2WALKER_PROTECTION;
	public static boolean SERVER_NEWS;
	public static int ZONE_TOWN;

	/** Banking System */
	public static boolean BANKING_SYSTEM_ENABLED;
	public static int BANKING_SYSTEM_GOLDBARS;
	public static int BANKING_SYSTEM_ADENA;
	
	// --------------------------------------------------
	// Those "hidden" settings haven't configs to avoid admins to fuck their server
	// You still can experiment changing values here. But don't say I didn't warn you.
	// --------------------------------------------------
	
	/** Reserve Host on LoginServerThread */
	public static boolean RESERVE_HOST_ON_LOGIN = false; // default false
	
	/** MMO settings */
	public static int MMO_SELECTOR_SLEEP_TIME = 20; // default 20
	public static int MMO_MAX_SEND_PER_PASS = 80; // default 80
	public static int MMO_MAX_READ_PER_PASS = 80; // default 80
	public static int MMO_HELPER_BUFFER_COUNT = 20; // default 20
	
	/** Client Packets Queue settings */
	public static int CLIENT_PACKET_QUEUE_SIZE = MMO_MAX_READ_PER_PASS + 2; // default MMO_MAX_READ_PER_PASS + 2
	public static int CLIENT_PACKET_QUEUE_MAX_BURST_SIZE = MMO_MAX_READ_PER_PASS + 1; // default MMO_MAX_READ_PER_PASS + 1
	public static int CLIENT_PACKET_QUEUE_MAX_PACKETS_PER_SECOND = 160; // default 160
	public static int CLIENT_PACKET_QUEUE_MEASURE_INTERVAL = 5; // default 5
	public static int CLIENT_PACKET_QUEUE_MAX_AVERAGE_PACKETS_PER_SECOND = 80; // default 80
	public static int CLIENT_PACKET_QUEUE_MAX_FLOODS_PER_MIN = 2; // default 2
	public static int CLIENT_PACKET_QUEUE_MAX_OVERFLOWS_PER_MIN = 1; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNDERFLOWS_PER_MIN = 1; // default 1
	public static int CLIENT_PACKET_QUEUE_MAX_UNKNOWN_PER_MIN = 5; // default 5
	
	// --------------------------------------------------
	
	/**
	 * Initialize {@link ExProperties} from specified configuration file.
	 * @param filename : File name to be loaded.
	 * @return ExProperties : Initialized {@link ExProperties}.
	 */
	public static final ExProperties initProperties(String filename)
	{
		final ExProperties result = new ExProperties();
		
		try
		{
			result.load(new File(filename));
		}
		catch (Exception e)
		{
			LOGGER.error("An error occured loading '{}' config.", e, filename);
		}
		
		return result;
	}
	
	/**
	 * Loads clan and clan hall settings.
	 */
	private static final void loadClans()
	{
		final ExProperties clans = initProperties(CLANS_FILE);
		
		CLAN_JOIN_DAYS = clans.getProperty("DaysBeforeJoinAClan", 5);
		CLAN_CREATE_DAYS = clans.getProperty("DaysBeforeCreateAClan", 10);
		MAX_NUM_OF_CLANS_IN_ALLY = clans.getProperty("MaxNumOfClansInAlly", 3);
		CLAN_MEMBERS_FOR_WAR = clans.getProperty("ClanMembersForWar", 15);
		CLAN_WAR_PENALTY_WHEN_ENDED = clans.getProperty("ClanWarPenaltyWhenEnded", 5);
		CLAN_DISSOLVE_DAYS = clans.getProperty("DaysToPassToDissolveAClan", 7);
		ALLY_JOIN_DAYS_WHEN_LEAVED = clans.getProperty("DaysBeforeJoinAllyWhenLeaved", 1);
		ALLY_JOIN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeJoinAllyWhenDismissed", 1);
		ACCEPT_CLAN_DAYS_WHEN_DISMISSED = clans.getProperty("DaysBeforeAcceptNewClanWhenDismissed", 1);
		CREATE_ALLY_DAYS_WHEN_DISSOLVED = clans.getProperty("DaysBeforeCreateNewAllyWhenDissolved", 10);
		MEMBERS_CAN_WITHDRAW_FROM_CLANWH = clans.getProperty("MembersCanWithdrawFromClanWH", false);
		
		MANOR_REFRESH_TIME = clans.getProperty("ManorRefreshTime", 20);
		MANOR_REFRESH_MIN = clans.getProperty("ManorRefreshMin", 0);
		MANOR_APPROVE_TIME = clans.getProperty("ManorApproveTime", 6);
		MANOR_APPROVE_MIN = clans.getProperty("ManorApproveMin", 0);
		MANOR_MAINTENANCE_MIN = clans.getProperty("ManorMaintenanceMin", 6);
		MANOR_SAVE_PERIOD_RATE = clans.getProperty("ManorSavePeriodRate", 2) * 3600000;
		
		CH_TELE_FEE_RATIO = clans.getProperty("ClanHallTeleportFunctionFeeRatio", 86400000L);
		CH_TELE1_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl1", 7000);
		CH_TELE2_FEE = clans.getProperty("ClanHallTeleportFunctionFeeLvl2", 14000);
		CH_SUPPORT_FEE_RATIO = clans.getProperty("ClanHallSupportFunctionFeeRatio", 86400000L);
		CH_SUPPORT1_FEE = clans.getProperty("ClanHallSupportFeeLvl1", 17500);
		CH_SUPPORT2_FEE = clans.getProperty("ClanHallSupportFeeLvl2", 35000);
		CH_SUPPORT3_FEE = clans.getProperty("ClanHallSupportFeeLvl3", 49000);
		CH_SUPPORT4_FEE = clans.getProperty("ClanHallSupportFeeLvl4", 77000);
		CH_SUPPORT5_FEE = clans.getProperty("ClanHallSupportFeeLvl5", 147000);
		CH_SUPPORT6_FEE = clans.getProperty("ClanHallSupportFeeLvl6", 252000);
		CH_SUPPORT7_FEE = clans.getProperty("ClanHallSupportFeeLvl7", 259000);
		CH_SUPPORT8_FEE = clans.getProperty("ClanHallSupportFeeLvl8", 364000);
		CH_MPREG_FEE_RATIO = clans.getProperty("ClanHallMpRegenerationFunctionFeeRatio", 86400000L);
		CH_MPREG1_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl1", 14000);
		CH_MPREG2_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl2", 26250);
		CH_MPREG3_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl3", 45500);
		CH_MPREG4_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl4", 96250);
		CH_MPREG5_FEE = clans.getProperty("ClanHallMpRegenerationFeeLvl5", 140000);
		CH_HPREG_FEE_RATIO = clans.getProperty("ClanHallHpRegenerationFunctionFeeRatio", 86400000L);
		CH_HPREG1_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl1", 4900);
		CH_HPREG2_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl2", 5600);
		CH_HPREG3_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl3", 7000);
		CH_HPREG4_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl4", 8166);
		CH_HPREG5_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl5", 10500);
		CH_HPREG6_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl6", 12250);
		CH_HPREG7_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl7", 14000);
		CH_HPREG8_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl8", 15750);
		CH_HPREG9_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl9", 17500);
		CH_HPREG10_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl10", 22750);
		CH_HPREG11_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl11", 26250);
		CH_HPREG12_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl12", 29750);
		CH_HPREG13_FEE = clans.getProperty("ClanHallHpRegenerationFeeLvl13", 36166);
		CH_EXPREG_FEE_RATIO = clans.getProperty("ClanHallExpRegenerationFunctionFeeRatio", 86400000L);
		CH_EXPREG1_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl1", 21000);
		CH_EXPREG2_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl2", 42000);
		CH_EXPREG3_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl3", 63000);
		CH_EXPREG4_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl4", 105000);
		CH_EXPREG5_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl5", 147000);
		CH_EXPREG6_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl6", 163331);
		CH_EXPREG7_FEE = clans.getProperty("ClanHallExpRegenerationFeeLvl7", 210000);
		CH_ITEM_FEE_RATIO = clans.getProperty("ClanHallItemCreationFunctionFeeRatio", 86400000L);
		CH_ITEM1_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl1", 210000);
		CH_ITEM2_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl2", 490000);
		CH_ITEM3_FEE = clans.getProperty("ClanHallItemCreationFunctionFeeLvl3", 980000);
		CH_CURTAIN_FEE_RATIO = clans.getProperty("ClanHallCurtainFunctionFeeRatio", 86400000L);
		CH_CURTAIN1_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl1", 2002);
		CH_CURTAIN2_FEE = clans.getProperty("ClanHallCurtainFunctionFeeLvl2", 2625);
		CH_FRONT_FEE_RATIO = clans.getProperty("ClanHallFrontPlatformFunctionFeeRatio", 86400000L);
		CH_FRONT1_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl1", 3031);
		CH_FRONT2_FEE = clans.getProperty("ClanHallFrontPlatformFunctionFeeLvl2", 9331);
	}
	
	/**
	 * Loads event settings.<br>
	 * Such as olympiad, seven signs festival, four sepulchures, dimensional rift, weddings, lottery, fishing championship.
	 */
	private static final void loadEvents()
	{
		final ExProperties events = initProperties(EVENTS_FILE);
		
		OLY_START_TIME = events.getProperty("OlyStartTime", 18);
		OLY_MIN = events.getProperty("OlyMin", 0);
		OLY_CPERIOD = events.getProperty("OlyCPeriod", 21600000L);
		OLY_BATTLE = events.getProperty("OlyBattle", 180000L);
		OLY_WPERIOD = events.getProperty("OlyWPeriod", 604800000L);
		OLY_VPERIOD = events.getProperty("OlyVPeriod", 86400000L);
		OLY_WAIT_TIME = events.getProperty("OlyWaitTime", 30);
		OLY_WAIT_BATTLE = events.getProperty("OlyWaitBattle", 60);
		OLY_WAIT_END = events.getProperty("OlyWaitEnd", 40);
		OLY_START_POINTS = events.getProperty("OlyStartPoints", 18);
		OLY_WEEKLY_POINTS = events.getProperty("OlyWeeklyPoints", 3);
		OLY_MIN_MATCHES = events.getProperty("OlyMinMatchesToBeClassed", 5);
		OLY_CLASSED = events.getProperty("OlyClassedParticipants", 5);
		OLY_NONCLASSED = events.getProperty("OlyNonClassedParticipants", 9);
		OLY_CLASSED_REWARD = events.parseIntIntList("OlyClassedReward", "6651-50");
		OLY_NONCLASSED_REWARD = events.parseIntIntList("OlyNonClassedReward", "6651-30");
		OLY_GP_PER_POINT = events.getProperty("OlyGPPerPoint", 1000);
		OLY_HERO_POINTS = events.getProperty("OlyHeroPoints", 300);
		OLY_RANK1_POINTS = events.getProperty("OlyRank1Points", 100);
		OLY_RANK2_POINTS = events.getProperty("OlyRank2Points", 75);
		OLY_RANK3_POINTS = events.getProperty("OlyRank3Points", 55);
		OLY_RANK4_POINTS = events.getProperty("OlyRank4Points", 40);
		OLY_RANK5_POINTS = events.getProperty("OlyRank5Points", 30);
		OLY_MAX_POINTS = events.getProperty("OlyMaxPoints", 10);
		OLY_DIVIDER_CLASSED = events.getProperty("OlyDividerClassed", 3);
		OLY_DIVIDER_NON_CLASSED = events.getProperty("OlyDividerNonClassed", 5);
		OLY_ANNOUNCE_GAMES = events.getProperty("OlyAnnounceGames", true);
		
		SEVEN_SIGNS_BYPASS_PREREQUISITES = events.getProperty("SevenSignsBypassPrerequisites", false);
		FESTIVAL_MIN_PLAYER = MathUtil.limit(events.getProperty("FestivalMinPlayer", 5), 2, 9);
		MAXIMUM_PLAYER_CONTRIB = events.getProperty("MaxPlayerContrib", 1000000);
		FESTIVAL_MANAGER_START = events.getProperty("FestivalManagerStart", 120000L);
		FESTIVAL_LENGTH = events.getProperty("FestivalLength", 1080000L);
		FESTIVAL_CYCLE_LENGTH = events.getProperty("FestivalCycleLength", 2280000L);
		FESTIVAL_FIRST_SPAWN = events.getProperty("FestivalFirstSpawn", 120000L);
		FESTIVAL_FIRST_SWARM = events.getProperty("FestivalFirstSwarm", 300000L);
		FESTIVAL_SECOND_SPAWN = events.getProperty("FestivalSecondSpawn", 540000L);
		FESTIVAL_SECOND_SWARM = events.getProperty("FestivalSecondSwarm", 720000L);
		FESTIVAL_CHEST_SPAWN = events.getProperty("FestivalChestSpawn", 900000L);
		
		FS_TIME_ENTRY = events.getProperty("EntryTime", 55);
		FS_TIME_END = events.getProperty("EndTime", 50);
		FS_PARTY_MEMBER_COUNT = MathUtil.limit(events.getProperty("NeededPartyMembers", 4), 2, 9);
		
		RIFT_MIN_PARTY_SIZE = events.getProperty("RiftMinPartySize", 2);
		RIFT_MAX_JUMPS = events.getProperty("MaxRiftJumps", 4);
		RIFT_SPAWN_DELAY = events.getProperty("RiftSpawnDelay", 10000);
		RIFT_AUTO_JUMPS_TIME_MIN = events.getProperty("AutoJumpsDelayMin", 480);
		RIFT_AUTO_JUMPS_TIME_MAX = events.getProperty("AutoJumpsDelayMax", 600);
		RIFT_ENTER_COST_RECRUIT = events.getProperty("RecruitCost", 18);
		RIFT_ENTER_COST_SOLDIER = events.getProperty("SoldierCost", 21);
		RIFT_ENTER_COST_OFFICER = events.getProperty("OfficerCost", 24);
		RIFT_ENTER_COST_CAPTAIN = events.getProperty("CaptainCost", 27);
		RIFT_ENTER_COST_COMMANDER = events.getProperty("CommanderCost", 30);
		RIFT_ENTER_COST_HERO = events.getProperty("HeroCost", 33);
		RIFT_BOSS_ROOM_TIME_MULTIPLY = events.getProperty("BossRoomTimeMultiply", 1.);
		
		ALLOW_WEDDING = events.getProperty("AllowWedding", false);
		WEDDING_PRICE = events.getProperty("WeddingPrice", 1000000);
		WEDDING_SAMESEX = events.getProperty("WeddingAllowSameSex", false);
		WEDDING_FORMALWEAR = events.getProperty("WeddingFormalWear", true);
		
		LOTTERY_PRIZE = events.getProperty("LotteryPrize", 50000);
		LOTTERY_TICKET_PRICE = events.getProperty("LotteryTicketPrice", 2000);
		LOTTERY_5_NUMBER_RATE = events.getProperty("Lottery5NumberRate", 0.6);
		LOTTERY_4_NUMBER_RATE = events.getProperty("Lottery4NumberRate", 0.2);
		LOTTERY_3_NUMBER_RATE = events.getProperty("Lottery3NumberRate", 0.2);
		LOTTERY_2_AND_1_NUMBER_PRIZE = events.getProperty("Lottery2and1NumberPrize", 200);
		
		ALLOW_FISH_CHAMPIONSHIP = events.getProperty("AllowFishChampionship", true);
		FISH_CHAMPIONSHIP_REWARD_ITEM = events.getProperty("FishChampionshipRewardItemId", 57);
		FISH_CHAMPIONSHIP_REWARD_1 = events.getProperty("FishChampionshipReward1", 800000);
		FISH_CHAMPIONSHIP_REWARD_2 = events.getProperty("FishChampionshipReward2", 500000);
		FISH_CHAMPIONSHIP_REWARD_3 = events.getProperty("FishChampionshipReward3", 300000);
		FISH_CHAMPIONSHIP_REWARD_4 = events.getProperty("FishChampionshipReward4", 200000);
		FISH_CHAMPIONSHIP_REWARD_5 = events.getProperty("FishChampionshipReward5", 100000);
	}
	
	/**
	 * Loads geoengine settings.
	 */
	private static final void loadGeoengine()
	{
		final ExProperties geoengine = initProperties(GEOENGINE_FILE);
		
		GEODATA_PATH = geoengine.getProperty("GeoDataPath", "./data/geodata/");
		GEODATA_TYPE = Enum.valueOf(GeoType.class, geoengine.getProperty("GeoDataType", "L2OFF"));
		
		PART_OF_CHARACTER_HEIGHT = geoengine.getProperty("PartOfCharacterHeight", 75);
		MAX_OBSTACLE_HEIGHT = geoengine.getProperty("MaxObstacleHeight", 32);
		
		PATHFIND_BUFFERS = geoengine.getProperty("PathFindBuffers", "500x10;1000x10;3000x5;5000x3;10000x3");
		MOVE_WEIGHT = geoengine.getProperty("MoveWeight", 10);
		MOVE_WEIGHT_DIAG = geoengine.getProperty("MoveWeightDiag", 14);
		OBSTACLE_WEIGHT = geoengine.getProperty("ObstacleWeight", 30);
		OBSTACLE_WEIGHT_DIAG = (int) (OBSTACLE_WEIGHT * Math.sqrt(2));
		HEURISTIC_WEIGHT = geoengine.getProperty("HeuristicWeight", 12);
		HEURISTIC_WEIGHT_DIAG = geoengine.getProperty("HeuristicWeightDiag", 18);
		MAX_ITERATIONS = geoengine.getProperty("MaxIterations", 3500);
	}
	
	/**
	 * Loads hex ID settings.
	 */
	private static final void loadHexID()
	{
		final ExProperties hexid = initProperties(HEXID_FILE);
		
		SERVER_ID = Integer.parseInt(hexid.getProperty("ServerID"));
		HEX_ID = new BigInteger(hexid.getProperty("HexID"), 16).toByteArray();
	}
	
	/**
	 * Saves hex ID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hex ID of server.
	 */
	public static final void saveHexid(int serverId, String hexId)
	{
		saveHexid(serverId, hexId, HEXID_FILE);
	}
	
	/**
	 * Saves hexID file.
	 * @param serverId : The ID of server.
	 * @param hexId : The hexID of server.
	 * @param filename : The file name.
	 */
	public static final void saveHexid(int serverId, String hexId, String filename)
	{
		try
		{
			final File file = new File(filename);
			file.createNewFile();
			
			final Properties hexSetting = new Properties();
			hexSetting.setProperty("ServerID", String.valueOf(serverId));
			hexSetting.setProperty("HexID", hexId);
			
			try (OutputStream out = new FileOutputStream(file))
			{
				hexSetting.store(out, "the hexID to auth into login");
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to save hex ID to '{}' file.", e, filename);
		}
	}
	
	/**
	 * Loads NPC settings.<br>
	 * Such as champion monsters, NPC buffer, class master, wyvern, raid bosses and grand bosses, AI.
	 */
	private static final void loadNpcs()
	{
		final ExProperties npcs = initProperties(NPCS_FILE);
		
		BUFFER_MAX_SCHEMES = npcs.getProperty("BufferMaxSchemesPerChar", 4);
		BUFFER_STATIC_BUFF_COST = npcs.getProperty("BufferStaticCostPerBuff", -1);
		
		ALLOW_CLASS_MASTERS = npcs.getProperty("AllowClassMasters", false);
		ALLOW_ENTIRE_TREE = npcs.getProperty("AllowEntireTree", false);
		if (ALLOW_CLASS_MASTERS)
			CLASS_MASTER_SETTINGS = new ClassMasterSettings(npcs.getProperty("ConfigClassMaster"));
		
		FREE_TELEPORT = npcs.getProperty("FreeTeleport", false);
		ANNOUNCE_MAMMON_SPAWN = npcs.getProperty("AnnounceMammonSpawn", true);
		MOB_AGGRO_IN_PEACEZONE = npcs.getProperty("MobAggroInPeaceZone", true);
		SHOW_NPC_LVL = npcs.getProperty("ShowNpcLevel", false);
		SHOW_NPC_CREST = npcs.getProperty("ShowNpcCrest", false);
		SHOW_SUMMON_CREST = npcs.getProperty("ShowSummonCrest", false);
		
		WYVERN_ALLOW_UPGRADER = npcs.getProperty("AllowWyvernUpgrader", true);
		WYVERN_REQUIRED_LEVEL = npcs.getProperty("RequiredStriderLevel", 55);
		WYVERN_REQUIRED_CRYSTALS = npcs.getProperty("RequiredCrystalsNumber", 10);
		
		RAID_HP_REGEN_MULTIPLIER = npcs.getProperty("RaidHpRegenMultiplier", 1.);
		RAID_MP_REGEN_MULTIPLIER = npcs.getProperty("RaidMpRegenMultiplier", 1.);
		RAID_DEFENCE_MULTIPLIER = npcs.getProperty("RaidDefenceMultiplier", 1.);
		RAID_MINION_RESPAWN_TIMER = npcs.getProperty("RaidMinionRespawnTime", 300000);
		
		RAID_DISABLE_CURSE = npcs.getProperty("DisableRaidCurse", false);
		
		SPAWN_INTERVAL_AQ = npcs.getProperty("AntQueenSpawnInterval", 36);
		RANDOM_SPAWN_TIME_AQ = npcs.getProperty("AntQueenRandomSpawn", 17);
		
		SPAWN_INTERVAL_ANTHARAS = npcs.getProperty("AntharasSpawnInterval", 264);
		RANDOM_SPAWN_TIME_ANTHARAS = npcs.getProperty("AntharasRandomSpawn", 72);
		WAIT_TIME_ANTHARAS = npcs.getProperty("AntharasWaitTime", 30) * 60000;
		
		SPAWN_INTERVAL_BAIUM = npcs.getProperty("BaiumSpawnInterval", 168);
		RANDOM_SPAWN_TIME_BAIUM = npcs.getProperty("BaiumRandomSpawn", 48);
		
		SPAWN_INTERVAL_CORE = npcs.getProperty("CoreSpawnInterval", 60);
		RANDOM_SPAWN_TIME_CORE = npcs.getProperty("CoreRandomSpawn", 23);
		
		SPAWN_INTERVAL_FRINTEZZA = npcs.getProperty("FrintezzaSpawnInterval", 48);
		RANDOM_SPAWN_TIME_FRINTEZZA = npcs.getProperty("FrintezzaRandomSpawn", 8);
		WAIT_TIME_FRINTEZZA = npcs.getProperty("FrintezzaWaitTime", 1) * 60000;
		
		SPAWN_INTERVAL_ORFEN = npcs.getProperty("OrfenSpawnInterval", 48);
		RANDOM_SPAWN_TIME_ORFEN = npcs.getProperty("OrfenRandomSpawn", 20);
		
		SPAWN_INTERVAL_SAILREN = npcs.getProperty("SailrenSpawnInterval", 36);
		RANDOM_SPAWN_TIME_SAILREN = npcs.getProperty("SailrenRandomSpawn", 24);
		WAIT_TIME_SAILREN = npcs.getProperty("SailrenWaitTime", 5) * 60000;
		
		SPAWN_INTERVAL_VALAKAS = npcs.getProperty("ValakasSpawnInterval", 264);
		RANDOM_SPAWN_TIME_VALAKAS = npcs.getProperty("ValakasRandomSpawn", 72);
		WAIT_TIME_VALAKAS = npcs.getProperty("ValakasWaitTime", 30) * 60000;
		
		SPAWN_INTERVAL_ZAKEN = npcs.getProperty("ZakenSpawnInterval", 60);
		RANDOM_SPAWN_TIME_ZAKEN = npcs.getProperty("ZakenRandomSpawn", 20);
		
		GUARD_ATTACK_AGGRO_MOB = npcs.getProperty("GuardAttackAggroMob", false);
		RANDOM_WALK_RATE = npcs.getProperty("RandomWalkRate", 30);
		MAX_DRIFT_RANGE = npcs.getProperty("MaxDriftRange", 200);
		MIN_NPC_ANIMATION = npcs.getProperty("MinNPCAnimation", 20);
		MAX_NPC_ANIMATION = npcs.getProperty("MaxNPCAnimation", 40);
		MIN_MONSTER_ANIMATION = npcs.getProperty("MinMonsterAnimation", 10);
		MAX_MONSTER_ANIMATION = npcs.getProperty("MaxMonsterAnimation", 40);
		RAID_BOSS_RESPAWN_TIME = new HashMap<>();
		String[] data = npcs.getProperty("MainRaidBossRespawn", (String[]) null, ";");
		if (data != null)
		{
			for (String itemData : data)
			{
				String[] boss = itemData.split(",");
				RAID_BOSS_RESPAWN_TIME.put(Integer.parseInt(boss[0]), boss[1]);
			}
		}
		RAID_BOSS_RANGE = npcs.getProperty("RaidBossRange", 3000);
		AGATHION_NPC_ID = new ArrayList<>();
		data = npcs.getProperty("AgathionNpcId", (String[]) null, ",");
		if (data != null)
		{
			for (String itemData : data)
			{
				AGATHION_NPC_ID.add(Integer.valueOf(itemData));
			}
		}
		// Config, MainRaidBossRespawn=25035,1-21:00;25035,2-21:00:00 (bossId,time);
	}
	
	private static final void loadCustom()
	{
		final ExProperties custom = initProperties(Config.CUSTOM_FILE);
		PREMIUM_RATE_DROP = custom.getProperty("PremiumRateDrop", 1.);
		PREMIUM_RATE_ADENA = custom.getProperty("PremiumRateAdena", 1.);
		PREMIUM_RATE_SPOIL = custom.getProperty("PremiumRateSpoil", 1.);
		PREMIUM_RATE_DROP_RAID = custom.getProperty("PremiumRateDropRaid", 1.);
		PREMIUM_RATE_XP = custom.getProperty("PremiumRateXp", 1.);
		
	}
	
	// c1c0s auto vote reward system start
	private static final void loadAutoReward()
	{
		final ExProperties AutoRewardSystem = initProperties(AutoReward_System);
		ENABLE_VOTE_SYSTEM = Boolean.parseBoolean(AutoRewardSystem.getProperty("EnableVoteSystem", "False"));
		HOPZONE_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("HopzoneRewardId", "3470"));
		HOPZONE_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("HopzoneRewardCount", "10"));
		VOTES_SITE_HOPZONE_URL1 = AutoRewardSystem.getProperty("VotesSiteHopZoneUrl", "");
		NETWORK_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("NetworkRewardId", "3470"));
		NETWORK_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("NetworkRewardCount", "10"));
		VOTES_SITE_NETWORK_URL1 = AutoRewardSystem.getProperty("VotesSiteNetworkUrl", "");
		TOPZONE_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("TopzoneRewardId", "3470"));
		TOPZONE_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("TopzoneRewardCount", "10"));
		VOTES_SITE_TOPZONE_URL1 = AutoRewardSystem.getProperty("VotesSiteTopzoneUrl", "");
		VOTES_SITE_TOPZONE_URL_API = AutoRewardSystem.getProperty("VotesSiteTopzoneUrlAPI", "");
		MMOTOP_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("MMOTOPRewardId", "3470"));
		MMOTOP_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("MMOTOPRewardCount", "10"));
		VOTES_SITE_MMOTOP_URL1 = AutoRewardSystem.getProperty("VotesSiteMMOTOPUrl", "");
		L2TOPSERVERS_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("L2TOPSERVERSRewardId", "3470"));
		L2TOPSERVERS_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("L2TOPSERVERSRewardCount", "10"));
		VOTES_SITE_L2TOPSERVERS_URL1 = AutoRewardSystem.getProperty("VotesSiteL2TOPSERVERSUrl", "");
		L2TOPGSNET_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("L2TOPGSNETRewardId", "3470"));
		L2TOPGSNET_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("L2TOPGSNETRewardCount", "10"));
		VOTES_SITE_L2TOPGSNET_URL1 = AutoRewardSystem.getProperty("VotesSiteL2TOPGSNETUrl", "");
		L2TOPCO_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("L2TOPCORewardId", "3470"));
		L2TOPCO_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("L2TOPCORewardCount", "10"));
		VOTES_SITE_L2TOPCO_URL1 = AutoRewardSystem.getProperty("VotesSiteL2TOPCOUrl", "");
		L2JBrasil_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("L2JBrasilRewardId", "3470"));
		L2JBrasil_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("L2JBrasilRewardCount", "10"));
		VOTES_SITE_L2JBrasil_URL1 = AutoRewardSystem.getProperty("VotesSiteL2JBrasilUrl", "");
		L2VOTES_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("L2VOTESRewardId", "3470"));
		L2VOTES_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("L2VOTESRewardCount", "10"));
		VOTES_SITE_L2VOTES_URL1 = AutoRewardSystem.getProperty("VotesSiteL2VOTESUrl", "");
		itopz_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("itopzRewardId", "3470"));
		itopz_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("itopzRewardCount", "10"));
		VOTES_SITE_itopz_URL1 = AutoRewardSystem.getProperty("VotesSiteitopzUrl", "");
		mmotopru_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("mmotopruRewardId", "3470"));
		mmotopru_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("mmotopruRewardCount", "10"));
		VOTES_SITE_mmotopru_URL1 = AutoRewardSystem.getProperty("VotesSitemmotopruUrl", "");
		l2serverscom_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("l2serverscomRewardId", "3470"));
		l2serverscom_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("l2serverscomRewardCount", "10"));
		VOTES_SITE_l2serverscom_URL1 = AutoRewardSystem.getProperty("VotesSitel2serverscomUrl", "");
		l2serversVOTES_FOR_REWARD = Integer.parseInt(AutoRewardSystem.getProperty("l2serversVotesRequiredForReward", "100"));
		l2jtopcom_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("l2jtopcomRewardId", "3470"));
		l2jtopcom_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("l2jtopcomRewardCount", "10"));
		VOTES_SITE_l2jtopcom_URL1 = AutoRewardSystem.getProperty("VotesSitel2jtopcomUrl", "");
		L2TOPGR_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("L2TOPGRRewardId", "3470"));
		L2TOPGR_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("L2TOPGRRewardCount", "10"));
		VOTES_SITE_L2TOPGR_URL1 = AutoRewardSystem.getProperty("VotesSiteL2TOPGRUrl", "");
		Gametopseu_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("GAMETOPSEURewardId", "3470"));
		Gametopseu_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("GAMETOPSEURewardCount", "10"));
		VOTES_SITE_Gametopseu_URL1 = AutoRewardSystem.getProperty("VotesSiteGAMETOPSEUUrl", "");
		GAMETOPSEUVOTES_FOR_REWARD = Integer.parseInt(AutoRewardSystem.getProperty("GAMETOPSEUVotesRequiredForReward", "100"));
		VOTES_FOR_REWARDHOPZONE = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardHOPZONE", "100"));
		VOTES_FOR_REWARDITOPZ = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardITOPZ", "100"));
		VOTES_FOR_REWARDL2JBRASIL = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardL2JBRASIL", "100"));
		VOTES_FOR_REWARDL2JTOP = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardL2JTOP", "100"));
		VOTES_FOR_REWARDL2TOPCO = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardL2TOPCO", "100"));
		VOTES_FOR_REWARDL2TOPGR = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardL2TOPGR", "100"));
		VOTES_FOR_REWARDL2TOPGSNET = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardL2TOPGSNET", "100"));
		VOTES_FOR_REWARDL2TOPSERVERS = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardL2TOPSERVERS", "100"));
		VOTES_FOR_REWARDL2VOTES = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardL2VOTES", "100"));
		VOTES_FOR_REWARDMMOTOPEU = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardMMOTOPEU", "100"));
		VOTES_FOR_REWARDMMOTOPRU = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardMMOTOPRU", "100"));
		VOTES_FOR_REWARDNETWORK = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardNETWORK", "100"));
		VOTES_FOR_REWARDTOPZONE = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardTOPZONE", "100"));
		VOTES_SYSTEM_INITIAL_DELAYGAMETOPSEU = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayGAMETOPSEU", "1800000"));
		VOTES_SYSTEM_STEP_DELAYGAMETOPSEU = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayGAMETOPSEU", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYHOPZONE = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayHOPZONE", "1800000"));
		VOTES_SYSTEM_STEP_DELAYHOPZONE = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayHOPZONE", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYITOPZ = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayITOPZ", "1800000"));
		VOTES_SYSTEM_STEP_DELAYITOPZ = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayITOPZ", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYL2JBRASIL = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayL2JBRASIL", "1800000"));
		VOTES_SYSTEM_STEP_DELAYL2JBRASIL = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayL2JBRASIL", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYL2JTOP = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayL2JTOP", "1800000"));
		VOTES_SYSTEM_STEP_DELAYL2JTOP = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayL2JTOP", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYL2SERVERS = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayL2SERVERS", "1800000"));
		VOTES_SYSTEM_STEP_DELAYL2SERVERS = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayL2SERVERS", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYL2TOPCO = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayL2TOPCO", "1800000"));
		VOTES_SYSTEM_STEP_DELAYL2TOPCO = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayL2TOPCO", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYL2TOPGR = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayL2TOPGR", "1800000"));
		VOTES_SYSTEM_STEP_DELAYL2TOPGR = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayL2TOPGR", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYL2TOPGSNET = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayL2TOPGSNET", "1800000"));
		VOTES_SYSTEM_STEP_DELAYL2TOPGSNET = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayL2TOPGSNET", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYL2TOPSERVERS = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayL2TOPSERVERS", "1800000"));
		VOTES_SYSTEM_STEP_DELAYL2TOPSERVERS = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayL2TOPSERVERS", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYL2VOTES = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayL2VOTES", "1800000"));
		VOTES_SYSTEM_STEP_DELAYL2VOTES = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayL2VOTES", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYMMOTOPEU = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayMMOTOPEU", "1800000"));
		VOTES_SYSTEM_STEP_DELAYMMOTOPEU = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayMMOTOPEU", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYMMOTOPRU = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayMMOTOPRU", "1800000"));
		VOTES_SYSTEM_STEP_DELAYMMOTOPRU = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayMMOTOPRU", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYNETWORK = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayNETWORK", "1800000"));
		VOTES_SYSTEM_STEP_DELAYNETWORK = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayNETWORK", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYTOPZONE = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayTOPZONE", "1800000"));
		VOTES_SYSTEM_STEP_DELAYTOPZONE = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayTOPZONE", "60000"));
		VOTES_SYSTEM_INITIAL_DELAYSERVERTOP = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelaySERVERTOP", "1800000"));
		VOTES_SYSTEM_STEP_DELAYSERVERTOP = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelaySERVERTOP", "60000"));
		VOTES_FOR_REWARDSERVERTOP = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardSERVERTOP", "100"));
		SERVERTOP_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("SERVERTOPRewardId", "3470"));
		SERVERTOP_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("SERVERTOPRewardCount", "10"));
		VOTES_SITE_servertop_URL1 = AutoRewardSystem.getProperty("VotesSiteSERVERTOPUrl", "");
		VOTES_SYSTEM_INITIAL_DELAYL2NET = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayL2NET", "1800000"));
		VOTES_SYSTEM_STEP_DELAYL2NET = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayL2NET", "60000"));
		VOTES_FOR_REWARDL2NET = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardL2NET", "100"));
		L2NET_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("L2NETRewardId", "3470"));
		L2NET_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("L2NETRewardCount", "10"));
		VOTES_SITE_L2NET_URL1 = AutoRewardSystem.getProperty("VotesSiteL2NETUrl", "");
		VOTES_SYSTEM_INITIAL_DELAYL2TOPEU = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemInitialDelayL2TOPEU", "1800000"));
		VOTES_SYSTEM_STEP_DELAYL2TOPEU = Integer.parseInt(AutoRewardSystem.getProperty("VotesSystemStepDelayL2TOPEU", "60000"));
		VOTES_FOR_REWARDL2TOPEU = Integer.parseInt(AutoRewardSystem.getProperty("VotesForRewardL2TOPEU", "100"));
		L2TOPEU_REWARD_ID = Integer.parseInt(AutoRewardSystem.getProperty("L2TOPEURewardId", "3470"));
		L2TOPEU_REWARD_COUNT = Integer.parseInt(AutoRewardSystem.getProperty("L2TOPEURewardCount", "10"));
		VOTES_SITE_L2TOPEU_URL1 = AutoRewardSystem.getProperty("VotesSiteL2TOPEUUrl", "");
	}
	// c1c0s auto vote reward system end
	
	// c1c0s vote manager start
	private static final void loadVoteManagerAPI()
	{
		final ExProperties VoteManagerAPI = initProperties(VoteManager_Personal);
		API_KEYTOPZONE = VoteManagerAPI.getProperty("ApiKeyTopzone", "dsad");
		SERVERID_KEYTOPZONE = Integer.parseInt(VoteManagerAPI.getProperty("ServerIdTopzone", "100"));
		APIKEY_HOPZONE = VoteManagerAPI.getProperty("ApiKeyHopzone", "dsada");
		SERVERID_NETWORK = VoteManagerAPI.getProperty("ServerIdNetwork", "dasa");
		VoteManagerApi_REWARD_ID = Integer.parseInt(VoteManagerAPI.getProperty("VoteManagerRewardId", "3470"));
		VoteManagerApi_REWARD_COUNT = Integer.parseInt(VoteManagerAPI.getProperty("VoteManagerRewardCount", "10"));
		APIKEY_L2JTOPCOM = VoteManagerAPI.getProperty("ApiKeyL2JTOPCOM", "dsada");
		APIKEY_L2TOPSERVERS = VoteManagerAPI.getProperty("ApiKeyL2TOPSERVERS", "dsada");
		APIKEY_L2TOPGAMESERVER = VoteManagerAPI.getProperty("ApiKeyL2TOPGAMESERVER", "dsada");
		APIKEY_ITOPZ = VoteManagerAPI.getProperty("ApiKeyITOPZ", "dsad");
		SERVERID_ITOPZ = Integer.parseInt(VoteManagerAPI.getProperty("ServerIdITOPZ", "100"));
		APIKEY_L2VOTESCOM = VoteManagerAPI.getProperty("ApiKeyL2VOTESCOM", "dsad");
		SERVERID_L2TOPCO = Integer.parseInt(VoteManagerAPI.getProperty("ServerIdL2TOPCO", "100"));
		HASH_L2SERVERSCOM = VoteManagerAPI.getProperty("HASHL2SERVERSCOM", "dsad");
		SERVERID_L2SERVERSCOM = Integer.parseInt(VoteManagerAPI.getProperty("ServerIdL2SERVERSCOM", "100"));
		APIKEY_L2JBRASIL = VoteManagerAPI.getProperty("ApiKeyL2JBRASIL", "dsad");
		SERVERID_GAMETOPSEU = Integer.parseInt(VoteManagerAPI.getProperty("ServerIdGAMETOPSEU", "100"));
		SERVERID_TOP100ARENA = Integer.parseInt(VoteManagerAPI.getProperty("ServerIdTOP100ARENA", "100"));
		SERVERID_L2NET = Integer.parseInt(VoteManagerAPI.getProperty("ServerIdL2NET", "100"));
		SERVERID_L2TOPEU = Integer.parseInt(VoteManagerAPI.getProperty("ServerIdL2TOPEU", "100"));
	}
	// c1c0s vote manager end
	
	private static final void loadDungeon()
	{
		// Dungeon
		ExProperties SafeDungeon = initProperties(DUNGEON_REWARD_FILE);
		
		DUNGEON_COIN_ID = SafeDungeon.getProperty("DungeonCoinId", 57);
		PARTY_DUNGEON_COIN_ID = SafeDungeon.getProperty("PartyDungeonCoinId", 57);
		CONT_DUNGEON_ITEM = SafeDungeon.getProperty("DungeonContItem", 1);
		DUNGEON_SPAWN_X = SafeDungeon.getProperty("DungeonSpawnX", 82635);
		DUNGEON_SPAWN_Y = SafeDungeon.getProperty("DungeonSpawnY", 148798);
		DUNGEON_SPAWN_Z = SafeDungeon.getProperty("DungeonSpawnZ", -3464);
		DUNGEON_SPAWN_RND = SafeDungeon.getProperty("DungeonSpawnRnd", 25);
		
		DUNGEON_ITEM_RENEWAL0 = SafeDungeon.getProperty("DungeonRenewalHtml0", 15);
		DUNGEON_ITEM_RENEWAL1 = SafeDungeon.getProperty("DungeonRenewalHtml1", 15);
		DUNGEON_ITEM_RENEWAL2 = SafeDungeon.getProperty("DungeonRenewalHtml2", 15);
		DUNGEON_ITEM_RENEWAL3 = SafeDungeon.getProperty("DungeonRenewalHtml3", 15);
		DUNGEON_ITEM_RENEWAL4 = SafeDungeon.getProperty("DungeonRenewalHtml4", 15);
		DUNGEON_ITEM_RENEWAL5 = SafeDungeon.getProperty("DungeonRenewalHtml5", 15);
		DUNGEON_ITEM_RENEWAL6 = SafeDungeon.getProperty("DungeonRenewalHtml6", 15);
		DUNGEON_ITEM_RENEWAL7 = SafeDungeon.getProperty("DungeonRenewalHtml7", 15);
		DUNGEON_ITEM_RENEWAL8 = SafeDungeon.getProperty("DungeonRenewalHtml8", 15);
		DUNGEON_ITEM_RENEWAL9 = SafeDungeon.getProperty("DungeonRenewalHtml9", 15);
		DUNGEON_ITEM_RENEWAL10 = SafeDungeon.getProperty("DungeonRenewalHtml10", 15);
		DUNGEON_PARTY_ITEM_RENEWAL0 = SafeDungeon.getProperty("DungeonPartyRenewalHtml0", 15);
		DUNGEON_PARTY_ITEM_RENEWAL1 = SafeDungeon.getProperty("DungeonPartyRenewalHtml1", 15);
		DUNGEON_PARTY_ITEM_RENEWAL2 = SafeDungeon.getProperty("DungeonPartyRenewalHtml2", 15);
		DUNGEON_PARTY_ITEM_RENEWAL3 = SafeDungeon.getProperty("DungeonPartyRenewalHtml3", 15);
		DUNGEON_PARTY_ITEM_RENEWAL4 = SafeDungeon.getProperty("DungeonPartyRenewalHtml4", 15);
	}
	
	// c1c0s premium system start
	private static final void loadPremiumSystem()
	{
		final ExProperties premiumsystem = initProperties(Premium_System);
		AUGMENTATION_NG_SKILL_CHANCE_FORVIP = premiumsystem.getProperty("AugmentationNGSkillChanceFORVIP", 1);
		AUGMENTATION_NG_GLOW_CHANCE_FORVIP = premiumsystem.getProperty("AugmentationNGGlowChanceFORVIP", 0);
		AUGMENTATION_MID_SKILL_CHANCE_FORVIP = premiumsystem.getProperty("AugmentationMidSkillChanceFORVIP", 2);
		AUGMENTATION_MID_GLOW_CHANCE_FORVIP = premiumsystem.getProperty("AugmentationMidGlowChanceFORVIP", 40);
		AUGMENTATION_HIGH_SKILL_CHANCE_FORVIP = premiumsystem.getProperty("AugmentationHighSkillChanceFORVIP", 5);
		AUGMENTATION_HIGH_GLOW_CHANCE_FORVIP = premiumsystem.getProperty("AugmentationHighGlowChanceFORVIP", 70);
		AUGMENTATION_TOP_SKILL_CHANCE_FORVIP = premiumsystem.getProperty("AugmentationTopSkillChanceFORVIP", 8);
		AUGMENTATION_TOP_GLOW_CHANCE_FORVIP = premiumsystem.getProperty("AugmentationTopGlowChanceFORVIP", 100);
	}
	// c1c0s premium system end
	
	/**
	 * Loads player settings.<br>
	 * Such as stats, inventory/warehouse, enchant, augmentation, karma, party, admin, petition, skill learn.
	 */
	private static final void loadPlayers()
	{
		final ExProperties players = initProperties(PLAYERS_FILE);
		
		BANKING_SYSTEM_ENABLED = Boolean.parseBoolean(players.getProperty("BankingEnabled", "false"));
		BANKING_SYSTEM_GOLDBARS = Integer.parseInt(players.getProperty("BankingGoldbarCount", "1"));
		BANKING_SYSTEM_ADENA = Integer.parseInt(players.getProperty("BankingAdenaCount", "100000000"));	//100KK
		
		EFFECT_CANCELING = players.getProperty("CancelLesserEffect", true);
		HP_REGEN_MULTIPLIER = players.getProperty("HpRegenMultiplier", 1.);
		MP_REGEN_MULTIPLIER = players.getProperty("MpRegenMultiplier", 1.);
		CP_REGEN_MULTIPLIER = players.getProperty("CpRegenMultiplier", 1.);
		PLAYER_SPAWN_PROTECTION = players.getProperty("PlayerSpawnProtection", 0);
		PLAYER_FAKEDEATH_UP_PROTECTION = players.getProperty("PlayerFakeDeathUpProtection", 0);
		RESPAWN_RESTORE_HP = players.getProperty("RespawnRestoreHP", 0.7);
		MAX_PVTSTORE_SLOTS_DWARF = players.getProperty("MaxPvtStoreSlotsDwarf", 5);
		MAX_PVTSTORE_SLOTS_OTHER = players.getProperty("MaxPvtStoreSlotsOther", 4);
		DEEPBLUE_DROP_RULES = players.getProperty("UseDeepBlueDropRules", true);
		ALLOW_DELEVEL = players.getProperty("AllowDelevel", true);
		DEATH_PENALTY_CHANCE = players.getProperty("DeathPenaltyChance", 20);
		
		INVENTORY_MAXIMUM_NO_DWARF = players.getProperty("MaximumSlotsForNoDwarf", 80);
		INVENTORY_MAXIMUM_DWARF = players.getProperty("MaximumSlotsForDwarf", 100);
		INVENTORY_MAXIMUM_PET = players.getProperty("MaximumSlotsForPet", 12);
		MAX_ITEM_IN_PACKET = Math.max(INVENTORY_MAXIMUM_NO_DWARF, INVENTORY_MAXIMUM_DWARF);
		WEIGHT_LIMIT = players.getProperty("WeightLimit", 1.);
		WAREHOUSE_SLOTS_NO_DWARF = players.getProperty("MaximumWarehouseSlotsForNoDwarf", 100);
		WAREHOUSE_SLOTS_DWARF = players.getProperty("MaximumWarehouseSlotsForDwarf", 120);
		WAREHOUSE_SLOTS_CLAN = players.getProperty("MaximumWarehouseSlotsForClan", 150);
		FREIGHT_SLOTS = players.getProperty("MaximumFreightSlots", 20);
		REGION_BASED_FREIGHT = players.getProperty("RegionBasedFreight", true);
		FREIGHT_PRICE = players.getProperty("FreightPrice", 1000);
		
		ENCHANT_CHANCE_WEAPON = players.getProperty("EnchantChanceWeapon", 0.60);
		ENCHANT_CHANCE_WEAPON_10PLUS = players.getProperty("EnchantChanceWeapon10Plus", 0.55);
		ENCHANT_CHANCE_WEAPON_13PLUS = players.getProperty("EnchantChanceWeapon13Plus", 0.50);
		ENCHANT_CHANCE_WEAPON_16PLUS = players.getProperty("EnchantChanceWeapon16Plus", 0.45);
		
		 ENCHANT_CHANCE_NORMAL_WEAPON = players.getProperty("EnchantChanceNormalWeapon", 0.65);
		 ENCHANT_CHANCE_NORMAL70_WEAPON = players.getProperty("EnchantChanceNormal70Weapon", 0.70);
		 ENCHANT_CHANCE_NORMAL75_WEAPON = players.getProperty("EnchantChanceNormal75Weapon", 0.75);
		 ENCHANT_CHANCE_NORMAL80_WEAPON = players.getProperty("EnchantChanceNormal80Weapon", 0.80);
		
		 ENCHANT_CHANCE_NORMAL_ARMOR  = players.getProperty("EnchantChanceNormalArmor", 0.65);
		 ENCHANT_CHANCE_NORMAL70_ARMOR = players.getProperty("EnchantChanceNormal70Armor", 0.70);
		 ENCHANT_CHANCE_NORMAL75_ARMOR = players.getProperty("EnchantChanceNormal75Armor", 0.75);
		 ENCHANT_CHANCE_NORMAL80_ARMOR = players.getProperty("EnchantChanceNormal80Armor", 0.80);
		
		 ENCHANT_CHANCE_BLESSED_WEAPON   = players.getProperty("EnchantChanceBlessedWeapon", 0.70);
		 ENCHANT_CHANCE_BLESSED75_WEAPON = players.getProperty("EnchantChanceNormal75Weapon", 0.75);
		
		 ENCHANT_CHANCE_BLESSED_ARMOR   = players.getProperty("EnchantChanceBlessedArmor", 0.70);
		 ENCHANT_CHANCE_BLESSED75_ARMOR = players.getProperty("EnchantChanceNormal75Armor", 0.75);
		
		 ENCHANT_CHANCE_IMPROVEDBLESSED_WEAPON = players.getProperty("EnchantChanceImprovedBlessedWeapon", 0.70);
		 ENCHANT_CHANCE_IMPROVEDBLESSED_ARMOR  = players.getProperty("EnchantChanceImprovedBlessedArmor", 0.70);
		
		ENCHANT_CHANCE_WEAPON_BLESSED = players.getProperty("EnchantChanceBlessed", 0.65);
		ENCHANT_CHANCE_WEAPON_BLESSED_10PLUS = players.getProperty("EnchantChanceBlessed10Plus", 0.6);
		ENCHANT_CHANCE_WEAPON_BLESSED_13PLUS = players.getProperty("EnchantChanceBlessed13Plus", 0.55);
		ENCHANT_CHANCE_WEAPON_BLESSED_16PLUS = players.getProperty("EnchantChanceBlessed16Plus", 0.45);
		
		ENCHANT_CHANCE_WEAPON_CRYSTAL = players.getProperty("EnchantChanceCrystal", 0.65);
		ENCHANT_CHANCE_WEAPON_CRYSTAL_10PLUS = players.getProperty("EnchantChanceCrystal10Plus", 0.6);
		ENCHANT_CHANCE_WEAPON_CRYSTAL_13PLUS = players.getProperty("EnchantChanceCrystal13Plus", 0.55);
		ENCHANT_CHANCE_WEAPON_CRYSTAL_16PLUS = players.getProperty("EnchantChanceCrystal16Plus", 0.5);
		//ENCHANT_CHANCE_WEAPON_NONMAGIC = players.getProperty("EnchantChanceNonMagicWeapon", 0.65);
		//ENCHANT_CHANCE_WEAPON_NONMAGIC_12PLUS = players.getProperty("EnchantChanceNonMagicWeapon12Plus", 0.5);
		ENCHANT_CHANCE_ARMOR = players.getProperty("EnchantChanceArmor", 0.65);
		ENCHANT_CHANCE_ARMOR_12PLUS = players.getProperty("EnchantChanceArmor12Plus", 0.6);
		ENCHANT_CHANCE_ARMOR_16PLUS = players.getProperty("EnchantChanceArmor16Plus", 0.5);
		ENCHANT_MAX_WEAPON = players.getProperty("EnchantMaxWeapon", 20);
		ENCHANT_MAX_ARMOR = players.getProperty("EnchantMaxArmor", 20);
		ENCHANT_SAFE_MAX = players.getProperty("EnchantSafeMax", 3);
		ENCHANT_SAFE_MAX_FULL = players.getProperty("EnchantSafeMaxFull", 4);
		
		AUGMENTATION_NG_SKILL_CHANCE = players.getProperty("AugmentationNGSkillChance", 15);
		AUGMENTATION_NG_GLOW_CHANCE = players.getProperty("AugmentationNGGlowChance", 0);
		AUGMENTATION_MID_SKILL_CHANCE = players.getProperty("AugmentationMidSkillChance", 30);
		AUGMENTATION_MID_GLOW_CHANCE = players.getProperty("AugmentationMidGlowChance", 40);
		AUGMENTATION_HIGH_SKILL_CHANCE = players.getProperty("AugmentationHighSkillChance", 45);
		AUGMENTATION_HIGH_GLOW_CHANCE = players.getProperty("AugmentationHighGlowChance", 70);
		AUGMENTATION_TOP_SKILL_CHANCE = players.getProperty("AugmentationTopSkillChance", 60);
		AUGMENTATION_TOP_GLOW_CHANCE = players.getProperty("AugmentationTopGlowChance", 100);
		AUGMENTATION_BASESTAT_CHANCE = players.getProperty("AugmentationBaseStatChance", 1);
		
		KARMA_PLAYER_CAN_SHOP = players.getProperty("KarmaPlayerCanShop", false);
		KARMA_PLAYER_CAN_USE_GK = players.getProperty("KarmaPlayerCanUseGK", false);
		KARMA_PLAYER_CAN_TELEPORT = players.getProperty("KarmaPlayerCanTeleport", true);
		KARMA_PLAYER_CAN_TRADE = players.getProperty("KarmaPlayerCanTrade", true);
		KARMA_PLAYER_CAN_USE_WH = players.getProperty("KarmaPlayerCanUseWareHouse", true);
		KARMA_DROP_GM = players.getProperty("CanGMDropEquipment", false);
		KARMA_AWARD_PK_KILL = players.getProperty("AwardPKKillPVPPoint", true);
		KARMA_PK_LIMIT = players.getProperty("MinimumPKRequiredToDrop", 5);
		KARMA_NONDROPPABLE_PET_ITEMS = players.getProperty("ListOfPetItems", new int[]
		{
			2375,
			3500,
			3501,
			3502,
			4422,
			4423,
			4424,
			4425,
			6648,
			6649,
			6650
		});
		KARMA_NONDROPPABLE_ITEMS = players.getProperty("ListOfNonDroppableItemsForPK", new int[]
		{
			1147,
			425,
			1146,
			461,
			10,
			2368,
			7,
			6,
			2370,
			2369
		});
		
		PVP_NORMAL_TIME = players.getProperty("PvPVsNormalTime", 40000);
		PVP_PVP_TIME = players.getProperty("PvPVsPvPTime", 20000);
		
		PARTY_XP_CUTOFF_METHOD = players.getProperty("PartyXpCutoffMethod", "level");
		PARTY_XP_CUTOFF_PERCENT = players.getProperty("PartyXpCutoffPercent", 3.);
		PARTY_XP_CUTOFF_LEVEL = players.getProperty("PartyXpCutoffLevel", 20);
		PARTY_RANGE = players.getProperty("PartyRange", 1500);
		
		DEFAULT_ACCESS_LEVEL = players.getProperty("DefaultAccessLevel", 0);
		GM_HERO_AURA = players.getProperty("GMHeroAura", false);
		GM_STARTUP_INVULNERABLE = players.getProperty("GMStartupInvulnerable", false);
		GM_STARTUP_INVISIBLE = players.getProperty("GMStartupInvisible", false);
		GM_STARTUP_BLOCK_ALL = players.getProperty("GMStartupBlockAll", false);
		GM_STARTUP_AUTO_LIST = players.getProperty("GMStartupAutoList", true);
		
		PETITIONING_ALLOWED = players.getProperty("PetitioningAllowed", true);
		MAX_PETITIONS_PER_PLAYER = players.getProperty("MaxPetitionsPerPlayer", 5);
		MAX_PETITIONS_PENDING = players.getProperty("MaxPetitionsPending", 25);
		
		IS_CRAFTING_ENABLED = players.getProperty("CraftingEnabled", true);
		DWARF_RECIPE_LIMIT = players.getProperty("DwarfRecipeLimit", 50);
		COMMON_RECIPE_LIMIT = players.getProperty("CommonRecipeLimit", 50);
		BLACKSMITH_USE_RECIPES = players.getProperty("BlacksmithUseRecipes", true);
		
		AUTO_LEARN_SKILLS = players.getProperty("AutoLearnSkills", false);
		MAGIC_FAILURES = players.getProperty("MagicFailures", true);
		PERFECT_SHIELD_BLOCK_RATE = players.getProperty("PerfectShieldBlockRate", 5);
		LIFE_CRYSTAL_NEEDED = players.getProperty("LifeCrystalNeeded", true);
		SP_BOOK_NEEDED = players.getProperty("SpBookNeeded", true);
		ES_SP_BOOK_NEEDED = players.getProperty("EnchantSkillSpBookNeeded", true);
		DIVINE_SP_BOOK_NEEDED = players.getProperty("DivineInspirationSpBookNeeded", true);
		SUBCLASS_WITHOUT_QUESTS = players.getProperty("SubClassWithoutQuests", false);
		
		MAX_BUFFS_AMOUNT = players.getProperty("MaxBuffsAmount", 20);
		MAX_CUSTOM_BUFFS = players.getProperty("MaxCustomBuffsAmount", 4);
		STORE_SKILL_COOLTIME = players.getProperty("StoreSkillCooltime", true);
	
		
		
	}
	
	/**
	 * Loads siege settings.
	 */
	private static final void loadSieges()
	{
		final ExProperties sieges = initProperties(Config.SIEGE_FILE);
		
		SIEGE_LENGTH = sieges.getProperty("SiegeLength", 120);
		MINIMUM_CLAN_LEVEL = sieges.getProperty("SiegeClanMinLevel", 4);
		MAX_ATTACKERS_NUMBER = sieges.getProperty("AttackerMaxClans", 10);
		MAX_DEFENDERS_NUMBER = sieges.getProperty("DefenderMaxClans", 10);
		ATTACKERS_RESPAWN_DELAY = sieges.getProperty("AttackerRespawn", 10000);
		
		CH_MINIMUM_CLAN_LEVEL = sieges.getProperty("ChSiegeClanMinLevel", 4);
		CH_MAX_ATTACKERS_NUMBER = sieges.getProperty("ChAttackerMaxClans", 10);
	}
	
	private static final void load_News()
	{
		final ExProperties myNews = initProperties(Config.NEWS);
		News_1 = myNews.getProperty("News_1","");
		News_2 = myNews.getProperty("News_2","");
		News_3 = myNews.getProperty("News_3","");
		News_4 = myNews.getProperty("News_4","");
		News_5 = myNews.getProperty("News_5","");
		News_6 = myNews.getProperty("News_6","");
		News_7 = myNews.getProperty("News_7","");
		News_8 = myNews.getProperty("News_8","");
		News_9 = myNews.getProperty("News_9","");
		News_10 = myNews.getProperty("News_10","");
		News_11 = myNews.getProperty("News_11","");
		News_12 = myNews.getProperty("News_12","");
		News_13 = myNews.getProperty("News_13","");
		News_14 = myNews.getProperty("News_14","");
		News_15 = myNews.getProperty("News_15","");
		
	}
	
	private static final void loadTournament()
	{
		final ExProperties tournament = initProperties(TOURNAMENT);
		
		String[] npcLoc = tournament.getProperty("TournamentNpcLocation", "150086,46733,-3412").split(",");
		TOURNAMENT_NPC_ID = tournament.getProperty("TournamentNpcId", 50009);
		TOURNAMENT_NPC_LOCATION = new Location(Integer.parseInt(npcLoc[0]), Integer.parseInt(npcLoc[1]), Integer.parseInt(npcLoc[2]));
		TOURNAMENT_EVENT_INTERVAL_BY_TIME_OF_DAY = tournament.getProperty("TournamentStartTime", "20:00").split(",");
		TOURNAMENT_EVENT_DURATION = tournament.getProperty("TournamentDuration", 5);
		for (String item : tournament.getProperty("ItemRestrictedList", "").split(","))
		{
			TOURNAMENT_RESTRICTED_ITEM_LIST.add(Integer.parseInt(item));
		}
		for (String skill : tournament.getProperty("SkillRestrictedList", "").split(","))
		{
			TOURNAMENT_RESTRICTED_SKILL_LIST.add(Integer.parseInt(skill));
		}
		TOURNAMENT_TIME_SEARCH_FIGHTS = tournament.getProperty("TimeBetweenSearchFights", 5);
		TOURNAMENT_DEBUG = tournament.getProperty("Debug", true);
		TOURNAMENT_TIME_TO_TELEPORT = tournament.getProperty("TeleportFightTime", 10);
		TOURNAMENT_FIGHT_REWARD_WINNER.clear();
		for (String s : tournament.getProperty("WinnerRewards", "57,1000;3470,10").split(";"))
		{
			String[] reward = s.split(",");
			
			RewardHolder simpleReward = new RewardHolder(Integer.parseInt(reward[0]), Integer.parseInt(reward[1]));
			TOURNAMENT_FIGHT_REWARD_WINNER.add(simpleReward);
		}
		TOURNAMENT_FIGHT_REWARD_LOOSER.clear();
		for (String s : tournament.getProperty("LooserRewards", "57,500;3470,5").split(";"))
		{
			String[] reward = s.split(",");
			
			RewardHolder simpleReward = new RewardHolder(Integer.parseInt(reward[0]), Integer.parseInt(reward[1]));
			TOURNAMENT_FIGHT_REWARD_LOOSER.add(simpleReward);
		}
		
		TOURNAMENT_FIGHT_START_TIME.clear();
		int startTime1x1 = tournament.getProperty("FightStartTime_1x1", 10);
		int startTime2x2 = tournament.getProperty("FightStartTime_2x2", 10);
		int startTime3x3 = tournament.getProperty("FightStartTime_3x3", 10);
		int startTime4x4 = tournament.getProperty("FightStartTime_4x4", 10);
		int startTime5x5 = tournament.getProperty("FightStartTime_5x5", 10);
		int startTime9x9 = tournament.getProperty("FightStartTime_9x9", 10);
		
		TOURNAMENT_FIGHT_START_TIME.put(TournamentFightType.F1X1, startTime1x1);
		TOURNAMENT_FIGHT_START_TIME.put(TournamentFightType.F2X2, startTime2x2);
		TOURNAMENT_FIGHT_START_TIME.put(TournamentFightType.F3X3, startTime3x3);
		TOURNAMENT_FIGHT_START_TIME.put(TournamentFightType.F4X4, startTime4x4);
		TOURNAMENT_FIGHT_START_TIME.put(TournamentFightType.F5X5, startTime5x5);
		TOURNAMENT_FIGHT_START_TIME.put(TournamentFightType.F9X9, startTime9x9);
		
		TOURNAMENT_FIGHT_DURATION.clear();
		int fightTime1x1 = tournament.getProperty("FightDuration_1x1", 1);
		int fightTime2x2 = tournament.getProperty("FightDuration_2x2", 1);
		int fightTime3x3 = tournament.getProperty("FightDuration_3x3", 1);
		int fightTime4x4 = tournament.getProperty("FightDuration_4x4", 1);
		int fightTime5x5 = tournament.getProperty("FightDuration_5x5", 1);
		int fightTime9x9 = tournament.getProperty("FightDuration_9x9", 1);
		
		TOURNAMENT_FIGHT_DURATION.put(TournamentFightType.F1X1, fightTime1x1);
		TOURNAMENT_FIGHT_DURATION.put(TournamentFightType.F2X2, fightTime2x2);
		TOURNAMENT_FIGHT_DURATION.put(TournamentFightType.F3X3, fightTime3x3);
		TOURNAMENT_FIGHT_DURATION.put(TournamentFightType.F4X4, fightTime4x4);
		TOURNAMENT_FIGHT_DURATION.put(TournamentFightType.F5X5, fightTime5x5);
		TOURNAMENT_FIGHT_DURATION.put(TournamentFightType.F9X9, fightTime9x9);
		
		TOURNAMENT_DUELIST_ALLOWED.clear();
		TOURNAMENT_DREADNOUGHT_ALLOWED.clear();
		TOURNAMENT_TANKER_ALLOWED.clear();
		TOURNAMENT_DAGGER_ALLOWED.clear();
		TOURNAMENT_ARCHER_ALLOWED.clear();
		TOURNAMENT_HEALER_ALLOWED.clear();
		TOURNAMENT_ARCHMAGE_ALLOWED.clear();
		TOURNAMENT_SOULTAKER_ALLOWED.clear();
		TOURNAMENT_MYSTICMUSE_ALLOWED.clear();
		TOURNAMENT_STORMSCREAMER_ALLOWED.clear();
		TOURNAMENT_TITAN_ALLOWED.clear();
		TOURNAMENT_DOMINATOR_ALLOWED.clear();
		TOURNAMENT_DOOMCRYER_ALLOWED.clear();
		// ARCHERS
		int archer1x1 = tournament.getProperty("ArchersCountAllowed_1x1", 1);
		int archer2x2 = tournament.getProperty("ArchersCountAllowed_2x2", 1);
		int archer3x3 = tournament.getProperty("ArchersCountAllowed_3x3", 1);
		int archer4x4 = tournament.getProperty("ArchersCountAllowed_4x4", 1);
		int archer5x5 = tournament.getProperty("ArchersCountAllowed_5x5", 1);
		int archer9x9 = tournament.getProperty("ArchersCountAllowed_9x9", 1);
		
		TOURNAMENT_ARCHER_ALLOWED.put(TournamentFightType.F1X1, archer1x1);
		TOURNAMENT_ARCHER_ALLOWED.put(TournamentFightType.F2X2, archer2x2);
		TOURNAMENT_ARCHER_ALLOWED.put(TournamentFightType.F3X3, archer3x3);
		TOURNAMENT_ARCHER_ALLOWED.put(TournamentFightType.F4X4, archer4x4);
		TOURNAMENT_ARCHER_ALLOWED.put(TournamentFightType.F5X5, archer5x5);
		TOURNAMENT_ARCHER_ALLOWED.put(TournamentFightType.F9X9, archer9x9);
		
		// archmages
		int archmage1X1 = tournament.getProperty("ArchmagesCountAllowed_1x1", 1);
		int archmage2x2 = tournament.getProperty("ArchmagesCountAllowed_2x2", 1);
		int archmage3x3 = tournament.getProperty("ArchmagesCountAllowed_3x3", 1);
		int archmage4x4 = tournament.getProperty("ArchmagesCountAllowed_4x4", 1);
		int archmage5x5 = tournament.getProperty("ArchmagesCountAllowed_5x5", 1);
		int archmage9x9 = tournament.getProperty("ArchmagesCountAllowed_9x9", 1);
		
		TOURNAMENT_ARCHMAGE_ALLOWED.put(TournamentFightType.F1X1, archmage1X1);
		TOURNAMENT_ARCHMAGE_ALLOWED.put(TournamentFightType.F2X2, archmage2x2);
		TOURNAMENT_ARCHMAGE_ALLOWED.put(TournamentFightType.F3X3, archmage3x3);
		TOURNAMENT_ARCHMAGE_ALLOWED.put(TournamentFightType.F4X4, archmage4x4);
		TOURNAMENT_ARCHMAGE_ALLOWED.put(TournamentFightType.F5X5, archmage5x5);
		TOURNAMENT_ARCHMAGE_ALLOWED.put(TournamentFightType.F9X9, archmage9x9);
		
		// DAGGERS
		int dagger1x1 = tournament.getProperty("DaggersCountAllowed_1x1", 1);
		int dagger2x2 = tournament.getProperty("DaggersCountAllowed_2x2", 1);
		int dagger3x3 = tournament.getProperty("DaggersCountAllowed_3x3", 1);
		int dagger4x4 = tournament.getProperty("DaggersCountAllowed_4x4", 1);
		int dagger5x5 = tournament.getProperty("DaggersCountAllowed_5x5", 1);
		int dagger9x9 = tournament.getProperty("DaggersCountAllowed_9x9", 1);
		
		TOURNAMENT_DAGGER_ALLOWED.put(TournamentFightType.F1X1, dagger1x1);
		TOURNAMENT_DAGGER_ALLOWED.put(TournamentFightType.F2X2, dagger2x2);
		TOURNAMENT_DAGGER_ALLOWED.put(TournamentFightType.F3X3, dagger3x3);
		TOURNAMENT_DAGGER_ALLOWED.put(TournamentFightType.F4X4, dagger4x4);
		TOURNAMENT_DAGGER_ALLOWED.put(TournamentFightType.F5X5, dagger5x5);
		TOURNAMENT_DAGGER_ALLOWED.put(TournamentFightType.F9X9, dagger9x9);
		
		// DOMINATOR
		int dominator1x1 = tournament.getProperty("DominatorsCountAllowed_1x1", 1);
		int dominator2x2 = tournament.getProperty("DominatorsCountAllowed_2x2", 1);
		int dominator3x3 = tournament.getProperty("DominatorsCountAllowed_3x3", 1);
		int dominator4x4 = tournament.getProperty("DominatorsCountAllowed_4x4", 1);
		int dominator5x5 = tournament.getProperty("DominatorsCountAllowed_5x5", 1);
		int dominator9x9 = tournament.getProperty("DominatorsCountAllowed_9x9", 1);
		
		TOURNAMENT_DOMINATOR_ALLOWED.put(TournamentFightType.F1X1, dominator1x1);
		TOURNAMENT_DOMINATOR_ALLOWED.put(TournamentFightType.F2X2, dominator2x2);
		TOURNAMENT_DOMINATOR_ALLOWED.put(TournamentFightType.F3X3, dominator3x3);
		TOURNAMENT_DOMINATOR_ALLOWED.put(TournamentFightType.F4X4, dominator4x4);
		TOURNAMENT_DOMINATOR_ALLOWED.put(TournamentFightType.F5X5, dominator5x5);
		TOURNAMENT_DOMINATOR_ALLOWED.put(TournamentFightType.F9X9, dominator9x9);
		
		// DOOMCRYER
		int doomcryer1x1 = tournament.getProperty("DoomcryersCountAllowed_1x1", 1);
		int doomcryer2x2 = tournament.getProperty("DoomcryersCountAllowed_2x2", 1);
		int doomcryer3x3 = tournament.getProperty("DoomcryersCountAllowed_3x3", 1);
		int doomcryer4x4 = tournament.getProperty("DoomcryersCountAllowed_4x4", 1);
		int doomcryer5x5 = tournament.getProperty("DoomcryersCountAllowed_5x5", 1);
		int doomcryer9x9 = tournament.getProperty("DoomcryersCountAllowed_9x9", 1);
		
		TOURNAMENT_DOOMCRYER_ALLOWED.put(TournamentFightType.F1X1, doomcryer1x1);
		TOURNAMENT_DOOMCRYER_ALLOWED.put(TournamentFightType.F2X2, doomcryer2x2);
		TOURNAMENT_DOOMCRYER_ALLOWED.put(TournamentFightType.F3X3, doomcryer3x3);
		TOURNAMENT_DOOMCRYER_ALLOWED.put(TournamentFightType.F4X4, doomcryer4x4);
		TOURNAMENT_DOOMCRYER_ALLOWED.put(TournamentFightType.F5X5, doomcryer5x5);
		TOURNAMENT_DOOMCRYER_ALLOWED.put(TournamentFightType.F9X9, doomcryer9x9);
		
		// DREADNOUGHT
		int dreadnought1x1 = tournament.getProperty("DreadnoughtsCountAllowed_1x1", 1);
		int dreadnought2x2 = tournament.getProperty("DreadnoughtsCountAllowed_2x2", 1);
		int dreadnought3x3 = tournament.getProperty("DreadnoughtsCountAllowed_3x3", 1);
		int dreadnought4x4 = tournament.getProperty("DreadnoughtsCountAllowed_4x4", 1);
		int dreadnought5x5 = tournament.getProperty("DreadnoughtsCountAllowed_5x5", 1);
		int dreadnought9x9 = tournament.getProperty("DreadnoughtsCountAllowed_9x9", 1);
		
		TOURNAMENT_DREADNOUGHT_ALLOWED.put(TournamentFightType.F1X1, dreadnought1x1);
		TOURNAMENT_DREADNOUGHT_ALLOWED.put(TournamentFightType.F2X2, dreadnought2x2);
		TOURNAMENT_DREADNOUGHT_ALLOWED.put(TournamentFightType.F3X3, dreadnought3x3);
		TOURNAMENT_DREADNOUGHT_ALLOWED.put(TournamentFightType.F4X4, dreadnought4x4);
		TOURNAMENT_DREADNOUGHT_ALLOWED.put(TournamentFightType.F5X5, dreadnought5x5);
		TOURNAMENT_DREADNOUGHT_ALLOWED.put(TournamentFightType.F9X9, dreadnought9x9);
		
		// DUELIST
		int duelist1x1 = tournament.getProperty("DuelistsCountAllowed_1x1", 1);
		int duelist2x2 = tournament.getProperty("DuelistsCountAllowed_2x2", 1);
		int duelist3x3 = tournament.getProperty("DuelistsCountAllowed_3x3", 1);
		int duelist4x4 = tournament.getProperty("DuelistsCountAllowed_4x4", 1);
		int duelist5x5 = tournament.getProperty("DuelistsCountAllowed_5x5", 1);
		int duelist9x9 = tournament.getProperty("DuelistsCountAllowed_9x9", 1);
		
		TOURNAMENT_DUELIST_ALLOWED.put(TournamentFightType.F1X1, duelist1x1);
		TOURNAMENT_DUELIST_ALLOWED.put(TournamentFightType.F2X2, duelist2x2);
		TOURNAMENT_DUELIST_ALLOWED.put(TournamentFightType.F3X3, duelist3x3);
		TOURNAMENT_DUELIST_ALLOWED.put(TournamentFightType.F4X4, duelist4x4);
		TOURNAMENT_DUELIST_ALLOWED.put(TournamentFightType.F5X5, duelist5x5);
		TOURNAMENT_DUELIST_ALLOWED.put(TournamentFightType.F9X9, duelist9x9);
		
		// HEALER
		int healer1x1 = tournament.getProperty("HealersCountAllowed_1x1", 1);
		int healer2x2 = tournament.getProperty("HealersCountAllowed_2x2", 1);
		int healer3x3 = tournament.getProperty("HealersCountAllowed_3x3", 1);
		int healer4x4 = tournament.getProperty("HealersCountAllowed_4x4", 1);
		int healer5x5 = tournament.getProperty("HealersCountAllowed_5x5", 1);
		int healer9x9 = tournament.getProperty("HealersCountAllowed_9x9", 1);
		
		TOURNAMENT_HEALER_ALLOWED.put(TournamentFightType.F1X1, healer1x1);
		TOURNAMENT_HEALER_ALLOWED.put(TournamentFightType.F2X2, healer2x2);
		TOURNAMENT_HEALER_ALLOWED.put(TournamentFightType.F3X3, healer3x3);
		TOURNAMENT_HEALER_ALLOWED.put(TournamentFightType.F4X4, healer4x4);
		TOURNAMENT_HEALER_ALLOWED.put(TournamentFightType.F5X5, healer5x5);
		TOURNAMENT_HEALER_ALLOWED.put(TournamentFightType.F9X9, healer9x9);
		
		// MYSTIC MUSE
		int mysticmuse1x1 = tournament.getProperty("MysticMusesCountAllowed_1x1", 1);
		int mysticmuse2x2 = tournament.getProperty("MysticMusesCountAllowed_2x2", 1);
		int mysticmuse3x3 = tournament.getProperty("MysticMusesCountAllowed_3x3", 1);
		int mysticmuse4x4 = tournament.getProperty("MysticMusesCountAllowed_4x4", 1);
		int mysticmuse5x5 = tournament.getProperty("MysticMusesCountAllowed_5x5", 1);
		int mysticmuse9x9 = tournament.getProperty("MysticMusesCountAllowed_9x9", 1);
		
		TOURNAMENT_MYSTICMUSE_ALLOWED.put(TournamentFightType.F1X1, mysticmuse1x1);
		TOURNAMENT_MYSTICMUSE_ALLOWED.put(TournamentFightType.F2X2, mysticmuse2x2);
		TOURNAMENT_MYSTICMUSE_ALLOWED.put(TournamentFightType.F3X3, mysticmuse3x3);
		TOURNAMENT_MYSTICMUSE_ALLOWED.put(TournamentFightType.F4X4, mysticmuse4x4);
		TOURNAMENT_MYSTICMUSE_ALLOWED.put(TournamentFightType.F5X5, mysticmuse5x5);
		TOURNAMENT_MYSTICMUSE_ALLOWED.put(TournamentFightType.F9X9, mysticmuse9x9);
		
		// SOUL TAKER
		int soulTaker1x1 = tournament.getProperty("SoulTakersCountAllowed_1x1", 1);
		int soulTaker2x2 = tournament.getProperty("SoulTakersCountAllowed_2x2", 1);
		int soulTaker3x3 = tournament.getProperty("SoulTakersCountAllowed_3x3", 1);
		int soulTaker4x4 = tournament.getProperty("SoulTakersCountAllowed_4x4", 1);
		int soulTaker5x5 = tournament.getProperty("SoulTakersCountAllowed_5x5", 1);
		int soulTaker9x9 = tournament.getProperty("SoulTakersCountAllowed_9x9", 1);
		
		TOURNAMENT_SOULTAKER_ALLOWED.put(TournamentFightType.F1X1, soulTaker1x1);
		TOURNAMENT_SOULTAKER_ALLOWED.put(TournamentFightType.F2X2, soulTaker2x2);
		TOURNAMENT_SOULTAKER_ALLOWED.put(TournamentFightType.F3X3, soulTaker3x3);
		TOURNAMENT_SOULTAKER_ALLOWED.put(TournamentFightType.F4X4, soulTaker4x4);
		TOURNAMENT_SOULTAKER_ALLOWED.put(TournamentFightType.F5X5, soulTaker5x5);
		TOURNAMENT_SOULTAKER_ALLOWED.put(TournamentFightType.F9X9, soulTaker9x9);
		
		// TITAN
		int titan1x1 = tournament.getProperty("TitansCountAllowed_1x1", 1);
		int titan2x2 = tournament.getProperty("TitansCountAllowed_2x2", 1);
		int titan3x3 = tournament.getProperty("TitansCountAllowed_3x3", 1);
		int titan4x4 = tournament.getProperty("TitansCountAllowed_4x4", 1);
		int titan5x5 = tournament.getProperty("TitansCountAllowed_5x5", 1);
		int titan9x9 = tournament.getProperty("TitansCountAllowed_9x9", 1);
		
		TOURNAMENT_TITAN_ALLOWED.put(TournamentFightType.F1X1, titan1x1);
		TOURNAMENT_TITAN_ALLOWED.put(TournamentFightType.F2X2, titan2x2);
		TOURNAMENT_TITAN_ALLOWED.put(TournamentFightType.F3X3, titan3x3);
		TOURNAMENT_TITAN_ALLOWED.put(TournamentFightType.F4X4, titan4x4);
		TOURNAMENT_TITAN_ALLOWED.put(TournamentFightType.F5X5, titan5x5);
		TOURNAMENT_TITAN_ALLOWED.put(TournamentFightType.F9X9, titan9x9);
		
		// STORM SCREAMER
		int stormScreamer1x1 = tournament.getProperty("StormScreamersCountAllowed_1x1", 1);
		int stormScreamer2x2 = tournament.getProperty("StormScreamersCountAllowed_2x2", 1);
		int stormScreamer3x3 = tournament.getProperty("StormScreamersCountAllowed_3x3", 1);
		int stormScreamer4x4 = tournament.getProperty("StormScreamersCountAllowed_4x4", 1);
		int stormScreamer5x5 = tournament.getProperty("StormScreamersCountAllowed_5x5", 1);
		int stormScreamer9x9 = tournament.getProperty("StormScreamersCountAllowed_9x9", 1);
		
		TOURNAMENT_STORMSCREAMER_ALLOWED.put(TournamentFightType.F1X1, stormScreamer1x1);
		TOURNAMENT_STORMSCREAMER_ALLOWED.put(TournamentFightType.F2X2, stormScreamer2x2);
		TOURNAMENT_STORMSCREAMER_ALLOWED.put(TournamentFightType.F3X3, stormScreamer3x3);
		TOURNAMENT_STORMSCREAMER_ALLOWED.put(TournamentFightType.F4X4, stormScreamer4x4);
		TOURNAMENT_STORMSCREAMER_ALLOWED.put(TournamentFightType.F5X5, stormScreamer5x5);
		TOURNAMENT_STORMSCREAMER_ALLOWED.put(TournamentFightType.F9X9, stormScreamer9x9);
		
		// TANKERS
		int tanker1x1 = tournament.getProperty("TankersCountAllowed_1x1", 1);
		int tanker2x2 = tournament.getProperty("TankersCountAllowed_2x2", 1);
		int tanker3x3 = tournament.getProperty("TankersCountAllowed_3x3", 1);
		int tanker4x4 = tournament.getProperty("TankersCountAllowed_4x4", 1);
		int tanker5x5 = tournament.getProperty("TankersCountAllowed_5x5", 1);
		int tanker9x9 = tournament.getProperty("TankersCountAllowed_9x9", 1);
		
		TOURNAMENT_TANKER_ALLOWED.put(TournamentFightType.F1X1, tanker1x1);
		TOURNAMENT_TANKER_ALLOWED.put(TournamentFightType.F2X2, tanker2x2);
		TOURNAMENT_TANKER_ALLOWED.put(TournamentFightType.F3X3, tanker3x3);
		TOURNAMENT_TANKER_ALLOWED.put(TournamentFightType.F4X4, tanker4x4);
		TOURNAMENT_TANKER_ALLOWED.put(TournamentFightType.F5X5, tanker5x5);
		TOURNAMENT_TANKER_ALLOWED.put(TournamentFightType.F9X9, tanker9x9);
		
	}
	
	/**
	 * Loads gameserver settings.<br>
	 * IP addresses, database, rates, feature enabled/disabled, misc.
	 */
	private static final void loadServer()
	{
		final ExProperties server = initProperties(SERVER_FILE);
		
		HOSTNAME = server.getProperty("Hostname", "*");
		GAMESERVER_HOSTNAME = server.getProperty("GameserverHostname");
		GAMESERVER_PORT = server.getProperty("GameserverPort", 7777);
		GAMESERVER_LOGIN_HOSTNAME = server.getProperty("LoginHost", "127.0.0.1");
		GAMESERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
		REQUEST_ID = server.getProperty("RequestServerID", 0);
		ACCEPT_ALTERNATE_ID = server.getProperty("AcceptAlternateID", true);
		USE_BLOWFISH_CIPHER = server.getProperty("UseBlowfishCipher", true);
		
		DATABASE_URL = server.getProperty("URL", "jdbc:mariadb://localhost/acis");
		DATABASE_LOGIN = server.getProperty("Login", "root");
		DATABASE_PASSWORD = server.getProperty("Password", "");
		DATABASE_MAX_CONNECTIONS = server.getProperty("MaximumDbConnections", 10);
		
		SERVER_LIST_BRACKET = server.getProperty("ServerListBrackets", false);
		SERVER_LIST_CLOCK = server.getProperty("ServerListClock", false);
		SERVER_GMONLY = server.getProperty("ServerGMOnly", false);
		SERVER_LIST_AGE = server.getProperty("ServerListAgeLimit", 0);
		SERVER_LIST_TESTSERVER = server.getProperty("TestServer", false);
		SERVER_LIST_PVPSERVER = server.getProperty("PvpServer", true);
		
		DELETE_DAYS = server.getProperty("DeleteCharAfterDays", 7);
		MAXIMUM_ONLINE_USERS = server.getProperty("MaximumOnlineUsers", 100);
		
		AUTO_LOOT = server.getProperty("AutoLoot", false);
		AUTO_LOOT_HERBS = server.getProperty("AutoLootHerbs", false);
		AUTO_LOOT_RAID = server.getProperty("AutoLootRaid", false);
		AUTO_LOOT_DUNGEON = server.getProperty("AutoLootDungeon", false);
		
		
		ALLOW_DISCARDITEM = server.getProperty("AllowDiscardItem", true);
		MULTIPLE_ITEM_DROP = server.getProperty("MultipleItemDrop", true);
		HERB_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyHerbTime", 15) * 1000;
		ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyItemTime", 600) * 1000;
		EQUIPABLE_ITEM_AUTO_DESTROY_TIME = server.getProperty("AutoDestroyEquipableItemTime", 0) * 1000;
		SPECIAL_ITEM_DESTROY_TIME = new HashMap<>();
		String[] data = server.getProperty("AutoDestroySpecialItemTime", (String[]) null, ",");
		if (data != null)
		{
			for (String itemData : data)
			{
				String[] item = itemData.split("-");
				SPECIAL_ITEM_DESTROY_TIME.put(Integer.parseInt(item[0]), Integer.parseInt(item[1]) * 1000);
			}
		}
		PLAYER_DROPPED_ITEM_MULTIPLIER = server.getProperty("PlayerDroppedItemMultiplier", 1);
		
		RATE_XP = server.getProperty("RateXp", 1.);
		RATE_SP = server.getProperty("RateSp", 1.);
		RATE_PARTY_XP = server.getProperty("RatePartyXp", 1.);
		RATE_PARTY_SP = server.getProperty("RatePartySp", 1.);
		RATE_DROP_ADENA = server.getProperty("RateDropAdena", 1.);
		RATE_DROP_ITEMS = server.getProperty("RateDropItems", 1.);
		RATE_DROP_ITEMS_BY_RAID = server.getProperty("RateRaidDropItems", 1.);
		RATE_DROP_SPOIL = server.getProperty("RateDropSpoil", 1.);
		RATE_DROP_MANOR = server.getProperty("RateDropManor", 1);
		RATE_QUEST_DROP = server.getProperty("RateQuestDrop", 1.);
		RATE_QUEST_REWARD = server.getProperty("RateQuestReward", 1.);
		RATE_QUEST_REWARD_XP = server.getProperty("RateQuestRewardXP", 1.);
		RATE_QUEST_REWARD_SP = server.getProperty("RateQuestRewardSP", 1.);
		RATE_QUEST_REWARD_ADENA = server.getProperty("RateQuestRewardAdena", 1.);
		RATE_KARMA_EXP_LOST = server.getProperty("RateKarmaExpLost", 1.);
		RATE_SIEGE_GUARDS_PRICE = server.getProperty("RateSiegeGuardsPrice", 1.);
		RATE_DROP_COMMON_HERBS = server.getProperty("RateCommonHerbs", 1.);
		RATE_DROP_HP_HERBS = server.getProperty("RateHpHerbs", 1.);
		RATE_DROP_MP_HERBS = server.getProperty("RateMpHerbs", 1.);
		RATE_DROP_SPECIAL_HERBS = server.getProperty("RateSpecialHerbs", 1.);
		PLAYER_DROP_LIMIT = server.getProperty("PlayerDropLimit", 3);
		PLAYER_RATE_DROP = server.getProperty("PlayerRateDrop", 5);
		PLAYER_RATE_DROP_ITEM = server.getProperty("PlayerRateDropItem", 70);
		PLAYER_RATE_DROP_EQUIP = server.getProperty("PlayerRateDropEquip", 25);
		PLAYER_RATE_DROP_EQUIP_WEAPON = server.getProperty("PlayerRateDropEquipWeapon", 5);
		PET_XP_RATE = server.getProperty("PetXpRate", 1.);
		PET_FOOD_RATE = server.getProperty("PetFoodRate", 1);
		SINEATER_XP_RATE = server.getProperty("SinEaterXpRate", 1.);
		KARMA_DROP_LIMIT = server.getProperty("KarmaDropLimit", 10);
		KARMA_RATE_DROP = server.getProperty("KarmaRateDrop", 70);
		KARMA_RATE_DROP_ITEM = server.getProperty("KarmaRateDropItem", 50);
		KARMA_RATE_DROP_EQUIP = server.getProperty("KarmaRateDropEquip", 40);
		KARMA_RATE_DROP_EQUIP_WEAPON = server.getProperty("KarmaRateDropEquipWeapon", 10);
		
		ALLOW_FREIGHT = server.getProperty("AllowFreight", true);
		ALLOW_WAREHOUSE = server.getProperty("AllowWarehouse", true);
		ALLOW_WEAR = server.getProperty("AllowWear", true);
		WEAR_DELAY = server.getProperty("WearDelay", 5);
		WEAR_PRICE = server.getProperty("WearPrice", 10);
		ALLOW_LOTTERY = server.getProperty("AllowLottery", true);
		ALLOW_WATER = server.getProperty("AllowWater", true);
		ALLOW_MANOR = server.getProperty("AllowManor", true);
		ALLOW_BOAT = server.getProperty("AllowBoat", true);
		ALLOW_CURSED_WEAPONS = server.getProperty("AllowCursedWeapons", true);
		
		ENABLE_FALLING_DAMAGE = server.getProperty("EnableFallingDamage", true);
		
		NO_SPAWNS = server.getProperty("NoSpawns", false);
		DEVELOPER = server.getProperty("Developer", false);
		PACKET_HANDLER_DEBUG = server.getProperty("PacketHandlerDebug", false);
		
		DEADLOCK_DETECTOR = server.getProperty("DeadLockDetector", false);
		DEADLOCK_CHECK_INTERVAL = server.getProperty("DeadLockCheckInterval", 20);
		RESTART_ON_DEADLOCK = server.getProperty("RestartOnDeadlock", false);
		
		LOG_CHAT = server.getProperty("LogChat", false);
		LOG_ITEMS = server.getProperty("LogItems", false);
		GMAUDIT = server.getProperty("GMAudit", false);
		
		ENABLE_COMMUNITY_BOARD = server.getProperty("EnableCommunityBoard", false);
		BBS_DEFAULT = server.getProperty("BBSDefault", "_bbshome");
		
		ROLL_DICE_TIME = server.getProperty("RollDiceTime", 4200);
		HERO_VOICE_TIME = server.getProperty("HeroVoiceTime", 10000);
		SUBCLASS_TIME = server.getProperty("SubclassTime", 2000);
		DROP_ITEM_TIME = server.getProperty("DropItemTime", 1000);
		SERVER_BYPASS_TIME = server.getProperty("ServerBypassTime", 100);
		MULTISELL_TIME = server.getProperty("MultisellTime", 100);
		MANUFACTURE_TIME = server.getProperty("ManufactureTime", 300);
		MANOR_TIME = server.getProperty("ManorTime", 3000);
		SENDMAIL_TIME = server.getProperty("SendMailTime", 10000);
		CHARACTER_SELECT_TIME = server.getProperty("CharacterSelectTime", 3000);
		GLOBAL_CHAT_TIME = server.getProperty("GlobalChatTime", 0);
		TRADE_CHAT_TIME = server.getProperty("TradeChatTime", 0);
		SOCIAL_TIME = server.getProperty("SocialTime", 2000);
		MOVE_TIME = server.getProperty("MoveTime", 100);
		
		SCHEDULED_THREAD_POOL_COUNT = server.getProperty("ScheduledThreadPoolCount", -1);
		THREADS_PER_SCHEDULED_THREAD_POOL = server.getProperty("ThreadsPerScheduledThreadPool", 4);
		INSTANT_THREAD_POOL_COUNT = server.getProperty("InstantThreadPoolCount", -1);
		THREADS_PER_INSTANT_THREAD_POOL = server.getProperty("ThreadsPerInstantThreadPool", 2);
		
		L2WALKER_PROTECTION = server.getProperty("L2WalkerProtection", false);
		ZONE_TOWN = server.getProperty("ZoneTown", 0);
		SERVER_NEWS = server.getProperty("ShowServerNews", false);
	}
	
	/**
	 * Loads loginserver settings.<br>
	 * IP addresses, database, account, misc.
	 */
	private static final void loadLogin()
	{
		final ExProperties server = initProperties(LOGINSERVER_FILE);
		
		HOSTNAME = server.getProperty("Hostname", "localhost");
		LOGINSERVER_HOSTNAME = server.getProperty("LoginserverHostname", "*");
		LOGINSERVER_PORT = server.getProperty("LoginserverPort", 2106);
		GAMESERVER_LOGIN_HOSTNAME = server.getProperty("LoginHostname", "*");
		GAMESERVER_LOGIN_PORT = server.getProperty("LoginPort", 9014);
		LOGIN_TRY_BEFORE_BAN = server.getProperty("LoginTryBeforeBan", 3);
		LOGIN_BLOCK_AFTER_BAN = server.getProperty("LoginBlockAfterBan", 600);
		ACCEPT_NEW_GAMESERVER = server.getProperty("AcceptNewGameServer", false);
		SHOW_LICENCE = server.getProperty("ShowLicence", true);
		
		DATABASE_URL = server.getProperty("URL", "jdbc:mariadb://localhost/acis");
		DATABASE_LOGIN = server.getProperty("Login", "root");
		DATABASE_PASSWORD = server.getProperty("Password", "");
		DATABASE_MAX_CONNECTIONS = server.getProperty("MaximumDbConnections", 5);
		
		AUTO_CREATE_ACCOUNTS = server.getProperty("AutoCreateAccounts", true);
		
		FLOOD_PROTECTION = server.getProperty("EnableFloodProtection", true);
		FAST_CONNECTION_LIMIT = server.getProperty("FastConnectionLimit", 15);
		NORMAL_CONNECTION_TIME = server.getProperty("NormalConnectionTime", 700);
		FAST_CONNECTION_TIME = server.getProperty("FastConnectionTime", 350);
		MAX_CONNECTION_PER_IP = server.getProperty("MaxConnectionPerIP", 50);
	}
	
	public static final void loadGameServer()
	{
		LOGGER.info("Loading gameserver configuration files.");
		
		// clans settings
		loadClans();
		
		// events settings
		loadEvents();
		
		// geoengine settings
		loadGeoengine();
		
		// hexID
		loadHexID();
		
		// NPCs/monsters settings
		loadNpcs();
		
		// players settings
		loadPlayers();
		
		// siege settings
		loadSieges();
		
		//my_news
		load_News();
		
		// server settings
		loadServer();
		
		// tournament settings
		loadTournament();
		
		// custom settings
		loadCustom();
		
		// c1c0s auto vote reward system
		loadAutoReward();
		
		// c1c0s vote manager
		loadVoteManagerAPI();
		
		// Dungeon settings
		loadDungeon();
		
		loadPremiumSystem();
	}
	
	public static final void loadLoginServer()
	{
		LOGGER.info("Loading loginserver configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadAccountManager()
	{
		LOGGER.info("Loading account manager configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final void loadGameServerRegistration()
	{
		LOGGER.info("Loading gameserver registration configuration files.");
		
		// login settings
		loadLogin();
	}
	
	public static final class ClassMasterSettings
	{
		private final Map<Integer, Boolean> _allowedClassChange;
		private final Map<Integer, List<IntIntHolder>> _claimItems;
		private final Map<Integer, List<IntIntHolder>> _rewardItems;
		
		public ClassMasterSettings(String configLine)
		{
			_allowedClassChange = new HashMap<>(3);
			_claimItems = new HashMap<>(3);
			_rewardItems = new HashMap<>(3);
			
			if (configLine != null)
				parseConfigLine(configLine.trim());
		}
		
		private void parseConfigLine(String configLine)
		{
			StringTokenizer st = new StringTokenizer(configLine, ";");
			while (st.hasMoreTokens())
			{
				// Get allowed class change.
				int job = Integer.parseInt(st.nextToken());
				
				_allowedClassChange.put(job, true);
				
				List<IntIntHolder> items = new ArrayList<>();
				
				// Parse items needed for class change.
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				// Feed the map, and clean the list.
				_claimItems.put(job, items);
				items = new ArrayList<>();
				
				// Parse gifts after class change.
				if (st.hasMoreTokens())
				{
					StringTokenizer st2 = new StringTokenizer(st.nextToken(), "[],");
					while (st2.hasMoreTokens())
					{
						StringTokenizer st3 = new StringTokenizer(st2.nextToken(), "()");
						items.add(new IntIntHolder(Integer.parseInt(st3.nextToken()), Integer.parseInt(st3.nextToken())));
					}
				}
				
				_rewardItems.put(job, items);
			}
		}
		
		public boolean isAllowed(int job)
		{
			if (_allowedClassChange == null)
				return false;
			
			if (_allowedClassChange.containsKey(job))
				return _allowedClassChange.get(job);
			
			return false;
		}
		
		public List<IntIntHolder> getRewardItems(int job)
		{
			return _rewardItems.get(job);
		}
		
		public List<IntIntHolder> getRequiredItems(int job)
		{
			return _claimItems.get(job);
		}
	}
}