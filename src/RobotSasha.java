import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

class RobotSasha extends Robot{
    private static Conjecture conjecturePrecedenteBlack;
    private static Conjecture conjecturePrecedenteWhite;

    private static MoveKey keyPrecedentBlack;
    private static MoveKey keyPrecedentWhite;

    private static HashMap<Conjecture, Double> alpha;
    private static HashMap<Conjecture, Double> q;

    private static HashMap<MoveKey, Double> qMove;
    private static HashMap<MoveKey, Double> alphaMove;



    private static Conjecture conjecturePrecedente;
    private static MoveKey keyPrecedente;


    static double epsilon = 0.3;
    static double gamma = 0.99;
    static double  alpha_facteur = 0.99;

    public static void makeNextWhiteMoves() {
        conjecturePrecedente = conjecturePrecedenteWhite;
        keyPrecedente = keyPrecedentWhite;
        makeNextMove(Player.white);
        conjecturePrecedenteWhite = conjecturePrecedente;
        keyPrecedentWhite = keyPrecedente;

    }

    public static void makeNextBlackMoves(){
        conjecturePrecedente = conjecturePrecedenteBlack;
        keyPrecedente = keyPrecedentBlack;
        makeNextMove(Player.black);
        conjecturePrecedenteBlack = conjecturePrecedente;
        keyPrecedentBlack = keyPrecedente;
    }

    static void Initialiser()
    {
        alpha = new HashMap<Conjecture, Double>();
        q = new HashMap<Conjecture, Double>();
        alphaMove = new HashMap<MoveKey, Double>();
        qMove = new HashMap<MoveKey, Double>();
    }

    static void debutGame()
    {
        conjecturePrecedenteWhite = null;
        conjecturePrecedenteBlack = null;
        keyPrecedentBlack = null;
        keyPrecedentWhite= null;

        keyPrecedente = null;
        conjecturePrecedente = null;
    }

    //C'est un QLearning malgré le look inversé. On n'effectue pas l'action suivante avant d'assigner la qValue
    private static void makeNextMove(Player p) {

        EtatAbstrait etatCourant = getState(Game.board, p);
        Vector<Vector<Move>> actionsPossibles = expandMoves(Game.board, p);
        Vector<Vector<Move>> meilleuresActions = getMeilleursAction(actionsPossibles, etatCourant, p);
        if(conjecturePrecedente != null && keyPrecedente != null)
        {
            double qValue = getQValue(conjecturePrecedente) + (getAlphaValue(conjecturePrecedente) * ((reward(p) + (gamma * getMaxQValue(meilleuresActions, etatCourant, p))) - getQValue(conjecturePrecedente)));
            setQ(conjecturePrecedente,qValue);

            double qMoveValue = getQMoveValue(keyPrecedente) + getAlphaMoveValue(keyPrecedente) * ((reward(p)+gamma* getMaxQMoveValue(meilleuresActions, p) - getQMoveValue(keyPrecedente)));
            setQMove(keyPrecedente,qMoveValue);
        }
        if(meilleuresActions.size() > 0) {
            Vector<Move> action = epsilonGreedy(etatCourant, meilleuresActions, p);
            keyPrecedente = new MoveKey(getMoveState(action.elementAt(0),p),action.elementAt(0).getMoveDir(),p);
            executeAction(action, Game.board, p);
            conjecturePrecedente = new Conjecture(etatCourant,getState(Game.board, p));
        }
        //EXECUTER L'action de l'autre bot
    }

    private static double getMaxQMoveValue(Vector<Vector<Move>> meilleuresActions, Player p) {
        if(meilleuresActions.size() == 0)
            return 0;
        Vector<Move> meilleureAction = meilleuresActions.elementAt(0) ;
        EtatMove etat =  getMoveState(meilleureAction.elementAt(0), p);
        MoveKey mk = new MoveKey(etat,meilleureAction.elementAt(0).getMoveDir(), p);
        double meilleurQ = getQMoveValue(mk);

        for(Vector<Move> action: meilleuresActions)
        {
            etat =  getMoveState(action.elementAt(0), p);
            mk = new MoveKey(etat,action.elementAt(0).getMoveDir(), p);
            double qValuePossible = getQMoveValue(mk);
            if(qValuePossible>meilleurQ)
            {
                meilleurQ = qValuePossible;
            }
        }
        return meilleurQ;
    }


