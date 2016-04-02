import javafx.util.Pair;

import java.util.HashMap;

class RobotSasha {

    private HashMap<Pair<Etat,Etat>, Double> alpha;
    private HashMap<Pair<Etat,Etat>, Integer> nbQ;
    private HashMap<Pair<Etat,Etat>, Double> q;

    private static double  alpha_facteur = 0.99;

    static void makeNextWhiteMoves() {

        System.out.println("YOLO");
    }

    static void makeNextBlackMoves() {
        System.out.println("Swag");

    }

    public Double getQValue(Pair<Etat,Etat> conjecture) {
        if(!q.containsKey(conjecture))
            return q.get(conjecture);
        return EvaluateConjecture(conjecture);
    }

    public Double getAlphaValue(Pair<Etat,Etat> conjecture) {
        if(!alpha.containsKey(conjecture))
            return alpha.get(conjecture);
        return 1.0;
    }

    public int getNbQ(Pair<Etat,Etat> conjecture) {
        if(nbQ.containsKey(conjecture))
            return nbQ.get(conjecture);
        return 0;
    }

    public void setQ(Pair<Etat,Etat> conjecture, double value)
    {
        q.put(conjecture,value);
        if(!nbQ.containsKey(conjecture))
            nbQ.put(conjecture, 1);
        else
            nbQ.put(conjecture,nbQ.get(conjecture)+1);
        if(!alpha.containsKey(conjecture))
            alpha.put(conjecture, 1.0);
        else
            alpha.put(conjecture, alpha.get(conjecture)*alpha_facteur);
    }



    private Etat getState(Board b, Player p)
    {
        Etat etatPresent = new Etat(p);
        for(int r = 0; r<Board.rows; r++) {
            // Check only valid cols
            int c = (r % 2 == 0) ? 0 : 1;
            for (; c < Board.cols; c += 2) {
                if (b.cell[r][c] != CellEntry.empty)
                {
                    etatPresent.add(b.cell[r][c]);
                    if(estSafe(r,c,b)){
                        etatPresent.addSafe(b.cell[r][c]);
                    }else if(estMenace(r,c,b)){
                        etatPresent.addMenace(b.cell[r][c]);
                    }
                }
            }
        }
        return etatPresent;
    }

    private boolean estMenace(int r, int c, Board b) {
        //Todo
        return true;
    }

    private boolean estSafe(int r, int c, Board b) {
        if(r == 0 || c == 0 || r == b.rows-1 || c == b.cols-1) {
            return true;
        }
        Player cur = enumHelper.getCellPlayer(b.cell[r][c]);
        if(enumHelper.getCellPlayer(b.cell[r+1][c+1]) == cur || enumHelper.getCellPlayer(b.cell[r-1][c-1])== cur)
        {
            if(enumHelper.getCellPlayer(b.cell[r+1][c-1]) == cur || enumHelper.getCellPlayer(b.cell[r-1][c+1])== cur)
            {
                return true;
            }
        }
        return false;
    }



    private Double EvaluateConjecture(Pair<Etat, Etat> conjecture) {
        if(conjecture.getValue().miensVivants == 0)
            return -100.0;
        if(conjecture.getValue().ennemisVivants == 0)
            return 100.0;
        return 0.0;
    }

    private class Etat {
        int miensVivants;
        int ennemisVivants;
        int miensIntouchables;
        int ennemisIntouchables;
        int miensMenaces;
        int ennemisMenaces;
        int miensReines;
        int ennemisReines;
        private Player player;

        Etat(Player p)
        {
            player = p;
            miensVivants = 0;
            ennemisVivants = 0;
            miensIntouchables = 0;
            ennemisIntouchables = 0;
            miensMenaces = 0;
            ennemisMenaces = 0;
            miensReines = 0;
            ennemisReines = 0;
        }

        void add(CellEntry e)
        {

        }

        void addMenace(CellEntry e)
        {

        }

        void addSafe(CellEntry e)
        {

        }

        @Override
        public int hashCode() {
            int result = 13;
            result = 37 * result + miensVivants;
            result = 37 * result + ennemisVivants;
            result = 37 * result + miensIntouchables;
            result = 37 * result + ennemisIntouchables;
            result = 37 * result + miensMenaces;
            result = 37 * result + ennemisMenaces;
            return result;
        }
    }
}