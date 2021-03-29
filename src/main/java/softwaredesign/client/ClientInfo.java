package softwaredesign.client;

import softwaredesign.server.ServerProgram;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class ClientInfo {
    private static String username;
    private static ArrayList<String> ownHand = new ArrayList<>();
    private static LinkedHashMap<String,Integer> playerNamesAndHandSizes = new LinkedHashMap<>();
    private static String requestedCard = "";
    private static String currentServer = "";
    private static String roomName = "";
    private static String[] gameRules = new String[]{"", ""};
    private static String deckSize;
    private static String currentDeck;
    private static String discardDeckTop;
    private static Boolean offlineGame;
    private static ServerProgram server;
    private static LinkedList<String> serverMessage = new LinkedList<>();

    public static ArrayList<String> getOwnHand() {
        return ownHand;
    }

    public static Boolean getOfflineGame() {
        return offlineGame;
    }

    public static LinkedHashMap<String, Integer> getPlayerNamesAndHandSizes() {
        return playerNamesAndHandSizes;
    }

    public static LinkedList<String> getServerMessage() {
        return serverMessage;
    }

    public static ServerProgram getServer() {
        return server;
    }

    public static String getCurrentDeck() {
        return currentDeck;
    }

    public static String getCurrentServer() {
        return currentServer;
    }

    public static String getDeckSize() {
        return deckSize;
    }

    public static String getRequestedCard() {
        return requestedCard;
    }

    public static String getRoomName() {
        return roomName;
    }

    public static String getDiscardDeckTop() {
        return discardDeckTop;
    }

    public static String getUsername() {
        return username;
    }

    public static String[] getGameRules() {
        return gameRules;
    }

    public static void setCurrentDeck(String currentDeck) {
        ClientInfo.currentDeck = currentDeck;
    }

    public static void setCurrentServer(String currentServer) {
        ClientInfo.currentServer = currentServer;
    }

    public static void setDeckSize(String deckSize) {
        ClientInfo.deckSize = deckSize;
    }

    public static void setDiscardDeckTop(String discardDeckTop) {
        ClientInfo.discardDeckTop = discardDeckTop;
    }

    public static void setGameRules(String[] gameRules) {
        ClientInfo.gameRules = gameRules;
    }

    public static void setOfflineGame(Boolean offlineGame) {
        ClientInfo.offlineGame = offlineGame;
    }

    public static void setOwnHand(ArrayList<String> ownHand) {
        ClientInfo.ownHand = ownHand;
    }

    public static void setPlayerNamesAndHandSizes(LinkedHashMap<String, Integer> playerNamesAndHandSizes) {
        ClientInfo.playerNamesAndHandSizes = playerNamesAndHandSizes;
    }

    public static void setRequestedCard(String requestedCard) {
        ClientInfo.requestedCard = requestedCard;
    }

    public static void setServer(ServerProgram server) {
        ClientInfo.server = server;
    }

    public static void setRoomName(String roomName) {
        ClientInfo.roomName = roomName;
    }

    public static void setUsername(String username) {
        ClientInfo.username = username;
    }
}

