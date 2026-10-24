package com.example.fontscale;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class FontScaleScreen extends Screen {
    private static final int[] PRESETS = {1, 2, 5, 10};
    private static final float PREVIEW_MAX = 5.0f;

    private final Screen parent;
    private ScaleSlider slider;

    public FontScaleScreen(Screen parent) {
        super(Text.literal("Font Scale"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;

        // Barra principal
        slider = new ScaleSlider(cx - 155, 100, 310, 20, FontScaleMod.getScale());
        this.addDrawableChild(slider);

        // Accesos rápidos: x1 x2 x5 x10
        int bw = 74;
        for (int i = 0; i < PRESETS.length; i++) {
            final int preset = PRESETS[i];
            this.addDrawableChild(ButtonWidget.builder(Text.literal("x" + preset), b -> slider.setFromScale(preset))
                .dimensions(cx - 155 + i * (bw + 4), 126, bw, 20)
                .build());
        }

        // Restablecer / Listo
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Restablecer a x1"), b -> slider.setFromScale(1))
            .dimensions(cx - 155, this.height - 28, 150, 20)
            .build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Listo"), b -> this.close())
            .dimensions(cx + 5, this.height - 28, 150, 20)
            .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 0xFFFFFFFF);

        // Vista previa (limitada a x5 para que quepa en pantalla)
        float s = Math.min(FontScaleMod.getScale(), PREVIEW_MAX);
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(this.width / 2f, 38f);
        context.getMatrices().scale(s, s);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Texto de ejemplo"), 0, 0, 0xFFFFFFFF);
        context.getMatrices().popMatrix();

        if (FontScaleMod.getScale() > PREVIEW_MAX) {
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("(la vista previa se limita a x" + (int) PREVIEW_MAX + ")"),
                this.width / 2, 86, 0xFFAAAAAA);
        }
    }

    @Override
    public void removed() {
        FontScaleMod.saveNow();
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    /** Barra de 1 a 100 en pasos enteros. */
    private static class ScaleSlider extends SliderWidget {
        ScaleSlider(int x, int y, int w, int h, float scale) {
            super(x, y, w, h, Text.empty(), (Math.round(scale) - 1) / 99.0);
            updateMessage();
        }

        private int current() {
            return Math.round(1f + (float) this.value * 99f);
        }

        void setFromScale(int s) {
            this.value = (Math.max(1, Math.min(100, s)) - 1) / 99.0;
            applyValue();
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Text.literal("Tamaño de fuente: x" + current()));
        }

        @Override
        protected void applyValue() {
            FontScaleMod.setScaleNoSave(current());
        }
    }
}
