package com.bryan.rpc.common.serializer;

import org.nustaq.serialization.FSTConfiguration;

public class FstSerializer implements Serializer {
    //--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.math=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.sql/java.sql=ALL-UNNAMED --add-opens java.base/java.util.concurrent=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED
    private static final FSTConfiguration fstConfig = FSTConfiguration.createDefaultConfiguration();

    public byte[] serialize(Object result) {
        return fstConfig.asByteArray(result);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return clazz.cast(fstConfig.asObject(bytes));
    }
}
