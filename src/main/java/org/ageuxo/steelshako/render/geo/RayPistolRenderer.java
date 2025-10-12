package org.ageuxo.steelshako.render.geo;

import org.ageuxo.steelshako.item.RayPistol;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RayPistolRenderer extends GeoItemRenderer<RayPistol> {
    public RayPistolRenderer() {
        super(new RayPistolModel());
    }
}
