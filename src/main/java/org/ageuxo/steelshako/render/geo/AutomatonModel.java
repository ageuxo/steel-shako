package org.ageuxo.steelshako.render.geo;

import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.entity.Automaton;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class AutomatonModel extends DefaultedEntityGeoModel<Automaton> {

    public AutomatonModel() {
        super(SteelShakoMod.modRL("automaton"));
    }
}
