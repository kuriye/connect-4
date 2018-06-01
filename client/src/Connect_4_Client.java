import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Connect_4_Client extends JFrame implements Connect4Constants, MouseListener {
    // Indicate whether the player has the turn
    private boolean myTurn = false;

    // Indicate the token for the player
    private char myToken = ' ';

    // Indicate the token for the other player
    private char otherToken = ' ';

    // Indicate selected row and column by the current move
    private int rowSelected;
    private int columnSelected;

    // Input and output streams from/to server
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;

    // Continue to play?
    private boolean continueToPlay = true;

    // Wait for the player to mark a cell
    private boolean waiting = true;

    private char[][] cells;

    // Host name or ip
    private String host = "localhost";

    private ArrayList<Point2D> buttons;
    private static final int MAX_CONNECT_TRIES = 50;
    private Socket socket;
    private int connectedTries;
    private JPanel content;
    private GridPanel panel;
    private boolean clicked;

    public static void main(String[] args){
        Connect_4_Client client = new Connect_4_Client();
    }

    public Connect_4_Client() {
        //gui
        this.content = new JPanel(new BorderLayout());
        this.panel = new GridPanel(7,6);
        this.content.add(panel, BorderLayout.CENTER);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setContentPane(content);
        this.setVisible(true);

        //
        char space = ' ';
        cells = new char[6][7];

        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 7; j++)
                cells[i][j] = space;

        String result = Arrays
                .stream(cells)
                .map(Arrays::toString)
                .collect(Collectors.joining(System.lineSeparator()));
        System.out.println(result);

        clicked = false;
        connectedTries = 0;
        buttons = panel.getButtons();

        // Connect to the server
        connectToServer();
        this.addMouseListener(this);
    }

    private boolean createSocket(){
        try{
            // Create a socket to connect to the server
            socket = new Socket(host, 8000);
        }catch(Exception e){
            connectedTries++;

            System.out.println("Not connected, trying to reconnect... (try: " + connectedTries + ")");

            if(connectedTries < MAX_CONNECT_TRIES)
                return createSocket();
            else
                return false;
        }
        return true;
    }

    private void connectToServer() {
        if(!createSocket()){
            System.out.println("Couldn't connect to server.");
//            System.exit(0);
            return;
        } else {
            System.out.println("Connected to server.");
        }

        try {
            // Create an input stream to receive data from the server
            toServer = new ObjectOutputStream(socket.getOutputStream());
            toServer.flush();

            // Create an output stream to send data to the server
            fromServer = new ObjectInputStream(socket.getInputStream());


        }catch(Exception e){
            e.printStackTrace();
        }

        // Control the game on a separate thread
        new Thread(() -> {
            try {
                System.out.println("okay");
                // Get notification from the server
                int player = fromServer.readInt();
                System.out.println(player);

                // Am I player 1 or 2?
                if (player == PLAYER1) {
                    myToken = 'R';
                    otherToken = 'G';
                    // Receive startup notification from the server
                    //fromServer.readInt(); // Whatever read is ignored
                    // The other player has joined
                    // It is my turn
                    myTurn = true;
                } else if (player == PLAYER2) {
                    myToken = 'G';
                    otherToken = 'R';

                }

                // Continue to play
                while (continueToPlay) {
                    if (player == PLAYER1) {
                        waitForPlayerAction(); // Wait for player 1 to move
                        sendMove(); // Send the move to the server
                        receiveInfoFromServer(); // Receive info from the server
                    } else if (player == PLAYER2) {
                        receiveInfoFromServer(); // Receive info from the server
                        waitForPlayerAction(); // Wait for player 2 to move
                        sendMove(); // Send player 2's move to the server
                    }
                    repaint();
                }
                repaint();

            } catch (Exception ex) {
                ex.printStackTrace();
//                System.out.println("Server closed.");
            }
        }).start();
    }

    private void waitForPlayerAction() throws InterruptedException {
        while (waiting) {
            Thread.sleep(100);
        }

        waiting = true;
    }

    private void sendMove() throws IOException {
        int x = rowSelected;
        int y = columnSelected;

        toServer.writeObject(new Point2D.Double(x,y));
        System.out.println("move send");
    }

    private void receiveInfoFromServer() throws IOException, ClassNotFoundException {
        // Receive game status
        int status = fromServer.readInt();

        if (status == PLAYER1_WON) {
            // Player 1 won, stop playing
            continueToPlay = false;
            if (myToken == 'R') {

            } else if (myToken == 'G') {

                receiveMove();
            }
        } else if (status == PLAYER2_WON) {
            // Player 2 won, stop playing
            continueToPlay = false;
            if (myToken == 'G') {

            } else if (myToken == 'R') {

                receiveMove();
            }
        } else if (status == DRAW) {
            // No winner, game is over
            continueToPlay = false;


            if (myToken == 'G') {
                receiveMove();
            }
        } else {
            receiveMove();

            myTurn = true; // It is my turn
        }
    }

    private void receiveMove() throws IOException, ClassNotFoundException {
        // Get the other player's move
        Point2D point = (Point2D) fromServer.readObject();
        int row = (int) point.getX();
        int column = (int) point.getY();

        for(CoinLocation coin : panel.getCoins()){
            if(coin.getRow() == row && coin.getColumn() == column){
                if(myToken == 'G')
                    coin.setColor(Color.green);
                else
                    coin.setColor(Color.red);

                coin.draw();
                return;
            }
        }

        if (myTurn) {
            myTurn = false;
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        clicked = false;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!clicked) {
            clicked = true;
            Point2D mousePoint = new Point2D.Double(e.getX(), e.getY());
            for (int i1 = 0; i1 < buttons.size(); i1++) {
                Point2D buttonPoint = buttons.get(i1);

                if (mousePoint.getX() > buttonPoint.getX() && mousePoint.getX() < buttonPoint.getX() + 70 && mousePoint.getY() > buttonPoint.getY() && mousePoint.getY() < buttonPoint.getY() + 90) {
                    if (myTurn) {
                        System.out.println(i1+ "");
                        columnSelected = i1;
                        for (int i = 5; i > 0; i--) {
                            System.out.println("i: " + i);
                            System.out.println("columnSelected: " + columnSelected);
                            if (cells[i][columnSelected] == ' ') {
                                rowSelected = i;
                                cells[i][columnSelected] = 'R';
                                System.out.println("row: " + rowSelected + "/ column:  " + columnSelected);
                                waiting = false;
                                break;
                            }

                        }
                        //myTurn = false;
                        // System.out.println("mouse point: " + mousePoint);
                    }
                }
                //System.out.println(buttonPoint);
            }
            //System.out.println("mouse point: " + mousePoint);
            //System.out.println(point2D);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void buttonPressed(int button){
        rowSelected = button-1;

        ArrayList<CoinLocation> selectedRow = new ArrayList<>();

        for(CoinLocation coin : panel.getCoins()) {
            if(coin.getRow() == rowSelected){
                selectedRow.add(coin);
            }
        }

        for(int i = selectedRow.size()-1; i > -1; i--){
            CoinLocation coin = selectedRow.get(i);
            if(!coin.isDrawnWithColor()){
                System.out.println("setDrawnWithColor(true)");
                columnSelected = i;
                coin.setDrawnWithColor(true);
                break;
            }
        }

        try {
            sendMove();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

