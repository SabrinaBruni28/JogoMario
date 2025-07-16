package com.badlogic.mario.Items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;

public class Items {
    private static final int HEIGHT_Y = 172;
    private final Array<Item> itens = new Array<>();
    private float itemTimer = 0;

    public void draw(float delta, SpriteBatch batch, boolean aumenta) {
        for (int i = itens.size - 1; i >= 0; i--) {
            Item item = itens.get(i);
            item.update(delta); // atualiza frame de animação
            item.draw(batch);   // desenha frame atual
        }

        itemTimer += delta;
        if (itemTimer > 5f) {
            itemTimer = 0;
            spawnItem();
        }
    }

    private void spawnItem() {
        ItemType tipo = ItemType.values()[MathUtils.random(ItemType.values().length - 1)];

        float x = MathUtils.random(100, 700); // posição aleatória
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
}
