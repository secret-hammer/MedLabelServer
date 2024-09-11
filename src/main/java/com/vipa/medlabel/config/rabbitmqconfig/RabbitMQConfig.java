package com.vipa.medlabel.config.rabbitmqconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnsCallback;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;

@Slf4j
@Configuration
public class RabbitMQConfig {

    private static final Logger logger = LoggerFactory.getLogger("rabbitmq_log");

    @Bean
    public Queue medLabelImageConvertQueue() {
        return new Queue("medlabel_image_convert_queue", true);
    }

    @Bean
    public Queue imageConvertTaskFinishCallbackQueue() {
        return new Queue("medlabel_image_convert_task_finish_callback_queue", true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("task_exchange", true, false);
    }

    @Bean
    public Binding medlabelImageConvertQueueBinding(Queue medLabelImageConvertQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(medLabelImageConvertQueue).to(directExchange).with("image_convert");
    }

    @Bean
    public Binding imageConvertTaskFinishCallbackQueueBinding(Queue imageConvertTaskFinishCallbackQueue,
            DirectExchange directExchange) {
        return BindingBuilder.bind(imageConvertTaskFinishCallbackQueue).to(directExchange)
                .with("image_convert_task_finish_callback");
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("10.214.211.209");
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("123456");
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setConfirmCallback(confirmCallback());
        rabbitTemplate.setReturnsCallback(returnCallback());
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConfirmCallback confirmCallback() {
        return new ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (ack) {
                    logger.info("Message successfully delivered to exchange");
                } else {
                    logger.error("Message delivery to exchange failed: " + cause);
                }
            }
        };
    }

    @Bean
    public ReturnsCallback returnCallback() {
        return new ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                logger.error("Message returned: " + new String(returnedMessage.getMessage().getBody()) +
                        ", replyCode: " + returnedMessage.getReplyCode() +
                        ", replyText: " + returnedMessage.getReplyText() +
                        ", exchange: " + returnedMessage.getExchange() +
                        ", routingKey: " + returnedMessage.getRoutingKey());
            }
        };
    }
}
