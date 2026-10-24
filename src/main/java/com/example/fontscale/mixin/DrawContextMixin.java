package com.example.fontscale.mixin;

import com.example.fontscale.FontScaleMod;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Inject(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)V", at = @At("HEAD"))
    private void fontscale$begin1(TextRenderer r, String t, int x, int y, int c, boolean s, CallbackInfo ci) {
        fontscale$begin(x, y);
    }

    @Inject(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)V", at = @At("HEAD"))
    private void fontscale$begin2(TextRenderer r, OrderedText t, int x, int y, int c, boolean s, CallbackInfo ci) {
        fontscale$begin(x, y);
    }

    @Inject(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V", at = @At("HEAD"))
    private void fontscale$begin3(TextRenderer r, Text t, int x, int y, int c, boolean s, CallbackInfo ci) {
        fontscale$begin(x, y);
    }

    @Inject(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)V", at = @At("RETURN"))
    private void fontscale$end1(TextRenderer r, String t, int x, int y, int c, boolean s, CallbackInfo ci) {
        fontscale$end();
    }

    @Inject(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)V", at = @At("RETURN"))
    private void fontscale$end2(TextRenderer r, OrderedText t, int x, int y, int c, boolean s, CallbackInfo ci) {
        fontscale$end();
    }

    @Inject(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V", at = @At("RETURN"))
    private void fontscale$end3(TextRenderer r, Text t, int x, int y, int c, boolean s, CallbackInfo ci) {
        fontscale$end();
    }

    @Unique
    private void fontscale$begin(int x, int y) {
        float scale = FontScaleMod.getScale();
        if (FontScaleMod.depth == 0 && scale != 1.0f && FontScaleMod.shouldScale()) {
            DrawContext self = (DrawContext) (Object) this;
            self.getMatrices().pushMatrix();
            self.getMatrices().translate((float) x, (float) y);
            self.getMatrices().scale(scale, scale);
            self.getMatrices().translate((float) -x, (float) -y);
            FontScaleMod.pushed = true;
        }
        FontScaleMod.depth++;
    }

    @Unique
    private void fontscale$end() {
        FontScaleMod.depth--;
        if (FontScaleMod.depth <= 0) {
            FontScaleMod.depth = 0;
            if (FontScaleMod.pushed) {
                ((DrawContext) (Object) this).getMatrices().popMatrix();
                FontScaleMod.pushed = false;
            }
        }
    }
}
