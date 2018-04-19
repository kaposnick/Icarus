package services;
import java.util.Set;

import com.ntuaece.nikosapos.entities.Packet;

public interface IcasResponsible {
    boolean askForSendPermission(long destinationNodeId);
    void confirmSuccessfulDelivery(Set<Packet> packet);
    void registerToIcas();
    void updateNeighborBehavior();
    void unregister();
}
