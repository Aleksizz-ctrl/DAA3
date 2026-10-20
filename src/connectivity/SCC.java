package connectivity;

import graph.Edge;
import graph.Graph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SCC {
    public final int[] comp;
    public int count = 0;
    public long ops = 0;
    private final Graph g;
    private final List<List<Integer>> rev = new ArrayList<>();
    private final boolean[] seen;
    private final int[] order;
    private int k = 0;

    public SCC(Graph g) {
        this.g = g;
        int n = g.n;
        for (int i = 0; i < n; i++) rev.add(new ArrayList<>());
        for (Edge e : g.edges) rev.get(e.v).add(e.u);
        seen = new boolean[n];
        order = new int[n];
        comp = new int[n];
        Arrays.fill(comp, -1);
        for (int v = 0; v < n; v++) if (!seen[v]) dfs1(v);
        for (int i = n - 1; i >= 0; i--)
            if (comp[order[i]] == -1) { dfs2(order[i]); count++; }
    }

    private void dfs1(int u) {
        seen[u] = true;
        for (Edge e : g.adj.get(u)) { ops++; if (!seen[e.v]) dfs1(e.v); }
        order[k++] = u;
    }

    private void dfs2(int u) {
        comp[u] = count;
        for (int v : rev.get(u)) { ops++; if (comp[v] == -1) dfs2(v); }
    }
}
