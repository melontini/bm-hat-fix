package me.melontini.hatfix.mixin;

import io.github.apace100.cosmetic_armor.CosmeticArmor;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import party.lemons.biomemakeover.entity.render.feature.HatFeatureRenderer;
import party.lemons.biomemakeover.item.HatItem;

import static party.lemons.biomemakeover.entity.render.feature.HatFeatureRenderer.MODELS;

@Mixin(HatFeatureRenderer.class)
public abstract class HatFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
    public HatFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }
    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V")
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        ItemStack cosmetic = CosmeticArmor.getStackInCosmeticSlot(entity, EquipmentSlot.HEAD);
        if (!cosmetic.isEmpty() && cosmetic.getItem() instanceof HatItem) {
            matrices.push();
            matrices.scale(1.2F, 1.2F, 1.2F);
            EntityModel hatModel = (EntityModel)MODELS.get(cosmetic.getItem());
            ((ModelWithHead)this.getContextModel()).getHead().rotate(matrices);
            VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, hatModel.getLayer(this.getTexture(entity)), true, false);
            hatModel.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
            matrices.pop();
        }
    }
    @Inject(at = @At("RETURN"), method = "getTexture(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/util/Identifier;", cancellable = true)
    public void getTexture(T entity, CallbackInfoReturnable<Identifier> cir) {
        ItemStack cosmetic = CosmeticArmor.getStackInCosmeticSlot(entity, EquipmentSlot.HEAD);
        if (!cosmetic.isEmpty() && cosmetic.getItem() instanceof HatItem) {
            cir.setReturnValue(((HatItem)cosmetic.getItem()).getHatTexture());
        } else {
            cir.cancel();
        }
    }
}
