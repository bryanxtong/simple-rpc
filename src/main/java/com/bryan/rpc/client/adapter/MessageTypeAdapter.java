package com.bryan.rpc.client.adapter;


import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.model.RpcResponse;

/**
 * adaptor the RpcRequest and RpcResponse to real type for a serialization way like Thrift, protobuf and FlatBuffers
 */
public interface MessageTypeAdapter<Req,Rsp> {

    Req adapterRequest(RpcRequest request);

    RpcResponse adapterResponse(Rsp response);
}
