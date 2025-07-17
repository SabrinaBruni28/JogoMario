package com.badlogic.mario.Utils;

import com.badlogic.gdx.math.Rectangle;

public class CollisionUtils {

    private static final float MARGEM = 10f;

    public static boolean isColisaoPorCima(Rectangle r1, Rectangle r2) {
        return r1.overlaps(r2)
            && r1.y >= r2.y + r2.height - MARGEM
            && r1.x + r1.width > r2.x + MARGEM
            && r1.x < r2.x + r2.width - MARGEM;
    }

    public static boolean isColisaoPorBaixo(Rectangle r1, Rectangle r2) {
        return r1.overlaps(r2)
            && r1.y + r1.height <= r2.y + MARGEM
            && r1.x + r1.width > r2.x + MARGEM
            && r1.x < r2.x + r2.width - MARGEM;
    }

    public static boolean isColisaoPelaEsquerda(Rectangle r1, Rectangle r2) {
        return r1.overlaps(r2)
            && r1.x + r1.width <= r2.x + MARGEM
            && r1.y + r1.height > r2.y + MARGEM
            && r1.y < r2.y + r2.height - MARGEM;
    }

    public static boolean isColisaoPelaDireita(Rectangle r1, Rectangle r2) {
        return r1.overlaps(r2)
            && r1.x >= r2.x + r2.width - MARGEM
            && r1.y + r1.height > r2.y + MARGEM
            && r1.y < r2.y + r2.height - MARGEM;
    }

    public static boolean estaSobreItem(Rectangle r1, Rectangle r2) {
        return r1.y <= r2.y + r2.height + MARGEM
            && r1.y >= r2.y + r2.height - MARGEM
            && r1.x + r1.width > r2.x + MARGEM
            && r1.x < r2.x + r2.width - MARGEM;
    }
}
