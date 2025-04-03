package com.badlogic.mario;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Object {
    /* Lista das gotas de chuva. */
    Array<Sprite> objectSprites;
    Rectangle objectRectangle;
    /* Delay entre uma gota e outra. */
    float objectTimer;
    float objectSpeed;

    TextureRegion[][] spriteRegions;
    private static final int FRAME_WIDTH = 30;  // Largura de cada sprite
    private static final int FRAME_HEIGHT = 30; // Altura de cada sprite

    public Object() {
        // Carregar a folha de sprites
        Texture spriteSheet = new Texture("smb_enemies_sheet.png");

        // Dividir a folha de sprites em uma matriz de TextureRegion
        spriteRegions = TextureRegion.split(spriteSheet, FRAME_WIDTH, FRAME_HEIGHT);

        objectSprites = new Array<>();
        objectRectangle = new Rectangle();
    }

    public void draw(float delta, SpriteBatch batch, boolean aumenta) {
        logic(delta, aumenta);
        batch.begin();
        /* Desenha cada gota. */
        for (Sprite sprite : objectSprites) {
            sprite.draw(batch);
        }
        batch.end();
    }

    public void logic(float delta, boolean aumenta) {
        /* Percorre a lista de gotas removendo a última gota que caiu para que não ocorra erro de memória. */
        for (int i = objectSprites.size - 1; i >= 0; i--) {
            Sprite objectSprite = objectSprites.get(i); // Get the sprite from the list
            float objectWidth = objectSprite.getWidth();
            float objectHeight = objectSprite.getHeight();
            
            if (aumenta){
                objectSpeed = 600;
            } 
            else {
                objectSpeed = 200;
            }
			objectSprite.translateX(-objectSpeed * delta);

            /* Aplica as coordenadas da gota para o retângulo da gota. */
            objectRectangle.set(objectSprite.getX(), objectSprite.getY(), objectWidth, objectHeight);

            /* Remove a gota se ela tiver passado direto pela tela. */
            if (objectSprite.getX() < 0) {
				
				/* Para fazer um som com quando a gota passa direto. */
				objectSprites.removeIndex(i);
			}
        }

        objectTimer += delta; // Adds the current delta to the timer
        if (objectTimer > 5f) { // Check if it has been more than a second
            objectTimer = 0; // Reset the timer
            /* Função que cria uma gota. */
            createObject(1080, 1920);
        }
    }

    private void createObject(float screenWidth, float screenHeight) {
        // create local variables for convenience
        float objectWidth = 100;
        float objectHeight = 120;
        
        // create the object sprite
        Sprite objectSprite = new Sprite(spriteRegions[MathUtils.random(0, 6)][MathUtils.random(0, 13)]);
        objectSprite.setSize(objectWidth, objectHeight);
        objectSprite.setX(screenHeight); // Randomize the object's x position
        objectSprite.setY(160);
        objectSprites.add(objectSprite); // Add it to the list
    }
}
