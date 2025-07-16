package com.badlogic.mario;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class FirstScreen implements Screen {
    private SpriteBatch batch;
    private FitViewport view;

    private Mario  mario;
    private Cenario cenario;

    public FirstScreen() {
        batch = new SpriteBatch();
        view = new FitViewport(1920, 1080);
        view.getCamera().position.set(1920 / 2.0f, 1080 / 2.0f, 0);

        mario = new Mario();
        cenario = new Cenario();
        
    }

    @Override
    public void show() {
        // Método chamado quando a tela é exibida
    }

    @Override
    public void render(float delta) {
        input(delta);
        cenario.verificarColisao(mario);
        cenario.verificarColisaoEnemys();
        cenario.verificarColisaoComItens(mario);
        draw(delta);
    }

    public void input(float delta) {
        // Obter as dimensões da tela
        float screenWidth = view.getWorldWidth();
        float screenHeight = view.getWorldHeight();

        mario.input(delta, cenario, screenWidth, screenHeight);
    }

    public void draw(float delta) {
        // Desenhar a cena
        ScreenUtils.clear(Color.BLACK);
        view.apply();
        batch.setProjectionMatrix(view.getCamera().combined);
        cenario.draw(delta, batch);
        mario.draw(batch);
    }

    @Override
    public void resize(int width, int height) {
        view.update(width, height, true);
    }

    @Override
    public void pause() {
        // Método chamado quando a aplicação é pausada
    }

    @Override
    public void resume() {
        // Método chamado quando a aplicação é retomada
    }

    @Override
    public void hide() {
        // Método chamado quando outra tela substitui esta
    }

    @Override
    public void dispose() {
        mario.dispose();
        cenario.dispose();
        batch.dispose();
    }
}
