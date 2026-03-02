package mate.academy;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

public class AsyncRequestProcessor {
    private final Executor executor;
    private final ConcurrentMap<String, CompletableFuture<UserData>> cache;

    public AsyncRequestProcessor(Executor executor) {
        this.executor = executor;
        this.cache = new ConcurrentHashMap<>();
    }

    public CompletableFuture<UserData> processRequest(String userId) {
        return cache.computeIfAbsent(userId, key -> CompletableFuture.supplyAsync(
                () -> getUserData(userId),
                executor
        ));
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
