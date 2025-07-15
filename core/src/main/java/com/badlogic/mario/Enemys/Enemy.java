package com.badlogic.mario.Enemys;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

 // Classe interna para associar Sprite e sua animação
public class Enemy {
    Sprite sprite;
    Animation<TextureRegion> animation;

    Enemy(Sprite sprite, Animation<TextureRegion> animation) {
        this.sprite = sprite;
        this.animation = animation;
    }
}
