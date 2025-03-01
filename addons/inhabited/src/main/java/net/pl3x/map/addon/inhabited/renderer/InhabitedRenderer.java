package net.pl3x.map.addon.inhabited.renderer;

import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.image.Image;
import net.pl3x.map.render.Renderer;
import net.pl3x.map.render.RendererHolder;
import net.pl3x.map.render.RendererRegistry;
import net.pl3x.map.render.ScanData;
import net.pl3x.map.render.ScanTask;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.Mathf;

public class InhabitedRenderer extends Renderer {
    public InhabitedRenderer(RendererHolder holder, ScanTask scanTask) {
        super(holder, scanTask);
    }

    @Override
    public void scanData(RegionCoordinate region, ScanData.Data scanData) {
        // get the basic renderer so we can copy its tiles
        Renderer basic = getScanTask().getRenderer(RendererRegistry.BASIC);

        // scan each block's data
        for (ScanData data : scanData.values()) {
            // get the current tile coordinates
            int pixelX = data.getCoordinate().getBlockX() & Image.SIZE - 1;
            int pixelZ = data.getCoordinate().getBlockZ() & Image.SIZE - 1;

            // get basic pixel color
            int pixelColor;
            if (basic != null) {
                // get current color from basic renderer
                pixelColor = basic.getImageHolder().getImage().getPixel(pixelX, pixelZ);
            } else {
                // could not find basic renderer (disabled?), we have to draw it ourselves
                pixelColor = basicPixelColor(data, scanData);
            }

            // we hsb lerp between blue and red with ratio being the
            // percent inhabited time is of the maxed out inhabited time
            float ratio = Mathf.clamp(0F, 1F, data.getChunk().getInhabitedTime() / 3600000F);
            int inhabitedRGB = Colors.lerpHSB(0x880000FF, 0x88FF0000, ratio, false);

            // set the color, mixing our heatmap on top
            // set a low enough alpha, so we can see the basic map underneath
            pixelColor = Colors.blend(inhabitedRGB, pixelColor);

            // draw color data to image
            getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);
        }
    }
}
