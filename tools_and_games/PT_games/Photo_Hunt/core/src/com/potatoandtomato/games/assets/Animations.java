package com.potatoandtomato.games.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.assets.AnimationAssets;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class Animations extends AnimationAssets {


    public Animations(AssetManager assetManager) {
        super(assetManager);
    }


    public enum Name{
        KNIGHT_WALK, KNIGHT_RUN, KNIGHT_ATK,
        KING_NORMAL, KING_PANIC, KING_WIN, KING_LOSE
    }



}