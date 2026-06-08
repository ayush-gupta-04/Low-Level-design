import java.util.*;

// Observer Pattern
interface IObserver {
    void update(String msg);
}

// Sample observer implementation
class SnakeAndLadderConsoleNotifier implements IObserver {
    public void update(String msg) {
        System.out.println("[NOTIFICATION] " + msg);
    }
}

// Dice class
class Dice {
    private int faces;
    
    public Dice(int f) {
        faces = f;
    }
    
    public int roll() {
        return (int)(Math.random() * faces) + 1;
    }
}

// Base class for Snake and Ladder (both have start and end positions)
abstract class BoardEntity {
    protected int startPosition;
    protected int endPosition;
    
    public BoardEntity(int start, int end) {
        startPosition = start;
        endPosition = end;
    }
    
    public int getStart() { 
        return startPosition; 
    }

    public int getEnd() { 
        return endPosition;
    }
    
    public abstract void display();
    public abstract String name();
}

// Snake class
class Snake extends BoardEntity {
    public Snake(int start, int end) {
        super(start, end);
        if(end >= start) {
            System.out.println("Invalid snake! End must be less than start.");
        }
    }
    
    @Override
    public void display() {
        System.out.println("Snake: " + startPosition + " -> " + endPosition);
    }

    @Override
    public String name() {
        return "SNAKE";
    }
}

// Ladder class
class Ladder extends BoardEntity {
    public Ladder(int start, int end) {
        super(start, end);
        if(end <= start) {
            System.out.println("Invalid ladder! End must be greater than start.");
        }
    }
    
    @Override
    public void display() {
        System.out.println("Ladder: " + startPosition + " -> " + endPosition);
    }

    @Override
    public String name() {
        return "LADDER";
    }
}

// Board class
class Board {
    private int size;
    private List<BoardEntity> snakesAndLadders;
    private Map<Integer, BoardEntity> boardEntities;
    
    public Board(int s) {
        size = s * s;  // m*m board
        snakesAndLadders = new ArrayList<>();
        boardEntities = new HashMap<>();
    }

    // main methods
    public void addBoardEntity(BoardEntity boardEntity) {
        if(canAddEntity(boardEntity.getStart())) {
            snakesAndLadders.add(boardEntity);
            boardEntities.put(boardEntity.getStart(), boardEntity);
        }
    }
    public void setupBoard(BoardSetupStrategy strategy) {
        strategy.setupBoard(this);
    }
    public void display() {
        System.out.println("\n=== Board Configuration ===");
        System.out.println("Board Size: " + size + " cells");

        int snakeCount = 0;
        int ladderCount = 0;
        for(BoardEntity entity : snakesAndLadders) {
            if(entity.name().equals("SNAKE")) snakeCount++;
            else ladderCount++;
        }
        
        System.out.println("\nSnakes: " + snakeCount);
        for(BoardEntity entity : snakesAndLadders) {
            if(entity.name().equals("SNAKE")) {
                entity.display();
            }
        }
        
        System.out.println("\nLadders: " + ladderCount);
        for(BoardEntity entity : snakesAndLadders) {
            if(entity.name().equals("LADDER")) {
                entity.display();
            }
        }
        System.out.println("=========================");
    }



    // helpers , getters, setters
    public boolean canAddEntity(int position) {
        return !boardEntities.containsKey(position);
    }
    public BoardEntity getEntity(int position) {
        return boardEntities.get(position);
    }
    public int getBoardSize() { 
        return size;
    }
}

// Strategy Pattern for Board Setup
interface BoardSetupStrategy {
    void setupBoard(Board board);
}

// Random Strategy with difficulty levels
class RandomBoardSetupStrategy implements BoardSetupStrategy {
    // Takes the Difficulty and Randomly places snakes and ladder.
    // Not implemented right now.
    public void setupBoard(Board board){
        System.out.println("Random Board Generated!");
    }
    
}

// Custom Strategy - User provides count
class CustomCountBoardSetupStrategy implements BoardSetupStrategy {
    // we will input snakes position and ladder positions from the user
    // we will add those positions to the board.
    public void setupBoard(Board board){
        System.out.println("Custom Board Generated!");
    }
}

