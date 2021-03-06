package mcjty.rftools.blocks.teleporter;

import mcjty.container.GenericItemBlock;
import mcjty.rftools.RFTools;
import mcjty.rftools.dimension.RfToolsDimensionManager;
import mcjty.varia.GlobalCoordinate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class SimpleDialerItemBlock extends GenericItemBlock {
    public SimpleDialerItemBlock(Block block) {
        super(block);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float sx, float sy, float sz) {
        TileEntity te = world.getTileEntity(x, y, z);
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }

        if (te instanceof MatterTransmitterTileEntity) {
            if (!world.isRemote) {
                MatterTransmitterTileEntity matterTransmitterTileEntity = (MatterTransmitterTileEntity) te;

                if (!matterTransmitterTileEntity.checkAccess(player.getDisplayName())) {
                    RFTools.message(player, EnumChatFormatting.RED + "You have no access to this matter transmitter!");
                    return true;
                }

                tagCompound.setInteger("transX", matterTransmitterTileEntity.xCoord);
                tagCompound.setInteger("transY", matterTransmitterTileEntity.yCoord);
                tagCompound.setInteger("transZ", matterTransmitterTileEntity.zCoord);
                tagCompound.setInteger("transDim", world.provider.dimensionId);

                if (matterTransmitterTileEntity.isDialed()) {
                    Integer id = matterTransmitterTileEntity.getTeleportId();
                    boolean access = checkReceiverAccess(player, world, id);
                    if (!access) {
                        RFTools.message(player, EnumChatFormatting.RED + "You have no access to the matter receiver!");
                        return true;
                    }

                    tagCompound.setInteger("receiver", id);
                    RFTools.message(player, EnumChatFormatting.YELLOW + "Receiver set!");
                }

                RFTools.message(player, EnumChatFormatting.YELLOW + "Transmitter set!");
            }
        } else if (te instanceof MatterReceiverTileEntity) {
            if (!world.isRemote) {
                MatterReceiverTileEntity matterReceiverTileEntity = (MatterReceiverTileEntity) te;

                Integer id  = matterReceiverTileEntity.getOrCalculateID();
                boolean access = checkReceiverAccess(player, world, id);
                if (!access) {
                    RFTools.message(player, EnumChatFormatting.RED + "You have no access to this matter receiver!");
                    return true;
                }

                tagCompound.setInteger("receiver", id);
                RFTools.message(player, EnumChatFormatting.YELLOW + "Receiver set!");
            }
        } else {
            return super.onItemUse(stack, player, world, x, y, z, side, sx, sy, sz);
        }

        stack.setTagCompound(tagCompound);
        return true;
    }

    private boolean checkReceiverAccess(EntityPlayer player, World world, Integer id) {
        boolean access = true;
        TeleportDestinations destinations = TeleportDestinations.getDestinations(world);
        GlobalCoordinate coordinate = destinations.getCoordinateForId(id);
        if (coordinate != null) {
            TeleportDestination destination = destinations.getDestination(coordinate);
            if (destination != null) {
                World worldForDimension = RfToolsDimensionManager.getWorldForDimension(destination.getDimension());
                if (worldForDimension != null) {
                    TileEntity recTe = worldForDimension.getTileEntity(
                            destination.getCoordinate().getX(),
                            destination.getCoordinate().getY(),
                            destination.getCoordinate().getZ());
                    if (recTe instanceof MatterReceiverTileEntity) {
                        MatterReceiverTileEntity matterReceiverTileEntity = (MatterReceiverTileEntity) recTe;
                        if (!matterReceiverTileEntity.checkAccess(player.getDisplayName())) {
                            access = false;
                        }
                    }
                }
            }
        }
        return access;
    }
}
