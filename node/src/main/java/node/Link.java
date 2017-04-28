package node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.GsonBuilder;
import com.ntuaece.nikosapos.entities.Packet;

import deliver.DeliverServiceImpl;
import deliver.DeliveryService;
import okhttp3.OkHttpClient;

public class Link {
    public static final List<Link> LinkList = new ArrayList<>();

    public synchronized static void createLinkIfNotExists(Node node1, Node node2, long distance) {

        // check if link already existing
        if (!LinkList.stream()
                     .noneMatch(link -> link.getFirstEndPoint().equals(node1) && link.getSecondEndPoint().equals(node2)
                             || link.getFirstEndPoint().equals(node2)
                                     && link.getSecondEndPoint().equals(node1))) { return; }

        // create link and bind to each neighbor
        Link link = new Link(node1, node2, distance);
        node1.getNeighbors().stream().filter(n -> n.getId() == node2.getId()).forEach(n -> {
            n.bindLink(link);
            n.setDistance((int) distance);
        });
        node2.getNeighbors().stream().filter(n -> n.getId() == node1.getId()).forEach(n -> {
            n.bindLink(link);
            n.setDistance((int) distance);
        });
        
        if (node1.getNodeRoutingThread() != null && node1.getTotalPacketsSent() > 5) {
            node1.getNodeRoutingThread().addNeighbor(node1.findNeighborById(node2.getId()).get());
        } else if (node2.getNodeRoutingThread() != null && node2.getTotalPacketsSent() > 5 ) {
            node2.getNodeRoutingThread().addNeighbor(node2.findNeighborById(node1.getId()).get());
        }

        LinkList.add(link);
        // System.out.println("Created Link " + node1.getId() + " - " +
        // node2.getId() + " distance " + distance);
    }

    private final long id;
    private final static AtomicLong linkCounter = new AtomicLong();

    private final double distance;
    private Node firstEndPoint;
    private Node secondEndPoint;

    private PacketReceiver firstEndPointPacketReceiver;
    private PacketReceiver secondEndPointPacketReceiver;

    private Queue<Packet> firstEndPointUpLink;
    private Queue<Packet> firstEndPointDownLink;
    private Queue<Packet> secondEndPointUpLink;
    private Queue<Packet> secondEndPointDownLink;

    private Timer timer;

    private DeliveryService deliveryService;

    public Link(final Node node1, final Node node2, final double distance) {
        this.id = linkCounter.getAndIncrement();

        this.firstEndPoint = node1;
        this.secondEndPoint = node2;
        this.distance = distance;

        firstEndPointUpLink = new ConcurrentLinkedQueue<>();
        firstEndPointDownLink = new ConcurrentLinkedQueue<>();
        secondEndPointUpLink = new ConcurrentLinkedQueue<>();
        secondEndPointDownLink = new ConcurrentLinkedQueue<>();

        deliveryService = new DeliverServiceImpl(new OkHttpClient(),
                                                 new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create());

        timer = new Timer("Link timer " + id, true);
    }

    public void setPacketReceiver(Node receiver, PacketReceiver pReceiver) {
        if (receiver.equals(firstEndPoint)) {
            firstEndPointPacketReceiver = pReceiver;
        } else if (receiver.equals(secondEndPoint)) {
            secondEndPointPacketReceiver = pReceiver;
        } else throw new IllegalArgumentException("Node " + receiver.getId() + " is not an endpoint of Link " + id);
    }

    public void addPacketToUpLink(Node sender, Packet p) {
        if (willDrop(p)) {
            System.out.println("Oups... " + p + " dropped");
//            p.drop();
            Packet.incrementDroppedPackets();
            return;
        }
        if (sender.equals(firstEndPoint)) {
            firstEndPointUpLink.offer(p);
            setTimer(1);
        } else if (sender.equals(secondEndPoint)) {
            secondEndPointUpLink.offer(p);
            setTimer(2);
        }
        // else throw new IllegalArgumentException("Node " + sender.getId() + "
        // is not an endpoint of Link " + id);
    }

    public synchronized void addPacketToDownLink(Node receiver, Packet p) {
        if (receiver.equals(firstEndPoint)) {
            firstEndPointDownLink.offer(p);
            if (firstEndPointPacketReceiver != null) firstEndPointPacketReceiver.onPacketReceived(id);
        } else if (receiver.equals(secondEndPoint)) {
            secondEndPointDownLink.offer(p);
            if (secondEndPointPacketReceiver != null) secondEndPointPacketReceiver.onPacketReceived(id);
        } else throw new IllegalArgumentException("Node " + receiver.getId() + " is not an endpoint of Link " + id);
    }

    public Optional<Packet> removePacketFromDownLink(Node receiver) {
        if (receiver.equals(firstEndPoint)) return Optional.ofNullable(firstEndPointDownLink.poll());
        else if (receiver.equals(secondEndPoint)) return Optional.ofNullable(secondEndPointDownLink.poll());
        else throw new IllegalArgumentException("Node " + receiver.getId() + " is not an endpoint of Link " + id);
    }

    private void sendPacketToNode(final Packet p, final Node nextNode) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                deliveryService.deliverPacketToNode(nextNode.getId(), p);
            }
        }).start();
    }

    /**
     * Decides if the packet will get dropped
     * 
     * @return true if random below the probability factor
     */

    private boolean willDrop(Packet packet) {
        if (packet.isAck()) {
            return new Random().nextDouble() * 10000 < 2;
        } else {
            return new Random().nextDouble() * 10000 < 20;
        }
    }

    /**
     * Set the timer for the packet to get pop out of the queue
     * 
     * @param i
     *            =1 for firstEndPoint =2 for secondEndPoint
     */

    private void setTimer(final int i) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Packet p = null;
                if (i == 1) p = firstEndPointUpLink.poll();
                else if (i == 2) p = secondEndPointUpLink.poll();
                if (p != null) {
                    if (i == 1) {
                        sendPacketToNode(p, secondEndPoint);
                    } else if (i == 2) {
                        sendPacketToNode(p, firstEndPoint);
                    }
                }
            }
        }, calculateDeliveryTimerBasedOnDistance());
    }

    private long calculateDeliveryTimerBasedOnDistance() {
        return 50;
    }

    public long getId() {
        return id;
    }

    public Node getFirstEndPoint() {
        return firstEndPoint;
    }

    public Node getSecondEndPoint() {
        return secondEndPoint;
    }

    @Override
    public String toString() {
        return String.format("Link: ID =  %d\t FirstEP= %d\t SecondEP= %d",
                             id,
                             firstEndPoint.getId(),
                             secondEndPoint.getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Link link = (Link) obj;

        if (link.getId() == this.id) return true;
        else return false;
    }

    public void destroy() {
        timer.cancel();

        // clear queues
        firstEndPointUpLink.clear();
        firstEndPointDownLink.clear();
        secondEndPointUpLink.clear();
        secondEndPointDownLink.clear();

        /* set pointers to null */
        firstEndPointUpLink = null;
        firstEndPointDownLink = null;
        secondEndPointDownLink = null;
        secondEndPointUpLink = null;

        firstEndPoint = null;
        secondEndPoint = null;
        firstEndPointPacketReceiver = null;
        secondEndPointPacketReceiver = null;
    }
}
