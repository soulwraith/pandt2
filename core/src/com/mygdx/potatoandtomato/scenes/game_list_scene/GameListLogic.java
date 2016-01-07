package com.mygdx.potatoandtomato.scenes.game_list_scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.absintflis.scenes.LogicAbstract;
import com.mygdx.potatoandtomato.absintflis.scenes.SceneAbstract;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.models.UserPlayingState;
import com.mygdx.potatoandtomato.scenes.prerequisite_scene.PrerequisiteLogic;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class GameListLogic extends LogicAbstract {

    GameListScene _scene;
    ArrayList<Room> _rooms;
    Room _selectedRoom;
    String _continueRoomId;

    public GameListLogic(PTScreen screen, Services services, Object... objs) {
        super(screen, services, objs);

        _scene = new GameListScene(services, screen);
        _rooms = new ArrayList();

        _scene.getNewGameButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _screen.toScene(SceneEnum.CREATE_GAME);
            }
        });

        _scene.getContinueGameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_continueRoomId != null){
                    _screen.toScene(SceneEnum.PREREQUISITE, null, PrerequisiteLogic.JoinType.CONTINUING, _continueRoomId);
                }
            }
        });

        _scene.getJoinGameButton().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(_selectedRoom != null){
                    _screen.toScene(SceneEnum.PREREQUISITE, null, PrerequisiteLogic.JoinType.JOINING, _selectedRoom.getId());
                }
            }
        });


        _scene.getSettingsButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                _screen.toScene(SceneEnum.SETTINGS);
            }
        });


        _services.getDatabase().monitorAllRooms(_rooms, getClassTag(), new SpecialDatabaseListener<ArrayList<Room>, Room>(Room.class) {
            @Override
            public void onCallbackTypeOne(ArrayList<Room> obj, Status st) {
                if(st == Status.SUCCESS){
                    for(Room r : obj){
                        roomDataChanged(r);
                    }
                }
            }

            @Override
            public void onCallbackTypeTwo(Room obj, Status st) {
                if(st == Status.SUCCESS){
                    roomDataChanged(obj);
                }
            }
        });

    }

    @Override
    public void onShow() {
        checkCanContinue();
        super.onShow();
        _scene.setUsername(_services.getProfile().getDisplayName(15));
    }

    private void checkCanContinue(){
        _scene.getContinueGameButton().setEnabled(false);
        _continueRoomId = null;
        if(!_services.getProfile().getUserPlayingState().getRoomId().equals("0")){
            final UserPlayingState state = _services.getProfile().getUserPlayingState();
            _services.getDatabase().getRoomById(state.getRoomId(), new DatabaseListener<Room>(Room.class) {
                @Override
                public void onCallback(Room obj, Status st) {
                    if(st == Status.SUCCESS){
                        if(obj.canContinue(_services.getProfile().getUserId(), state.getRoundCounter(), state.getRoomId())){
                            _continueRoomId = obj.getId();
                            _scene.getContinueGameButton().setEnabled(true);
                        }
                        else{
                            _services.getProfile().setUserPlayingState(null);
                            _services.getDatabase().updateProfile(_services.getProfile(), null);
                        }
                    }
                }
            });


        }
    }

    public void roomDataChanged(final Room room){
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                if(room.isOpen()){
                    final Actor clicked = _scene.updatedRoom(room);
                    if(clicked != null){
                        clicked.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                _selectedRoom = room;
                                _scene.gameRowHighlight(clicked.getName());
                            }
                        });
                    }
                }
                else{
                    if(_selectedRoom != null && _selectedRoom.getId().equals(room.getId())) {
                        _selectedRoom = null;
                        _scene.gameRowHighlight("-1");
                    }
                    _scene.removeRoom(room);
                }
            }
        });


    }

    @Override
    public SceneAbstract getScene() {
        return _scene;
    }
}
