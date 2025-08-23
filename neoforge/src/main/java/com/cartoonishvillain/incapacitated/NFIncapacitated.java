package com.cartoonishvillain.incapacitated;

import com.cartoonishvillain.incapacitated.capability.PlayerCapability;
import com.cartoonishvillain.incapacitated.commands.*;
import com.cartoonishvillain.incapacitated.config.IncapacitatedClientConfig;
import com.cartoonishvillain.incapacitated.event.ReviveCheckEvent;
import com.cartoonishvillain.incapacitated.gui.IncapacitatedOverlay;
import com.cartoonishvillain.incapacitated.networking.IncapGiveUpPacketServerHandler;
import com.cartoonishvillain.incapacitated.networking.IncapPacketClientHandler;
import com.cartoonishvillain.incapacitated.networking.IncapPacketServerHandler;
import com.cartoonishvillain.incapacitated.platform.Services;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(Constants.MOD_ID)
public class NFIncapacitated {

    public static MinecraftServer server;

    public NFIncapacitated(IEventBus modEventBus, ModContainer modContainer) {
        Incapacitated.init();

        PlayerCapability.loadDataAttachment(modEventBus);
        NFIncapEffects.init(modEventBus);
        NFIncapStats.modConstruction(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, IncapacitatedClientConfig.CLIENTSPEC);

        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void serverTick(ServerTickEvent.Post event) {
        server = event.getServer();
    }

    @SubscribeEvent
    public void commandLoad(RegisterCommandsEvent event) {
        SetIncapacitatedCommand.register(event.getDispatcher());
        SetDownCount.register(event.getDispatcher());
        GetDownCount.register(event.getDispatcher());
        KillPlayer.register(event.getDispatcher());
        ConfigCommands.register(event.getDispatcher());
        SetDownTicks.register(event.getDispatcher());

        if (!FMLLoader.isProduction()) {
            IncapDevMode.register(event.getDispatcher());
        }
    }

    @SubscribeEvent
    public void keybindCheck(ClientTickEvent.Post event) {
        while (NFIncapKeybind.GiveUpKeybind.consumeClick()) {
            if (Services.PLATFORM.getPlayerData(Minecraft.getInstance().player).isIncapacitated()) {
                PacketDistributor.sendToServer(new NFIncapacitated.IncapGiveupPayload(Minecraft.getInstance().player.getId(), Minecraft.getInstance().player.getGameProfile()));
            }
        }
    }

    //@SubscribeEvent
    public void creativeCheckExampleEvent(ReviveCheckEvent event) {
        if (!event.getRevivingPlayer().isCreative()) {
            event.getRevivingPlayer().displayClientMessage(Component.literal("You need to be in creative to revive."), true);
            event.setCanceled(true);
        }
    }

    @EventBusSubscriber(modid = Constants.MOD_ID)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(NFIncapKeybind.GiveUpKeybind);
        }

        @SubscribeEvent
        public static void registerOverlay(final RegisterGuiLayersEvent event) {
            event.registerBelow(
                   VanillaGuiLayers.HOTBAR,
                    ResourceLocation.parse("incapacitated:down_counter"),
                    ((guiGraphics, deltaTracker) -> {
                        Minecraft minecraft = Minecraft.getInstance();
                        if (Services.PLATFORM.shouldShowGUIDownCounter() && !minecraft.options.hideGui && (minecraft.gameMode.getPlayerMode() == GameType.SURVIVAL || minecraft.gameMode.getPlayerMode() == GameType.ADVENTURE)) {
                            IncapacitatedOverlay.renderOverlay(guiGraphics);
                        }
                        //TODO CONFIG CHECK
                    })
            );
        }

        @SubscribeEvent
        public static void commonSetup(FMLCommonSetupEvent event) {
            NFIncapStats.setup();
        }

        @SubscribeEvent
        public static void onClientSetup(final RegisterPayloadHandlersEvent event) {
            final PayloadRegistrar registrar = event.registrar(Constants.MOD_ID);
            registrar.playBidirectional(
                    IncapPayload.TYPE,
                    IncapPayload.STREAM_CODEC,
                    new DirectionalPayloadHandler<>(
                            IncapPacketClientHandler::handleData,
                            IncapPacketServerHandler::handleData
                    )
            );

            registrar.playToServer(
                    IncapGiveupPayload.TYPE,
                    IncapGiveupPayload.STREAM_CODEC,
                    new DirectionalPayloadHandler<>(
                            IncapGiveUpPacketServerHandler::handleData,
                            IncapGiveUpPacketServerHandler::handleData
                    )
            );
        }
    }

    public record IncapPayload(int ID, boolean isIncapacitated, short downCount,
                               int downTicks) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<IncapPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "incap_payload"));

        public static final StreamCodec<ByteBuf, IncapPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                IncapPayload::ID,
                ByteBufCodecs.BOOL,
                IncapPayload::isIncapacitated,
                ByteBufCodecs.SHORT,
                IncapPayload::downCount,
                ByteBufCodecs.VAR_INT,
                IncapPayload::downTicks,
                IncapPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record IncapGiveupPayload(int ID, GameProfile gameProfile) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<IncapGiveupPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "incap_giveup_payload"));

        public static final StreamCodec<ByteBuf, IncapGiveupPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT,
                IncapGiveupPayload::ID,
                ByteBufCodecs.GAME_PROFILE,
                IncapGiveupPayload::gameProfile,
                IncapGiveupPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}