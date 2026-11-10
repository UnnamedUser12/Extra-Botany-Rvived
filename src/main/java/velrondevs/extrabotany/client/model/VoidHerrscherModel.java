package velrondevs.extrabotany.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

import java.util.function.Supplier;

/**
 * 空之律者的模型：一个玩家体型的人形，带一层外衣、一对翅膀和三面旋转的护盾。
 * The model of the Herrscher of the Void: a humanoid with the shape of a player, with an outer layer, a pair of wings and three shields that turn.
 *
 * <p>全部部件使用同一张 128x128 的贴图。下面的数值逐项来自 ExtraMeteorP 1.12 {@code ModelVoidHerrscher}。
 * <p>All parts use the same 128x128 texture. The values below come item by item from ExtraMeteorP 1.12 {@code ModelVoidHerrscher}.
 */
public class VoidHerrscherModel extends HumanoidModel<VoidHerrscherModel.State> {
	public static final int SHIELD_COUNT = 3;

	public static class State extends HumanoidRenderState {
		public boolean rankII;
		public int shields;
	}

	private final ModelPart[] outerLayer;
	private final ModelPart[] bodyParts;
	private final ModelPart[] wings;
	private final ModelPart[] shields = new ModelPart[SHIELD_COUNT];

	public VoidHerrscherModel() {
		this(createLayer().bakeRoot(), createEffectLayer().bakeRoot());
	}

	private VoidHerrscherModel(ModelPart root, ModelPart effects) {
		super(root);
		outerLayer = new ModelPart[] {
				root.getChild("body_wear"), root.getChild("left_arm_wear"), root.getChild("right_arm_wear"),
				root.getChild("left_leg_wear"), root.getChild("right_leg_wear") };
		bodyParts = new ModelPart[] { body, leftArm, rightArm, leftLeg, rightLeg };
		wings = new ModelPart[] { effects.getChild("left_wing"), effects.getChild("right_wing") };
		for (int i = 0; i < SHIELD_COUNT; i++) {
			shields[i] = effects.getChild("shield" + (i + 1));
		}
	}

	private static LayerDefinition createLayer() {
		MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0F);
		PartDefinition root = mesh.getRoot();
		CubeDeformation outer = new CubeDeformation(0.25F);
		root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1F, -2F, -2F, 4, 12, 4), PartPose.offset(5F, 2F, 0F));
		root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2F, 0F, -2F, 4, 12, 4), PartPose.offset(1.9F, 12F, 0F));
		root.addOrReplaceChild("left_arm_wear", CubeListBuilder.create().texOffs(48, 48).addBox(-1F, -2F, -2F, 4, 12, 4, outer), PartPose.offset(5F, 2F, 0F));
		root.addOrReplaceChild("right_arm_wear", CubeListBuilder.create().texOffs(40, 32).addBox(-3F, -2F, -2F, 4, 12, 4, outer), PartPose.offset(-5F, 2F, 0F));
		root.addOrReplaceChild("left_leg_wear", CubeListBuilder.create().texOffs(0, 48).addBox(-2F, 0F, -2F, 4, 12, 4, outer), PartPose.offset(1.9F, 12F, 0F));
		root.addOrReplaceChild("right_leg_wear", CubeListBuilder.create().texOffs(0, 32).addBox(-2F, 0F, -2F, 4, 12, 4, outer), PartPose.offset(-1.9F, 12F, 0F));
		root.addOrReplaceChild("body_wear", CubeListBuilder.create().texOffs(16, 32).addBox(-4F, 0F, -2F, 8, 12, 4, outer), PartPose.ZERO);
		return LayerDefinition.create(mesh, 128, 128);
	}

	private static LayerDefinition createEffectLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition root = mesh.getRoot();
		root.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 64).addBox(0F, 0F, 0F, 26, 32, 0),
				PartPose.offsetAndRotation(-30F, -11F, 5F, 0F, 0F, -0.1745329F));
		root.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(52, 64).addBox(0F, 0F, 0F, 26, 32, 0),
				PartPose.offsetAndRotation(4F, -15.8F, 5F, 0F, 0F, 0.1745329F));
		for (int i = 1; i <= SHIELD_COUNT; i++) {
			root.addOrReplaceChild("shield" + i, CubeListBuilder.create().texOffs(0, 113).addBox(-6F, -1F, 9F, 12, 11, 0), PartPose.ZERO);
		}
		return LayerDefinition.create(mesh, 128, 128);
	}

	@Override
	public void setupAnim(State state) {
		super.setupAnim(state);
		for (int i = 0; i < outerLayer.length; i++) {
			outerLayer[i].loadPose(bodyParts[i].storePose());
		}
	}

	public void renderWings(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay) {
		for (ModelPart wing : wings) {
			wing.render(poseStack, buffer, packedLight, packedOverlay);
		}
	}

	public void renderShields(State state, PoseStack poseStack, Supplier<VertexConsumer> buffer, int packedLight, int packedOverlay, int color) {
		int count = state.shields;
		for (int i = 0; i < SHIELD_COUNT && i < count; i++) {
			shields[i].yRot = (float) (Math.sin(state.ageInTicks * 0.1F) + Math.PI * 2 / count * (i + 1));
			shields[i].render(poseStack, buffer.get(), packedLight, packedOverlay, color);
		}
	}
}
