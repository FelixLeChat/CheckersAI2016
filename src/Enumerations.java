/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ASHISH
 */
enum CellEntry{ 
    inValid,
    empty,
    white,
    whiteKing,
    black,
    blackKing
}

enum Player{
    white,
    black    
}

/**
 * Owner is anybody who owns a color.
 * In a two player game the owners of both the colors will be Owner.HUMAN
 * In a single player game the owner of one color will be Owner.HUMAN and the owner of the other will be Owner.ROBOT.
 * @author apurv
 */
enum Owner{
    HUMAN,
    ROBOT,
    ROBOTALICE,
    ROBOTSASHA,
    RANDOM
}

enum MoveDir{
    forwardLeft,
    forwardRight,
    backwardLeft,
    backwardRight
}