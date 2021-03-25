package softwaredesign.server;

import softwaredesign.core.Player;

public class Computer extends Player {
    public Computer(Room roomAI, int computerID) {
        super(null);
        this.setCurrentRoom(roomAI);
        this.setPlayerName("Computer_" + computerID);
    }

    @Override
    public boolean isComputer(){ return true; }

    private String getComputerID(){ return this.getPlayerName().split("_")[1]; }    // Maybe needed idk
}
