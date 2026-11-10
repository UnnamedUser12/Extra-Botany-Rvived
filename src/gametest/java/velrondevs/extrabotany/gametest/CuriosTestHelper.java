package velrondevs.extrabotany.gametest;

import net.minecraft.world.entity.LivingEntity;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public final class CuriosTestHelper {
	private CuriosTestHelper() {}

	public static ICuriosItemHandler inventory(LivingEntity entity) {
		ICuriosItemHandler inventory = CuriosApi.getCuriosInventory(entity).orElseThrow();
		if (inventory.getCurios().values().stream().noneMatch(handler -> handler.getStacks().getSlots() > 0)) {
			inventory.loadDatapacks();
		}
		return inventory;
	}
}
