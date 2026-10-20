package mst;

import graph.Edge;
import graph.Graph;
import java.util.PriorityQueue;

public class Prim {

    public static MstResult run(Graph g) {
        boolean[] inTree = new boolean[g.n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.add(new int[]{0, 0});
        long weight = 0, ops = 0;
        int count = 0;
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            if (inTree[top[1]]) continue;
            inTree[top[1]] = true;
            weight += top[0];
            count++;
            for (Edge e : g.adj.get(top[1])) {
                ops++;
                if (!inTree[e.v]) { pq.add(new int[]{e.w, e.v}); ops++; }
            }
        }
        return new MstResult(weight, count - 1, count == g.n, ops);
    }
}
