import java.util.Random;
import java.util.Vector;

/**
 * Created by Felix on 3/29/2016.
 */
public class RandomRobot extends Robot{

    public static void makeNextWhiteMoves() {
        makeNextMoveRandom(Player.white);
    }

    public static void makeNextBlackMoves(){
        makeNextMoveRandom(Player.black);
    }

    private static void makeNextMoveRandom(Player type)
    {
        Vector<Move> resultantMoveSeq = new Vector<Move>();

        if(type == Player.white) {
            resultantMoveSeq = White.CalculateAllForcedMovesForWhite(Game.board);
            if (resultantMoveSeq.isEmpty()) {
                resultantMoveSeq = White.CalculateAllNonForcedMovesForWhite(Game.board);
            }
        }
        else{
            resultantMoveSeq = Black.CalculateAllForcedMovesForBlack(Game.board);
            if (resultantMoveSeq.isEmpty()) {
                resultantMoveSeq = Black.CalculateAllNonForcedMovesForBlack(Game.board);
            }
        }


        int rnd = new Random().nextInt(resultantMoveSeq.size());
        Move move = resultantMoveSeq.elementAt(rnd);
        Game.board.genericMakeWhiteMove(move);


        System.out.print("Robot's Move was ");
        Vector<Move> currentMove = new Vector<Move>();
        currentMove.add(move);
        UserInteractions.DisplayMoveSeq(currentMove);
        System.out.println();
    }

}

