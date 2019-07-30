package org.zzy.initiator.sort;

import java.util.ArrayList;
import java.util.List;

/**
 * 有向无环图的拓扑排序
 * @作者 ZhouZhengyi
 * @创建日期 2019/7/30
 */
public class Graph {

    /**
     * 顶点数
     */
    private int mVerticeCount;

    /**
     * 邻接表
     */
    private List<Integer>[] mAdj;
    public Graph(int verticeCount){
        this.mVerticeCount = verticeCount;
        mAdj = new ArrayList[mVerticeCount];
        for (int i = 0 ; i < mVerticeCount;i++){
            mAdj[i] = new ArrayList<>();
        }
    }

    /**
     * 添加边
     * @param u from
     * @param v to
     */
    public void addEdge(int u,int v){
        mAdj[u].add(v);
    }


}
