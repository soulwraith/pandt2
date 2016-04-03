import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.games.models.BoardModel;
import com.potatoandtomato.games.models.GraveModel;
import com.potatoandtomato.games.models.MatchHistory;
import com.potatoandtomato.games.screens.BoardLogic;
import com.potatoandtomato.games.services.Database;
import com.potatoandtomato.games.services.GameDataController;
import com.potatoandtomato.games.services.ScoresHandler;
import com.potatoandtomato.games.services.Texts;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.Mockito.mock;

/**
 * Created by SiongLeng on 31/3/2016.
 */
public class TestScoresHandler {

    @Test
    public void testWinSituation(){

        ScoresHandler scoresHandler = new ScoresHandler(mock(GameCoordinator.class),
                                    mock(Database.class), mock(Texts.class), mock(GameDataController.class)){
            @Override
            public void populateData() {
                dataReady = true;
            }

        };

        BoardModel boardModel = new BoardModel();
        boardModel.setAccTurnCount(100);
        GraveModel graveModel = new GraveModel();

        boolean canAddStreak;
        ArrayList<ScoreDetails> scoreDetails = new ArrayList<>();
        canAddStreak = scoresHandler.checkWinSituation(scoreDetails, boardModel, graveModel);

        Assert.assertEquals(true, canAddStreak);
        Assert.assertEquals(ScoresHandler.HARD_WIN, scoreDetails.get(0).getValue(), 0);
        Assert.assertEquals(true, scoreDetails.get(0).isAddOrMultiply());
        Assert.assertEquals(false, scoreDetails.get(0).canAddStreak());
        Assert.assertEquals(1, scoreDetails.size());


        boardModel.setAccTurnCount(50);
        graveModel.setRedLeftTime(500);
        graveModel.setYellowLeftTime(500);

        scoreDetails.clear();
        canAddStreak = scoresHandler.checkWinSituation(scoreDetails, boardModel, graveModel);

        Assert.assertEquals(true, canAddStreak);
        Assert.assertEquals(ScoresHandler.NORMAL_WIN, scoreDetails.get(0).getValue(), 0);
        Assert.assertEquals(true, scoreDetails.get(0).isAddOrMultiply());
        Assert.assertEquals(false, scoreDetails.get(0).canAddStreak());
        Assert.assertEquals(1, scoreDetails.size());

        boardModel.setAccTurnCount(50);
        graveModel.setRedLeftTime(800);
        graveModel.setYellowLeftTime(800);

        scoreDetails.clear();
        canAddStreak = scoresHandler.checkWinSituation(scoreDetails, boardModel, graveModel);

        Assert.assertEquals(false, canAddStreak);
        Assert.assertEquals(ScoresHandler.EASY_WIN, scoreDetails.get(0).getValue(), 0);
        Assert.assertEquals(true, scoreDetails.get(0).isAddOrMultiply());
        Assert.assertEquals(false, scoreDetails.get(0).canAddStreak());
        Assert.assertEquals(1, scoreDetails.size());
    }


    @Test
    public void testGetMultiply(){
        ScoresHandler scoresHandler = new ScoresHandler(mock(GameCoordinator.class),
                mock(Database.class), mock(Texts.class), mock(GameDataController.class)){
            @Override
            public void populateData() {
                dataReady = true;
            }

            @Override
            public String getWinnerUserId() {
                return "0";
            }
        };

        ArrayList<MatchHistory> matchHistories = new ArrayList<>();
        matchHistories.add(new MatchHistory("9", false));
        matchHistories.add(new MatchHistory("9", false));
        matchHistories.add(new MatchHistory("9", false));
        matchHistories.add(new MatchHistory("9", false));
        matchHistories.add(new MatchHistory("9", false));

        HashMap<String, ArrayList<MatchHistory>> histories = new HashMap<>();
        histories.put("0", matchHistories);
        scoresHandler.setLastMatchHistories(histories);

        double result;
        ArrayList<ScoreDetails> scoreDetails = new ArrayList<>();
        result = scoresHandler.getMultiply(scoreDetails, true);

        Assert.assertEquals(ScoresHandler.CATCH_UP_MULTIPLIER, result, 0);
        Assert.assertEquals(ScoresHandler.CATCH_UP_MULTIPLIER, scoreDetails.get(0).getValue(), 0);
        Assert.assertEquals(false, scoreDetails.get(0).isAddOrMultiply());
        Assert.assertEquals(true, scoreDetails.get(0).canAddStreak());
        Assert.assertEquals(1, scoreDetails.size());

    }

}

