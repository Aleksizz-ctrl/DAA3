package graph;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GraphGen {

    static void add(Graph g, Set<Long> used, long key, int u, int v, Random r) {
        if (used.add(key)) g.addEdge(u, v, 1 + r.nextInt(100));
    }

    public static Graph connected(int n, int m, long seed) {
        Random r = new Random(seed);
        Graph g = new Graph(n, false);
        Set<Long> used = new HashSet<>();
        for (int i = 1; i < n; i++) {
            int j = r.nextInt(i);
            add(g, used, (long) j * n + i, i, j, r);
        }
        while (g.edges.size() < m) {
            int u = r.nextInt(n), v = r.nextInt(n);
            if (u != v) add(g, used, (long) Math.min(u, v) * n + Math.max(u, v), u, v, r);
        }
        return g;
    }

    public static Graph directed(int n, int m, long seed) {
        Random r = new Random(seed);
        Graph g = new Graph(n, true);
        Set<Long> used = new HashSet<>();
        while (g.edges.size() < m) {
            int u = r.nextInt(n), v = r.nextInt(n);
            if (u != v) add(g, used, (long) u * n + v, u, v, r);
        }
        return g;
    }

    public static Graph dag(int n, int m, long seed) {
        Random r = new Random(seed);
        int[] p = new int[n];
        for (int i = 0; i < n; i++) p[i] = i;
        for (int i = n - 1; i > 0; i--) {
            int j = r.nextInt(i + 1), t = p[i];
            p[i] = p[j];
            p[j] = t;
        }
        Graph g = new Graph(n, true);
        Set<Long> used = new HashSet<>();
        while (g.edges.size() < m) {
            int i = r.nextInt(n), j = r.nextInt(n);
            if (i < j) add(g, used, (long) i * n + j, p[i], p[j], r);
        }
        return g;
    }
}
