package org.vivecraft.settings;

import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.pipeline.RenderTarget;
import java.awt.Color;
import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.optifine.Config;
import net.optifine.Lang;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.vivecraft.reflection.MCReflection;
import org.vivecraft.settings.profile.ProfileManager;
import org.vivecraft.settings.profile.ProfileReader;
import org.vivecraft.settings.profile.ProfileWriter;
import org.vivecraft.utils.LangHelper;
import org.vivecraft.utils.math.Angle;
import org.vivecraft.utils.math.Quaternion;
import org.vivecraft.utils.math.Vector3;

public class VRSettings
{
    public static final int VERSION = 2;
    public static final Logger logger = LogManager.getLogger();
    public static VRSettings inst;
    public JSONObject defaults = new JSONObject();
    public static final int UNKNOWN_VERSION = 0;
    public final String DEGREE = "\u00b0";
    public static final int INERTIA_NONE = 0;
    public static final int INERTIA_NORMAL = 1;
    public static final int INERTIA_LARGE = 2;
    public static final int INERTIA_MASSIVE = 3;
    public static final int BOW_MODE_ON = 2;
    public static final int BOW_MODE_VANILLA = 1;
    public static final int BOW_MODE_OFF = 0;
    public static final float INERTIA_NONE_ADD_FACTOR = 100.0F;
    public static final float INERTIA_NORMAL_ADD_FACTOR = 1.0F;
    public static final float INERTIA_LARGE_ADD_FACTOR = 0.25F;
    public static final float INERTIA_MASSIVE_ADD_FACTOR = 0.0625F;
    public static final int RENDER_FIRST_PERSON_FULL = 0;
    public static final int RENDER_FIRST_PERSON_HAND = 1;
    public static final int RENDER_FIRST_PERSON_NONE = 2;
    public static final int RENDER_CROSSHAIR_MODE_ALWAYS = 0;
    public static final int RENDER_CROSSHAIR_MODE_HUD = 1;
    public static final int RENDER_CROSSHAIR_MODE_NEVER = 2;
    public static final int RENDER_BLOCK_OUTLINE_MODE_ALWAYS = 0;
    public static final int RENDER_BLOCK_OUTLINE_MODE_HUD = 1;
    public static final int RENDER_BLOCK_OUTLINE_MODE_NEVER = 2;
    public static final int CHAT_NOTIFICATIONS_NONE = 0;
    public static final int CHAT_NOTIFICATIONS_HAPTIC = 1;
    public static final int CHAT_NOTIFICATIONS_SOUND = 2;
    public static final int CHAT_NOTIFICATIONS_BOTH = 3;
    public static final int MIRROR_OFF = 10;
    public static final int MIRROR_ON_DUAL = 11;
    public static final int MIRROR_ON_SINGLE = 12;
    public static final int MIRROR_FIRST_PERSON = 13;
    public static final int MIRROR_THIRD_PERSON = 14;
    public static final int MIRROR_MIXED_REALITY = 15;
    public static final int MIRROR_ON_CROPPED = 16;
    public static final int HUD_LOCK_HEAD = 1;
    public static final int HUD_LOCK_HAND = 2;
    public static final int HUD_LOCK_WRIST = 3;
    public static final int HUD_LOCK_BODY = 4;
    public static final int FREEMOVE_CONTROLLER = 1;
    public static final int FREEMOVE_HMD = 2;
    public static final int FREEMOVE_RUNINPLACE = 3;
    public static final int FREEMOVE_ROOM = 5;

    @Deprecated
    public static final int FREEMOVE_JOYPAD = 4;
    public static final int MENU_WORLD_BOTH = 0;
    public static final int MENU_WORLD_CUSTOM = 1;
    public static final int MENU_WORLD_OFFICIAL = 2;
    public static final int MENU_WORLD_NONE = 3;
    public static final int NO_SHADER = -1;
    public int version = 0;
    public int renderFullFirstPersonModelMode = 1;
    public int shaderIndex = -1;
    public String stereoProviderPluginID = "openvr";
    public String badStereoProviderPluginID = "";
    public boolean storeDebugAim = false;
    public int smoothRunTickCount = 20;
    public boolean smoothTick = false;
    public VRSettings.ServerOverrides overrides = new VRSettings.ServerOverrides();
    public String[] vrQuickCommands;
    public String[] vrRadialItems;
    public String[] vrRadialItemsAlt;
    public boolean vrReverseHands = false;
    public boolean vrReverseShootingEye = false;
    public float vrWorldScale = 1.0F;
    public float vrWorldRotation = 0.0F;
    public float vrWorldRotationCached;
    public float vrWorldRotationIncrement = 45.0F;
    public float xSensitivity = 1.0F;
    public float ySensitivity = 1.0F;
    public float keyholeX = 15.0F;
    public double headToHmdLength = (double)0.1F;
    public float autoCalibration = -1.0F;
    public float manualCalibration = -1.0F;
    public boolean alwaysSimulateKeyboard = false;
    public int bowMode = 2;
    public String keyboardKeys = "`1234567890-=qwertyuiop[]\\asdfghjkl;':\"zxcvbnm,./?<>";
    public String keyboardKeysShift = "~!@#$%^&*()_+QWERTYUIOP{}|ASDFGHJKL;':\"ZXCVBNM,./?<>";
    public int hrtfSelection = 0;
    public boolean firstRun = true;
    public int rightclickDelay = 6;
    public int inertiaFactor = 1;
    public boolean walkUpBlocks = true;
    public boolean simulateFalling = true;
    public int weaponCollision = 2;
    public float movementSpeedMultiplier = 1.0F;
    public int vrFreeMoveMode = 1;
    public boolean vrLimitedSurvivalTeleport = true;
    public int vrTeleportUpLimit = 1;
    public int vrTeleportDownLimit = 4;
    public int vrTeleportHorizLimit = 16;
    public boolean seated = false;
    public boolean seatedUseHMD = false;
    public float jumpThreshold = 0.05F;
    public float sneakThreshold = 0.4F;
    public float crawlThreshold = 0.82F;
    public boolean realisticJumpEnabled = true;
    public boolean realisticSneakEnabled = true;
    public boolean realisticClimbEnabled = true;
    public boolean realisticSwimEnabled = true;
    public boolean realisticRowEnabled = true;
    public boolean backpackSwitching = true;
    public boolean physicalGuiEnabled = false;
    public float walkMultiplier = 1.0F;
    public boolean vrAllowCrawling = true;
    public boolean vrShowBlueCircleBuddy = true;
    public boolean vehicleRotation = true;
    public boolean analogMovement = true;
    public boolean autoSprint = true;
    public float autoSprintThreshold = 0.9F;
    public boolean allowStandingOriginOffset = false;
    public boolean seatedFreeMove = false;
    public boolean forceStandingFreeMove = false;
    public boolean useFsaa = true;
    public boolean useFOVReduction = false;
    public float fovRedutioncOffset = 0.1F;
    public float fovReductionMin = 0.25F;
    public boolean vrUseStencil = true;
    public boolean insideBlockSolidColor = false;
    public float renderScaleFactor = 1.0F;
    public int displayMirrorMode = 16;
    public boolean displayMirrorLeftEye = false;
    public boolean shouldRenderSelf = false;
    public boolean tmpRenderSelf;
    public int menuWorldSelection = 0;
    public Color mixedRealityKeyColor = new Color(0, 0, 0);
    public float mixedRealityAspectRatio = 1.7777778F;
    public boolean mixedRealityRenderHands = false;
    public boolean mixedRealityUnityLike = true;
    public boolean mixedRealityMRPlusUndistorted = true;
    public boolean mixedRealityAlphaMask = false;
    public float mixedRealityFov = 40.0F;
    public float vrFixedCamposX = -1.0F;
    public float vrFixedCamposY = 2.4F;
    public float vrFixedCamposZ = 2.7F;
    public Quaternion vrFixedCamrotQuat = new Quaternion(0.962F, 0.125F, 0.239F, 0.041F);
    public float mrMovingCamOffsetX = 0.0F;
    public float mrMovingCamOffsetY = 0.0F;
    public float mrMovingCamOffsetZ = 0.0F;
    public Quaternion mrMovingCamOffsetRotQuat = new Quaternion();
    public Angle.Order externalCameraAngleOrder = Angle.Order.XZY;
    public float handCameraFov = 70.0F;
    public float handCameraResScale = 1.0F;
    public boolean mixedRealityRenderCameraModel = true;
    public boolean vrTouchHotbar = true;
    public float hudScale = 1.0F;
    public float hudDistance = 1.25F;
    public float hudPitchOffset = -2.0F;
    public float hudYawOffset = 0.0F;
    public boolean floatInventory = true;
    public boolean menuAlwaysFollowFace;
    public int vrHudLockMode = 3;
    public boolean hudOcclusion = true;
    public float crosshairScale = 1.0F;
    public boolean crosshairScalesWithDistance = false;
    public int renderInGameCrosshairMode = 0;
    public int renderBlockOutlineMode = 0;
    public float hudOpacity = 1.0F;
    public boolean menuBackground = false;
    public float menuCrosshairScale = 1.0F;
    public boolean useCrosshairOcclusion = true;
    public boolean seatedHudAltMode = true;
    public boolean autoOpenKeyboard = false;
    public int forceHardwareDetection = 0;
    public boolean radialModeHold = true;
    public boolean physicalKeyboard = true;
    public float physicalKeyboardScale = 1.0F;
    public boolean allowAdvancedBindings = false;
    public int chatNotifications = 0;
    public String chatNotificationSound = "block.note_block.bell";
    public boolean guiAppearOverBlock = true;
    private Map<String, String> preservedSettingMap;
    private Minecraft mc;

    public VRSettings(Minecraft minecraft, File dataDir)
    {
        this.mc = minecraft;
        inst = this;
        this.storeDefaults();
        this.loadOptions();
    }

    public void loadOptions()
    {
        this.loadOptions((JSONObject)null);
    }

    public void loadDefaults()
    {
        this.loadOptions(this.defaults);
    }

