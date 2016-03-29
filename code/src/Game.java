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
        while(!Game.board.CheckGameComplete())
        {  
            if(Game.board.CheckGameDraw(Player.white)){
                break;
            }
            
            White.Move();
            if(Game.board.CheckGameComplete()){
                UserInteractions.DisplayGreetings(Player.white);
                Game.board.Display();
                break;
            }

            if(Game.board.CheckGameDraw(Player.black)){
                break;
            }
            
            /////////////////////////////////////////
            //System.out.println("Black ="+Game.board.blackPieces+", White="+Game.board.whitePieces);
            /////////////////////////////////////////
            Game.board.Display();
            
            Black.Move();
            if(Game.board.CheckGameComplete()){
                UserInteractions.DisplayGreetings(Player.black);                
                Game.board.Display();
                break;
            }
            
//            Game.board.Display();           
            /////////////////////////////////////////
//            System.out.println("Black ="+Game.board.blackPieces+", White="+Game.board.whitePieces);
            /////////////////////////////////////////
        }
    }
    
   
    private void Initialize(char human)
    {        
        assert(human=='w' || human=='b' || human == 'a' || human == 'n' || human == 'm' || human == 'r');
        
        board  = new Board();
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
                White.owner = Owner.ROBOTALICE;
                Black.owner = Owner.ROBOTSASHA;
                break;
            case 'r':
                White.owner = Owner.RANDOM;
                Black.owner = Owner.RANDOM;
                break;
        }
    }
}