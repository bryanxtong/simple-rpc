package com.bryan.rpc.server;

import com.bryan.rpc.common.model.RpcRequest;
import com.bryan.rpc.common.utils.JacksonTypeUtils;
import java.lang.reflect.Method;
import java.time.*;
import java.util.Objects;

/**
 * Jackson/MessagePack serialization date time problem for java 8 time library
 * and used for Jackson/MessagePack serialization to support java 8 time library
 */
public class JacksonNonIDLRpcMethodInvoker extends NonIDLRpcMethodInvoker {

    /**
     * As the result object type in RpcResponse[Object result], the type is missing during serialization
     * @param request
     * @param serviceRegistry
     * @return
     * @throws Exception
     */
    @Override
    public Object invoke(RpcRequest request, ObjectServiceRegistry serviceRegistry) throws Exception {
        //processJava8DateTypes(request);
        //process params values deserialization for jackson/messagepack jackson
        Object service = serviceRegistry.getService(request.getServiceName());
        if (service == null) {
            throw new IllegalArgumentException("service not found" + request.getServiceName());
        }
        Class<?> serviceClass = service.getClass();
        String methodName = request.getMethodName();
        Object[] params = request.getParams();
        Class<?>[] paramTypes = request.getParamTypes();
        Method method = serviceClass.getMethod(methodName, paramTypes);
        //no serialization for messagepack and jackson as they are different, only converter
        Object[] newParams = JacksonTypeUtils.deserializeMethodParams(params, method);
        //change params to their original type
        request.setParams(newParams);
        return super.invoke(request, serviceRegistry);

    }

    @Deprecated
    public void  processJava8DateTypes(RpcRequest request) throws Exception {
        Objects.requireNonNull(request, "RpcRequest must not be null");
        Object[] params = request.getParams();
        Class<?>[] paramTypes = request.getParamTypes();

        if (params == null || paramTypes == null || params.length != paramTypes.length) {
            return;
        }

        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            Class<?> expectedType = paramTypes[i];

            if(expectedType == Instant.class){
                Object obj = Instant.parse((String) param);
                params[i] =  obj;
            }if(expectedType == LocalDate.class){
                Object obj = LocalDate.parse((String) param);
                params[i] =  obj;
            }if(expectedType == LocalDateTime.class){
                Object obj = LocalDateTime.parse((String) param);
                params[i] =  obj;
            }if(expectedType == ZonedDateTime.class){
                Object obj = ZonedDateTime.parse((String) param);
                params[i] =  obj;
            }if(expectedType == OffsetDateTime.class){
                Object obj = OffsetDateTime.parse((String) param);
                params[i] =  obj;
            }if(expectedType == OffsetTime.class){
                Object obj = OffsetTime.parse((String) param);
                params[i] =  obj;
            }if(expectedType == Duration.class){
                Object obj = Duration.parse((String) param);
                params[i] =  obj;
            }if(expectedType == Period.class){
                Object obj = Period.parse((String) param);
                params[i] =  obj;
            }
        }
    }
}