    public void loadOptions(JSONObject theProfiles)
    {
        try
        {
            ProfileReader profilereader = new ProfileReader("Vr", theProfiles);
            String s = "";

            while ((s = profilereader.readLine()) != null)
            {
                try
                {
                    String[] astring = s.split(":");

                    if (astring[0].equals("version"))
                    {
                        this.version = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("stereoProviderPluginID"))
                    {
                        this.stereoProviderPluginID = astring[1];
                    }

                    if (astring[0].equals("badStereoProviderPluginID") && astring.length > 1)
                    {
                        this.badStereoProviderPluginID = astring[1];
                    }

                    if (astring[0].equals("hudOpacity"))
                    {
                        this.hudOpacity = this.parseFloat(astring[1]);

                        if (this.hudOpacity < 0.15F)
                        {
                            this.hudOpacity = 1.0F;
                        }
                    }

                    if (astring[0].equals("menuBackground"))
                    {
                        this.menuBackground = astring[1].equals("true");
                    }

                    if (astring[0].equals("renderFullFirstPersonModelMode"))
                    {
                        this.renderFullFirstPersonModelMode = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("shaderIndex"))
                    {
                        this.shaderIndex = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("walkUpBlocks"))
                    {
                        this.walkUpBlocks = astring[1].equals("true");
                    }

                    if (astring[0].equals("displayMirrorMode"))
                    {
                        this.displayMirrorMode = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("displayMirrorLeftEye"))
                    {
                        this.displayMirrorLeftEye = astring[1].equals("true");
                    }

                    if (astring[0].equals("mixedRealityKeyColor"))
                    {
                        String[] astring1 = astring[1].split(",");
                        this.mixedRealityKeyColor = new Color(Integer.parseInt(astring1[0]), Integer.parseInt(astring1[1]), Integer.parseInt(astring1[2]));
                    }

                    if (astring[0].equals("mixedRealityRenderHands"))
                    {
                        this.mixedRealityRenderHands = astring[1].equals("true");
                    }

                    if (astring[0].equals("mixedRealityUnityLike"))
                    {
                        this.mixedRealityUnityLike = astring[1].equals("true");
                    }

                    if (astring[0].equals("mixedRealityUndistorted"))
                    {
                        this.mixedRealityMRPlusUndistorted = astring[1].equals("true");
                    }

                    if (astring[0].equals("mixedRealityAlphaMask"))
                    {
                        this.mixedRealityAlphaMask = astring[1].equals("true");
                    }

                    if (astring[0].equals("mixedRealityFov"))
                    {
                        this.mixedRealityFov = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("insideBlockSolidColor"))
                    {
                        this.insideBlockSolidColor = astring[1].equals("true");
                    }

                    if (astring[0].equals("headHudScale"))
                    {
                        this.hudScale = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("renderScaleFactor"))
                    {
                        this.renderScaleFactor = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("vrHudLockMode"))
                    {
                        this.vrHudLockMode = Integer.parseInt(astring[1]);

                        if (this.vrHudLockMode == 4)
                        {
                            this.vrHudLockMode = 2;
                        }
                    }

                    if (astring[0].equals("hudDistance"))
                    {
                        this.hudDistance = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("hudPitchOffset"))
                    {
                        this.hudPitchOffset = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("hudYawOffset"))
                    {
                        this.hudYawOffset = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("useFsaa"))
                    {
                        this.useFsaa = astring[1].equals("true");
                    }

                    if (astring[0].equals("movementSpeedMultiplier"))
                    {
                        this.movementSpeedMultiplier = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("renderInGameCrosshairMode"))
                    {
                        this.renderInGameCrosshairMode = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("crosshairScalesWithDistance"))
                    {
                        this.crosshairScalesWithDistance = astring[1].equals("true");
                    }

                    if (astring[0].equals("renderBlockOutlineMode"))
                    {
                        this.renderBlockOutlineMode = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("crosshairScale"))
                    {
                        this.crosshairScale = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("menuCrosshairScale"))
                    {
                        this.menuCrosshairScale = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("renderInGameCrosshairMode"))
                    {
                        this.renderInGameCrosshairMode = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("renderBlockOutlineMode"))
                    {
                        this.renderBlockOutlineMode = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("hudOcclusion"))
                    {
                        this.hudOcclusion = astring[1].equals("true");
                    }

                    if (astring[0].equals("menuAlwaysFollowFace"))
                    {
                        this.menuAlwaysFollowFace = astring[1].equals("true");
                    }

                    if (astring[0].equals("useCrosshairOcclusion"))
                    {
                        this.useCrosshairOcclusion = astring[1].equals("true");
                    }

                    if (astring[0].equals("inertiaFactor"))
                    {
                        this.inertiaFactor = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("smoothRunTickCount"))
                    {
                        this.smoothRunTickCount = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("smoothTick"))
                    {
                        this.smoothTick = astring[1].equals("true");
                    }

                    if (astring[0].equals("simulateFalling"))
                    {
                        this.simulateFalling = astring[1].equals("true");
                    }

                    if (astring[0].equals("weaponCollisionNew"))
                    {
                        this.weaponCollision = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("allowCrawling"))
                    {
                        this.vrAllowCrawling = astring[1].equals("true");
                    }

                    if (astring[0].equals("limitedTeleport"))
                    {
                        this.vrLimitedSurvivalTeleport = astring[1].equals("true");
                    }

                    if (astring[0].equals("reverseHands"))
                    {
                        this.vrReverseHands = astring[1].equals("true");
                    }

                    if (astring[0].equals("stencilOn"))
                    {
                        this.vrUseStencil = astring[1].equals("true");
                    }

                    if (astring[0].equals("bcbOn"))
                    {
                        this.vrShowBlueCircleBuddy = astring[1].equals("true");
                    }

                    if (astring[0].equals("worldScale"))
                    {
                        this.vrWorldScale = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("worldRotation"))
                    {
                        this.vrWorldRotation = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("vrWorldRotationIncrement"))
                    {
                        this.vrWorldRotationIncrement = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("vrFixedCamposX"))
                    {
                        this.vrFixedCamposX = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("vrFixedCamposY"))
                    {
                        this.vrFixedCamposY = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("vrFixedCamposZ"))
                    {
                        this.vrFixedCamposZ = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("vrFixedCamrotW"))
                    {
                        this.vrFixedCamrotQuat.w = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("vrFixedCamrotX"))
                    {
                        this.vrFixedCamrotQuat.x = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("vrFixedCamrotY"))
                    {
                        this.vrFixedCamrotQuat.y = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("vrFixedCamrotZ"))
                    {
                        this.vrFixedCamrotQuat.z = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("mrMovingCamOffsetX"))
                    {
                        this.mrMovingCamOffsetX = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("mrMovingCamOffsetY"))
                    {
                        this.mrMovingCamOffsetY = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("mrMovingCamOffsetZ"))
                    {
                        this.mrMovingCamOffsetZ = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("mrMovingCamOffsetRotW"))
                    {
                        this.mrMovingCamOffsetRotQuat.w = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("mrMovingCamOffsetRotX"))
                    {
                        this.mrMovingCamOffsetRotQuat.x = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("mrMovingCamOffsetRotY"))
                    {
                        this.mrMovingCamOffsetRotQuat.y = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("mrMovingCamOffsetRotZ"))
                    {
                        this.mrMovingCamOffsetRotQuat.z = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("externalCameraAngleOrder"))
                    {
                        try
                        {
                            this.externalCameraAngleOrder = Angle.Order.valueOf(astring[1].toUpperCase());
                        }
                        catch (IllegalArgumentException illegalargumentexception)
                        {
                            System.out.println("Invalid angle order: " + astring[1]);
                        }
                    }

                    if (astring[0].equals("vrTouchHotbar"))
                    {
                        this.vrTouchHotbar = astring[1].equals("true");
                    }

                    if (astring[0].equals("seated"))
                    {
                        this.seated = astring[1].equals("true");
                    }

                    if (astring[0].equals("jumpThreshold"))
                    {
                        this.jumpThreshold = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("sneakThreshold"))
                    {
                        this.sneakThreshold = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("realisticSneakEnabled"))
                    {
                        this.realisticSneakEnabled = astring[1].equals("true");
                    }

                    if (astring[0].equals("physicalGuiEnabled"))
                    {
                        this.physicalGuiEnabled = astring[1].equals("true");
                        this.physicalGuiEnabled = false;
                    }

                    if (astring[0].equals("seatedhmd"))
                    {
                        this.seatedUseHMD = astring[1].equals("true");
                    }

                    if (astring[0].equals("seatedHudAltMode"))
                    {
                        this.seatedHudAltMode = astring[1].equals("true");
                    }

                    if (astring[0].equals("realisticJumpEnabled"))
                    {
                        this.realisticJumpEnabled = astring[1].equals("true");
                    }

                    if (astring[0].equals("realisticClimbEnabled"))
                    {
                        this.realisticClimbEnabled = astring[1].equals("true");
                    }

                    if (astring[0].equals("realisticSwimEnabled"))
                    {
                        this.realisticSwimEnabled = astring[1].equals("true");
                    }

                    if (astring[0].equals("realisticRowEnabled"))
                    {
                        this.realisticRowEnabled = astring[1].equals("true");
                    }

                    if (astring[0].equals("headToHmdLength"))
                    {
                        this.headToHmdLength = (double)this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("walkMultiplier"))
                    {
                        this.walkMultiplier = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("vrFreeMoveMode"))
                    {
                        this.vrFreeMoveMode = Integer.parseInt(astring[1]);

                        if (this.vrFreeMoveMode == 4)
                        {
                            this.vrFreeMoveMode = 1;
                        }
                    }

                    if (astring[0].equals("xSensitivity"))
                    {
                        this.xSensitivity = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("ySensitivity"))
                    {
                        this.ySensitivity = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("keyholeX"))
                    {
                        this.keyholeX = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("autoCalibration"))
                    {
                        this.autoCalibration = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("manualCalibration"))
                    {
                        this.manualCalibration = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("vehicleRotation"))
                    {
                        this.vehicleRotation = astring[1].equals("true");
                    }

                    if (astring[0].equals("fovReduction"))
                    {
                        this.useFOVReduction = astring[1].equals("true");
                    }

                    if (astring[0].equals("fovReductionMin"))
                    {
                        this.fovReductionMin = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("fovRedutioncOffset"))
                    {
                        this.fovRedutioncOffset = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("alwaysSimulateKeyboard"))
                    {
                        this.alwaysSimulateKeyboard = astring[1].equals("true");
                    }

                    if (astring[0].equals("autoOpenKeyboard"))
                    {
                        this.autoOpenKeyboard = astring[1].equals("true");
                    }

                    if (astring[0].equals("forceHardwareDetection"))
                    {
                        this.forceHardwareDetection = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("backpackSwitching"))
                    {
                        this.backpackSwitching = astring[1].equals("true");
                    }

                    if (astring[0].equals("analogMovement"))
                    {
                        this.analogMovement = astring[1].equals("true");
                    }

                    if (astring[0].equals("bowMode"))
                    {
                        this.bowMode = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("hideGUI"))
                    {
                        this.mc.options.hideGui = astring[1].equals("true");
                    }

                    if (astring[0].equals("teleportLimitUp"))
                    {
                        this.vrTeleportUpLimit = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("teleportLimitDown"))
                    {
                        this.vrTeleportDownLimit = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("teleportLimitHoriz"))
                    {
                        this.vrTeleportHorizLimit = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("radialModeHold"))
                    {
                        this.radialModeHold = astring[1].equals("true");
                    }

                    if (astring[0].equals("physicalKeyboard"))
                    {
                        this.physicalKeyboard = astring[1].equals("true");
                    }

                    if (astring[0].equals("physicalKeyboardScale"))
                    {
                        this.physicalKeyboardScale = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("originOffset"))
                    {
                        String[] astring2 = astring[1].split(",");
                        this.mc.vr.offset = new Vector3(Float.parseFloat(astring2[0]), Float.parseFloat(astring2[1]), Float.parseFloat(astring2[2]));
                    }

                    if (astring[0].equals("allowStandingOriginOffset"))
                    {
                        this.allowStandingOriginOffset = astring[1].equals("true");
                    }

                    if (astring[0].equals("seatedFreeMove"))
                    {
                        this.seatedFreeMove = astring[1].equals("true");
                    }

                    if (astring[0].equals("forceStandingFreeMove"))
                    {
                        this.forceStandingFreeMove = astring[1].equals("true");
                    }

                    if (astring[0].equals("allowAdvancedBindings"))
                    {
                        this.allowAdvancedBindings = astring[1].equals("true");
                    }

                    if (astring[0].equals("menuWorldSelection"))
                    {
                        this.menuWorldSelection = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("chatNotifications"))
                    {
                        this.chatNotifications = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("chatNotificationSound"))
                    {
                        this.chatNotificationSound = astring[1];
                    }

                    if (astring[0].equals("autoSprint"))
                    {
                        this.autoSprint = astring[1].equals("true");
                    }

                    if (astring[0].equals("autoSprintThreshold"))
                    {
                        this.autoSprintThreshold = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("hrtfSelection"))
                    {
                        this.hrtfSelection = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("rightclickDelay"))
                    {
                        this.rightclickDelay = Integer.parseInt(astring[1]);
                    }

                    if (astring[0].equals("guiAppearOverBlock"))
                    {
                        this.guiAppearOverBlock = astring[1].equals("true");
                    }

                    if (astring[0].equals("handCameraFov"))
                    {
                        this.handCameraFov = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("handCameraResScale"))
                    {
                        this.handCameraResScale = this.parseFloat(astring[1]);
                    }

                    if (astring[0].equals("mixedRealityRenderCameraModel"))
                    {
                        this.mixedRealityRenderCameraModel = astring[1].equals("true");
                    }

                    if (astring[0].equals("firstRun"))
                    {
                        this.firstRun = astring[1].equals("true");
                    }

                    if (astring[0].equals("keyboardKeys"))
                    {
                        String s1 = astring[1];

                        for (int i = 2; i < astring.length; ++i)
                        {
                            s1 = s1 + ":" + astring[i];
                        }

                        this.keyboardKeys = s1;
                        int j = s1.length();
                    }

                    if (astring[0].equals("keyboardKeysShift"))
                    {
                        String s2 = astring[1];

                        for (int k = 2; k < astring.length; ++k)
                        {
                            s2 = s2 + ":" + astring[k];
                        }

                        this.keyboardKeysShift = s2;
                    }

                    if (astring[0].startsWith("QUICKCOMMAND_"))
                    {
                        String[] astring3 = astring[0].split("_");
                        int l = Integer.parseInt(astring3[1]);

                        if (astring.length == 1)
                        {
                            this.vrQuickCommands[l] = "";
                        }
                        else
                        {
                            this.vrQuickCommands[l] = astring[1];
                        }
                    }

                    if (astring[0].startsWith("RADIAL_"))
                    {
                        String[] astring4 = astring[0].split("_");
                        int i1 = Integer.parseInt(astring4[1]);

                        if (astring.length == 1)
                        {
                            this.vrRadialItems[i1] = "";
                        }
                        else
                        {
                            this.vrRadialItems[i1] = astring[1];
                        }
                    }

                    if (astring[0].startsWith("RADIALALT_"))
                    {
                        String[] astring5 = astring[0].split("_");
                        int j1 = Integer.parseInt(astring5[1]);

                        if (astring.length == 1)
                        {
                            this.vrRadialItemsAlt[j1] = "";
                        }
                        else
                        {
                            this.vrRadialItemsAlt[j1] = astring[1];
                        }
                    }
                }
                catch (Exception exception)
                {
                    logger.warn("Skipping bad VR option: " + s);
                    exception.printStackTrace();
                }
            }

            this.preservedSettingMap = profilereader.getData();
            profilereader.close();
        }
        catch (Exception exception1)
        {
            logger.warn("Failed to load VR options!");
            exception1.printStackTrace();
        }
    }

