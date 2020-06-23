package Group7.agent.factories;

import Group7.agent.Capturer_Guard;
import Group7.agent.RandomIntruderAgent;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides common way to build agents for the competition.
 *
 * Sharing knowledge between agents is NOT ALLOWED.
 *
 * For example:
 * Agents must not hold ANY references to common objects or references to each other.
 */
public class DefaultAgentFactory implements IAgentFactory {

    public List<Intruder> createIntruders(int number) {
        List<Intruder> intruders = new ArrayList<>();
        for(int i = 0; i < number; i++)
        {
            //intruders.add(new AstarAgent());
            intruders.add(new RandomIntruderAgent());
        }
        return intruders;
    }

    public List<Guard> createGuards(int number) {
        List<Guard> guards = new ArrayList<>();
        for(int i = 0; i < number; i++) {
            //guards.add(new Map_());
            //guards.add(new RL());
            //guards.add(new MapAgent(300,600));
            //guards.add(new DeepSpace());
            //
            guards.add(new Capturer_Guard(i));
            //guards.add(new Explorator());
            //guards.add(new Explorator());
        }
        return guards;
    }
}