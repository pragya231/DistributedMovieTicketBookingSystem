package FinalVariables;

public class FinalVariables {
    //System IP Address
    public static final String SYSTEM_IP_ADDRESS = "172.20.10.8"; // NEED TO CHANGE WHEN CONNECTION GETS CHANGED

    public static final String RM1_IP_ADDRESS="172.20.10.8";
    public static final String RM2_IP_ADDRESS="172.20.10.7";
    public static final String RM3_IP_ADDRESS="172.20.10.2";
    public static final int FRONT_PORT_WS = 9995;
    public static final int FRONT_PORT = 9996;
    public static final int FRONT_PORT_Failure_Port = 9997;
    public static final int SEQUENCER_PORT = 8082;
    public static final int SEQUENCER_TO_FRONT_PORT=8083;
    public static final int RM_LISTENER_PORT_FROM_SEQUENCER = 3341; // ALL RMs WILL USE THIS PORT NUMBER
    public static final int RM_LISTENER_PORT_FOR_SOFTWARE_FAILURE = 3342; // ALL RMs WILL USE THIS PORT NUMBER
    public static final int RM_SENDER_PORT_TO_FE = 3343;
    public static final int RM_SENDER = 3344;

}