    //Retour les meilleures actions ayant la meme utilite par rapport a l'etat abstrait
    private static Vector<Vector<Move>> getMeilleursAction(Vector<Vector<Move>> actionsPossibles, EtatAbstrait etatCourant, Player p) {
        if(actionsPossibles.size()==0)
            return new Vector<Vector<Move>>();

        Vector<Vector<Move>> meilleuresActionsPossibles =  new Vector<Vector<Move>>();
        Vector<Move> meilleureAction = actionsPossibles.elementAt(0) ;

        Board copy = Game.board.duplicate();
        executeAction(meilleureAction, copy, p);
        EtatAbstrait etatSuivantPossible = getState(copy, p);
        Conjecture test = new Conjecture( etatCourant, etatSuivantPossible);
        double meilleurQ = getQValue(test);

        for(Vector<Move> action: actionsPossibles)
        {
            copy = Game.board.duplicate();
            executeAction(action, copy, p);
            etatSuivantPossible = getState(copy, p);
            double qValuePossible = getQValue(new Conjecture( etatCourant, etatSuivantPossible));
            if(qValuePossible>meilleurQ)
            {
                meilleurQ = qValuePossible;
                meilleuresActionsPossibles =  new Vector<Vector<Move>>();
                meilleuresActionsPossibles.add(action);
            }
            else if(qValuePossible == meilleurQ)
            {
                meilleuresActionsPossibles.add(action);
            }
        }
        return meilleuresActionsPossibles;
    }

    private static double reward(Player p) {
        if(Game.WhiteVictory())
        {
            if(p == Player.white)
                return 100;
            return -100;
        }
        if(Game.BlackVictory())
        {
            if(p == Player.black)
                return 100;
            return -100;
        }
        if(Game.isDraw())
            return -50.0;
        return -1.0;
    }

    private static double getMaxQValue(Vector<Vector<Move>> meilleuresActions, EtatAbstrait nouvelEtat, Player p) {
        if(meilleuresActions.size() == 0)
            return 0;
        Vector<Move> meilleureAction = meilleuresActions.elementAt(0) ;
        Board copy = Game.board.duplicate();
        executeAction(meilleureAction, copy, p);
        EtatAbstrait etatSuivantPossible = getState(copy, p);
        return getQValue(new Conjecture( nouvelEtat, etatSuivantPossible));
    }

    private static Vector<Move> epsilonGreedy(EtatAbstrait EtatCourant, Vector<Vector<Move>> meilleuresActionsPossibles , Player p) {
        Vector<Move> moves;
        double rnd = new Random().nextDouble();
        if(rnd < epsilon)
        {
            int rnd2 = new Random().nextInt(meilleuresActionsPossibles.size());
            moves = meilleuresActionsPossibles.elementAt(rnd2);
        }else{

            if(meilleuresActionsPossibles.size() == 1)
                return meilleuresActionsPossibles.elementAt(0);
            moves = epsilonGreedyMoves(meilleuresActionsPossibles, p);
        }
        return moves;
    }


