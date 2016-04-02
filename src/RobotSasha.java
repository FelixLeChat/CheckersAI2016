import javafx.util.Pair;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

class RobotSasha extends Robot{
    private static Etat etatPrecedentBlack;
    private static Etat actionPrecedenteBlack;
    private static Etat etatPrecedentWhite;
    private static Etat actionPrecedenteWhite;

    private static HashMap<Conjecture, Double> alpha;
    private static HashMap<Conjecture, Integer> nbQ;
    private static HashMap<Conjecture, Double> q;

    public static double epsilon = 0.3;
    private static double gamma = 0.99;
    public static double  alpha_facteur = 0.99;

    public static void makeNextWhiteMoves() {
        makeNextMove(Player.white);
    }

    public static void makeNextBlackMoves(){ makeNextMove(Player.black); }

    public static void Initialiser()
    {
        alpha = new HashMap<Conjecture, Double>();
        nbQ  = new HashMap<Conjecture, Integer>();
        q = new HashMap<Conjecture, Double>();
    }

    public static void debutGame()
    {
        etatPrecedentBlack = null;
        actionPrecedenteBlack = null;
        etatPrecedentWhite = null;
        actionPrecedenteWhite= null;
    }

    //C'est un QLearning malgré le look inversé. On n'effectue pas l'action suivante avant d'assigner la qValue
    private static void makeNextMove(Player p) {

        Etat etatCourant = getState(Game.board, p);
        Vector<Vector<Move>> actionsPossibles = expandMoves(Game.board, p);
        if(etatPrecedent(p) != null && actionPrecedente(p)!=null)
        {
            Conjecture conjecture = new Conjecture(etatPrecedent(p), actionPrecedente(p));
            double qValue = getQValue(conjecture) + getAlphaValue(conjecture) * (reward(etatCourant)+gamma* getMaxQValue(actionsPossibles, etatCourant, p) - getQValue(conjecture));
            setQ(conjecture,qValue);
        }
        if(actionsPossibles.size() > 0) {
            Vector<Move> action = epsilonGreedy(etatCourant, actionsPossibles, p);
            executeAction(action, Game.board, p);
            setEtatPrecedent(p, etatCourant);
            setActionPrecedente(p,getState(Game.board, p));
        }
        //EXECUTER L'action de l'autre bot
    }

    private static void setActionPrecedente(Player p, Etat state) {
        if(p == Player.white)
            actionPrecedenteWhite = state;
        else
            actionPrecedenteBlack = state;
    }

    private static void setEtatPrecedent(Player p, Etat etatCourant) {
        if(p == Player.white)
            etatPrecedentWhite = etatCourant;
        else
            etatPrecedentBlack = etatCourant;
    }

    private static Etat actionPrecedente(Player p) {
        if(p == Player.black)
            return actionPrecedenteBlack;
        return actionPrecedenteWhite;
    }

    private static Etat etatPrecedent(Player p) {
        if(p == Player.white)
            return etatPrecedentWhite;
        return etatPrecedentBlack;
    }

    private static double reward(Etat etatCourant) {
        if(etatCourant.miensVivants == 0)
            return -100.0;
        if(etatCourant.ennemisVivants == 0)
            return 100.0;
        return -1.0;
    }

    private static double getMaxQValue(Vector<Vector<Move>> actionsPossibles, Etat nouvelEtat, Player p) {
        if(actionsPossibles.size() == 0)
            return 0;
        Vector<Move> meilleureAction = actionsPossibles.elementAt(0) ;
        Board copy = Game.board.duplicate();
        executeAction(meilleureAction, copy, p);
        Etat etatSuivantPossible = getState(copy, p);
        double meilleurQ = getQValue(new Conjecture( nouvelEtat, etatSuivantPossible));

        for(Vector<Move> action: actionsPossibles)
        {
            copy = Game.board.duplicate();
            executeAction(action, copy, p);
            etatSuivantPossible = getState(copy, p);
            double qValuePossible = getQValue(new Conjecture( nouvelEtat, etatSuivantPossible));
            if(qValuePossible>meilleurQ)
            {
                meilleurQ = qValuePossible;
            }
        }
        return meilleurQ;
    }

    private static Vector<Move> epsilonGreedy(Etat EtatCourant,Vector<Vector<Move>> actionsPossibles , Player p) {
        Vector<Move> moves;
        double rnd = new Random().nextDouble();
        if(rnd < epsilon)
        {
            int rnd2 = new Random().nextInt(actionsPossibles.size());
            moves = actionsPossibles.elementAt(rnd2);
        }else{
            Vector<Move> meilleureAction = actionsPossibles.elementAt(0) ;

            Board copy = Game.board.duplicate();
            executeAction(meilleureAction, copy, p);
            Etat etatSuivantPossible = getState(copy, p);
            Conjecture test = new Conjecture( EtatCourant, etatSuivantPossible);
            double meilleurQ = getQValue(test);

            for(Vector<Move> action: actionsPossibles)
            {
                copy = Game.board.duplicate();
                executeAction(action, copy, p);
                etatSuivantPossible = getState(copy, p);
                double qValuePossible = getQValue(new Conjecture( EtatCourant, etatSuivantPossible));
                if(qValuePossible>meilleurQ)
                {
                    meilleureAction = action;
                    meilleurQ = qValuePossible;
                }
            }
            moves = meilleureAction;
        }
        return moves;
    }



