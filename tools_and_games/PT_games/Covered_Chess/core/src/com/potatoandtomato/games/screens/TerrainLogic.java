package com.potatoandtomato.games.screens;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.games.absint.ActionListener;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.helpers.*;
import com.potatoandtomato.games.models.ChessModel;
import com.potatoandtomato.games.models.TerrainModel;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 30/12/2015.
 */
public class TerrainLogic {

    private TerrainModel _terrainModel;
    private TerrainLogic _me;
    private Assets _assets;
    private TerrainActor terrainActor;
    private ChessLogic chessLogic;
    private ActionListener actionListener;

    private GameCoordinator _coordinator;
    private BattleReference _battleRefs;
    private GameDataController _gameDataController;
    private Sounds _sounds;

    public TerrainLogic(TerrainModel _terrainModel, Assets _assets,
                        GameCoordinator _coordinator, ChessModel chessModel,
                        Sounds sounds, GameDataController gameDataController,
                        BattleReference battleReference) {
        this._me = this;
        this._terrainModel = _terrainModel;
        this._assets = _assets;
        this._coordinator = _coordinator;
        this._sounds = sounds;
        this._gameDataController = gameDataController;
        this._battleRefs = battleReference;

        chessLogic = new ChessLogic(chessModel, _assets, sounds, gameDataController);
        terrainActor = new TerrainActor(_assets, chessLogic.getChessActor());

        setListeners();
    }

    public void moveChessToThis(final TerrainLogic fromLogic, boolean showMoveAnimation, final boolean isFromWon){

        final ChessModel fromChessModel = fromLogic.getChessLogic().getChessModel().clone();
        final Actor fromChessClone = fromLogic.getChessLogic().cloneActor();

        final Runnable afterMove = new Runnable() {
            @Override
            public void run() {

                if(isEmpty()){
                    _sounds.playSounds(Sounds.Name.MOVE_CHESS);
                    fromChessModel.setDragging(false);
                    chessLogic.setChessModel(fromChessModel);
                    actionListener.changeTurnReady();
                }
                else {
                    getTerrainActor().showBattle();
                    _sounds.playSounds(Sounds.Name.FIGHT_CHESS);
                    _coordinator.requestVibrate(1500);

                    ChessModel winnerChessModel;
                    final ChessType loserChessType;
                    final boolean loserIsYellow;
                    final Actor clone;

                    if(isFromWon){
                        winnerChessModel = fromChessModel;
                        loserChessType = _me.getChessLogic().getChessModel().getChessType();
                        loserIsYellow = _me.getChessLogic().getChessModel().getChessColor() == ChessColor.YELLOW;
                        clone = _me.getChessLogic().cloneActor();
                    }
                    else{
                        winnerChessModel = _me.getChessLogic().getChessModel();
                        loserChessType = fromChessModel.getChessType();
                        loserIsYellow = fromChessModel.getChessColor() == ChessColor.YELLOW;
                        clone = fromChessClone;
                    }

                    final Stage _stage = _me.getTerrainActor().getStage();

                    winnerChessModel.setDragging(false);
                    chessLogic.setChessModel(winnerChessModel);

                    Threadings.delay(1500, new Runnable() {
                        @Override
                        public void run() {
                            clone.setOrigin(Align.center);
                            clone.setPosition(Positions.actorLocalToStageCoord(_me.getChessLogic().getChessActor()).x,
                                    Positions.actorLocalToStageCoord(_me.getChessLogic().getChessActor()).y);
                            _stage.addActor(clone);

                            clone.addAction(parallel(
                                    moveTo(loserIsYellow ? 30 : _coordinator.getGameWidth() - 50, _coordinator.getGameHeight(), 0.5f),
                                    scaleTo(0.3f, 0.3f, 0.5f),
                                    forever(rotateBy(360, 1f))));
                            Threadings.delay(500, new Runnable() {
                                @Override
                                public void run() {
                                    getTerrainActor().hideBattle();
                                    actionListener.changeTurnReady();
                                    actionListener.onChessKilled(loserChessType);
                                }
                            });

                        }
                    });
                }


            }
        };

        if(showMoveAnimation){
            final Actor clone = fromLogic.getChessLogic().cloneActor();
            Stage stage = this.getTerrainActor().getStage();
            Vector2 initialPositionOnStage = Positions.actorLocalToStageCoord(fromLogic.getChessLogic().getChessActor());
            Vector2 finalPositionOnStage = Positions.actorLocalToStageCoord(this.getTerrainActor());
            clone.setPosition(initialPositionOnStage.x, initialPositionOnStage.y);
            stage.addActor(clone);
            fromLogic.getChessLogic().setChessModel(null);

            clone.addAction(sequence(moveTo(finalPositionOnStage.x + clone.getWidth() / 4, finalPositionOnStage.y + 5, 0.25f),
                    new Action() {
                            @Override
                            public boolean act(float delta) {
                                clone.remove();
                                afterMove.run();
                                return true;
                            }
                }));
        }
        else{
            afterMove.run();
            fromLogic.getChessLogic().setChessModel(null);
        }


    }