    public void resetSettings()
    {
        this.loadDefaults();
    }

    public String getButtonDisplayString(VRSettings.VrOptions par1EnumOptions)
    {
        String s = Lang.get("vivecraft.options." + par1EnumOptions.name());
        String s1 = s + ": ";

        switch (par1EnumOptions)
        {
            case MOVEMENT_MULTIPLIER:
                return s1 + String.format("%.2f", this.movementSpeedMultiplier);

            case HUD_OPACITY:
                if ((double)this.hudOpacity > 0.99D)
                {
                    return s1 + Lang.get("vivecraft.options.opaque");
                }

                return s1 + String.format("%.2f", this.hudOpacity);

            case RENDER_MENU_BACKGROUND:
                return this.menuBackground ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case MIRROR_DISPLAY:
                switch (this.displayMirrorMode)
                {
                    case 10:
                    default:
                        return s1 + Lang.get("vivecraft.options.mirror.off");

                    case 11:
                        return s1 + Lang.get("vivecraft.options.mirror.dual");

                    case 12:
                        return s1 + Lang.get("vivecraft.options.mirror.single");

                    case 13:
                        return s1 + Lang.get("vivecraft.options.mirror.firstperson");

                    case 14:
                        return s1 + Lang.get("vivecraft.options.mirror.thirdperson");

                    case VRSettings.MIRROR_MIXED_REALITY:
                        return s1 + Lang.get("vivecraft.options.mirror.mixedreality");

                    case 16:
                        return s1 + Lang.get("vivecraft.options.mirror.cropped");
                }

            case MIRROR_EYE:
                return this.displayMirrorLeftEye ? s1 + Lang.get("vivecraft.options.left") : s1 + Lang.get("vivecraft.options.right");

            case MIXED_REALITY_KEY_COLOR:
                if (this.mixedRealityKeyColor.equals(new Color(0, 0, 0)))
                {
                    return s1 + Lang.get("vivecraft.options.color.black");
                }
                else if (this.mixedRealityKeyColor.equals(new Color(255, 0, 0)))
                {
                    return s1 + Lang.get("vivecraft.options.color.red");
                }
                else if (this.mixedRealityKeyColor.equals(new Color(255, 255, 0)))
                {
                    return s1 + Lang.get("vivecraft.options.color.yellow");
                }
                else if (this.mixedRealityKeyColor.equals(new Color(0, 255, 0)))
                {
                    return s1 + Lang.get("vivecraft.options.color.green");
                }
                else if (this.mixedRealityKeyColor.equals(new Color(0, 255, 255)))
                {
                    return s1 + Lang.get("vivecraft.options.color.cyan");
                }
                else if (this.mixedRealityKeyColor.equals(new Color(0, 0, 255)))
                {
                    return s1 + Lang.get("vivecraft.options.color.blue");
                }
                else
                {
                    if (this.mixedRealityKeyColor.equals(new Color(255, 0, 255)))
                    {
                        return s1 + Lang.get("vivecraft.options.color.magenta");
                    }

                    return s1 + this.mixedRealityKeyColor.getRed() + " " + this.mixedRealityKeyColor.getGreen() + " " + this.mixedRealityKeyColor.getBlue();
                }

            case MIXED_REALITY_RENDER_HANDS:
                return this.mixedRealityRenderHands ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case MIXED_REALITY_UNITY_LIKE:
                return this.mixedRealityUnityLike ? s1 + Lang.get("vivecraft.options.unity") : s1 + Lang.get("vivecraft.options.sidebyside");

            case MIXED_REALITY_UNDISTORTED:
                return this.mixedRealityMRPlusUndistorted ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case MIXED_REALITY_ALPHA_MASK:
                return this.mixedRealityAlphaMask ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case MIXED_REALITY_FOV:
                return s1 + String.format("%.0f\u00b0", this.mc.vrSettings.mixedRealityFov);

            case WALK_UP_BLOCKS:
                return this.walkUpBlocks ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case HUD_SCALE:
                return s1 + String.format("%.2f", this.hudScale);

            case HUD_LOCK_TO:
                switch (this.vrHudLockMode)
                {
                    case 1:
                        return s1 + Lang.get("vivecraft.options.head");

                    case 2:
                        return s1 + Lang.get("vivecraft.options.hand");

                    case 3:
                        return s1 + Lang.get("vivecraft.options.wrist");

                    case 4:
                        return s1 + Lang.get("vivecraft.options.body");
                }

            case HUD_DISTANCE:
                return s1 + String.format("%.2f", this.hudDistance);

            case HUD_HIDE:
                return this.mc.options.hideGui ? s1 + LangHelper.getYes() : s1 + LangHelper.getNo();

            case RENDER_SCALEFACTOR:
                RenderTarget rendertarget = this.mc.vrRenderer.framebufferEye0;
                return s1 + Math.round(this.renderScaleFactor * 100.0F) + "% (" + (int)Math.ceil((double)rendertarget.viewWidth * Math.sqrt((double)this.renderScaleFactor)) + "x" + (int)Math.ceil((double)rendertarget.viewHeight * Math.sqrt((double)this.renderScaleFactor)) + ")";

            case FSAA:
                return this.useFsaa ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case CROSSHAIR_SCALE:
                return s1 + String.format("%.2f", this.crosshairScale);

            case MENU_CROSSHAIR_SCALE:
                return s1 + String.format("%.2f", this.menuCrosshairScale);

            case CROSSHAIR_SCALES_WITH_DISTANCE:
                return this.crosshairScalesWithDistance ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case RENDER_CROSSHAIR_MODE:
                if (this.renderInGameCrosshairMode == 1)
                {
                    return s1 + Lang.get("vivecraft.options.withhud");
                }
                else if (this.renderInGameCrosshairMode == 0)
                {
                    return s1 + Lang.get("vivecraft.options.always");
                }
                else if (this.renderInGameCrosshairMode == 2)
                {
                    return s1 + Lang.get("vivecraft.options.never");
                }

            case RENDER_BLOCK_OUTLINE_MODE:
                if (this.renderBlockOutlineMode == 1)
                {
                    return s1 + Lang.get("vivecraft.options.withhud");
                }
                else if (this.renderBlockOutlineMode == 0)
                {
                    return s1 + Lang.get("vivecraft.options.always");
                }
                else if (this.renderBlockOutlineMode == 2)
                {
                    return s1 + Lang.get("vivecraft.options.never");
                }

            case CHAT_NOTIFICATIONS:
                if (this.chatNotifications == 0)
                {
                    return s1 + Lang.get("vivecraft.options.chatnotification.none");
                }
                else if (this.chatNotifications == 1)
                {
                    return s1 + Lang.get("vivecraft.options.chatnotification.haptic");
                }
                else if (this.chatNotifications == 2)
                {
                    return s1 + Lang.get("vivecraft.options.chatnotification.sound");
                }
                else if (this.chatNotifications == 3)
                {
                    return s1 + Lang.get("vivecraft.options.chatnotification.both");
                }

            case CHAT_NOTIFICATION_SOUND:
                try
                {
                    SoundEvent soundevent = Registry.SOUND_EVENT.get(new ResourceLocation(this.chatNotificationSound));
                    return Lang.get(soundevent.getLocation().getPath());
                }
                catch (Exception exception)
                {
                    return "error";
                }

            case GUI_APPEAR_OVER_BLOCK:
                return this.guiAppearOverBlock ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case HUD_OCCLUSION:
                return this.hudOcclusion ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case MENU_ALWAYS_FOLLOW_FACE:
                return this.menuAlwaysFollowFace ? s1 + Lang.get("vivecraft.options.always") : s1 + Lang.get("vivecraft.options.seated");

            case CROSSHAIR_OCCLUSION:
                return this.useCrosshairOcclusion ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case MONO_FOV:
                return s1 + String.format("%.0f\u00b0", this.mc.options.fov);

            case INERTIA_FACTOR:
                if (this.inertiaFactor == 0)
                {
                    return s1 + Lang.get("vivecraft.options.inertia.none");
                }
                else if (this.inertiaFactor == 1)
                {
                    return s1 + Lang.get("vivecraft.options.inertia.normal");
                }
                else if (this.inertiaFactor == 2)
                {
                    return s1 + Lang.get("vivecraft.options.inertia.large");
                }
                else if (this.inertiaFactor == 3)
                {
                    return s1 + Lang.get("vivecraft.options.inertia.massive");
                }

            case SIMULATE_FALLING:
                return this.simulateFalling ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case WEAPON_COLLISION:
                if (this.weaponCollision == 0)
                {
                    return s1 + Lang.getOff();
                }
                else if (this.weaponCollision == 1)
                {
                    return s1 + Lang.getOn();
                }
                else if (this.weaponCollision == 2)
                {
                    return s1 + Lang.get("vivecraft.options.auto");
                }

            case ALLOW_CRAWLING:
                return this.vrAllowCrawling ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case LIMIT_TELEPORT:
                return this.overrides.getSetting(par1EnumOptions).getBoolean() ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case REVERSE_HANDS:
                return this.vrReverseHands ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case STENCIL_ON:
                return this.vrUseStencil ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case BCB_ON:
                return this.vrShowBlueCircleBuddy ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case WORLD_SCALE:
                return s1 + String.format("%.2f", this.overrides.getSetting(par1EnumOptions).getFloat()) + "x";

            case WORLD_ROTATION:
                return s1 + String.format("%.0f", this.vrWorldRotation);

            case WORLD_ROTATION_INCREMENT:
                return s1 + (this.vrWorldRotationIncrement == 0.0F ? Lang.get("vivecraft.options.smooth") : String.format("%.0f", this.vrWorldRotationIncrement));

            case TOUCH_HOTBAR:
                return this.vrTouchHotbar ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case PLAY_MODE_SEATED:
                return this.seated ? s1 + Lang.get("vivecraft.options.seated") : s1 + Lang.get("vivecraft.options.standing");

            case REALISTIC_JUMP:
                return this.realisticJumpEnabled ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case SEATED_HMD:
                return this.seatedUseHMD ? s1 + Lang.get("vivecraft.options.hmd") : s1 + Lang.get("vivecraft.options.crosshair");

            case SEATED_HUD_XHAIR:
                return this.seatedHudAltMode ? s1 + Lang.get("vivecraft.options.crosshair") : s1 + Lang.get("vivecraft.options.hmd");

            case REALISTIC_SNEAK:
                return this.realisticSneakEnabled ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case PHYSICAL_GUI:
                return this.physicalGuiEnabled ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case REALISTIC_CLIMB:
                return this.realisticClimbEnabled ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case REALISTIC_SWIM:
                return this.realisticSwimEnabled ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case REALISTIC_ROW:
                return this.realisticRowEnabled ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case VEHICLE_ROTATION:
                return this.vehicleRotation ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case WALK_MULTIPLIER:
                return s1 + String.format("%.1f", this.walkMultiplier);

            case X_SENSITIVITY:
                return s1 + String.format("%.2f", this.xSensitivity);

            case Y_SENSITIVITY:
                return s1 + String.format("%.2f", this.ySensitivity);

            case KEYHOLE:
                return s1 + String.format("%.0f", this.keyholeX);

            case RESET_ORIGIN:
                return s;

            case FREEMOVE_MODE:
                switch (this.vrFreeMoveMode)
                {
                    case 1:
                        return s1 + Lang.get("vivecraft.options.controller");

                    case 2:
                        return s1 + Lang.get("vivecraft.options.hmd");

                    case 3:
                        return s1 + Lang.get("vivecraft.options.runinplace");

                    case 4:
                    default:
                        break;

                    case 5:
                        return s1 + Lang.get("vivecraft.options.room");
                }

            case FOV_REDUCTION:
                return this.useFOVReduction ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case FOV_REDUCTION_MIN:
                return s1 + String.format("%.2f", this.fovReductionMin);

            case FOV_REDUCTION_OFFSET:
                return s1 + String.format("%.2f", this.fovRedutioncOffset);

            case AUTO_OPEN_KEYBOARD:
                return this.autoOpenKeyboard ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case BACKPACK_SWITCH:
                return this.backpackSwitching ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case ANALOG_MOVEMENT:
                return this.analogMovement ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case AUTO_SPRINT:
                return this.autoSprint ? s1 + Lang.getOn() : s1 + Lang.getOff();

            case AUTO_SPRINT_THRESHOLD:
                return s1 + String.format("%.2f", this.autoSprintThreshold);

            case RADIAL_MODE_HOLD:
                return this.radialModeHold ? s1 + Lang.get("vivecraft.options.hold") : s1 + Lang.get("vivecraft.options.press");

            case PHYSICAL_KEYBOARD:
                return this.physicalKeyboard ? s1 + Lang.get("vivecraft.options.keyboard.physical") : s1 + Lang.get("vivecraft.options.keyboard.pointer");

            case PHYSICAL_KEYBOARD_SCALE:
                return s1 + Math.round(this.physicalKeyboardScale * 100.0F) + "%";

            case BOW_MODE:
                if (this.bowMode == 0)
                {
                    return s1 + Lang.getOff();
                }
                else if (this.bowMode == 2)
                {
                    return s1 + Lang.getOn();
                }
                else
                {
                    if (this.bowMode == 1)
                    {
                        return s1 + Lang.get("vivecraft.options.vanilla");
                    }

                    return s1 + "wtf?";
                }

            case TELEPORT_UP_LIMIT:
                int k = this.overrides.getSetting(par1EnumOptions).getInt();
                return s1 + (k > 0 ? LangHelper.get("vivecraft.options.teleportlimit", k) : Lang.getOff());

            case TELEPORT_DOWN_LIMIT:
                int j = this.overrides.getSetting(par1EnumOptions).getInt();
                return s1 + (j > 0 ? LangHelper.get("vivecraft.options.teleportlimit", j) : Lang.getOff());

            case TELEPORT_HORIZ_LIMIT:
                int i = this.overrides.getSetting(par1EnumOptions).getInt();
                return s1 + (i > 0 ? LangHelper.get("vivecraft.options.teleportlimit", i) : Lang.getOff());

            case ALLOW_STANDING_ORIGIN_OFFSET:
                return this.allowStandingOriginOffset ? s1 + LangHelper.getYes() : s1 + LangHelper.getNo();

            case SEATED_FREE_MOVE:
                return this.seatedFreeMove ? s1 + Lang.get("vivecraft.options.freemove") : s1 + Lang.get("vivecraft.options.teleport");

            case FORCE_STANDING_FREE_MOVE:
                return this.forceStandingFreeMove ? s1 + LangHelper.getYes() : s1 + LangHelper.getNo();

            case ALLOW_ADVANCED_BINDINGS:
                return this.allowAdvancedBindings ? s1 + LangHelper.getYes() : s1 + LangHelper.getNo();

            case MENU_WORLD_SELECTION:
                switch (this.menuWorldSelection)
                {
                    case 0:
                        return s1 + Lang.get("vivecraft.options.menuworld.both");

                    case 1:
                        return s1 + Lang.get("vivecraft.options.menuworld.custom");

                    case 2:
                        return s1 + Lang.get("vivecraft.options.menuworld.official");

                    case 3:
                        return s1 + Lang.get("vivecraft.options.menuworld.none");
                }

            case HRTF_SELECTION:
                if (this.hrtfSelection == -1)
                {
                    return s1 + Lang.getOff();
                }
                else if (this.hrtfSelection == 0)
                {
                    return s1 + Lang.get("vivecraft.options.default");
                }
                else if (this.hrtfSelection <= Library.hrtfList.size())
                {
                    return s1 + (String)Library.hrtfList.get(this.hrtfSelection - 1);
                }

            case RIGHT_CLICK_DELAY:
                switch (this.rightclickDelay)
                {
                    case 4:
                        return s1 + Lang.get("vivecraft.options.default");

                    case 5:
                    case 7:
                    case 9:
                    default:
                        break;

                    case 6:
                        return s1 + Lang.get("vivecraft.options.rightclickdelay.slow");

                    case 8:
                        return s1 + Lang.get("vivecraft.options.rightclickdelay.slower");

                    case 10:
                        return s1 + Lang.get("vivecraft.options.rightclickdelay.slowest");
                }

            case HANDHELD_CAMERA_FOV:
                return s1 + String.format("%.0f\u00b0", this.handCameraFov);

            case HANDHELD_CAMERA_RENDER_SCALE:
                if (Config.isShaders())
                {
                    RenderTarget rendertarget1 = this.mc.vrRenderer.cameraFramebuffer;
                    return s1 + rendertarget1.viewWidth + "x" + rendertarget1.viewHeight;
                }

                return s1 + Math.round(1920.0F * this.handCameraResScale) + "x" + Math.round(1080.0F * this.handCameraResScale);

            case MIXED_REALITY_RENDER_CAMERA_MODEL:
                return this.mixedRealityRenderCameraModel ? s1 + LangHelper.getYes() : s1 + LangHelper.getNo();

            case RELOAD_EXTERNAL_CAMERA:
                return s;

            default:
                return "";
        }
    }

