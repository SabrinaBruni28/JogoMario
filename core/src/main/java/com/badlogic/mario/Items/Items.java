package com.badlogic.mario.Items;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Items {
    private static final int HEIGHT_Y = 172;
    private final Array<Item> itens = new Array<>();
    private float itemTimer = 0;

    public void draw(float delta, SpriteBatch batch) {
        for (int i = itens.size - 1; i >= 0; i--) {
            Item item = itens.get(i);
            
            if (item.estaForaDaTelaEsquerda()) {
                itens.removeIndex(i);
                continue;
            }

            item.update(delta);
            item.draw(batch);
        }

        itemTimer += delta;
        if (itemTimer > 6f) {
            itemTimer = 0;
            spawnItem();
        }
    }

    private void spawnItem() {
        ItemType tipo = ItemType.values()[MathUtils.random(ItemType.values().length - 1)];

        float x = 1920; // posição aleatória
        float y = MathUtils.random(HEIGHT_Y, 300);

        Item novoItem = ItemFactory.createItem(tipo, x, y);
        itens.add(novoItem);
    }

    public void addItem(ItemType tipo) {
        float x = MathUtils.random(100, 700);
        float y = MathUtils.random(HEIGHT_Y, 300);

        Item novoItem = ItemFactory.createItem(tipo, x, y);
        itens.add(novoItem);
    }

    public void removeItem(int index) {
        itens.removeIndex(index);
    }

    public Array<Item> getItens() {
        return itens;
    }

    public void mover(float dx) {
        for (Item item : itens) {
            item.mover(dx);
        }
    }
}
