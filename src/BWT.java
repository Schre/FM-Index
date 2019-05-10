import java.util.*;

public class BWT {

    private Map<Character, Integer> freq = new HashMap<>();
    private List<Character> alphabet = new ArrayList<>();
    private HashMap<Character, Integer> alphabetMappings = new HashMap();
    private Map<Character, TreeMap<Integer, Integer>> countMap = new HashMap<>();
    private int bwtLength = 0;
    // for each char in alphabet, build a treemap that keeps track of count with key = index, value = count


    public boolean checkIfPatternMatches(String s) {
        int top = 0;
        int bottom = bwtLength;
        for (int i = s.length() - 1; i >= 0; --i) {
            top = lf(top, s.charAt(i));
            bottom = lf(bottom, s.charAt(i));
            if (top == bottom && i != 0) {
                return false;
            }
        }
        return true;
    }

    public int lf(int row,Character c) {
        int occ = occ(c);
        int count = countMap.get(c).get(countMap.get(c).ceilingKey(row));
        return count + occ;
    }

    private int occ(Character c) {
        int occ = 0;

        int indx = alphabetMappings.get(c) - 1; // previous char in lexicographical order

        for (; indx >= 0; --indx) { // add up all chars less than
            occ += freq.get(alphabet.get(indx));
        }

        return occ;
    }

    private void createAlphabet(Set<Character> set) {
        for (Character c : set) {
            alphabet.add(c);
        }
        Collections.sort(alphabet); // sort alphabet

        for (int i = 0; i < alphabet.size(); ++i) {
            alphabetMappings.put(alphabet.get(i), i); // map char to index
        }
    }

    public BWT(String s) {
        buildBWT(s + "$"); // build BMT to derive LF mappings and occ
        bwtLength = s.length();
    }

     private List<String> buildBWT(String s) {

        for (int i = 0; i < s.length(); ++i) {
            Character c = s.charAt(i);
            freq.putIfAbsent(c,0);
            freq.put(c, freq.get(c) + 1); // increment count

        }

         Set<Character> alpha_set = new HashSet<>();
         List<String> ret = new ArrayList<>();
         Map<Character, Integer> tempCounts = new HashMap<>();


         for (int i = 0; i < s.length(); ++i) {
             String lst = "";
             for (int j = i; j < s.length() + i; ++j) {
                 int realIndex = (j) % s.length();
                 lst = lst + (s.charAt(realIndex));

                 alpha_set.add(s.charAt(realIndex));
             }
             ret.add(lst);
         }
         Collections.sort(ret);


        for (int i = 0; i < ret.size(); ++i) {
            String lst = "";
            for (int j = 0; j < ret.get(i).length(); ++j) {

                Character c = ret.get(i).charAt(j);
                if (j == ret.get(i).length() - 1) {
                    // record last character at row i's count
                    tempCounts.putIfAbsent(c,0);

                    countMap.putIfAbsent(c, new TreeMap<>());
                    countMap.get(c).put(i, tempCounts.get(c));
                    tempCounts.put(c, tempCounts.get(c) + 1); // increment count

                    countMap.get(c).put(Integer.MAX_VALUE, countMap.get(c).get(i) + 1);
                }

                alpha_set.add(c);
            }
        }

        createAlphabet(alpha_set);
        return ret;
    }

}