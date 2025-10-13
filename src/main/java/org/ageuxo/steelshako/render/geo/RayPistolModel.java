package org.ageuxo.steelshako.render.geo;

import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.item.RayPistol;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class RayPistolModel extends DefaultedItemGeoModel<RayPistol> {

    public RayPistolModel() {
        super(SteelShakoMod.modRL("ray_pistol"));
    }
}
