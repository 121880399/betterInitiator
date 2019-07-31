package org.zzy.initiator.sort;

import android.util.ArraySet;

import org.zzy.initiator.task.Task;
import org.zzy.initiator.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @作者 ZhouZhengyi
 * @创建日期 2019/7/30
 */
public class TaskSortUtil {

    /**
     * 使用有向无环图排序
     */
    public static synchronized List<Task> getSortResult(List<Task> originTasks,List<Class<? extends Task>> clazzTasks){
        long startTime = System.currentTimeMillis();

        Set<Integer> dependSet = new ArraySet<>();
        Graph graph = new Graph(originTasks.size());

        for (int i = 0; i < originTasks.size(); i++) {
            Task task = originTasks.get(i);
            //如果任务已经发送或者没有依赖，跳过遍历下一个Task
            if (task.isSend() || task.dependsOn() == null || task.dependsOn().size() == 0) {
                continue;
            }
            for(Class cls :task.dependsOn()){
                int indexOfDepend = getIndexOfTask(originTasks,clazzTasks,cls);
                if(indexOfDepend < 0){
                    throw new IllegalStateException(task.getClass().getSimpleName() +
                            " depends on " + cls.getSimpleName() + " can not be found in task list ");
                }
                dependSet.add(indexOfDepend);
                graph.addEdge(indexOfDepend,i);
            }
        }
        List<Integer> indexList = graph.topologicalSort();
        List<Task> newTasksAll = getResultTasks(originTasks,dependSet,indexList);
        LogUtils.i("task analyse cost makeTime " + (System.currentTimeMillis() - startTime));
        return newTasksAll;
    }

    private static List<Task> getResultTasks(List<Task> originTask,Set<Integer> dependSet,List<Integer> indexList){
        List<Task> newTasksALL = new ArrayList<>(originTask.size());
        //依赖
        List<Task> newTaskDepended = new ArrayList<>();
        //没有依赖
        List<Task> newTaskWithOutDepend = new ArrayList<>();
        for(int index : indexList){
            if(dependSet.contains(index)){
                newTaskDepended.add(originTask.get(index));
            }else{
                Task task = originTask.get(index);
                newTaskWithOutDepend.add(task);
            }
        }
        //顺序:被依赖的--->没有依赖的
        newTasksALL.addAll(newTaskDepended);
        newTasksALL.addAll(newTaskWithOutDepend);
        return newTasksALL;
    }

    /**
     * 得到任务在任务列表中的index
     */
    private static int getIndexOfTask(List<Task> originTasks,List<Class<? extends Task>> clazzTasks,Class cls){
        //得到cls在链表中第一次出现的位置
        int index =clazzTasks.indexOf(cls);
        if(index >= 0){
            return index;
        }

        //保护性代码，如果没在clazzTasks中找到，则从originTasks中去寻找
        int size = originTasks.size();
        for(int i = 0 ; i < size; i++){
            if(cls.getSimpleName().equals(originTasks.get(i).getClass().getSimpleName())){
                return  i;
            }
        }
        return index;
    }
}
