import java.util.Random;
import java.util.Vector;

/**
 * Created by Felix on 3/29/2016.
 * TP3 - AI
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
        Vector< Vector<Move>> resultantMoveSeq = expandMoves(Game.board, type);

        int rnd = new Random().nextInt(resultantMoveSeq.size());
        Vector<Move> moves = resultantMoveSeq.elementAt(rnd);
        for(Move m:moves){
            if(type ==   Player.white)
                Game.board.genericMakeWhiteMove(m);
            else
                Game.board.genericMakeBlackMove(m);

        }

        //System.out.print("Random Robot's Move was ");
       // UserInteractions.DisplayMoveSeq(moves);
        //System.out.println();
    }

}

