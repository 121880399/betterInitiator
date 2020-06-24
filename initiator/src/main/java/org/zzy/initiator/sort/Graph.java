package org.zzy.initiator.sort;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

/**
 * 有向无环图的拓扑排序
 * 这里使用邻接表而不是邻接矩阵的数据存储结构来避免空间的浪费
 * 因为可能会存在大量没有依赖的Task
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
     * 添加边，一条边由两个顶点组成
     * @param u from
     * @param v to
     */
    public void addEdge(int u,int v){
        mAdj[u].add(v);
    }

    public Vector<Integer> topologicalSort(){
        //用于记录每个节点的入度
        int indegree[] = new int[mVerticeCount];
        //初始化所有节点的入度
        for (int i = 0 ; i < mVerticeCount ; i++){
            ArrayList<Integer> temp = (ArrayList<Integer>) mAdj[i];
            for(int node : temp){
                indegree[node]++;
            }
        }

        //找出所有入度为0的节点，放入队列中
        Queue<Integer> queue = new LinkedList<>();
        for(int i = 0 ; i < mVerticeCount ;i++){
            if(indegree[i] == 0){
                queue.add(i);
            }
        }
        //记录节点数
        int count = 0;
        //从队列中取节点，并且遍历该节点之后的节点加入到Vector中
        //Vector是线程安全的，并且容量是动态扩展的
        Vector<Integer> order = new Vector<>();
        while(!queue.isEmpty()){
            //从队列中获取入度为0的节点
            int u = queue.poll();
            order.add(u);
            //遍历该节点的相邻节点
            for(int node : mAdj[u]){
                //节点的入度减一，如果发现入度为0，加入到队列中
                if(--indegree[node] == 0){
                    queue.add(node);
                }
            }
            count++;
        }
        //如果取出的节点数量不等于所有节点数量，说明存在环
        //原因是，有环的情况下，环内各节点的入度不能消减为0，所以count值只能环外的节点个数
        if(count != mVerticeCount){
            throw new IllegalStateException("Exists a cycle in the graph");
        }
        return order;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(System.getProperty("line.separator"));
        for ( int i =0 ;i<mAdj.length;i++){
            stringBuilder.append(i);
            for (int node :mAdj[i]) {
                stringBuilder.append("->");
                stringBuilder.append(node);
            }
            stringBuilder.append(System.getProperty("line.separator"));
        }
        return stringBuilder.toString();
    }
}
