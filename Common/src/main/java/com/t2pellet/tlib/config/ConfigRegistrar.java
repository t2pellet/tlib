package com.t2pellet.tlib.config;

import com.t2pellet.tlib.config.api.Config;

import java.io.IOException;
import java.util.Set;

public interface ConfigRegistrar {

    @FunctionalInterface
    interface ConfigSupplier<T extends Config> {
        T get() throws IOException, IllegalAccessException;
    }

    ConfigRegistrar INSTANCE = new ConfigRegistrarImpl();

    <T extends Config> void register(String modid, ConfigSupplier<T> configSupplier);

    <T extends Config> T get(String modid);

    Set<String> getAllRegistered();

}
