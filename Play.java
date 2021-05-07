import java.util.Objects;

/**
 * This is the main file for Battleship.
 * Created using IntelliJ EDU following a course by JetBrains' HyperSkill.
 *
 * Enjoy!
 *
 * @since 4/24/2021
 * @author Mateo Rodriguez
 * @implNote menu()
 * @version 5.2
 */

public class Play {
    private boolean crashed = false;
    private final static Intro intro = new Intro();
    private final static Play m = new Play();
    private final static java.util.Scanner scanner = new java.util.Scanner(System.in);
    private final Workshop ws = new Workshop();
    Player p1 = new Player(10);
    Player p2 = new Player(10);

    public static void main(String[] args) {
        intro.menu();
    }

    public void play() throws IllegalStateException {
        try {
            intro.printLogo(true);
            System.out.println("                   The game has started!\n           Take turns and remember: No Peeking!\n");
            for (int i = 0; i < 59; i++) {System.out.print('-');}

            System.out.println("\n\nPlayer 1, place your ships on the game field!");
            m.preGame(p1);
            System.out.println("Player 2, place your ships on the game field!");
            m.preGame(p2);

            for (int i = 0; Workshop.isGameActive(p1) && Workshop.isGameActive(p2); i++) {
                (i % 2 == 0 ? p2 : p1).print(true);
                System.out.print("---------------------");
                (i % 2 == 0 ? p1 : p2).print();
                inGame(i % 2 == 0 ? p2 : p1);
                if (!(Workshop.isGameActive(p1) && Workshop.isGameActive(p2))) {
                    return;
                }
                ws.pass();
            }
        } finally {
            if (m.crashed) {
                System.out.printf("%nGAME CRASH! -%nPlease report this to be fixed :)%n------------------------%n%n");
            }
            System.out.flush();
            scanner.close();
        }
    }

    private void inGame(Player player) {
        if (this.crashed) {
            return;
        }

        System.out.println("\nTake a shot!");
        String input;
        while (Workshop.isGameActive(player)) {
            try {
                input = scanner.next();
            } catch (java.util.NoSuchElementException exception) {
                System.out.printf("Something went wrong while reading your input! ID: %s%n", exception.getLocalizedMessage());
                this.crashed = true;
                return;
            }

            try {
                if (ws.isValidShot(player, ws.inputToIndex(input))) {
                    ws.shoot(player, ws.inputToIndex(input));
                    scanner.nextLine();
                    return;
                } else {
                    System.out.println("Error! You entered the wrong coordinates! Try again:");
                }
            } catch (IllegalArgumentException | IllegalStateException exception) {
                if (exception.getMessage().contains("Bad Input")) {
                    System.out.println("Error! You entered the wrong coordinates! Try again:");
                }
            }
        }
    }

    private void preGame(Player player) {
        if (this.crashed) {
            return;
        }

        player.print();
        System.out.println();
        Ships boat = Ships.AIRCRAFT_CARRIER;
        String input1;
        String input2;
        int i;
        int c;

        for (c = Ships.values().length; c != 0;) {
            boat = boat.getShipFromPos(c);
            for (i = 0; i < 2; i++) {
                System.out.printf("%n%d %s Left to Place!%nEnter the coordinates of the %s (%d cells):%n", c, c != 1 ? "Ships" : "Ship", boat.getName(), boat.getSize());
                try {
                    input1 = scanner.next();
                    input2 = scanner.next();
                } catch (java.util.NoSuchElementException exception) {
                    System.out.printf("Something went wrong while reading your input! ID: %s%n", exception.getLocalizedMessage());
                    System.out.flush();
                    crashed = true;
                    return;
                }

                try {
                    if (ws.isValidPlacement(true, player, boat, java.util.Objects.requireNonNull(input1), java.util.Objects.requireNonNull(input2))) {
                        ws.placeShip(player, input1, input2, boat);
                        player.print();
                        c--;
                        break;
                    }
                } catch (IllegalStateException | IllegalArgumentException e) {
                    if (e.getMessage().contains("length")) {
                        System.out.printf("Error! Wrong length of the %s! Try again:%n", boat.getName());
                    } else if (e.getMessage().contains("diagonal")) {
                        System.out.println("Error! Wrong ship location! Try again:");
                    } else if (e.getMessage().contains("full")) {
                        System.out.println("Error! Overlapping ships.");
                    } else if (e.getMessage().contains("touch")) {
                        System.out.println("Error! You can't have the ships touching each other!");
                    } else if (e.getMessage().contains("Bad Input")) {
                        System.out.println("Error! You entered the wrong coordinates! Try again:");
                    } else {
                        System.out.printf("Error! %s%n", e.getMessage());
                    }
                } catch (Exception e) {
                    System.out.printf("Something went wrong: %s", e.getMessage());
                } finally {
                    i = 0;
                }
            }
        }
        System.out.print("\nYou placed the last ship!");
        ws.pass(true);
    }
}

