package com.hexszeug.werewolf.game.connections;

import com.hexszeug.werewolf.game.model.player.Player;
import lombok.experimental.StandardException;

/**
 * If a player establishes a {@link Connection} but there already exists another connection to them,
 * the old connection is closed by passing an instance of this exception
 * to {@link Connection#completeWithError(Throwable)}.
 * @see ConnectionService#connect(Player)
 */
@StandardException
public class ConnectedFromOtherLocationException extends Exception implements ServerSentError {}
