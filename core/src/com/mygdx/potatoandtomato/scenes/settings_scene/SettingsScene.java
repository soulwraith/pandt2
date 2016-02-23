package com.mygdx.potatoandtomato.scenes.settings_scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.assets.Fonts;
import com.mygdx.potatoandtomato.assets.Patches;
import com.mygdx.potatoandtomato.assets.Textures;
import com.mygdx.potatoandtomato.helpers.controls.BtnColor;
import com.mygdx.potatoandtomato.helpers.controls.TopBar;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.statics.Global;

/**
 * Created by SiongLeng on 19/12/2015.
 */
public class SettingsScene extends SceneAbstract {

    TextField _displayNameTextField;
    BtnColor _facebookBtn, _saveBtn, _reportBtn;
    Image _soundsEnabledImage;

    public SettingsScene(Services services, PTScreen screen) {
        super(services, screen);
    }

    @Override
    public void populateRoot() {
        new TopBar(_root, _texts.settingsTitle(), false, _assets, _screen);

        Label.LabelStyle labelTitleStyle = new Label.LabelStyle(_assets.getFonts().get(Fonts.FontName.PIZZA,
                            Fonts.FontSize.XXL, Fonts.FontColor.TEAL, Fonts.FontShadowColor.DARK_ORANGE), null);

        Table settingsTable = new Table();
        settingsTable.setBackground(new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.WOOD_BG_NORMAL)));

        /////////////////////////
        //Sounds
        /////////////////////////
        Label soundsLabel = new Label(_texts.sounds(), labelTitleStyle);
        _soundsEnabledImage = new Image();
        changeSoundEnabledImage();

        //////////////////////////
        //Separator
        //////////////////////////
        Image separatorImage = new Image(_assets.getTextures().get(Textures.Name.WOOD_SEPARATOR_HORIZONTAL));

        ///////////////////
        //Display name
        ////////////////////
        Label displayNameLabel = new Label(_texts.displayName(), labelTitleStyle);

        Table displayNameFieldTable = new Table();
        displayNameFieldTable.setBackground(new NinePatchDrawable(_assets.getPatches().get(Patches.Name.TEXT_FIELD_BG)));
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = _assets.getFonts().get(Fonts.FontName.MYRIAD);
        textFieldStyle.fontColor = Color.BLACK;
        textFieldStyle.cursor = new TextureRegionDrawable(_assets.getTextures().get(Textures.Name.CURSOR_BLACK));
        _displayNameTextField = new TextField(_services.getProfile().getDisplayName(15), textFieldStyle);
        displayNameFieldTable.add(_displayNameTextField).width(110).padTop(10).padBottom(10);

        //////////////////////////
        //Save Button
        //////////////////////////
        _saveBtn = new BtnColor(BtnColor.ColorChoice.GREEN, _assets);
        _saveBtn.setText(_texts.save());


        //////////////////////////
        //Separator
        //////////////////////////
        Image separatorImage2 = new Image(_assets.getTextures().get(Textures.Name.WOOD_SEPARATOR_HORIZONTAL));

        ///////////////////////////
        //Facebook status
        //////////////////////////
        Label socialLabel = new Label(_texts.facebook(), labelTitleStyle);

        _facebookBtn = new BtnColor(BtnColor.ColorChoice.BLUE, _assets);
        _facebookBtn.setText(_services.getSocials().isFacebookLogon() ? _texts.logout() : _texts.login());


        ////////////////////////
        //populations
        /////////////////////////
        settingsTable.align(Align.top);
        settingsTable.padLeft(25).padRight(25).padTop(30).padBottom(30);
        settingsTable.add(soundsLabel).left();
        settingsTable.add(_soundsEnabledImage).right();
        settingsTable.row();
        settingsTable.add(separatorImage).expandX().fillX().colspan(2).padTop(10).padBottom(10);
        settingsTable.row();
        settingsTable.add(displayNameLabel).left();
        settingsTable.add(displayNameFieldTable).right();
        settingsTable.row();
        settingsTable.add(_saveBtn).colspan(2).right().padTop(15);
        settingsTable.row();
        settingsTable.add(separatorImage2).expandX().fillX().colspan(2).padTop(10).padBottom(10);
        settingsTable.row();
        settingsTable.add(socialLabel).left();
        settingsTable.add(_facebookBtn).right();


        _root.add(settingsTable).width(350);
    }

    public Image getSoundsEnabledImage() {
        return _soundsEnabledImage;
    }

    public TextField getDisplayNameTextField() {
        return _displayNameTextField;
    }

    public BtnColor getFacebookBtn() {
        return _facebookBtn;
    }

    public BtnColor getSaveBtn() {
        return _saveBtn;
    }

    public BtnColor getReportBtn() {
        return _reportBtn;
    }

    public void changeSoundEnabledImage(){
        _soundsEnabledImage.setDrawable(new TextureRegionDrawable(Global.ENABLE_SOUND ?
                        _assets.getTextures().get(Textures.Name.SELECT_BOX) : _assets.getTextures().get(Textures.Name.UNSELECT_BOX)));
    }
}
