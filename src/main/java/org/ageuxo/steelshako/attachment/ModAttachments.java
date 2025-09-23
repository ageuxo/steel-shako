package org.ageuxo.steelshako.attachment;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.ageuxo.steelshako.SteelShakoMod;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SteelShakoMod.MOD_ID);

    public static final Supplier<AttachmentType<MiningRayCache>> MINING_RAY_CACHE = ATTACHMENTS.register("mining_ray_cache",
            ()-> AttachmentType.builder(MiningRayCache::new).build());
}
