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
        boolean paraDireita = false;
        String arquivo = "smb_enemies_sheet.png";

        switch (tipo) {
            case TARTARUGA_VERDE_L:
                x1 = 0; y1 = 5; x2 = 0; y2 = 6;
                xMorte = 0; yMorte = 12;
                posY = HEIGHT_Y;
                break;
            case TARTARUGA_VERMELHA_L:
                x1 = 1; y1 = 5; x2 = 1; y2 = 6;
                xMorte = 1; yMorte = 12;
                posY = HEIGHT_Y;
                break;
            case TARTARUGA_AZUL_L:
                x1 = 2; y1 = 5; x2 = 2; y2 = 6;
                xMorte = 2; yMorte = 12;
                posY = HEIGHT_Y;
                break;
            case TARTARUGA_VERDE_R:
                x1 = 0; y1 = 7; x2 = 0; y2 = 8;
                xMorte = 0; yMorte = 12;
                posY = HEIGHT_Y;
                paraDireita = true;
                break;
            case TARTARUGA_VERMELHA_R:
                x1 = 1; y1 = 7; x2 = 1; y2 = 8;
                xMorte = 1; yMorte = 12;
                posY = HEIGHT_Y;
                paraDireita = true;
                break;
            case TARTARUGA_AZUL_R:
                x1 = 2; y1 = 7; x2 = 2; y2 = 8;
                xMorte = 2; yMorte = 12;
                posY = HEIGHT_Y;
                paraDireita = true;
                break;
            case TARTARUGA_VOADORA_VERDE_L:
                x1 = 0; y1 = 3; x2 = 0; y2 = 4;
                xMorte = 0; yMorte = 12;
                posY = HEIGHT_Y + 150;
                sobe = true;
                pontoSubida = screenWidth - 600;
                alturaSubida = 100;
                break;
            case TARTARUGA_VOADORA_VERMELHA_L:
                x1 = 1; y1 = 3; x2 = 1; y2 = 4;
                xMorte = 1; yMorte = 12;
                posY = HEIGHT_Y + 150;
                sobe = true;
                pontoSubida = screenWidth - 600;
                alturaSubida = 100;
                break;
            case TARTARUGA_VOADORA_AZUL_L:
                x1 = 2; y1 = 3; x2 = 2; y2 = 4;
                xMorte = 2; yMorte = 12;
                posY = HEIGHT_Y + 150;
                sobe = true;
                pontoSubida = screenWidth - 600;
                alturaSubida = 100;
                break;
             case TARTARUGA_VOADORA_VERDE_R:
                x1 = 0; y1 = 3; x2 = 0; y2 = 4;
                xMorte = 0; yMorte = 12;
                posY = HEIGHT_Y + 150;
                sobe = true;
                pontoSubida = screenWidth - 600;
                alturaSubida = 100;
                break;
            case TARTARUGA_VOADORA_VERMELHA_R:
                x1 = 1; y1 = 3; x2 = 1; y2 = 4;
                xMorte = 1; yMorte = 12;
                posY = HEIGHT_Y + 150;
                sobe = true;
                pontoSubida = screenWidth - 600;
                alturaSubida = 100;
                break;
            case TARTARUGA_VOADORA_AZUL_R:
                x1 = 2; y1 = 3; x2 = 2; y2 = 4;
                xMorte = 2; yMorte = 12;
                posY = HEIGHT_Y + 150;
                sobe = true;
                pontoSubida = screenWidth - 600;
                alturaSubida = 100;
                break;
            case BICHO_MARROM_L:
                x1 = 0; y1 = 0; x2 = 0; y2 = 1;
                xMorte = 0; yMorte = 2;
                posY = HEIGHT_Y - 20;
                break;
            case BICHO_AZUL_L:
                x1 = 1; y1 = 0; x2 = 1; y2 = 1;
                xMorte = 1; yMorte = 2;
                posY = HEIGHT_Y - 20;
                break;
            case BICHO_BRANCO_L:
                x1 = 2; y1 = 0; x2 = 2; y2 = 1;
                xMorte = 2; yMorte = 2;
                posY = HEIGHT_Y - 20;
                break;
            case BICHO_MARROM_R:
                x1 = 0; y1 = 0; x2 = 0; y2 = 1;
                xMorte = 0; yMorte = 2;
                posY = HEIGHT_Y - 20;
                paraDireita = true;
                screenWidth = 0;
                break;
            case BICHO_AZUL_R:
                x1 = 1; y1 = 0; x2 = 1; y2 = 1;
                xMorte = 1; yMorte = 2;
                posY = HEIGHT_Y - 20;
                paraDireita = true;
                screenWidth = 0;
                break;
            case BICHO_BRANCO_R:
                x1 = 2; y1 = 0; x2 = 2; y2 = 1;
                xMorte = 2; yMorte = 2;
                posY = HEIGHT_Y - 20;
                paraDireita = true;
                screenWidth = 0;
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
            paraDireita,
            pontoSubida,
            alturaSubida,
            tipo,
            arquivo
        );
    }

    public static EnemyType inverterTipo(EnemyType tipo) {
        switch (tipo) {
            case TARTARUGA_VERDE_L: return EnemyType.TARTARUGA_VERDE_R;
            case TARTARUGA_VERDE_R: return EnemyType.TARTARUGA_VERDE_L;

            case TARTARUGA_VERMELHA_L: return EnemyType.TARTARUGA_VERMELHA_R;
            case TARTARUGA_VERMELHA_R: return EnemyType.TARTARUGA_VERMELHA_L;

            case TARTARUGA_AZUL_L: return EnemyType.TARTARUGA_AZUL_R;
            case TARTARUGA_AZUL_R: return EnemyType.TARTARUGA_AZUL_L;

            case TARTARUGA_VOADORA_VERDE_L: return EnemyType.TARTARUGA_VOADORA_VERDE_R;
            case TARTARUGA_VOADORA_VERDE_R: return EnemyType.TARTARUGA_VOADORA_VERDE_L;

            case TARTARUGA_VOADORA_VERMELHA_L: return EnemyType.TARTARUGA_VOADORA_VERMELHA_R;
            case TARTARUGA_VOADORA_VERMELHA_R: return EnemyType.TARTARUGA_VOADORA_VERMELHA_L;

            case TARTARUGA_VOADORA_AZUL_L: return EnemyType.TARTARUGA_VOADORA_AZUL_R;
            case TARTARUGA_VOADORA_AZUL_R: return EnemyType.TARTARUGA_VOADORA_AZUL_L;

            case BICHO_MARROM_L: return EnemyType.BICHO_MARROM_R;
            case BICHO_MARROM_R: return EnemyType.BICHO_MARROM_L;

            case BICHO_AZUL_L: return EnemyType.BICHO_AZUL_R;
            case BICHO_AZUL_R: return EnemyType.BICHO_AZUL_L;

            case BICHO_BRANCO_L: return EnemyType.BICHO_BRANCO_R;
            case BICHO_BRANCO_R: return EnemyType.BICHO_BRANCO_L;

            default: return tipo;
        }
    }
}
