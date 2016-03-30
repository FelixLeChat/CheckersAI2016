public class enumHelper {
    public static Player getCellPlayer(CellEntry entry) {
        if (entry == CellEntry.white || entry == CellEntry.whiteKing)
            return Player.white;
        if (entry == CellEntry.black || entry == CellEntry.blackKing)
            return Player.black;
        return null;
    }
}
