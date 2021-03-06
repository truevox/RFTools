package mcjty.rftools.items.storage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcjty.rftools.RFTools;
import mcjty.rftools.blocks.storage.ModularStorageSetup;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class StorageModuleItem extends Item {
    private final IIcon[] icons = new IIcon[7];
    private IIcon activeIcon;

    public static final int STORAGE_TIER1 = 0;
    public static final int STORAGE_TIER2 = 1;
    public static final int STORAGE_TIER3 = 2;
    public static final int STORAGE_REMOTE = 6;
    public static final int MAXSIZE[] = new int[] { 100, 200, 300, 0, 0, 0, -1 };

    public StorageModuleItem() {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        for (int i = 0 ; i < 7 ; i++) {
            if (MAXSIZE[i] != 0) {
                icons[i] = iconRegister.registerIcon(RFTools.MODID + ":storage/storageModule" + i);
            }
        }
        activeIcon = iconRegister.registerIcon(RFTools.MODID + ":storage/storageModule6Active");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            RFTools.message(player, EnumChatFormatting.YELLOW + "Place this module in a storage module tablet to access contents");
            return stack;
        }
        return stack;
    }

    // Called from the Remote or Modular store TE's to update the stack size for this item while it is inside that TE.
    public static void updateStackSize(ItemStack stack, int numStacks) {
        if (stack == null || stack.stackSize == 0) {
            return;
        }
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
            stack.setTagCompound(tagCompound);
        }
        tagCompound.setInteger("count", numStacks);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean whatIsThis) {
        super.addInformation(itemStack, player, list, whatIsThis);
        int max = MAXSIZE[itemStack.getItemDamage()];
        NBTTagCompound tagCompound = itemStack.getTagCompound();
        if (tagCompound != null) {
            addModuleInformation(list, max, tagCompound);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            list.add(EnumChatFormatting.WHITE + "This storage module is for the Modular Storage block.");
            if (max == -1) {
                list.add(EnumChatFormatting.WHITE + "This module supports a remote inventory.");
                list.add(EnumChatFormatting.WHITE + "Link to another storage module in the remote storage block.");
            } else {
                list.add(EnumChatFormatting.WHITE + "This module supports " + max + " stacks");
            }
        } else {
            list.add(EnumChatFormatting.WHITE + RFTools.SHIFT_MESSAGE);
        }
    }

    public static void addModuleInformation(List list, int max, NBTTagCompound tagCompound) {
        if (max == -1) {
            // This is a remote storage module.
            if (tagCompound.hasKey("id")) {
                int id = tagCompound.getInteger("id");
                list.add(EnumChatFormatting.GREEN + "Remote id: " + id);
            } else {
                list.add(EnumChatFormatting.YELLOW + "Unlinked");
            }
        } else {
            int cnt = tagCompound.getInteger("count");
            if (tagCompound.hasKey("id")) {
                int id = tagCompound.getInteger("id");
                list.add(EnumChatFormatting.GREEN + "Contents id: " + id);
            }
            list.add(EnumChatFormatting.GREEN + "Contents: " + cnt + "/" + max + " stacks");
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconIndex(ItemStack stack) {
        int damage = stack.getItemDamage();
        if (damage == 6) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("id")) {
                return activeIcon;
            } else {
                return icons[damage];
            }
        } else {
            return icons[damage];
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        return super.getUnlocalizedName(itemStack) + itemStack.getItemDamage();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        for (int i = 0 ; i < 7 ; i++) {
            if (MAXSIZE[i] != 0) {
                list.add(new ItemStack(ModularStorageSetup.storageModuleItem, 1, i));
            }
        }
    }
}
