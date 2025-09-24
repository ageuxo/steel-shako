package org.ageuxo.steelshako.render.geo;

import org.ageuxo.steelshako.item.MiningRayGun;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MiningRayGunRenderer extends GeoItemRenderer<MiningRayGun> {

    public MiningRayGunRenderer() {
        super(new MiningRayModel());
    }

}
