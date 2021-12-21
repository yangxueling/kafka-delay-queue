package com.yxlisv.kafka.util;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka工具类
 *
 * @author yangxueling
 * @date 2021/12/21
 */
public class KafkaUtil {

    /**
     * logger
     */
    private final static Logger log = LoggerFactory.getLogger(KafkaUtil.class);


    /**
     * 延迟处理
     *
     * @param record        消息内容
     * @param delayDuration 延迟时长（毫秒）
     * @author yangxueling
     * @date 2021/12/21
     */
    @SuppressWarnings("all")
    public static void delay(ConsumerRecord record, long delayDuration) {
        try {
            //执行时间
            long executeTime = record.timestamp() + delayDuration;
            //未到执行时间，延迟执行
            if (System.currentTimeMillis() < executeTime) {
                delayDuration = executeTime - System.currentTimeMillis();
                log.debug("delay consumer kafka msg，delayDuration=" + delayDuration + ", msg=" + record.value());
                Thread.sleep(delayDuration);
            }
        } catch (Exception ignored) {
        }
    }
}