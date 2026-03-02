package mate.academy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AsyncRequestProcessor {
    private final Executor executor;
    private final ConcurrentMap<String, UserData> cache = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public AsyncRequestProcessor(Executor executor) {
        this.executor = executor;
    }

    public CompletableFuture<UserData> processRequest(String userId) {
        lock.readLock().lock();
        try {
            if (cache.containsKey(userId)) {
                return CompletableFuture.completedFuture(cache.get(userId));
            }
        } finally {
            lock.readLock().unlock();
        }
        CompletableFuture<UserData> future = CompletableFuture.supplyAsync(
                () -> getUserData(userId),
                executor
        );
        future.thenAcceptAsync(userData -> {
            lock.writeLock().lock();
            try {
                cache.put(userId, userData);
            } finally {
                lock.writeLock().unlock();
            }
        }, executor);
        return future;
    }

    private UserData getUserData(String userId) {
        try {
            Thread.sleep(100 + (long)(Math.random() * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return new UserData(userId, "Details for " + userId);
    }
}
