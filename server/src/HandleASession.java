import javafx.application.Platform;

import java.awt.geom.Point2D;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.stream.Collectors;



public class HandleASession implements Runnable, Connect4Constants {
        private Socket player1;
        private Socket player2;

        // Create and initialize cells
        private char[][] cell =  new char[6][7];

        private ObjectInputStream fromPlayer1;
        private ObjectOutputStream toPlayer1;
        private ObjectInputStream fromPlayer2;
        private ObjectOutputStream toPlayer2;

        // Continue to play
        private boolean continueToPlay = true;

        /** Construct a thread */
        public HandleASession(Socket player1, Socket player2) {
            this.player1 = player1;
            this.player2 = player2;

            // Initialize cells
            for (int i = 0; i < 6; i++)
                for (int j = 0; j < 7; j++)
                    cell[i][j] = ' ';

            //sout
            String result = Arrays
                    .stream(cell)
                    .map(Arrays::toString)
                    .collect(Collectors.joining(System.lineSeparator()));
            System.out.println(result);

//            System.out.println(isRowFull(0)); wordt ook gecontroleerd in client -> als hij vol zit dan wordt hij niet gestuurd naar de server

        }

        /** Implement the run() method for the thread */
        public void run() {
            try {
                // Create data input and output streams
                 fromPlayer1 = new ObjectInputStream(
                        player1.getInputStream());
                 toPlayer1 = new ObjectOutputStream(
                        player1.getOutputStream());
                 fromPlayer2 = new ObjectInputStream(
                        player2.getInputStream());
                 toPlayer2 = new ObjectOutputStream(
                        player2.getOutputStream());

                 boolean isValid;

                System.out.println("run() in HandleASession");

                // Write anything to notify player 1 to start
                // This is just to let player 1 know to start
                //toPlayer1.writeInt(1);
                toPlayer1.writeInt(PLAYER1);
                System.out.println("Send to player 1");
                toPlayer1.flush();
                toPlayer2.writeInt(PLAYER2);
                toPlayer2.flush();
                System.out.println("Send to player 2");

                // Continuously serve the players and determine and report
                // the game status to the players
                while (true) {
                    // Receive a move from player 1
                    Point2D point = (Point2D) fromPlayer1.readObject();
                    if(cell[(int) point.getX()][(int)(point.getY())] == ' ') {
                        cell[(int) point.getX()][(int) point.getY()] = 'R';
                        isValid = true;
                        System.out.println(point);
                    }
                    else {
                        toPlayer1.writeInt(INVALID_MOVE);
                        isValid = false;
                    }

                    // Check if Player 1 wins
                    if (isWon('R')) {
                        toPlayer1.writeInt(PLAYER1_WON);
                        toPlayer2.writeInt(PLAYER1_WON);
                        toPlayer2.writeObject(point);
                        break; // Break the loop
                    }
                    else if (isFull()) { // Check if all cells are filled
                        toPlayer1.writeInt(DRAW);
                        toPlayer2.writeInt(DRAW);
                        toPlayer2.writeObject(point);
                        break;
                    }
                    else {
                        // Notify player 2 to take the turn
                        if(isValid) {
                            toPlayer2.writeInt(CONTINUE);

                            // Send player 1's selected row and column to player 2
                            toPlayer2.writeObject(point);
                            isValid = false;
                        }
                    }

                    // Receive a move from Player 2
                    point = (Point2D) fromPlayer2.readObject();
                    if(cell[(int) point.getX()][(int)(point.getY())] == ' ') {
                        cell[(int) point.getX()][(int) (point.getY())] = 'G';
                        isValid = true;
                    }
                    else {
                        toPlayer2.writeInt(INVALID_MOVE);
                        isValid = false;
                    }


                    // Check if Player 2 wins
                    if (isWon('G')) {
                        toPlayer1.writeInt(PLAYER2_WON);
                        toPlayer2.writeInt(PLAYER2_WON);
                        toPlayer1.writeObject(point);
                        break;
                    }
                    else {
                        if(isValid) {
                            // Notify player 1 to take the turn
                            toPlayer1.writeInt(CONTINUE);

                            // Send player 2's selected row and column to player 1
                            toPlayer1.writeObject(point);
                        }
                    }
                }
            }catch (IOException ioEx){
//                ioEx.printStackTrace();
                System.out.println("Player left.");

            }catch (ClassNotFoundException classEx){
                classEx.printStackTrace();
            }
        }

        /** Send the move to other player */
//        private void sendMove(ObjectOutputStream out, int row, int column)
//                throws IOException {
//            out.writeInt(row); // Send row index
//            out.writeInt(column); // Send column index
//        }

        /** Determine if the cells are all occupied */
        private boolean isFull() {
            for (int i = 0; i < 7; i++)
                for (int j = 0; j < 6; j++)
                    if (cell[i][j] == ' ') {
                        System.out.println("vol");
                        return false; // At least one cell is not filled
                    }
            // All cells are filled
            return true;
        }

        private boolean isRowFull(int row){
            return cell[5][row] != ' ';
        }

        /** Determine if the player with the specified token wins */
        private boolean isWon(char token) {
            // TEST BOARD VALUES
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 6; y++) {
                    System.out.print(cell[x][y]);
                }
                System.out.println();
            }


            // Check all rows
            for (int x = 0; x < 6; x++) {
                for (int y = 0; y < 3; y++) {
                    if (cell[x][y] == token && cell[x][y+1] == token && cell[x][y+2] == token && cell[x][y+3] == token) {
                        return true;
                    }
                }
            }
            ///Vert

            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 7; y++) {
                    if (cell[x][y] == token && cell[x+1][y] == token && cell[x+2][y] == token && cell[x+3][y] == token) {
                        return true;
                    }
                }
            }

            //Diagonal wins
            //0 to 1
            //0 to 3
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 6; y++) {
                    if (cell[x][y] == token && cell[x+1][y+1] == token && cell[x+2][y+2] == token && cell[x+3][y+3] == token) {
                        return true;
                    }
                }
            }

            //Other diagonal wins
            for (int x = 0; x < 3; x++) {
                for (int y = 3; y < 7; y++) {
                    if (cell[x][y] == token && cell[x+1][y-1] == token && cell[x+2][y-2] == token && cell[x+3][y-3] == token) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
