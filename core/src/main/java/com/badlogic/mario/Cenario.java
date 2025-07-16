package com.badlogic.mario;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.mario.Enemys.Bicho;
import com.badlogic.mario.Enemys.Enemys;
import com.badlogic.mario.Items.Item;
import com.badlogic.mario.Items.ItemType;
import com.badlogic.mario.Items.Items;

public class Cenario {
    private float backgroundPosX = 0; // Posição do fundo (cenário) no eixo X
    private float backgroundSpeed = 200; // Velocidade do movimento do fundo
    private float backgroundWidth = 1920; // Largura do fundo para controle da movimentação
    private float backgroundHeight = 1080; // Altura do fundo (supondo que seja 1080p)
    private Texture background;
    private boolean moviment = false;

    public Enemys enemys;
    public Items items;

    private Music musicaDeFundo;
    private Sound somMoeda, somBatendo;

    public Cenario() {
        background = new Texture("cenario.jpg");
        enemys = new Enemys(); 
        items = new Items();

        setMusica();
        setSounds();
    }

    private void setMusica(){
        musicaDeFundo = Gdx.audio.newMusic(Gdx.files.internal("Sounds/music.mp3"));
        musicaDeFundo.setLooping(true); // toca em loop
        musicaDeFundo.setVolume(0.5f); // volume entre 0 e 1
        musicaDeFundo.play();
    }

    private void setSounds() {
        somMoeda = Gdx.audio.newSound(Gdx.files.internal("Sounds/coin.mp3"));
        somBatendo = Gdx.audio.newSound(Gdx.files.internal("Sounds/kick.wav"));
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
        enemys.draw(delta, batch, moviment);
        items.draw(delta, batch, moviment);
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

    public void verificarColisao(Mario mario) {
        Rectangle marioRect = mario.getBoundingBox();

        for (Bicho bicho : enemys.getBichos()) {
            Rectangle bichoRect = bicho.getBoundingBox();
            if (marioRect.overlaps(bichoRect)) {
                if (!mario.isKilling()) {
                    if (bicho.isMorto()) {
                        if (bicho.isTartaruga()) {
                            mario.bounce();
                            boolean marioEstaADireita = mario.getPosX() > bicho.getPosX();
                            bicho.iniciarDeslize(!marioEstaADireita); // Tartaruga desliza para lado oposto do Mario
                        }
                    }
                    else if (mario.getVelocidadeY() < 0 && mario.getBoundingBox().y > bicho.getBoundingBox().y + bicho.getBoundingBox().height * 0.5f) {
                        mario.bounce();
                        bicho.morrer();
                    } 
                    else {
                        mario.takeDamage();
                    }
                }
            }
        }
    }

    public void verificarColisaoEnemys() {
        Array<Bicho> bichos = enemys.getBichos();

        for (int i = 0; i < bichos.size; i++) {
            Bicho bichoDeslizando = bichos.get(i);

            if (!bichoDeslizando.isAtivo()) {
                enemys.removeEmeny(i);
                i--; // Ajusta o índice após remover
                continue;
            }

            if (bichoDeslizando.isDeslizando() && bichoDeslizando.isAtivo()) {

                Rectangle deslizandoRect = bichoDeslizando.getBoundingBox();

                if (bichoDeslizando.getVelocidadeDeslizamento() == 0) {
                    bichoDeslizando.pararDeslize(); 
                    continue;
                }

                for (int j = 0; j < bichos.size; j++) {
                    if (i == j) continue;

                    Bicho outroBicho = bichos.get(j);

                    if (outroBicho.isAtivo() && !outroBicho.isMorto()) {
                        Rectangle outroRect = outroBicho.getBoundingBox();

                        if (deslizandoRect.overlaps(outroRect)) {
                            outroBicho.matar();
                        }
                    }
                }
            }
        }
    }

    public void verificarColisaoComItens(Mario mario) {
        Rectangle marioRect = mario.getBoundingBox();

        for (int i = 0; i < items.getItens().size; i++) {
            Item item = items.getItens().get(i);

            if (!item.isAtivo()) continue;

            Rectangle itemRect = item.getBoundingBox();

            if (marioRect.overlaps(itemRect)) {
                ItemType tipo = item.getTipo();
                boolean vindoDaDireita = mario.getPosX() < itemRect.x &&
                    marioRect.x + marioRect.width > itemRect.x;
                boolean vindoDaEsquerda = mario.getPosX() > itemRect.x &&
                    marioRect.x < itemRect.x + itemRect.width;
                boolean vindoPorBaixo = mario.getVelocidadeY() > 0 &&
                    marioRect.y + marioRect.height <= itemRect.y + 15f;
                boolean vindoPorCima = mario.getVelocidadeY() <= 0 &&
                    marioRect.y > itemRect.y + itemRect.height * 0.5f;

                switch (tipo) {
                    case MOEDA:
                        item.coletar();
                        somMoeda.play();
                        // talvez adicionar pontuação
                        break;

                    case BLOCO:
                    case BLOCO_TIJOLO:
                        if (vindoPorBaixo) {
                            mario.pararSubida(itemRect.y - marioRect.height);
                            somBatendo.play();
                        } 
                        else if (vindoPorCima) {
                            mario.pararQueda(itemRect.y + itemRect.height);
                        }
                        else if (vindoDaDireita) {
                            mario.bloquearDireita(itemRect.x - marioRect.width);
                        } 
                        else if (vindoDaEsquerda) {
                            mario.bloquearEsquerda(itemRect.x + itemRect.width);
                        }
                        break;

                    case CANO_VERDE:
                    case CANO_AMARELO:
                        if (vindoPorBaixo) {
                            mario.pararSubida(itemRect.y - marioRect.height);
                            somBatendo.play();
                        } 
                        if (vindoPorCima) {
                            mario.pararQueda(itemRect.y + itemRect.height);
                        }
                        else if (vindoDaDireita) {
                            mario.bloquearDireita(itemRect.x - marioRect.width);
                        } 
                        else if (vindoDaEsquerda) {
                            mario.bloquearEsquerda(itemRect.x + itemRect.width);
                        }
                        break;
                    // Adicione mais reações aqui...

                    default:
                        break;
                }
            }
        }
    }

    public boolean verificarSuporte(Mario mario) {
        Rectangle marioRect = mario.getBoundingBox();

        // Checa se há algum item logo abaixo do Mario
        for (Item item : items.getItens()) {
            if (!item.isAtivo()) continue;

            Rectangle itemRect = item.getBoundingBox();

            // Considera como suporte se a parte inferior do Mario estiver tocando ou quase tocando o item
            float margem = 10f; // tolerância para contato com chão
            boolean estaSobreItem = 
                marioRect.y > itemRect.y + itemRect.height - margem &&
                marioRect.y < itemRect.y + itemRect.height + margem &&
                marioRect.x + marioRect.width > itemRect.x &&
                marioRect.x < itemRect.x + itemRect.width;

            if (estaSobreItem) {
                return true;
            }
        }

        // Se nenhum item suporta o Mario, retorna false
        return false;
    }

}
