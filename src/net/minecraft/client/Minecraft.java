package net.minecraft.client;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.modules.combat.AutoClicker;
import net.ccbluex.liquidbounce.features.module.modules.exploit.AbortBreaking;
import net.ccbluex.liquidbounce.features.module.modules.exploit.MultiActions;
import net.ccbluex.liquidbounce.features.module.modules.world.FastPlace;
import net.ccbluex.liquidbounce.ui.client.GuiUpdate;
import net.ccbluex.liquidbounce.ui.client.GuiWelcome;
import net.ccbluex.liquidbounce.utils.CPSCounter;
import net.ccbluex.liquidbounce.utils.render.IconUtils;
import net.ccbluex.liquidbounce.utils.render.MiniMapRegister;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.stream.GuiStreamUnavailable;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.stream.IStream;
import net.minecraft.client.stream.NullStream;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class Minecraft implements IThreadListener, IPlayerUsage
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean isRunningOnMac = Util.getOSType() == Util.EnumOS.OSX;

    /**
     * A 10MiB preallocation to ensure the heap is reasonably sized.
     */
    public static byte[] memoryReserve = new byte[10485760];
    private static final List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode[] {new DisplayMode(2560, 1600), new DisplayMode(2880, 1800)});
    private final File fileResourcepacks;
    private final PropertyMap twitchDetails;

    /**
     * The player's GameProfile properties
     */
    private final PropertyMap profileProperties;
    private ServerData currentServerData;
    private TextureManager renderEngine;
    private static Minecraft theMinecraft;
    public PlayerControllerMP playerController;
    private boolean fullscreen;
    private boolean enableGLErrorChecking = true;
    private boolean hasCrashed;
    private CrashReport crashReporter;
    public int displayWidth;
    public int displayHeight;
    private boolean connectedToRealms = false;
    public Timer timer = new Timer(20.0F);
    private PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getCurrentTimeMillis());
    public WorldClient theWorld;
    public RenderGlobal renderGlobal;
    private RenderManager renderManager;
    private RenderItem renderItem;
    private ItemRenderer itemRenderer;
    public EntityPlayerSP thePlayer;
    private Entity renderViewEntity;
    public Entity pointedEntity;
    public EffectRenderer effectRenderer;
    public Session session;
    private boolean isGamePaused;
    public FontRenderer fontRendererObj;
    public FontRenderer standardGalacticFontRenderer;
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;
    private int leftClickCounter;
    private int tempDisplayWidth;
    private int tempDisplayHeight;
    private IntegratedServer theIntegratedServer;
    public GuiAchievement guiAchievement;
    public GuiIngame ingameGUI;
    public boolean skipRenderWorld;
    public MovingObjectPosition objectMouseOver;
    public GameSettings gameSettings;
    public MouseHelper mouseHelper;
    public final File mcDataDir;
    private final File fileAssets;
    private final String launchedVersion;
    private final Proxy proxy;
    private ISaveFormat saveLoader;
    public static int debugFPS;
    public int rightClickDelayTimer;
    private String serverName;
    private int serverPort;
    public boolean inGameHasFocus;
    long systemTime = getSystemTime();
    private int joinPlayerCounter;
    public final FrameTimer frameTimer = new FrameTimer();
    long startNanoTime = System.nanoTime();
    private final boolean jvm64bit;
    private final boolean isDemo;
    private NetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;
    public final Profiler mcProfiler = new Profiler();
    private long debugCrashKeyPressTime = -1L;
    private IReloadableResourceManager mcResourceManager;
    private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
    private final List<IResourcePack> defaultResourcePacks = Lists.<IResourcePack>newArrayList();
    private final DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    private LanguageManager mcLanguageManager;
    private IStream stream;
    private Framebuffer framebufferMc;
    private TextureMap textureMapBlocks;
    private SoundHandler mcSoundHandler;
    private MusicTicker mcMusicTicker;
    private ResourceLocation mojangLogo;
    private final MinecraftSessionService sessionService;
    private SkinManager skinManager;
    private final Queue < FutureTask<? >> scheduledTasks = Queues. < FutureTask<? >> newArrayDeque();
    private long field_175615_aJ = 0L;
    private final Thread mcThread = Thread.currentThread();
    private ModelManager modelManager;
    private BlockRendererDispatcher blockRenderDispatcher;
    volatile boolean running = true;
    public String debug = "";
    public boolean field_175613_B = false;
    public boolean field_175614_C = false;
    public boolean field_175611_D = false;
    public boolean renderChunksMany = true;
    long debugUpdateTime = getSystemTime();
    int fpsCounter;
    long prevFrameTime = -1L;
    private String debugProfilerName = "root";

    public Minecraft(GameConfiguration gameConfig)
    {
        theMinecraft = this;
        this.mcDataDir = gameConfig.folderInfo.mcDataDir;
        this.fileAssets = gameConfig.folderInfo.assetsDir;
        this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
        this.launchedVersion = gameConfig.gameInfo.version;
        this.twitchDetails = gameConfig.userInfo.userProperties;
        this.profileProperties = gameConfig.userInfo.profileProperties;
        this.mcDefaultResourcePack = new DefaultResourcePack((new ResourceIndex(gameConfig.folderInfo.assetsDir, gameConfig.folderInfo.assetIndex)).getResourceMap());
        this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
        this.sessionService = (new YggdrasilAuthenticationService(gameConfig.userInfo.proxy, UUID.randomUUID().toString())).createMinecraftSessionService();
        this.session = gameConfig.userInfo.session;
        logger.info("Setting user: " + this.session.getUsername());
        logger.info("(Session ID is " + this.session.getSessionID() + ")");
        this.isDemo = gameConfig.gameInfo.isDemo;
        this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
        this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
        this.tempDisplayWidth = gameConfig.displayInfo.width;
        this.tempDisplayHeight = gameConfig.displayInfo.height;
        this.fullscreen = gameConfig.displayInfo.fullscreen;
        this.jvm64bit = isJvm64bit();
        this.theIntegratedServer = new IntegratedServer(this);

        if (gameConfig.serverInfo.serverName != null)
        {
            this.serverName = gameConfig.serverInfo.serverName;
            this.serverPort = gameConfig.serverInfo.serverPort;
        }

        ImageIO.setUseCache(false);
        Bootstrap.register();
    }

    public void run()
    {
        //改变一开始的分辨率
        displayWidth = Math.max(1067, displayWidth);
        displayHeight = Math.max(622, displayHeight);

        this.running = true;

        try
        {
            this.startGame();
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
            crashreport.makeCategory("Initialization");
            this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashreport));
            return;
        }

        while (true)
        {
            try
            {
                while (this.running)
                {
                    if (!this.hasCrashed || this.crashReporter == null)
                    {
                        try {
                            this.runGameLoop();
                        } catch (OutOfMemoryError var10)
                        {
                            this.freeMemory();
                            this.displayGuiScreen(new GuiMemoryErrorScreen());
                            System.gc();
                        }
                    } else {
                        this.displayCrashReport(this.crashReporter);
                    }
                }
            }
            catch (MinecraftError var12) {
                break;
            }
            catch (ReportedException reportedexception)
            {
                this.addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
                this.freeMemory();
                logger.fatal((String)"Reported exception thrown!", (Throwable)reportedexception);
                this.displayCrashReport(reportedexception.getCrashReport());
                break;
            }
            catch (Throwable throwable1)
            {
                CrashReport crashreport1 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable1));
                this.freeMemory();
                logger.fatal("Unreported exception thrown!", throwable1);
                this.displayCrashReport(crashreport1);
                break;
            }
            finally
            {
                this.shutdownMinecraftApplet();
            }

            return;
        }
    }

    private void startGame() throws LWJGLException, IOException
    {
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
        this.startTimerHackThread();

        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0)
        {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }

        logger.info("LWJGL Version: " + Sys.getVersion());
        this.setWindowIcon();
        this.setInitialDisplayMode();
        this.createDisplay();
        OpenGlHelper.initializeTextures();
        this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
        this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.registerMetadataSerializers();
        this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        this.refreshResources();
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.drawSplashScreen(this.renderEngine);
        this.initStream();
        this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
        this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        this.mcMusicTicker = new MusicTicker(this);
        this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);

        if (this.gameSettings.language != null)
        {
            this.fontRendererObj.setUnicodeFlag(this.isUnicode());
            this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRendererObj);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat() {
            public String formatString(String str) {
                try {
                    return String.format(str, new Object[] {GameSettings.getKeyDisplayString(Minecraft.this.gameSettings.keyBindInventory.getKeyCode())});
                } catch (Exception exception)
                {
                    return "Error: " + exception.getLocalizedMessage();
                }
            }
        });
        this.mouseHelper = new MouseHelper();
        this.checkGLError("Pre startup");
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0D);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.cullFace(1029);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        this.checkGLError("Startup");
        this.textureMapBlocks = new TextureMap("textures");
        this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
        this.renderEngine.loadTickableTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);
        this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
        this.modelManager = new ModelManager(this.textureMapBlocks);
        this.mcResourceManager.registerReloadListener(this.modelManager);
        this.renderItem = new RenderItem(this.renderEngine, this.modelManager);
        this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
        this.itemRenderer = new ItemRenderer(this);
        this.mcResourceManager.registerReloadListener(this.renderItem);
        this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.entityRenderer);
        this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
        this.renderGlobal = new RenderGlobal(this);
        this.mcResourceManager.registerReloadListener(this.renderGlobal);
        this.guiAchievement = new GuiAchievement(this);
        GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);

        LiquidBounce.INSTANCE.startClient();  //加载水影

        //进入自定义主界面
        GuiScreen mainMenu = LiquidBounce.guiMain;
        if(LiquidBounce.fileManager.firstStart){
            mainMenu = new GuiWelcome();
        }else if(LiquidBounce.INSTANCE.getLatestVersion() > LiquidBounce.CLIENT_VERSION - (LiquidBounce.IN_DEV ? 1 : 0)){
            mainMenu = new GuiUpdate();
        }

        if (this.serverName != null)
        {
            this.displayGuiScreen(new GuiConnecting(mainMenu, this, this.serverName, this.serverPort));
        }
        else
        {
            this.displayGuiScreen(mainMenu);
        }

        this.renderEngine.deleteTexture(this.mojangLogo);
        this.mojangLogo = null;
        this.loadingScreen = new LoadingScreenRenderer(this);

        if (this.gameSettings.fullScreen && !this.fullscreen)
        {
            this.toggleFullscreen();
        }

        try {
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
        } catch (OpenGLException var2)
        {
            this.gameSettings.enableVsync = false;
            this.gameSettings.saveOptions();
        }

        this.renderGlobal.makeEntityOutlineShader();
    }

    private void registerMetadataSerializers()
    {
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
    }

    private void initStream()
    {
        try
        {
            this.stream = new TwitchStream(this, (Property)Iterables.getFirst(this.twitchDetails.get("twitch_access_token"), null));
        }
        catch (Throwable throwable)
        {
            this.stream = new NullStream(throwable);
            logger.error("Couldn\'t initialize twitch stream");
        }
    }

    private void createDisplay() throws LWJGLException
    {
        Display.setResizable(true);
        Display.setTitle(LiquidBounce.CLIENT_NAME+" "+ LiquidBounce.CLIENT_VERSION + " | " + LiquidBounce.MINECRAFT_VERSION + (LiquidBounce.IN_DEV ? " | DEVELOPMENT BUILD" : "MADE BY YUXIANGLL")); //自定义标题

        try {
            Display.create((new PixelFormat()).withDepthBits(24));
        }
        catch (LWJGLException lwjglexception) {
            logger.error((String)"Couldn\'t set pixel format", (Throwable)lwjglexception);

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException var3) {
                ;
            }

            if (this.fullscreen)
            {
                this.updateDisplayMode();
            }

            Display.create();
        }
    }

    private void setInitialDisplayMode() throws LWJGLException
    {
        if (this.fullscreen)
        {
            Display.setFullscreen(true);
            DisplayMode displaymode = Display.getDisplayMode();
            this.displayWidth = Math.max(1, displaymode.getWidth());
            this.displayHeight = Math.max(1, displaymode.getHeight());
        } else {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }
    }

    private void setWindowIcon() // Mac无法使用这个方法设置
    {
        if(Util.getOSType() != Util.EnumOS.OSX) {
            final ByteBuffer[] liquidBounceFavicon = IconUtils.getFavicon();
            if(liquidBounceFavicon != null) {
                Display.setIcon(liquidBounceFavicon);
                return; // 设置完水影的直接返回，设置失败再用原版的
            }else{
                logger.info("LiquidBounce icon is null, skipping setting it");
            }
        }



         Util.EnumOS util$enumos = Util.getOSType();

        if (util$enumos != Util.EnumOS.OSX) {
            InputStream inputstream = null;
            InputStream inputstream1 = null;

            try {
                inputstream = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
                inputstream1 = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));

                if (inputstream != null && inputstream1 != null) {
                    Display.setIcon(new ByteBuffer[] {this.readImageToBuffer(inputstream), this.readImageToBuffer(inputstream1)});
                }
            } catch (IOException ioexception) {
                logger.error((String)"Couldn\'t set icon", (Throwable)ioexception);
            } finally {
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(inputstream1);
            }
        }
    }

    private static boolean isJvm64bit() //判断是否是64位Jvm
    {
        String[] astring = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

        for (String s : astring) {
            String s1 = System.getProperty(s);

            if (s1 != null && s1.contains("64"))
            {
                return true;
            }
        }

        return false;
    }

    public Framebuffer getFramebuffer()
    {
        return this.framebufferMc;
    }

    public String getVersion()
    {
        return this.launchedVersion;
    }

    private void startTimerHackThread() {
        Thread thread = new Thread("Timer hack thread") {
            public void run() {
                while (Minecraft.this.running) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException var2) {
                        ;
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void crashed(CrashReport crash)
    {
        this.hasCrashed = true;
        this.crashReporter = crash;
    }

    public void displayCrashReport(CrashReport crashReportIn)
    {
        File file1 = new File(getMinecraft().mcDataDir, "crash-reports");
        File file2 = new File(file1, "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-client.txt");
        Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());

        if (crashReportIn.getFile() != null)
        {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
            System.exit(-1);
        }
        else if (crashReportIn.saveToFile(file2))
        {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            System.exit(-1);
        }
        else
        {
            Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public boolean isUnicode()
    {
        return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
    }

    public void refreshResources()
    {
        List<IResourcePack> list = Lists.newArrayList(this.defaultResourcePacks);

        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries()) {
            list.add(resourcepackrepository$entry.getResourcePack());
        }

        if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
            list.add(this.mcResourcePackRepository.getResourcePackInstance());
        }

        try {
            this.mcResourceManager.reloadResources(list);
        } catch (RuntimeException runtimeexception) {
            logger.info((String)"Caught error stitching, removing all assigned resourcepacks", (Throwable)runtimeexception);
            list.clear();
            list.addAll(this.defaultResourcePacks);
            this.mcResourcePackRepository.setRepositories(Collections.<ResourcePackRepository.Entry>emptyList());
            this.mcResourceManager.reloadResources(list);
            this.gameSettings.resourcePacks.clear();
            this.gameSettings.incompatibleResourcePacks.clear();
            this.gameSettings.saveOptions();
        }

        this.mcLanguageManager.parseLanguageMetadata(list);

        if (this.renderGlobal != null)
        {
            this.renderGlobal.loadRenderers();
        }
    }

    private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException
    {
        BufferedImage bufferedimage = ImageIO.read(imageStream);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), (int[])null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

        for (int i : aint) {
            bytebuffer.putInt(i << 8 | i >> 24 & 255);
        }

        bytebuffer.flip();
        return bytebuffer;
    }

    private void updateDisplayMode() throws LWJGLException
    {
        Set<DisplayMode> set = Sets.<DisplayMode>newHashSet();
        Collections.addAll(set, Display.getAvailableDisplayModes());
        DisplayMode displaymode = Display.getDesktopDisplayMode();

        if (!set.contains(displaymode) && Util.getOSType() == Util.EnumOS.OSX)
        {
            label53:

            for (DisplayMode displaymode1 : macDisplayModes)
            {
                boolean flag = true;

                for (DisplayMode displaymode2 : set)
                {
                    if (displaymode2.getBitsPerPixel() == 32 && displaymode2.getWidth() == displaymode1.getWidth() && displaymode2.getHeight() == displaymode1.getHeight())
                    {
                        flag = false;
                        break;
                    }
                }

                if (!flag)
                {
                    Iterator iterator = set.iterator();
                    DisplayMode displaymode3;

                    while (true)
                    {
                        if (!iterator.hasNext())
                        {
                            continue label53;
                        }

                        displaymode3 = (DisplayMode)iterator.next();

                        if (displaymode3.getBitsPerPixel() == 32 && displaymode3.getWidth() == displaymode1.getWidth() / 2 && displaymode3.getHeight() == displaymode1.getHeight() / 2)
                        {
                            break;
                        }
                    }

                    displaymode = displaymode3;
                }
            }
        }

        Display.setDisplayMode(displaymode);
        this.displayWidth = displaymode.getWidth();
        this.displayHeight = displaymode.getHeight();
    }

    private void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException
    {//显示加载动画(界面)
        ScaledResolution scaledresolution = new ScaledResolution(this);
        int i = scaledresolution.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i, true);
        framebuffer.bindFramebuffer(false);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double)scaledresolution.getScaledWidth(), (double)scaledresolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        InputStream inputstream = null;

        try
        {
            inputstream = this.mcDefaultResourcePack.getInputStream(locationMojangPng);
            this.mojangLogo = textureManagerInstance.getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(inputstream)));
            textureManagerInstance.bindTexture(this.mojangLogo);
        }
        catch (IOException ioexception)
        {
            logger.error((String)("Unable to load logo: " + locationMojangPng), (Throwable)ioexception);
        }
        finally
        {
            IOUtils.closeQuietly(inputstream);
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(0.0D, (double)this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos((double)this.displayWidth, (double)this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos((double)this.displayWidth, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int j = 256;
        int k = 256;
        this.draw((scaledresolution.getScaledWidth() - j) / 2, (scaledresolution.getScaledHeight() - k) / 2, 0, 0, j, k, 255, 255, 255, 255);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledresolution.getScaledWidth() * i, scaledresolution.getScaledHeight() * i);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        this.updateDisplay();
    }

    /**
     * Draw with the WorldRenderer
     *
     * @param posX   X position for the render
     * @param posY   Y position for the render
     * @param texU   X position for the texture
     * @param texV   Y position for the texture
     * @param width  Width of the render
     * @param height Height of the render
     * @param red    The red component of the render's color
     * @param green  The green component of the render's color
     * @param blue   The blue component of the render's color
     * @param alpha  The alpha component of the render's color
     */
    public void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue, int alpha)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos((double)posX, (double)(posY + height), 0.0D).tex((double)((float)texU * f), (double)((float)(texV + height) * f1)).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos((double)(posX + width), (double)(posY + height), 0.0D).tex((double)((float)(texU + width) * f), (double)((float)(texV + height) * f1)).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos((double)(posX + width), (double)posY, 0.0D).tex((double)((float)(texU + width) * f), (double)((float)texV * f1)).color(red, green, blue, alpha).endVertex();
        worldrenderer.pos((double)posX, (double)posY, 0.0D).tex((double)((float)texU * f), (double)((float)texV * f1)).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    public ISaveFormat getSaveLoader()
    {
        return this.saveLoader;
    }

    public void displayGuiScreen(GuiScreen guiScreenIn) {
        if (this.currentScreen != null) {
            this.currentScreen.onGuiClosed();
        }

        if (guiScreenIn == null && this.theWorld == null) {
            guiScreenIn = LiquidBounce.guiMain;
        } else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0F) {
            guiScreenIn = new GuiGameOver();
        }

        if (guiScreenIn instanceof GuiMainMenu) {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages();
        }

        if(currentScreen instanceof net.minecraft.client.gui.GuiMainMenu || (currentScreen != null && currentScreen.getClass().getName().startsWith("net.labymod") && currentScreen.getClass().getSimpleName().equals("ModGuiMainMenu"))) {
            guiScreenIn = LiquidBounce.guiMain;

            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            guiScreenIn.setWorldAndResolution(Minecraft.getMinecraft(), scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
            skipRenderWorld = false;
        }

        LiquidBounce.eventManager.callEvent(new ScreenEvent(currentScreen));

        this.currentScreen = (GuiScreen)guiScreenIn;

        if (guiScreenIn != null)
        {
            this.setIngameNotInFocus();
            ScaledResolution scaledresolution = new ScaledResolution(this);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            ((GuiScreen)guiScreenIn).setWorldAndResolution(this, i, j);
            this.skipRenderWorld = false;
        }
        else
        {
            this.mcSoundHandler.resumeSounds();
            this.setIngameFocus();
        }
    }

    /**
     * Checks for an OpenGL error. If there is one, prints the error ID and error string.
     */
    private void checkGLError(String message)
    {
        if (this.enableGLErrorChecking)
        {
            int i = GL11.glGetError();

            if (i != 0)
            {
                String s = GLU.gluErrorString(i);
                logger.error("########## GL ERROR ##########");
                logger.error("@ " + message);
                logger.error(i + ": " + s);
            }
        }
    }


    /**
     * Shuts down the minecraft applet by stopping the resource downloads, and clearing up GL stuff; called when the
     * application (or web page) is exited.
     */
    public void shutdownMinecraftApplet()
    {
        try
        {
            this.stream.shutdownStream();
            logger.info("Stopping!");

            try
            {
                this.loadWorld((WorldClient)null);
            }
            catch (Throwable var5)
            {
                ;
            }

            this.mcSoundHandler.unloadSounds();
        }
        finally
        {
            Display.destroy();

            if (!this.hasCrashed)
            {
                System.exit(0);
            }
        }

        System.gc();
    }

    private long lastFrame = getTime();

    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    /**
     * Called repeatedly from run()
     */
    private void runGameLoop() throws IOException
    {
        final long currentTime = getTime();
        final int deltaTime = (int) (currentTime - lastFrame);
        lastFrame = currentTime;

        RenderUtils.deltaTime = deltaTime;

        long i = System.nanoTime();
        this.mcProfiler.startSection("root");

        if (Display.isCreated() && Display.isCloseRequested()) {
            this.shutdown();
        }

        if (this.isGamePaused && this.theWorld != null) {
            float f = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = f;
        } else {
            this.timer.updateTimer();
        }

        this.mcProfiler.startSection("scheduledExecutables");

        synchronized (this.scheduledTasks) {
            while (!this.scheduledTasks.isEmpty()) {
                Util.runTask((FutureTask)this.scheduledTasks.poll(), logger);
            }
        }

        this.mcProfiler.endSection();
        long l = System.nanoTime();
        this.mcProfiler.startSection("tick");

        for (int j = 0; j < this.timer.elapsedTicks; ++j) {
            this.runTick();
        }

        this.mcProfiler.endStartSection("preRenderErrors");
        long i1 = System.nanoTime() - l;
        this.checkGLError("Pre render");
        this.mcProfiler.endStartSection("sound");
        this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GlStateManager.pushMatrix();
        GlStateManager.clear(16640);
        this.framebufferMc.bindFramebuffer(true);
        this.mcProfiler.startSection("display");
        GlStateManager.enableTexture2D();

        if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock())
        {
            this.gameSettings.thirdPersonView = 0;
        }

        this.mcProfiler.endSection();

        if (!this.skipRenderWorld)
        {
            this.mcProfiler.endStartSection("gameRenderer");
            this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks, i);
            this.mcProfiler.endSection();
        }

        this.mcProfiler.endSection();

        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI)
        {
            if (!this.mcProfiler.profilingEnabled)
            {
                this.mcProfiler.clearProfiling();
            }

            this.mcProfiler.profilingEnabled = true;
            this.displayDebugInfo(i1);
        }
        else
        {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }

        this.guiAchievement.updateAchievementWindow();
        this.framebufferMc.unbindFramebuffer();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.entityRenderer.renderStreamIndicator(this.timer.renderPartialTicks);
        GlStateManager.popMatrix();
        this.mcProfiler.startSection("root");
        this.updateDisplay();
        Thread.yield();
        this.mcProfiler.startSection("stream");
        this.mcProfiler.startSection("update");
        this.stream.func_152935_j();
        this.mcProfiler.endStartSection("submit");
        this.stream.func_152922_k();
        this.mcProfiler.endSection();
        this.mcProfiler.endSection();
        this.checkGLError("Post render");
        ++this.fpsCounter;
        this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
        long k = System.nanoTime();
        this.frameTimer.addFrame(k - this.startNanoTime);
        this.startNanoTime = k;

        while (getSystemTime() >= this.debugUpdateTime + 1000L)
        {
            debugFPS = this.fpsCounter;
            this.debug = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", new Object[] {Integer.valueOf(debugFPS), Integer.valueOf(RenderChunk.renderChunksUpdated), RenderChunk.renderChunksUpdated != 1 ? "s" : "", (float)this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : Integer.valueOf(this.gameSettings.limitFramerate), this.gameSettings.enableVsync ? " vsync" : "", this.gameSettings.fancyGraphics ? "" : " fast", this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"), OpenGlHelper.useVbo() ? " vbo" : ""});
            RenderChunk.renderChunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.usageSnooper.addMemoryStatsToSnooper();

            if (!this.usageSnooper.isSnooperRunning())
            {
                this.usageSnooper.startSnooper();
            }
        }

        if (this.isFramerateLimitBelowMax())
        {
            this.mcProfiler.startSection("fpslimit_wait");
            Display.sync(this.getLimitFramerate());
            this.mcProfiler.endSection();
        }

        this.mcProfiler.endSection();
    }

    public void updateDisplay()
    {
        this.mcProfiler.startSection("display_update");
        Display.update();
        this.mcProfiler.endSection();
        this.checkWindowResize();
    }

    protected void checkWindowResize()
    {
        if (!this.fullscreen && Display.wasResized())
        {
            int i = this.displayWidth;
            int j = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();

            if (this.displayWidth != i || this.displayHeight != j)
            {
                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }

                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }

    public int getLimitFramerate()
    {
        return this.theWorld == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
    }

    public boolean isFramerateLimitBelowMax()
    {
        return (float)this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }

    public void freeMemory()
    {
        try
        {
            memoryReserve = new byte[0];
            this.renderGlobal.deleteAllDisplayLists();
        }
        catch (Throwable var3)
        {
            ;
        }

        try
        {
            System.gc();
            this.loadWorld((WorldClient)null);
        }
        catch (Throwable var2)
        {
            ;
        }

        System.gc();
    }

    private void updateDebugProfilerName(int keyCount)
    {
        List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);

        if (list != null && !list.isEmpty())
        {
            Profiler.Result profiler$result = (Profiler.Result)list.remove(0);

            if (keyCount == 0)
            {
                if (profiler$result.field_76331_c.length() > 0)
                {
                    int i = this.debugProfilerName.lastIndexOf(".");

                    if (i >= 0)
                    {
                        this.debugProfilerName = this.debugProfilerName.substring(0, i);
                    }
                }
            }
            else
            {
                --keyCount;

                if (keyCount < list.size() && !((Profiler.Result)list.get(keyCount)).field_76331_c.equals("unspecified"))
                {
                    if (this.debugProfilerName.length() > 0)
                    {
                        this.debugProfilerName = this.debugProfilerName + ".";
                    }

                    this.debugProfilerName = this.debugProfilerName + ((Profiler.Result)list.get(keyCount)).field_76331_c;
                }
            }
        }
    }

    private void displayDebugInfo(long elapsedTicksTime)
    {
        if (this.mcProfiler.profilingEnabled)
        {
            List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
            Profiler.Result profiler$result = (Profiler.Result)list.remove(0);
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.enableColorMaterial();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, (double)this.displayWidth, (double)this.displayHeight, 0.0D, 1000.0D, 3000.0D);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -2000.0F);
            GL11.glLineWidth(1.0F);
            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            int i = 160;
            int j = this.displayWidth - i - 10;
            int k = this.displayHeight - i * 2;
            GlStateManager.enableBlend();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos((double)((float)j - (float)i * 1.1F), (double)((float)k - (float)i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
            worldrenderer.pos((double)((float)j - (float)i * 1.1F), (double)(k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
            worldrenderer.pos((double)((float)j + (float)i * 1.1F), (double)(k + i * 2), 0.0D).color(200, 0, 0, 0).endVertex();
            worldrenderer.pos((double)((float)j + (float)i * 1.1F), (double)((float)k - (float)i * 0.6F - 16.0F), 0.0D).color(200, 0, 0, 0).endVertex();
            tessellator.draw();
            GlStateManager.disableBlend();
            double d0 = 0.0D;

            for (int l = 0; l < list.size(); ++l)
            {
                Profiler.Result profiler$result1 = (Profiler.Result)list.get(l);
                int i1 = MathHelper.floor_double(profiler$result1.field_76332_a / 4.0D) + 1;
                worldrenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
                int j1 = profiler$result1.getColor();
                int k1 = j1 >> 16 & 255;
                int l1 = j1 >> 8 & 255;
                int i2 = j1 & 255;
                worldrenderer.pos((double)j, (double)k, 0.0D).color(k1, l1, i2, 255).endVertex();

                for (int j2 = i1; j2 >= 0; --j2)
                {
                    float f = (float)((d0 + profiler$result1.field_76332_a * (double)j2 / (double)i1) * Math.PI * 2.0D / 100.0D);
                    float f1 = MathHelper.sin(f) * (float)i;
                    float f2 = MathHelper.cos(f) * (float)i * 0.5F;
                    worldrenderer.pos((double)((float)j + f1), (double)((float)k - f2), 0.0D).color(k1, l1, i2, 255).endVertex();
                }

                tessellator.draw();
                worldrenderer.begin(5, DefaultVertexFormats.POSITION_COLOR);

                for (int i3 = i1; i3 >= 0; --i3)
                {
                    float f3 = (float)((d0 + profiler$result1.field_76332_a * (double)i3 / (double)i1) * Math.PI * 2.0D / 100.0D);
                    float f4 = MathHelper.sin(f3) * (float)i;
                    float f5 = MathHelper.cos(f3) * (float)i * 0.5F;
                    worldrenderer.pos((double)((float)j + f4), (double)((float)k - f5), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
                    worldrenderer.pos((double)((float)j + f4), (double)((float)k - f5 + 10.0F), 0.0D).color(k1 >> 1, l1 >> 1, i2 >> 1, 255).endVertex();
                }

                tessellator.draw();
                d0 += profiler$result1.field_76332_a;
            }

            DecimalFormat decimalformat = new DecimalFormat("##0.00");
            GlStateManager.enableTexture2D();
            String s = "";

            if (!profiler$result.field_76331_c.equals("unspecified"))
            {
                s = s + "[0] ";
            }

            if (profiler$result.field_76331_c.length() == 0)
            {
                s = s + "ROOT ";
            }
            else
            {
                s = s + profiler$result.field_76331_c + " ";
            }

            int l2 = 16777215;
            this.fontRendererObj.drawStringWithShadow(s, (float)(j - i), (float)(k - i / 2 - 16), l2);
            this.fontRendererObj.drawStringWithShadow(s = decimalformat.format(profiler$result.field_76330_b) + "%", (float)(j + i - this.fontRendererObj.getStringWidth(s)), (float)(k - i / 2 - 16), l2);

            for (int k2 = 0; k2 < list.size(); ++k2)
            {
                Profiler.Result profiler$result2 = (Profiler.Result)list.get(k2);
                String s1 = "";

                if (profiler$result2.field_76331_c.equals("unspecified"))
                {
                    s1 = s1 + "[?] ";
                }
                else
                {
                    s1 = s1 + "[" + (k2 + 1) + "] ";
                }

                s1 = s1 + profiler$result2.field_76331_c;
                this.fontRendererObj.drawStringWithShadow(s1, (float)(j - i), (float)(k + i / 2 + k2 * 8 + 20), profiler$result2.getColor());
                this.fontRendererObj.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.field_76332_a) + "%", (float)(j + i - 50 - this.fontRendererObj.getStringWidth(s1)), (float)(k + i / 2 + k2 * 8 + 20), profiler$result2.getColor());
                this.fontRendererObj.drawStringWithShadow(s1 = decimalformat.format(profiler$result2.field_76330_b) + "%", (float)(j + i - this.fontRendererObj.getStringWidth(s1)), (float)(k + i / 2 + k2 * 8 + 20), profiler$result2.getColor());
            }
        }
    }

    /**
     * Called when the window is closing. Sets 'running' to false which allows the game loop to exit cleanly.
     */
    public void shutdown()
    {
        this.running = false;
        LiquidBounce.INSTANCE.stopClient();
    }

    public void setIngameFocus()
    {
        if (Display.isActive()) {
            if (!this.inGameHasFocus) {
                this.inGameHasFocus = true;
                this.mouseHelper.grabMouseCursor();
                this.displayGuiScreen((GuiScreen)null);
                this.leftClickCounter = 10000;
            }
        }
    }

    public void setIngameNotInFocus() {
        if (this.inGameHasFocus) {
            KeyBinding.unPressAllKeys();
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    public void displayInGameMenu()
    {
        if (this.currentScreen == null)
        {
            this.displayGuiScreen(new GuiIngameMenu());

            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic())
            {
                this.mcSoundHandler.pauseSounds();
            }
        }
    }

    private void sendClickBlockToController(boolean leftClick) {
        if (!leftClick) {
            this.leftClickCounter = 0;
        }

        if (this.leftClickCounter <= 0 && (!this.thePlayer.isUsingItem() || MultiActions.Companion.getInstance().getState())) {
            if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();
                if(this.leftClickCounter == 0) {
                    LiquidBounce.eventManager.callEvent(new ClickBlockEvent(blockpos, this.objectMouseOver.sideHit));
                }

                if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
                    this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver.sideHit);
                    this.thePlayer.swingItem();
                }
            } else if (!AbortBreaking.Companion.getInstance().getState()) {
                this.playerController.resetBlockRemoving();
            }
        }
        if(this.leftClickCounter == 0 && theWorld.getBlockState(objectMouseOver.getBlockPos()).getBlock().getMaterial() != Material.air) {
            LiquidBounce.eventManager.callEvent(new ClickBlockEvent(objectMouseOver.getBlockPos(), this.objectMouseOver.sideHit));
        }
    }

    private void clickMouse() //点击鼠标
    {
        CPSCounter.registerClick(CPSCounter.MouseButton.LEFT);

        if (AutoClicker.Companion.getInstance().getState())
            leftClickCounter = 0;

        if (this.leftClickCounter <= 0)
        {
            this.thePlayer.swingItem();

            if (this.objectMouseOver == null)
            {
                logger.error("Null returned as \'hitResult\', this shouldn\'t happen!");

                if (this.playerController.isNotCreative())
                {
                    this.leftClickCounter = 10;
                }
            }
            else
            {
                switch (this.objectMouseOver.typeOfHit)
                {
                    case ENTITY:
                        this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                        break;

                    case BLOCK:
                        BlockPos blockpos = this.objectMouseOver.getBlockPos();

                        if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air)
                        {
                            this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                            break;
                        }

                    case MISS:
                    default:
                        if (this.playerController.isNotCreative())
                        {
                            this.leftClickCounter = 10;
                        }
                }
            }
        }
    }

    @SuppressWarnings("incomplete-switch")
    /**
     * Called when user clicked he's mouse right button (place)
     */
    public void rightClickMouse() {
        CPSCounter.registerClick(CPSCounter.MouseButton.RIGHT);
        final FastPlace fastPlace = FastPlace.Companion.getInstance();

//        if (fastPlace.getState())
//            rightClickDelayTimer = fastPlace.getSpeedValue().get();


        if (!this.playerController.getIsHittingBlock()) {
            this.rightClickDelayTimer = 4;
            if (fastPlace.getState())
                rightClickDelayTimer = fastPlace.getSpeedValue().get();
            // System.out.println(this.rightClickDelayTimer);
            boolean flag = true;
            ItemStack itemstack = this.thePlayer.inventory.getCurrentItem();

            if (this.objectMouseOver == null)
            {
                logger.warn("Null returned as \'hitResult\', this shouldn\'t happen!");
            }
            else
            {
                switch (this.objectMouseOver.typeOfHit)
                {
                    case ENTITY:
                        if (this.playerController.isPlayerRightClickingOnEntity(this.thePlayer, this.objectMouseOver.entityHit, this.objectMouseOver))
                        {
                            flag = false;
                        }
                        else if (this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit))
                        {
                            flag = false;
                        }

                        break;

                    case BLOCK:
                        BlockPos blockpos = this.objectMouseOver.getBlockPos();

                        if (this.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air)
                        {
                            int i = itemstack != null ? itemstack.stackSize : 0;

                            if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, itemstack, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec))
                            {
                                flag = false;
                                this.thePlayer.swingItem();
                            }

                            if (itemstack == null)
                            {
                                return;
                            }

                            if (itemstack.stackSize == 0)
                            {
                                this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                            }
                            else if (itemstack.stackSize != i || this.playerController.isInCreativeMode())
                            {
                                this.entityRenderer.itemRenderer.resetEquippedProgress();
                            }
                        }
                }
            }

            if (flag)
            {
                ItemStack itemstack1 = this.thePlayer.inventory.getCurrentItem();

                if (itemstack1 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, itemstack1))
                {
                    this.entityRenderer.itemRenderer.resetEquippedProgress2();
                }
            }
        }
    }


    /**
     * Toggles fullscreen mode.
     */
    public void toggleFullscreen()
    {
        try
        {
            this.fullscreen = !this.fullscreen;
            this.gameSettings.fullScreen = this.fullscreen;

            if (this.fullscreen)
            {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }
            else
            {
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;

                if (this.displayWidth <= 0)
                {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0)
                {
                    this.displayHeight = 1;
                }
            }

            if (this.currentScreen != null)
            {
                this.resize(this.displayWidth, this.displayHeight);
            }
            else
            {
                this.updateFramebufferSize();
            }

            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.updateDisplay();
        }
        catch (Exception exception)
        {
            logger.error((String)"Couldn\'t toggle fullscreen", (Throwable)exception);
        }
    }

    private void resize(int width, int height)
    {
        this.displayWidth = Math.max(1, width);
        this.displayHeight = Math.max(1, height);

        if (this.currentScreen != null)
        {
            ScaledResolution scaledresolution = new ScaledResolution(this);
            this.currentScreen.onResize(this, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        }

        this.loadingScreen = new LoadingScreenRenderer(this);
        this.updateFramebufferSize();
    }

    private void updateFramebufferSize()
    {
        this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);

        if (this.entityRenderer != null)
        {
            this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
        }
    }

    public MusicTicker getMusicTicker()
    {
        return this.mcMusicTicker;
    }

    public void runTick() throws IOException
    {
        if (this.rightClickDelayTimer > 0)
        {
            --this.rightClickDelayTimer;
        }

        this.mcProfiler.startSection("gui");

        if (!this.isGamePaused)
        {
            this.ingameGUI.updateTick();
        }

        this.mcProfiler.endSection();
        this.entityRenderer.getMouseOver(1.0F);
        this.mcProfiler.startSection("gameMode");

        if (!this.isGamePaused && this.theWorld != null)
        {
            this.playerController.updateController();
        }

        this.mcProfiler.endStartSection("textures");

        if (!this.isGamePaused)
        {
            this.renderEngine.tick();
        }

        if (this.currentScreen == null && this.thePlayer != null)
        {
            if (this.thePlayer.getHealth() <= 0.0F)
            {
                this.displayGuiScreen((GuiScreen)null);
            }
            else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null)
            {
                this.displayGuiScreen(new GuiSleepMP());
            }
        }
        else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping())
        {
            this.displayGuiScreen((GuiScreen)null);
        }

        if (this.currentScreen != null)
        {
            this.leftClickCounter = 10000;
        }

        if (this.currentScreen != null)
        {
            try
            {
                this.currentScreen.handleInput();
            }
            catch (Throwable throwable1)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
                crashreportcategory.addCrashSectionCallable("Screen name", new Callable<String>()
                {
                    public String call() throws Exception
                    {
                        return Minecraft.this.currentScreen.getClass().getCanonicalName();
                    }
                });
                throw new ReportedException(crashreport);
            }

            if (this.currentScreen != null)
            {
                try
                {
                    this.currentScreen.updateScreen();
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
                    CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
                    crashreportcategory1.addCrashSectionCallable("Screen name", new Callable<String>()
                    {
                        public String call() throws Exception
                        {
                            return Minecraft.this.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    throw new ReportedException(crashreport1);
                }
            }
        }

        if (this.currentScreen == null || this.currentScreen.allowUserInput)
        {
            this.mcProfiler.endStartSection("mouse");

            while (Mouse.next())
            {
                int i = Mouse.getEventButton();
                KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());

                if (Mouse.getEventButtonState())
                {
                    if (this.thePlayer.isSpectator() && i == 2)
                    {
                        this.ingameGUI.getSpectatorGui().func_175261_b();
                    }
                    else
                    {
                        KeyBinding.onTick(i - 100);
                    }
                }

                long i1 = getSystemTime() - this.systemTime;

                if (i1 <= 200L)
                {
                    int j = Mouse.getEventDWheel();

                    if (j != 0)
                    {
                        if (this.thePlayer.isSpectator())
                        {
                            j = j < 0 ? -1 : 1;

                            if (this.ingameGUI.getSpectatorGui().func_175262_a())
                            {
                                this.ingameGUI.getSpectatorGui().func_175259_b(-j);
                            }
                            else
                            {
                                float f = MathHelper.clamp(this.thePlayer.capabilities.getFlySpeed() + (float)j * 0.005F, 0.0F, 0.2F);
                                this.thePlayer.capabilities.setFlySpeed(f);
                            }
                        }
                        else
                        {
                            this.thePlayer.inventory.changeCurrentItem(j);
                        }
                    }

                    if (this.currentScreen == null)
                    {
                        if (!this.inGameHasFocus && Mouse.getEventButtonState())
                        {
                            this.setIngameFocus();
                        }
                    }
                    else if (this.currentScreen != null)
                    {
                        this.currentScreen.handleMouseInput();
                    }
                }
            }

            if (this.leftClickCounter > 0)
            {
                --this.leftClickCounter;
            }

            this.mcProfiler.endStartSection("keyboard");

            while (Keyboard.next())
            {
                int k = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
                KeyBinding.setKeyBindState(k, Keyboard.getEventKeyState());

                if (Keyboard.getEventKeyState())
                {
                    KeyBinding.onTick(k);
                }

                if (this.debugCrashKeyPressTime > 0L)
                {
                    if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L)
                    {
                        throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                    }

                    if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61))
                    {
                        this.debugCrashKeyPressTime = -1L;
                    }
                }
                else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61))
                {
                    this.debugCrashKeyPressTime = getSystemTime();
                }

                this.dispatchKeypresses();

                if (Keyboard.getEventKeyState()) {
                    if (k == 62 && this.entityRenderer != null) {
                        this.entityRenderer.switchUseShader();
                    }

                    if (this.currentScreen != null) {
                        this.currentScreen.handleKeyboardInput();
                    } else {

                        LiquidBounce.eventManager.callEvent(new KeyEvent(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()));

                        if (k == 1) {
                            this.displayInGameMenu();
                        }

                        if (k == 32 && Keyboard.isKeyDown(61) && this.ingameGUI != null)
                        {
                            this.ingameGUI.getChatGUI().clearChatMessages();
                        }

                        if (k == 31 && Keyboard.isKeyDown(61))
                        {
                            this.refreshResources();
                        }

                        if (k == 17 && Keyboard.isKeyDown(61))
                        {
                            ;
                        }

                        if (k == 18 && Keyboard.isKeyDown(61))
                        {
                            ;
                        }

                        if (k == 47 && Keyboard.isKeyDown(61))
                        {
                            ;
                        }

                        if (k == 38 && Keyboard.isKeyDown(61))
                        {
                            ;
                        }

                        if (k == 22 && Keyboard.isKeyDown(61))
                        {
                            ;
                        }

                        if (k == 20 && Keyboard.isKeyDown(61))
                        {
                            this.refreshResources();
                        }

                        if (k == 33 && Keyboard.isKeyDown(61))
                        {
                            this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
                        }

                        if (k == 30 && Keyboard.isKeyDown(61))
                        {
                            this.renderGlobal.loadRenderers();
                        }

                        if (k == 35 && Keyboard.isKeyDown(61))
                        {
                            this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                            this.gameSettings.saveOptions();
                        }

                        if (k == 48 && Keyboard.isKeyDown(61))
                        {
                            this.renderManager.setDebugBoundingBox(!this.renderManager.isDebugBoundingBox());
                        }

                        if (k == 25 && Keyboard.isKeyDown(61))
                        {
                            this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                            this.gameSettings.saveOptions();
                        }

                        if (k == 59)
                        {
                            this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                        }

                        if (k == 61)
                        {
                            this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                            this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                            this.gameSettings.showLagometer = GuiScreen.isAltKeyDown();
                        }

                        if (this.gameSettings.keyBindTogglePerspective.isPressed())
                        {
                            ++this.gameSettings.thirdPersonView;

                            if (this.gameSettings.thirdPersonView > 2)
                            {
                                this.gameSettings.thirdPersonView = 0;
                            }

                            if (this.gameSettings.thirdPersonView == 0)
                            {
                                this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
                            }
                            else if (this.gameSettings.thirdPersonView == 1)
                            {
                                this.entityRenderer.loadEntityShader((Entity)null);
                            }

                            this.renderGlobal.setDisplayListEntitiesDirty();
                        }

                        if (this.gameSettings.keyBindSmoothCamera.isPressed())
                        {
                            this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                        }
                    }

                    if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart)
                    {
                        if (k == 11)
                        {
                            this.updateDebugProfilerName(0);
                        }

                        for (int j1 = 0; j1 < 9; ++j1)
                        {
                            if (k == 2 + j1)
                            {
                                this.updateDebugProfilerName(j1 + 1);
                            }
                        }
                    }
                }
            }

            for (int l = 0; l < 9; ++l)
            {
                if (this.gameSettings.keyBindsHotbar[l].isPressed())
                {
                    if (this.thePlayer.isSpectator())
                    {
                        this.ingameGUI.getSpectatorGui().func_175260_a(l);
                    }
                    else
                    {
                        this.thePlayer.inventory.currentItem = l;
                    }
                }
            }

            boolean flag = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

            while (this.gameSettings.keyBindInventory.isPressed())
            {
                if (this.playerController.isRidingHorse())
                {
                    this.thePlayer.sendHorseInventory();
                }
                else
                {
                    this.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    this.displayGuiScreen(new GuiInventory(this.thePlayer));
                }
            }

            while (this.gameSettings.keyBindDrop.isPressed())
            {
                if (!this.thePlayer.isSpectator())
                {
                    this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
                }
            }

            while (this.gameSettings.keyBindChat.isPressed() && flag)
            {
                this.displayGuiScreen(new GuiChat());
            }

            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && flag)
            {
                this.displayGuiScreen(new GuiChat("/"));
            }

            if (this.thePlayer.isUsingItem())
            {
                if (!this.gameSettings.keyBindUseItem.isKeyDown())
                {
                    this.playerController.onStoppedUsingItem(this.thePlayer);
                }

                while (this.gameSettings.keyBindAttack.isPressed())
                {
                    ;
                }

                while (this.gameSettings.keyBindUseItem.isPressed())
                {
                    ;
                }

                while (this.gameSettings.keyBindPickBlock.isPressed())
                {
                    ;
                }
            }
            else
            {
                while (this.gameSettings.keyBindAttack.isPressed())
                {
                    this.clickMouse();
                }

                while (this.gameSettings.keyBindUseItem.isPressed())
                {
                    this.rightClickMouse();
                }

                while (this.gameSettings.keyBindPickBlock.isPressed())
                {
                    this.middleClickMouse();
                }
            }

            if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem())
            {
                this.rightClickMouse();
            }

            this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
        }

        if (this.theWorld != null)
        {
            if (this.thePlayer != null)
            {
                LiquidBounce.eventManager.callEvent(new TickEvent());
                ++this.joinPlayerCounter;

                if (this.joinPlayerCounter == 30)
                {
                    this.joinPlayerCounter = 0;
                    this.theWorld.joinEntityInSurroundings(this.thePlayer);
                }
            }

            this.mcProfiler.endStartSection("gameRenderer");

            if (!this.isGamePaused)
            {
                this.entityRenderer.updateRenderer();
            }

            this.mcProfiler.endStartSection("levelRenderer");

            if (!this.isGamePaused)
            {
                this.renderGlobal.updateClouds();
            }

            this.mcProfiler.endStartSection("level");

            if (!this.isGamePaused)
            {
                if (this.theWorld.getLastLightningBolt() > 0)
                {
                    this.theWorld.setLastLightningBolt(this.theWorld.getLastLightningBolt() - 1);
                }

                this.theWorld.updateEntities();
            }
        }
        else if (this.entityRenderer.isShaderActive())
        {
            this.entityRenderer.stopUseShader();
        }

        if (!this.isGamePaused)
        {
            this.mcMusicTicker.update();
            this.mcSoundHandler.update();
        }

        if (this.theWorld != null)
        {
            if (!this.isGamePaused)
            {
                this.theWorld.setAllowedSpawnTypes(this.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);

                try
                {
                    this.theWorld.tick();
                }
                catch (Throwable throwable2)
                {
                    CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");

                    if (this.theWorld == null)
                    {
                        CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
                        crashreportcategory2.addCrashSection("Problem", "Level is null!");
                    }
                    else
                    {
                        this.theWorld.addWorldInfoToCrashReport(crashreport2);
                    }

                    throw new ReportedException(crashreport2);
                }
            }

            this.mcProfiler.endStartSection("animateTick");

            if (!this.isGamePaused && this.theWorld != null)
            {
                this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
            }

            this.mcProfiler.endStartSection("particles");

            if (!this.isGamePaused)
            {
                this.effectRenderer.updateEffects();
            }
        }
        else if (this.myNetworkManager != null)
        {
            this.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReceivedPackets();
        }

        this.mcProfiler.endSection();
        this.systemTime = getSystemTime();
    }

    /**
     * Arguments: World foldername,  World ingame name, WorldSettings
     */
    public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn)
    {
        this.loadWorld((WorldClient)null);
        System.gc();
        ISaveHandler isavehandler = this.saveLoader.getSaveLoader(folderName, false);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();

        if (worldinfo == null && worldSettingsIn != null)
        {
            worldinfo = new WorldInfo(worldSettingsIn, folderName);
            isavehandler.saveWorldInfo(worldinfo);
        }

        if (worldSettingsIn == null)
        {
            worldSettingsIn = new WorldSettings(worldinfo);
        }

        try
        {
            this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
            crashreportcategory.addCrashSection("Level ID", folderName);
            crashreportcategory.addCrashSection("Level Name", worldName);
            throw new ReportedException(crashreport);
        }

        this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel", new Object[0]));

        while (!this.theIntegratedServer.serverIsInRunLoop())
        {
            String s = this.theIntegratedServer.getUserMessage();

            if (s != null)
            {
                this.loadingScreen.displayLoadingString(I18n.format(s, new Object[0]));
            }
            else
            {
                this.loadingScreen.displayLoadingString("");
            }

            try
            {
                Thread.sleep(200L);
            }
            catch (InterruptedException var9)
            {
                ;
            }
        }

        this.displayGuiScreen((GuiScreen)null);
        SocketAddress socketaddress = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
        NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
        networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, this, (GuiScreen)null));
        networkmanager.sendPacket(new C00Handshake(47, socketaddress.toString(), 0, EnumConnectionState.LOGIN));
        networkmanager.sendPacket(new C00PacketLoginStart(this.getSession().getProfile()));
        this.myNetworkManager = networkmanager;
    }

    public void loadWorld(WorldClient worldClientIn)
    {
        this.loadWorld(worldClientIn, "");
    }

    public void loadWorld(WorldClient worldClientIn, String loadingMessage)
    {
        if (worldClientIn == null)
        {
            NetHandlerPlayClient nethandlerplayclient = this.getNetHandler();

            if (nethandlerplayclient != null) {
                nethandlerplayclient.cleanup();
            }

            if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet()) {
                this.theIntegratedServer.initiateShutdown();
                this.theIntegratedServer.setStaticInstance();
            }

            this.theIntegratedServer = null;
            this.guiAchievement.clearAchievements();
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }

        if(theWorld != null){
            MiniMapRegister.INSTANCE.unloadAllChunks();
        }
        LiquidBounce.eventManager.callEvent(new WorldEvent(worldClientIn));



        this.renderViewEntity = null;
        this.myNetworkManager = null;

        if (this.loadingScreen != null) {
            this.loadingScreen.resetProgressAndMessage(loadingMessage);
            this.loadingScreen.displayLoadingString("");
        }

        if (worldClientIn == null && this.theWorld != null)
        {
            this.mcResourcePackRepository.clearResourcePack();
            this.ingameGUI.resetPlayersOverlayFooterHeader();
            this.setServerData((ServerData)null);
            this.integratedServerIsRunning = false;
        }

        this.mcSoundHandler.stopSounds();
        this.theWorld = worldClientIn;

        if (worldClientIn != null)
        {
            if (this.renderGlobal != null)
            {
                this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
            }

            if (this.effectRenderer != null)
            {
                this.effectRenderer.clearEffects(worldClientIn);
            }

            if (this.thePlayer == null)
            {
                this.thePlayer = this.playerController.func_178892_a(worldClientIn, new StatFileWriter());
                this.playerController.flipPlayer(this.thePlayer);
            }

            this.thePlayer.preparePlayerToSpawn();
            worldClientIn.spawnEntityInWorld(this.thePlayer);
            this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.thePlayer);
            this.renderViewEntity = this.thePlayer;
        }
        else
        {
            this.saveLoader.flushCache();
            this.thePlayer = null;
        }

        System.gc();
        this.systemTime = 0L;
    }

    public void setDimensionAndSpawnPlayer(int dimension)
    {
        this.theWorld.setInitialSpawnLocation();
        this.theWorld.removeAllEntities();
        int i = 0;
        String s = null;

        if (this.thePlayer != null)
        {
            i = this.thePlayer.getEntityId();
            this.theWorld.removeEntity(this.thePlayer);
            s = this.thePlayer.getClientBrand();
        }

        this.renderViewEntity = null;
        EntityPlayerSP entityplayersp = this.thePlayer;
        this.thePlayer = this.playerController.func_178892_a(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
        this.thePlayer.getDataWatcher().updateWatchedObjectsFromList(entityplayersp.getDataWatcher().getAllWatched());
        this.thePlayer.dimension = dimension;
        this.renderViewEntity = this.thePlayer;
        this.thePlayer.preparePlayerToSpawn();
        this.thePlayer.setClientBrand(s);
        this.theWorld.spawnEntityInWorld(this.thePlayer);
        this.playerController.flipPlayer(this.thePlayer);
        this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.thePlayer.setEntityId(i);
        this.playerController.setPlayerCapabilities(this.thePlayer);
        this.thePlayer.setReducedDebug(entityplayersp.hasReducedDebug());

        if (this.currentScreen instanceof GuiGameOver)
        {
            this.displayGuiScreen((GuiScreen)null);
        }
    }

    public final boolean isDemo()
    {
        return this.isDemo;
    }

    public NetHandlerPlayClient getNetHandler()
    {
        return this.thePlayer != null ? this.thePlayer.sendQueue : null;
    }

    public static boolean isGuiEnabled()
    {
        return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled() // useless
    {
        return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
    }

    public static boolean isAmbientOcclusionEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
    }

    private void middleClickMouse()
    {
        if (this.objectMouseOver != null)
        {
            boolean flag = this.thePlayer.capabilities.isCreativeMode;
            int i = 0;
            boolean flag1 = false;
            TileEntity tileentity = null;
            Item item;

            if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();
                Block block = this.theWorld.getBlockState(blockpos).getBlock();

                if (block.getMaterial() == Material.air)
                {
                    return;
                }

                item = block.getItem(this.theWorld, blockpos);

                if (item == null)
                {
                    return;
                }

                if (flag && GuiScreen.isCtrlKeyDown())
                {
                    tileentity = this.theWorld.getTileEntity(blockpos);
                }

                Block block1 = item instanceof ItemBlock && !block.isFlowerPot() ? Block.getBlockFromItem(item) : block;
                i = block1.getDamageValue(this.theWorld, blockpos);
                flag1 = item.getHasSubtypes();
            }
            else
            {
                if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !flag)
                {
                    return;
                }

                if (this.objectMouseOver.entityHit instanceof EntityPainting)
                {
                    item = Items.painting;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot)
                {
                    item = Items.lead;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityItemFrame)
                {
                    EntityItemFrame entityitemframe = (EntityItemFrame)this.objectMouseOver.entityHit;
                    ItemStack itemstack = entityitemframe.getDisplayedItem();

                    if (itemstack == null)
                    {
                        item = Items.item_frame;
                    }
                    else
                    {
                        item = itemstack.getItem();
                        i = itemstack.getMetadata();
                        flag1 = true;
                    }
                }
                else if (this.objectMouseOver.entityHit instanceof EntityMinecart)
                {
                    EntityMinecart entityminecart = (EntityMinecart)this.objectMouseOver.entityHit;

                    switch (entityminecart.getMinecartType())
                    {
                        case FURNACE:
                            item = Items.furnace_minecart;
                            break;

                        case CHEST:
                            item = Items.chest_minecart;
                            break;

                        case TNT:
                            item = Items.tnt_minecart;
                            break;

                        case HOPPER:
                            item = Items.hopper_minecart;
                            break;

                        case COMMAND_BLOCK:
                            item = Items.command_block_minecart;
                            break;

                        default:
                            item = Items.minecart;
                    }
                }
                else if (this.objectMouseOver.entityHit instanceof EntityBoat)
                {
                    item = Items.boat;
                }
                else if (this.objectMouseOver.entityHit instanceof EntityArmorStand)
                {
                    item = Items.armor_stand;
                }
                else
                {
                    item = Items.spawn_egg;
                    i = EntityList.getEntityID(this.objectMouseOver.entityHit);
                    flag1 = true;

                    if (!EntityList.entityEggs.containsKey(Integer.valueOf(i)))
                    {
                        return;
                    }
                }
            }

            InventoryPlayer inventoryplayer = this.thePlayer.inventory;

            if (tileentity == null)
            {
                inventoryplayer.setCurrentItem(item, i, flag1, flag);
            }
            else
            {
                ItemStack itemstack1 = this.pickBlockWithNBT(item, i, tileentity);
                inventoryplayer.setInventorySlotContents(inventoryplayer.currentItem, itemstack1);
            }

            if (flag)
            {
                int j = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + inventoryplayer.currentItem;
                this.playerController.sendSlotPacket(inventoryplayer.getStackInSlot(inventoryplayer.currentItem), j);
            }
        }
    }

    private ItemStack pickBlockWithNBT(Item itemIn, int meta, TileEntity tileEntityIn)
    {
        ItemStack itemstack = new ItemStack(itemIn, 1, meta);
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        tileEntityIn.writeToNBT(nbttagcompound);

        if (itemIn == Items.skull && nbttagcompound.hasKey("Owner"))
        {
            NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
            NBTTagCompound nbttagcompound3 = new NBTTagCompound();
            nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
            itemstack.setTagCompound(nbttagcompound3);
            return itemstack;
        }
        else
        {
            itemstack.setTagInfo("BlockEntityTag", nbttagcompound);
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            NBTTagList nbttaglist = new NBTTagList();
            nbttaglist.appendTag(new NBTTagString("(+NBT)"));
            nbttagcompound1.setTag("Lore", nbttaglist);
            itemstack.setTagInfo("display", nbttagcompound1);
            return itemstack;
        }
    }

    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash)
    {
        theCrash.getCategory().addCrashSectionCallable("Launched Version", new Callable<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.launchedVersion;
            }
        });
        theCrash.getCategory().addCrashSectionCallable("LWJGL", new Callable<String>()
        {
            public String call()
            {
                return Sys.getVersion();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("OpenGL", new Callable<String>()
        {
            public String call()
            {
                return GL11.glGetString(GL11.GL_RENDERER) + " GL version " + GL11.glGetString(GL11.GL_VERSION) + ", " + GL11.glGetString(GL11.GL_VENDOR);
            }
        });
        theCrash.getCategory().addCrashSectionCallable("GL Caps", new Callable<String>()
        {
            public String call()
            {
                return OpenGlHelper.getLogText();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Using VBOs", new Callable<String>()
        {
            public String call()
            {
                return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Is Modded", new Callable<String>()
        {
            public String call() throws Exception
            {
                String s = ClientBrandRetriever.getClientModName();
                return !s.equals("vanilla") ? "Definitely; Client brand changed to \'" + s + "\'" : (Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.");
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Type", new Callable<String>()
        {
            public String call() throws Exception
            {
                return "Client (map_client.txt)";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Resource Packs", new Callable<String>()
        {
            public String call() throws Exception
            {
                StringBuilder stringbuilder = new StringBuilder();

                for (String s : Minecraft.this.gameSettings.resourcePacks)
                {
                    if (stringbuilder.length() > 0)
                    {
                        stringbuilder.append(", ");
                    }

                    stringbuilder.append(s);

                    if (Minecraft.this.gameSettings.incompatibleResourcePacks.contains(s))
                    {
                        stringbuilder.append(" (incompatible)");
                    }
                }

                return stringbuilder.toString();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Current Language", new Callable<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>()
        {
            public String call() throws Exception
            {
                return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("CPU", new Callable<String>()
        {
            public String call()
            {
                return OpenGlHelper.getCpu();
            }
        });

        if (this.theWorld != null)
        {
            this.theWorld.addWorldInfoToCrashReport(theCrash);
        }

        return theCrash;
    }

    public static Minecraft getMinecraft()
    {
        return theMinecraft;
    }

    public ListenableFuture<Object> scheduleResourcesRefresh()
    {
        return this.addScheduledTask(new Runnable()
        {
            public void run()
            {
                Minecraft.this.refreshResources();
            }
        });
    }

    public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper)
    {
        playerSnooper.addClientStat("fps", Integer.valueOf(debugFPS));
        playerSnooper.addClientStat("vsync_enabled", Boolean.valueOf(this.gameSettings.enableVsync));
        playerSnooper.addClientStat("display_frequency", Integer.valueOf(Display.getDisplayMode().getFrequency()));
        playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
        playerSnooper.addClientStat("run_time", Long.valueOf((MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L));
        playerSnooper.addClientStat("current_action", this.getCurrentAction());
        String s = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        playerSnooper.addClientStat("endianness", s);
        playerSnooper.addClientStat("resource_packs", Integer.valueOf(this.mcResourcePackRepository.getRepositoryEntries().size()));
        int i = 0;

        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries())
        {
            playerSnooper.addClientStat("resource_pack[" + i++ + "]", resourcepackrepository$entry.getResourcePackName());
        }

        if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null)
        {
            playerSnooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
        }
    }

    private String getCurrentAction()
    {
        return this.theIntegratedServer != null ? (this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer") : (this.currentServerData != null ? (this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer") : "out_of_game");
    }

    public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper)
    {
        playerSnooper.addStatToSnooper("opengl_version", GL11.glGetString(GL11.GL_VERSION));
        playerSnooper.addStatToSnooper("opengl_vendor", GL11.glGetString(GL11.GL_VENDOR));
        playerSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
        playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
        ContextCapabilities contextcapabilities = GLContext.getCapabilities();
        playerSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", Boolean.valueOf(contextcapabilities.GL_ARB_arrays_of_arrays));
        playerSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", Boolean.valueOf(contextcapabilities.GL_ARB_base_instance));
        playerSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", Boolean.valueOf(contextcapabilities.GL_ARB_blend_func_extended));
        playerSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_clear_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_color_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", Boolean.valueOf(contextcapabilities.GL_ARB_compatibility));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", Boolean.valueOf(contextcapabilities.GL_ARB_compressed_texture_pixel_storage));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_compute_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_buffer));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_image));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_compute_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_buffer));
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", Boolean.valueOf(contextcapabilities.GL_ARB_copy_image));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_buffer_float));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_clamp));
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", Boolean.valueOf(contextcapabilities.GL_ARB_depth_texture));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_buffers));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_buffers_blend));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_elements_base_vertex));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_indirect));
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", Boolean.valueOf(contextcapabilities.GL_ARB_draw_instanced));
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", Boolean.valueOf(contextcapabilities.GL_ARB_explicit_attrib_location));
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", Boolean.valueOf(contextcapabilities.GL_ARB_explicit_uniform_location));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_layer_viewport));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_program));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", Boolean.valueOf(contextcapabilities.GL_ARB_fragment_program_shadow));
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_framebuffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", Boolean.valueOf(contextcapabilities.GL_ARB_framebuffer_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", Boolean.valueOf(contextcapabilities.GL_ARB_geometry_shader4));
        playerSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", Boolean.valueOf(contextcapabilities.GL_ARB_gpu_shader5));
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", Boolean.valueOf(contextcapabilities.GL_ARB_half_float_pixel));
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", Boolean.valueOf(contextcapabilities.GL_ARB_half_float_vertex));
        playerSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", Boolean.valueOf(contextcapabilities.GL_ARB_instanced_arrays));
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", Boolean.valueOf(contextcapabilities.GL_ARB_map_buffer_alignment));
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", Boolean.valueOf(contextcapabilities.GL_ARB_map_buffer_range));
        playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", Boolean.valueOf(contextcapabilities.GL_ARB_multisample));
        playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", Boolean.valueOf(contextcapabilities.GL_ARB_multitexture));
        playerSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", Boolean.valueOf(contextcapabilities.GL_ARB_occlusion_query2));
        playerSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_pixel_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", Boolean.valueOf(contextcapabilities.GL_ARB_seamless_cube_map));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_objects));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_stencil_export));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", Boolean.valueOf(contextcapabilities.GL_ARB_shader_texture_lod));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow]", Boolean.valueOf(contextcapabilities.GL_ARB_shadow));
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", Boolean.valueOf(contextcapabilities.GL_ARB_shadow_ambient));
        playerSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", Boolean.valueOf(contextcapabilities.GL_ARB_stencil_texturing));
        playerSnooper.addStatToSnooper("gl_caps[ARB_sync]", Boolean.valueOf(contextcapabilities.GL_ARB_sync));
        playerSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_tessellation_shader));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_border_clamp));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_cube_map));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_cube_map_array));
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", Boolean.valueOf(contextcapabilities.GL_ARB_texture_non_power_of_two));
        playerSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_uniform_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_blend));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_program));
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", Boolean.valueOf(contextcapabilities.GL_ARB_vertex_shader));
        playerSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", Boolean.valueOf(contextcapabilities.GL_EXT_bindable_uniform));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_equation_separate));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_func_separate));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_minmax));
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", Boolean.valueOf(contextcapabilities.GL_EXT_blend_subtract));
        playerSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", Boolean.valueOf(contextcapabilities.GL_EXT_draw_instanced));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_multisample));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_object));
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", Boolean.valueOf(contextcapabilities.GL_EXT_framebuffer_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", Boolean.valueOf(contextcapabilities.GL_EXT_geometry_shader4));
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", Boolean.valueOf(contextcapabilities.GL_EXT_gpu_program_parameters));
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", Boolean.valueOf(contextcapabilities.GL_EXT_gpu_shader4));
        playerSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", Boolean.valueOf(contextcapabilities.GL_EXT_multi_draw_arrays));
        playerSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", Boolean.valueOf(contextcapabilities.GL_EXT_packed_depth_stencil));
        playerSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", Boolean.valueOf(contextcapabilities.GL_EXT_paletted_texture));
        playerSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", Boolean.valueOf(contextcapabilities.GL_EXT_rescale_normal));
        playerSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", Boolean.valueOf(contextcapabilities.GL_EXT_separate_shader_objects));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", Boolean.valueOf(contextcapabilities.GL_EXT_shader_image_load_store));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", Boolean.valueOf(contextcapabilities.GL_EXT_shadow_funcs));
        playerSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", Boolean.valueOf(contextcapabilities.GL_EXT_shared_texture_palette));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_clear_tag));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_two_side));
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", Boolean.valueOf(contextcapabilities.GL_EXT_stencil_wrap));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_3d));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_array));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_buffer_object));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_integer));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_lod_bias));
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", Boolean.valueOf(contextcapabilities.GL_EXT_texture_sRGB));
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", Boolean.valueOf(contextcapabilities.GL_EXT_vertex_shader));
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", Boolean.valueOf(contextcapabilities.GL_EXT_vertex_weighting));
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", Integer.valueOf(GL11.glGetInteger(GL20.GL_MAX_VERTEX_UNIFORM_COMPONENTS)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", Integer.valueOf(GL11.glGetInteger(GL20.GL_MAX_FRAGMENT_UNIFORM_COMPONENTS)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", Integer.valueOf(GL11.glGetInteger(GL20.GL_MAX_VERTEX_ATTRIBS)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", Integer.valueOf(GL11.glGetInteger(GL20.GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", Integer.valueOf(GL11.glGetInteger(GL20.GL_MAX_TEXTURE_IMAGE_UNITS)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", Integer.valueOf(GL11.glGetInteger(35071)));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_max_texture_size", Integer.valueOf(getGLMaximumTextureSize()));
    }

    public static int getGLMaximumTextureSize()
    {
        for (int i = 16384; i > 0; i >>= 1)
        {
            GL11.glTexImage2D(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_RGBA, i, i, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)((ByteBuffer)null));
            int j = GL11.glGetTexLevelParameteri(GL11.GL_PROXY_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);

            if (j != 0)
            {
                return i;
            }
        }

        return -1;
    }

    public boolean isSnooperEnabled()
    {
        return this.gameSettings.snooperEnabled;
    }

    public void setServerData(ServerData serverDataIn)
    {
        this.currentServerData = serverDataIn;
    }

    public ServerData getCurrentServerData()
    {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning()
    {
        return this.integratedServerIsRunning;
    }

    public boolean isSingleplayer()
    {
        return this.integratedServerIsRunning && this.theIntegratedServer != null;
    }

    public IntegratedServer getIntegratedServer()
    {
        return this.theIntegratedServer;
    }

    public static void stopIntegratedServer()
    {
        if (theMinecraft != null)
        {
            IntegratedServer integratedserver = theMinecraft.getIntegratedServer();

            if (integratedserver != null)
            {
                integratedserver.stopServer();
            }
        }
    }

    public PlayerUsageSnooper getPlayerUsageSnooper()
    {
        return this.usageSnooper;
    }

    public static long getSystemTime()
    {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    public boolean isFullScreen()
    {
        return this.fullscreen;
    }

    public Session getSession()
    {
        return this.session;
    }

    public PropertyMap getTwitchDetails()
    {
        return this.twitchDetails;
    }

    public PropertyMap getProfileProperties()
    {
        if (this.profileProperties.isEmpty())
        {
            GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
            this.profileProperties.putAll(gameprofile.getProperties());
        }

        return this.profileProperties;
    }

    public Proxy getProxy()
    {
        return this.proxy;
    }

    public TextureManager getTextureManager()
    {
        return this.renderEngine;
    }

    public IResourceManager getResourceManager()
    {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository()
    {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager()
    {
        return this.mcLanguageManager;
    }

    public TextureMap getTextureMapBlocks()
    {
        return this.textureMapBlocks;
    }

    public boolean isJava64bit()
    {
        return this.jvm64bit;
    }

    public boolean isGamePaused()
    {
        return this.isGamePaused;
    }

    public SoundHandler getSoundHandler()
    {
        return this.mcSoundHandler;
    }

    public MusicTicker.MusicType getAmbientMusicType()
    {
        return this.thePlayer != null ? (this.thePlayer.worldObj.provider instanceof WorldProviderHell ? MusicTicker.MusicType.NETHER : (this.thePlayer.worldObj.provider instanceof WorldProviderEnd ? (BossStatus.bossName != null && BossStatus.statusBarTime > 0 ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END) : (this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME))) : MusicTicker.MusicType.MENU;
    }

    public IStream getTwitchStream()
    {
        return this.stream;
    }

    public void dispatchKeypresses()
    {
        int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();

        if (i != 0 && !Keyboard.isRepeatEvent())
        {
            if (!(this.currentScreen instanceof GuiControls) || ((GuiControls)this.currentScreen).time <= getSystemTime() - 20L)
            {
                if (Keyboard.getEventKeyState())
                {
                    if (i == this.gameSettings.keyBindStreamStartStop.getKeyCode())
                    {
                        if (this.getTwitchStream().isBroadcasting())
                        {
                            this.getTwitchStream().stopBroadcasting();
                        }
                        else if (this.getTwitchStream().isReadyToBroadcast())
                        {
                            this.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback()
                            {
                                public void confirmClicked(boolean result, int id)
                                {
                                    if (result)
                                    {
                                        Minecraft.this.getTwitchStream().func_152930_t();
                                    }

                                    Minecraft.this.displayGuiScreen((GuiScreen)null);
                                }
                            }, I18n.format("stream.confirm_start", new Object[0]), "", 0));
                        }
                        else if (this.getTwitchStream().func_152928_D() && this.getTwitchStream().func_152936_l())
                        {
                            if (this.theWorld != null)
                            {
                                this.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("Not ready to start streaming yet!"));
                            }
                        }
                        else
                        {
                            GuiStreamUnavailable.func_152321_a(this.currentScreen);
                        }
                    }
                    else if (i == this.gameSettings.keyBindStreamPauseUnpause.getKeyCode())
                    {
                        if (this.getTwitchStream().isBroadcasting())
                        {
                            if (this.getTwitchStream().isPaused())
                            {
                                this.getTwitchStream().unpause();
                            }
                            else
                            {
                                this.getTwitchStream().pause();
                            }
                        }
                    }
                    else if (i == this.gameSettings.keyBindStreamCommercials.getKeyCode())
                    {
                        if (this.getTwitchStream().isBroadcasting())
                        {
                            this.getTwitchStream().requestCommercial();
                        }
                    }
                    else if (i == this.gameSettings.keyBindStreamToggleMic.getKeyCode())
                    {
                        this.stream.muteMicrophone(true);
                    }
                    else if (i == this.gameSettings.keyBindFullscreen.getKeyCode())
                    {
                        this.toggleFullscreen();
                    }
                    else if (i == this.gameSettings.keyBindScreenshot.getKeyCode())
                    {
                        this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
                    }
                }
                else if (i == this.gameSettings.keyBindStreamToggleMic.getKeyCode())
                {
                    this.stream.muteMicrophone(false);
                }
            }
        }
    }

    public MinecraftSessionService getSessionService()
    {
        return this.sessionService;
    }

    public SkinManager getSkinManager()
    {
        return this.skinManager;
    }

    public Entity getRenderViewEntity()
    {
        return this.renderViewEntity;
    }

    public void setRenderViewEntity(Entity viewingEntity)
    {
        this.renderViewEntity = viewingEntity;
        this.entityRenderer.loadEntityShader(viewingEntity);
    }

    public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule)
    {
        Validate.notNull(callableToSchedule);

        if (!this.isCallingFromMinecraftThread())
        {
            ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.<V>create(callableToSchedule);

            synchronized (this.scheduledTasks)
            {
                this.scheduledTasks.add(listenablefuturetask);
                return listenablefuturetask;
            }
        }
        else
        {
            try
            {
                return Futures.<V>immediateFuture(callableToSchedule.call());
            }
            catch (Exception exception)
            {
                return Futures.immediateFailedCheckedFuture(exception);
            }
        }
    }

    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule)
    {
        Validate.notNull(runnableToSchedule);
        return this.<Object>addScheduledTask(Executors.callable(runnableToSchedule));
    }

    public boolean isCallingFromMinecraftThread()
    {
        return Thread.currentThread() == this.mcThread;
    }

    public BlockRendererDispatcher getBlockRendererDispatcher()
    {
        return this.blockRenderDispatcher;
    }

    public RenderManager getRenderManager()
    {
        return this.renderManager;
    }

    public RenderItem getRenderItem()
    {
        return this.renderItem;
    }

    public ItemRenderer getItemRenderer()
    {
        return this.itemRenderer;
    }

    public static int getDebugFPS()
    {
        return debugFPS;
    }

    public FrameTimer getFrameTimer()
    {
        return this.frameTimer;
    }

    public static Map<String, String> getSessionInfo()
    {
        Map<String, String> map = Maps.<String, String>newHashMap();
        map.put("X-Minecraft-Username", getMinecraft().getSession().getUsername());
        map.put("X-Minecraft-UUID", getMinecraft().getSession().getPlayerID());
        map.put("X-Minecraft-Version", "1.8.9");
        return map;
    }

    public boolean isConnectedToRealms()
    {
        return this.connectedToRealms;
    }

    public void setConnectedToRealms(boolean isConnected)
    {
        this.connectedToRealms = isConnected;
    }
}
