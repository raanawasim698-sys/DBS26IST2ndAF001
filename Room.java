package domain;

public class Room {

    private int roomId;
    private String roomNumber;
    private int blockId;
    private String blockName;   // joined field for display
    private int floor;
    private String roomType;    // Single / Double / Triple
    private int capacity;
    private double monthlyRent;
    private String acStatus;    // AC / Non-AC
    private String bathroomAttached; // Yes / No
    private String status;      // Available / Occupied / Maintenance

    // ── Constructors ─────────────────────────────────────────
    public Room() {}

    public Room(int roomId, String roomNumber, int blockId, int floor,
                String roomType, int capacity, double monthlyRent,
                String acStatus, String bathroomAttached, String status) {
        this.roomId           = roomId;
        this.roomNumber       = roomNumber;
        this.blockId          = blockId;
        this.floor            = floor;
        this.roomType         = roomType;
        this.capacity         = capacity;
        this.monthlyRent      = monthlyRent;
        this.acStatus         = acStatus;
        this.bathroomAttached = bathroomAttached;
        this.status           = status;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getRoomId()                           { return roomId; }
    public void setRoomId(int roomId)                { this.roomId = roomId; }

    public String getRoomNumber()                    { return roomNumber; }
    public void setRoomNumber(String roomNumber)     { this.roomNumber = roomNumber; }

    public int getBlockId()                          { return blockId; }
    public void setBlockId(int blockId)              { this.blockId = blockId; }

    public String getBlockName()                     { return blockName; }
    public void setBlockName(String blockName)       { this.blockName = blockName; }

    public int getFloor()                            { return floor; }
    public void setFloor(int floor)                  { this.floor = floor; }

    public String getRoomType()                      { return roomType; }
    public void setRoomType(String roomType)         { this.roomType = roomType; }

    public int getCapacity()                         { return capacity; }
    public void setCapacity(int capacity)            { this.capacity = capacity; }

    public double getMonthlyRent()                   { return monthlyRent; }
    public void setMonthlyRent(double monthlyRent)   { this.monthlyRent = monthlyRent; }

    public String getAcStatus()                      { return acStatus; }
    public void setAcStatus(String acStatus)         { this.acStatus = acStatus; }

    public String getBathroomAttached()              { return bathroomAttached; }
    public void setBathroomAttached(String b)        { this.bathroomAttached = b; }

    public String getStatus()                        { return status; }
    public void setStatus(String status)             { this.status = status; }

    @Override
    public String toString() {
        return roomNumber + " (" + blockName + ") — " + roomType + " — " + status;
    }
}
