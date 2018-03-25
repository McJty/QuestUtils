package mcjty.questutils.data;

import net.minecraft.util.math.BlockPos;

public class QUEntry {

    private final String id;

    private final int dimension;
    private final BlockPos pos;

    public QUEntry(String id, int dimension, BlockPos pos) {
        this.id = id;
        this.dimension = dimension;
        this.pos = pos;
    }

    public String getId() {
        return id;
    }

    public int getDimension() {
        return dimension;
    }

    public BlockPos getPos() {
        return pos;
    }
}
