package com.potatoandtomato.common;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;

/**
 * Created by SiongLeng on 27/12/2015.
 */
public interface IPTGame {

    void addInputProcessor(InputProcessor processor, int priority);
    void addInputProcessor(InputProcessor processor);
    void removeInputProcessor(InputProcessor processor);

    void setScreen(Screen screen);


}