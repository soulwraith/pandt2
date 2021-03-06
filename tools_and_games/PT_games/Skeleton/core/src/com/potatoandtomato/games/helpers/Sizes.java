package com.potatoandtomato.games.helpers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by SiongLeng on 4/12/2015.
 */
public class Sizes {

    public static Vector2 resize(float finalWidth, TextureRegion textureRegion){
        float percent = textureRegion.getRegionWidth() / finalWidth;
        return new Vector2(finalWidth, textureRegion.getRegionHeight() / percent);
    }

    public static Vector2 resizeByH(float finalHeight, TextureRegion textureRegion){
        float percent = textureRegion.getRegionHeight() / finalHeight;
        return new Vector2(textureRegion.getRegionWidth() / percent, finalHeight);
    }

}
