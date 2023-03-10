package com.t2pellet.tlib.common;

import com.t2pellet.tlib.Services;
import com.t2pellet.tlib.TenzinLib;
import com.t2pellet.tlib.common.network.IPacketHandler;
import com.t2pellet.tlib.common.network.Packet;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ForgePacketHandler implements IPacketHandler {

    private final String PROTOCOL_VERSION = "4";
    private final Map<ResourceLocation, Integer> idMap = new HashMap<>();
    private final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TenzinLib.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );


    @Override
    public <T extends Packet<T>> void registerServerPacket(String modid, String name, Class<T> packetClass) {
        idMap.put(new ResourceLocation(modid, name), idMap.size());
        registerPacket(modid, name, packetClass);
    }

    @Override
    public <T extends Packet<T>> void registerClientPacket(String modid, String name, Class<T> packetClass) {
        idMap.put(new ResourceLocation(modid, name), idMap.size());
        registerPacket(modid, name, packetClass);
    }

    private <T extends Packet<T>> void registerPacket(String modid, String name, Class<T> packetClass) {
        ResourceLocation id = new ResourceLocation(modid, name);
        INSTANCE.registerMessage(idMap.get(id), packetClass, Packet::encode, friendlyByteBuf -> {
            try {
                return packetClass.getDeclaredConstructor(FriendlyByteBuf.class).newInstance(friendlyByteBuf);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                    InvocationTargetException ex) {
                TenzinLib.LOG.error("Error: Failed to instantiate packet - " + id);
            }
            return null;
        }, (t, contextSupplier) -> {
            if (contextSupplier.get().getDirection().getReceptionSide().isClient()) {
                Services.SIDE.scheduleClient(t.getExecutor());
            } else {
                Services.SIDE.scheduleServer(t.getExecutor());
            }
            contextSupplier.get().setPacketHandled(true);
        });
    }

    @Override
    public <T extends Packet<T>> void sendToServer(Packet<T> packet) {
        INSTANCE.sendToServer(packet);
    }

    @Override
    public <T extends Packet<T>> void sendTo(Packet<T> packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    @Override
    public <T extends Packet<T>> void sendTo(Packet<T> packet, ServerPlayer... players) {
        for (ServerPlayer player : players) {
            sendTo(packet, player);
        }
    }

    @Override
    public <T extends Packet<T>> void sendInRange(Packet<T> packet, Entity e, float range) {
        AABB box = new AABB(e.blockPosition());
        box.inflate(range);
        sendInArea(packet, e.getLevel(), box);
    }

    @Override
    public <T extends Packet<T>> void sendInArea(Packet<T> packet, Level world, AABB area) {

    }
}
