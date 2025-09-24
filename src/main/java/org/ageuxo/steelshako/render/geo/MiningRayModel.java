package org.ageuxo.steelshako.render.geo;

import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.item.MiningRayGun;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class MiningRayModel extends DefaultedItemGeoModel<MiningRayGun> {

    public MiningRayModel() {
        super(SteelShakoMod.modRL("mining_ray_gun"));
    }
}
