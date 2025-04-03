package com.badlogic.mario;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Cenario {
    private float backgroundPosX = 0; // Posição do fundo (cenário) no eixo X
    private float backgroundSpeed = 200; // Velocidade do movimento do fundo
    private float backgroundWidth = 1920; // Largura do fundo para controle da movimentação
    private float backgroundHeight = 1080; // Altura do fundo (supondo que seja 1080p)
    private Texture background;
    private boolean moviment = false;

    private Object object; // Referência ao objeto

    public Cenario() {
        background = new Texture("cenario.jpg");
        object = new Object(); // Criar o objeto, posicionado inicialmente à direita
    }

    public void dispose() {
        background.dispose();
    }

    public void draw(float delta, SpriteBatch batch) {
        batch.begin();

        // Desenha a primeira parte do fundo
        batch.draw(background, backgroundPosX, 0, backgroundWidth, backgroundHeight);

        // Desenha a segunda parte do fundo quando o primeiro fundo sair da tela
        batch.draw(background, backgroundPosX + backgroundWidth, 0, backgroundWidth, backgroundHeight);

        batch.end();
        // Desenha o objeto
        object.draw(delta, batch, moviment);

    }

    public void moverEsquerda(float delta, float screenWidth) {
        backgroundPosX += backgroundSpeed * delta; // Movimentar o fundo para a esquerda

        // Se a posição do fundo for maior que a largura do fundo, reposicione
        if (backgroundPosX >= backgroundWidth) {
            backgroundPosX = 0;
        }
    }

    public void moverDireita(float delta) {
        backgroundPosX -= backgroundSpeed * delta; // Movimentar o fundo para a direita

        // Se a posição do fundo for menor que a largura negativa, reposicione
        if (backgroundPosX <= -backgroundWidth) {
            backgroundPosX = 0;
        }
    }

    public void setMoviment(boolean moviment) {
        this.moviment = moviment;
    }
}