class Workshop {
    char playerNum = '1';
    java.util.Scanner scanner = new java.util.Scanner(System.in);

    @SuppressWarnings(value = "unused parameter")
    final void pass() {
        System.out.println("\nPress ENTER and pass the move to another player!");
        String s = scanner.nextLine();
        for (int i = 0; i < 250; i++) {
            System.out.println();
        }
        this.playerNum = this.playerNum == '1' ? '2' : '1';
        System.out.printf("Player %c, it's your turn!%n%n", this.playerNum);
    }

    @SuppressWarnings(value = {"unused parameter", "SameParameterValue"})
    final void pass(boolean preGame) {
        if (preGame) {
            System.out.println("\nPress ENTER and pass the move to another player!");
            String s = scanner.nextLine();
            for (int i = 0; i < 250; i++) {
                System.out.println();
            }
            this.playerNum = this.playerNum == '1' ? '2' : '1';
        } else {
            pass();
        }
    }

    static boolean isGameActive(Player player) {
        return player.getBoatsMissing() < 5;
    }

    final void shoot(Player player, int[] index) throws IllegalArgumentException {
        char[][] c;
        char[][] s;
        if (isValidShot(player, index)) {
            c = player.getGrid();
            s = player.getGrid(true);

            s[index[0]][index[1]] = c[index[0]][index[1]] == 'O' ? 'X' : 'M';
            c[index[0]][index[1]] = c[index[0]][index[1]] == 'O' ? 'X' : 'M';
            player.setGrid(c);
            player.setSecretGrid(s);

            for (Ships a : Ships.values()) {
                if (isSunk(player, a)) {
                    if (coordOnBoat(player, a, index)) {
                        player.setBoatsMissing(player.getBoatsMissing() + 1);
                        System.out.println(isGameActive(player) ? "\nYou sank a ship!" :
                                "\nYou sank the last ship. *You won!* Congratulations!");
                        return;
                    }}}
            System.out.println(c[index[0]][index[1]] == 'X' ? "\nYou hit a ship!" : "\nYou missed.");
        }
    }

    private boolean coordOnBoat(Player player, Ships type, int[] coords) {
        int x1 = inputToIndex(player.getCoords(type)[0])[0];
        int y1 = inputToIndex(player.getCoords(type)[0])[1];

        int x2 = inputToIndex(player.getCoords(type)[1])[0];
        int y2 = inputToIndex(player.getCoords(type)[1])[1];

        for (int i = Math.min(x1, x2); i <= Math.max(x1, x2); i++) {
            for (int b = Math.min(y1, y2); b <= Math.max(y1, y2); b++) {
                if (coords[0] == i && coords[1] == b) {
                    return true;
                }}}
        return false;
    }

    private boolean isSunk(Player player, Ships type) {
        boolean tempBoolean = true;
        char[][] grid = player.getGrid();

        int x1 = inputToIndex(player.getCoords(type)[0])[0];
        int y1 = inputToIndex(player.getCoords(type)[0])[1];

        int x2 = inputToIndex(player.getCoords(type)[1])[0];
        int y2 = inputToIndex(player.getCoords(type)[1])[1];

        for (int i = Math.min(x1, x2); i <= Math.max(x1, x2); i++) {
            for (int b = Math.min(y1, y2); b <= Math.max(y1, y2); b++) {
                tempBoolean = grid[i][b] == 'X' && tempBoolean;
            }
        }
        return tempBoolean;
    }

