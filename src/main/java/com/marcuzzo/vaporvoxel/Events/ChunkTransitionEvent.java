package com.marcuzzo.vaporvoxel.Events;

import javafx.event.Event;
import javafx.event.EventType;

public class ChunkTransitionEvent extends Event {

    public ChunkTransitionEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
