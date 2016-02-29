package nlp.app.math.util;

/**
 * @author Arindam
 *
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class SubsetIterator<E> {
    private final List<E> set;
    private final int max;
    private int index;
    public SubsetIterator(List<E> originalList) {
        set = originalList;
        max = (1 << set.size());
        index = 0;
    }
    public boolean hasNext() {
        return index < max;
    }
    public List<E> next() {
        List<E> newSet = new ArrayList<E>();
        int flag = 1;      
        for (E element : set) {
            if ((index & flag) != 0) {
                newSet.add(element);
            }
            flag <<= 1;
        }
        ++index;
        return newSet;
    }
    
    // Test.
    public static void main(String[] args) {
        List<String> set = Arrays.asList("A", "B", "C", "D");
        SubsetIterator<String> it = 
                new SubsetIterator<String>(set);
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
}
