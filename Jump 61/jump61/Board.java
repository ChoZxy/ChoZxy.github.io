
package jump61;

import java.util.function.Consumer;
import java.util.ArrayList;

import java.util.Formatter;
import java.util.Stack;
import java.util.ArrayDeque;


import static jump61.Side.*;
import static jump61.Square.square;

/** Represents the state of a Jump61 game.  Squares are indexed either by
 *  row and column (between 1 and size()), or by square number, numbering
 *  squares by rows, with squares in row 1 numbered from 0 to size()-1, in
 *  row 2 numbered from size() to 2*size() - 1, etc. (i.e., row-major order).
 *
 *  A Board may be given a notifier---a Consumer<Board> whose
 *  .accept method is called whenever the Board's contents are changed.
 *
 *  @author Xuanyi Zhang
 */
class Board {

    /** An uninitialized Board.  Only for use by subtypes. */
    protected Board() {
        _notifier = NOP;
    }

    /** An N x N board in initial configuration. */
    Board(int N) {
        this();
        _squares = new Square[N * N];
        for (int i = 0; i < N * N; i++) {
            _squares[i] = square(WHITE, 1);
        }
        _size = N;

        _numMoves = 0;

    }

    /** A board whose initial contents are copied from BOARD0, but whose
     *  undo history is clear, and whose notifier does nothing. */

    Board(Board board0) {
        this(board0.size());
        copy(board0);



        _readonlyBoard = new ConstantBoard(this);
    }

    /** Returns a readonly version of this board. */
    Board readonlyBoard() {
        return _readonlyBoard;
    }

    /** (Re)initialize me to a cleared board with N squares on a side. Clears
     *  the undo history and sets the number of moves to 0. */
    void clear(int N) {
        _squares = new Square[N * N];
        for (int i = 0; i < N * N; i++) {
            _squares[i] = square(WHITE, 1);
        }
        _size = N;
        _history.clear();
        _numMoves = 0;

        announce();
    }

    /** Copy the contents of BOARD into me. */

    void copy(Board board) {
        this._squares = new Square[board.size() * board.size()];
        internalCopy(board);
        _history.clear();
        _numMoves = 0;
    }

    /** Copy the contents of BOARD into me, without modifying my undo
     *  history. Assumes BOARD and I have the same size. */
    private void internalCopy(Board board) {
        assert size() == board.size();

        for (int i = 0; i < _size * _size; i++) {
            _squares[i] = board.get(i);
        }


    }

    /** Return the number of rows and of columns of THIS. */
    int size() {
        return this._size;
    }

    /** Returns the contents of the square at row R, column C
     *  1 <= R, C <= size (). */
    Square get(int r, int c) {
        return get(sqNum(r, c));
    }

    /** Returns the contents of square #N, numbering squares by rows, with
     *  squares in row 1 number 0 - size()-1, in row 2 numbered
     *  size() - 2*size() - 1, etc. */
    Square get(int n) {
        if (exists(n)) {
            return _squares[n];
        } else {
            throw new GameException("invalid index");
        }
    }

    /** Returns the total number of spots on the board. */
    int numPieces() {
        int totalspots = 0;
        for (int i = 0; i < _squares.length; i++) {
            totalspots += get(i).getSpots();
        }
        return totalspots;
    }

    /** Returns the Side of the player who would be next to move.  If the
     *  game is won, this will return the loser (assuming legal position). */
    Side whoseMove() {
        return ((numPieces() + size()) & 1) == 0 ? RED : BLUE;
    }

    /** Return true iff row R and column C denotes a valid square. */
    final boolean exists(int r, int c) {
        return 1 <= r && r <= size() && 1 <= c && c <= size();
    }

    /** Return true iff S is a valid square number. */
    final boolean exists(int s) {
        int N = size();
        return 0 <= s && s < N * N;
    }

    /** Return the row number for square #N. */
    final int row(int n) {
        return n / size() + 1;
    }

    /** Return the column number for square #N. */
    final int col(int n) {
        return n % size() + 1;
    }

    /** Return the square number of row R, column C. */
    final int sqNum(int r, int c) {
        return (c - 1) + (r - 1) * size();
    }

    /** Return a string denoting move (ROW, COL)N. */
    String moveString(int row, int col) {
        return String.format("%d %d", row, col);
    }

