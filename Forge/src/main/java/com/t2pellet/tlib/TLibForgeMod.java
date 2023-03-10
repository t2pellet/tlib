package com.t2pellet.tlib;

import com.t2pellet.tlib.client.TLibModClient;
import com.t2pellet.tlib.client.compat.ForgeConfigMenu;
import com.t2pellet.tlib.common.TLibMod;
import com.t2pellet.tlib.config.ConfigRegistrar;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class TLibForgeMod {

    // Mod references
    private final String modid;
    private final TLibMod commonMod;
    private final TLibModClient clientMod;

    // Deferred registries
    public final DeferredRegister<EntityType<?>> ENTITIES;
    public final DeferredRegister<ParticleType<?>> PARTICLES;
    public final DeferredRegister<SoundEvent> SOUNDS;
    public final DeferredRegister<Item> ITEMS;

    public TLibForgeMod() {
        TLibMod.IMod modAnnotation = getClass().getAnnotation(TLibMod.IMod.class);
        commonMod = TenzinLib.INSTANCE.get(modAnnotation.value());
        clientMod = TenzinLib.INSTANCE.getClient(modAnnotation.value());
        modid = modAnnotation.value();

        // Setup stages
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::onCommonSetup);
        bus.addListener(this::onClientSetup);
        // Create deferred registers
        ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, modid);
        ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, modid);
        PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, modid);
        SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, modid);
        // Pre-init
        CommonRegistrar.register(modid, commonMod.entities());
        CommonRegistrar.register(modid, commonMod.items());
        CommonRegistrar.register(modid, commonMod.particles());
        CommonRegistrar.register(modid, commonMod.sounds());
        // Register into deferred registers
        ENTITIES.register(bus);
        ITEMS.register(bus);
        PARTICLES.register(bus);
        SOUNDS.register(bus);
    }

    protected ForgeConfigMenu configMenu() {
        return null;
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        CommonRegistrar.register(modid, commonMod.packets());
        CommonRegistrar.register(modid, commonMod.capabilities());
        ConfigRegistrar.INSTANCE.register(modid, commonMod.config());
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        ClientRegistrar.register(modid, clientMod.entityModels());
        ClientRegistrar.register(modid, clientMod.particleFactories());
        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            ForgeConfigMenu menu = configMenu();
            if (menu != null) {
                menu.registerConfigMenu();
            }
        }
    }



}
