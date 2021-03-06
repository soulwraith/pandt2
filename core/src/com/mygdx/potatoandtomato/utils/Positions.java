package com.mygdx.potatoandtomato.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.potatoandtomato.statics.Global;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public class Positions {

    private static int _height = 640;
    private static int _width = 360;
    public static int getHeight() {
        return Global.IS_POTRAIT ? _height : _width;
    }
    public static int getWidth() {
        return Global.IS_POTRAIT ? _width : _height;
    }

    public static int getScreenHeight() {
        return Gdx.graphics.getHeight() ;
    }
    public static int getScreenWidth() {
        return Gdx.graphics.getWidth();
    }


    public static void setWidth(int _width) { Positions._width = _width; }
    public static void setHeight(int _height) { Positions._height = _height; }

    public static float centerX(int textureWidth){
        return centerX((float) textureWidth);
    }

    public static float centerX(float textureWidth){
        return (float) getWidth() / 2 - textureWidth / 2;
    }

    public static float screenYToGdxY(float y, float screenHeight){
        return ((y * getHeight())/screenHeight);
    }

    public static float screenYToGdxY(float y){
        int myHeight = getHeight();
        int screenheight = getScreenHeight();

        return ((y * getHeight())/getScreenHeight());
    }

    public static float screenXToGdxX(float x){
        return ((x * getWidth()) /getScreenWidth());
    }

    public static Vector2 actorLocalToStageCoord(Actor actor){
        Vector2 coords = new Vector2(0, 0);
        actor.localToStageCoordinates(/*in/out*/coords);
        return coords;
    }

}
