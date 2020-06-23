package Group7.agent.Intruder;

import Interop.Percept.IntruderPercepts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlgorithm {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public static void main2(String[] args) {
        ArrayList<Double> speeds = new ArrayList<>();
        speeds.add(0.0);
        speeds.add(0.0);
        speeds.add(0.0);
        speeds.add(0.0);

        ArrayList<Double> directions = new ArrayList<>();
        directions.add(43.0);
        directions.add(3.0);
        directions.add(13.0);
        directions.add(45.0);

        Indiv a = new Indiv(speeds,directions);
        //printPopulation(new Indiv[]{a});
    }

    public static final int generation_nb = 1000;
    public static final int population_size = 100;

    public static Indiv computePath() {

        Indiv[] population = new Indiv[population_size];
        for (int i = 0; i < population_size; i++) {
            Indiv a = new Indiv();
            population[i] = a;
        }
        System.out.println("Start");
        //printPopulation(population);
        System.out.println();
       return computePath(population);
    }

    public static Indiv computePath( Indiv[] population) {

        double actualFitness;
        int fitt = 0;
            int index = 0;

            double prevFitness = -Double.MAX_VALUE;
            double error;
            do {
                index++;
//                for (int j = 0; j < generation_nb; j++) {
                    System.out.println("generation " + index);

//                 System.out.println("Population= ");
//                printPopulation(population);
//                System.out.println();

                    Indiv[] child = reproductPopulation(population);

             //  System.out.println("reproduct");
//                printPopulation(child);
//                System.out.println();

                    mutation(child);
                //System.out.println("mutation");

                    population = child;
                    double newFitness = -Double.MAX_VALUE;
                fitt=0;
                for (int w = 0; w < population.length; w++) {
                        if (population[w].getFitness() > newFitness) {
                            newFitness = population[w].getFitness();
                            fitt = w;
                        }
                    }
                error = Math.abs(prevFitness-newFitness);
                System.out.println("error = " + error);
                prevFitness = newFitness;
                System.out.println("fitness = " +prevFitness+ "; path_fitness = " + population[fitt].path_length_cost()+"; distance_fitness = " + population[fitt].distance_cost()+"; obstacle_fitness = "+population[fitt].obstacle_cost());
                System.out.println();
            }while (generation_nb>index || population[fitt].obstacle_cost()>0);

//        printPopulation(population);

                double path_fitness = Double.MAX_VALUE;
                double distance_fitness = Double.MAX_VALUE;
                int fit_index = 0;
                double dst_index = 0;
                double path_index = 0;
                for (int w = 0; w < population.length; w++) {
                    if (population[w].getFitness() >= prevFitness) {
                        actualFitness = population[w].getFitness();
                        fit_index = w;
                    }
                    if (population[w].path_length_cost() < path_fitness) {
                        path_fitness = population[w].path_length_cost();
                        path_index = w;
                    }
                    if (population[w].distance_cost() < distance_fitness) {
                        distance_fitness = population[w].distance_cost();
                        dst_index = w;
                    }
                }
                System.out.println("fitness = " + prevFitness + " -- " + fit_index + "; path_fitness = " + path_fitness + " -- " + path_index + "; distance_fitness = " + distance_fitness + " -- " + dst_index);

            return population[fit_index];

            }


    private static void printIndividual( ArrayList<Double> s, ArrayList<Double> d){
        for(Double a  : s){
            System.out.print(a+", ");
        }
        System.out.println();
        for(Double a  : d) {
            System.out.print(a + ", ");
        }
//        System.out.print(ANSI_RESET);
    }

    private static void printPopulation( Indiv[] a){
        for(int i = 0; i < a.length; i++){
            System.out.print(/*ANSI_RED +*/ "Individual fitness " + i + ": "+ ANSI_RED+ a[i].getFitness() + ANSI_RESET+", costs: ");
            System.out.print(" path_cost= "+a[i].path_length_cost());
            System.out.print(" distance_cost= "+a[i].distance_cost());
            System.out.print(" obstacle_cost= "+a[i].obstacle_cost());
            System.out.println();
            
           printIndividual(a[i].getSpeeds(),a[i].getDirections());
           System.out.println();
        }
    }

    private static Indiv[] selectParents(Indiv[] a){

            Arrays.sort(a, new Comparator<Indiv>() {
                @Override
                public int compare(Indiv indiv, Indiv t1) {
                    return Double.compare(indiv.getFitness(),t1.getFitness());
                }
            });
               Indiv[] parents = {a[a.length-2],a[a.length-1]};
        return parents;
    }

    private static Indiv[] reproductPopulation(Indiv[] population) {
        return reproduct(selectParents(population));
    }

    public static Indiv[] reproduct(Indiv i) {
        Indiv[] out = new Indiv[population_size];
        for (int m = 0; m < population_size; m++) {
          out[m] = i.clone();
        }
        return out;
    }

    public static Indiv[] reproduct(Indiv[] parents) {
        Random rnd = new Random();

        Indiv male = parents[0];
        Indiv female = parents[1];
        Indiv[] child = new Indiv[population_size];

        for (int m = 0; m < population_size; m = m+2) {
            int cross_limit = rnd.nextInt(Math.min(male.getSpeeds().size(),female.getSpeeds().size()));
            Indiv clone1 = male.clone();
            Indiv clone2 = female.clone();

                Indiv child1 = new Indiv(new ArrayList<Double>(clone1.getSpeeds().subList(0,cross_limit)), new ArrayList<Double>(clone1.getDirections().subList(0,cross_limit)));
                Indiv child2 = new Indiv(new ArrayList<Double>(clone2.getSpeeds().subList(0,cross_limit)), new ArrayList<Double>(clone2.getDirections().subList(0,cross_limit)));
               child1.getSpeeds().addAll(clone2.getSpeeds().subList(cross_limit,clone2.getSpeeds().size()));
            child1.getDirections().addAll(clone2.getDirections().subList(cross_limit,clone2.getDirections().size()));
            child2.getSpeeds().addAll(clone1.getSpeeds().subList(cross_limit,clone1.getSpeeds().size()));
            child2.getDirections().addAll(clone1.getDirections().subList(cross_limit,clone1.getDirections().size()));

            child1.comuteFitness();
            child2.comuteFitness();

            child[m] = child1;
            child[m+1] = child2;
          }

        return child;
    }

    private static void mutation(Indiv[]a){
        int speed_mutationRate = 50;
        int direction_mutationRate = 50;
        int intertion_rate = 50;
        int deletion_rate = 50;
//        double direction_strength = 200;
        double speed_strength = Indiv.getMax_speed()/2;
        Random rnd = new Random();

        for(int i = 0; i < a.length; i++) {


            int mutate = rnd.nextInt(100);
           if (mutate <= speed_mutationRate) {
//               System.out.println("mutate = " + i);
                int mutated = rnd.nextInt(a[i].getSpeeds().size());
                double speed_mutation = Math.random() * speed_strength + a[i].getSpeeds().get(mutated);

                if (Math.random() <= 0.5) {
                    speed_mutation = -speed_mutation;
                }
                if (speed_mutation < 0) {
                    speed_mutation = 0;
                }
                if (speed_mutation > Indiv.getMax_speed()) {
                    speed_mutation = Indiv.getMax_speed();
                }

                a[i].getSpeeds().set(mutated, speed_mutation);
                a[i].comuteFitness();
            }


            mutate = rnd.nextInt(100);
            if (mutate <= direction_mutationRate) {
                int mutated = rnd.nextInt(a[i].getSpeeds().size());

                double direction_mutation = Math.random() * Indiv.getMax_angle() + a[i].getDirections().get(mutated);
                if (Math.random() <= 0.5) {
                    direction_mutation = -direction_mutation;
                }
                if (direction_mutation < Indiv.getMin_angle()) {
                    direction_mutation = Indiv.getMin_angle();
                }
                if (direction_mutation >= Indiv.getMax_angle()) {
                    direction_mutation = Indiv.getMax_angle();
                }

                a[i].getDirections().set(mutated, direction_mutation);
                a[i].comuteFitness();
            }

            mutate = rnd.nextInt(100);
            if (mutate <= intertion_rate) {
                int added_gene = rnd.nextInt(a[i].getDirections().size() + 1);
                a[i].getSpeeds().add(added_gene, Math.random() * Indiv.getMax_speed());
                int positive = 1;
                if (Math.random() > 0.5) {
                    positive = -1;
                }
                a[i].getDirections().add(added_gene, Math.random() * Indiv.getMax_angle() * positive);
                a[i].comuteFitness();
            }

            mutate = rnd.nextInt(100);
            if (mutate <= deletion_rate) {
                int deleted_gene = rnd.nextInt(a[i].getDirections().size());
//                System.out.println("remove "+deleted_gene);
                a[i].getSpeeds().remove(deleted_gene);
                a[i].getDirections().remove(deleted_gene);
                a[i].comuteFitness();
            }
        }
//        System.out.println("mutation");
//        printPopulation(a);
//        System.out.println();
    }

}

