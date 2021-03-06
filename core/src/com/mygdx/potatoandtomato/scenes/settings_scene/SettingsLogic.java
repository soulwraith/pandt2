package com.mygdx.potatoandtomato.scenes.settings_scene;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.ConfirmResultListener;
import com.mygdx.potatoandtomato.absintflis.controls.ToggleStateListener;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.absintflis.socials.FacebookListener;
import com.mygdx.potatoandtomato.enums.ConfirmIdentifier;
import com.mygdx.potatoandtomato.services.Confirm;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.RunnableArgs;

/**
 * Created by SiongLeng on 19/12/2015.
 */
public class SettingsLogic extends LogicAbstract {

    SettingsScene _scene;

    public SettingsLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);
        _scene = new SettingsScene(services, screen);
    }

    public void facebookRequest(){
        if(!_services.getSocials().isFacebookLogon()){
            _services.getSocials().loginFacebook(new FacebookListener() {
                @Override
                public void onLoginComplete(Result result) {
                    if(result == Result.SUCCESS){
                        _screen.backToBoot();
                    }
                    else{
                        _confirm.show(ConfirmIdentifier.SettingsScene, _texts.confirmFacebookRequestFailed(), Confirm.Type.YES, null);
                    }
                }
            });
        }
        else{
            _services.getSocials().logoutFacebook(new FacebookListener() {
                @Override
                public void onLogoutComplete(Result result) {
                    _services.getPreferences().delete(Terms.USERID);
                    _services.getPreferences().delete(Terms.USER_SECRET);
                    _services.getProfile().setFacebookName(null);
                    _services.getProfile().setFacebookUserId(null);     //update profile on hide
                    _screen.backToBoot();   //always success
                }
            });
        }
    }

    public void toggleSounds(boolean isOn){
        Global.ENABLE_SOUND = isOn;
        _services.getPreferences().put(Terms.SOUNDS_DISABLED, Global.ENABLE_SOUND ? "false" : "true");
        _services.getBroadcaster().broadcast(BroadcastEvent.SOUNDS_CHANGED, Global.ENABLE_SOUND);
    }

    public void toggleAutoPlayAudioMsg(boolean isOn){
        Global.AUTO_PLAY_AUDIO_MSG = isOn;
        _services.getPreferences().put(Terms.AUTO_PLAY_AUDIO_DISABLED, Global.AUTO_PLAY_AUDIO_MSG ? "false" : "true");
    }

    public void updateProfile(){
        final String newName = _scene.getDisplayNameTextField().getText().trim();
        if(newName.equals("")) return;

        loadingSave();
        _services.getDatabase().getProfileByGameNameLower(newName, new DatabaseListener<Profile>() {
            @Override
            public void onCallback(Profile obj, Status st) {
                if (st == Status.SUCCESS) {
                    if (obj == null || obj.equals(_services.getProfile())) {
                        if (_services.getProfile().getFacebookName() == null || !_services.getProfile().getFacebookName().equals(newName)) {
                            _services.getProfile().setGameName(newName);
                        }
                        _services.getDatabase().updateProfile(_services.getProfile(), null);
                        _screen.back();
                    } else {
                        _services.getConfirm().show(ConfirmIdentifier.SettingsScene, _texts.confirmDuplicateNameError(), Confirm.Type.YES, null);
                        clearLoadingSave();
                    }
                } else {
                    _services.getConfirm().show(ConfirmIdentifier.SettingsScene, _texts.generalError(), Confirm.Type.YES, null);
                    clearLoadingSave();
                }
            }
        });
    }

    public void loadingSave(){
        _scene.getSaveBtn().loading();
        _scene.getRoot().setTouchable(Touchable.disabled);
    }

    public void clearLoadingSave(){
        _scene.getSaveBtn().clearLoading();
        _scene.getRoot().setTouchable(Touchable.enabled);
    }

    @Override
    public void onHide() {
        super.onHide();
    }

    @Override
    public void setListeners() {
        super.setListeners();
        _scene.getFacebookBtn().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                _confirm.show(ConfirmIdentifier.SettingsScene, _services.getSocials().isFacebookLogon() ? _texts.confirmLogoutFacebook() : _texts.confirmLoginFacebook(),
                        Confirm.Type.YESNO, new ConfirmResultListener() {
                            @Override
                            public void onResult(Result result) {
                                if(result == Result.YES){
                                    facebookRequest();
                                }
                            }
                        });
            }
        });

        _scene.getSaveBtn().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                updateProfile();
            }
        });

        _scene.getSoundToggleButton().addToggleStateListener(new ToggleStateListener() {
            @Override
            public void onToggle(boolean isOn) {
                toggleSounds(isOn);
            }
        });

        _scene.getAutoPlayAudioToggleButton().addToggleStateListener(new ToggleStateListener() {
            @Override
            public void onToggle(boolean isOn) {
                toggleAutoPlayAudioMsg(isOn);
            }
        });

        _scene.getCreditBtn().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _scene.getCreditsDesign(new RunnableArgs<Actor>() {
                    @Override
                    public void run() {
                        _confirm.show(ConfirmIdentifier.Credits, this.getFirstArg(), Confirm.Type.YES, null, "");
                    }
                });


            }
        });

    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
