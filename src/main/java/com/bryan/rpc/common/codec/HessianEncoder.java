/*
package com.example.rpc.hessian;

import com.caucho.hessian.io.Hessian2Output;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws IOException {
        System.out.println("HessianEncoder " + msg.getClass().getName());
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Hessian2Output output = new Hessian2Output(bos);
            output.writeObject(msg);
            output.flush();
            byte[] bytes = bos.toByteArray();
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
            output.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
*/

package com.bryan.rpc.common.codec;

import com.bryan.rpc.common.serializer.HessianSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

public class HessianEncoder extends MessageToByteEncoder<Object> {
    private static final HessianSerializer serializer = new HessianSerializer();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws IOException {
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}

