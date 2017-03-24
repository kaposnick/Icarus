package services;
import com.ntuaece.nikosapos.entities.Packet;

public interface IcasResponsible {
    boolean askForSendPermission(long destinationNodeId);
    void confirmSuccessfulDelivery(Packet packet);
    void registerToIcas();
    void updateNeighborBehavior();
}