    void placeShip(Player player, String input1, String input2, Ships type) {
        int[] index1 = inputToIndex(input1);
        int[] index2 = inputToIndex(input2);

        char[][] c = player.getGrid();
        for (int i = Math.min(index1[0], index2[0]); i <= Math.max(index1[0], index2[0]); i++) {
            for (int b = Math.min(index1[1], index2[1]); b <= Math.max(index1[1], index2[1]); b++) {
                c[i][b] = 'O';
            }
        }
        player.setCoords(type, input1, input2);
        player.setGrid(c);
    }

    protected int[] inputToIndex(String input) throws IllegalArgumentException {
        java.util.ArrayList<Integer> a = new java.util.ArrayList<>();

        try {
            if (Integer.parseInt(input.substring(1)) > 10) {
                throw new IllegalArgumentException("The string must be <= 10");
            } else if (input.substring(1).matches("\\d++")) {
                a.add((int) (Character.toString(input.charAt(0)).toUpperCase()).charAt(0) - 65);
                a.add(Integer.parseInt(input.substring(1)) - 1);

                if (a.get(0) >= 0 && a.get(0) < 10 && a.get(1) >= 0 && a.get(1) < 10) {
                    return new int[]{a.get(0), a.get(1)};
                }
            }
            throw new IllegalArgumentException("Bad Input!");
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad Input!");
        }
    }

    boolean isValidShot(Player player, int[] index) {
        char[][] c = player.getGrid();
        if (java.util.Objects.nonNull(index)) {
            if (index[0] < 10 && index[1] < 10) {
                return c[index[0]][index[1]] == '~' || c[index[0]][index[1]] == 'O';
            }
        }
        return false;
    }

    boolean isValidPlacement(Player player, Ships type, String input1, String input2) {
        int[] index1 = inputToIndex(input1);
        int[] index2 = inputToIndex(input2);
        int size = type.getSize() - 1;
        char[][] c = player.getGrid();

        if (Math.abs(index1[0] - index2[0]) != size && Math.abs(index1[1] - index2[1]) != size) {
            throw new IllegalStateException("Wrong length.");
        }

        if (index1[1] == index2[1] || index1[0] == index2[0]) {
            for (int i = Math.min(index1[0], index2[0]); i <= Math.max(index1[0], index2[0]); i++) {
                for (int b = Math.min(index1[1], index2[1]); b <= Math.max(index1[1], index2[1]); b++) {
                    if (c[i][b] != '~') {
                        throw new IllegalStateException(String.format("The slot %d, %d is full", i, b));
                    }}}
            return true;
        } else {
            throw new IllegalStateException("You can't place this ship on a diagonal");
        }
    }

