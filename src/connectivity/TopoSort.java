package connectivity;

import graph.Edge;
import graph.Graph;
import java.util.ArrayDeque;
import java.util.Queue;

public class TopoSort {
    public final int[] order;
    public final boolean isDag;
    public long ops = 0;

    public TopoSort(Graph g) {
        int[] indeg = new int[g.n];
        for (Edge e : g.edges) { indeg[e.v]++; ops++; }
        Queue<Integer> queue = new ArrayDeque<>();
        for (int v = 0; v < g.n; v++) if (indeg[v] == 0) queue.add(v);
        int[] result = new int[g.n];
        int k = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            result[k++] = u;
            for (Edge e : g.adj.get(u)) {
                ops++;
                if (--indeg[e.v] == 0) queue.add(e.v);
            }
        }
        isDag = k == g.n;
        order = isDag ? result : null;
    }
}