    public float getOptionFloatValue(VRSettings.VrOptions par1EnumOptions)
    {
        switch (par1EnumOptions)
        {
            case MOVEMENT_MULTIPLIER:
                return this.movementSpeedMultiplier;

            case HUD_OPACITY:
                return this.hudOpacity;

            case RENDER_MENU_BACKGROUND:
            case MIRROR_DISPLAY:
            case MIRROR_EYE:
            case MIXED_REALITY_KEY_COLOR:
            case MIXED_REALITY_RENDER_HANDS:
            case MIXED_REALITY_UNITY_LIKE:
            case MIXED_REALITY_UNDISTORTED:
            case MIXED_REALITY_ALPHA_MASK:
            case WALK_UP_BLOCKS:
            case HUD_LOCK_TO:
            case HUD_HIDE:
            case FSAA:
            case CROSSHAIR_SCALES_WITH_DISTANCE:
            case RENDER_CROSSHAIR_MODE:
            case RENDER_BLOCK_OUTLINE_MODE:
            case CHAT_NOTIFICATIONS:
            case CHAT_NOTIFICATION_SOUND:
            case GUI_APPEAR_OVER_BLOCK:
            case HUD_OCCLUSION:
            case MENU_ALWAYS_FOLLOW_FACE:
            case CROSSHAIR_OCCLUSION:
            case INERTIA_FACTOR:
            case SIMULATE_FALLING:
            case WEAPON_COLLISION:
            case ALLOW_CRAWLING:
            case LIMIT_TELEPORT:
            case REVERSE_HANDS:
            case STENCIL_ON:
            case BCB_ON:
            case TOUCH_HOTBAR:
            case PLAY_MODE_SEATED:
            case REALISTIC_JUMP:
            case SEATED_HMD:
            case SEATED_HUD_XHAIR:
            case REALISTIC_SNEAK:
            case PHYSICAL_GUI:
            case REALISTIC_CLIMB:
            case REALISTIC_SWIM:
            case REALISTIC_ROW:
            case VEHICLE_ROTATION:
            case RESET_ORIGIN:
            case FREEMOVE_MODE:
            case FOV_REDUCTION:
            case AUTO_OPEN_KEYBOARD:
            case BACKPACK_SWITCH:
            case ANALOG_MOVEMENT:
            case AUTO_SPRINT:
            case RADIAL_MODE_HOLD:
            case PHYSICAL_KEYBOARD:
            case ALLOW_STANDING_ORIGIN_OFFSET:
            case SEATED_FREE_MOVE:
            case FORCE_STANDING_FREE_MOVE:
            case ALLOW_ADVANCED_BINDINGS:
            case MENU_WORLD_SELECTION:
            case HRTF_SELECTION:
            case RIGHT_CLICK_DELAY:
            default:
                return 0.0F;

            case MIXED_REALITY_FOV:
                return this.mixedRealityFov;

            case HUD_SCALE:
                return this.hudScale;

            case HUD_DISTANCE:
                return this.hudDistance;

            case RENDER_SCALEFACTOR:
                return this.renderScaleFactor;

            case CROSSHAIR_SCALE:
                return this.crosshairScale;

            case MENU_CROSSHAIR_SCALE:
                return this.menuCrosshairScale;

            case MONO_FOV:
                return (float)this.mc.options.fov;

            case WORLD_SCALE:
                float f = this.overrides.getSetting(par1EnumOptions).getFloat();

                if (f == 0.1F)
                {
                    return 0.0F;
                }
                else if (f == 0.25F)
                {
                    return 1.0F;
                }
                else if (f >= 0.5F && f <= 2.0F)
                {
                    return f / 0.1F - 3.0F;
                }
                else if (f == 3.0F)
                {
                    return 18.0F;
                }
                else if (f == 4.0F)
                {
                    return 19.0F;
                }
                else if (f == 6.0F)
                {
                    return 20.0F;
                }
                else if (f == 8.0F)
                {
                    return 21.0F;
                }
                else if (f == 10.0F)
                {
                    return 22.0F;
                }
                else if (f == 12.0F)
                {
                    return 23.0F;
                }
                else if (f == 16.0F)
                {
                    return 24.0F;
                }
                else if (f == 20.0F)
                {
                    return 25.0F;
                }
                else if (f == 30.0F)
                {
                    return 26.0F;
                }
                else if (f == 50.0F)
                {
                    return 27.0F;
                }
                else if (f == 75.0F)
                {
                    return 28.0F;
                }
                else
                {
                    if (f == 100.0F)
                    {
                        return 29.0F;
                    }

                    return 7.0F;
                }

            case WORLD_ROTATION:
                return this.vrWorldRotation;

            case WORLD_ROTATION_INCREMENT:
                if (this.vrWorldRotationIncrement == 0.0F)
                {
                    return -1.0F;
                }
                else if (this.vrWorldRotationIncrement == 10.0F)
                {
                    return 0.0F;
                }
                else if (this.vrWorldRotationIncrement == 36.0F)
                {
                    return 1.0F;
                }
                else if (this.vrWorldRotationIncrement == 45.0F)
                {
                    return 2.0F;
                }
                else if (this.vrWorldRotationIncrement == 90.0F)
                {
                    return 3.0F;
                }
                else
                {
                    if (this.vrWorldRotationIncrement == 180.0F)
                    {
                        return 4.0F;
                    }

                    return 0.0F;
                }

            case WALK_MULTIPLIER:
                return this.walkMultiplier;

            case X_SENSITIVITY:
                return this.xSensitivity;

            case Y_SENSITIVITY:
                return this.ySensitivity;

            case KEYHOLE:
                return this.keyholeX;

            case FOV_REDUCTION_MIN:
                return this.fovReductionMin;

            case FOV_REDUCTION_OFFSET:
                return this.fovRedutioncOffset;

            case AUTO_SPRINT_THRESHOLD:
                return this.autoSprintThreshold;

            case PHYSICAL_KEYBOARD_SCALE:
                return this.physicalKeyboardScale;

            case BOW_MODE:
                return (float)this.bowMode;

            case TELEPORT_UP_LIMIT:
                return (float)this.overrides.getSetting(par1EnumOptions).getInt();

            case TELEPORT_DOWN_LIMIT:
                return (float)this.overrides.getSetting(par1EnumOptions).getInt();

            case TELEPORT_HORIZ_LIMIT:
                return (float)this.overrides.getSetting(par1EnumOptions).getInt();

            case HANDHELD_CAMERA_FOV:
                return this.handCameraFov;

            case HANDHELD_CAMERA_RENDER_SCALE:
                return this.handCameraResScale;
        }
    }

