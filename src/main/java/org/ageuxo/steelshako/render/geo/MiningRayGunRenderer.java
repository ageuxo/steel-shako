package org.ageuxo.steelshako.render.geo;

import org.ageuxo.steelshako.item.MiningRayGun;
import org.ageuxo.steelshako.item.component.ChargeComponent;
import org.ageuxo.steelshako.item.component.ModComponents;
import org.ageuxo.steelshako.render.geo.layer.SimpleGlowingGeoLayer;
import org.ageuxo.steelshako.render.geo.layer.WarmUpGlowingGeoLayer;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MiningRayGunRenderer extends GeoItemRenderer<MiningRayGun> {

    public MiningRayGunRenderer() {
        super(new MiningRayModel());
        addRenderLayer(new SimpleGlowingGeoLayer<>(this, "_crystal"));
        addRenderLayer(new WarmUpGlowingGeoLayer(this, "_tubes"));
    }

    public int getRampUp() {
        return getCurrentItemStack().getOrDefault(ModComponents.RAY_RAMPUP.get(), 0);
    }

    public boolean hasCharge() {
        ChargeComponent component = getCurrentItemStack().get(ModComponents.CHARGE.get());
        return component != null && component.charge() > 0;
    }
}
