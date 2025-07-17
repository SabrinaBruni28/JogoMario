package com.badlogic.mario;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

 // Classe interna para associar Sprite e sua animação
public class Object {
    public Sprite sprite;
    public Animation<TextureRegion> animation;

    public Object(Sprite sprite, Animation<TextureRegion> animation) {
        this.sprite = sprite;
        this.animation = animation;
    }
}
