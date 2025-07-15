package com.badlogic.mario.Enemys;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class Enemys {
    private final Array<Bicho> bichos = new Array<>();
    private float objectTimer = 0;

    public void draw(float delta, SpriteBatch batch, boolean aumenta) {
        float speed = aumenta ? 300f : 100f;

        for (int i = bichos.size - 1; i >= 0; i--) {
            Bicho bicho = bichos.get(i);
            bicho.update(delta);

            if (bicho.isAtivo()) {
                bicho.draw(batch);
            } else {
                bichos.removeIndex(i);
            }
        }

        objectTimer += delta;
        if (objectTimer > 5f) {
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

    public Array<Bicho> getBichos() {
        return bichos;
    }
}
