package com.bryan.rpc.common.serializer;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * use alibaba version for java 8+ time library support
 */
public class HessianSerializer implements Serializer {

    public byte[] serialize(Object object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Hessian2Output out = new Hessian2Output(bos);
            out.writeObject(object);
            out.flush();
            out.close();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new IOException("Hessian serialization error", e);
        }
    }

    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            Hessian2Input in = new Hessian2Input(bis);
            Object o = in.readObject();
            in.close();
            return clazz.cast(o);
        } catch (IOException e) {
            throw new IOException("Hessian deserialization error", e);
        }
    }
}