    @SuppressWarnings(value = "SameParameterValue")
    boolean isValidPlacement(boolean HyperSkill, Player player, Ships type, String input1, String input2) {
        if (HyperSkill) {
            int[] index1 = Objects.requireNonNull(inputToIndex(input1));
            int[] index2 = Objects.requireNonNull(inputToIndex(input2));

            int size = type.getSize() - 1;
            char[][] c = player.getGrid();

            if (Math.abs(index1[0] - index2[0]) != size && Math.abs(index1[1] - index2[1]) != size) {
                throw new IllegalStateException("Wrong length.");
            }

            for (int i = Math.min(index1[0], index2[0]); i <= Math.max(index1[0], index2[0]); i++) {
                for (int b = Math.min(index1[1], index2[1]); b <= Math.max(index1[1], index2[1]); b++) {
                    try {
                        if (c[i + 1][b] == 'O') {
                            throw new IllegalStateException("Ships cannot touch!");
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}

                    try {
                        if (c[i - 1][b] == 'O') {
                            throw new IllegalStateException("Ships cannot touch!");
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}

                    try {
                        if (c[i][b + 1] == 'O') {
                            throw new IllegalStateException("Ships cannot touch!");
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}

                    try {
                        if (c[i][b - 1] == 'O') {
                            throw new IllegalStateException("Ships cannot touch!");
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}
                }
            }

            if (index1[1] == index2[1] || index1[0] == index2[0]) {
                for (int i = Math.min(index1[0], index2[0]); i <= Math.max(index1[0], index2[0]); i++) {
                    for (int b = Math.min(index1[1], index2[1]); b <= Math.max(index1[1], index2[1]); b++) {
                        if (c[i][b] != '~') {
                            throw new IllegalStateException(String.format("The slot %d, %d is full", i, b));
                        }}}
                return true;
            } else {
                throw new IllegalStateException("You can't place this ship on a diagonal");
            }
        } else {
            return isValidPlacement(player, type, input1, input2);
        }
    }
}

final class Player {
    private char[][] grid;
    private char[][] secretGrid;
    private final int dimension;
    private final boolean initialized;
    private int boatsMissing = 0;

    private final String[] ship5 = new String[2];
    private final String[] ship4 = new String[2];
    private final String[] ship3 = new String[2];
    private final String[] ship2 = new String[2];
    private final String[] ship1 = new String[2];

    Player(int dimension) throws IllegalArgumentException {
        if (!(dimension <= 10) || !(dimension > 0)) {
            throw new IllegalArgumentException("The dimension parameter must fit into the range of 1-10 (For best results)");
        }
        Grid g = new Grid(dimension);
        Grid s = new Grid(dimension);
        this.dimension = dimension;
        this.grid = g.getGrid();
        this.secretGrid = s.getGrid();
        this.initialized = true;
    }

    final int getBoatsMissing() {
        return boatsMissing;
    }

    final void setBoatsMissing(int boatsMissing) {
        this.boatsMissing = boatsMissing;
    }

    final String[] getCoords(Ships type) throws NullPointerException {
        return type.equals(Ships.AIRCRAFT_CARRIER) ? Objects.requireNonNull(ship5) :
                type.equals(Ships.BATTLESHIP) ? Objects.requireNonNull(ship4) :
                        type.equals(Ships.SUBMARINE) ? Objects.requireNonNull(ship3) :
                                type.equals(Ships.CRUISER) ? Objects.requireNonNull(ship2) :
                                        type.equals(Ships.DESTROYER) ? Objects.requireNonNull(ship1) :
                                                new String[] {"z10", "z10"};
    }

    void setCoords(Ships type, String x, String y) throws IllegalArgumentException {
        switch (type) {
            case AIRCRAFT_CARRIER:
                ship5[0] = x;
                ship5[1] = y;
                break;
            case BATTLESHIP:
                ship4[0] = x;
                ship4[1] = y;
                break;
            case SUBMARINE:
                ship3[0] = x;
                ship3[1] = y;
                break;
            case CRUISER:
                ship2[0] = x;
                ship2[1] = y;
                break;
            case DESTROYER:
                ship1[0] = x;
                ship1[1] = y;
                break;
            default:
                throw new IllegalArgumentException(String.format("There is no ship with the name: '%s'", type));
        }
    }

    void setSecretGrid(char[][] secretGrid) {
        this.secretGrid = secretGrid;
    }

    char[][] getGrid() {
        return this.grid;
    }

    @SuppressWarnings("SameParameterValue")
    char[][] getGrid(boolean secret) {
        if (secret) {
            return this.secretGrid;
        } else {
            return this.grid;
        }
    }

    void setGrid(char[][] grid) {
        this.grid = grid;
    }

    void print() {
        if (!initialized) {
            throw new ExceptionInInitializerError("The grid hasn't been initialized yet!");
        }
        int c;
        System.out.print("\n  ");
        for (int i = 1; i <= this.dimension; i++) {
            System.out.printf("%d ", i);
        }
        System.out.println();
        for (int i = 0; i < this.dimension; i++) {
            c = i + 97;
            System.out.printf("%s ", Character.toString((char) c).toUpperCase());
            for (int b = 0; b < this.dimension; b++) {
                System.out.printf("%c ", this.grid[i][b]);
            }
            System.out.println();
        }
    }

    @SuppressWarnings("SameParameterValue")
    void print(boolean secret) {
        if (!initialized) {
            throw new ExceptionInInitializerError("The grid hasn't been initialized yet!");
        }
        if (secret) {
            int c;
            System.out.print("\n  ");
            for (int i = 1; i <= this.dimension; i++) {
                System.out.printf("%d ", i);
            }
            System.out.println();
            for (int i = 0; i < this.dimension; i++) {
                c = i + 97;
                System.out.printf("%s ", Character.toString((char) c).toUpperCase());
                for (int b = 0; b < this.dimension; b++) {
                    System.out.printf("%c ", this.secretGrid[i][b]);
                }
                System.out.println();
            }
        }
    }
}

final class Grid {
    private final char[][] baseGrid;

    Grid(int dimension) throws IllegalArgumentException {
        if (!(dimension <= 10) || !(dimension > 0)) {
            throw new IllegalArgumentException("The dimension parameter must fit into the range of 1-10 (For best results)");
        }
        this.baseGrid = new char[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            for (int b = 0; b < dimension; b++) {
                this.baseGrid[i][b] = '~';
            }
        }
    }

    final char[][] getGrid() {
        return this.baseGrid;
    }
}

enum Ships {
    AIRCRAFT_CARRIER("Aircraft Carrier", 5, 5),
    BATTLESHIP("Battleship", 4, 4),
    SUBMARINE("Submarine", 3, 3),
    CRUISER("Cruiser", 3, 2),
    DESTROYER("Destroyer", 2, 1);

    private final String name;
    private final int size;
    private final int rank;

    Ships(String name, int size, int rank) {
        this.name = name;
        this.size = size;
        this.rank = rank;
    }

    String getName() {
        return this.name;
    }

    Ships getShipFromPos(int pos) {
        for (Ships a : Ships.values()) {
            if (a.rank == pos) {
                return a;
            }
        }
        throw new IllegalStateException("Could not find a ship with index " + pos);
    }

    int getSize() {
        return this.size;
    }
}

final class Intro {
    private static final java.util.Scanner scanner = new java.util.Scanner(System.in);
    private static final Play m = new Play();

    private enum Logo {
        L1("  ______         _    _    _             _      _"),
        L2("  | ___ \\       | |  | |  | |           | |    (_)"),
        L3("  | |_/ /  __ _ | |_ | |_ | |  ___  ___ | |__   _  _ __"),
        L4("  | ___ \\ / _` || __|| __|| | / _ \\/ __|| '_ \\ | || '_ \\"),
        L5("  | |_/ /| (_| || |_ | |_ | ||  __/\\__ \\| | | || || |_) |"),
        L6("  \\____/  \\__,_| \\__| \\__||_| \\___||___/|_| |_||_|| .__/ "),
        L7("                                                  | |");

        String line;

        Logo(String line) {
            this.line = line;
        }
    }

    void printLogo() {
        for (Logo logo : Logo.values()) {
            System.out.println(logo.line);
        }
        System.out.println("                           MENU                   |_|");
    }

    @SuppressWarnings({"unused parameter", "SameParameterValue"})
    void printLogo(boolean regular) {
        if (regular) {
            for (Logo logo : Logo.values()) {
                System.out.println(logo.line);
            }
            System.out.println("                                                  |_|");
        } else {
            printLogo();
        }
    }

    protected void menu() {
        String line = "-----------------------------------------------------------";
        System.out.printf("%n%s%n%n", line);

        printLogo();

        System.out.println("             Type 'start' -> start the game" +
                "\n             Type 'help' -> view a tutorial" +
                "\n             Type 'back' -> back to main menu");
        System.out.println("                                                    "); //the length of the title
        checkInput(prompt());
    }

    private void menuClear() {
        for (int i = 0; i < 1050; i++) {
            System.out.println();
        }
    }

    @SuppressWarnings("unused")
    private void checkInput(String input) {
        if (input.toLowerCase().contains("start")) {
            menuClear();
            menuClear();
            scanner.nextLine();
            m.play();
        } else if (input.toLowerCase().contains("help")) {
            print();
            scanner.nextLine();
            System.out.println("Type anything to return to the menu!");
            String s = scanner.nextLine();
            menuClear();
            menu();
            checkInput(prompt());
        } else if (input.toLowerCase().contains("back")) {
            menuClear();
            System.out.println("Sorry, this hasn't been added yet!");
            menu();
            checkInput(prompt());
        } else {
            menuClear();
            System.out.printf("  Sorry, I don't know what to do with '%s'. Try again!%n%n%n", input);
            menu();
            checkInput(prompt());
        }
    }

    private String prompt() {
        System.out.println("                Type in your selection!\n");
        System.out.println("-----------------------------------------------------------");
        System.out.println("\n...");
        return scanner.next();
    }

    protected void print() {
        menuClear();
        System.out.println("Reading from the tutorial:\n");
        String border = "|++-------------------------------++|";
        System.out.printf("%s%n|                                   |%n", border);
        System.out.println("|            BATTLESHIP             |");
        System.out.println("| Are you ready to play Battleship? |");
        System.out.println("| This is my take on the classic    |");
        System.out.println("| board game.                       |");
        System.out.println("|                                   |");
        System.out.println("| Below is everything you need to   |");
        System.out.println("| know!                             |");
        System.out.println("|                                   |");
        System.out.println(border);
        System.out.println("|                                   |");
        System.out.println("|         Grid Information          |");
        System.out.println("|  Use coordinates to mark the      |");
        System.out.println("|  coordinates of where you'd like  |");
        System.out.println("|  to perform an action.            |");
        System.out.println("|                                   |");
        System.out.println("|  Every '*' is a valid placement   |");
        System.out.println("|                                   |");
        System.out.println("|    1  2  3  4  5  6  7  8  9  10  |");
        System.out.println("|  A *  *  *  *  *  *  *  *  *  *   |");
        System.out.println("|  B *  *  *  *  *  *  *  *  *  *   |");
        System.out.println("|  C *  *  *  *  *  *  *  *  *  *   |");
        System.out.println("|  D *  *  *  *  *  *  *  *  *  *   |");
        System.out.println("|  E *  *  *  *  *  *  *  *  *  *   |");
        System.out.println("|  F *  *  *  *  *  *  *  *  *  *   |");
        System.out.println("|  G *  *  *  *  *  *  *  *  *  *   |");
        System.out.println("|  H *  *  *  *  *  *  *  *  *  *   |");
        System.out.println("|  I *  *  *  *  *  *  *  *  *  *   |");
        System.out.println("|  J *  *  *  *  *  *  *  *  *  *   |");
        System.out.println("|                                   |");
        System.out.println("|  Example formatting -             |");
        System.out.println("|  > H1 H5                          |");
        System.out.println("|  > a2                             |");
        System.out.println("|                                   |");
        System.out.println(border);
        System.out.println("|                                   |");
        System.out.println("|         How to play               |");
        System.out.println("|  Start by placing your ships on   |");
        System.out.println("|  the grid (2 Coordinates: front   |");
        System.out.println("|  and back of the ship)            |");
        System.out.println("|                                   |");
        System.out.println("|  When prompted to pass the move   |");
        System.out.println("|  to another player, press ENTER.  |");
        System.out.println("|                                   |");
        System.out.println("|  To shoot, type the coordinate    |");
        System.out.println("|  you wish to strike.              |");
        System.out.println("|                                   |");
        System.out.println("|  The game ends once a player has  |");
        System.out.println("|  lost all of their ships!         |");
        System.out.println("|                                   |");
        System.out.println("|  Good Luck!                       |");
        System.out.println("|                                   |");
        System.out.printf("%s%n%n", border);
    }
}