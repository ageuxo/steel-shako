package org.ageuxo.steelshako.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.ageuxo.steelshako.block.multi.MultiblockCoreBlockEntity;

import java.util.Objects;

public class BoilerMenu extends MultiblockMenu {

    public BoilerMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(ModMenuTypes.BOILER.get(), containerId, playerInv, (MultiblockCoreBlockEntity) Objects.requireNonNull(playerInv.player.level().getBlockEntity(buf.readBlockPos())), new SimpleContainerData(5), 1);

        initialiseMenu();
    }

    public BoilerMenu(int containerId, Inventory inventory, MultiblockCoreBlockEntity blockEntity) {
        this(ModMenuTypes.BOILER.get(), containerId, inventory, blockEntity, blockEntity.data(), 1);

        initialiseMenu();
    }

    protected BoilerMenu(MenuType<BoilerMenu> type, int containerId, Inventory inventory, MultiblockCoreBlockEntity blockEntity, ContainerData data, int menuSlots) {
        super(type, containerId, inventory, blockEntity, data, menuSlots);
    }

    private void initialiseMenu() {
        IItemHandler itemHandler = this.blockEntity.getItemCapDirect();
        this.addSlot(new SlotItemHandler(itemHandler, 0, 80, 52));
    }

    public int fuel() {
        return data.get(1);
    }

    public int maxFuel() {
        return data.get(2);
    }

    public int water() {
        return data.get(3);
    }

    public int maxWater() {
        return data.get(4);
    }


}
