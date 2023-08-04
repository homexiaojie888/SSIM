import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class PairMap {
    Map<Integer, Integer> mapItemToNewTWU=new TreeMap<>();
    Map<Integer, Integer> mapItemToNewLessdeltaTWU=new TreeMap<>();

    public PairMap(Map<Integer, Integer> mapItemToNewTWU, Map<Integer, Integer> mapItemToNewLessdeltaTWU) {
        this.mapItemToNewTWU = mapItemToNewTWU;
        this.mapItemToNewLessdeltaTWU = mapItemToNewLessdeltaTWU;
    }

    public Map<Integer, Integer> getMapItemToNewTWU() {
        return mapItemToNewTWU;
    }

    public void setMapItemToNewTWU(Map<Integer, Integer> mapItemToNewTWU) {
        this.mapItemToNewTWU = mapItemToNewTWU;
    }

    public Map<Integer, Integer> getMapItemToNewLessdeltaTWU() {
        return mapItemToNewLessdeltaTWU;
    }

    public void setMapItemToNewLessdeltaTWU(Map<Integer, Integer> mapItemToNewLessdeltaTWU) {
        this.mapItemToNewLessdeltaTWU = mapItemToNewLessdeltaTWU;
    }
}
