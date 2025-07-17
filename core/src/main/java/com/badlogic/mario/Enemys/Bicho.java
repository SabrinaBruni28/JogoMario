package com.badlogic.mario.Enemys;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.mario.Cenario;
import com.badlogic.mario.Object;
import com.badlogic.mario.Items.Item;

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
    private boolean andandoParaDireita = false;
    private boolean emSuporte = false;

    private EnemyType tipo;

    private static final int FRAME_WIDTH = 30;
    private static final int FRAME_HEIGHT = 30;
    private static final int HEIGHT_Y = 172;

    private Animation<TextureRegion> animation;
    private TextureRegion[][] spriteRegions;
    private Object enemy;

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

    private Sound somMatando;

    public Bicho(
        int x1, int y1, int x2, int y2,
        int xMorte, int yMorte,
        float startX, float startY,
        boolean horizontal,
        float end,
        float speed,
        boolean sobeNoPonto,
        boolean andandoParaDireita,
        float pontoDeSubida,
        float alturaDeSubida,
        EnemyType tipo,
        String arquivo
    ) {
        // Carrega sprite sheet e configura animação
        this.tipo = tipo;

        somMatando = Gdx.audio.newSound(Gdx.files.internal("Sounds/kick.wav"));

        Texture spriteSheet = new Texture(arquivo);
        spriteRegions = TextureRegion.split(spriteSheet, FRAME_WIDTH, FRAME_HEIGHT);
        animation = new Animation<>(0.4f, spriteRegions[x1][y1], spriteRegions[x2][y2]);
        animation.setPlayMode(Animation.PlayMode.LOOP);

        // Sprite de morte
        morteFrame = spriteRegions[xMorte][yMorte];

        // Cria o inimigo
        Sprite sprite = new Sprite(animation.getKeyFrame(0));
        sprite.setSize(100, 120);
        sprite.setPosition(startX, startY);
        this.enemy = new Object(sprite, animation);

        // Armazena parâmetros de lógica
        this.horizontal = horizontal;
        this.end = end;
        this.speed = speed;
        this.sobeNoPonto = sobeNoPonto;
        this.pontoDeSubida = pontoDeSubida;
        this.alturaDeSubida = alturaDeSubida;
        this.andandoParaDireita = andandoParaDireita;

    }

    public void update(float delta, Cenario cenario) {
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
            
            if (enemy.sprite.getY() > HEIGHT_Y - 25 || velocidadeY != 0) {
                boolean suporteChao = !deslizando && enemy.sprite.getY() <= HEIGHT_Y - 25;
                boolean suporteItem = !deslizando && !suporteChao && cenario.verificarSuporteBicho(this);
                
                if (!emSuporte) {
                    if (suporteItem) {
                        alinharComTopoDoItem(cenario);
                        emSuporte = true;
                        velocidadeY = 0;
                    }
                    else if (suporteChao) {
                        emSuporte = false;
                        enemy.sprite.setY(HEIGHT_Y - 25);
                        velocidadeY = 0;
                    } 
                    
                    else {
                        emSuporte = false;
                        velocidadeY += GRAVIDADE * delta;
                        enemy.sprite.translateY(velocidadeY * delta);
                    }
                // Atualizar o retângulo de colisão
                objectRectangle.set(
                    enemy.sprite.getX(),
                    enemy.sprite.getY(),
                    enemy.sprite.getWidth(),
                    enemy.sprite.getHeight()
                );
                }
            }

            if (isTartaruga()) {
                if (!deslizando) {
                    // parado
                } 
                else {
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

    private void alinharComTopoDoItem(Cenario cenario) {
        Rectangle bichoRect = getBoundingBox();
        float baseDoBicho = bichoRect.y;

        for (Item item : cenario.getItems().getItens()) {
            if (!item.isAtivo()) continue;

            Rectangle itemRect = item.getBoundingBox();
            float topoDoItem = itemRect.y + itemRect.height;

            float margem = 2f;
            boolean alinhadoHorizontalmente =
                bichoRect.x + bichoRect.width > itemRect.x &&
                bichoRect.x < itemRect.x + itemRect.width;

            boolean tocandoEmCima =
                Math.abs(baseDoBicho - topoDoItem) <= margem;

            if (alinhadoHorizontalmente && tocandoEmCima) {
                // Seta a Y do sprite para o topo do item
                float novaY = topoDoItem;
                enemy.sprite.setY(novaY);
                emSuporte = true;
                return;
            }
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
            float direcao = andandoParaDireita ? 1f : -1f;
            sprite.translateX(direcao * deslocamento);
        }
        else {
            float direcao = andandoParaDireita ? 1f : -1f;
            sprite.translateY(direcao * deslocamento);
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
        somMatando.play();
        morto = true;
        deslizando = false;
        tempoMorte = 0;
        enemy.sprite.setRegion(morteFrame);
    }

    public void matar() {
        somMatando.play();
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

    public void setPosX(float posX) {
        enemy.sprite.setX(posX);
    }

    public float getPosY() {
        return enemy.sprite.getY();
    }

    public void setPosY(float posY) {
        enemy.sprite.setY(posY);
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Rectangle getBoundingBox() {
        float marginX = 12f; // Reduz 10px de cada lado (horizontal)
        float marginY = 10f;  // Reduz 5px de cada lado (vertical)

        objectRectangle.set(
            enemy.sprite.getX() + marginX,
            enemy.sprite.getY() + marginY,
            enemy.sprite.getWidth() - marginX * 2,
            enemy.sprite.getHeight() - marginY * 2
        );
        return objectRectangle;
    }

    public float getVelocidadeDeslizamento() {
        return velocidadeDeslizamento;
    }

    public boolean isType(EnemyType tipo) {
        return this.tipo == tipo;
    }

    public boolean isTartaruga() {
        if (
            isType(EnemyType.TARTARUGA_AZUL_L) || isType(EnemyType.TARTARUGA_VERDE_L) || isType(EnemyType.TARTARUGA_VERMELHA_L) ||
            isType(EnemyType.TARTARUGA_AZUL_R) || isType(EnemyType.TARTARUGA_VERDE_R) || isType(EnemyType.TARTARUGA_VERMELHA_R)
        ) 
            return true;
        
        if (
            isType(EnemyType.TARTARUGA_VOADORA_AZUL_L) || isType(EnemyType.TARTARUGA_VOADORA_VERDE_L) || isType(EnemyType.TARTARUGA_VOADORA_VERMELHA_L) ||
            isType(EnemyType.TARTARUGA_VOADORA_AZUL_R) || isType(EnemyType.TARTARUGA_VOADORA_VERDE_R) || isType(EnemyType.TARTARUGA_VOADORA_VERMELHA_R)
            
        )
            return true;
        
        return false;
    }

    public void mover(float dx) {
        enemy.sprite.setX(enemy.sprite.getX() + dx);
    }

    public Bicho inverterDirecao() {
        andandoParaDireita = !andandoParaDireita;
        return inverterTipo(this.speed);
    }

    public void inverterDeslizamento() {
        velocidadeDeslizamento = -velocidadeDeslizamento;
    }

    public Bicho inverterTipo(float speed) {
        EnemyType tipoInvertido = EnemyFactory.inverterTipo(this.tipo);
        Bicho invertido = EnemyFactory.create(tipoInvertido, this.getPosX(), speed);
        invertido.setPosX(getPosX()); // mantém posição
        invertido.setPosY(getPosY());
        return invertido;
    }
}
