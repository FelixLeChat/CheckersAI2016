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
    
    Game()
    {
        this.Initialize(UserInteractions.GameChoice().charAt(0));
    }
    
    public void PlayGame()
    {
        UserInteractions.isDisplayOn = false;
        RobotSasha.epsilon = 0.3;
        playMatches(2000);

        RobotSasha.epsilon = 0.7;
        RobotSasha.alpha_facteur = 1.0;
        White.owner = Owner.ROBOTSASHA;
        playMatches(2000);

        White.owner = Owner.RANDOM;
        RobotSasha.epsilon = 0;
        RobotSasha.alpha_facteur = 0;
        playMatches(1000);
        UserInteractions.isDisplayOn = true;
        White.owner = Owner.HUMAN;
        playMatches(1);



    }

    private void playMatches(int number) {
        int blackVict = 0;
        int whiteVict = 0;

        for(int i = 0; i< number; i++)
        {
            board  = new Board();
            int nbTurnsDraw = 0;
            if(White.owner == Owner.ROBOTSASHA ||Black.owner == Owner.ROBOTSASHA)
                RobotSasha.debutGame();

            while(!Game.board.CheckGameComplete()) {
                if(board.blackPieces == 1 && board.whitePieces == 1)
                {
                    nbTurnsDraw++;
                    if (nbTurnsDraw > 3) {
                        break;
                    }
                }

                if (Game.board.CheckGameDraw(Player.white)) {
                    break;
                }

                White.Move();
                if (Game.board.CheckGameComplete()) {
                    UserInteractions.DisplayGreetings(Player.white);
                    Game.board.Display();
                    whiteVict++;
                    if(Black.owner == Owner.ROBOTSASHA)
                        Black.Move();
                    else if(White.owner == Owner.ROBOTSASHA)
                        White.Move();
                    break;
                }

                if (Game.board.CheckGameDraw(Player.black)) {
                    break;
                }

                /////////////////////////////////////////
                //System.out.println("Black ="+Game.board.blackPieces+", White="+Game.board.whitePieces);
                /////////////////////////////////////////
               // Game.board.Display();

                Black.Move();
                if (Game.board.CheckGameComplete()) {
                    UserInteractions.DisplayGreetings(Player.black);
                    Game.board.Display();
                    blackVict++;
                    if(White.owner == Owner.ROBOTSASHA)
                        White.Move();
                    else if (Black.owner == Owner.ROBOTSASHA)
                        Black.Move();
                    break;
                }

    //            Game.board.Display();
                /////////////////////////////////////////
    //            System.out.println("Black ="+Game.board.blackPieces+", White="+Game.board.whitePieces);
                /////////////////////////////////////////
            }
        }
        System.out.println("Blacks :" +blackVict +" whites :" + whiteVict);
    }


    private void Initialize(char human)
    {        
        assert(human=='w' || human=='b' || human == 'a' || human == 'n' || human == 'm' || human == 'r');

        switch(human)
        {            
            case 'w':
                White.owner = Owner.HUMAN;
                Black.owner = Owner.ROBOT;
                break;
            case 'b':            
                White.owner = Owner.ROBOT;
                Black.owner = Owner.HUMAN;
                break;
            case 'a':            
                White.owner = Owner.HUMAN;
                Black.owner = Owner.HUMAN;
                break;
            case 'n':            
                White.owner = Owner.ROBOT;
                Black.owner = Owner.ROBOT;
                break;
            case 'm':            
                White.owner = Owner.RANDOM;
                Black.owner = Owner.ROBOTSASHA;
                RobotSasha.Initialiser();
                break;
            case 'r':
                White.owner = Owner.ROBOT;
                Black.owner = Owner.RANDOM;
                break;
        }
    }
}