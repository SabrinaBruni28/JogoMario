package com.badlogic.mario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

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

    private float widthMario = 130;
    private float heightMario = 180;
    private float speed = 200; // Velocidade de movimento do personagem (pixels por segundo)

    private boolean isJumping = false; // Verifica se o personagem está pulando
    private float velocityY = 3f; // Velocidade vertical do personagem
    private float gravity = -900f; // Aceleração da gravidade (negativa porque puxa para baixo)
    private float jumpHeight = 600f; // A altura do pulo
    private boolean sideRight = true;

    private boolean invincible = false;       // Está no estado invencível?
    private float invincibleTime = 0f;        // Tempo que ele já está invencível
    private static final float INVINCIBLE_DURATION = 2f; // Duração total da invencibilidade em segundos

    private float blinkInterval = 0.1f;       // Intervalo de piscar (a cada 0.1s ele alterna)
    private float blinkTimer = 0f;            // Timer para controlar o piscar
    private boolean visible = true;           // Se está visível ou não no frame atual
    private boolean isKilling = false;

    private State currentState = State.STANDING_RIGHT; // Estado atual do personagem
    private Animation<TextureRegion> standRightAnimation, standLeftAnimation;
    private Animation<TextureRegion> standDownRightAnimation, standDownLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation, walkLeftAnimation;
    private Animation<TextureRegion> jumpingLeftAnimation, jumpingRightAnimation;

    private Sound somPulando, somPulandoBicho;

    public Mario() {

        somPulando = Gdx.audio.newSound(Gdx.files.internal("Sounds/jump.mp3"));
        somPulandoBicho = Gdx.audio.newSound(Gdx.files.internal("Sounds/stomp.mp3"));

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

        // Limites da tela
        if (posY < 100)
            posY = 100;
        else if (posY > screenHeight - heightMario)
            posY = screenHeight - heightMario;

        // Abaixar
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !isJumping) {
            isMoving = true;
            if (!sideRight)
                currentState = State.STANDING_DOWN_LEFT;
            else
                currentState = State.STANDING_DOWN_RIGHT;
            return;
        }

        // Movimento para a esquerda (pode mover mesmo pulando)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            andarEsquerda(delta, screenWidth);
            currentState = isJumping ? State.JUMPING_LEFT : State.WALKING_LEFT;
            isMoving = true;
        }

        // Movimento para a direita (pode mover mesmo pulando)
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            andarDireita(delta, screenWidth);

            if (posX == screenWidth * 0.3f) {
                cenario.moverDireita(delta);
                cenario.setMoviment(true);
            } else {
                cenario.setMoviment(false);
            }

            currentState = isJumping ? State.JUMPING_RIGHT : State.WALKING_RIGHT;
            isMoving = true;
        } else {
            cenario.setMoviment(false);
        }

        // Pular
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !isJumping) {
            long id = somPulando.play(); // Retorna o ID do som tocando
            somPulando.setVolume(id, 0.1f);
            currentState = sideRight ? State.JUMPING_RIGHT : State.JUMPING_LEFT;
            velocityY = jumpHeight;
            isJumping = true;
            isMoving = true;
            pular(delta);
        }

        // Atualiza pulo (aplica gravidade)
        if (isJumping) pular(delta);

        // Se não se move horizontalmente e não pula, fica parado
        if (!isMoving && !isJumping) {
            currentState = sideRight ? State.STANDING_RIGHT : State.STANDING_LEFT;
        }
    }

    public void draw(SpriteBatch batch) {
        float delta = Gdx.graphics.getDeltaTime();

        if (invincible) {
            invincibleTime += delta;
            blinkTimer += delta;

            if (blinkTimer >= blinkInterval) {
                blinkTimer = 0f;
                visible = !visible; // Alterna visibilidade para efeito piscar
            }

            if (invincibleTime >= INVINCIBLE_DURATION) {
                invincible = false; // Acabou a invencibilidade
                visible = true; // Garantir que termine visível
            }
        } else {
            visible = true;
        }

        if (!visible) return; // Se invisível, não desenha (piscar)
        
        // ... seu código normal para escolher currentFrame e desenhar
        stateTime += delta;

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
            isKilling = false;
            velocityY = 0f; // Reseta a velocidade vertical
        }
    }

    public float getVelocidadeY() {
        return velocityY;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setKilling(boolean killing) {
        this.isKilling = killing;
    }

    public boolean isKilling() {
        return isKilling;
    }

    public void takeDamage() {
        if (invincible) return; // Se já estiver invencível, ignora

        invincible = true;
        invincibleTime = 0f;
        blinkTimer = 0f;
        visible = false; // Começa invisível para efeito piscar
        // Aqui você pode também reduzir vida, ativar sons, etc.
    }

    public void bounce() {
        // Faz o Mario pular levemente ao matar inimigo (efeito clássico)
        somPulandoBicho.play();
        velocityY = jumpHeight / 2; // impulso menor para pulo rápido
        isJumping = true;
        isKilling = true;
    }

    public Rectangle getBoundingBox() {
        float marginX = 6f; // reduz largura em 12px no total
        float marginY = 4f; // reduz altura em 8px no total

        return new Rectangle(
            posX + marginX,
            posY + marginY,
            widthMario - marginX * 2,
            heightMario - marginY * 2
        );
    }
}
 