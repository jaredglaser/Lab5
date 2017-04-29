package pkgPoker.app.model;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javafx.scene.control.ToggleButton;
import netgame.common.Hub;
import pkgPokerBLL.Action;
import pkgPokerBLL.Card;
import pkgPokerBLL.CardDraw;
import pkgPokerBLL.Deck;
import pkgPokerBLL.GamePlay;
import pkgPokerBLL.GamePlayPlayerHand;
import pkgPokerBLL.Player;
import pkgPokerBLL.Rule;
import pkgPokerBLL.Table;

import pkgPokerEnum.eAction;
import pkgPokerEnum.eCardDestination;
import pkgPokerEnum.eDrawCount;
import pkgPokerEnum.eGame;
import pkgPokerEnum.eGameState;

public class PokerHub extends Hub {

	private Table HubPokerTable = new Table();
	private GamePlay HubGamePlay;
	private int iDealNbr = 0;

	public PokerHub(int port) throws IOException {
		super(port);
	}

	protected void playerConnected(int playerID) {

		if (playerID == 2) {
			shutdownServerSocket();
		}
	}

	protected void playerDisconnected(int playerID) {
		shutDownHub();
	}

	protected void messageReceived(int ClientID, Object message) {
		Rule rle = null;
		if (message instanceof Action) {
			Player actPlayer = (Player) ((Action) message).getPlayer();
			Action act = (Action) message;
			switch (act.getAction()) {
			case Sit:
				HubPokerTable.AddPlayerToTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case Leave:			
				HubPokerTable.RemovePlayerFromTable(actPlayer);
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case TableState:
				resetOutput();
				sendToAll(HubPokerTable);
				break;
			case StartGame:
				// Get the rule from the Action object.
				rle = new Rule(act.geteGame());
				
				//TODO Lab #5 - If neither player has 'the button', pick a random player
				//		and assign the button.				
				
				//TODO Lab #5 - Start the new instance of GamePlay
				
				 //whoever presses play is the dealer
				UUID dealerID = actPlayer.getPlayerID();
				GamePlay game = new GamePlay(rle, dealerID );
				
				// Add Players to Game
				HubGamePlay.setGamePlayers(HubPokerTable.getHmPlayer());
				
				// Set the order of players
				int[] order = {1,2};
				game.setiActOrder(order);


			case Draw:
				this.iDealNbr+=1;

				rle.GetDrawCard(eDrawCount.FIRST);
				
				//TODO Lab #5 -	Draw card(s) for each player in the game.
				
				//TODO Lab #5 -	Make sure to set the correct visiblity
				//TODO Lab #5 -	Make sure to account for community cards

				//TODO Lab #5 -	Check to see if the game is over
				HubGamePlay.isGameOver();
				
				resetOutput();
				//	Send the state of the gameplay back to the clients
				sendToAll(HubGamePlay);
				break;
			case ScoreGame:
				// Am I at the end of the game?

				resetOutput();
				sendToAll(HubGamePlay);
				break;
			}
			
		}

	}

}