# Introduce
    Realize the delayed consumption of Kafka messages


# Steps for usage
## Step1、
    <dependency>
        <groupId>com.yxlisv</groupId>
        <artifactId>kafka-delay-queue</artifactId>
        <version>1.0.2</version>
    </dependency>
    
## Step2、
    @KafkaDelayQueue(delayDuration = 1000)
    
## Step3、
    Method add param ConsumerRecord

## Example
    @KafkaDelayQueue(delayDuration = 1000)
    @KafkaListener(
            topics = {"testYxlTopic"},
            containerFactory = "kafkaListenerFactory"
    )
    public void processDelayMsg(ConsumerRecord<String, String> record, Acknowledgment ack) throws JsonProcessingException {

        long createTime = record.timestamp();
        ack.acknowledge();
    }