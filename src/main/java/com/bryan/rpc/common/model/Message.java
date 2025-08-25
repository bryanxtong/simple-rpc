package com.bryan.rpc.common.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * For jackson(and messagepack-jackson) type
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public abstract class Message {
}