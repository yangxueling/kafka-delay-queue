package com.yxl.kafka.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Kafka延迟队列
 * 消费者方法必须包含参数：ConsumerRecord
 * 设置消费工厂enable_auto_commit=false
 * 消息确认模式: ContainerProperties.AckMode.MANUAL_IMMEDIATE
 *
 * @author yangxueling
 * @date 2021/12/17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface KafkaDelayQueue {

    /**
     * 延迟时长(毫秒)
     */
    long delayDuration();
}