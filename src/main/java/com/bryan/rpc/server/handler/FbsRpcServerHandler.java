package com.bryan.rpc.server.handler;

import com.bryan.rpc.common.model.fbs.FbsRpcRequest;
import com.bryan.rpc.common.model.fbs.FbsRpcResponse;
import com.bryan.rpc.common.utils.FbsUtils;
import com.bryan.rpc.server.ObjectServiceRegistry;
import com.bryan.rpc.server.FbsRpcMethodInvoker;
import com.bryan.rpc.server.RpcMethodInvoker;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class FbsRpcServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FbsRpcServerHandler.class);
    private final ObjectServiceRegistry objectServiceRegistry;

    public FbsRpcServerHandler(ObjectServiceRegistry objectServiceRegistry) {
        this.objectServiceRegistry = objectServiceRegistry;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("FbsRpcServerHandler received：{}", msg);
        if(msg instanceof FbsRpcRequest request) {

            try{
                RpcMethodInvoker<FbsRpcRequest,Object> methodInvoker = new FbsRpcMethodInvoker();
                Object resp = methodInvoker.invoke(request, objectServiceRegistry);

                if (FbsRpcResponse.class.isAssignableFrom(resp.getClass())) {
                    FbsRpcResponse response = (FbsRpcResponse) resp;
                    ctx.writeAndFlush(response);
                }else if(CompletableFuture.class.isAssignableFrom(resp.getClass())){
                    CompletableFuture<FbsRpcResponse> futureResult = (CompletableFuture<FbsRpcResponse>) resp;
                    futureResult.whenComplete((result, e) -> {
                        if(e == null){
                            ctx.writeAndFlush(result);
                        }else{
                            ctx.writeAndFlush(FbsUtils.createRpcResponse("".getBytes(), "Rpc error: "+e.getMessage(), request.requestId()));
                        }
                    });
                }
            } catch (Exception e) {
                ctx.writeAndFlush(FbsUtils.createRpcResponse("".getBytes(),"Rpc error: "+e.getMessage(), request.requestId()));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
