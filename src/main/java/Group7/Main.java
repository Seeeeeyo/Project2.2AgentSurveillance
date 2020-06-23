package Group7;

import Group7.agent.factories.DefaultAgentFactory;
import Group7.map.parser.Parser;


public class Main {

    public static void main(String[] args) {
        int gameTurns = 0;
        double averageTurns = 0;
        int nGames = 25;
        int nGamesWon = 0;
        double averageTime = 0;
        double[] captureTimes = new double[nGames];
        double[] captureTurns = new double[nGames];
        int winCounter = 0;
        for (int i = 0; i < nGames; i++) {
            Game game = new Game(Parser.parseFile("src/main/java/Group7/map/maps/openSpace-0-InnerWalls.map"), new DefaultAgentFactory(), false);
            long start = System.currentTimeMillis();
            game.run();
            gameTurns = game.getTurns();
            //System.out.printf("The winner is: %s\n", game.getWinner());
            long finish = System.currentTimeMillis();
            long timeElapsed = finish - start;
            if (game.getWinner() == Game.Team.GUARDS) {
                nGamesWon++;
                averageTurns = averageTurns + gameTurns;
                averageTime = averageTime + timeElapsed;
                captureTimes[winCounter] = timeElapsed;
                captureTurns[winCounter] = gameTurns;
                winCounter++;
            }

        }
        averageTurns = averageTurns / nGamesWon;
        averageTime = averageTime / nGamesWon;
        System.out.println("Guards won " + nGamesWon + " out of " + nGames + " games");
        System.out.println("Average capture time : " + averageTime);
        System.out.println("Average capture turns : " + averageTurns);
        for (int i = 0; i < nGamesWon; i++) {
            System.out.print(captureTimes[i] + ", ");
        }
        System.out.println();

        for (int i = 0; i < nGamesWon; i++) {
            System.out.print(captureTurns[i] + ", ");
        }

    }


}

