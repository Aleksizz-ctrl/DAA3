import connectivity.SCC;
import connectivity.TopoSort;
import dsu.DSU;
import graph.Graph;
import graph.GraphGen;
import mst.Kruskal;
import mst.Prim;
import shortestpath.Dijkstra;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class Benchmark {
    static final String[] NAMES = {"Small", "Medium", "Large"};
    static final int[] V = {10, 100, 1000};
    static final int[] E = {30, 500, 5000};
    static final int REPEATS = 5;
    static final long SEED = 42;
    static FileWriter out;

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Paths.get("results/tables"));
        out = new FileWriter("results/tables/results.csv");
        out.write("algorithm,graph,V,E,avg_time_ns,ops\n");
        for (int i = 0; i < NAMES.length; i++) {
            int n = V[i], m = E[i];
            Graph ug = GraphGen.connected(n, m, SEED);
            Graph dg = GraphGen.directed(n, m, SEED);
            Graph dag = GraphGen.dag(n, m, SEED);

            run("Kruskal", i, () -> Kruskal.run(ug).ops);
            run("Prim", i, () -> Prim.run(ug).ops);
            run("Topo-DAG", i, () -> new TopoSort(dag).ops);
            run("Topo-Cyclic", i, () -> new TopoSort(dg).ops);
            run("SCC", i, () -> new SCC(dg).ops);
            run("Dijkstra", i, () -> new Dijkstra(dg, 0).ops);
            run("DSU-none", i, () -> chain(n, false, false));
            run("DSU-compress", i, () -> chain(n, true, false));
            run("DSU-rank", i, () -> chain(n, false, true));
            run("DSU-both", i, () -> chain(n, true, true));
        }
        out.close();
        System.out.println("Done. results/tables/results.csv");
    }

    static long chain(int n, boolean compress, boolean rank) {
        DSU d = new DSU(n, compress, rank);
        for (int i = 0; i + 1 < n; i++) d.union(i + 1, i);
        for (int i = 0; i < n; i++) d.find(0);
        return d.finds + d.steps;
    }

    static void run(String name, int i, Supplier<Long> task) throws IOException {
        int inner = 100000 / E[i];
        for (int w = 0; w < 3 * inner; w++) task.get();
        long total = 0, ops = 0;
        for (int r = 0; r < REPEATS; r++) {
            long t0 = System.nanoTime();
            for (int k = 0; k < inner; k++) ops = task.get();
            total += (System.nanoTime() - t0) / inner;
        }
        out.write(name + "," + NAMES[i] + "," + V[i] + "," + E[i] + "," + (total / REPEATS) + "," + ops + "\n");
    }
}
