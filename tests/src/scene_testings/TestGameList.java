package scene_testings;

import abstracts.TestAbstract;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.potatoandtomato.PTScreen;
import com.mygdx.potatoandtomato.enums.MascotEnum;
import com.mygdx.potatoandtomato.scenes.game_list_scene.GameListLogic;
import com.mygdx.potatoandtomato.scenes.game_list_scene.GameListScene;
import com.mygdx.potatoandtomato.scenes.mascot_pick_scene.MascotPickLogic;
import com.mygdx.potatoandtomato.scenes.mascot_pick_scene.MascotPickScene;
import helpers.T_Assets;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 11/12/2015.
 */
public class TestGameList extends TestAbstract {

    @Test
    public void testGameListLogicScene(){
        GameListLogic logic = new GameListLogic(mock(PTScreen.class), T_Assets.mockAssets());
        GameListScene scene = (GameListScene) logic.getScene();
        Assert.assertEquals(true, ((Table) scene.getRoot()).hasChildren());
    }

    @Test
    public void testGameListLogicSceneAddRecord(){
        GameListLogic logic = new GameListLogic(mock(PTScreen.class), T_Assets.mockAssets());
        GameListScene scene = (GameListScene) logic.getScene();
        for(int i = 0; i<20; i++){
            logic.onGameCreated();
        }
        scene.gameRowHighlight("0");
    }


}
