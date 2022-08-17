
package jump61;

import ucb.gui2.TopLevel;
import ucb.gui2.LayoutSpec;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static jump61.Side.*;

/** The GUI controller for jump61.  To require minimal change to textual
 *  interface, we adopt the strategy of converting GUI input (mouse clicks)
 *  into textual commands that are sent to the Game object through a
 *  a Writer.  The Game object need never know where its input is coming from.
 *  A Display is an Observer of Games and Boards so that it is notified when
 *  either changes.
 *  @author Xuanyi Zhang
 */
class Display extends TopLevel implements View, CommandSource, Reporter {

    /** A new window with given TITLE displaying GAME, and using COMMANDWRITER
     *  to send commands to the current game. */
    Display(String title) {
        super(title, true);
        addMenuButton("Game->New Game", this::newGame);
        addMenuButton("Game->Quit", this::quit);

        addMenuRadioButton("Option->Red Manual", null, true, this::redManual);
        addMenuRadioButton("Option->Red AI", null, false, this::redAI);

        addMenuRadioButton("Option->Blue Manual", "hehe",
                false, this::blueManual);
        addMenuRadioButton("Option->Blue AI", "hehe", true, this::blueAI);
        addMenuButton("Option->Set Seed...", this::setSeed);
        addMenuButton("Option->Board Size...", this::boardSize);


        _boardWidget = new BoardWidget(_commandQueue);
        add(_boardWidget, new LayoutSpec("y", 1, "width", 2));
        display(true);
    }

    /** Margins around label placement are multiples of this (pixels). */
    static final int UNIT_MARGIN = 5;

    /** Response to "Quit" button click. */
    void quit(String dummy) {
        System.exit(0);
    }

    /** Response to "New Game" button click. */
    void newGame(String dummy) {
        _commandQueue.offer("new");
    }
    /** Response to "New Game" button click. */
    void redManual(String dummy) {
        _commandQueue.offer("manual red");
    }
    /** Response to "New Game" button click. */
    void blueManual(String dummy) {
        _commandQueue.offer("manual blue");
    }
    /** Response to "New Game" button click. */
    void blueAI(String dummy) {
        _commandQueue.offer("auto blue");
    }
    /** Response to "New Game" button click. */
    void redAI(String dummy) {
        _commandQueue.offer("auto red");
    }

    /** Respond to "Seed" menu click. */
    private void setSeed(String dummy) {
        String response =
                getTextInput("Enter seed value", "Seed", "plain", "");
        if (response != null) {
            Matcher mat = SEED_PATN.matcher(response);
            if (mat.matches()) {
                _commandQueue.offer(String.format("SEED %s", mat.group(1)));
            } else {
                showMessage("Enter an integral seed value.", "Error", "error");
            }
        }
    }

    /** Pattern describing the 'seed' command's arguments. */
    private static final Pattern SEED_PATN =
            Pattern.compile("\\s*(-?\\d{1,18})\\s*$");
    /** Pattern describing the 'seed' command's arguments. */
    void boardSize(String dummy) {
        String response =
                getTextInput("Enter number of rows and columns (2-10)",
                        "Size", "plain", "");
        if (response != null) {
            if (!Pattern.matches("\\d+$", response)) {
                showMessage("size must be non-negative integers "
                                + "between 2 and 10.",
                        "Error", "error");
            }
        }

        int size = toInt(response);

        if (size >= 2 && size <= 10) {
            _commandQueue.offer(String.format("SIZE %d", size));
        } else {
            showMessage("Bad board configuration parameters.",
                    "Error", "error");
        }
    }

    @Override
    public void update(Board board) {
        _boardWidget.update(board);
        pack();
        _boardWidget.repaint();
        display(true);
    }



    @Override
    public String getCommand(String ignored) {
        try {
            return _commandQueue.take();
        } catch (InterruptedException excp) {
            throw new Error("unexpected interrupt");
        }
    }

    @Override
    public void announceWin(Side side) {
        showMessage(String.format("%s wins!", side.toCapitalizedString()),
                    "Game Over", "information");
    }

    @Override
    public void announceMove(int row, int col) {
    }

    @Override
    public void msg(String format, Object... args) {
        showMessage(String.format(format, args), "", "information");
    }

    @Override
    public void err(String format, Object... args) {
        showMessage(String.format(format, args), "Error", "error");
    }

    /** Time interval in msec to wait after a board update. */
    static final long BOARD_UPDATE_INTERVAL = 50;
    /** Return integer denoted by NUMERAL. */
    static int toInt(String numeral) {
        return Integer.parseInt(numeral);
    }
    /** The widget that displays the actual playing board. */
    private BoardWidget _boardWidget;
    /** Queue for commands going to the controlling Game. */
    private final ArrayBlockingQueue<String> _commandQueue =
        new ArrayBlockingQueue<>(5);
}
