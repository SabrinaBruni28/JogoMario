package com.badlogic.mario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Mario {
    private float stateTime = 0f; // Tempo de estado da animação
    private static final int FRAME_WIDTH = 29;  // Largura de cada sprite
    private static final int FRAME_HEIGHT = 40; // Altura de cada sprite
    private static final int HEIGHT_Y = 172;
    private Texture spriteSheet;

    private enum State {
        STANDING_RIGHT, STANDING_LEFT, STANDING_DOWN_RIGHT, STANDING_DOWN_LEFT, WALKING_RIGHT, WALKING_LEFT, JUMPING_RIGHT, JUMPING_LEFT
    }

    private float posX = 10; // Posição inicial do personagem no eixo X
    private float posY = HEIGHT_Y; // Posição inicial do personagem no eixo Y

    private float widthMario = 150;
    private float heightMario = 200;
    private float speed = 200; // Velocidade de movimento do personagem (pixels por segundo)

    private boolean isJumping = false; // Verifica se o personagem está pulando
    private float velocityY = 2f; // Velocidade vertical do personagem
    private float gravity = -800f; // Aceleração da gravidade (negativa porque puxa para baixo)
    private float jumpHeight = 400f; // A altura do pulo
    private boolean sideRight = true;

    private State currentState = State.STANDING_RIGHT; // Estado atual do personagem
    private Animation<TextureRegion> standRightAnimation, standLeftAnimation;
    private Animation<TextureRegion> standDownRightAnimation, standDownLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation, walkLeftAnimation;
    private Animation<TextureRegion> jumpingLeftAnimation, jumpingRightAnimation;

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

        TextureRegion finalFrame = new TextureRegion(spriteSheet, 13 * FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT);
        standDownRightAnimation = new Animation<>(0.4f, finalFrame);
        standDownRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        standDownLeftAnimation = new Animation<>(0.4f, spriteRegions[0][0]);
        standDownLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        walkRightAnimation = new Animation<>(0.4f, spriteRegions[0][9], spriteRegions[0][10]);
        walkRightAnimation.setPlayMode(Animation.PlayMode.LOOP);

        walkLeftAnimation = new Animation<>(0.4f, spriteRegions[0][3], spriteRegions[0][4]);
        walkLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);

        jumpingLeftAnimation = new Animation<>(0.4f, spriteRegions[0][1]);
        jumpingLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        jumpingRightAnimation = new Animation<>(0.4f, spriteRegions[0][12]);
        jumpingRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    public void dispose() {
        spriteSheet.dispose();
    }

    public void input(float delta, Cenario cenario, float screenWidth, float screenHeight) {
        boolean isMoving = false;
        if (isJumping) isMoving = true;

        // Verificar se o personagem saiu dos limites da tela e ajustar a posição
        
        if (posY < 100) // Não pode sair da borda inferior
            posY = 100;

        else if (posY > screenHeight - heightMario) // Não pode sair da borda superior (200 é a altura do personagem)
            posY = screenHeight - heightMario;

        // Movimento para a esquerda
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            andarEsquerda(delta, screenWidth);
            if (isJumping)
                currentState = State.JUMPING_LEFT;
            else
                currentState = State.WALKING_LEFT;
            isMoving = true;
        }

        // Movimento para a direita
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            andarDireita(delta, screenWidth);
            if (posX == screenWidth * 0.3f) {
                cenario.moverDireita(delta);
                cenario.setMoviment(true);
            }
            else {
                cenario.setMoviment(false);
            }

            if (isJumping)
                currentState = State.JUMPING_RIGHT;
            else
                currentState = State.WALKING_RIGHT;
            isMoving = true;
        }


        // Se o personagem não estiver pulando e a tecla de pulo estiver pressionada
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !isJumping) {
            if (!sideRight)
                currentState = State.JUMPING_LEFT;
            else
                currentState = State.JUMPING_RIGHT;
            
            velocityY = jumpHeight; // Inicia o pulo
            isJumping = true;
            isMoving = true;
            pular(delta);
        }

        // Atualizar a posição vertical com base na velocidade vertical e gravidade
        if (isJumping) pular(delta);

        // Verificar se o personagem deve abaixar
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            isMoving = true;
            if (!sideRight)
                currentState = State.STANDING_DOWN_LEFT;
            else
                currentState = State.STANDING_DOWN_RIGHT;
        } 

        // Se não estiver se movendo, definir o estado como parado
        if (!isMoving) {
            if (sideRight) {
                currentState = State.STANDING_RIGHT;
            } 
            else {
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
                posY = HEIGHT_Y;
                currentFrame = standRightAnimation.getKeyFrame(stateTime, false);
                sideRight = true;
                break;
            case STANDING_LEFT:
                posY = HEIGHT_Y;
                currentFrame = standLeftAnimation.getKeyFrame(stateTime, false);
                sideRight = false;
                break;
            case STANDING_DOWN_RIGHT:
                posY = HEIGHT_Y - 25;
                currentFrame = standDownRightAnimation.getKeyFrame(stateTime, false);
                sideRight = true;
                break;
            case STANDING_DOWN_LEFT:
                posY = HEIGHT_Y - 25;
                currentFrame = standDownLeftAnimation.getKeyFrame(stateTime, false);
                sideRight = false;
                break;
            case WALKING_RIGHT:
                posY = HEIGHT_Y;
                currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
                sideRight = true;
                break;
            case WALKING_LEFT:
                posY = HEIGHT_Y;
                currentFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
                sideRight = false;
                break;
            case JUMPING_RIGHT:
                currentFrame = jumpingRightAnimation.getKeyFrame(stateTime, true);
                sideRight = true;
                break;
            case JUMPING_LEFT:
                currentFrame = jumpingLeftAnimation.getKeyFrame(stateTime, true);
                sideRight = false;
                break;
        }

        batch.begin();
            batch.draw(currentFrame, posX, posY, widthMario, heightMario); // Desenhar na posição atual
        batch.end();
    }

    public void andarDireita(float delta, float screenWidth) {
        // Não pode sair da borda direita
        posX = Math.min(posX + speed * delta, screenWidth * 0.3f);
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
        if (posY <= HEIGHT_Y) {
            posY = HEIGHT_Y; // Garante que o personagem não passe do chão
            isJumping = false; // O pulo foi finalizado
            velocityY = 0f; // Reseta a velocidade vertical
        }
    }
}
 