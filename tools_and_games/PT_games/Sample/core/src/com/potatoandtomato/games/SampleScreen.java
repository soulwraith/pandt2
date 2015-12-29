package com.potatoandtomato.games;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.potatoandtomato.common.*;

/**
 * Created by SiongLeng on 25/12/2015.
 */
public class SampleScreen extends GameScreen {

    private Image _image;
    private Image _image2;
    private Stage _stage;

    public SampleScreen(GameCoordinator gameCoordinator) {
        super(gameCoordinator);

        _stage = new Stage(new StretchViewport(getCoordinator().getGameWidth(), getCoordinator().getGameHeight()));
        _image = new Image(new Texture(getCoordinator().getFileH("test.png")));
        _image2 = new Image(new Texture(getCoordinator().getFileH("test2.png")));




        _image.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getCoordinator().sendRoomUpdate("1");
            }
        });

        _image2.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getCoordinator().sendRoomUpdate("2");
            }
        });

        gameCoordinator.addInGameUpdateListener(new InGameUpdateListener() {
            @Override
            public void onUpdateReceived(String msg, String userId) {
                switchImage(msg);
            }
        });

    }

    private void switchImage(String shownImage){
        if(shownImage.equals("1")){
            _image.getColor().a = 1;
            _image2.getColor().a = 0;
        }
        else if(shownImage.equals("2")){
            _image.getColor().a = 0;
            _image2.getColor().a = 1;
        }
    }

    @Override
    public void show() {
        Table table = new Table();
        table.setFillParent(true);
        table.add(_image).expandX().fillX().height(300);
        table.add(_image2).expandX().fillX().height(300);
        switchImage("1");
        _stage.addActor(table);
        getCoordinator().addInputProcessor(_stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(253/255, 221/255, 221/255, 1f);
        _stage.act();
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
}
