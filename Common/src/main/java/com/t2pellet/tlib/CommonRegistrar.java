package com.t2pellet.tlib;

import com.t2pellet.tlib.common.entity.capability.Capability;
import com.t2pellet.tlib.common.entity.capability.CapabilityRegistrar;
import com.t2pellet.tlib.common.entity.capability.IModCapabilities;
import com.t2pellet.tlib.common.network.IModPackets;
import com.t2pellet.tlib.common.network.Packet;
import com.t2pellet.tlib.common.registry.IModEntities;
import com.t2pellet.tlib.common.registry.IModItems;
import com.t2pellet.tlib.common.registry.IModParticles;
import com.t2pellet.tlib.common.registry.IModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

class CommonRegistrar {

    private CommonRegistrar() {
    }

    public static void register(String modid, IModEntities entities) {
        for (Field field : entities.getClass().getDeclaredFields()) {
            IModEntities.IEntity entityInfo = field.getDeclaredAnnotation(IModEntities.IEntity.class);
            if (entityInfo != null && field.getType().equals(IModEntities.TLibEntity.class)) {
                try {
                    IModEntities.TLibEntity<? extends LivingEntity> entity = (IModEntities.TLibEntity<? extends LivingEntity>) field.get(null);
                    Field result = entity.getClass().getDeclaredField("TYPE");
                    setField(result, entity, Services.COMMON_REGISTRY.registerEntity(modid, entityInfo.name(), entity._factory, entityInfo.category(), entityInfo.width(), entityInfo.height(), entity._attributes));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void register(String modid, IModItems items) {
        for (Field field : items.getClass().getDeclaredFields()) {
            IModItems.IItem itemInfo = field.getDeclaredAnnotation(IModItems.IItem.class);
            if (itemInfo != null && field.getType().equals(IModItems.TLibItem.class)) {
                try {
                    IModItems.TLibItem item = (IModItems.TLibItem) field.get(null);
                    Field result = item.getClass().getDeclaredField("ITEM");
                    setField(result, item, Services.COMMON_REGISTRY.registerItem(modid, itemInfo.value(), item._properties));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void register(String modid, IModParticles particles) {
        for (Field field : particles.getClass().getDeclaredFields()) {
            IModParticles.IParticle particleInfo = field.getAnnotation(IModParticles.IParticle.class);
            if (particleInfo != null && field.getType().equals(IModParticles.TLibParticle.class)) {
                try {
                    IModParticles.TLibParticle particle = (IModParticles.TLibParticle) field.get(null);
                    Field result = particle.getClass().getDeclaredField("PARTICLE");
                    setField(result, particle, Services.COMMON_REGISTRY.registerParticle(modid, particleInfo.value()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void register(String modid, IModSounds sounds) {
        for (Field field : sounds.getClass().getDeclaredFields()) {
            IModSounds.ISound soundInfo = field.getAnnotation(IModSounds.ISound.class);
            if (soundInfo != null && field.getType().equals(SoundEvent.class)) {
                try {
                    SoundEvent sound = new SoundEvent(new ResourceLocation(modid, soundInfo.value()));
                    Services.COMMON_REGISTRY.registerSound(sound);
                    setField(field, null, sound);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void register(String modid, IModPackets packets) {
        for (Field field : packets.getClass().getDeclaredFields()) {
            IModPackets.IPacket packetInfo = field.getAnnotation(IModPackets.IPacket.class);
            if (packetInfo != null && field.getType().equals(IModPackets.TLibPacket.class)) {
                try {
                    IModPackets.TLibPacket<? extends Packet<?>> packet = (IModPackets.TLibPacket<? extends Packet<?>>) field.get(null);
                    if (packetInfo.client()) {
                        Services.PACKET_HANDLER.registerClientPacket(modid, packetInfo.name(), packet.PACKET);
                    } else {
                        Services.PACKET_HANDLER.registerServerPacket(modid, packetInfo.name(), packet.PACKET);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void register(String modid, IModCapabilities capabilities) {
        for (Field field : capabilities.getClass().getDeclaredFields()) {
            IModCapabilities.ICapability capabilityInfo = field.getAnnotation(IModCapabilities.ICapability.class);
            if (capabilityInfo != null && field.getType().equals(IModCapabilities.TLibCapability.class)) {
                try {
                    IModCapabilities.TLibCapability<? extends Capability> capability = (IModCapabilities.TLibCapability<? extends Capability>) field.get(null);
                    registerCapability(capabilityInfo.value(), capability::get);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Capability> void registerCapability(Class<? extends Capability> clazz, Supplier<T> supplier) {
        CapabilityRegistrar.INSTANCE.register((Class<T>) clazz, supplier);
    }

    private static void setField(Field field, Object object, Object value) {
        try {
            field.setAccessible(true);
            field.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(object, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
