package velrondevs.extrabotany.common.item.equipment.tool.hammer;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;

import velrondevs.extrabotany.api.item.IPhotonium;

/**
 * 光子锤。它有光子材料的增益，见 {@code IPhotonium}。
 * Photonium Hammer. It has the buff of the photonium material, see {@code IPhotonium}.
 */
public class PhotoniumHammerItem extends ManasteelHammerItem implements IPhotonium {

	public PhotoniumHammerItem(ToolMaterial tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
		super(tier, attackDamageModifier, attackSpeedModifier, properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot eqSlot) {
		super.inventoryTick(stack, world, entity, eqSlot);
		photoniumTick(stack, world, entity, 0, false);
	}
}
