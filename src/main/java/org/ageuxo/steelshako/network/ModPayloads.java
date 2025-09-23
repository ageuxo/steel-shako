package org.ageuxo.steelshako.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPayloads {

    public static final String NET_VERSION = "1";

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NET_VERSION);

        registrar.playToClient(
                BlockHeatUpdatePayload.TYPE,
                BlockHeatUpdatePayload.STREAM_CODEC,
                ClientPayloadHandler::handleBlockHeatUpdatePayload
        );


    }

}