    private static void executeAction(Vector<Move> action, Board board, Player p) {
        for(Move m:action){
            if(p ==   Player.white)
                board.genericMakeWhiteMove(m);
            else
                board.genericMakeBlackMove(m);

        }
    }


    public static Double getQValue(Conjecture conjecture) {
        if(q.containsKey(conjecture))
            return q.get(conjecture);
        return 0.0;
    }

    public static Double getAlphaValue(Conjecture conjecture) {
        if(alpha.containsKey(conjecture))
            return alpha.get(conjecture);
        return 1.0;
    }

    public static int getNbQ(Conjecture conjecture) {
        if(nbQ.containsKey(conjecture))
            return nbQ.get(conjecture);
        return 0;
    }

    public static void setQ(Conjecture conjecture, double value)
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



    private static Etat getState(Board b, Player p)
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

    private static boolean estMenace(int r, int c, Board b) {
        //black est au top
        Player player = enumHelper.getCellPlayer(b.cell[r][c]);
        if(player == Player.black)
        {
            if(( enumHelper.getCellPlayer(b.cell[r-1][c-1]) == Player.white && b.cell[r+1][c+1]==CellEntry.empty)
                    || ( enumHelper.getCellPlayer(b.cell[r-1][c+1]) == Player.white && b.cell[r+1][c-1]==CellEntry.empty)
                    || (b.cell[r+1][c+1] == CellEntry.whiteKing && b.cell[r-1][c-1]==CellEntry.empty)
                    || ( b.cell[r+1][c-1] ==CellEntry.whiteKing && b.cell[r-1][c+1]==CellEntry.empty)
                    ) {
                return true;
            }

        }
        else
        {
            if(( enumHelper.getCellPlayer(b.cell[r+1][c-1]) == Player.black && b.cell[r-1][c+1]==CellEntry.empty)
                    || ( enumHelper.getCellPlayer(b.cell[r+1][c+1]) == Player.black && b.cell[r-1][c-1]==CellEntry.empty)
                    || (b.cell[r-1][c+1] == CellEntry.blackKing && b.cell[r+1][c-1]==CellEntry.empty)
                    || ( b.cell[r-1][c-1] ==CellEntry.blackKing && b.cell[r+1][c+1]==CellEntry.empty)
                    ) {
                return true;
            }
        }
        return false;
    }

    private static boolean estSafe(int r, int c, Board b) {
        if(r == 0 || c == 0 || r == Board.rows -1 || c == Board.cols -1) {
            return true;
        }
        Player player = enumHelper.getCellPlayer(b.cell[r][c]);

        if(enumHelper.getCellPlayer(b.cell[r+1][c+1]) == player || enumHelper.getCellPlayer(b.cell[r-1][c-1])== player)
        {
            if(enumHelper.getCellPlayer(b.cell[r+1][c-1]) == player || enumHelper.getCellPlayer(b.cell[r-1][c+1])== player)
            {
                return true;
            }
        }
        return false;
    }



    private static Double EvaluateConjecture(Pair<Etat, Etat> conjecture) {
        if(conjecture.getValue().miensVivants == 0)
            return -100.0;
        if(conjecture.getValue().ennemisVivants == 0)
            return 100.0;
        return 0.0;
    }

    private static class Etat {
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
            if(player == Player.black)
            {
                switch (e)
                {
                    case blackKing:
                     //   miensReines++;
                    case black:
                        miensVivants++;
                        break;
                    case whiteKing:
                    //    ennemisReines ++;
                    case white:
                        ennemisVivants++;
                        break;
                }

            }else{
                switch (e)
                {
                    case blackKing:
                    //    ennemisReines++;
                    case black:
                        ennemisVivants++;
                        break;
                    case whiteKing:
                    //    miensReines ++;
                    case white:
                        miensVivants++;
                        break;
                }
            }
        }

        void addMenace(CellEntry e)
        {
            if(player == enumHelper.getCellPlayer(e))
            {
                miensMenaces ++;
            }else{
                ennemisMenaces++;
            }
        }

        void addSafe(CellEntry e)
        {
            //if(player == enumHelper.getCellPlayer(e))
            //{
             //   miensIntouchables ++;
            //}else{
             //   ennemisIntouchables ++;
            //}
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Etat)) return false;
            Etat other = (Etat) o;
            return player == other.player &&
            miensVivants == other.miensVivants &&
            ennemisVivants == other.ennemisVivants &&
            miensIntouchables ==other.miensIntouchables &&
            ennemisIntouchables ==other.ennemisIntouchables &&
            miensMenaces == other.miensMenaces &&
            ennemisMenaces == other.ennemisMenaces &&
            miensReines == other.miensReines &&
            ennemisReines == other.ennemisReines;
        }
    }

    private static class Conjecture {

        private final Etat x;
        private final Etat y;

        public Conjecture(Etat x, Etat y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Conjecture)) return false;
            Conjecture conj = (Conjecture) o;
            return x.equals(conj.x) && y.equals(conj.y);
        }

        @Override
        public int hashCode() {
            int result = x.hashCode();
            result = 31 * result + y.hashCode();
            return result;
        }

    }
}