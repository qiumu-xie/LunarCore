package emu.lunarcore;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.SerializedName;

import emu.lunarcore.data.common.ItemParam;
import lombok.Getter;

@Getter
public class Config {
    public DatabaseInfo accountDatabase = new DatabaseInfo();
    public DatabaseInfo gameDatabase = new DatabaseInfo();
    public InternalMongoInfo internalMongoServer = new InternalMongoInfo();
    public boolean useSameDatabase = true;

    public KeystoreInfo keystore = new KeystoreInfo();

    public HttpServerConfig httpServer = new HttpServerConfig(80);
    public GameServerConfig gameServer = new GameServerConfig(23301);
    
    public ServerOptions serverOptions = new ServerOptions();
    public ServerTime serverTime = new ServerTime();
    public ServerRates serverRates = new ServerRates();
    public LogOptions logOptions = new LogOptions();

    public String resourceDir = "./resources";
    public String dataDir = "./data";

    @Getter
    public static class DatabaseInfo {
        public String uri = "mongodb://localhost:27017";
        public String collection = "lunarcore";
        public boolean useInternal = false;
    }

    @Getter
    public static class InternalMongoInfo {
        public String address = "localhost";
        public int port = 27017;
        public String filePath = "database.mv";
    }

    @Getter
    public static class KeystoreInfo {
        public String path = "./keystore.p12";
        public String password = "";
    }

    @Getter
    private static class ServerConfig {
        public String bindAddress = "0.0.0.0";
        @SerializedName(value = "bindPort", alternate = {"port"})
        public int bindPort;
        
        // Will return bindAddress if publicAddress is null
        public String publicAddress = "127.0.0.1";
        // Will return bindPort if publicPort is null
        public Integer publicPort;
        
        public ServerConfig(int port) {
            this.bindPort = port;
        }
        
        public String getPublicAddress() {
            if (this.publicAddress != null && !this.publicAddress.isEmpty()) {
                return this.publicAddress;
            }
            
            return this.bindAddress;
        }
        
        public int getPublicPort() {
            if (this.publicPort != null && this.publicPort != 0) {
                return this.publicPort;
            }
            
            return this.bindPort;
        }
    }
    
    @Getter
    public static class HttpServerConfig extends ServerConfig {
        public boolean useSSL = false;
        public long regionListRefresh = 60_000; // Time in milliseconds to wait before refreshing region list cache again

        public HttpServerConfig(int port) {
            super(port);
        }
        
        public String getDisplayAddress() {
            return (useSSL ? "https" : "http") + "://" + getPublicAddress() + ":" + getPublicPort();
        }
    }

    @Getter
    public static class GameServerConfig extends ServerConfig {
        public String id = "lunar_rail";
        public String name = "火萤";
        public String description = "火萤服";
        public int kcpInterval = 40;
        public Integer kcpTimeout = 30;

        public GameServerConfig(int port) {
            super(port);
        }
        
        public int getKcpTimeout() {
            return kcpTimeout.intValue();
        }
    }
    
    @Getter 
    public static class ServerTime {
        public boolean spoofTime = false;
        public Date spoofDate = new Date(1705276800000L); // January 15, 2024 12:00:00 AM (GMT)
    }
    
    @Getter
    public static class ServerOptions {
        public boolean autoCreateAccount = true;
        public int sceneMaxEntites = 500;
        public int maxCustomRelicLevel = 15; // 玩家可以使用 /give 命令
        public boolean unlockAllChallenges = true;
        public boolean spendStamina = true;
        public int staminaRecoveryRate = 5 * 60;
        public int staminaReserveRecoveryRate = 18 * 60;
        public int startTrailblazerLevel = 70; // 为新玩家开始开拓者级别
        public boolean autoUpgradeWorldLevel = true; // 当玩家达到一定 TB 等级时自动升级世界等级
//        public String language = "EN";
        public String language = "CHS";
        public Set<String> defaultPermissions = Set.of("*");
        public int maxPlayers = -1;
        public ServerProfile serverFriendInfo = new ServerProfile();
        public WelcomeMail welcomeMail = new WelcomeMail();
        
        public int getStaminaRecoveryRate() {
            return staminaRecoveryRate > 0 ? staminaRecoveryRate : 1;
        }
        
        public int getStaminaReserveRecoveryRate() {
            return staminaReserveRecoveryRate > 0 ? staminaReserveRecoveryRate : 1;
        }
    }
    
    @Getter
    public static class ServerRates {
        public double exp = 1.0;
        public double credit = 1.0;
        public double jade = 1.0;
        public double material = 1.0;
        public double equip = 1.0;
    }
    
    @Getter
    public static class ServerProfile {
        public String name = "流萤本萤";
        public String signature = "飞萤扑火，向死而生。";
        public int level = 520;
        public int headIcon = 200123;
        public int chatBubbleId = 0;
        public List<ServerDisplayAvatar> displayAvatars = List.of(new ServerDisplayAvatar(1310, 520));
        
        @Getter
        public static class ServerDisplayAvatar {
            public int avatarId;
            public int level;
            
            public ServerDisplayAvatar(int avatarId, int level) {
                this.avatarId = avatarId;
                this.level = level;
            }
        }
    }
    
    @Getter
    public static class WelcomeMail {
        public String title;
        public String sender;
        public String content;
        public List<ItemParam> attachments;
        
        public WelcomeMail() {
            this.title = "初次见面";
            this.sender = "流萤本萤";
            this.content = "嗨，又见面啦…我的意思，很高兴见到你。和往常一样，叫我「流萤」吧。";
            this.attachments = List.of(
                new ItemParam(1, 5201314),
                new ItemParam(101, 1314),
                new ItemParam(102, 1314),
                new ItemParam(1310, 1),
                new ItemParam(11310, 6),
                new ItemParam(23025, 1),
                new ItemParam(200123, 1),
                new ItemParam(201310, 1)
            );
        }
    }
    
    @Getter
    public static class LogOptions {
        public boolean commands = true;
        public boolean connections = true;
        public boolean packets = false;
        public boolean filterLoopingPackets = false;
    }

    public void validate() {
        if (this.gameServer.kcpTimeout == null) {
            this.gameServer.kcpTimeout = 30;
        }
    }

}