    public void setOptionValue(VRSettings.VrOptions par1EnumOptions)
    {
        label352:

        switch (par1EnumOptions)
        {
            case RENDER_MENU_BACKGROUND:
                this.menuBackground = !this.menuBackground;
                break;

            case MIRROR_DISPLAY:
                switch (this.displayMirrorMode)
                {
                    case 10:
                    default:
                        this.displayMirrorMode = 16;
                        break;

                    case 11:
                        this.displayMirrorMode = 13;
                        break;

                    case 12:
                        this.displayMirrorMode = 11;
                        break;

                    case 13:
                        this.displayMirrorMode = 14;
                        break;

                    case 14:
                        this.displayMirrorMode = 15;
                        break;

                    case 15:
                        this.displayMirrorMode = 10;
                        break;

                    case 16:
                        this.displayMirrorMode = 12;
                }

                this.mc.vrRenderer.reinitFrameBuffers("Mirror Setting Changed");
                break;

            case MIRROR_EYE:
                this.displayMirrorLeftEye = !this.displayMirrorLeftEye;
                break;

            case MIXED_REALITY_KEY_COLOR:
                if (this.mixedRealityKeyColor.equals(new Color(0, 0, 0)))
                {
                    this.mixedRealityKeyColor = new Color(255, 0, 0);
                }
                else if (this.mixedRealityKeyColor.equals(new Color(255, 0, 0)))
                {
                    this.mixedRealityKeyColor = new Color(255, 255, 0);
                }
                else if (this.mixedRealityKeyColor.equals(new Color(255, 255, 0)))
                {
                    this.mixedRealityKeyColor = new Color(0, 255, 0);
                }
                else if (this.mixedRealityKeyColor.equals(new Color(0, 255, 0)))
                {
                    this.mixedRealityKeyColor = new Color(0, 255, 255);
                }
                else if (this.mixedRealityKeyColor.equals(new Color(0, 255, 255)))
                {
                    this.mixedRealityKeyColor = new Color(0, 0, 255);
                }
                else if (this.mixedRealityKeyColor.equals(new Color(0, 0, 255)))
                {
                    this.mixedRealityKeyColor = new Color(255, 0, 255);
                }
                else if (this.mixedRealityKeyColor.equals(new Color(255, 0, 255)))
                {
                    this.mixedRealityKeyColor = new Color(0, 0, 0);
                }
                else
                {
                    this.mixedRealityKeyColor = new Color(0, 0, 0);
                }

                break;

            case MIXED_REALITY_RENDER_HANDS:
                this.mixedRealityRenderHands = !this.mixedRealityRenderHands;
                break;

            case MIXED_REALITY_UNITY_LIKE:
                this.mixedRealityUnityLike = !this.mixedRealityUnityLike;
                this.mc.vrRenderer.reinitFrameBuffers("MR Setting Changed");
                break;

            case MIXED_REALITY_UNDISTORTED:
                this.mixedRealityMRPlusUndistorted = !this.mixedRealityMRPlusUndistorted;
                this.mc.vrRenderer.reinitFrameBuffers("MR Setting Changed");
                break;

            case MIXED_REALITY_ALPHA_MASK:
                this.mixedRealityAlphaMask = !this.mixedRealityAlphaMask;
                this.mc.vrRenderer.reinitFrameBuffers("MR Setting Changed");

            case MIXED_REALITY_FOV:
            case HUD_SCALE:
            case HUD_DISTANCE:
            case RENDER_SCALEFACTOR:
            case CROSSHAIR_SCALE:
            case MENU_CROSSHAIR_SCALE:
            case MONO_FOV:
            case WORLD_SCALE:
            case WORLD_ROTATION:
            case WORLD_ROTATION_INCREMENT:
            case WALK_MULTIPLIER:
            case X_SENSITIVITY:
            case Y_SENSITIVITY:
            case KEYHOLE:
            case RESET_ORIGIN:
            case FOV_REDUCTION_MIN:
            case FOV_REDUCTION_OFFSET:
            case AUTO_SPRINT_THRESHOLD:
            case PHYSICAL_KEYBOARD_SCALE:
            case TELEPORT_UP_LIMIT:
            case TELEPORT_DOWN_LIMIT:
            case TELEPORT_HORIZ_LIMIT:
            case HANDHELD_CAMERA_FOV:
            case HANDHELD_CAMERA_RENDER_SCALE:
            default:
                break;

            case WALK_UP_BLOCKS:
                this.walkUpBlocks = !this.walkUpBlocks;
                break;

            case HUD_LOCK_TO:
                switch (this.vrHudLockMode)
                {
                    case 1:
                        this.vrHudLockMode = 3;
                        break label352;

                    case 2:
                        this.vrHudLockMode = 1;
                        break label352;

                    case 3:
                        this.vrHudLockMode = 2;
                        break label352;

                    default:
                        this.vrHudLockMode = 2;
                        break label352;
                }

            case HUD_HIDE:
                this.mc.options.hideGui = !this.mc.options.hideGui;
                break;

            case FSAA:
                this.useFsaa = !this.useFsaa;
                break;

            case CROSSHAIR_SCALES_WITH_DISTANCE:
                this.crosshairScalesWithDistance = !this.crosshairScalesWithDistance;
                break;

            case RENDER_CROSSHAIR_MODE:
                ++this.renderInGameCrosshairMode;

                if (this.renderInGameCrosshairMode > 2)
                {
                    this.renderInGameCrosshairMode = 0;
                }

                break;

            case RENDER_BLOCK_OUTLINE_MODE:
                ++this.renderBlockOutlineMode;

                if (this.renderBlockOutlineMode > 2)
                {
                    this.renderBlockOutlineMode = 0;
                }

                break;

            case CHAT_NOTIFICATIONS:
                ++this.chatNotifications;

                if (this.chatNotifications > 3)
                {
                    this.chatNotifications = 0;
                }

                break;

            case CHAT_NOTIFICATION_SOUND:
                try
                {
                    SoundEvent soundevent = Registry.SOUND_EVENT.get(new ResourceLocation(this.chatNotificationSound));
                    int i = Registry.SOUND_EVENT.getId(soundevent);
                    ++i;

                    if (i >= Registry.SOUND_EVENT.keySet().size())
                    {
                        i = 0;
                    }

                    this.chatNotificationSound = Registry.SOUND_EVENT.byId(i).getLocation().getPath();
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }

                break;

            case GUI_APPEAR_OVER_BLOCK:
                this.guiAppearOverBlock = !this.guiAppearOverBlock;
                break;

            case HUD_OCCLUSION:
                this.hudOcclusion = !this.hudOcclusion;
                break;

            case MENU_ALWAYS_FOLLOW_FACE:
                this.menuAlwaysFollowFace = !this.menuAlwaysFollowFace;
                break;

            case CROSSHAIR_OCCLUSION:
                this.useCrosshairOcclusion = !this.useCrosshairOcclusion;
                break;

            case INERTIA_FACTOR:
                ++this.inertiaFactor;

                if (this.inertiaFactor > 3)
                {
                    this.inertiaFactor = 0;
                }

                break;

            case SIMULATE_FALLING:
                this.simulateFalling = !this.simulateFalling;
                break;

            case WEAPON_COLLISION:
                ++this.weaponCollision;

                if (this.weaponCollision > 2)
                {
                    this.weaponCollision = 0;
                }

                break;

            case ALLOW_CRAWLING:
                this.vrAllowCrawling = !this.vrAllowCrawling;
                break;

            case LIMIT_TELEPORT:
                this.vrLimitedSurvivalTeleport = !this.vrLimitedSurvivalTeleport;
                break;

            case REVERSE_HANDS:
                this.vrReverseHands = !this.vrReverseHands;
                break;

            case STENCIL_ON:
                this.vrUseStencil = !this.vrUseStencil;
                break;

            case BCB_ON:
                this.vrShowBlueCircleBuddy = !this.vrShowBlueCircleBuddy;
                break;

            case TOUCH_HOTBAR:
                this.vrTouchHotbar = !this.vrTouchHotbar;
                break;

            case PLAY_MODE_SEATED:
                this.seated = !this.seated;
                break;

            case REALISTIC_JUMP:
                this.realisticJumpEnabled = !this.realisticJumpEnabled;
                break;

            case SEATED_HMD:
                this.seatedUseHMD = !this.seatedUseHMD;
                break;

            case SEATED_HUD_XHAIR:
                this.seatedHudAltMode = !this.seatedHudAltMode;
                break;

            case REALISTIC_SNEAK:
                this.realisticSneakEnabled = !this.realisticSneakEnabled;
                break;

            case PHYSICAL_GUI:
                this.physicalGuiEnabled = !this.physicalGuiEnabled;
                break;

            case REALISTIC_CLIMB:
                this.realisticClimbEnabled = !this.realisticClimbEnabled;
                break;

            case REALISTIC_SWIM:
                this.realisticSwimEnabled = !this.realisticSwimEnabled;
                break;

            case REALISTIC_ROW:
                this.realisticRowEnabled = !this.realisticRowEnabled;
                break;

            case VEHICLE_ROTATION:
                this.vehicleRotation = !this.vehicleRotation;
                break;

            case FREEMOVE_MODE:
                switch (this.vrFreeMoveMode)
                {
                    case 1:
                        this.vrFreeMoveMode = 2;
                        break label352;

                    case 2:
                        this.vrFreeMoveMode = 3;
                        break label352;

                    case 3:
                        this.vrFreeMoveMode = 5;
                        break label352;

                    default:
                        this.vrFreeMoveMode = 1;
                        break label352;
                }

            case FOV_REDUCTION:
                this.useFOVReduction = !this.useFOVReduction;
                break;

            case AUTO_OPEN_KEYBOARD:
                this.autoOpenKeyboard = !this.autoOpenKeyboard;
                break;

            case BACKPACK_SWITCH:
                this.backpackSwitching = !this.backpackSwitching;
                break;

            case ANALOG_MOVEMENT:
                this.analogMovement = !this.analogMovement;
                break;

            case AUTO_SPRINT:
                this.autoSprint = !this.autoSprint;
                break;

            case RADIAL_MODE_HOLD:
                this.radialModeHold = !this.radialModeHold;
                break;

            case PHYSICAL_KEYBOARD:
                this.physicalKeyboard = !this.physicalKeyboard;
                break;

            case BOW_MODE:
                ++this.bowMode;

                if (this.bowMode > 2)
                {
                    this.bowMode = 0;
                }

                break;

            case ALLOW_STANDING_ORIGIN_OFFSET:
                this.allowStandingOriginOffset = !this.allowStandingOriginOffset;
                break;

            case SEATED_FREE_MOVE:
                this.seatedFreeMove = !this.seatedFreeMove;
                break;

            case FORCE_STANDING_FREE_MOVE:
                this.forceStandingFreeMove = !this.forceStandingFreeMove;
                break;

            case ALLOW_ADVANCED_BINDINGS:
                this.allowAdvancedBindings = !this.allowAdvancedBindings;
                break;

            case MENU_WORLD_SELECTION:
                switch (this.menuWorldSelection)
                {
                    case 0:
                        this.menuWorldSelection = 1;
                        break label352;

                    case 1:
                        this.menuWorldSelection = 2;
                        break label352;

                    case 2:
                        this.menuWorldSelection = 3;
                        break label352;

                    default:
                        this.menuWorldSelection = 0;
                        break label352;
                }

            case HRTF_SELECTION:
                ++this.hrtfSelection;

                if (this.hrtfSelection > Library.hrtfList.size())
                {
                    this.hrtfSelection = -1;
                }
                SoundEngine eng = (SoundEngine) MCReflection.SoundHandler_sndManager.get(this.mc.getSoundManager());
                eng.reload();
                break;

            case RIGHT_CLICK_DELAY:
                this.rightclickDelay += 2;

                if (this.rightclickDelay > 10)
                {
                    this.rightclickDelay = 4;
                }

                break;

            case MIXED_REALITY_RENDER_CAMERA_MODEL:
                this.mixedRealityRenderCameraModel = !this.mixedRealityRenderCameraModel;
                break;

            case RELOAD_EXTERNAL_CAMERA:
                VRHotkeys.loadExternalCameraConfig();
        }

        this.saveOptions();
    }

