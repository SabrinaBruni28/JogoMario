package com.badlogic.mario;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MarioGame extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }
}