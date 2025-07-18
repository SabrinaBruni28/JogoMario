package com.badlogic.mario.Mario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.mario.Cenario;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Mario {
    private static final int FRAME_WIDTH = 29;  // Largura de cada sprite
    private static final int FRAME_HEIGHT = 40; // Altura de cada sprite
    private static final int HEIGHT_Y = 172;
    
    private float posX = 10; // Posição inicial do personagem no eixo X
    private float posY = HEIGHT_Y; // Posição inicial do personagem no eixo Y
    private float widthMario = 130;
    private float heightMario = 180;

    private float stateTime = 0f; // Tempo de estado da animação
    private float speed = 200; // Velocidade de movimento do personagem (pixels por segundo)
    private float velocityY = 3f; // Velocidade vertical do personagem
    private float gravity = -900f; // Aceleração da gravidade (negativa porque puxa para baixo)
    private float jumpHeight = 600f; // A altura do pulo

    private float invincibleTime = 0f;        // Tempo que ele já está invencível
    private float blinkInterval = 0.1f;       // Intervalo de piscar (a cada 0.1s ele alterna)
    private float blinkTimer = 0f;            // Timer para controlar o piscar
    
    private boolean visible = true;           // Se está visível ou não no frame atual
    private boolean sideRight = true;
    private boolean isJumping = false; // Verifica se o personagem está pulando
    private boolean isKilling = false;
    private boolean invincible = false;       // Está no estado invencível?
    
    private static final float INVINCIBLE_DURATION = 2f; // Duração total da invencibilidade em segundos

    private MarioStates currentState = MarioStates.STANDING_RIGHT; // Estado atual do personagem
    private Animation<TextureRegion> standRightAnimation, standLeftAnimation;
    private Animation<TextureRegion> standDownRightAnimation, standDownLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation, walkLeftAnimation;
    private Animation<TextureRegion> jumpingLeftAnimation, jumpingRightAnimation;

    private Sound somPulando, somPulandoBicho;

    public Mario() {

        somPulando = Gdx.audio.newSound(Gdx.files.internal("Sounds/jump.mp3"));
        somPulandoBicho = Gdx.audio.newSound(Gdx.files.internal("Sounds/stomp.mp3"));

        // Carregar a folha de sprites
        Texture spriteSheet = new Texture("smb_mario_sheet.png");

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
        //
    }

    public void input(float delta, Cenario cenario, float screenWidth, float screenHeight) {
        boolean isMoving = false;
        if (isJumping) isMoving = true;

        boolean temSuporte = cenario.verificarSuporteMario(this);
        if (!isJumping && !temSuporte && posY > HEIGHT_Y) {
            setJumping(true); // Caiu de plataforma
        }

        // Limites da tela
        if (posY < 100) posY = 100;
        else if (posY > screenHeight - heightMario) posY = screenHeight - heightMario;

        // Abaixar
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && !isJumping) {
            isMoving = true;
            currentState = sideRight ? MarioStates.STANDING_DOWN_RIGHT : MarioStates.STANDING_DOWN_LEFT;
            return;
        }

        // Movimento para a esquerda (pode mover mesmo pulando)
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            andarEsquerda(delta, screenWidth);
            currentState = isJumping ? MarioStates.JUMPING_LEFT : MarioStates.WALKING_LEFT;
            isMoving = true;
        }

        // Movimento para a direita (pode mover mesmo pulando)
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            andarDireita(delta, screenWidth);

            if (posX == screenWidth * 0.3f) {
                cenario.moverDireita(delta);
            }

            currentState = isJumping ? MarioStates.JUMPING_RIGHT : MarioStates.WALKING_RIGHT;
            isMoving = true;
        }

        // Pular
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && !isJumping) {
            long id = somPulando.play(); // Retorna o ID do som tocando
            somPulando.setVolume(id, 0.1f);
            currentState = sideRight ? MarioStates.JUMPING_RIGHT : MarioStates.JUMPING_LEFT;
            velocityY = jumpHeight;
            isJumping = true;
            isMoving = true;
            pular(delta);
        }

        // Atualiza pulo (aplica gravidade)
        if (isJumping) pular(delta);

        // Se não se move horizontalmente e não pula, fica parado
        if (!isMoving && !isJumping) {
            currentState = sideRight ? MarioStates.STANDING_RIGHT : MarioStates.STANDING_LEFT;
        }
    }

    public void draw(SpriteBatch batch) {
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;

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
        } 
        else {
            visible = true;
        }

        if (!visible) return; // Se invisível, não desenha (piscar)
        
        // Selecionar o frame atual com base no estado
        TextureRegion currentFrame = null;
        float offsetY = 0, offsetX = 0;
        switch (currentState) {
            case STANDING_RIGHT:
                currentFrame = standRightAnimation.getKeyFrame(stateTime, false);
                sideRight = true;
                offsetY = 5;
                break;
            case STANDING_LEFT:
                currentFrame = standLeftAnimation.getKeyFrame(stateTime, false);
                sideRight = false;
                offsetY = 5;
                break;
            case STANDING_DOWN_RIGHT:
                currentFrame = standDownRightAnimation.getKeyFrame(stateTime, false);
                sideRight = true;
                offsetY = - 18;
                offsetX = - 15;
                break;
            case STANDING_DOWN_LEFT:
                currentFrame = standDownLeftAnimation.getKeyFrame(stateTime, false);
                sideRight = false;
                offsetY = - 18;
                offsetX = 15;
                break;
            case WALKING_RIGHT:
                currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
                sideRight = true;
                break;
            case WALKING_LEFT:
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
            batch.draw(currentFrame, posX + offsetX, posY + offsetY, widthMario, heightMario); // Desenhar na posição atual
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

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
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
        float marginX = 10; // reduz largura em 12px no total
        float marginY = 10f; // reduz altura em 8px no total

        return new Rectangle(
            posX + marginX,
            posY + marginY,
            widthMario - marginX * 2,
            heightMario - marginY * 2
        );
    }

    public float getVelocidadeY() {
        return velocityY; // ou a variável que controla a gravidade/salto
    }

    public void pararQueda(float yPisandoEmCima) {
        this.posY = yPisandoEmCima - 10;
        this.velocityY = 0; // ou o que quer que controle a gravidade
        this.isJumping = false;
        if (this.sideRight)
            this.currentState = MarioStates.STANDING_RIGHT;
        else 
            this.currentState = MarioStates.STANDING_LEFT;
    }

    public void pararSubida(float yBatendoPorBaixo) {
        this.posY = yBatendoPorBaixo;
        this.velocityY = 0;
    }

    public void bloquearDireita(float novaPosX) {
        this.posX = novaPosX;
    }

    public void bloquearEsquerda(float novaPosX) {
        this.posX = novaPosX;
    }

    public boolean getSideRight() {
        return sideRight;
    }

}
 