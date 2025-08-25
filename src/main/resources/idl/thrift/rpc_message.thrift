//G:\springcloud\rpc-framework\simple-rpc\src\main\thrift>thrift --gen java -out G:\springcloud\rpc-framework\simple-rpc\src\main\java rpc_message.thrift
namespace java com.bryan.rpc.common.model.thrift

struct ThriftDynamicParam {
    1: string type,
    2: binary value
}

struct ThriftRpcRequest {
    1: string className,
    2: string methodName,
    3: list<ThriftDynamicParam> params,
    4: string requestId,
    5: string serviceName
}

struct ThriftRpcResponse {
    1: binary value,
    2: string error,
    3: string requestId
}