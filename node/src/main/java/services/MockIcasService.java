package services;

import com.ntuaece.nikosapos.entities.Packet;

public class MockIcasService implements IcasResponsible {

    @Override
    public boolean askForSendPermission(long destinationNodeId) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void confirmSuccessfulDelivery(Packet packet) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void registerToIcas() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNeighborBehavior() {
        // TODO Auto-generated method stub
        
    }

}
