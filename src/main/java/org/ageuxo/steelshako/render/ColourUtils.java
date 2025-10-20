package org.ageuxo.steelshako.render;

import org.jetbrains.annotations.Nullable;

public class ColourUtils {

    /** Blends two RGB colours by interpolating them in HSB colour-space
     */
    public static int colourLerp(float t, int rgbA, int rgbB) {
        float[] a = RGBtoHSB(
                (rgbA >> 16) & 0xFF,
                (rgbA >> 8) & 0xFF,
                rgbA & 0xFF
        );
        float[] b = RGBtoHSB(
                (rgbB >> 16) & 0xFF,
                (rgbB >> 8) & 0xFF,
                rgbB & 0xFF
        );
        float[] lerped = lerpHSB(t, a, b);
        return HSBtoRGB(lerped[0], lerped[1], lerped[2]);
    }

    // Borrowed from AWT
    public static float[] RGBtoHSB(int r, int g, int b) {
        return RGBtoHSB(r, g, b, null);
    }

    // Borrowed from AWT
    @SuppressWarnings("ManualMinMaxCalculation")
    public static float[] RGBtoHSB(int r, int g, int b, float @Nullable [] hsbvals) {
        float hue, saturation, brightness;
        if (hsbvals == null) {
            hsbvals = new float[3];
        }
        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

    // Borrowed from AWT
    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (t * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 1:
                    r = (int) (q * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (p * 255.0f + 0.5f);
                    break;
                case 2:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (brightness * 255.0f + 0.5f);
                    b = (int) (t * 255.0f + 0.5f);
                    break;
                case 3:
                    r = (int) (p * 255.0f + 0.5f);
                    g = (int) (q * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 4:
                    r = (int) (t * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (brightness * 255.0f + 0.5f);
                    break;
                case 5:
                    r = (int) (brightness * 255.0f + 0.5f);
                    g = (int) (p * 255.0f + 0.5f);
                    b = (int) (q * 255.0f + 0.5f);
                    break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b);
    }

    public static float[] lerpHSB(float t, float[] hsbA, float[] hsbB) {
        float hue;
        float hueDiff = hsbB[0] - hsbA[0];

        if (Math.abs(hueDiff) <= 180) {
            // Take the shortest path
            hue = hsbA[0] + hueDiff * t;
        } else if (hueDiff > 180) {
            // Counterclockwise
            hue = hsbA[0] + (hueDiff - 360) * t;
        } else {
            // Clockwise
            hue = hsbA[0] + (hueDiff + 360) * t;
        }

        hue = (hue + 360) % 360;

        // Saturation
        float sat = hsbA[1] + (hsbB[1] - hsbA[1]) * t;

        // Brightness
        float bri = hsbA[2] + (hsbB[2] - hsbA[2] * t);

        return new float[]{
                hue,
                sat,
                bri
        };

    }
}
