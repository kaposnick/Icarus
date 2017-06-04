package com.ntuaece.nikosapos;

public final class SimulationParameters {
	
	/**
	 * Number of credits assigned to each node initially
	 */
	public static final int CREDITS_INITIAL = 220;
	
	/**
	 * 1st threshold used to assist distance nodes
	 */
	public static final float DISTANT_NODES_THRESHOLD_FIRST = 1.1f;
	
	/**
	 * 2nd threshold used to assist distance nodes
	 */
	public static final float DISTANT_NODES_THRESOLD_SECOND = 4f;
	
	/**
	 * Credit threshold to determine node state
	 */
	public static final int CREDIT_STATUS_THRESHOLD = 4;
	
	/**
	 * Threshold used by ICAS to determine if a node is selfish
	 */
	public static final float CONNECTIVITY_RATIO_ICAS_THRESHOLD = 0.5f;
	
	/**
	 * Number of packets that must be dropped before
	 * a selfish begins cooperating
	 */
	public static final int IFN = 5;
	
	/**
	 * Threshold used by nodes to determine if another node is 
	 * selfish
	 */
	public static final float EDP = 0.85f;
	
	/**
	 * Initial amount of credits that are given to distant nodes
	 */
	public static final int CREDITS_EXTRA= 28;
	
	/**
	 * Maximum number of hops allowed per packet
	 */
	public static final int MAX_HOPS = 15;
	
	/**
	 * Maximum distance for two nodes to be considered as neighbors
	 */
	public static final int MAX_NEIGHBOR_DISTANCE = 60;

	/**
	 * Threshold under which a node is considered as selfish
	 */
    public static final double CP_THRESHOLD = 0.4f;
    
    /**
     * Cheating probability
     */
    public static final double CHEATING_PROBABILITY = 1.0f;
    
    /**
     * Total cheating nodes
     */
    public static final int TOTAL_CHEATING_NODES = 3;
    
    /**
     * USES_ICARUS OR DARWIN
     */
    public static final boolean USES_ICARUS = false;

}
