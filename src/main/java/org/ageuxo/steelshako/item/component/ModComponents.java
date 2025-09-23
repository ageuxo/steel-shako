package org.ageuxo.steelshako.item.component;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.ageuxo.steelshako.SteelShakoMod;

public class ModComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SteelShakoMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ChargeComponent>> CHARGE = COMPONENTS.registerComponentType("charge",
            b -> b.networkSynchronized(ChargeComponent.STREAM_CODEC)
                    .persistent(ChargeComponent.CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RAY_RAMPUP = COMPONENTS.registerComponentType("ray_rampup",
            b -> b.networkSynchronized(ByteBufCodecs.VAR_INT)
    );

}
