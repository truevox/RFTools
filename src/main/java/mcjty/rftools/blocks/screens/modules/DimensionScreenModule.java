package mcjty.rftools.blocks.screens.modules;

import mcjty.rftools.blocks.dimlets.DimletConfiguration;
import mcjty.rftools.blocks.screens.ScreenConfiguration;
import mcjty.rftools.dimension.DimensionStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class DimensionScreenModule implements ScreenModule {
    private int dim = 0;
    private ScreenModuleHelper helper = new ScreenModuleHelper();

    @Override
    public Object[] getData(World worldObj, long millis) {
        int energy = DimensionStorage.getDimensionStorage(DimensionManager.getWorld(0)).getEnergyLevel(dim);
        return helper.getContentsValue(millis, energy, DimletConfiguration.MAX_DIMENSION_POWER);
    }

    @Override
    public void setupFromNBT(NBTTagCompound tagCompound, int dim, int x, int y, int z) {
        if (tagCompound != null) {
            this.dim = tagCompound.getInteger("dim");
            helper.setShowdiff(tagCompound.getBoolean("showdiff"));
        }
    }

    @Override
    public int getRfPerTick() {
        return ScreenConfiguration.DIMENSION_RFPERTICK;
    }

    @Override
    public void mouseClick(World world, int x, int y, boolean clicked) {

    }
}
