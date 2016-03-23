package com.potatoandtomato.games;

import com.firebase.client.Firebase;
import com.potatoandtomato.common.GameEntrance;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.helpers.*;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.references.BattleRef;
import com.potatoandtomato.games.screens.BoardLogic;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    BoardLogic _logic;
    Services _services;

    public Entrance(GameCoordinator gameCoordinator) {
        super(gameCoordinator);

        Assets assets = new Assets(gameCoordinator);
        assets.loadAll(null);
        Database database = new Database(gameCoordinator);

        _services =  new Services(assets, new Texts(), new SoundsWrapper(assets, gameCoordinator),
                database, new ScoresHelper(gameCoordinator, database));
        getGameCoordinator().finishLoading();
    }

    @Override
    public void init() {
        _logic = new BoardLogic(_services, getGameCoordinator());
        _logic.init();
        getGameCoordinator().getGame().setScreen((_logic.getScreen()));
        Firebase db = getGameCoordinator().getFirebase();
    }

    @Override
    public void onContinue() {
        _logic = new BoardLogic(_services, getGameCoordinator());
        getGameCoordinator().getGame().setScreen((_logic.getScreen()));
        _logic.continueGame();
    }

    @Override
    public void dispose() {
        _services.getSoundsWrapper().dispose();
        if(_logic != null) _logic.dispose();
    }


}
