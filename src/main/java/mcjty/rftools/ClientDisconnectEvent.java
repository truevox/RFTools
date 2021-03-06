package mcjty.rftools;

import mcjty.rftools.dimension.RfToolsDimensionManager;
import mcjty.rftools.items.dimlets.KnownDimletConfiguration;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;

public class ClientDisconnectEvent {

    @SubscribeEvent
    public void onDisconnectedFromServerEvent(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        RFTools.log("Disconnect from server: Unregistering RFTools dimensions");
        RfToolsDimensionManager.unregisterDimensions();
        KnownDimletConfiguration.clean();
    }

}
