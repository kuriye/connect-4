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

            System.out.println(isRowFull(0));

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

                // Write anything to notify player 1 to start
                // This is just to let player 1 know to start
                toPlayer1.writeInt(1);

                // Continuously serve the players and determine and report
                // the game status to the players
                while (true) {
                    // Receive a move from player 1
                    Point2D point = (Point2D) fromPlayer1.readObject();
                    cell[(int)point.getX()][(int)point.getY()] = 'R';

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
                        toPlayer2.writeInt(CONTINUE);

                        // Send player 1's selected row and column to player 2
                        toPlayer2.writeObject(point);
                    }

                    // Receive a move from Player 2
                    point = (Point2D) fromPlayer2.readObject();

                    cell[(int) point.getX()][(int)(point.getY())] = 'G';

                    // Check if Player 2 wins
                    if (isWon('G')) {
                        toPlayer1.writeInt(PLAYER2_WON);
                        toPlayer2.writeInt(PLAYER2_WON);
                        toPlayer1.writeObject(point);
                        break;
                    }
                    else {
                        // Notify player 1 to take the turn
                        toPlayer1.writeInt(CONTINUE);

                        // Send player 2's selected row and column to player 1
                        toPlayer1.writeObject(point);
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
//        private void sendMove(OutputStream out, int row, int column)
//                throws IOException {
//            out.writeInt(row); // Send row index
//            out.writeInt(column); // Send column index
//        }

        /** Determine if the cells are all occupied */
        private boolean isFull() {
            for (int i = 0; i < 7; i++)
                for (int j = 0; j < 6; j++)
                    if (cell[i][j] == ' ')
                        return false; // At least one cell is not filled

            // All cells are filled
            return true;
        }

        private boolean isRowFull(int row){
            return cell[5][row] != ' ';
        }

        /** Determine if the player with the specified token wins */
        private boolean isWon(char token) {
            // Check all rows
            for (int i = 0; i < 3; i++)
                if ((cell[i][0] == token)
                        && (cell[i][1] == token)
                        && (cell[i][2] == token)) {
                    return true;
                }

            /** Check all columns */
            for (int j = 0; j < 3; j++)
                if ((cell[0][j] == token)
                        && (cell[1][j] == token)
                        && (cell[2][j] == token)) {
                    return true;
                }

            /** Check major diagonal */
            if ((cell[0][0] == token)
                    && (cell[1][1] == token)
                    && (cell[2][2] == token)) {
                return true;
            }

            /** Check subdiagonal */
            if ((cell[0][2] == token)
                    && (cell[1][1] == token)
                    && (cell[2][0] == token)) {
                return true;
            }

            /** All checked, but no winner */
            return false;
        }
    }
