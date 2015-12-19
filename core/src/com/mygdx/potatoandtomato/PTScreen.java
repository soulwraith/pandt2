package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.mygdx.potatoandtomato.absintflis.OnQuitListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.services.Fonts;
import com.mygdx.potatoandtomato.helpers.services.Texts;
import com.mygdx.potatoandtomato.helpers.services.Textures;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.helpers.utils.Positions;
import com.mygdx.potatoandtomato.scenes.boot_scene.BootLogic;
import com.mygdx.potatoandtomato.scenes.create_game_scene.CreateGameLogic;
import com.mygdx.potatoandtomato.scenes.game_list_scene.GameListLogic;
import com.mygdx.potatoandtomato.scenes.mascot_pick_scene.MascotPickLogic;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteLogic;
import com.mygdx.potatoandtomato.scenes.room_scene.RoomLogic;

import java.util.Stack;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by SiongLeng on 5/12/2015.
 */
public class PTScreen implements Screen {

    Image _bgBlueImg, _bgAutumnImg, _sunriseImg, _sunrayImg, _greenGroundImg, _autumnGroundImg;
    Services _services;
    Textures _textures;
    Fonts _fonts;
    Texts _texts;
    Stage _stage;
    Stack<LogicEnumPair> _logicStacks;

    public PTScreen(Services services) {
        this._services = services;
        this._textures = _services.getTextures();
        this._fonts = _services.getFonts();
        this._texts = _services.getTexts();
        this._logicStacks = new Stack<>();
    }

    //call this function to change scene
    public void toScene(final SceneEnum sceneEnum, final Object... objs){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                final LogicAbstract logic = newSceneLogic(sceneEnum, objs);
                logic.onCreate();
                if(_logicStacks.size() == 0){
                    _stage.addActor(logic.getScene().getRoot());
                }
                else{
                    final LogicEnumPair logicOut = _logicStacks.peek();
                    sceneTransition(logic.getScene().getRoot(), logicOut.getLogic().getScene().getRoot(), true, new Runnable() {
                        @Override
                        public void run() {
                            logicOut.getLogic().onHide();
                            if (!logicOut.getLogic().isSaveToStack()) {
                                _logicStacks.remove(logicOut);
                                logicOut.getLogic().dispose();
                            }
                        }
                    });
                }
                _logicStacks.push(new LogicEnumPair(logic, sceneEnum, objs));

            }
        });
    }

    public void back(){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {

                if(_logicStacks.size() == 1){
                    confirmQuitGame();
                    return;
                }

                _logicStacks.peek().getLogic().onQuit(new OnQuitListener() {
                    @Override
                    public void onResult(Result result) {
                        if(result == Result.YES){
                            final LogicEnumPair current = _logicStacks.pop();
                            final LogicEnumPair previous = _logicStacks.peek();
                            previous.getLogic().onCreate();
                            sceneTransition(previous.getLogic().getScene().getRoot(), current.getLogic().getScene().getRoot(), false, new Runnable() {
                                @Override
                                public void run() {
                                    current.getLogic().onHide();
                                    current.getLogic().dispose();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    public void confirmQuitGame(){

    }

    private LogicAbstract newSceneLogic(SceneEnum sceneEnum, Object... objs){
        LogicAbstract logic = null;
        switch (sceneEnum){
            case BOOT:
                logic = new BootLogic(this, _services, objs);
                break;
            case MASCOT_PICK:
                logic = new MascotPickLogic(this, _services, objs);
                break;
            case GAME_LIST:
                logic = new GameListLogic(this, _services, objs);
                break;
            case CREATE_GAME:
                logic = new CreateGameLogic(this, _services, objs);
                break;
            case PREREQUISITE:
                logic = new PrerequisiteLogic(this, _services, objs);
                break;
            case ROOM:
                logic = new RoomLogic(this, _services, objs);
                break;
        }
        return logic;
    }

    private void sceneTransition(Actor _rootIn, final Actor _rootOut, boolean toRight, final Runnable onFinish){

        float duration = 0.5f;
        _rootIn.remove();
        _rootOut.remove();
        _rootIn.clearActions();
        _rootOut.clearActions();
        _stage.addActor(_rootIn);
        _stage.addActor(_rootOut);

        _rootIn.setPosition(toRight ? Positions.getWidth() : -Positions.getWidth(), 0);
        _rootOut.setPosition(0, 0);

        _rootIn.addAction(moveTo(0, 0, duration));
        _rootOut.addAction(sequence(moveBy(toRight ? -Positions.getWidth() : Positions.getWidth(), 0, duration), new Action() {
            @Override
            public boolean act(float delta) {
                _rootOut.remove();
                onFinish.run();
                return false;
            }
        }));

    }

    @Override
    public void show() {
        _stage = new Stage();

        //Ground Texture START////////////////////////////////////////////
        _greenGroundImg = new Image(_textures.getGreenGround());
        _autumnGroundImg = new Image(_textures.getAutumnGround());
        _autumnGroundImg.getColor().a = 0;
        _autumnGroundImg.addAction(sequence(delay(0.4f), fadeIn(0.5f)));
        //Ground Texture END//////////////////////////////////////////////

        //Background Texture START
        _bgBlueImg = new Image(_textures.getBlueBg());
        _bgBlueImg.setSize(Positions.getWidth(), Positions.getHeight());

        _bgAutumnImg = new Image(_textures.getAutumnBg());
        _bgAutumnImg.setSize(Positions.getWidth(), Positions.getHeight());
        _bgAutumnImg.getColor().a = 0;

        _sunriseImg = new Image(_textures.getSunrise());
        _sunriseImg.getColor().a = 0;

        _sunrayImg = new Image(_textures.getSunray());
        _sunrayImg.setPosition(Positions.centerX(1200), -470);
        _sunrayImg.setOrigin(599f, 601f);
        _sunrayImg.setSize(1200, 1200);
        _sunrayImg.getColor().a = 0;

        _sunriseImg.addAction(fadeIn(0.5f));
        _bgAutumnImg.addAction(sequence(fadeIn(0.5f), new Action() {
            @Override
            public boolean act(float delta) {
                _sunrayImg.addAction(parallel(
                        fadeIn(1f),
                        forever(rotateBy(3, 0.15f))
                ));
                return true;
            }
        }));
        //Background Texture END

        _stage.addActor(_bgBlueImg);
        _stage.addActor(_bgAutumnImg);
        _stage.addActor(_sunrayImg);
        _stage.addActor(_sunriseImg);
        _stage.addActor(_greenGroundImg);
        _stage.addActor(_autumnGroundImg);
        Gdx.input.setInputProcessor(_stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
        _stage.act(delta);
        _stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private class LogicEnumPair{
        LogicAbstract logicAbstract;
        SceneEnum sceneEnum;
        Object[] objs;

        public LogicEnumPair(LogicAbstract logicAbstract, SceneEnum sceneEnum, Object... objs) {
            this.logicAbstract = logicAbstract;
            this.sceneEnum = sceneEnum;
            this.objs = objs;
        }

        public LogicAbstract getLogic() {
            return logicAbstract;
        }

        public SceneEnum getSceneEnum() {
            return sceneEnum;
        }

        public void setLogic(LogicAbstract logicAbstract) {
            this.logicAbstract = logicAbstract;
        }

        public Object[] getObjs() {
            return objs;
        }
    }

}