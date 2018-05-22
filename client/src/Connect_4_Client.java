import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Connect_4_Client extends JFrame implements Connect4Constants {
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

    // Host name or ip
    private String host = "localhost";

    private static final int MAX_CONNECT_TRIES = 50;
    private Socket socket;
    private int connectedTries;
    private JPanel content;
    
    public static void main(String[] args){
        Connect_4_Client client = new Connect_4_Client();
    }

    public Connect_4_Client() {
        this.content = new JPanel(new BorderLayout());

        GridPanel panel = new GridPanel(7,6);

        this.content.add(panel, BorderLayout.CENTER);


        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setContentPane(content);
        this.setVisible(true);

        connectedTries = 0;

        // Connect to the server
        connectToServer();
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
            return;
        }else{
            System.out.println("Connected to server.");
        }

        try {
            // Create an input stream to receive data from the server
            fromServer = new ObjectInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new ObjectOutputStream(socket.getOutputStream());
        }catch(Exception e){
            e.printStackTrace();
        }

        // Control the game on a separate thread
        new Thread(() -> {
            try {
                // Get notification from the server
                int player = fromServer.readInt();

                // Am I player 1 or 2?
                if (player == PLAYER1) {
                    myToken = 'R';
                    otherToken = 'G';


                    // Receive startup notification from the server
                    fromServer.readInt(); // Whatever read is ignored

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
                }
                repaint();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * Wait for the player to mark a cell
     */
    private void waitForPlayerAction() throws InterruptedException {
        while (waiting) {
            Thread.sleep(100);
        }

        waiting = true;
    }

    /**
     * Send this player's move to the server
     */
    private void sendMove() throws IOException {
        toServer.writeInt(rowSelected); // Send the selected row
        toServer.writeInt(columnSelected); // Send the selected column
    }

    /**
     * Receive info from the server
     */
    private void receiveInfoFromServer() throws IOException {
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

    private void receiveMove() throws IOException {
        // Get the other player's move
        int row = fromServer.readInt();
        int column = fromServer.readInt();

    }
}

