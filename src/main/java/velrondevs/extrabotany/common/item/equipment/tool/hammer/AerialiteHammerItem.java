package velrondevs.extrabotany.common.item.equipment.tool.hammer;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;

import velrondevs.extrabotany.api.item.IAerialite;

/**
 * 天空锤。它有天空材料的增益，见 {@code IAerialite}。每次刷新增益消耗 10 点魔力。
 * Aerialite Hammer. It has the buff of the aerialite material, see {@code IAerialite}. Each application of the buff uses 10 mana.
 */
public class AerialiteHammerItem extends ManasteelHammerItem implements IAerialite {

	private static final int MANA_PER_DAMAGE = 70;
	private static final int BUFF_MANA = 10;

	public AerialiteHammerItem(ToolMaterial tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
		super(tier, attackDamageModifier, attackSpeedModifier, properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot eqSlot) {
		super.inventoryTick(stack, world, entity, eqSlot);
		aerialiteTick(stack, world, entity, 0, false);
	}

	@Override
	public int getManaPerDamage() {
		return MANA_PER_DAMAGE;
	}

	@Override
	public int getAerialiteBuffMana() {
		return BUFF_MANA;
	}
}
