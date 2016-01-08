package com.potatoandtomato.games;

import com.potatoandtomato.common.MockGame;

public class CoveredChessGame extends MockGame {

	private boolean _initialized;

	public boolean isContinue;

	public CoveredChessGame(String gameId) {
		super(gameId);
	}

	@Override
	public void create() {
		super.create();
		initiateMockGamingKit(2, 1);
	}

	@Override
	public void onReady() {
		if(!_initialized){
			_initialized = true;
			Entrance entrance = new Entrance(getCoordinator());

			if(!isContinue){
				entrance.init();
			}
			else{
				entrance.onContinue();
			}
		}

	}
}