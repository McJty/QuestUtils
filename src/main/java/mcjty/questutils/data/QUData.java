package mcjty.questutils.data;

import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class QUData extends AbstractWorldData<QUData> {

    private static final String NAME = "QUData";

    private Map<String, QUEntry> entries = new HashMap<>();
    private Map<Pair<Integer, BlockPos>, QUEntry> entriesByPosition = new HashMap<>();

    public QUData(String name) {
        super(name);
    }

    @Override
    public void clear() {
        entries.clear();
        entriesByPosition.clear();
    }

    public static QUData getData() {
        return getData(QUData.class, NAME);
    }

    public Map<String, QUEntry> getEntries() {
        return entries;
    }

    public void updateEntry(String id, int dimension, BlockPos pos) {
        removeEntry(id);
        if (!id.trim().isEmpty()) {
            QUEntry entry = new QUEntry(id, dimension, pos);
            entries.put(id, entry);
            entriesByPosition.put(Pair.of(dimension, pos), entry);
            save();
        }
    }

    public void removeEntry(String id) {
        QUEntry entry = entries.get(id);
        if (entry != null) {
            entriesByPosition.remove(Pair.of(entry.getDimension(), entry.getPos()));
            entries.remove(id);
            save();
        }
    }

    public void removeEntry(int dim, BlockPos pos) {
        QUEntry entry = entriesByPosition.get(Pair.of(dim, pos));
        if (entry != null) {
            entries.remove(entry.getId());
            entriesByPosition.remove(Pair.of(dim, pos));
            save();
        }
    }

    public QUEntry getEntry(int dimension, BlockPos pos) {
        return entriesByPosition.get(Pair.of(dimension, pos));
    }

    public QUEntry getEntry(String id) {
        return entries.get(id);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        entries.clear();
        entriesByPosition.clear();
        NBTTagList list = compound.getTagList("entries", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < list.tagCount() ; i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            String id = tag.getString("id");
            int dim = tag.getInteger("dim");
            BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
            QUEntry entry = new QUEntry(id, dim, pos);
            entries.put(id, entry);
            entriesByPosition.put(Pair.of(dim, pos), entry);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Map.Entry<String, QUEntry> entry : entries.entrySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("id", entry.getKey());
            tag.setInteger("dim", entry.getValue().getDimension());
            tag.setLong("pos", entry.getValue().getPos().toLong());
            list.appendTag(tag);
        }
        compound.setTag("entries", list);

        return compound;
    }
}
