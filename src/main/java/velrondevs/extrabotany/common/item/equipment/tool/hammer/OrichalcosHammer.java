package velrondevs.extrabotany.common.item.equipment.tool.hammer;

import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;

import velrondevs.extrabotany.api.item.IOrichalcos;

/**
 * 奥利哈钢锤。它有范围挖掘（范围比泰拉碾压者大）和奥利哈钢材料的增益，见 {@code IOrichalcos}。
 * Orichalcos Hammer. It has the area mining (with a larger area than the Terra Smasher) and the buff of the orichalcos material, see {@code IOrichalcos}.
 */
public class OrichalcosHammer extends TerrasteelHammerItem implements IOrichalcos {

	private static final int MANA_PER_DAMAGE = 200;
	private static final int ACTIVE_COST = 20;
	public static final int RANGE = 3;

	public OrichalcosHammer(ToolMaterial tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
		super(tier, attackDamageModifier, attackSpeedModifier, properties);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot eqSlot) {
		super.inventoryTick(stack, world, entity, eqSlot);
		orichalcosTick(stack, world, entity, 0, false);
	}

	@Override
	public boolean canBeHurtBy(ItemStack stack, DamageSource damageSource) {
		return IOrichalcos.canBeHurtBy(damageSource);
	}

	@Override
	public int getManaPerDamage() {
		return MANA_PER_DAMAGE;
	}

	@Override
	public int getActiveCost() {
		return ACTIVE_COST;
	}

	@Override
	public int getRange(Player player, ItemStack stack, BlockPos pos, BlockPos originPos, Direction side) {
		return RANGE;
	}
}
