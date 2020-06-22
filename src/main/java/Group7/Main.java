package Group7;

import Group7.agent.factories.DefaultAgentFactory;
import Group7.map.parser.Parser;


public class Main {

    public static void main(String[] args) {

        Game game = new Game(Parser.parseFile("./src/main/java/Group7/map/maps/generatedMap.map"), new DefaultAgentFactory(), false);

        long start = System.currentTimeMillis();
        game.run();
        System.out.printf("The winner is: %s\n", game.getWinner());
        long finish = System.currentTimeMillis();
        long timeElapsed = finish - start;
        System.out.println("Game took " + timeElapsed/1000 + " seconds to reach an outcome");

    }


}
