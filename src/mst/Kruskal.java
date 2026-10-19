package mst;

import dsu.DSU;
import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.List;

public class Kruskal {

    public static MstResult run(Graph g) {
        List<Edge> sorted = new ArrayList<>(g.edges);
        long[] cmp = {0};
        sorted.sort((a, b) -> { cmp[0]++; return Integer.compare(a.w, b.w); });
        DSU dsu = new DSU(g.n);
        long weight = 0;
        int used = 0;
        for (Edge e : sorted) {
            if (used == g.n - 1) break;
            if (dsu.union(e.u, e.v)) { weight += e.w; used++; }
        }
        return new MstResult(weight, used, used == g.n - 1, cmp[0] + dsu.finds + dsu.steps);
    }
}
