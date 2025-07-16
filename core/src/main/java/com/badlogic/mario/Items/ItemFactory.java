package com.badlogic.mario.Items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ItemFactory {
    private static final String SPRITE_SHEET_PATH = "smb_items_sheet_2.png";
    private static final Texture spriteSheet = new Texture(Gdx.files.internal(SPRITE_SHEET_PATH));

    public static Item createItem(ItemType tipo, float x, float y) {
        TextureRegion[] frames;
        float width = 50;  // Ajustei para 16 para combinar com os sprites
        float height = 70;

        switch (tipo) {
            case MOEDA:
                frames = new TextureRegion[] {
                    new TextureRegion(spriteSheet, 0, 0, 16, 16),
                    new TextureRegion(spriteSheet, 16, 0, 16, 16),
                    new TextureRegion(spriteSheet, 32, 0, 16, 16),
                    new TextureRegion(spriteSheet, 48, 0, 16, 16)
                };
                break;

            case BLOCO:
                frames = new TextureRegion[] {
                    new TextureRegion(spriteSheet, 0, 64, 16, 16),
                    new TextureRegion(spriteSheet, 16, 64, 16, 16),
                    new TextureRegion(spriteSheet, 32, 64, 16, 16),
                    new TextureRegion(spriteSheet, 48, 64, 16, 16)
                };
                break;

            case BLOCO_TIJOLO:
                frames = new TextureRegion[] {
                    new TextureRegion(spriteSheet, 0, 80, 16, 16),
                    new TextureRegion(spriteSheet, 16, 80, 16, 16),
                    new TextureRegion(spriteSheet, 32, 80, 16, 16),
                    new TextureRegion(spriteSheet, 48, 80, 16, 16)
                };
                break;

            case CANO_VERDE:
                frames = new TextureRegion[] {
                    new TextureRegion(spriteSheet, 90, 0, 38, 32),
                };
                width = 170;
                height = 150;
                break;

            case CANO_AMARELO:
                frames = new TextureRegion[] {
                    new TextureRegion(spriteSheet, 128, 0, 32, 32),
                };
                width = 170;
                height = 150;
                break;

            // Outros casos aqui...

            default:
                throw new IllegalArgumentException("Tipo de item não suportado: " + tipo);
        }

        return new Item(frames, tipo, x, y, width, height);
    }

    public static void dispose() {
        spriteSheet.dispose();
    }
}
