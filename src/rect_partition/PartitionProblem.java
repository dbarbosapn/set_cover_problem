package rect_partition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.parctechnologies.eclipse.CompoundTerm;
import com.parctechnologies.eclipse.EclipseEngine;
import com.parctechnologies.eclipse.EclipseEngineOptions;
import com.parctechnologies.eclipse.EmbeddedEclipse;

import rect_partition.utils.DataConverter;
import rect_partition.utils.Utils;
import rect_partition.approaches.AC3;
import rect_partition.approaches.AStar;
import rect_partition.approaches.Approach;
import rect_partition.approaches.BFS;
import rect_partition.approaches.BranchAndBound;
import rect_partition.approaches.CSPApproach;
import rect_partition.approaches.DFS;
import rect_partition.approaches.GreedyHardestRectanglesFirst;
import rect_partition.approaches.GreedyMostCoverageFirst;
import rect_partition.approaches.IDDFS;
import rect_partition.approaches.IteratedLocalSearch;
import rect_partition.approaches.SimulatedAnnealing;

public class PartitionProblem {

    private static final int NUM_APPROACHES = 15;
    private static final String headerText = "Welcome to the Rectangle Partition Problem.\nThis software was designed and developed by Diogo Barbosa.\n";

    private static int selectedApproach;
    private static boolean engineStarted = false;

    public static final String LOGGER = "PartitionProblemLogger";

    private static String CLPselectionMethod = "input_order";
    private static String CLPchoiceMethod = "indomain";
    private static String CLPsearchMethod = "complete";

    public static void main(String[] args) {
        Scanner stdin = new Scanner(System.in);

        setupLogger();

        readProperties();

        while (true) {
            Utils.clearWindow(headerText);

            System.out.println("Choose an approach by entering the corresponding number: ");
            // Approaches
            System.out.println("1: Greedy - State that has less rectangles left to cover first");
            System.out.println("2: Greedy - State that has the hardest rectangle covered first");
            System.out.println("3: BFS (Stop at the first final solution)");
            System.out.println("4: BFS (Find the best solution)");
            System.out.println("5: DFS (Stop at the first final solution)");
            System.out.println("6: DFS (Find the best solution)");
            System.out.println("7: IDDFS (Stop at the first final solution)");
            System.out.println("8: Branch And Bound");
            System.out.println("9: A* - Rectangles left as heuristic");
            System.out.println("10: Iterated Local Search");
            System.out.println("11: ILS with Randomization");
            System.out.println("12: Simulated Annealing");
            System.out.println("13: CSP - AC-3");
            System.out.println("14: ECLiPSe CLP");
            System.out.println("15: ECLiPSe CLP - Assign colors to verts");

            int chosen = stdin.nextInt();
            Utils.clearWindow(headerText);

            if (chosen < 1 || chosen > NUM_APPROACHES) {
                System.out.println("Invalid number of approaches. (Must be between 1 and " + NUM_APPROACHES + ")");
                stdin.close();
                return;
            }

            selectedApproach = chosen;

            System.out.print("Insert the file path: ");

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

            System.out.print("Do you wish to start again? (Y/N): ");

            if (!stdin.next().toLowerCase().startsWith("y"))
                break;
        }

        stdin.close();
    }

