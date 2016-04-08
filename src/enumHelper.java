public class enumHelper {
    public static Player getCellPlayer(CellEntry entry) {
        if (entry == CellEntry.white || entry == CellEntry.whiteKing)
            return Player.white;
        if (entry == CellEntry.black || entry == CellEntry.blackKing)
            return Player.black;
        return null;
    }

    public static MoveDir switchDirection(MoveDir dir)
    {
        switch (dir){
            case forwardLeft:
                return MoveDir.backwardRight;
            case forwardRight:
                return MoveDir.backwardLeft;
            case backwardLeft:
                return MoveDir.forwardRight;
            case backwardRight:
                return MoveDir.forwardLeft;
        }
        return null;
    }
}
