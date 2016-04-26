import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.models.LeaderboardRecord;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.enums.GameState;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.screens.hints.HintsLogic;
import com.potatoandtomato.games.screens.scores.ScoresActor;
import com.potatoandtomato.games.screens.scores.ScoresLogic;
import com.potatoandtomato.games.screens.time_bar.CastleLogic;
import com.potatoandtomato.games.screens.time_bar.KnightActor;
import com.potatoandtomato.games.screens.time_bar.KnightLogic;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by SiongLeng on 14/4/2016.
 */
public class TestScoresLogic extends TestAbstract {

    @Test
    public void testGetNextLeaderboardRecordsNoAnimate(){
        GameModel gameModel = new GameModel();
        gameModel.setScore(0);
        GameCoordinator gameCoordinator = _game.getCoordinator();

        ArrayList<LeaderboardRecord> leaderboardRecords = new ArrayList();
        LeaderboardRecord record1 = new LeaderboardRecord();
        record1.setScore(5000);
        leaderboardRecords.add(record1);
        LeaderboardRecord record2 = new LeaderboardRecord();
        record2.setScore(1000);
        leaderboardRecords.add(record2);
        gameCoordinator.setGameLeaderboardRecords(leaderboardRecords);

        ScoresLogic scoresLogic = new ScoresLogic(gameCoordinator, Mockings.mockServices(gameCoordinator),
                                        gameModel, mock(KnightLogic.class), mock(CastleLogic.class), mock(HintsLogic.class));

        Assert.assertEquals(record2.getScore(), scoresLogic.getNextLeaderboardScore(), 0);

        scoresLogic.addScoreWithoutAnimation(3000);

        Assert.assertEquals(record1.getScore(), scoresLogic.getNextLeaderboardScore(), 0);

        scoresLogic.addScoreWithoutAnimation(5000);

        Assert.assertEquals(-1, scoresLogic.getNextLeaderboardScore(), 0);

    }


    @Test
    public void testCalculate(){
        GameModel gameModel = new GameModel();
        gameModel.setScore(0);
        gameModel.setRemainingMiliSecs(1, false);
        gameModel.setHintsLeft(2);
        GameCoordinator gameCoordinator = _game.getCoordinator();

        ArrayList<LeaderboardRecord> leaderboardRecords = new ArrayList();
        LeaderboardRecord record1 = new LeaderboardRecord();
        record1.setScore(5000);
        leaderboardRecords.add(record1);
        LeaderboardRecord record2 = new LeaderboardRecord();
        record2.setScore(1000);
        leaderboardRecords.add(record2);
        gameCoordinator.setGameLeaderboardRecords(leaderboardRecords);

        KnightActor knightActor = new KnightActor(Mockings.mockServices(gameCoordinator), 1000){
            @Override
            public Vector2 getPositionOnStage() {
                return new Vector2(10, 10);
            }
        };

        KnightLogic knightLogic = new KnightLogic(gameModel,  Mockings.mockServices(gameCoordinator), gameCoordinator);
        knightLogic.setKnightActor(knightActor);

        ScoresLogic scoresLogic = new ScoresLogic(gameCoordinator, Mockings.mockServices(gameCoordinator),
                gameModel, knightLogic, mock(CastleLogic.class), mock(HintsLogic.class)){
            @Override
            public void addScoreAndPopScoreOnActor(Actor actor, int score, Runnable onFinish) {
                addScoreWithoutAnimation(score);
                onFinish.run();
            }
        };

        ScoresActor scoresActor = Mockito.mock(ScoresActor.class);
        scoresLogic.setScoresActor(scoresActor);

        scoresLogic.calculate();

        gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onGameStateChanged(GameState newState) {
                if(newState == GameState.Ended){
                    Threadings.oneTaskFinish();
                }
            }
        });

        Threadings.waitTasks(1);

        verify(scoresActor, atLeastOnce()).setNextHighScore(eq((int) record1.getScore()));

    }














}