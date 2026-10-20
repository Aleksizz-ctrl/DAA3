package shortestpath;

import graph.Edge;
import graph.Graph;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class Dijkstra {
    public static final long INF = Long.MAX_VALUE;
    public final long[] dist;
    public final int[] prev;
    public long ops = 0;

    public Dijkstra(Graph g, int src) {
        for (Edge e : g.edges) if (e.w < 0) throw new IllegalArgumentException("negative weight");
        dist = new long[g.n];
        prev = new int[g.n];
        Arrays.fill(dist, INF);
        Arrays.fill(prev, -1);
        dist[src] = 0;
        PriorityQueue<long[]> pq = new PriorityQueue<>((a, b) -> Long.compare(a[0], b[0]));
        pq.add(new long[]{0, src});
        while (!pq.isEmpty()) {
            long[] top = pq.poll();
            int u = (int) top[1];
            if (top[0] > dist[u]) continue;
            for (Edge e : g.adj.get(u)) {
                ops++;
                if (dist[u] + e.w < dist[e.v]) {
                    dist[e.v] = dist[u] + e.w;
                    prev[e.v] = u;
                    pq.add(new long[]{dist[e.v], e.v});
                }
            }
        }
    }

    public List<Integer> path(int target) {
        LinkedList<Integer> p = new LinkedList<>();
        if (dist[target] != INF) for (int v = target; v != -1; v = prev[v]) p.addFirst(v);
        return p;
    }
}
