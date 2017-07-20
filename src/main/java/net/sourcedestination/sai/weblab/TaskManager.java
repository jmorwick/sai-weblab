package net.sourcedestination.sai.weblab;

import net.sourcedestination.sai.reporting.Log;
import net.sourcedestination.sai.task.Task;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Created by jmorwick on 7/19/17.
 */
@Repository
public class TaskManager {

    private int nextTaskId = 1;
    private Map<Integer,Task> trackedTasks = new HashMap<>();
    private Map<Integer,CompletableFuture<Log>> taskFutures = new HashMap<>();

    public synchronized int addTask(Task t) {
        trackedTasks.put(nextTaskId, t);
        CompletableFuture<Log> f = CompletableFuture.supplyAsync(t);
        return nextTaskId++;
    }

    public synchronized Task getTask(int id) {
        return trackedTasks.get(id);
    }

    public synchronized Log getResult(int id) {
        if(taskFutures.containsKey(id)) {
            return taskFutures.get(id).getNow(null);
        }
        return null;
    }

    public synchronized Set<Integer> getTrackedTaskIds() {
        return new HashSet<Integer>(trackedTasks.keySet());
    }
}
