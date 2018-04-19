package darwin;

import com.ntuaece.nikosapos.SimulationParameters;

import node.Neighbor;
import node.Node;

public class DarwinUtils {
    public static double normalizeValue(double v) {
        if (v >= 1) return 1.0f;
        else if (v <= 0) return 0.0f;
        else return v;
    }
    
    public static void DecideDarwinSelfishNode(Node node, Neighbor neighbor) {
        if (neighbor.getDarwinI() >= SimulationParameters.EDP) {
            node.addDarwinSelfishNode(neighbor.getId());
        } else {
            node.removeDarwinSelfishNode(neighbor.getId());
        }
    }
}