    private static void readProperties() {
        try {
            Properties properties = new Properties();
            String propFile = "config.properties";
            InputStream stream = new FileInputStream(new File(propFile));
            properties.load(stream);

            IDDFS.INITIAL_K = Integer
                    .valueOf(properties.getProperty("IDDFSinitialDepth", String.valueOf(IDDFS.INITIAL_K)));

            IteratedLocalSearch.K = Integer
                    .valueOf(properties.getProperty("ILSiterations", String.valueOf(IteratedLocalSearch.K)));

            IteratedLocalSearch.VERTS_REMOVE_PERCENTAGE = Double.valueOf(properties.getProperty(
                    "ILSvertRemovePercentage", String.valueOf(IteratedLocalSearch.VERTS_REMOVE_PERCENTAGE)));

            IteratedLocalSearch.VERTS_ADD_PERCENTAGE = Double.valueOf(properties.getProperty("ILSvertAddPercentage",
                    String.valueOf(IteratedLocalSearch.VERTS_ADD_PERCENTAGE)));

            IteratedLocalSearch.PROBABILITY_ACCEPT_WRONG_SOLUTION = Double.valueOf(properties.getProperty(
                    "ILSprobWrongAccept", String.valueOf(IteratedLocalSearch.PROBABILITY_ACCEPT_WRONG_SOLUTION)));

            SimulatedAnnealing.INITIAL_TEMP = Double.valueOf(
                    properties.getProperty("SAinitialTemperature", String.valueOf(SimulatedAnnealing.INITIAL_TEMP)));

            SimulatedAnnealing.COOLING_RATE = Double
                    .valueOf(properties.getProperty("SAcoolingRate", String.valueOf(SimulatedAnnealing.COOLING_RATE)));

            SimulatedAnnealing.VERTS_REMOVE_PERCENTAGE = Double.valueOf(properties.getProperty("SAvertRemovePercentage",
                    String.valueOf(SimulatedAnnealing.VERTS_REMOVE_PERCENTAGE)));

            SimulatedAnnealing.VERTS_ADD_PERCENTAGE = Double.valueOf(properties.getProperty("SAvertAddPercentage",
                    String.valueOf(SimulatedAnnealing.VERTS_ADD_PERCENTAGE)));

            CLPchoiceMethod = properties.getProperty("CLPchoiceMethod", CLPchoiceMethod);
            CLPselectionMethod = properties.getProperty("CLPselectionMethod", CLPselectionMethod);
            CLPsearchMethod = properties.getProperty("CLPsearchMethod", CLPsearchMethod);

        } catch (Exception e) {
            Utils.logError(e);
        }
    }

    private static void startEclipseEngine() throws Exception {
        EclipseEngineOptions options = new EclipseEngineOptions();
        options.setUseQueues(false);

        EmbeddedEclipse.getInstance(options);

        engineStarted = true;
    }

    /**
     * Sets up the logger to write at log.txt
     */
    private static void setupLogger() {
        Logger logger = Logger.getLogger(LOGGER);

        FileHandler fh;

        try {
            fh = new FileHandler("log.txt");
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
            logger.info("Logger initialized");
        } catch (Exception e) {
            // Ignore
        }
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

        if (selectedApproach >= 14) {
            solveProlog(file, stdin, setNumber);
            return;
        }

        int rectangles = file.nextInt();
        Map<Integer, Vert> vertMap = new HashMap<>();
        Set<Integer> rectanglesToCover = new HashSet<>();

        readSetData(rectangles, rectanglesToCover, vertMap, file);

        if (selectedApproach == 13) {
            CSPApproach approach = new AC3(vertMap.values(), rectanglesToCover);
            startSolving(approach, stdin, setNumber);
        } else {
            Approach approach = chooseApproach(vertMap.values(), rectanglesToCover);
            startSolving(approach, stdin, setNumber);
        }

    }

    private static void startSolving(Approach approach, Scanner stdin, int setNumber) {
        try {
            int answer = approach.solve();

            Utils.clearWindow(headerText);

            System.out.println("Number of vertexes in solution: " + answer);
            System.out.println("Number of states expanded: " + approach.getStatesExpanded());
            System.out.println();
            System.out.print("Do you want to get an output of the vertexes chosen? (Y/N): ");

            if (stdin.next().toLowerCase().startsWith("y")) {
                String filename = outputVertexes(approach.getChosenVerts(), setNumber);
                System.out.println("Your file is located at " + filename);
            }

        } catch (Exception e) {
            Utils.logError(e);
            return;
        }
    }