    public void setOptionFloatValue(VRSettings.VrOptions par1EnumOptions, float par2)
    {
        switch (par1EnumOptions)
        {
            case MOVEMENT_MULTIPLIER:
                this.movementSpeedMultiplier = par2;
                break;

            case HUD_OPACITY:
                this.hudOpacity = par2;

            case RENDER_MENU_BACKGROUND:
            case MIRROR_DISPLAY:
            case MIRROR_EYE:
            case MIXED_REALITY_KEY_COLOR:
            case MIXED_REALITY_RENDER_HANDS:
            case MIXED_REALITY_UNITY_LIKE:
            case MIXED_REALITY_UNDISTORTED:
            case MIXED_REALITY_ALPHA_MASK:
            case WALK_UP_BLOCKS:
            case HUD_LOCK_TO:
            case HUD_HIDE:
            case FSAA:
            case CROSSHAIR_SCALES_WITH_DISTANCE:
            case RENDER_CROSSHAIR_MODE:
            case RENDER_BLOCK_OUTLINE_MODE:
            case CHAT_NOTIFICATIONS:
            case CHAT_NOTIFICATION_SOUND:
            case GUI_APPEAR_OVER_BLOCK:
            case HUD_OCCLUSION:
            case MENU_ALWAYS_FOLLOW_FACE:
            case CROSSHAIR_OCCLUSION:
            case INERTIA_FACTOR:
            case SIMULATE_FALLING:
            case WEAPON_COLLISION:
            case ALLOW_CRAWLING:
            case LIMIT_TELEPORT:
            case REVERSE_HANDS:
            case STENCIL_ON:
            case BCB_ON:
            case TOUCH_HOTBAR:
            case PLAY_MODE_SEATED:
            case REALISTIC_JUMP:
            case SEATED_HMD:
            case SEATED_HUD_XHAIR:
            case REALISTIC_SNEAK:
            case PHYSICAL_GUI:
            case REALISTIC_CLIMB:
            case REALISTIC_SWIM:
            case REALISTIC_ROW:
            case VEHICLE_ROTATION:
            case RESET_ORIGIN:
            case FREEMOVE_MODE:
            case FOV_REDUCTION:
            case AUTO_OPEN_KEYBOARD:
            case BACKPACK_SWITCH:
            case ANALOG_MOVEMENT:
            case AUTO_SPRINT:
            case RADIAL_MODE_HOLD:
            case PHYSICAL_KEYBOARD:
            case BOW_MODE:
            case ALLOW_STANDING_ORIGIN_OFFSET:
            case SEATED_FREE_MOVE:
            case FORCE_STANDING_FREE_MOVE:
            case ALLOW_ADVANCED_BINDINGS:
            case MENU_WORLD_SELECTION:
            case HRTF_SELECTION:
            case RIGHT_CLICK_DELAY:
            default:
                break;

            case MIXED_REALITY_FOV:
                this.mixedRealityFov = par2;
                break;

            case HUD_SCALE:
                this.hudScale = par2;
                break;

            case HUD_DISTANCE:
                this.hudDistance = par2;
                break;

            case RENDER_SCALEFACTOR:
                this.renderScaleFactor = par2;
                break;

            case CROSSHAIR_SCALE:
                this.crosshairScale = par2;
                break;

            case MENU_CROSSHAIR_SCALE:
                this.menuCrosshairScale = par2;
                break;

            case MONO_FOV:
                this.mc.options.fov = (double)par2;
                break;

            case WORLD_SCALE:
                this.mc.vrPlayer.roomScaleMovementDelay = 2;

                if (par2 == 0.0F)
                {
                    this.vrWorldScale = 0.1F;
                }
                else if (par2 == 1.0F)
                {
                    this.vrWorldScale = 0.25F;
                }
                else if (par2 >= 2.0F && par2 <= 17.0F)
                {
                    this.vrWorldScale = (float)((double)par2 * 0.1D + 0.3D);
                }
                else if (par2 == 18.0F)
                {
                    this.vrWorldScale = 3.0F;
                }
                else if (par2 == 19.0F)
                {
                    this.vrWorldScale = 4.0F;
                }
                else if (par2 == 20.0F)
                {
                    this.vrWorldScale = 6.0F;
                }
                else if (par2 == 21.0F)
                {
                    this.vrWorldScale = 8.0F;
                }
                else if (par2 == 22.0F)
                {
                    this.vrWorldScale = 10.0F;
                }
                else if (par2 == 23.0F)
                {
                    this.vrWorldScale = 12.0F;
                }
                else if (par2 == 24.0F)
                {
                    this.vrWorldScale = 16.0F;
                }
                else if (par2 == 25.0F)
                {
                    this.vrWorldScale = 20.0F;
                }
                else if (par2 == 26.0F)
                {
                    this.vrWorldScale = 30.0F;
                }
                else if (par2 == 27.0F)
                {
                    this.vrWorldScale = 50.0F;
                }
                else if (par2 == 28.0F)
                {
                    this.vrWorldScale = 75.0F;
                }
                else if (par2 == 29.0F)
                {
                    this.vrWorldScale = 100.0F;
                }
                else
                {
                    this.vrWorldScale = 1.0F;
                }

                this.vrWorldScale = Mth.clamp(this.vrWorldScale, this.overrides.getSetting(par1EnumOptions).getValueMin(), this.overrides.getSetting(par1EnumOptions).getValueMax());
                this.mc.vrPlayer.snapRoomOriginToPlayerEntity(this.mc.player, false, true);
                break;

            case WORLD_ROTATION:
                this.vrWorldRotation = par2;
                this.mc.vr.seatedRot = par2;
                break;

            case WORLD_ROTATION_INCREMENT:
                this.vrWorldRotation = 0.0F;

                if (par2 == -1.0F)
                {
                    this.vrWorldRotationIncrement = 0.0F;
                }

                if (par2 == 0.0F)
                {
                    this.vrWorldRotationIncrement = 10.0F;
                }

                if (par2 == 1.0F)
                {
                    this.vrWorldRotationIncrement = 36.0F;
                }

                if (par2 == 2.0F)
                {
                    this.vrWorldRotationIncrement = 45.0F;
                }

                if (par2 == 3.0F)
                {
                    this.vrWorldRotationIncrement = 90.0F;
                }

                if (par2 == 4.0F)
                {
                    this.vrWorldRotationIncrement = 180.0F;
                }

                break;

            case WALK_MULTIPLIER:
                this.walkMultiplier = par2;
                break;

            case X_SENSITIVITY:
                this.xSensitivity = par2;
                break;

            case Y_SENSITIVITY:
                this.ySensitivity = par2;
                break;

            case KEYHOLE:
                this.keyholeX = par2;
                break;

            case FOV_REDUCTION_MIN:
                this.fovReductionMin = par2;
                break;

            case FOV_REDUCTION_OFFSET:
                this.fovRedutioncOffset = par2;
                break;

            case AUTO_SPRINT_THRESHOLD:
                this.autoSprintThreshold = par2;
                break;

            case PHYSICAL_KEYBOARD_SCALE:
                this.physicalKeyboardScale = par2;
                break;

            case TELEPORT_UP_LIMIT:
                this.vrTeleportUpLimit = (int)par2;
                break;

            case TELEPORT_DOWN_LIMIT:
                this.vrTeleportDownLimit = (int)par2;
                break;

            case TELEPORT_HORIZ_LIMIT:
                this.vrTeleportHorizLimit = (int)par2;
                break;

            case HANDHELD_CAMERA_FOV:
                this.handCameraFov = par2;
                break;

            case HANDHELD_CAMERA_RENDER_SCALE:
                this.handCameraResScale = par2;
        }

        this.saveOptions();
    }

    public void saveOptions()
    {
        this.saveOptions((JSONObject)null);
    }

    private void storeDefaults()
    {
        this.saveOptions(this.defaults);
    }

