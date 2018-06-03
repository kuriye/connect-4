import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class Connect_4_Client extends JFrame implements Connect4Constants, MouseListener {

    private boolean myTurn = false;
    private char myToken;
    private char otherToken;
    private int rowSelected;
    private int columnSelected;
    private ObjectInputStream fromServer;
    private ObjectOutputStream toServer;
    private boolean continueToPlay = true;
    private boolean waiting = true;
    private char[][] cells;
    private ArrayList<Point2D> buttons;
    private static final int MAX_CONNECT_TRIES = 50;
    private Socket socket;
    private int connectedTries;
    private GridPanel panel;
    private boolean clicked;
    private boolean hasWon = false;

    public static void main(String[] args){
        new Connect_4_Client();
    }

    public Connect_4_Client() {
        //gui
        JPanel content = new JPanel(new BorderLayout());
        content.add(this.panel = new GridPanel(7,6), BorderLayout.CENTER);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setContentPane(content);
        this.setMinimumSize(new Dimension(500,500));
        this.setVisible(true);

        //tokens
        char space = ' ';
        this.myToken = space;
        this.otherToken = space;

        //cells
        cells = new char[6][7];

        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 7; j++)
                cells[i][j] = space;

        //prints board
        printBoard();

        //other initialisations
        this.clicked = false;
        this.connectedTries = 0;
        this.buttons = panel.getButtons();

        // Connect to the server
        connectToServer();
        this.addMouseListener(this);
    }

    private boolean createSocket(){
        try{
            // Create a socket to connect to the server
            String host = "localhost";
            socket = new Socket(host, 8000);
        }catch(Exception e){
            connectedTries++;

            System.out.println("Not connected, trying to reconnect... (try: " + connectedTries + ")");
            this.panel.setText("Not connected, trying to reconnect... (try: " + connectedTries + ")");
            repaint();

            if(connectedTries < MAX_CONNECT_TRIES)
                return createSocket();
            else
                return false;
        }
        return true;
    }

    private void printBoard(){
        String result = Arrays
                .stream(cells)
                .map(Arrays::toString)
                .collect(Collectors.joining(System.lineSeparator()));
        System.out.println(result);
    }

    private void connectToServer() {
        if(!createSocket()){
            System.out.println("Couldn't connect to server.");
            this.panel.setText("Couldn't connect to server.");
//            System.exit(0);
            return;
        } else {
            System.out.println("Connected to server.");
            this.panel.setText("Connected to server.");
            repaint();
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
                repaint();
                System.out.println("In Thread in connectToServer()");

                // Get notification from the server
                int player = fromServer.readInt();
                this.panel.setText("You are player: " + player);
                System.out.println("Player: " + player);

                // Am I player 1 or 2?
                if (player == PLAYER1) {
                    myToken = 'R';
                    otherToken = 'G';
                    setTitle("Player 1 (" + myToken + ")");
                    this.panel.setText("Your turn");
                    myTurn = true;
                } else if (player == PLAYER2) {
                    myToken = 'G';
                    otherToken = 'R';
                    setTitle("Player 2 (" + myToken + ")");
                    this.panel.setText("Waiting for player 1 to move");
                    myTurn = false;
                }

                // Continue to play
                while (continueToPlay) {
                    if (player == PLAYER1) {
                        waitForPlayerAction();
                        sendMove();
                        repaint();
                        receiveInfoFromServer(); //
                    } else if (player == PLAYER2) {
                        receiveInfoFromServer();
                        waitForPlayerAction();
                        sendMove();
                        repaint();
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
//                System.out.println("Server closed.");
            }
        }).start();
    }

    private void waitForPlayerAction() throws InterruptedException {
        System.out.println("waitForPlayerAction: waiting");

        while (waiting) {
            Thread.sleep(100);
        }

        System.out.println("waitForPlayerAction: done");

        waiting = true;
    }

    private void sendMove() throws IOException {
        int x = rowSelected;
        int y = columnSelected;

        myTurn = false;

        toServer.writeObject(new Point2D.Double(x,y));
        System.out.println("Send move to server");
        printBoard();
    }

    private void receiveInfoFromServer() throws IOException, ClassNotFoundException {
        // Receive game status
        int status = fromServer.readInt();

        if (status == PLAYER1_WON) {
            // Player 1 won, stop playing
            continueToPlay = false;
            if (myToken == 'R') {
                this.panel.setText("YOU WON!!!");
                hasWon = true;
            } else if (myToken == 'G') {
                this.panel.setText("you lost :(");
                receiveMove();
            }
        } else if (status == PLAYER2_WON) {
            // Player 2 won, stop playing
            continueToPlay = false;
            if (myToken == 'G') {
                this.panel.setText("YOU WON!!!");
                hasWon = true;
            } else if (myToken == 'R') {
                this.panel.setText("you lost :(");
                receiveMove();
            }
        } else if (status == DRAW) {
            // No winner, game is over
            this.panel.setText("Draw!");
            repaint();
            continueToPlay = false;

            if (myToken == 'G') {
                receiveMove();
            }
        } else {
            receiveMove();
            this.panel.setText("Your turn");
            myTurn = true;
        }
        repaint();
    }

    private void receiveMove() throws IOException, ClassNotFoundException {
        Point2D point = (Point2D) fromServer.readObject();
        int row = (int) point.getX();
        int column = (int) point.getY();
        
        for(CoinLocation coin : panel.getCoins()){
            if(coin.getRow() == row && coin.getColumn() == column){

                if(otherToken == 'R')
                    coin.setColor(Color.red);
                else
                    coin.setColor(Color.green);

                coin.setDrawnWithColor(true);
                coin.draw();
                repaint();

                cells[column][row] = otherToken;
                return;
            }
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
        if(!clicked && myTurn) {
            clicked = true;
            Point2D mousePoint = new Point2D.Double(e.getX(), e.getY());
            for (int buttonIndex = 0; buttonIndex < buttons.size(); buttonIndex++) {
                Point2D buttonPoint = buttons.get(buttonIndex);

                if (mousePoint.getX() > buttonPoint.getX() &&
                        mousePoint.getX() < buttonPoint.getX() + 70 &&
                        mousePoint.getY() > buttonPoint.getY() &&
                        mousePoint.getY() < buttonPoint.getY() + 90){
                    buttonPreassed(buttonIndex);
                    this.panel.setText("Wait for opponent");
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void buttonPreassed(int button){
        System.out.println("buttonPressed");

        rowSelected = button;

        ArrayList<CoinLocation> selectedRow = new ArrayList<>();

        for(CoinLocation coin : panel.getCoins()) {
            if(coin.getRow() == rowSelected){
                selectedRow.add(coin);
            }
        }

        //sorteren selectedRow list
        Collections.sort(selectedRow);

        for(int i = selectedRow.size() - 1; i > -1; i--){
            CoinLocation coin = selectedRow.get(i);
            if(!coin.isDrawnWithColor()){
                columnSelected = i;

                coin.draw();
                coin.setDrawnWithColor(true);

                cells[columnSelected][rowSelected] = myToken;

                if(myToken == 'R')
                    coin.setColor(Color.red);
                else
                    coin.setColor(Color.green);

                repaint();

                waiting = false;
                break;
            }
        }


    }
}

