package net.sourcedestination.sai.weblab;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.reporting.GraphRetrieverListener;
import net.sourcedestination.sai.reporting.Log;
import net.sourcedestination.sai.reporting.QueryRecord;
import net.sourcedestination.sai.retrieval.GraphRetriever;
import net.sourcedestination.sai.task.Task;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by jmorwick on 7/19/17.
 */
@Repository
public class TaskManager {

    private int nextTaskId = 1;
    private Map<Integer,Task> trackedTasks = new HashMap<>();
    private Map<Integer,CompletableFuture<Log>> taskFutures = new HashMap<>();
    private Map<Integer,Log> taskLogs = new HashMap<>();
    private Map<Integer,Date> startTimes = new HashMap<>();
    private Map<Integer,Date> endTimes = new HashMap<>();

    public synchronized int addTask(Task<Log> t) {
        final int id = nextTaskId;
        final TaskManager manager = this;
        startTimes.put(id, new Date());
        trackedTasks.put(id, t);
        CompletableFuture<Log> f = CompletableFuture.supplyAsync(t)
                .thenApply(log -> { // record the log here when finished
                    synchronized(manager) {
                        endTimes.put(id, new Date());
                        taskLogs.put(id, log);
                    }
                    return log;
                });
        taskFutures.put(nextTaskId, f);
        return nextTaskId++;
    }

    public synchronized Task getTask(int id) {
        return trackedTasks.get(id);
    }

    public synchronized Log getResult(int id) {
        return taskLogs.get(id);
    }

    public synchronized int addGraphRetrievalTask(DBInterface db, GraphRetriever retriever, Graph q, int maxResults) {
        Log log = new Log(retriever.getClass().getName());
        final GraphRetriever wretriever = new GraphRetrieverListener(retriever, log);
        final TaskManager manager = this;
        return addTask(new Task<Log>() {
            private AtomicInteger progress = new AtomicInteger(0);
            public Log get() {
                QueryRecord r = new QueryRecord(q, db);
                wretriever.retrieve(db,q).forEach(
                        gid -> {
                            r.recordRetrievedGraphID((int)gid);
                            progress.incrementAndGet();
                        }
                );
                log.recordQueryRecord(r);
                progress.set(maxResults);
                return log;
            }

            @Override
            public String getTaskName() { return retriever.getClass().getName(); }

            @Override
            public int getProgressUnits() { return progress.get(); }

            @Override
            public int getTotalProgressUnits() { return maxResults; }

        });
    }

    public synchronized Set<Integer> getTrackedTaskIds() {
        return new HashSet<Integer>(trackedTasks.keySet());
    }
}
