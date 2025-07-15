package com.badlogic.mario.Enemys;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Bicho {
    private final Rectangle objectRectangle = new Rectangle();

    private float stateTime = 0;
    private float speed;
    private boolean horizontal;
    private float end;
    private boolean sobeNoPonto;
    private float pontoDeSubida;
    private float alturaDeSubida;
    private boolean jaSubiu = false;
    private boolean ativo = true;

    private EnemyType tipo;

    private static final int FRAME_WIDTH = 30;
    private static final int FRAME_HEIGHT = 30;
    private static final int HEIGHT_Y = 172;

    private Animation<TextureRegion> animation;
    private TextureRegion[][] spriteRegions;
    private Enemy enemy;

    private TextureRegion morteFrame;
    private boolean morto = false;
    private boolean matar = false;
    private float tempoMorte = 0f;
    private static final float TEMPO_MORTE = 0.5f; // tempo visível após morrer

    private boolean deslizando = false;
    private float velocidadeDeslizamento = 0f;

    private boolean pulando = false;
    private float velocidadeY = 0;
    private static final float GRAVIDADE = -800f;
    private static final float FORCA_PULO = 400f;

    public Bicho(
        int x1, int y1, int x2, int y2,
        int xMorte, int yMorte,
        float startX, float startY,
        boolean horizontal,
        float end,
        float speed,
        boolean sobeNoPonto,
        float pontoDeSubida,
        float alturaDeSubida,
        EnemyType tipo
    ) {
        // Carrega sprite sheet e configura animação
        this.tipo = tipo;

        Texture spriteSheet = new Texture("smb_enemies_sheet.png");
        spriteRegions = TextureRegion.split(spriteSheet, FRAME_WIDTH, FRAME_HEIGHT);
        animation = new Animation<>(0.4f, spriteRegions[x1][y1], spriteRegions[x2][y2]);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        // Sprite de morte
        morteFrame = spriteRegions[xMorte][yMorte];

        // Cria o inimigo
        Sprite sprite = new Sprite(animation.getKeyFrame(0));
        sprite.setSize(100, 120);
        sprite.setPosition(startX, startY);
        this.enemy = new Enemy(sprite, animation);

        // Armazena parâmetros de lógica
        this.horizontal = horizontal;
        this.end = end;
        this.speed = speed;
        this.sobeNoPonto = sobeNoPonto;
        this.pontoDeSubida = pontoDeSubida;
        this.alturaDeSubida = alturaDeSubida;
    }

    public void update(float delta) {
        if (matar) {
            if (pulando) {
                velocidadeY += GRAVIDADE * delta;
                enemy.sprite.translateY(velocidadeY * delta);

                // Quando cair abaixo do chão, termina o pulo e remove
                if (enemy.sprite.getY() <= HEIGHT_Y - 25) {
                    enemy.sprite.setY(HEIGHT_Y - 25);
                    pulando = false;
                    tempoMorte += delta;
                }
            } else {
                tempoMorte += delta;
                if (tempoMorte >= TEMPO_MORTE) {
                    ativo = false;
                    deslizando = false;
                }
            }
        }

        else if (morto) {
            tempoMorte += delta;
            if (isTartaruga()) {
                if (!deslizando) {
                    // parado
                } else {
                    enemy.sprite.translateX(velocidadeDeslizamento * delta);

                    // Atualizar o retângulo de colisão
                    objectRectangle.set(
                        enemy.sprite.getX(),
                        enemy.sprite.getY(),
                        enemy.sprite.getWidth(),
                        enemy.sprite.getHeight()
                    );
                }
            }
            else if (tempoMorte >= TEMPO_MORTE) {
                ativo = false; // some só se estiver morto parado
                deslizando = false;
            }
        }
        else {
            stateTime += delta;
            logic(delta);
        }
    }

    public void iniciarDeslize(boolean paraDireita) {
        deslizando = true;
        morto = true; // Continua morto, mas agora deslizando
        velocidadeDeslizamento = paraDireita ? 300f : -300f;
        tempoMorte = 0; // Reinicia o tempo para não desativar
    }

    public void draw(SpriteBatch batch) {
        if (!ativo) return;

        batch.begin();
        if (!morto) {
            TextureRegion frame = animation.getKeyFrame(stateTime, true);
            enemy.sprite.setRegion(frame);
        } 
        else {
            enemy.sprite.setRegion(morteFrame);
        }
        enemy.sprite.draw(batch);
        batch.end();
    }

    private void logic(float delta) {
        Sprite sprite = enemy.sprite;
        float deslocamento = speed * delta;

        if (horizontal) {
            sprite.translateX(-deslocamento);
        } else {
            sprite.translateY(-deslocamento);
        }

        objectRectangle.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());

        // Verifica subida (uma vez só)
        if (sobeNoPonto && !jaSubiu) {
            if (horizontal && sprite.getX() <= pontoDeSubida) {
                sprite.translateY(alturaDeSubida);
                jaSubiu = true;
            } else if (!horizontal && sprite.getY() <= pontoDeSubida) {
                sprite.translateX(alturaDeSubida);
                jaSubiu = true;
            }
        }

        // Limite da tela
        if (horizontal && sprite.getX() + sprite.getWidth() < end) {
            ativo = false;
            deslizando = false;
        } else if (!horizontal && sprite.getY() + sprite.getHeight() < end) {
            ativo = false;
            deslizando = false;
        }
    }

    public void morrer() {
        morto = true;
        deslizando = false;
        tempoMorte = 0;
        enemy.sprite.setY(HEIGHT_Y - 25);
        enemy.sprite.setRegion(morteFrame);
    }

    public void matar() {
        matar = true;
        morto = true;
        tempoMorte = 0.2f;
        pulando = true;
        velocidadeY = FORCA_PULO; // começa o pulo
        enemy.sprite.setRegion(morteFrame);
    }

    public Sprite getSprite() {
        return enemy.sprite;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public boolean isDeslizando() {
        return deslizando;
    }

    public void pararDeslize() {
        deslizando = false;
        velocidadeDeslizamento = 0;
    }

    public boolean isMorto() {
        return morto;
    }

    public float getPosX() {
        return enemy.sprite.getX();
    }

    public float getPosY() {
        return enemy.sprite.getY();
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Rectangle getBoundingBox() {
        return objectRectangle;
    }

    public float getVelocidadeDeslizamento() {
        return velocidadeDeslizamento;
    }

    public boolean isType(EnemyType tipo) {
        return this.tipo == tipo;
    }

    public boolean isTartaruga() {
        if (isType(EnemyType.TARTARUGA_AZUL) || isType(EnemyType.TARTARUGA_VERDE) || isType(EnemyType.TARTARUGA_VERMELHA)) 
            return true;
        
        if (isType(EnemyType.TARTARUGA_VOADORA_AZUL) || isType(EnemyType.TARTARUGA_VOADORA_VERDE) || isType(EnemyType.TARTARUGA_VOADORA_VERMELHA)) 
            return true;
        
        return false;
    }
}