    private static Vector<Move> epsilonGreedyMoves(Vector<Vector<Move>> actionsPossibles , Player p) {
        Vector<Move> moves;
        double rnd = new Random().nextDouble();
        if(rnd < epsilon)
        {
            int rnd2 = new Random().nextInt(actionsPossibles.size());
            moves = actionsPossibles.elementAt(rnd2);
        }else{
            Vector<Vector<Move>> meilleuresActionsPossibles =  new Vector<Vector<Move>>();
            Vector<Move> meilleureAction = actionsPossibles.elementAt(0) ;
            EtatMove etat =  getMoveState(meilleureAction.elementAt(0), p);
            MoveKey mk = new MoveKey(etat,meilleureAction.elementAt(0).getMoveDir(), p);
            double meilleurQ = getQMoveValue(mk);

            for(Vector<Move> action: actionsPossibles)
            {
                etat =  getMoveState(action.elementAt(0), p);
                mk = new MoveKey(etat,action.elementAt(0).getMoveDir(), p);
                double qValuePossible = getQMoveValue(mk);
                if(qValuePossible>meilleurQ)
                {
                    meilleurQ = qValuePossible;
                    meilleuresActionsPossibles =  new Vector<Vector<Move>>();
                    meilleuresActionsPossibles.add(action);
                }
                else if(qValuePossible == meilleurQ)
                {
                    meilleuresActionsPossibles.add(action);
                }
            }
            moves = meilleuresActionsPossibles.get(new Random().nextInt(meilleuresActionsPossibles.size()));
        }
        return moves;
    }

    private static EtatMove getMoveState(Move action, Player p) {
        int initialRow = action.initialRow;
        int initialCol = action.initialCol;
        EtatMove em = new EtatMove(p, initialRow, initialCol);
        int iterator = 1;
        if(p == Player.white)
            iterator = -1;
        for(int r = initialRow - 2 * iterator; r <= initialRow+ 2* iterator; r+=iterator)
        {
            for(int c = initialCol - 2* iterator; c <= initialCol + 2* iterator; c+=iterator)
            {
                if(r >=0 && c>=0 && r < Board.rows && c < Board.cols &&( r!=initialRow || c!=initialCol))
                {
                    em.addEntite(Game.board.cell[r][c], r, c);
                }
            }
        }
        return em;
    }


    private static void executeAction(Vector<Move> action, Board board, Player p) {
        for(Move m:action){
            if(p ==   Player.white)
                board.genericMakeWhiteMove(m);
            else
                board.genericMakeBlackMove(m);

        }
    }


    private static Double getQValue(Conjecture conjecture) {
        if(q.containsKey(conjecture))
            return q.get(conjecture);
        return 0.0;
    }

    private static Double getQMoveValue(MoveKey key) {
        if(qMove.containsKey(key))
            return qMove.get(key);
        return 0.0;
    }

    private static Double getAlphaValue(Conjecture conjecture) {
        if(alpha.containsKey(conjecture))
            return alpha.get(conjecture);
        return 1.0;
    }

    public static Double getAlphaMoveValue(MoveKey key) {
        if(alphaMove.containsKey(key))
            return alphaMove.get(key);
        return 1.0;
    }

    private static void setQ(Conjecture conjecture, double value)
    {
        q.put(conjecture,value);
        if(!alpha.containsKey(conjecture))
            alpha.put(conjecture, 1.0);
        else
            alpha.put(conjecture, alpha.get(conjecture)*alpha_facteur);
    }

    private static void setQMove(MoveKey key, double value)
    {
        qMove.put(key,value);
        if(!alphaMove.containsKey(key))
            alphaMove.put(key, 1.0);
        else
            alphaMove.put(key, alphaMove.get(key)*alpha_facteur);
    }



