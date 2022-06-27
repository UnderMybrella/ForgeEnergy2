package dev.brella.fe2.impl.mechanised.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.brella.fe2.impl.mechanised.Mechanisation;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Collections;
import java.util.Optional;

public class EnergyHopperScreen extends AbstractContainerScreen<EnergyHopperMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Mechanisation.ID, "textures/gui/container/energy_hopper.png");

    public EnergyHopperScreen(EnergyHopperMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);

        this.passEvents = false;
        this.imageHeight = 133;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    public void render(PoseStack poseStack, int mouseX, int mouseY, float p_98421_) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, p_98421_);
        this.renderTooltip(poseStack, mouseX, mouseY);
        // When hovering the energy bar

        if (isHovering(52, 18, 70, 12, mouseX, mouseY)) {
            this.renderTooltip(
                    poseStack,
                    Collections.singletonList(
                            Component.literal(Integer.toString(this.menu.getEnergy()))
                                    .append(" ")
                                    .append(Mechanisation.MECHANISED_OPERANDS.get().getUnits())),
                    Optional.empty(),
                    mouseX,
                    mouseY);
        }
    }

    @Override
    protected void renderBg(PoseStack poseStack, float p_97788_, int p_97789_, int p_97790_) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);

        int percentFilled = this.menu.getEnergyPercentage();
        this.blit(poseStack,
                i + 54, //x
                j + 20, //y
                176, //src x
                0, //src y
                percentFilled, //width
                9); //height

//        int l = this.menu.getBurnProgress();
//        this.blit(poseStack, i + 79, j + 34, 176, 14, l + 1, 16);
    }

}
