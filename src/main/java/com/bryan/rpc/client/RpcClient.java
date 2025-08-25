package com.bryan.rpc.client;

import java.util.concurrent.CompletableFuture;

public interface RpcClient<TRequest,TResponse> {

    public TResponse send(TRequest req) throws Exception;

    public CompletableFuture<TResponse> sendAsync(TRequest req) throws Exception;

    public void shutdown();
}
