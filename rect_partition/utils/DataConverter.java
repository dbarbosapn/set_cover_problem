package rect_partition.utils;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import rect_partition.Vert;

public class DataConverter {

    private static final String DATA_FILE = "rect_partition/clp_approaches/prolog_data.tmp";

    public static void generatePrologDataFile(Scanner input) throws PartitionProblemException {
        try {
            FileWriter file = new FileWriter(DATA_FILE);

            int nrects = input.nextInt();

            file.write(String.format("nrects(%d).\n", nrects));

            List<Vert> verts = new ArrayList<>();

            List<RectangleCache> caches = new ArrayList<>();

            for (int i = 0; i < nrects; i++) {

                int r = input.nextInt();
                int nverts = input.nextInt();

                RectangleCache cache = new RectangleCache(r);

                for (int j = 0; j < nverts; j++) {
                    Vert v = new Vert(input.nextInt(), input.nextInt());

                    if (!verts.contains(v)) {
                        verts.add(v);
                        file.write(String.format("v(%d, %d, %d).\n", verts.indexOf(v) + 1, v.getX(), v.getY()));
                    }

                    cache.verts.add(verts.indexOf(v) + 1);

                }

                caches.add(cache);

            }

            for (RectangleCache cache : caches) {
                StringBuilder sb = new StringBuilder(String.format("r(%d, %d, [", cache.rectNum, cache.verts.size()));

                for (int v : cache.verts) {
                    sb.append(v);
                    sb.append(',');
                }

                sb.setLength(sb.length() - 1);

                sb.append("]).\n");

                file.write(sb.toString());
            }

            int ngoal = input.nextInt();
            StringBuilder sb = new StringBuilder();
            sb.append("goal([");

            for (int i = 0; i < ngoal; i++) {
                sb.append(String.valueOf(input.nextInt()));

                if (i != ngoal - 1) {
                    sb.append(',');
                }
            }

            sb.append("]).\n");

            file.write(sb.toString());

            file.close();
        } catch (Exception e) {
            throw new PartitionProblemException(e.getMessage(), e.getCause());
        }
    }

    private static class RectangleCache {
        int rectNum;
        Set<Integer> verts = new HashSet<>();

        RectangleCache(int rectNum) {
            this.rectNum = rectNum;
        }
    }

}