    /** Return a string denoting move N. */
    String moveString(int n) {
        return String.format("%d %d", row(n), col(n));
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
        to square at row R, column C. */
    boolean isLegal(Side player, int r, int c) {
        return isLegal(player, sqNum(r, c));
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
     *  to square #N. */
    boolean isLegal(Side player, int n) {
        if (exists(n) && getWinner() == null) {
            if (whoseMove() == player
                    && player.playableSquare(get(n).getSide())) {
                return true;
            }
        }
        return false;
    }

    /** Returns true iff PLAYER is allowed to move at this point. */
    boolean isLegal(Side player) {
        if (whoseMove() == player && getWinner() == null) {
            return true;
        }
        return false;
    }

    /** Returns the winner of the current position, if the game is over,
     *      *  and otherwise null. */
    final Side getWinner() {
        if (numOfSide(RED) == size() * size()) {
            return  RED;
        } else if (numOfSide(BLUE) == size() * size()) {
            return BLUE;
        } else {
            return null;
        }
    }

    /** Return the number of squares of given SIDE. */
    int numOfSide(Side side) {
        int numside = 0;
        for (int i = 0; i < size() * size(); i++) {
            if (get(i).getSide() == side) {
                numside += 1;
            }
        }
        return numside;
    }

    /** Add a spot from PLAYER at row R, column C.  Assumes
     *  isLegal(PLAYER, R, C). */
    void addSpot(Side player, int r, int c) {
        markUndo();
        int position = (r - 1) * size() + (c - 1);
        addSpot(player, position);
        announce();

    }

    /** Add a spot from PLAYER at square #N.  Assumes isLegal(PLAYER, N). */
    void addSpot(Side player, int n) {
        int num = get(n).getSpots();
        num += 1;
        internalSet(n, num, player);
        if (num == neighbors(n) + 1) {
            _workQueue.clear();
            jump(n);
        }
    }

    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white). */
    void set(int r, int c, int num, Side player) {
        internalSet(r, c, num, player);
        announce();
    }

    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white).  Does not announce
     *  changes. */
    private void internalSet(int r, int c, int num, Side player) {
        internalSet(sqNum(r, c), num, player);
    }

    /** Set the square #N to NUM spots (0 <= NUM), and give it color PLAYER
     *  if NUM > 0 (otherwise, white). Does not announce changes. */
    private void internalSet(int n, int num, Side player) {
        if (num != 0) {
            Square set = square(player, num);
            _squares[n] = set;
        } else {
            _squares[n] = square(WHITE, num);
        }


    }


    /** Undo the effects of one move (that is, one addSpot command).  One
     *  can only undo back to the last point at which the undo history
     *  was cleared, or the construction of this Board. */
    void undo() {

        internalCopy(_history.pop());
        announce();

    }

    /** Record the beginning of a move in the undo history. */
    private void markUndo() {
        Board curboard = new Board(size());
        curboard.copy(this);
        _history.push(curboard);
        _numMoves += 1;

    }

    /** Add DELTASPOTS spots of side PLAYER to row R, column C,
     *  updating counts of numbers of squares of each color. */
    private void simpleAdd(Side player, int r, int c, int deltaSpots) {
        internalSet(r, c, deltaSpots + get(r, c).getSpots(), player);
    }

    /** Add DELTASPOTS spots of color PLAYER to square #N,
     *  updating counts of numbers of squares of each color. */
    private void simpleAdd(Side player, int n, int deltaSpots) {
        internalSet(n, deltaSpots + get(n).getSpots(), player);
    }

    /** Used in jump to keep track of squares needing processing.  Allocated
     *  here to cut down on allocations. */
    private final ArrayDeque<Integer> _workQueue = new ArrayDeque<>();

    /** Do all jumping on this board, assuming that initially, S is the only
     *  square that might be over-full. */
    private void jump(int S) {
        if (get(S).getSpots() > neighbors(S)) {
            for (int i = 0; i < neighborList(S).size(); i++) {
                int currneigh = neighborList(S).get(i);
                simpleAdd(get(S).getSide(), currneigh, 1);
                if (get(currneigh).getSpots() > neighbors(currneigh)) {
                    _workQueue.add(currneigh);
                }
            }
            set(row(S), col(S), get(S).getSpots() - neighbors(S),
                    get(S).getSide());
            if (getWinner() == null) {
                for (int j = 0; j < _workQueue.size(); j++) {
                    jump(_workQueue.poll());
                }
            }

        }

    }

