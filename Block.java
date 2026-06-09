package domain;

public class Block {

    private int blockId;
    private String blockName;
    private int totalRooms;
    private int wardenId;
    private String wardenName; // joined field for display

    // ── Constructors ─────────────────────────────────────────
    public Block() {}

    public Block(int blockId, String blockName, int totalRooms, int wardenId) {
        this.blockId    = blockId;
        this.blockName  = blockName;
        this.totalRooms = totalRooms;
        this.wardenId   = wardenId;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getBlockId()                      { return blockId; }
    public void setBlockId(int blockId)          { this.blockId = blockId; }

    public String getBlockName()                 { return blockName; }
    public void setBlockName(String blockName)   { this.blockName = blockName; }

    public int getTotalRooms()                   { return totalRooms; }
    public void setTotalRooms(int totalRooms)    { this.totalRooms = totalRooms; }

    public int getWardenId()                     { return wardenId; }
    public void setWardenId(int wardenId)        { this.wardenId = wardenId; }

    public String getWardenName()                { return wardenName; }
    public void setWardenName(String wardenName) { this.wardenName = wardenName; }

    @Override
    public String toString() { return blockName; }
}
