package com.hexszeug.werewolf.game.connections;

import com.hexszeug.werewolf.game.events.connections.ConnectedEvent;
import com.hexszeug.werewolf.game.events.connections.DisconnectedEvent;
import com.hexszeug.werewolf.game.events.connections.DisconnectingEvent;
import com.hexszeug.werewolf.game.model.player.Player;
import lombok.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

@RequiredArgsConstructor
public class ConnectionImpl implements Connection {
    private final static Duration HEARTBEAT_DELAY = Duration.ofSeconds(10);

    private final ApplicationEventPublisher eventPublisher;
    private final TaskScheduler taskScheduler;

    @Getter
    private final Player player;
    private SseEmitter sseEmitter;
    private ScheduledFuture<?> heartbeatTask;
    private int messageId = 0;
    @Getter
    private boolean completed = false;
    private boolean disconnecting = false;

    public SseEmitter initialize() {
        synchronized (this) {
            if (sseEmitter != null) throw new IllegalStateException("Connection was already initialized.");
            sseEmitter = new SseEmitter(Long.MAX_VALUE);
            sseEmitter.onCompletion(() -> internalCompleteAfterResponseCommitted(null));
            sseEmitter.onError(this::internalCompleteAfterResponseCommitted);
        }
        heartbeatTask = taskScheduler.scheduleWithFixedDelay(this::sendHeartbeat, HEARTBEAT_DELAY);
        // note that this will never fail immediately as the SseEmitter queues all messages
        // to send them after it is initialized by being returned from this method
        send(event().reconnectTime(0));
        eventPublisher.publishEvent(new ConnectedEventImpl());
        return sseEmitter;
    }

    public void send(SseEmitter.SseEventBuilder eventBuilder) {
        internalSend(eventBuilder, true);
    }

    public void complete() {
        internalCompleteBeforeResponseCommitted(null);
    }

    public void completeWithError(Throwable ex) {
        // not using SseEmitter's completeWithError as dispatch into the app server is unwanted
        internalCompleteBeforeResponseCommitted(ex);
    }

    private void sendHeartbeat() {
        if (isCompleted()) {
            heartbeatTask.cancel(true);
        } else {
            internalSend(event().comment(""), false);
        }
    }

    private void internalSend(SseEmitter.SseEventBuilder eventBuilder, boolean addId) {
        if (sseEmitter == null) throw new IllegalStateException("Connection was not initialized yet.");
        // caller should check if already completed
        if (completed) throw new IllegalStateException("Connection was already completed.");
        synchronized (this) {
            // caller cannot predict completion happening while waiting for lock
            if (completed) return;

            if (addId) {
                eventBuilder.id(String.valueOf(messageId++));
            }

            try {
                sseEmitter.send(eventBuilder);
            } catch (IOException ex) {
                // when send fails response is already committed
                internalCompleteAfterResponseCommitted(ex);
            } catch (IllegalStateException ex) {
                // when send fails response is already committed
                internalCompleteAfterResponseCommitted(ex);
                throw ex;
            }
        }
    }

    private void internalCompleteBeforeResponseCommitted(Throwable ex) {
        if (sseEmitter == null) throw new IllegalStateException("Connection was not initialized yet.");
        // wait for other operations to finish before reading completion status
        synchronized (this) {
            // ignore call if already completed
            if (completed) return;
            if (disconnecting) throw new IllegalStateException("Already disconnecting.");
            disconnecting = true;
        }
        // event handlers can use intact connection to inform clients the connection will close
        // thus connection is not yet marked as completed
        eventPublisher.publishEvent(new DisconnectingEventImpl(ex));
        // wait for other operations to finish before reading and writing completion status
        synchronized (this) {
            // connection could have been closed in between by and error occurring while sending
            if (completed) return;
            completed = true;
        }
        sseEmitter.complete();
        eventPublisher.publishEvent(new DisconnectedEventImpl(ex));
    }

    private void internalCompleteAfterResponseCommitted(Throwable ex) {
        if (sseEmitter == null) throw new IllegalStateException("Connection was not initialized yet.");
        // wait for other operations to finish before reading and writing completion status
        synchronized (this) {
            if (completed) return;
            completed = true;
        }
        eventPublisher.publishEvent(new DisconnectedEventImpl(ex));
    }

    @Value
    private class ConnectedEventImpl implements ConnectedEvent {
        public Connection getConnection() {
            return ConnectionImpl.this;
        }
    }

    @Value
    @NoArgsConstructor(force = true)
    @RequiredArgsConstructor
    private class DisconnectingEventImpl implements DisconnectingEvent {
        Throwable cause;

        public Connection getConnection() {
            return ConnectionImpl.this;
        }
    }

    @Value
    @NoArgsConstructor(force = true)
    @RequiredArgsConstructor
    private class DisconnectedEventImpl implements DisconnectedEvent {
        Throwable cause;

        public Connection getConnection() {
            return ConnectionImpl.this;
        }
    }
}