    /** Returns my dumped representation. */
    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int m = 1; m <= size(); m += 1) {
            out.format("    ");
            for (int n = 1; n <= size(); n++) {
                if (get(m, n).getSide() == WHITE) {
                    out.format("%d%s ", get(m, n).getSpots(), "-");
                } else if (get(m, n).getSide() == RED) {
                    out.format("%d%s ", get(m, n).getSpots(), "r");
                } else {
                    out.format("%d%s ", get(m, n).getSpots(), "b");
                }

            }
            out.format("%n");
        }
        out.format("===%n");
        return out.toString();
    }

    /** Returns an external rendition of me, suitable for human-readable
     *  textual display, with row and column numbers.  This is distinct
     *  from the dumped representation (returned by toString). */
    public String toDisplayString() {
        String[] lines = toString().trim().split("\\R");
        Formatter out = new Formatter();
        for (int i = 1; i + 1 < lines.length; i += 1) {
            out.format("%2d %s%n", i, lines[i].trim());
        }
        out.format("  ");
        for (int i = 1; i <= size(); i += 1) {
            out.format("%3d", i);
        }
        return out.toString();
    }

    /** Returns the number of neighbors of the square at row R, column C. */
    int neighbors(int r, int c) {
        int size = size();
        int n;
        n = 0;
        if (r > 1) {
            n += 1;
        }
        if (c > 1) {
            n += 1;
        }
        if (r < size) {
            n += 1;
        }
        if (c < size) {
            n += 1;
        }
        return n;
    }

    /** Returns the number of neighbors of square #N. */
    int neighbors(int n) {
        return neighbors(row(n), col(n));
    }
    /** Returns the number of neighbors of square #N. */
    ArrayList<Integer> neighborList(int n) {
        ArrayList<Integer> rv = new ArrayList();
        if (!(n < size())) {
            rv.add(n - size());
        }
        if (!(n < size() * size() && n >= size() * size() - size())) {
            rv.add(n + size());
        }
        if (!(n % size() == 0)) {
            rv.add(n - 1);
        }
        if (!(n % size() == size() - 1)) {
            rv.add(n + 1);
        }
        return rv;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board)) {
            return false;
        } else {
            Board B = (Board) obj;
            return (this._squares.equals(B._squares)
                    && this.whoseMove().equals(B.whoseMove())
                    && this._numMoves == B._numMoves);

        }

    }

    @Override
    public int hashCode() {
        return numPieces();
    }



    /** Set my notifier to NOTIFY. */
    public void setNotifier(Consumer<Board> notify) {
        _notifier = notify;
        announce();
    }

    /** Take any action that has been set for a change in my state. */
    private void announce() {
        _notifier.accept(this);
    }

    /** A notifier that does nothing. */
    private static final Consumer<Board> NOP = (s) -> { };

    /** A read-only version of this Board. */
    private ConstantBoard _readonlyBoard;

    /** Use _notifier.accept(B) to announce changes to this board. */
    private Consumer<Board> _notifier;
    /** Use _notifier.accept(B) to announce changes to this board. */
    private Square[] _squares;
    /** Use _notifier.accept(B) to announce changes to this board. */
    private int _size;
    /** Use _notifier.accept(B) to announce changes to this board. */
    private int _numMoves;


    /** A sequence of puzzle states.  The initial puzzle is at index 0.
     *  _history[_current] is equal to the current puzzle state.
     *  _history[_current+1] through _history[_lastHistory] are undone
     *  states that can be redone.  _lastHistory is reset to _current after
     *  each move.  _history only expands: there can be more than
     *  _lastHistory+1 elements in it at any time, with those following
     *  _lastHistory being available for re-use.  This is basically an
     *  optimization to avoid constant allocation and deallocation of
     *  arrays. */
    private Stack<Board> _history = new Stack<>();

    /** The position of the current state in _history.  This is always
     *  non-negative and <=_lastHistory.  */
    private int _current;




}
