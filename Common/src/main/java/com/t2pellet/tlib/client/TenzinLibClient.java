package com.t2pellet.tlib.client;

import com.t2pellet.tlib.client.registry.TlibEntityRenderers;
import com.t2pellet.tlib.client.registry.TlibParticleFactories;
import com.t2pellet.tlib.registry.api.RegistryClass;

public class TenzinLibClient extends TLibModClient {

    public static final TenzinLibClient INSTANCE = new TenzinLibClient();

    @Override
    public Class<? extends RegistryClass> particleFactories() {
        return TlibParticleFactories.class;
    }

    @Override
    public Class<? extends RegistryClass> entityRenderers() {
        return TlibEntityRenderers.class;
    }
}
