package ryanhecht;

import java.util.*;

public class uandf {
    private ArrayList<Integer> root;
    private final ArrayList<Integer> rank;
    private boolean isFinal;
    private Map<Integer, Character> compressedRep;
    private Map<Integer, Integer> uniqueRootsCountMap;

    public uandf(int n) {
        this.root = new ArrayList<>();
        this.rank = new ArrayList<>();
        this.isFinal = false;
        this.compressedRep = new LinkedHashMap<>();
        this.uniqueRootsCountMap = new HashMap<>();

        for (int i = 0; i < n; i++) {
            root.add(-1);
            rank.add(1);
        }
    }

    public void make_set(int i) {
        if (isFinal) {
            System.out.println("Operation not permitted: The sets have been finalized.");
            return;
        }
        if (i < 0) {
            throw new IllegalArgumentException("Index must be non-negative.");
        }

        if (i >= root.size()) {
            root.ensureCapacity(i + 1);
            rank.ensureCapacity(i + 1);
        }

        root.set(i, i);
        rank.set(i, 0);
    }

    public int find_set(int i) {
        if (i == -1) {
            return -1;
        }
        if (i != root.get(i)) {
            root.set(i, find_set(root.get(i))); // Path compression
        }
        return root.get(i);
    }

    public void union_sets(int i, int j) {
        if (isFinal) {
            System.out.println("Sets are already final");
            return;
        }

        int rootOfI = find_set(i);
        int rootOfJ = find_set(j);
        if (rootOfI != rootOfJ) {
            if (rank.get(rootOfI) > rank.get(rootOfJ)) {
                root.set(rootOfJ, rootOfI);
            } else if (rank.get(rootOfI) < rank.get(rootOfJ)) {
                root.set(rootOfI, rootOfJ);
            } else {
                root.set(rootOfJ, rootOfI);
                rank.set(rootOfI, rank.get(rootOfI) + 1);
            }
        }
    }

    public int final_sets() {
        Set<Integer> uniqueRoots = new HashSet<>();
        int newRoot = 97;

        for (int i = 0; i < root.size(); i++) {
            int set = find_set(i); // path compression
            uniqueRoots.add(set);

            if (root.get(i) != -1) {
                uniqueRootsCountMap.put(set, uniqueRootsCountMap.getOrDefault(set, 0) + 1);
            }
        }

        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(uniqueRootsCountMap.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        Map<Integer, Integer> sortedUniqueRootsCountMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : list) {
            sortedUniqueRootsCountMap.put(entry.getKey(), entry.getValue());
        }

        for (Integer key : sortedUniqueRootsCountMap.keySet()) {
            if (!compressedRep.containsKey(key)) {
                compressedRep.put(key, (char) newRoot);
                newRoot++;
            }
        }

        return uniqueRoots.size();
    }

    public Map<Integer, Character> getCompressedRep() {
        return compressedRep;
    }

    public Map<Integer, Integer> getUniqueRootsCountMap() {
        return uniqueRootsCountMap;
    }
}