// Standard Board Strategy - Traditional Snake & Ladder positions
class StandardBoardSetupStrategy implements BoardSetupStrategy {
    @Override
    public void setupBoard(Board board) {
        // Only works for 10x10 board (100 cells)
        if(board.getBoardSize() != 100) {
            System.out.println("Standard setup only works for 10x10 board!");
            return;
        }
        
        // Standard snake positions (based on traditional board)
        board.addBoardEntity(new Snake(99, 54));
        board.addBoardEntity(new Snake(95, 75));
        board.addBoardEntity(new Snake(92, 88));
        board.addBoardEntity(new Snake(89, 68));
        board.addBoardEntity(new Snake(74, 53));
        board.addBoardEntity(new Snake(64, 60));
        board.addBoardEntity(new Snake(62, 19));
        board.addBoardEntity(new Snake(49, 11));
        board.addBoardEntity(new Snake(46, 25));
        board.addBoardEntity(new Snake(16, 6));
        
        // Standard ladder positions
        board.addBoardEntity(new Ladder(2, 38));
        board.addBoardEntity(new Ladder(7, 14));
        board.addBoardEntity(new Ladder(8, 31));
        board.addBoardEntity(new Ladder(15, 26));
        board.addBoardEntity(new Ladder(21, 42));
        board.addBoardEntity(new Ladder(28, 84));
        board.addBoardEntity(new Ladder(36, 44));
        board.addBoardEntity(new Ladder(51, 67));
        board.addBoardEntity(new Ladder(71, 91));
        board.addBoardEntity(new Ladder(78, 98));
        board.addBoardEntity(new Ladder(87, 94));
    }
}

// Player class
class SnakeAndLadderPlayer {
    private int playerId;
    private String name;
    private int position;
    private int score;
    
    public SnakeAndLadderPlayer(int playerId, String n) {
        this.playerId = playerId;
        name = n;
        position = 0;
        score = 0;
    }
    
    // Getters and Setters
    public String getName() { 
        return name;
    }
    public int getPosition() { 
        return position; 
    }
    public void setPosition(int pos) { 
        position = pos; 
    }
    public int getScore() { 
        return score;
    }
    public void incrementScore() { 
        score++; 
    }
}

// Strategy Pattern for game rules
interface SnakeAndLadderRules {
    boolean isValidMove(int currentPos, int diceValue, int boardSize);
    int calculateNewPosition(int currentPos, int diceValue, Board board);
    boolean checkWinCondition(int position, int boardSize);
}

// Standard rules
class StandardSnakeAndLadderRules implements SnakeAndLadderRules {
    @Override
    public boolean isValidMove(int currentPos, int diceValue, int boardSize) {
        return (currentPos + diceValue) <= boardSize;
    }
    
    @Override
    public int calculateNewPosition(int currentPos, int diceValue, Board board) {
        int newPos = currentPos + diceValue;
        BoardEntity entity = board.getEntity(newPos);
        
        if(entity != null) {
            return entity.getEnd();
        }
        return newPos;
    }
    
    @Override
    public boolean checkWinCondition(int position, int boardSize) {
        return position == boardSize;
    }
}

// Game class
class SnakeAndLadderGame {
    private Board board;
    private Dice dice;
    private Deque<SnakeAndLadderPlayer> players;
    private SnakeAndLadderRules rules;
    private List<IObserver> observers;
    private boolean gameOver;
    
    public SnakeAndLadderGame(Board b, Dice d) {
        board = b;
        dice = d;
        players = new ArrayDeque<>();
        rules = new StandardSnakeAndLadderRules();
        observers = new ArrayList<>();
        gameOver = false;
    }
    
    public void addPlayer(SnakeAndLadderPlayer player) {
        players.addLast(player);
    }
    
    public void addObserver(IObserver observer) {
        observers.add(observer);
    }

    public void notify(String msg) {
        for(IObserver observer : observers) {
            observer.update(msg);
        }
    }
    
    public void displayPlayerPositions() {
        System.out.println("\n=== Current Positions ===");
        for(SnakeAndLadderPlayer player : players) {
            System.out.println(player.getName() + ": " + player.getPosition());
        }
        System.out.println("=======================");
    }
    