    private static void startSolving(CSPApproach approach, Scanner stdin, int setNumber) {
        try {
            int answer = approach.solve();

            Utils.clearWindow(headerText);

            System.out.println("Number of vertexes in solution: " + answer);
            System.out.println("Number of arcs checked: " + approach.getArcsChecked());
            System.out.println();
            System.out.print("Do you want to get an output of the vertexes chosen? (Y/N): ");

            if (stdin.next().toLowerCase().startsWith("y")) {
                String filename = outputVertexes(approach.getChosenVerts(), setNumber);
                System.out.println("Your file is located at " + filename);
            }

        } catch (Exception e) {
            Utils.logError(e);
            return;
        }
    }

    private static void solveProlog(Scanner file, Scanner stdin, int setNumber) {
        try {
            DataConverter.generatePrologDataFile(file);

            if (!engineStarted) {
                startEclipseEngine();
            }

            EclipseEngine engine = EmbeddedEclipse.getInstance();

            File program = new File("classes/rect_partition/clp_approaches/partition_problem.ecl");

            Utils.clearWindow(headerText);

            engine.compile(program);

            if (selectedApproach == 14) {
                CompoundTerm term = engine.rpc(String.format("partition_problem(S, %s, %s, %s)", CLPselectionMethod,
                        CLPchoiceMethod, CLPsearchMethod));
                System.out.println("Chosen Verts: " + term.arg(1));
            } else {
                CompoundTerm term = engine.rpc(String.format("partition_color_problem(C, S, %s, %s, %s)",
                        CLPselectionMethod, CLPchoiceMethod, CLPsearchMethod));
                System.out.println("Chosen Verts: " + term.arg(2));
                System.out.println("Colors: " + term.arg(1));
            }

        } catch (Exception e) {
            Utils.logError(e);
        }
    }

    private static String outputVertexes(Set<Vert> chosenVerts, int setNumber) throws FileNotFoundException {
        final String dir = System.getProperty("user.dir");
        File directory = new File(dir + "/output");
        if (!directory.exists()) {
            directory.mkdir();
        }
        String filename = dir + "/output/vertexes-set" + setNumber + ".txt";
        PrintWriter writer = new PrintWriter(new FileOutputStream(filename, false));
        for (Vert v : chosenVerts)
            writer.println(v);
        writer.close();
        return filename;
    }

    /**
     * Returns an instance of the approach chosen
     * 
     * @param verts             all the verts in the set
     * @param rectanglesToCover the rectangles to be covered
     * @return the approach
     */
    private static Approach chooseApproach(Collection<Vert> verts, Set<Integer> rectanglesToCover) {
        switch (selectedApproach) {
            case 1:
                return new GreedyMostCoverageFirst(verts, rectanglesToCover);
            case 2:
                return new GreedyHardestRectanglesFirst(verts, rectanglesToCover);
            case 3:
                return new BFS(verts, rectanglesToCover, true);
            case 4:
                return new BFS(verts, rectanglesToCover, false);
            case 5:
                return new DFS(verts, rectanglesToCover, true);
            case 6:
                return new DFS(verts, rectanglesToCover, false);
            case 7:
                return new IDDFS(verts, rectanglesToCover);
            case 8:
                return new BranchAndBound(verts, rectanglesToCover);
            case 9:
                return new AStar(verts, rectanglesToCover);
            case 10:
                return new IteratedLocalSearch(verts, rectanglesToCover, false);
            case 11:
                return new IteratedLocalSearch(verts, rectanglesToCover, true);
            case 12:
                return new SimulatedAnnealing(verts, rectanglesToCover);
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
    private static void readSetData(int rectangles, Set<Integer> rectanglesToCover, Map<Integer, Vert> vertMap,
            Scanner file) {
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

        int r = file.nextInt();
        for (int i = 0; i < r; i++) {
            rectanglesToCover.add(file.nextInt());
        }

    }

}