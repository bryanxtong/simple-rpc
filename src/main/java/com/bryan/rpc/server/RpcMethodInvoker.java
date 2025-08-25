package com.bryan.rpc.server;

/**
 * Invoke the local method for a service which registered in ObjectServiceRegistry for Rpc Server
 * @param <TReq>
 * @param <TRes>
 */
public interface RpcMethodInvoker<TReq,TRes> {

    public TRes invoke(TReq request, ObjectServiceRegistry serviceRegistry) throws Exception;

    //public CompletableFuture<TRes> invokeAsync(TReq request, ObjectServiceRegistry serviceRegistry) throws Exception;

}
