package com.t2pellet.tlib.common.entity.capability;

import net.minecraft.world.entity.Entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IModCapabilities {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface ICapability {
        Class<? extends Capability> value();
    }

    class TLibCapability<T extends Capability> {
        CapabilityRegistrar.CapabilityFactory<T> supplier;

        public TLibCapability(CapabilityRegistrar.CapabilityFactory<T> supplier) {
            this.supplier = supplier;
        }

        public <E extends Entity & ICapabilityHaver> T get(E entity) {
            return supplier.get(entity);
        }
    }

}
