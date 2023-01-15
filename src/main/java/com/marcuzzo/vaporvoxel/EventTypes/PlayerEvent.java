package com.marcuzzo.vaporvoxel.EventTypes;

import com.marcuzzo.vaporvoxel.Events.ChunkTransitionEvent;
import javafx.event.EventType;

public class PlayerEvent {
    public static final EventType<ChunkTransitionEvent> CHUNK_TRANSITION = new EventType<>(ChunkTransitionEvent.ANY);

    public PlayerEvent() {
        super();
    }
}
