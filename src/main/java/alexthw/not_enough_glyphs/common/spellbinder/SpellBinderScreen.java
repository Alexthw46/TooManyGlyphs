package alexthw.not_enough_glyphs.common.spellbinder;

import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static alexthw.not_enough_glyphs.init.NotEnoughGlyphs.prefix;


public class SpellBinderScreen extends AbstractContainerScreen<SpellBinderContainer> {

    public static final ResourceLocation BACKGROUND =prefix("textures/gui/spell_binder.png");

    public SpellBinderScreen(SpellBinderContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics gui, float partialTicks, int x, int y) {
        gui.blit(BACKGROUND, leftPos - 40, topPos - 40, 0, 0, 256, 256);
    }

    @Override
    public void render(@NotNull GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        renderBackground(gui, mouseX, mouseY, partialTicks);
        super.render(gui, mouseX, mouseY, partialTicks);
        renderGlyphPreview(gui, this.leftPos + 8, this.topPos + 8);
        renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = -20;
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics gui, int mouseX, int mouseY) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack itemstack = this.hoveredSlot.getItem();
            gui.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), itemstack, mouseX, mouseY);
        }
    }

    public void renderGlyphPreview(GuiGraphics gui, int x, int y) {
        if (this.menu.binder.isEmpty()) return;
        AbstractCaster<?> spellCaster = SpellCasterRegistry.from(this.menu.binder);
        int offsetX = 0, offsetY = -26;
        for (int i = 0; i < spellCaster.getMaxSlots(); ++i) {
            if (i % 2 == 0) {
                offsetY += 23;
                offsetX = 0;
            } else {
                offsetX += 40;
            }
            Spell spell = spellCaster.getSpell(i);
            AbstractSpellPart primaryIcon = null;
            AbstractSpellPart secondaryIcon = null;

            for (AbstractSpellPart p : spell.recipe()) {
                if (p instanceof AbstractCastMethod) {
                    primaryIcon = p;
                }

                if (p instanceof AbstractEffect) {
                    secondaryIcon = p;
                    break;
                }
            }


            if (primaryIcon != null)
                RenderUtils.drawSpellPart(primaryIcon, gui, x + offsetX + 6, y + offsetY - 20, 8, false);

            if (secondaryIcon != null)
                RenderUtils.drawSpellPart(secondaryIcon, gui, x + offsetX + 6, y + offsetY - 10, 12, false);

        }
    }

}
