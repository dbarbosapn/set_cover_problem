package rect_partition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import rect_partition.utils.Utils;

import rect_partition.approaches.Approach;
import rect_partition.approaches.GreedyMostCoverageFirst;

public class PartitionProblem {

    private static final int NUM_APPROACHES = 1;
    private static final String headerText = "Welcome to the Partition Problem.\nThis software was design and developed by Diogo Barbosa.\n";
    private static int selectedApproach;

    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);

        Utils.clearWindow(headerText);

        System.out.println("Choose an approach by entering the corresponding number: ");
        // Approaches
        System.out.println("1: Greedy - Vertex that covers the most triangles first");

        int chosen = stdin.nextInt();
        Utils.clearWindow(headerText);

        if (chosen < 1 || chosen > NUM_APPROACHES) {
            System.out.println("Invalid number of approaches. (Must be between 1 and " + NUM_APPROACHES + ")");
            stdin.close();
            return;
        }

        selectedApproach = chosen;

        System.out.print("Insert the file path (must be absolute path): ");

        String filePath = stdin.next();
        Scanner file;
        try {
            file = new Scanner(new File(filePath));
        } catch (IOException e) {
            Utils.logError(e);
            stdin.close();
            return;
        }

        Utils.clearWindow(headerText);
        loadSets(file, stdin);

        file.close();
        stdin.close();
    }

    /**
     * Starts reading the file and asks before each set if the users wants to
     * continue
     * 
     * @param file
     * @param stdin
     */
    private static void loadSets(Scanner file, Scanner stdin) {
        int sets = file.nextInt();

        System.out.println("Number of sets: " + sets);
        System.out.println();

        for (int i = 0; i < sets; i++) {
            System.out.print("Proceed to set n." + (i + 1) + "? (Y/N): ");

            if (stdin.next().toLowerCase().startsWith("y"))
                solveSet(file, stdin, i + 1);
            else
                break;

            System.out.println();
        }

    }

    /**
     * Solves the next set in the file
     * 
     * @param file
     * @param stdin
     */
    private static void solveSet(Scanner file, Scanner stdin, int setNumber) {
        int rectangles = file.nextInt();
        Map<Integer, Vert> vertMap = new HashMap<>();

        readSetData(rectangles, vertMap, file);

        Approach approach = chooseApproach(rectangles, vertMap.values());

        try {
            int answer = approach.solve();

            Utils.clearWindow(headerText);

            System.out.println("Number of vertexes in solution: " + answer);
            System.out.println("Number of states expanded: " + approach.getStatesExpanded());
            System.out.println();
            System.out.print("Do you want to get an output of the vertexes chosen? (Y/N): ");

            if (stdin.next().toLowerCase().startsWith("y")) {
                String filename = outputVertexes(approach, setNumber);
                System.out.println("Your file is located at " + filename);
            }

        } catch (Exception e) {
            Utils.logError(e);
            return;
        }
    }

    private static String outputVertexes(Approach approach, int setNumber) throws FileNotFoundException {
        final String dir = System.getProperty("user.dir");
        File directory = new File(dir + "/output");
        if (!directory.exists()) {
            directory.mkdir();
        }
        String filename = dir + "/output/vertexes-set" + setNumber + ".txt";
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false));
        for (Vert v : approach.getChosenVerts())
            writer.println(v);
        writer.close();
        return filename;
    }

    /**
     * Returns an instance of the approach chosen
     * 
     * @param rectangles - number of rectangles in the set
     * @param verts      - all the verts in the set
     * @return the approach
     */
    private static Approach chooseApproach(int rectangles, Collection<Vert> verts) {
        switch (selectedApproach) {
            case 1:
                return new GreedyMostCoverageFirst(rectangles, verts);
        }

        return null;
    }

    /**
     * Reads the set data. This includes all the rectangles and vertexes.
     * 
     * @param rectangles - the number of rectangles in the set
     * @param vertMap    - the map containing the vertexes
     * @param file       - the file scanner
     */
    private static void readSetData(int rectangles, Map<Integer, Vert> vertMap, Scanner file) {
        // Read the rectangles
        for (int i = 0; i < rectangles; i++) {
            int currentRect = file.nextInt();

            int numVerts = file.nextInt();

            // Read the vertexes
            for (int j = 0; j < numVerts; j++) {
                int x = file.nextInt();
                int y = file.nextInt();

                Vert v = new Vert(x, y);

                // If we've already added this vert, get its reference
                if (vertMap.containsValue(v))
                    v = vertMap.get(v.hashCode());
                else
                    vertMap.put(v.hashCode(), v);

                v.getRectangles().add(currentRect);
            }
        }
    }

}