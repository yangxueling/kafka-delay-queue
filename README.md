# Introduce
    Kafka consumer message delayed
    
    
# Steps for usage
## Step1、
    <dependency>
        <groupId>com.yxlisv</groupId>
        <artifactId>kafka-delay-queue</artifactId>
        <version>1.0.2</version>
    </dependency>
    
## Step2、
    Consumer set ack mode as MANUAL_IMMEDIATE
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);   
     
## Step3、
    Consumer listener method add param ConsumerRecord
    
## Step4、
    Add @KafkaDelayQueue for Consumer listener method
    @KafkaDelayQueue(delayDuration = 10000)
    
## Example Consumer listener method
    @KafkaDelayQueue(delayDuration = 10000)
    @KafkaListener(
            topics = {"testYxlTopic"},
            containerFactory = "kafkaListenerFactory"
    )
    public void processDelayMsg(ConsumerRecord<String, String> record, Acknowledgment ack) {

        long createTime = record.timestamp();
        ack.acknowledge();
    }
    
## Example KafkaListenerContainerFactory
    @Resource
    private Environment env;
    
    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerFactory() {

        //set props
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env.getProperty("spring.kafka.bootstrap-servers"));
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("spring.kafka.consumer.group-id"));
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, env.getProperty("spring.kafka.consumer.auto-offset-reset"));
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        propsMap.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 1900800000);
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        //create consumer Factory
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(propsMap));
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(60000);
        factory.setBatchListener(false);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        //consumer faild handler
        factory.setErrorHandler(
                new SeekToCurrentErrorHandler((consumerRecord, e) -> {
                    log.info("kafka consumer faild: record={}, error={}", consumerRecord, e.getMessage());
                }, new FixedBackOff(1000L, 3L))
        );
        return factory;
    }
    
    