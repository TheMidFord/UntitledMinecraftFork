package net.minecraft.client.gui;

public class RecipeViewerScreen extends Screen{


    @Override
    public void render(final int xm, final int ym, final float a) {
        this.drawCenteredString(font,"You're seeing the RecipeViewer Prototype!", 200,100,0xFFFFFF);
        super.render(xm,ym,a);
    }
}