    public void showPercentTile(TerrainLogic fromLogic){
        int leftTopRightBottom = 0;
        if(fromLogic.getTerrainModel().getCol() > this.getTerrainModel().getCol()) leftTopRightBottom = 2;
        if(fromLogic.getTerrainModel().getRow() > this.getTerrainModel().getRow()) leftTopRightBottom = 3;
        if(fromLogic.getTerrainModel().getCol() < this.getTerrainModel().getCol()) leftTopRightBottom = 0;
        if(fromLogic.getTerrainModel().getRow() < this.getTerrainModel().getRow()) leftTopRightBottom = 1;

        int percent = 0;
        if(this.isEmpty()) percent = -1;
        else{
             percent = _battleRefs.getWinPercent(fromLogic.getChessLogic().getChessModel().getChessType(),
                                                    this.getChessLogic().getChessModel().getChessType());
        }

        terrainActor.showPercent(percent, leftTopRightBottom);

    }

    public void hidePercentTile(){
        terrainActor.hidePercent();
    }

    public void showCanMoveTo(){
        terrainActor.showCanMoveTo();
    }

    public void hideCanMoveTo(){
        terrainActor.hideCanMoveTo();
    }

    public void setSelected(boolean selected){
        if(chessLogic != null) chessLogic.setSelected(selected);
        terrainActor.setSelected(selected);
    }

    public void setDragAndDrop(ArrayList<TerrainLogic> possibleMoveTerrainLogics){
        chessLogic.clearDragDropTargets();
        for(final TerrainLogic terrainLogic : possibleMoveTerrainLogics){
            DragAndDrop.Target target = new DragAndDrop.Target(terrainLogic.getTerrainActor()) {
                public boolean drag (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    terrainLogic.showCanMoveTo();
                    return true;
                }

                public void reset (DragAndDrop.Source source, DragAndDrop.Payload payload) {
                    terrainLogic.hideCanMoveTo();
                }

                public void drop (DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    terrainLogic.hideCanMoveTo();
                    actionListener.onMoved(_me.getTerrainModel().getCol(), _me.getTerrainModel().getRow(),
                            terrainLogic.getTerrainModel().getCol(), terrainLogic.getTerrainModel().getRow(),
                            _battleRefs.getFromIsWinner(_me.getChessLogic().getChessModel().getChessType(),
                                    terrainLogic.getChessLogic().getChessModel().getChessType()));
                }
            };
            chessLogic.addDragDropTarget(target);
        }
    }


    public boolean isEmpty() {
        return chessLogic.getChessModel().chessType == ChessType.NONE;
    }

    public boolean isOpened() {
        return chessLogic.getChessModel().getOpened();
    }

    public TerrainActor getTerrainActor() {
        return terrainActor;
    }

    public boolean isSelected() {
        return this.getChessLogic().getChessModel().getSelected();
    }

    public void setListeners(){
        terrainActor.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if((!getChessLogic().getChessModel().getOpened() ||
                        _gameDataController.getMyChessColor() == getChessLogic().getChessModel().getChessColor()) &&
                        (!isEmpty())){
                    actionListener.onSelected();
                }

                return super.touchDown(event, x, y, pointer, button);
            }
        });

    }

    public TerrainModel getTerrainModel() {
        return _terrainModel;
    }

    public ChessLogic getChessLogic() {
        return chessLogic;
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
        this.actionListener.setTerrainLogic(this);
        chessLogic.setActionListener(this.actionListener);
    }

}
