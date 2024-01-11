package alexthw.not_enough_glyphs.common.spellbinder;

import alexthw.not_enough_glyphs.init.NotEnoughGlyphs;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;


public class SpellBinderScreen extends AbstractContainerScreen<SpellBinderContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(NotEnoughGlyphs.MODID, "textures/gui/spell_binder.png");

    public SpellBinderScreen(SpellBinderContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics gui, float partialTicks, int x, int y) {
        int topPos = (this.height - this.imageHeight)/3;
        gui.blit(BACKGROUND, leftPos -45, topPos, 0, 0, 256, 256);
    }

    @Override
    public void render(@NotNull GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        super.render(gui, mouseX, mouseY, partialTicks);
        renderTooltip(gui, mouseX, mouseY);
    }


    @Override
    protected void renderTooltip(@NotNull GuiGraphics gui, int mouseX, int mouseY) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack itemstack = this.hoveredSlot.getItem();
            gui.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, mouseX, mouseY);
        }
    }


}
