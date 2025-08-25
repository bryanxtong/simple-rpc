/*
package com.example.rpc.hessian;

import com.caucho.hessian.io.Hessian2Input;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class HessianDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[length];
        in.readBytes(data);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            Hessian2Input input = new Hessian2Input(bis);
            Object obj = input.readObject();
            out.add(obj);
            input.close();
        } catch (IOException e) {
            throw new DecoderException(e);
        }
    }
}
*/
package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.serializer.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.io.IOException;
import java.util.List;

/**
 * Use alibaba as It support java 8+ time library
 */
public class HessianDecoder extends MessageToMessageDecoder<ByteBuf> {
    private static final HessianSerializer serializer = new HessianSerializer();
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws IOException {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[length];
        in.readBytes(data);

        Object obj = serializer.deserialize(data, Object.class);
        out.add(obj);
    }
}
