package services;

import com.ntuaece.nikosapos.entities.Packet;

public class MockIcasService implements IcasResponsible {

    @Override
    public boolean askForSendPermission(long destinationNodeId) {
        return true;
    }

    @Override
    public void confirmSuccessfulDelivery(Packet packet) {
        
    }

    @Override
    public void registerToIcas() {
        
    }

    @Override
    public void updateNeighborBehavior() {
        
    }

    @Override
    public void unregister() {        
    }
    
    

}
