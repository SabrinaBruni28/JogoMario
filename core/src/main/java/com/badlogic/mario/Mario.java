package com.badlogic.mario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Mario {
    private float stateTime = 0f; // Tempo de estado da animação
    private static final int FRAME_WIDTH = 30;  // Largura de cada sprite
    private static final int FRAME_HEIGHT = 30; // Altura de cada sprite
    private Texture spriteSheet;

    private enum State {
        STANDING_RIGHT, STANDING_LEFT, STANDING_DOWN, WALKING_RIGHT, WALKING_LEFT
    }

    private float posX = 10; // Posição inicial do personagem no eixo X
    private float posY = 100; // Posição inicial do personagem no eixo Y
    private float widthMario = 150;
    private float heightMario = 200;
    private float speed = 200; // Velocidade de movimento do personagem (pixels por segundo)

    private boolean isJumping = false; // Verifica se o personagem está pulando
    private float velocityY = 2f; // Velocidade vertical do personagem
    private float gravity = -800f; // Aceleração da gravidade (negativa porque puxa para baixo)
    private float jumpHeight = 400f; // A altura do pulo

    private boolean isCrouching = false; // Verifica se o personagem está abaixado
    private float crouchHeight = 100f; // A altura do personagem quando abaixado

    private State currentState = State.STANDING_RIGHT; // Estado atual do personagem
    private Animation<TextureRegion> standRightAnimation;
    private Animation<TextureRegion> standLeftAnimation;
    private Animation<TextureRegion> standDownAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> walkLeftAnimation;

    public Mario() {
        // Carregar a folha de sprites
        spriteSheet = new Texture("smb_mario_sheet.png");

        // Dividir a folha de sprites em uma matriz de TextureRegion
        TextureRegion[][] spriteRegions = TextureRegion.split(spriteSheet, FRAME_WIDTH, FRAME_HEIGHT);

        // Configurar as animações
        standRightAnimation = new Animation<>(0.4f, spriteRegions[0][7]);
        standRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        standLeftAnimation = new Animation<>(0.4f, spriteRegions[0][6]);
        standLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        standDownAnimation = new Animation<>(0.4f, spriteRegions[0][6]);
        standDownAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        walkRightAnimation = new Animation<>(0.4f, spriteRegions[0][9], spriteRegions[0][10]);
        walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);

        walkLeftAnimation = new Animation<>(0.4f, spriteRegions[0][3], spriteRegions[0][4]);
        walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void dispose() {
        spriteSheet.dispose();
    }

    public void input(float delta, Cenario cenario, float screenWidth, float screenHeight) {
        boolean isMoving = false;

        // Verificar se o personagem saiu dos limites da tela e ajustar a posição
        
        if (posY < 100) // Não pode sair da borda inferior
            posY = 100;

        else if (posY > screenHeight - heightMario) // Não pode sair da borda superior (200 é a altura do personagem)
            posY = screenHeight - heightMario;

        // Movimento para a esquerda
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            andarEsquerda(delta, screenWidth);
            currentState = State.WALKING_LEFT;
            isMoving = true;
        }

        // Movimento para a direita
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            andarDireita(delta, screenWidth);
            cenario.moverDireita(delta);
            currentState = State.WALKING_RIGHT;
            isMoving = true;
        }


        // Se o personagem não estiver pulando e a tecla de pulo estiver pressionada
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !isJumping) {
            velocityY = jumpHeight; // Inicia o pulo
            isJumping = true;
            isMoving = true;
            pular(delta);
        }

        // Atualizar a posição vertical com base na velocidade vertical e gravidade
        if (isJumping) pular(delta);

        // Verificar se o personagem deve abaixar
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            isCrouching = true; // Marca o personagem como abaixado
            posY = 150;
        } 
        else {
            isCrouching = false; // Marca o personagem como não abaixado
            posY = 100;
        }

        // Se não estiver se movendo, definir o estado como parado
        if (!isMoving) {
            if (currentState == State.WALKING_RIGHT) {
                currentState = State.STANDING_RIGHT;
            } 
            else if (currentState == State.WALKING_LEFT) {
                currentState = State.STANDING_LEFT;
            }
        }
    }

    public void draw(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime(); // Atualizar tempo de estado

        // Selecionar o frame atual com base no estado
        TextureRegion currentFrame = null;
        switch (currentState) {
            case STANDING_RIGHT:
                currentFrame = standRightAnimation.getKeyFrame(stateTime, false);
                break;
            case STANDING_LEFT:
                currentFrame = standLeftAnimation.getKeyFrame(stateTime, false);
                break;
            case STANDING_DOWN:
                currentFrame = standDownAnimation.getKeyFrame(stateTime, false);
                break;
            case WALKING_RIGHT:
                currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
                break;
            case WALKING_LEFT:
                currentFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
                break;
        }

        // Se o personagem estiver abaixado, desenha com uma altura reduzida
        float drawHeight = isCrouching ? crouchHeight : heightMario;

        batch.begin();
            batch.draw(currentFrame, posX, posY, widthMario, drawHeight); // Desenhar na posição atual
        batch.end();
    }

    public void andarDireita(float delta, float screenWidth) {
        // Não pode sair da borda direita
        posX = Math.min(posX + speed * delta, screenWidth * 0.7f);
    }

    public void andarEsquerda(float delta, float screenWidth) {
        // Não pode sair da borda esquerda
        if (posX < 0) posX = 0; 
        else posX -= speed * delta;;
    }

    public void pular(float delta){
        velocityY += gravity * delta; // Aplica a gravidade
        posY += velocityY * delta; // Atualiza a posição vertical

        // Quando o personagem tocar o chão, parar o pulo
        if (posY <= 100) {
            posY = 100; // Garante que o personagem não passe do chão
            isJumping = false; // O pulo foi finalizado
            velocityY = 0f; // Reseta a velocidade vertical
        }
    }
}
 