package Group9.agent;

import java.util.ArrayList;
import java.util.List;

public class testMoveIntruderDebug {

    public static void main (String[] args){
        ArrayList array = new ArrayList<>();
        array.add(1);
        array.add(1);
        array.add(1);
        array.add(2);
    /*    array.add(1);
        array.add(2);
        array.add(2);
        array.add(3);
        array.add(3);*/


        List<int[]> list = getNumberConsecutiveMoves(array);

        for (int i = 0; i < list.size(); i++) {
            System.out.println("number = " + list.get(i)[0]);
            System.out.println("consec # = " + list.get(i)[1]);
            System.out.println("-------");
        }
    }


    public static List<int[]> getNumberConsecutiveMoves(ArrayList listPositions){
        List<int[]> listOfmoves = new ArrayList<int[]>();
        int length = listPositions.size();
        int numberAdded = 0;

        int i = 0;
        do {
            System.out.println("do1");
            // int counter = 0;
            int indexSame = 1;
            boolean same = true;
            while (same){
                System.out.println("while2");
                System.out.println("i = " + i);
                System.out.println("ind = " + indexSame);
                System.out.println("i+ind = " + (i+indexSame));
                System.out.println("---");
                if ((i+indexSame)<length) {
                    if ((int) listPositions.get(i) == (int) listPositions.get(i + indexSame)) {
                        indexSame++;
                    } else {
                        same = false;
                        int move = (int) listPositions.get(i);
                        int[] moveAndNumberConsecutiveTimes = {move, indexSame};
                        listOfmoves.add(numberAdded, moveAndNumberConsecutiveTimes);
                        numberAdded++;
                    }
                }
                else{
                    same = false;
                }
            }
            System.out.println("i before = " + i);
            i = i + indexSame;
            System.out.println("i after = " + i);
            System.out.println("-");
        }
        while((i)<(length-2));
        return listOfmoves;
    }
}
