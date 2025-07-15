package com.badlogic.mario.Enemys;

public class EnemyFactory {
    private static final int HEIGHT_Y = 172;

    public static Bicho create(EnemyType tipo, float screenWidth, float speed) {
        int x1 = 0, y1 = 0, x2 = 0, y2 = 1;
        int xMorte = 2, yMorte = 0;
        float posY = HEIGHT_Y;
        boolean horizontal = true;
        float end = -200f;
        boolean sobe = false;
        float pontoSubida = 0;
        float alturaSubida = 0;

        switch (tipo) {
            case TARTARUGA_VERDE:
                x1 = 0; y1 = 5; x2 = 0; y2 = 6;
                xMorte = 0; yMorte = 12;
                posY = HEIGHT_Y;
                break;
            case TARTARUGA_VERMELHA:
                x1 = 1; y1 = 5; x2 = 1; y2 = 6;
                xMorte = 1; yMorte = 12;
                posY = HEIGHT_Y;
                break;
            case TARTARUGA_AZUL:
                x1 = 2; y1 = 5; x2 = 2; y2 = 6;
                xMorte = 2; yMorte = 12;
                posY = HEIGHT_Y;
                break;
            case TARTARUGA_VOADORA_VERDE:
                x1 = 0; y1 = 3; x2 = 0; y2 = 4;
                xMorte = 0; yMorte = 12;
                posY = HEIGHT_Y + 150;
                sobe = true;
                pontoSubida = screenWidth - 600;
                alturaSubida = 100;
                break;
            case TARTARUGA_VOADORA_VERMELHA:
                x1 = 1; y1 = 3; x2 = 1; y2 = 4;
                xMorte = 1; yMorte = 12;
                posY = HEIGHT_Y + 150;
                sobe = true;
                pontoSubida = screenWidth - 600;
                alturaSubida = 100;
                break;
            case TARTARUGA_VOADORA_AZUL:
                x1 = 2; y1 = 3; x2 = 2; y2 = 4;
                xMorte = 2; yMorte = 12;
                posY = HEIGHT_Y + 150;
                sobe = true;
                pontoSubida = screenWidth - 600;
                alturaSubida = 100;
                break;
            case BICHO_MARROM:
                x1 = 0; y1 = 0; x2 = 0; y2 = 1;
                xMorte = 0; yMorte = 2;
                posY = HEIGHT_Y;
                break;
        }

        return new Bicho(
            x1, y1, x2, y2,
            xMorte, yMorte,
            screenWidth, posY,
            horizontal,
            end,
            speed,
            sobe,
            pontoSubida,
            alturaSubida,
            tipo
        );
    }
}
