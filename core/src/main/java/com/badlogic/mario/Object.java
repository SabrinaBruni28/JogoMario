package com.badlogic.mario;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Object {
    private final Array<Enemy> objectSprites = new Array<>();
    private final Rectangle objectRectangle = new Rectangle();

    private float objectTimer = 0;
    private float objectSpeed;
    private float stateTime = 0;

    private static final int FRAME_WIDTH = 30;
    private static final int FRAME_HEIGHT = 30;
    private static final int HEIGHT_Y = 172;

    private Animation<TextureRegion> tartarugaAnimation, tartarugaVoadoraAnimation;
    private TextureRegion[][] spriteRegions;

    enum EnemyType {
        TARTARUGA,
        TARTARUGA_VOADORA
    }

    // Classe interna para associar Sprite e sua animação
    private class Enemy {
        Sprite sprite;
        Animation<TextureRegion> animation;

        Enemy(Sprite sprite, Animation<TextureRegion> animation) {
            this.sprite = sprite;
            this.animation = animation;
        }
    }

    public Object() {
        // Carrega sprite sheet
        Texture spriteSheet = new Texture("smb_enemies_sheet.png");
        spriteRegions = TextureRegion.split(spriteSheet, FRAME_WIDTH, FRAME_HEIGHT);

        // Inicializa animações
        tartaruga();
        tartarugaVoadora();
    }

    private void tartaruga() {
        tartarugaAnimation = new Animation<>(0.4f, spriteRegions[0][5], spriteRegions[0][6]);
        tartarugaAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    private void tartarugaVoadora() {
        tartarugaVoadoraAnimation = new Animation<>(0.4f, spriteRegions[0][3], spriteRegions[0][4]);
        tartarugaVoadoraAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void draw(float delta, SpriteBatch batch, boolean aumenta) {
        logic(delta, aumenta);
        stateTime += delta;

        batch.begin();
        for (Enemy enemy : objectSprites) {
            TextureRegion frame = enemy.animation.getKeyFrame(stateTime, true);
            enemy.sprite.setRegion(frame);
            enemy.sprite.draw(batch);
        }
        batch.end();
    }

    private void logic(float delta, boolean aumenta) {
        for (int i = objectSprites.size - 1; i >= 0; i--) {
            Enemy enemy = objectSprites.get(i);
            Sprite sprite = enemy.sprite;

            float width = sprite.getWidth();

            objectSpeed = aumenta ? 300f : 100f;
            sprite.translateX(-objectSpeed * delta);

            objectRectangle.set(sprite.getX(), sprite.getY(), width, sprite.getHeight());

            if (sprite.getX() + width < 0) {
                objectSprites.removeIndex(i);
            }
        }

        objectTimer += delta;
        if (objectTimer > 5f) {
            objectTimer = 0;
            createObject(1920);
        }
    }

    private void createObject(float screenWidth) {
        float objectWidth = 100;
        float objectHeight = 120;

        EnemyType tipo = EnemyType.values()[MathUtils.random(EnemyType.values().length - 1)];

        Animation<TextureRegion> anim;
        float posY;

        switch (tipo) {
            case TARTARUGA:
                anim = tartarugaAnimation;
                posY = HEIGHT_Y;
                break;
            case TARTARUGA_VOADORA:
                anim = tartarugaVoadoraAnimation;
                posY = HEIGHT_Y + 150;
                break;
            default:
                anim = tartarugaAnimation;
                posY = HEIGHT_Y;
        }

        TextureRegion frame = anim.getKeyFrame(0);
        Sprite newSprite = new Sprite(frame);
        newSprite.setSize(objectWidth, objectHeight);
        newSprite.setX(screenWidth); // aparece fora da tela, à direita
        newSprite.setY(posY);

        objectSprites.add(new Enemy(newSprite, anim));
    }
}
