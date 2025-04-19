package com.rect.iot.model.events;

public class MqttMessageEvent {
    private final String topic;
    private final String payload;

    public MqttMessageEvent( String topic, String payload) {
        this.topic = topic;
        this.payload = payload;
    }

    public String getTopic() {
        return topic;
    }

    public String getPayload() {
        return payload;
    }
}
