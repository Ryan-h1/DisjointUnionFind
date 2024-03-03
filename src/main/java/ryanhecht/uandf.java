package ryanhecht;

import java.util.*;

public class uandf {
  private ArrayList<Integer> root;
  private final ArrayList<Integer> rank;
  private boolean isFinal;
  private int originalSize;

  public uandf(int n) {
    this.root = new ArrayList<>();
    this.rank = new ArrayList<>();
    this.isFinal = false;
    this.originalSize = n;

    for (int i = 0; i < n; i++) {
      root.add(-1);
      rank.add(-1);
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

    root.add(i);
    rank.add(1);
  }

  public int find_set(int i) {
    if (root.get(i) == -1) {
      return -1;
    } else if (i != root.get(i)) {
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
    if (rootOfI > 0 && rootOfJ > 0) {
      if (rootOfI == rootOfJ) {
        return;
      }

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
    Map<Integer, Integer> uniqueRootsCountMap = new HashMap<>();
    int newRoot = root.size();
    int n = root.size();

    for (int i = 0; i < root.size(); i++) {
      int root = find_set(i);
      uniqueRootsCountMap.put(root, uniqueRootsCountMap.getOrDefault(root, 0) + 1);
    }

    List<Map.Entry<Integer, Integer>> list = new ArrayList<>(uniqueRootsCountMap.entrySet());
    list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
    Map<Integer, Integer> sortedUniqueRootsCountMap = new LinkedHashMap<>();
    for (Map.Entry<Integer, Integer> entry : list) {
      sortedUniqueRootsCountMap.put(entry.getKey(), entry.getValue());
    }

    for (int key : sortedUniqueRootsCountMap.keySet()) {
      if (sortedUniqueRootsCountMap.get(key) > 1) {
        System.out.println("Root: " + key + " Count: " + sortedUniqueRootsCountMap.get(key));
      }
    }

    for (int i = 0; i < n; i++) {
      if (root.get(i) == i && rank.get(i) > 1) { // if this element is a root of many children
        make_set(newRoot);
        root.set(i, newRoot);
        newRoot++;
      }
    }

    for (int i = 0; i < n; i++) {
      if (root.get(i) == i && rank.get(i) == 1) { // if this element is a singleton
        make_set(newRoot);
        newRoot++;
      }
    }

    for (int i = 0; i < root.size(); i++) {
      if (rank.get(i) > 1) {
        root.set(i, find_set(i)); // path compression
      }
    }

    for (int i = 0; i < root.size(); i++) {
      root.set(i, root.get(i) - root.size() + 1);
    }

    return uniqueRootsCountMap.size();
  }
}
