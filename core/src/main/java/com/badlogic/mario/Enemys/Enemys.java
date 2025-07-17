package com.badlogic.mario.Enemys;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.mario.Cenario;

public class Enemys {
    private final Array<Bicho> bichos = new Array<>();
    private float objectTimer = 0;
    private float speed = 100f;

    public void draw(float delta, SpriteBatch batch, Cenario cenario) {
        for (int i = bichos.size - 1; i >= 0; i--) {
            Bicho bicho = bichos.get(i);
            bicho.update(delta, cenario);

            if (bicho.isAtivo()) {
                bicho.draw(batch);
            } else {
                bichos.removeIndex(i);
            }
        }

        objectTimer += delta;
        if (objectTimer > 10f) {
            objectTimer = 0;
            spawnEnemy(speed); // spawn automático
        }
    }

    private void spawnEnemy(float speed) {
        float screenWidth = 1920;
        EnemyType tipo = EnemyType.values()[MathUtils.random(EnemyType.values().length - 1)];
        Bicho novoBicho = EnemyFactory.create(tipo, screenWidth, speed);
        bichos.add(novoBicho);
    }

    public void addEnemy(EnemyType tipo, float screenWidth, float speed) {
        Bicho novoBicho = EnemyFactory.create(tipo, screenWidth, speed);
        bichos.add(novoBicho);
    }

    public void removeEmeny(int index) {
        bichos.removeIndex(index);
    }

    public Array<Bicho> getBichos() {
        return bichos;
    }

    public void mover(float dx) {
        for (Bicho bicho : bichos) {
            bicho.mover(dx);
        }
    }
}
