package mst;

public class MstResult {
    public final long weight,ops;
    public final int  edges;
    public final boolean connected;

    public MstResult(long weight,int edges,boolean connected,long ops){
        this.weight=weight;
        this.edges=edges;
        this.connected=connected;
        this.ops=ops;
    }
}
