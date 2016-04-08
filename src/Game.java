/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ASHISH
 */
public class Game {
    
    static Board board;
    private static boolean draw;
    private int blackVict = 0;
    private int whiteVict = 0;

    private static boolean blackVitory = false;
    private static boolean whiteVictory = false;

    Game()
    {
        Initialize();
    }

    public static boolean isDraw() {
        return draw;
    }

    public void PlayGame()
    {

        UserInteractions.isDisplayOn = false;

        System.out.println("#################################");

        System.out.println("Apprentissage: ");
        System.out.println("\nContre random: ");
        RobotSasha.epsilon = 0.3;
        RobotSasha.alpha_facteur = 0.99;
        RobotSasha.gamma = 0.99;
        System.out.println("(Random blanc, AI noir)");

        playMatches(500);

        White.owner = Owner.ROBOTSASHA;
        Black.owner = Owner.RANDOM;
        System.out.println("(Random noir, AI blanc)");

        playMatches(500);


        RobotSasha.epsilon = 0.7;
        RobotSasha.alpha_facteur = 1.0;
        White.owner = Owner.ROBOTSASHA;
        Black.owner = Owner.ROBOTSASHA;
        System.out.println("\nContre Soi-meme:");
        for(int i = 0; i< 20 ; i++)
            playMatches(250);


        System.out.println("\n#################################");

        System.out.println("Resultats:  ");
        System.out.println("\nContre random:");
        System.out.println("(Random blanc, AI noir)");
        White.owner = Owner.RANDOM;
        RobotSasha.epsilon = 0;
        RobotSasha.alpha_facteur = 0;
        for(int i = 0; i< 20 ; i++)
            playMatches(250);

        UserInteractions.isDisplayOn = true;
        White.owner = Owner.HUMAN;
        System.out.println("\nContre Humain:");
        playMatches(1);
    }

    private void playMatches(int number) {
        blackVict = 0;
        whiteVict = 0;
        for(int i = 0; i< number; i++)
        {
            board  = new Board();
            draw = false;
            whiteVictory = false;
            blackVitory = false;
            int nbTurnsDraw = 0;
            if(White.owner == Owner.ROBOTSASHA ||Black.owner == Owner.ROBOTSASHA)
                RobotSasha.debutGame();

            while(!Game.board.CheckGameComplete()) {
                if(board.blackPieces == 1 && board.whitePieces == 1)
                {
                    nbTurnsDraw++;
                    if (nbTurnsDraw > 20) {
                        draw = true;
                        if(Black.owner == Owner.ROBOTSASHA)
                            Black.Move();
                        if(White.owner == Owner.ROBOTSASHA)
                            White.Move();
                        break;
                    }
                }

                if (Game.board.CheckGameDraw(Player.white)) {
                    Victory(Player.black);
                    break;

                }

                White.Move();
                if (Game.board.CheckGameComplete()) {
                    Victory(Player.white);
                    break;

                }

                if (Game.board.CheckGameDraw(Player.black)) {
                    Victory(Player.white);
                    break;

                }

                Black.Move();
                if (Game.board.CheckGameComplete()) {
                    Victory(Player.black);
                    break;

                }
            }
        }
        System.out.println("Victoires des noirs :" +blackVict +" Victoires des blancs  :" + whiteVict);
    }


    private void Victory(Player p) {
        UserInteractions.DisplayGreetings(p);
        Game.board.Display();
        if (p == Player.white)
        {
            whiteVictory = true;
            whiteVict++;
        }
        else
        {
            blackVitory = true;
            blackVict++;
        }
        if(Black.owner == Owner.ROBOTSASHA)
            Black.Move();
        if(White.owner == Owner.ROBOTSASHA)
            White.Move();
    }

    private void Initialize()
    {
        White.owner = Owner.RANDOM;
        Black.owner = Owner.ROBOTSASHA;
        RobotSasha.Initialiser();
    }

    public static boolean WhiteVictory() {
        return whiteVictory;
    }

    public static boolean BlackVictory() {
        return blackVitory;
    }
}