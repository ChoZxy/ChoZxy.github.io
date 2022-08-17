
package jump61;


import java.util.Random;

import static jump61.Side.*;

/** An automated Player.
 *  @author P. N. Hilfinger
 */
class AI extends Player {

    /** A new player of GAME initially COLOR that chooses moves automatically.
     *  SEED provides a random-number seed used for choosing moves.
     */
    AI(Game game, Side color, long seed) {
        super(game, color);
        _random = new Random(seed);
    }

    @Override
    String getMove() {
        Board board = getGame().getBoard();

        assert getSide() == board.whoseMove();
        int choice = searchForMove();
        getGame().reportMove(board.row(choice), board.col(choice));
        return String.format("%d %d", board.row(choice), board.col(choice));
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private int searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert  getSide() == work.whoseMove();
        _foundMove = -1;
        int sense;
        if (getSide() == RED) {
            sense = 1;
        } else {
            sense = -1;
        }
        minMax(work, 4, true, sense, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return _foundMove;
    }


    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */

    private int minMax(Board board, int depth, boolean saveMove,
                       int sense, int alpha, int beta) {
        if (sense > 0) {
            return maxPlayerValue(board, depth, true, alpha, beta);
        } else {
            return minPlayerValue(board, depth, true, alpha, beta);
        }
    }

    /**
     * caphelp  function @param cond.
     * * @param board num
     * * @param depth smth
     * * @param saveMove smth
     * * @param alpha smth
     * * @param beta smth
     * * @return smth
     */
    int maxPlayerValue(Board board, int depth, boolean saveMove, int alpha,
                       int beta) {
        int bestmove = 0;
        if (depth == 0 || board.getWinner() != null) {
            return staticEval(board, WINNING);
        }
        int bestSoFar = Integer.MIN_VALUE;

        for (int i = 0; i < board.size() * board.size(); i++) {
            if (board.isLegal(RED, i)) {
                board.addSpot(RED, board.row(i), board.col(i));
                int response = minPlayerValue(board, depth - 1,
                        false, alpha, beta);
                board.undo();
                if (response > bestSoFar) {
                    bestSoFar = response;
                    bestmove = i;
                    alpha = Math.max(alpha, bestSoFar);
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
        }
        if (saveMove) {
            _foundMove = bestmove;
        }

        return bestSoFar;
    }
    /**
     * caphelp  function @param cond.
     * * @param board num
     * * @param depth smth
     * * @param saveMove smth
     * * @param alpha smth
     * * @param beta smth
     * * @return smth
     */
    int minPlayerValue(Board board, int depth, boolean saveMove,
                        int alpha, int beta) {
        int bestmove = 0;
        if (depth == 0 || board.getWinner() != null) {
            return staticEval(board, WINNING);
        }
        int bestSoFar = Integer.MAX_VALUE;
        for (int i = 0; i < board.size() * board.size(); i++) {
            if (board.isLegal(BLUE, i)) {
                board.addSpot(BLUE, board.row(i), board.col(i));
                int response = maxPlayerValue(board, depth - 1,
                        false, alpha, beta);
                board.undo();
                if (response < bestSoFar) {
                    bestSoFar = response;
                    bestmove = i;
                    beta = Math.min(beta, bestSoFar);
                    if (alpha >= beta) {
                        break;
                    }
                }
            }

        }
        if (saveMove) {
            _foundMove = bestmove;
        }
        return bestSoFar;

    }

    /** Return a heuristic estimate of the value of board position B.
     *  Use WINNINGVALUE to indicate a win for Red and -WINNINGVALUE to
     *  indicate a win for Blue. */
    private int staticEval(Board b, int winningValue) {

        if (b.getWinner() == null) {
            return b.numOfSide(RED) - b.numOfSide(BLUE);
        } else if (b.getWinner() == BLUE) {
            return -winningValue;
        } else {
            return winningValue;
        }
    }

    /** A random-number generator used for move selection. */
    private Random _random;

    /** Used to convey moves discovered by minMax. */
    private int _foundMove;

    /** Used to convey moves discovered by minMax. */
    private static final int WINNING = 10000;
}
