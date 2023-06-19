package com.hexszeug.werewolf.game.connections;

import com.hexszeug.werewolf.game.events.connections.DisconnectedEvent;
import com.hexszeug.werewolf.game.events.connections.DisconnectingEvent;
import com.hexszeug.werewolf.game.events.connections.ServerSentEvent;
import com.hexszeug.werewolf.game.model.player.Player;
import com.hexszeug.werewolf.game.model.village.Village;
import com.hexszeug.werewolf.game.model.village.VillageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {
    private final ApplicationEventPublisher eventPublisher;
    private final TaskScheduler taskScheduler;
    private final VillageRepository villageRepository;

    private final Map<Player, Connection> connections = new HashMap<>();

    @Override
    public SseEmitter connect(Player player) {
        Connection oldConnection = connections.get(player);
        if (oldConnection != null && !oldConnection.isCompleted()) {
            oldConnection.completeWithError(new ConnectedFromOtherLocationException());
        }
        ConnectionImpl connection = new ConnectionImpl(eventPublisher, taskScheduler, player);
        connections.put(player, connection);
        return connection.initialize();
    }

    @Override
    public void disconnect(Player player) {
        Connection connection = connections.get(player);
        if (connection != null && !connection.isCompleted()) {
            connection.complete();
        }
    }

    @Override
    public void disconnect(Player player, Throwable cause) {
        Connection connection = connections.get(player);
        if (connection != null && !connection.isCompleted()) {
            connection.completeWithError(cause);
        }
    }

    @EventListener
    public void forwardServerSentEvents(ServerSentEvent<?> serverSentEvent) {
        Village village = villageRepository.getByVillageId(serverSentEvent.getVillageId());
        if (village == null) return;
        village.getPlayerList().forEach((player -> {
            Connection connection = connections.get(player);
            if (connection != null && !connection.isCompleted() && serverSentEvent.isTarget(player)) {
                connection.send(event().name(serverSentEvent.getName()).data(serverSentEvent.getPayload()));
            }
        }));
    }

    @EventListener
    public void sendServerSentErrorsWhileDisconnectingToClient(DisconnectingEvent event) {
        if (event.getCause() instanceof ServerSentError) {
            event.getConnection().send(event()
                    .name("error")
                    .data(new ServerSentError.Data(event.getCause()))
            );
        }
    }

    @EventListener
    public void removeCompletedConnectionsFromMemory(DisconnectedEvent event) {
        Connection connection = event.getConnection();
        connections.remove(connection.getPlayer(), connection);
    }
}
