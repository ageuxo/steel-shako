package org.ageuxo.steelshako.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ChargeComponent(int charge, int maxCharge) {

    public static final Codec<ChargeComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("charge").forGetter(ChargeComponent::charge),
            Codec.INT.fieldOf("maxCharge").forGetter(ChargeComponent::maxCharge)
    ).apply(instance, ChargeComponent::new));

    public static final StreamCodec<ByteBuf, ChargeComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            ChargeComponent::charge,
            ByteBufCodecs.VAR_INT,
            ChargeComponent::maxCharge,
            ChargeComponent::new
    );

    public ChargeComponent sub(int amount) {
        return new ChargeComponent(Math.max(this.charge - amount, 0), this.maxCharge);
    }

    public ChargeComponent add(int amount) {
        return new ChargeComponent(Math.min(this.charge + amount, this.maxCharge), this.maxCharge);
    }

    public boolean isFull() {
        return charge() >= maxCharge();
    }


}
