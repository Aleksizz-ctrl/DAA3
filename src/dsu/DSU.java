package dsu;

public class DSU {
    private final int[] parent, rank;
    private final boolean compress, byRank;
    public long finds = 0, steps = 0;
    public int sets;

    public DSU(int n) { this(n, true, true); }

    public DSU(int n, boolean compress, boolean byRank) {
        this.compress = compress;
        this.byRank = byRank;
        parent = new int[n];
        rank = new int[n];
        sets = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    public int find(int x) {
        finds++;
        int root = x;
        while (parent[root] != root) { root = parent[root]; steps++; }
        while (compress && parent[x] != root) { int next = parent[x]; parent[x] = root; x = next; }
        return root;
    }

    public boolean union(int a, int b) {
        int ra = find(a), rb = find(b);
        if (ra == rb) return false;
        if (byRank && rank[ra] < rank[rb]) { int t = ra; ra = rb; rb = t; }
        parent[rb] = ra;
        if (byRank && rank[ra] == rank[rb]) rank[ra]++;
        sets--;
        return true;
    }
}
