package com.bryan.rpc.common.serializer;

import com.bryan.rpc.common.model.fbs.FbsRpcRequest;
import com.bryan.rpc.common.model.fbs.FbsRpcResponse;

import java.nio.ByteBuffer;

public class FbsSerializer implements Serializer {
    @Override
    public <T> byte[] serialize(T obj) throws Exception {
        if (obj instanceof FbsRpcRequest request) {
            ByteBuffer byteBuffer = request.getByteBuffer();
            byte[] value = new byte[byteBuffer.remaining()];
            byteBuffer.get(value);
            return value;
        } else if (obj instanceof FbsRpcResponse response) {
            ByteBuffer byteBuffer = response.getByteBuffer();
            byte[] value = new byte[byteBuffer.remaining()];
            byteBuffer.get(value);
            return value;
        } else {
            throw new UnsupportedOperationException("invalid type" + obj.getClass());
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int messageType = getMessageType(clazz);
        switch (messageType) {
            case 1:
                return clazz.cast(FbsRpcRequest.getRootAsFbsRpcRequest(buffer));
            case 2:
                return clazz.cast(FbsRpcResponse.getRootAsFbsRpcResponse(buffer));
            default:
                throw new IllegalArgumentException("invalid type" + messageType);
        }
    }

    private int getMessageType(Class<?> clazz) {
        if (clazz.isAssignableFrom(FbsRpcRequest.class)) {
            return 1;
        } else if (clazz.isAssignableFrom(FbsRpcResponse.class)) {
            return 2;
        }
        throw new IllegalArgumentException("invalid type" + clazz);
    }
}
