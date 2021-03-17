package softwaredesign;

public class Computer extends Client{
    public Computer(Room roomAI, int computerID) {
        super(null);
        this.setCurrentRoom(roomAI);
        this.setClientName("Computer_" + computerID);
    }

    private String getComputerID(){ return this.getClientName().split("_")[1]; }    // Maybe needed idk
}
