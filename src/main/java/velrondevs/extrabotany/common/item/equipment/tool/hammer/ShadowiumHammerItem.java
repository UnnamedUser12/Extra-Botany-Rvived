package velrondevs.extrabotany.common.item.equipment.tool.hammer;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;

import velrondevs.extrabotany.api.item.IShadowium;

/**
 * 暗影锤。它有暗影材料的增益，见 {@code IShadowium}。
 * Shadowium Hammer. It has the buff of the shadowium material, see {@code IShadowium}.
 */
public class ShadowiumHammerItem extends ManasteelHammerItem implements IShadowium {

	public ShadowiumHammerItem(ToolMaterial tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
		super(tier, attackDamageModifier, attackSpeedModifier, properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot eqSlot) {
		super.inventoryTick(stack, world, entity, eqSlot);
		shadowiumTick(stack, world, entity, 0, false);
	}
}