    private void saveOptions(JSONObject theProfiles)
    {
        try
        {
            ProfileWriter profilewriter = new ProfileWriter("Vr", theProfiles);

            if (this.preservedSettingMap != null)
            {
                profilewriter.setData(this.preservedSettingMap);
            }

            profilewriter.println("version:" + this.version);
            profilewriter.println("newlyCreated:false");
            profilewriter.println("stereoProviderPluginID:" + this.stereoProviderPluginID);
            profilewriter.println("badStereoProviderPluginID:" + this.badStereoProviderPluginID);
            profilewriter.println("hudOpacity:" + this.hudOpacity);
            profilewriter.println("menuBackground:" + this.menuBackground);
            profilewriter.println("renderFullFirstPersonModelMode:" + this.renderFullFirstPersonModelMode);
            profilewriter.println("shaderIndex:" + this.shaderIndex);
            profilewriter.println("displayMirrorMode:" + this.displayMirrorMode);
            profilewriter.println("displayMirrorLeftEye:" + this.displayMirrorLeftEye);
            profilewriter.println("mixedRealityKeyColor:" + this.mixedRealityKeyColor.getRed() + "," + this.mixedRealityKeyColor.getGreen() + "," + this.mixedRealityKeyColor.getBlue());
            profilewriter.println("mixedRealityRenderHands:" + this.mixedRealityRenderHands);
            profilewriter.println("mixedRealityUnityLike:" + this.mixedRealityUnityLike);
            profilewriter.println("mixedRealityUndistorted:" + this.mixedRealityMRPlusUndistorted);
            profilewriter.println("mixedRealityAlphaMask:" + this.mixedRealityAlphaMask);
            profilewriter.println("mixedRealityFov:" + this.mixedRealityFov);
            profilewriter.println("insideBlockSolidColor:" + this.insideBlockSolidColor);
            profilewriter.println("walkUpBlocks:" + this.walkUpBlocks);
            profilewriter.println("headHudScale:" + this.hudScale);
            profilewriter.println("renderScaleFactor:" + this.renderScaleFactor);
            profilewriter.println("vrHudLockMode:" + this.vrHudLockMode);
            profilewriter.println("hudDistance:" + this.hudDistance);
            profilewriter.println("hudPitchOffset:" + this.hudPitchOffset);
            profilewriter.println("hudYawOffset:" + this.hudYawOffset);
            profilewriter.println("useFsaa:" + this.useFsaa);
            profilewriter.println("movementSpeedMultiplier:" + this.movementSpeedMultiplier);
            profilewriter.println("renderInGameCrosshairMode:" + this.renderInGameCrosshairMode);
            profilewriter.println("renderBlockOutlineMode:" + this.renderBlockOutlineMode);
            profilewriter.println("hudOcclusion:" + this.hudOcclusion);
            profilewriter.println("menuAlwaysFollowFace:" + this.menuAlwaysFollowFace);
            profilewriter.println("useCrosshairOcclusion:" + this.useCrosshairOcclusion);
            profilewriter.println("crosshairScale:" + this.crosshairScale);
            profilewriter.println("menuCrosshairScale:" + this.menuCrosshairScale);
            profilewriter.println("crosshairScalesWithDistance:" + this.crosshairScalesWithDistance);
            profilewriter.println("inertiaFactor:" + this.inertiaFactor);
            profilewriter.println("smoothRunTickCount:" + this.smoothRunTickCount);
            profilewriter.println("smoothTick:" + this.smoothTick);
            profilewriter.println("simulateFalling:" + this.simulateFalling);
            profilewriter.println("weaponCollisionNew:" + this.weaponCollision);
            profilewriter.println("allowCrawling:" + this.vrAllowCrawling);
            profilewriter.println("limitedTeleport:" + this.vrLimitedSurvivalTeleport);
            profilewriter.println("reverseHands:" + this.vrReverseHands);
            profilewriter.println("stencilOn:" + this.vrUseStencil);
            profilewriter.println("bcbOn:" + this.vrShowBlueCircleBuddy);
            profilewriter.println("worldScale:" + this.vrWorldScale);
            profilewriter.println("worldRotation:" + this.vrWorldRotation);
            profilewriter.println("vrWorldRotationIncrement:" + this.vrWorldRotationIncrement);
            profilewriter.println("vrFixedCamposX:" + this.vrFixedCamposX);
            profilewriter.println("vrFixedCamposY:" + this.vrFixedCamposY);
            profilewriter.println("vrFixedCamposZ:" + this.vrFixedCamposZ);
            profilewriter.println("vrFixedCamrotW:" + this.vrFixedCamrotQuat.w);
            profilewriter.println("vrFixedCamrotX:" + this.vrFixedCamrotQuat.x);
            profilewriter.println("vrFixedCamrotY:" + this.vrFixedCamrotQuat.y);
            profilewriter.println("vrFixedCamrotZ:" + this.vrFixedCamrotQuat.z);
            profilewriter.println("mrMovingCamOffsetX:" + this.mrMovingCamOffsetX);
            profilewriter.println("mrMovingCamOffsetY:" + this.mrMovingCamOffsetY);
            profilewriter.println("mrMovingCamOffsetZ:" + this.mrMovingCamOffsetZ);
            profilewriter.println("mrMovingCamOffsetRotW:" + this.mrMovingCamOffsetRotQuat.w);
            profilewriter.println("mrMovingCamOffsetRotX:" + this.mrMovingCamOffsetRotQuat.x);
            profilewriter.println("mrMovingCamOffsetRotY:" + this.mrMovingCamOffsetRotQuat.y);
            profilewriter.println("mrMovingCamOffsetRotZ:" + this.mrMovingCamOffsetRotQuat.z);
            profilewriter.println("externalCameraAngleOrder:" + this.externalCameraAngleOrder.name());
            profilewriter.println("vrTouchHotbar:" + this.vrTouchHotbar);
            profilewriter.println("seatedhmd:" + this.seatedUseHMD);
            profilewriter.println("seatedHudAltMode:" + this.seatedHudAltMode);
            profilewriter.println("seated:" + this.seated);
            profilewriter.println("jumpThreshold:" + this.jumpThreshold);
            profilewriter.println("sneakThreshold:" + this.sneakThreshold);
            profilewriter.println("realisticJumpEnabled:" + this.realisticJumpEnabled);
            profilewriter.println("realisticSwimEnabled:" + this.realisticSwimEnabled);
            profilewriter.println("realisticClimbEnabled:" + this.realisticClimbEnabled);
            profilewriter.println("realisticRowEnabled:" + this.realisticRowEnabled);
            profilewriter.println("realisticSneakEnabled:" + this.realisticSneakEnabled);
            profilewriter.println("physicalGuiEnabled:" + this.physicalGuiEnabled);
            profilewriter.println("headToHmdLength:" + this.headToHmdLength);
            profilewriter.println("walkMultiplier:" + this.walkMultiplier);
            profilewriter.println("vrFreeMoveMode:" + this.vrFreeMoveMode);
            profilewriter.println("xSensitivity:" + this.xSensitivity);
            profilewriter.println("ySensitivity:" + this.ySensitivity);
            profilewriter.println("keyholeX:" + this.keyholeX);
            profilewriter.println("autoCalibration:" + this.autoCalibration);
            profilewriter.println("manualCalibration:" + this.manualCalibration);
            profilewriter.println("vehicleRotation:" + this.vehicleRotation);
            profilewriter.println("fovReduction:" + this.useFOVReduction);
            profilewriter.println("fovReductionMin:" + this.fovReductionMin);
            profilewriter.println("fovRedutioncOffset:" + this.fovRedutioncOffset);
            profilewriter.println("alwaysSimulateKeyboard:" + this.alwaysSimulateKeyboard);
            profilewriter.println("autoOpenKeyboard:" + this.autoOpenKeyboard);
            profilewriter.println("forceHardwareDetection:" + this.forceHardwareDetection);
            profilewriter.println("backpackSwitching:" + this.backpackSwitching);
            profilewriter.println("analogMovement:" + this.analogMovement);
            profilewriter.println("hideGUI:" + this.mc.options.hideGui);
            profilewriter.println("bowMode:" + this.bowMode);
            profilewriter.println("keyboardKeys:" + this.keyboardKeys);
            profilewriter.println("keyboardKeysShift:" + this.keyboardKeysShift);
            profilewriter.println("teleportLimitUp:" + this.vrTeleportUpLimit);
            profilewriter.println("teleportLimitDown:" + this.vrTeleportDownLimit);
            profilewriter.println("teleportLimitHoriz:" + this.vrTeleportHorizLimit);
            profilewriter.println("radialModeHold:" + this.radialModeHold);
            profilewriter.println("physicalKeyboard:" + this.physicalKeyboard);
            profilewriter.println("physicalKeyboardScale:" + this.physicalKeyboardScale);
            profilewriter.println("originOffset:" + this.mc.vr.offset.getX() + "," + this.mc.vr.offset.getY() + "," + this.mc.vr.offset.getZ());
            profilewriter.println("allowStandingOriginOffset:" + this.allowStandingOriginOffset);
            profilewriter.println("seatedFreeMove:" + this.seatedFreeMove);
            profilewriter.println("forceStandingFreeMove:" + this.forceStandingFreeMove);
            profilewriter.println("allowAdvancedBindings:" + this.allowAdvancedBindings);
            profilewriter.println("menuWorldSelection:" + this.menuWorldSelection);
            profilewriter.println("chatNotifications:" + this.chatNotifications);
            profilewriter.println("chatNotificationSound:" + this.chatNotificationSound);
            profilewriter.println("autoSprint:" + this.autoSprint);
            profilewriter.println("autoSprintThreshold:" + this.autoSprintThreshold);
            profilewriter.println("hrtfSelection:" + this.hrtfSelection);
            profilewriter.println("rightclickDelay:" + this.rightclickDelay);
            profilewriter.println("guiAppearOverBlock:" + this.guiAppearOverBlock);
            profilewriter.println("handCameraFov:" + this.handCameraFov);
            profilewriter.println("handCameraResScale:" + this.handCameraResScale);
            profilewriter.println("mixedRealityRenderCameraModel:" + this.mixedRealityRenderCameraModel);
            profilewriter.println("firstRun:" + this.firstRun);

            if (this.vrQuickCommands == null)
            {
                this.vrQuickCommands = this.getQuickCommandsDefaults();
            }

            for (int i = 0; i < 11; ++i)
            {
                profilewriter.println("QUICKCOMMAND_" + i + ":" + this.vrQuickCommands[i]);
            }

            if (this.vrRadialItems == null)
            {
                this.vrRadialItems = this.getRadialItemsDefault();
            }

            for (int j = 0; j < 8; ++j)
            {
                profilewriter.println("RADIAL_" + j + ":" + this.vrRadialItems[j]);
            }

            if (this.vrRadialItemsAlt == null)
            {
                this.vrRadialItemsAlt = new String[8];
            }

            for (int k = 0; k < 8; ++k)
            {
                profilewriter.println("RADIALALT_" + k + ":" + this.vrRadialItemsAlt[k]);
            }

            profilewriter.close();
        }
        catch (Exception exception)
        {
            logger.warn("Failed to save VR options: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private float parseFloat(String par1Str)
    {
        return par1Str.equals("true") ? 1.0F : (par1Str.equals("false") ? 0.0F : Float.parseFloat(par1Str));
    }

    public float getHeadTrackSensitivity()
    {
        return 1.0F;
    }

    public static double getInertiaAddFactor(int inertiaFactor)
    {
        float f = 1.0F;

        switch (inertiaFactor)
        {
            case 0:
                f = 100.0F;

            case 1:
            default:
                break;

            case 2:
                f = 0.25F;
                break;

            case 3:
                f = 0.0625F;
        }

        return (double)f;
    }

    public static synchronized void initSettings(Minecraft mc, File dataDir)
    {
        ProfileManager.init(dataDir);
        mc.options = new Options(mc, dataDir);
        mc.vrSettings = new VRSettings(mc, dataDir);
        mc.vrSettings.saveOptions();
    }

    public static synchronized void loadAll(Minecraft mc)
    {
        mc.options.load();
        mc.vrSettings.loadOptions();
    }

    public static synchronized void saveAll(Minecraft mc)
    {
        mc.options.save();
        mc.vrSettings.saveOptions();
    }

    public static synchronized void resetAll(Minecraft mc)
    {
        mc.options.resetSettings();
        mc.vrSettings.resetSettings();
    }

    public static synchronized String getCurrentProfile()
    {
        return ProfileManager.getCurrentProfileName();
    }

    public static synchronized boolean profileExists(String profile)
    {
        return ProfileManager.profileExists(profile);
    }

    public static synchronized SortedSet<String> getProfileList()
    {
        return ProfileManager.getProfileList();
    }

    public static synchronized boolean setCurrentProfile(String profile)
    {
        StringBuilder stringbuilder = new StringBuilder();
        return setCurrentProfile(profile, stringbuilder);
    }

    public static synchronized boolean setCurrentProfile(String profile, StringBuilder error)
    {
        boolean flag = true;
        Minecraft minecraft = Minecraft.getInstance();
        saveAll(minecraft);
        flag = ProfileManager.setCurrentProfile(profile, error);

        if (flag)
        {
            loadAll(minecraft);
        }

        return flag;
    }

    public static synchronized boolean createProfile(String profile, boolean useDefaults, StringBuilder error)
    {
        boolean flag = true;
        Minecraft minecraft = Minecraft.getInstance();
        String s = getCurrentProfile();
        saveAll(minecraft);

        if (!ProfileManager.createProfile(profile, error))
        {
            return false;
        }
        else
        {
            ProfileManager.setCurrentProfile(profile, error);

            if (useDefaults)
            {
                resetAll(minecraft);
            }

            saveAll(minecraft);
            ProfileManager.setCurrentProfile(s, error);
            loadAll(minecraft);
            return flag;
        }
    }

    public static synchronized boolean deleteProfile(String profile)
    {
        StringBuilder stringbuilder = new StringBuilder();
        return deleteProfile(profile, stringbuilder);
    }

    public static synchronized boolean deleteProfile(String profile, StringBuilder error)
    {
        Minecraft minecraft = Minecraft.getInstance();
        saveAll(minecraft);

        if (!ProfileManager.deleteProfile(profile, error))
        {
            return false;
        }
        else
        {
            loadAll(minecraft);
            return true;
        }
    }

    public static synchronized boolean duplicateProfile(String originalProfile, String newProfile, StringBuilder error)
    {
        Minecraft minecraft = Minecraft.getInstance();
        saveAll(minecraft);
        return ProfileManager.duplicateProfile(originalProfile, newProfile, error);
    }

    public static synchronized boolean renameProfile(String originalProfile, String newProfile, StringBuilder error)
    {
        Minecraft minecraft = Minecraft.getInstance();
        saveAll(minecraft);
        return ProfileManager.renameProfile(originalProfile, newProfile, error);
    }

    public String[] getQuickCommandsDefaults()
    {
        return new String[] {"/gamemode survival", "/gamemode creative", "/help", "/home", "/sethome", "/spawn", "hi!", "bye!", "follow me!", "take this!", "thank you!", "praise the sun!"};
    }

    public String[] getRadialItemsDefault()
    {
        return new String[] {"key.drop", "key.chat", "vivecraft.key.rotateRight", "key.pickItem", "vivecraft.key.toggleHandheldCam", "vivecraft.key.togglePlayerList", "vivecraft.key.rotateLeft", "vivecraft.key.quickTorch"};
    }

    public double normalizeValue(float optionFloatValue)
    {
        return 0.0D;
    }

    public class ServerOverrides
    {
        private Map<VRSettings.VrOptions, VRSettings.ServerOverrides.Setting> optionMap = new EnumMap<>(VRSettings.VrOptions.class);
        private Map<String, VRSettings.ServerOverrides.Setting> networkNameMap = new HashMap<>();

        private ServerOverrides()
        {
            this.registerSetting(VRSettings.VrOptions.LIMIT_TELEPORT, "limitedTeleport", () ->
            {
                return VRSettings.this.vrLimitedSurvivalTeleport;
            });
            this.registerSetting(VRSettings.VrOptions.TELEPORT_UP_LIMIT, "teleportLimitUp", () ->
            {
                return VRSettings.this.vrTeleportUpLimit;
            });
            this.registerSetting(VRSettings.VrOptions.TELEPORT_DOWN_LIMIT, "teleportLimitDown", () ->
            {
                return VRSettings.this.vrTeleportDownLimit;
            });
            this.registerSetting(VRSettings.VrOptions.TELEPORT_HORIZ_LIMIT, "teleportLimitHoriz", () ->
            {
                return VRSettings.this.vrTeleportHorizLimit;
            });
            this.registerSetting(VRSettings.VrOptions.WORLD_SCALE, "worldScale", () ->
            {
                return VRSettings.this.vrWorldScale;
            });
        }

        private void registerSetting(VRSettings.VrOptions option, String networkName, Supplier<Object> originalValue)
        {
            VRSettings.ServerOverrides.Setting vrsettings$serveroverrides$setting = new VRSettings.ServerOverrides.Setting(option, networkName, originalValue);
            this.optionMap.put(option, vrsettings$serveroverrides$setting);
            this.networkNameMap.put(networkName, vrsettings$serveroverrides$setting);
        }

        public void resetAll()
        {
            for (VRSettings.ServerOverrides.Setting vrsettings$serveroverrides$setting : this.optionMap.values())
            {
                vrsettings$serveroverrides$setting.valueSet = false;
                vrsettings$serveroverrides$setting.valueMinSet = false;
                vrsettings$serveroverrides$setting.valueMaxSet = false;
            }
        }

        public boolean hasSetting(VRSettings.VrOptions option)
        {
            return this.optionMap.containsKey(option);
        }

        public boolean hasSetting(String networkName)
        {
            return this.networkNameMap.containsKey(networkName);
        }

        public VRSettings.ServerOverrides.Setting getSetting(VRSettings.VrOptions option)
        {
            VRSettings.ServerOverrides.Setting vrsettings$serveroverrides$setting = this.optionMap.get(option);

            if (vrsettings$serveroverrides$setting == null)
            {
                throw new IllegalArgumentException("setting not registered: " + option);
            }
            else
            {
                return vrsettings$serveroverrides$setting;
            }
        }

        public VRSettings.ServerOverrides.Setting getSetting(String networkName)
        {
            VRSettings.ServerOverrides.Setting vrsettings$serveroverrides$setting = this.networkNameMap.get(networkName);

            if (vrsettings$serveroverrides$setting == null)
            {
                throw new IllegalArgumentException("setting not registered: " + networkName);
            }
            else
            {
                return vrsettings$serveroverrides$setting;
            }
        }

        public class Setting
        {
            private final VRSettings.VrOptions option;
            private final String networkName;
            private final Supplier<Object> originalValue;
            private boolean valueSet;
            private Object value;
            private boolean valueMinSet;
            private boolean valueMaxSet;
            private float valueMin;
            private float valueMax;

            public Setting(VRSettings.VrOptions option, String networkName, Supplier<Object> originalValue)
            {
                this.option = option;
                this.networkName = networkName;
                this.originalValue = originalValue;
            }

            private void checkFloat()
            {
                if (!this.option.enumFloat)
                {
                    throw new IllegalArgumentException("not a float option: " + this.option);
                }
            }

            public boolean isFloat()
            {
                return this.option.enumFloat;
            }

            public Object getOriginalValue()
            {
                return this.originalValue.get();
            }

            public boolean isValueOverridden()
            {
                return this.valueSet;
            }

            public Object getValue()
            {
                return this.valueSet ? this.value : this.originalValue.get();
            }

            public boolean getBoolean()
            {
                Object object = this.getValue();
                return object instanceof Boolean ? (Boolean)object : false;
            }

            public int getInt()
            {
                Object object = this.getValue();
                return object instanceof Number ? Mth.clamp(((Number)object).intValue(), (int)this.getValueMin(), (int)this.getValueMax()) : 0;
            }

            public float getFloat()
            {
                Object object = this.getValue();
                return object instanceof Number ? Mth.clamp(((Number)object).floatValue(), this.getValueMin(), this.getValueMax()) : 0.0F;
            }

            public String getString()
            {
                Object object = this.getValue();
                return object instanceof String ? object.toString() : "";
            }

            public void setValue(Object value)
            {
                this.value = value;
                this.valueSet = true;
            }

            public void resetValue()
            {
                this.valueSet = false;
            }

            public boolean isValueMinOverridden()
            {
                this.checkFloat();
                return this.valueMinSet;
            }

            public float getValueMin()
            {
                this.checkFloat();
                return this.valueMinSet ? this.valueMin : Float.MIN_VALUE;
            }

            public void setValueMin(float valueMin)
            {
                this.checkFloat();
                this.valueMin = valueMin;
                this.valueMinSet = true;
            }

            public void resetValueMin()
            {
                this.checkFloat();
                this.valueMinSet = false;
            }

            public boolean isValueMaxOverridden()
            {
                this.checkFloat();
                return this.valueMaxSet;
            }

            public float getValueMax()
            {
                this.checkFloat();
                return this.valueMaxSet ? this.valueMax : Float.MAX_VALUE;
            }

            public void setValueMax(float valueMax)
            {
                this.checkFloat();
                this.valueMax = valueMax;
                this.valueMaxSet = true;
            }

            public void resetValueMax()
            {
                this.checkFloat();
                this.valueMaxSet = false;
            }
        }
    }

    public static enum VrOptions
    {
        DUMMY(false, true),
        HUD_SCALE(true, false, 0.35F, 2.5F, 0.01F),
        HUD_DISTANCE(true, false, 0.25F, 5.0F, 0.01F),
        HUD_LOCK_TO(false, true),
        HUD_OPACITY(true, false, 0.15F, 1.0F, 0.05F),
        HUD_HIDE(false, true),
        RENDER_MENU_BACKGROUND(false, true),
        HUD_OCCLUSION(false, true),
        MENU_ALWAYS_FOLLOW_FACE(false, true),
        CROSSHAIR_OCCLUSION(false, true),
        CROSSHAIR_SCALE(true, false, 0.25F, 1.0F, 0.01F),
        MENU_CROSSHAIR_SCALE(true, false, 0.25F, 2.5F, 0.05F),
        RENDER_CROSSHAIR_MODE(false, true),
        CHAT_NOTIFICATIONS(false, true),
        CHAT_NOTIFICATION_SOUND(false, true),
        CROSSHAIR_SCALES_WITH_DISTANCE(false, true),
        RENDER_BLOCK_OUTLINE_MODE(false, true),
        AUTO_OPEN_KEYBOARD(false, true),
        RADIAL_MODE_HOLD(false, true),
        PHYSICAL_KEYBOARD(false, true),
        PHYSICAL_KEYBOARD_SCALE(true, false, 0.75F, 1.5F, 0.01F),
        GUI_APPEAR_OVER_BLOCK(false, true),
        FSAA(false, true),
        MIRROR_DISPLAY(false, true),
        MIRROR_EYE(false, true),
        MIXED_REALITY_KEY_COLOR(false, false),
        MIXED_REALITY_RENDER_HANDS(false, true),
        MIXED_REALITY_UNITY_LIKE(false, true),
        MIXED_REALITY_UNDISTORTED(false, true),
        MIXED_REALITY_ALPHA_MASK(false, true),
        MIXED_REALITY_FOV(true, false, 0.0F, 179.0F, 1.0F),
        WALK_UP_BLOCKS(false, true),
        MOVEMENT_MULTIPLIER(true, false, 0.15F, 1.3F, 0.01F),
        INERTIA_FACTOR(false, true),
        SIMULATE_FALLING(false, true),
        WEAPON_COLLISION(false, true),
        ALLOW_CRAWLING(false, true),
        LIMIT_TELEPORT(false, true),
        REVERSE_HANDS(false, true),
        STENCIL_ON(false, true),
        BCB_ON(false, true),
        WORLD_SCALE(true, false, 0.0F, 29.0F, 1.0F),
        WORLD_ROTATION(true, false, 0.0F, 360.0F, 30.0F),
        WORLD_ROTATION_INCREMENT(true, false, -1.0F, 4.0F, 1.0F),
        TOUCH_HOTBAR(false, true),
        PLAY_MODE_SEATED(false, true),
        RENDER_SCALEFACTOR(true, false, 0.1F, 9.0F, 0.1F),
        MONO_FOV(true, false, 0.0F, 179.0F, 1.0F),
        HANDHELD_CAMERA_FOV(true, false, 0.0F, 179.0F, 1.0F),
        HANDHELD_CAMERA_RENDER_SCALE(true, false, 0.5F, 3.0F, 0.25F),
        MIXED_REALITY_RENDER_CAMERA_MODEL(false, true),
        REALISTIC_JUMP(false, true),
        REALISTIC_SNEAK(false, true),
        PHYSICAL_GUI(false, true),
        REALISTIC_CLIMB(false, true),
        REALISTIC_SWIM(false, true),
        REALISTIC_ROW(false, true),
        WALK_MULTIPLIER(true, false, 1.0F, 10.0F, 0.1F),
        FREEMOVE_MODE(false, true),
        VEHICLE_ROTATION(false, true),
        RESET_ORIGIN(false, true),
        X_SENSITIVITY(true, false, 0.1F, 5.0F, 0.01F),
        Y_SENSITIVITY(true, false, 0.1F, 5.0F, 0.01F),
        KEYHOLE(true, false, 0.0F, 40.0F, 5.0F),
        FOV_REDUCTION(false, true),
        FOV_REDUCTION_MIN(true, false, 0.1F, 0.7F, 0.05F),
        FOV_REDUCTION_OFFSET(true, false, 0.0F, 0.3F, 0.01F),
        SEATED_HMD(false, true),
        SEATED_HUD_XHAIR(false, true),
        BACKPACK_SWITCH(false, true),
        ANALOG_MOVEMENT(false, true),
        AUTO_SPRINT(false, true),
        AUTO_SPRINT_THRESHOLD(true, false, 0.5F, 1.0F, 0.01F),
        BOW_MODE(false, true),
        TELEPORT_DOWN_LIMIT(true, false, 0.0F, 16.0F, 1.0F),
        TELEPORT_UP_LIMIT(true, false, 0.0F, 4.0F, 1.0F),
        TELEPORT_HORIZ_LIMIT(true, false, 0.0F, 32.0F, 1.0F),
        ALLOW_STANDING_ORIGIN_OFFSET(false, true),
        SEATED_FREE_MOVE(false, true),
        FORCE_STANDING_FREE_MOVE(false, true),
        ALLOW_ADVANCED_BINDINGS(false, true),
        MENU_WORLD_SELECTION(false, false),
        HRTF_SELECTION(false, false),
        RELOAD_EXTERNAL_CAMERA(false, false),
        RIGHT_CLICK_DELAY(false, false);

        private final boolean enumFloat;
        private final boolean enumBoolean;
        private final float valueStep;
        private final float valueMin;
        private final float valueMax;

        public static VRSettings.VrOptions getEnumOptions(int par0)
        {
            for (VRSettings.VrOptions vrsettings$vroptions : values())
            {
                if (vrsettings$vroptions.returnEnumOrdinal() == par0)
                {
                    return vrsettings$vroptions;
                }
            }

            return null;
        }

        private VrOptions(boolean isfloat, boolean isbool)
        {
            this(isfloat, isbool, 0.0F, 1.0F, 0.0F);

            if (isfloat)
            {
                boolean flag = false;
            }
        }

        private VrOptions(boolean isfloat, boolean isboolean, float min, float max, float step)
        {
            this.enumFloat = isfloat;
            this.enumBoolean = isboolean;
            this.valueMin = min;
            this.valueMax = max;
            this.valueStep = step;
        }

        public boolean getEnumFloat()
        {
            return this.enumFloat;
        }

        public boolean getEnumBoolean()
        {
            return this.enumBoolean;
        }

        public int returnEnumOrdinal()
        {
            return this.ordinal();
        }

        public float getValueMax()
        {
            return this.valueMax;
        }

        public float getValueMin()
        {
            return this.valueMin;
        }

        protected float snapToStep(float p_148264_1_)
        {
            if (this.valueStep > 0.0F)
            {
                p_148264_1_ = this.valueStep * (float)Math.round(p_148264_1_ / this.valueStep);
            }

            return p_148264_1_;
        }

        public double normalizeValue(float value)
        {
            return Mth.clamp((double)((this.snapToStep(value) - this.valueMin) / (this.valueMax - this.valueMin)), 0.0D, 1.0D);
        }

        public double denormalizeValue(float value)
        {
            return (double)this.snapToStep((float)((double)this.valueMin + (double)(this.valueMax - this.valueMin) * Mth.clamp((double)value, 0.0D, 1.0D)));
        }
    }
}
