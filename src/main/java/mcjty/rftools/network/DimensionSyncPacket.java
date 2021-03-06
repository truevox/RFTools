package mcjty.rftools.network;

import mcjty.rftools.RFTools;
import mcjty.rftools.dimension.world.GenericWorldProvider;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.List;

public class DimensionSyncPacket {

    private ByteBuf data = Unpooled.buffer();

    private int[] dimensions;

    public void addDimension(int id) {
        data.writeInt(id);
    }

    public void consumePacket(ByteBuf data) {
        int cnt = data.readableBytes() / 4;
        dimensions = new int[cnt];
        for (int i = 0 ; i < cnt ; i++) {
            dimensions[i] = data.readInt();
        }
    }

    public ByteBuf getData() {
        return data;
    }

    public void execute() {
        // Only do this on client side.
        for (int id : dimensions) {
            RFTools.log("DimensionSyncPacket: Registering id: id = " + id);
            if (!DimensionManager.isDimensionRegistered(id)) {
                DimensionManager.registerProviderType(id, GenericWorldProvider.class, false);
                DimensionManager.registerDimension(id, id);
            }
        }
    }

}
