package com.cartoonishvillain.incapacitated.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class IncapacitationMessenger {
    private static final String ProtocolVersion = "1";
    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation("incapacitated", "main"),
            () -> ProtocolVersion, ProtocolVersion::equals, ProtocolVersion::equals);
    private static int ID = 0;

    public static IncapMessageEncoder messageEncoder = new IncapMessageEncoder();

    public static IncapMessageConsumer messageConsumer = new IncapMessageConsumer();

    public static void register() {
        INSTANCE.registerMessage(nextID(), IncapPacket.class, messageEncoder, IncapPacket::decode, messageConsumer);
    }

    public static void sendTo(Object message, Player player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), message);
    }

    private static int nextID(){
        return ID++;
    }




}