    public void play() {
        if(players.size() < 2) {
            System.out.println("Need at least 2 players!");
            return;
        }
        
        notify("Game started");

        board.display();
        
        Scanner scanner = new Scanner(System.in);
        
        while(!gameOver) {
            SnakeAndLadderPlayer currentPlayer = players.peekFirst();
            
            System.out.println("\n" + currentPlayer.getName() + "'s turn. Press Enter to roll dice...");
            scanner.nextLine();
            
            int diceValue = dice.roll();
            System.out.println("Rolled: " + diceValue);
            
            int currentPos = currentPlayer.getPosition();
            
            if(rules.isValidMove(currentPos, diceValue, board.getBoardSize())) {
                int intermediatePos = currentPos + diceValue;
                int newPos = rules.calculateNewPosition(currentPos, diceValue, board);
                
                currentPlayer.setPosition(newPos);
                
                // Check if player encountered snake or ladder
                BoardEntity entity = board.getEntity(intermediatePos);
                if(entity != null) {
                    boolean isSnake = entity.name().equals("SNAKE");
                    if(isSnake) {
                        System.out.println("Oh no! Snake at " + intermediatePos + "! Going down to " + newPos);
                        notify(currentPlayer.getName() + " encountered snake at " + intermediatePos + " now going down to " + newPos);
                    }
                    else {
                        System.out.println("Great! Ladder at " + intermediatePos + "! Going up to " + newPos);
                        notify(currentPlayer.getName() + " encountered ladder at " + intermediatePos + " now going up to " + newPos);
                    }
                }
                
                notify(currentPlayer.getName() + " played. New Position : " + newPos);
                displayPlayerPositions();
                
                if(rules.checkWinCondition(newPos, board.getBoardSize())) {
                    System.out.println("\n" + currentPlayer.getName() + " wins!");
                    currentPlayer.incrementScore();

                    notify("Game Ended. Winner is : " + currentPlayer.getName());
                    gameOver = true;
                }
                else {
                    // Move player to back of queue
                    players.removeFirst();
                    players.addLast(currentPlayer);
                }
            }
            else {
                System.out.println("Need exact roll to reach " + board.getBoardSize() + "!");
                // Move player to back of queue
                players.removeFirst();
                players.addLast(currentPlayer);
            }
        }

        scanner.close();
    }
}

// Factory Pattern
class SnakeAndLadderGameFactory {
    public static SnakeAndLadderGame createStandardGame() {
        Board board = new Board(10);  // Standard 10x10 board
        BoardSetupStrategy strategy = new StandardBoardSetupStrategy();
        board.setupBoard(strategy);
        
        Dice dice = new Dice(6);  // Standard 6-faced dice
        
        return new SnakeAndLadderGame(board, dice);
    }
    
    // Extra Methods : 

    // 1. createRandomGame
    // creates the board.
    // setup the board with the randomSetupStrategy
    // returns the Game Object.

    // 2. createCustomGame
    // creates the board.
    // setup the board with the customSetupStrategy
    // returns the Game Object.
}

// Main class for Snake and Ladder
public class SnakeAndLadder {
    public static void main(String[] args) {
        System.out.println("=== SNAKE AND LADDER GAME ===");
        
        SnakeAndLadderGame game = null;
        
        System.out.println("Choose game setup:");
        System.out.println("1. Standard Game (10x10 board with traditional positions)");
        System.out.println("2. Random Game with Difficulty");
        System.out.println("3. Custom Game");
        
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        
        if(choice == 1) {
            // Standard game
            game = SnakeAndLadderGameFactory.createStandardGame();
            
        }
        else if(choice == 2) {
            // Random game with difficulty
            // we input the difficulty and make a game with randomGameSetup strategy
        } 
        else if(choice == 3) {
            // Custom game
            // we will make a custom game here with customGameSetup Strategy
        }
        
        if(game == null) {
            System.out.println("Invalid choice!");
            scanner.close();
            return;
        }
        
        // Add observer
        IObserver notifier = new SnakeAndLadderConsoleNotifier();
        game.addObserver(notifier);
        
        // Create players
        System.out.print("Enter number of players: ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        for(int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for player " + (i+1) + ": ");
            String name = scanner.nextLine();
            SnakeAndLadderPlayer player = new SnakeAndLadderPlayer(i+1, name);
            game.addPlayer(player);
        }
        
        // Play the game
        game.play();
        
        scanner.close();
    }
}
