package org.ageuxo.steelshako.render.geo;

import org.ageuxo.steelshako.SteelShakoMod;
import org.ageuxo.steelshako.block.be.ExcitationDynamoBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class ExcitationDynamoModel extends DefaultedBlockGeoModel<ExcitationDynamoBlockEntity> {

    public ExcitationDynamoModel() {
        super(SteelShakoMod.modRL("spinny_dynamo"));
    }
}
