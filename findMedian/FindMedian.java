
package findMedian;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FindMedian {

    public static void main(String[] args) {
        System.out.println(findMedian(Arrays.asList(5, 3, 1, 2, 4)));
    }

    public static int findMedian(List<Integer> arr) {
        return arr.stream().sorted().collect(Collectors.toList())
                .get(arr.size() / 2);
    }


}
