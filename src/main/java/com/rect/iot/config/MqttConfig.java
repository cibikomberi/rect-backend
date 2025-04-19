package com.rect.iot.config;

import com.rect.iot.model.events.MqttMessageEvent;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    @Value("${MQTT_BROKER_URL}")
    private String BROKER_URL;
    @Value("${MQTT_BROKER_URL}")
    private String CLIENT_ID;

    @Bean
    MqttPahoClientFactory mqttClientFactory() {
        System.out.println(BROKER_URL);
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{BROKER_URL});
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    MqttPahoMessageDrivenChannelAdapter inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(CLIENT_ID, mqttClientFactory(), "rect/#");
        adapter.setCompletionTimeout(5000);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    MessageHandler handler(ApplicationEventPublisher eventPublisher) {
        return message -> {
            eventPublisher.publishEvent(new MqttMessageEvent((String) message.getHeaders().get("mqtt_receivedTopic"),(String)  message.getPayload()));
        };
    }


    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    MessageHandler mqttOutbound() {
        MqttPahoMessageHandler handler =
                new MqttPahoMessageHandler(CLIENT_ID + "-out", mqttClientFactory());
        handler.setAsync(true);
        handler.setDefaultTopic("topic1");
        return handler;
    }

    @Bean
    MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
}