    private static EtatAbstrait getState(Board b, Player p)
    {
        EtatAbstrait etatPresent = new EtatAbstrait(p);
        for(int r = 0; r<Board.rows; r++) {
            // Check only valid cols
            int c = (r % 2 == 0) ? 0 : 1;
            for (; c < Board.cols; c += 2) {
                if (b.cell[r][c] != CellEntry.empty)
                {
                    etatPresent.add(b.cell[r][c]);
                    if(!estSafe(r,c,b) && estMenace(r,c,b)){
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


    private static class EtatAbstrait {
        int miensVivants;
        int ennemisVivants;
        int miensMenaces;
        int ennemisMenaces;
        private Player player;

        EtatAbstrait(Player p)
        {
            player = p;
            miensVivants = 0;
            ennemisVivants = 0;
            miensMenaces = 0;
            ennemisMenaces = 0;
        }

        void add(CellEntry e)
        {
            switch (e)
            {
                case blackKing:
                case black:
                    if(player == Player.black) {
                        miensVivants++;
                    }else{
                        ennemisVivants++;
                    }
                    break;
                case whiteKing:
                case white:
                    if(player == Player.black) {
                        ennemisVivants++;
                    }else{
                        miensVivants++;
                    }
                    break;
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
        @Override
        public int hashCode() {
            int result = 13;
            result = 37 * result + miensVivants;
            result = 37 * result + ennemisVivants;
            result = 37 * result + miensMenaces;
            result = 37 * result + ennemisMenaces;
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EtatAbstrait)) return false;
            EtatAbstrait other = (EtatAbstrait) o;
            return player == other.player &&
            miensVivants == other.miensVivants &&
            ennemisVivants == other.ennemisVivants &&
            miensMenaces == other.miensMenaces &&
            ennemisMenaces == other.ennemisMenaces ;
        }
    }

    private static class Conjecture {

        private final EtatAbstrait x;
        private final EtatAbstrait y;

        public Conjecture(EtatAbstrait x, EtatAbstrait y) {
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


    private static class EtatMove {

        private Vector<Entite> entitesProches;
        private Player p;
        private int row ,col;


        public EtatMove(Player p, int r, int c){
            entitesProches = new Vector<Entite>();
            this.p = p;
            if(p == Player.white)
            {
                this.row = 7-r;
                this.col = 7-c;
            }else{
                this.row = r;
                this.col = c;
            }
        }

        public void addEntite(CellEntry cell, int r, int c)
        {
            //Obtenir le type de cellule
            Entite.cellType cellType = Entite.cellType.vide;
            switch (cell) {
                case inValid:
                    return;
                case empty:
                    cellType = Entite.cellType.vide;
                    break;
                case white:
                case whiteKing:
                    if(p == Player.white)
                        cellType = Entite.cellType.ami;
                    else
                        cellType = Entite.cellType.ennemi;
                    break;
                case black:
                case blackKing:
                    if(p == Player.black)
                        cellType = Entite.cellType.ami;
                    else
                        cellType = Entite.cellType.ennemi;
                    break;
            }

            //Obtenir la position relative en fonction de la position courante
            //On flip les coordonnes des blancs
            if(p == Player.white)
            {
                r = 7-r;
                c = 7-c;
            }


            entitesProches.add(new Entite(cellType, r-row, c-col));
            //Sort entiteProches
        }

        @Override
        public int hashCode() {
            int result = entitesProches.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EtatMove)) return false;
            EtatMove em = (EtatMove) o;
            return entitesProches.equals(em.entitesProches);
        }



        private static class Entite{
            private cellType type;
            private int x;
            private int y;

            public Entite(cellType type, int xRelative, int yRelative) {
                x = xRelative;
                y = yRelative;
                this.type = type;
            }

            public enum cellType
            {
                ennemi,
                ami,
                vide
            }
            @Override
            public int hashCode() {
                int result = (x * 37 +y )*37 + type.hashCode();
                return result;
            }
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Entite)) return false;
                Entite et = (Entite) o;
                return x == et.x && y == et.y && type == et.type;
            }
        }
    }


    private static class MoveKey {

        private final EtatMove etat;
        private final MoveDir dir;

        public MoveKey(EtatMove x, MoveDir y, Player p) {
            this.etat = x;
            if(p == Player.white)
                dir = enumHelper.switchDirection(y);
            else
                dir = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MoveKey)) return false;
            MoveKey move = (MoveKey) o;
            return etat.equals(move.etat) && dir == move.dir;
        }

        @Override
        public int hashCode() {
            int result = etat.hashCode();
            result = 31 * result + dir.hashCode();
            return result;
        }

    }
}