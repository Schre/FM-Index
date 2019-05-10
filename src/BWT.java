/*
 * Written by Samuel Schreiber <samuelschreiber96@gmail.com>, May 2019
 */
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

        for (; indx >= 0; --indx) { // add up all chars less than c in lexicographical order
            occ += freq.get(alphabet.get(indx));
        }

        return occ;
    }

    private void createAlphabet(Set<Character> set) {
        // convert set to list sorted lexicographically, then map each character to their corresponding index in the list
        for (Character c : set) {
            alphabet.add(c);
        }
        Collections.sort(alphabet); // sort alphabet

        for (int i = 0; i < alphabet.size(); ++i) {
            alphabetMappings.put(alphabet.get(i), i); // map char to index
        }
    }

    public BWT(String s) {
        buildBWT(s + "$"); // build BMT to derive count mappings and occ
        bwtLength = s.length();
    }

     private List<String> buildBWT(String s) {

        // First record the frequencies of each character (useful for occ calculation)
        for (int i = 0; i < s.length(); ++i) {
            Character c = s.charAt(i);
            freq.putIfAbsent(c,0);
            freq.put(c, freq.get(c) + 1); // increment count

        }

         Set<Character> alpha_set = new HashSet<>();
         List<String> ret = new ArrayList<>();
         Map<Character, Integer> tempCounts = new HashMap<>();


         // Construct the BWT matrix (The matrix on the whiteboard)
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


         // Initialize data structures needed for computing occ and count

         int rowLength = ret.get(0).length();
         for (int i = 0; i < ret.size(); ++i)  {
            Character c = ret.get(i).charAt(rowLength-1);
            // At last column in BWT matrix
            // record last character at row i's count
            tempCounts.putIfAbsent(c,0);
            countMap.putIfAbsent(c, new TreeMap<>());
            countMap.get(c).put(i, tempCounts.get(c));
            tempCounts.put(c, tempCounts.get(c) + 1); // increment count

            // Set max value to the current row's count value plus 1 (because we use the ceiling function in checkIfPatternMatches)
            countMap.get(c).put(Integer.MAX_VALUE, countMap.get(c).get(i) + 1);
            alpha_set.add(c);
        }

        // initialize the alphabet list and mappings based on our alpha_set
        createAlphabet(alpha_set);
        return ret;
    }

}
