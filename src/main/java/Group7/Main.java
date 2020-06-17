package Group7;

import Group7.agent.factories.DefaultAgentFactory;
import Group7.map.parser.Parser;


public class Main {

    public static void main(String[] args) {

        Game game = new Game(Parser.parseFile("./src/main/java/Group7/map/maps/testWindow.map"), new DefaultAgentFactory(), false);
        game.run();
        System.out.printf("The winner is: %s\n", game.getWinner());
        System.out.println("test");

    }


}
