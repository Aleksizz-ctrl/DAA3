import connectivity.SCC;
import connectivity.TopoSort;
import dsu.DSU;
import graph.Edge;
import graph.Graph;
import graph.GraphGen;
import mst.Kruskal;
import mst.MstResult;
import mst.Prim;
import shortestpath.Dijkstra;
import java.util.Arrays;
import java.util.Random;

public class Tests {
    static int passed = 0, failed = 0;

    static void check(boolean cond, String name) {
        if (cond) passed++; else { failed++; System.out.println("FAILED: " + name); }
    }

    static Graph make(boolean directed, int n, int... e) {
        Graph g = new Graph(n, directed);
        for (int i = 0; i < e.length; i += 3) g.addEdge(e[i], e[i + 1], e[i + 2]);
        return g;
    }

    public static void main(String[] args) {
        testDsu();
        testMst();
        testTopoAndScc();
        testDijkstra();
        testRandom();
        System.out.println("Passed: " + passed + ", Failed: " + failed);
    }

    static void testDsu() {
        for (boolean c : new boolean[]{true, false})
            for (boolean r : new boolean[]{true, false}) {
                DSU d = new DSU(6, c, r);
                check(d.union(0, 1) && d.union(1, 2), "dsu union");
                check(!d.union(0, 2), "dsu same set");
                check(d.find(0) == d.find(2) && d.find(0) != d.find(5), "dsu find");
                check(d.sets == 4, "dsu sets count");
            }
    }

    static void testMst() {
        Graph one = new Graph(1, false);
        check(Kruskal.run(one).weight == 0 && Kruskal.run(one).connected, "kruskal single vertex");
        check(Prim.run(one).weight == 0 && Prim.run(one).connected, "prim single vertex");

        Graph disc = make(false, 4, 0, 1, 5, 2, 3, 7);
        check(!Kruskal.run(disc).connected && !Prim.run(disc).connected, "disconnected detected");

        Graph square = make(false, 4, 0, 1, 1, 1, 2, 1, 2, 3, 1, 3, 0, 1, 0, 2, 1);
        check(Kruskal.run(square).weight == 3 && Prim.run(square).weight == 3, "equal weights, many MSTs");

        Graph g = make(false, 5, 0, 1, 2, 0, 3, 6, 1, 2, 3, 1, 3, 8, 1, 4, 5, 2, 4, 7, 3, 4, 9);
        check(Kruskal.run(g).weight == 16 && Prim.run(g).weight == 16, "known MST weight");
    }

    static void testTopoAndScc() {
        TopoSort t = new TopoSort(make(true, 3, 0, 1, 1, 1, 2, 1));
        check(t.isDag && t.order[0] == 0 && t.order[2] == 2, "topo chain");

        Graph cycle = make(true, 3, 0, 1, 1, 1, 2, 1, 2, 0, 1);
        check(!new TopoSort(cycle).isDag && new TopoSort(cycle).order == null, "topo cycle detected");
        check(new SCC(cycle).count == 1, "scc one cycle");

        SCC s = new SCC(make(true, 5, 0, 1, 1, 1, 0, 1, 1, 2, 1, 2, 3, 1, 3, 4, 1, 4, 3, 1));
        check(s.count == 3 && s.comp[0] == s.comp[1] && s.comp[3] == s.comp[4] && s.comp[2] != s.comp[0], "scc groups");

        Graph one = new Graph(1, true);
        check(new TopoSort(one).isDag && new SCC(one).count == 1, "single vertex topo/scc");
    }

    static void testDijkstra() {
        Dijkstra d = new Dijkstra(make(true, 5, 0, 1, 4, 0, 2, 1, 2, 1, 2, 1, 3, 1), 0);
        check(d.dist[1] == 3 && d.dist[2] == 1 && d.dist[3] == 4, "dijkstra distances");
        check(d.dist[4] == Dijkstra.INF && d.path(4).isEmpty(), "dijkstra unreachable");
        check(d.path(3).toString().equals("[0, 2, 1, 3]"), "dijkstra path");

        boolean threw = false;
        try { new Dijkstra(make(true, 2, 0, 1, -5), 0); } catch (IllegalArgumentException e) { threw = true; }
        check(threw, "dijkstra rejects negative weights");
    }

    static void testRandom() {
        Random r = new Random(7);
        for (int i = 0; i < 300; i++) {
            int n = 2 + r.nextInt(14), maxU = n * (n - 1) / 2;
            Graph ug = GraphGen.connected(n, n - 1 + r.nextInt(maxU - (n - 1) + 1), i);
            MstResult k = Kruskal.run(ug), p = Prim.run(ug);
            check(k.connected && p.connected && k.weight == p.weight && k.edges == n - 1, "kruskal == prim");

            Graph dag = GraphGen.dag(n, r.nextInt(maxU + 1), i);
            TopoSort t = new TopoSort(dag);
            check(t.isDag && validOrder(dag, t.order), "topo valid on DAG");

            Graph dg = GraphGen.directed(n, r.nextInt(n * (n - 1) + 1), i);
            check(sccMatchesReachability(dg, new SCC(dg)), "scc matches reachability");

            long[] bf = bellmanFord(dg, 0);
            check(Arrays.equals(bf, new Dijkstra(dg, 0).dist), "dijkstra == bellman-ford");
        }
    }

    static boolean validOrder(Graph g, int[] order) {
        int[] pos = new int[g.n];
        for (int i = 0; i < order.length; i++) pos[order[i]] = i;
        for (Edge e : g.edges) if (pos[e.u] >= pos[e.v]) return false;
        return true;
    }

    static boolean sccMatchesReachability(Graph g, SCC s) {
        int n = g.n;
        boolean[][] reach = new boolean[n][n];
        for (int i = 0; i < n; i++) reach[i][i] = true;
        for (Edge e : g.edges) reach[e.u][e.v] = true;
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (reach[i][k] && reach[k][j]) reach[i][j] = true;
        for (int i = 0; i < n; i++) {
            if (s.comp[i] < 0 || s.comp[i] >= s.count) return false;
            for (int j = 0; j < n; j++)
                if ((s.comp[i] == s.comp[j]) != (reach[i][j] && reach[j][i])) return false;
        }
        return true;
    }

    static long[] bellmanFord(Graph g, int src) {
        long[] dist = new long[g.n];
        Arrays.fill(dist, Dijkstra.INF);
        dist[src] = 0;
        for (int i = 0; i < g.n; i++)
            for (Edge e : g.edges)
                if (dist[e.u] != Dijkstra.INF && dist[e.u] + e.w < dist[e.v]) dist[e.v] = dist[e.u] + e.w;
        return dist;
    }
}
