package com.badlogic.mario.Items;

import com.badlogic.mario.Object;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item {
    private Object itemObject;  // substitui sprite e animation
    private ItemType tipo;

    private float stateTime = 0;
    private boolean ativo = true;

    public Item(TextureRegion[] frames, ItemType tipo, float posX, float posY, float width, float height) {
        this.tipo = tipo;
        Animation<TextureRegion> animation = new Animation<>(0.2f, frames);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        // Cria o objeto encapsulando Sprite + Animation
        itemObject = new Object(new com.badlogic.gdx.graphics.g2d.Sprite(frames[0]), animation);
        itemObject.sprite.setSize(width, height);  // ajustar tamanho conforme preferir
        itemObject.sprite.setPosition(posX, posY);
    }

    public void update(float delta) {
        if (!ativo) return;

        stateTime += delta;
        // Atualiza frame no sprite de acordo com animação e stateTime
        itemObject.sprite.setRegion(itemObject.animation.getKeyFrame(stateTime));
    }

    public void draw(SpriteBatch batch) {
        if (!ativo) return;

        batch.begin();
        itemObject.sprite.draw(batch);
        batch.end();
    }

    public void coletar() {
        ativo = false;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public com.badlogic.gdx.graphics.g2d.Sprite getSprite() {
        return itemObject.sprite;
    }

    public ItemType getTipo() {
        return tipo;
    }

    // Adicione no Item.java
    public Rectangle getBoundingBox() {
        float marginX = 10f; // reduz largura em 12px no total
        float marginY = 10f; // reduz altura em 8px no total

        return new Rectangle(
            itemObject.sprite.getX() + marginX,
            itemObject.sprite.getY() + marginY,
            itemObject.sprite.getWidth() - marginX * 2,
            itemObject.sprite.getHeight() - marginY * 2
        );
    }

    public void mover(float dx) {
        itemObject.sprite.setX(itemObject.sprite.getX() + dx);
    }

    public boolean estaForaDaTelaEsquerda() {
        return itemObject.sprite.getX() + itemObject.sprite.getWidth() < 0;
    }


}
