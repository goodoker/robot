import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Future<Integer> future = executor.submit(new RouteAnalyzer());
            futures.add(future);
        }

        for (Future<Integer> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        analyzeResults();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    private static void analyzeResults() {

        int maxFrequency = 0;
        int mostCommonCount = 0;

        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostCommonCount = entry.getKey();
            }
        }

        System.out.println("Самое частое количество повторений " + mostCommonCount +
                " (встретилось " + maxFrequency + " раз)");
        System.out.println("Другие размеры:");

        List<Integer> keys = new ArrayList<>(sizeToFreq.keySet());
        Collections.sort(keys);

        for (Integer key : keys) {
            if (key != mostCommonCount) {
                System.out.println("- " + key + " (" + sizeToFreq.get(key) + " раз)");
            }
        }
    }


    static class RouteAnalyzer implements Callable<Integer> {
        @Override
        public Integer call() {

            String route = generateRoute("RLRFR", 100);

            int countR = 0;
            for (char c : route.toCharArray()) {
                if (c == 'R') {
                    countR++;
                }
            }

            System.out.println("Количество команд 'R': " + countR);

            synchronized (sizeToFreq) {
                sizeToFreq.put(countR, sizeToFreq.getOrDefault(countR, 0) + 1);
            }

            return countR;
        }
    }
}