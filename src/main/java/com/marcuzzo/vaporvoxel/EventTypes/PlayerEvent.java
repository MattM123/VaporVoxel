package com.marcuzzo.vaporvoxel.EventTypes;

import com.marcuzzo.vaporvoxel.Events.ChunkTransitionEvent;
import com.marcuzzo.vaporvoxel.Events.RegionTransitionEvent;
import javafx.event.Event;
import javafx.event.EventType;

public class PlayerEvent extends Event {
    public static final EventType<ChunkTransitionEvent> CHUNK_TRANSITION = new EventType<>(ChunkTransitionEvent.CHUNK_TRANSITION);
    public static final EventType<RegionTransitionEvent> REGION_TRANSITION = new EventType<>(RegionTransitionEvent.REGION_TRANSITION);

    public PlayerEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
