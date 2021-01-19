import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MultiThreadedWordCount {

    private final static int THREAD_COUNT = 6;

    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> fileContent = readFileContent();

        long startTime = System.currentTimeMillis();

        int noOfThreads = THREAD_COUNT;
        int lines = fileContent.size();
        int partitionSize = lines / noOfThreads;
        int previousIndex = 0;

        List<CounterThread> threads = new ArrayList<>();

        createThreads(fileContent, noOfThreads, lines, partitionSize, previousIndex, threads);

        startAndJoinThreads(threads);

        int totalWords = getTotalWords(threads);

        long endTime = System.currentTimeMillis();

        printResult(startTime, noOfThreads, totalWords, endTime);
    }

    private static void printResult(long startTime, int noOfThreads, int totalWords, long endTime) {
        System.out.println("Total words : " + totalWords);
        System.out.println("Time taken to count when using " + noOfThreads + " thread(s) : " + (endTime - startTime) + " millis");
    }

    private static int getTotalWords(List<CounterThread> threads) {
        int totalWords = 0;
        for (CounterThread t : threads) {
            totalWords += t.getCount();
        }
        return totalWords;
    }

    private static void startAndJoinThreads(List<CounterThread> threads) throws InterruptedException {
        for (CounterThread t : threads) {
            t.start();
        }
        for (CounterThread t : threads) {
            t.join();
        }
    }

    private static void createThreads(List<String> fileContent, int noOfThreads, int lines, int partitionSize, int previousIndex, List<CounterThread> threads) {
        for (int i = 1; i < noOfThreads; i++) {
            List<String> subList = fileContent.subList(previousIndex, partitionSize * i);
            threads.add(new CounterThread(subList));
            previousIndex = partitionSize * i;
        }
        List<String> subList = fileContent.subList(previousIndex, lines);
        threads.add(new CounterThread(subList));
    }

    private static List<String> readFileContent() throws IOException {
        String filePath = "C:\\Users\\Santhosh\\Documents\\Work_New\\Work\\SpringAu\\Multithreading\\Book.txt";
        Path path = Paths.get(filePath);
        List<String> read = Files.readAllLines(path);
        return read;
    }
}

class CounterThread extends Thread {
    private List<String> data;
    private int count;

    CounterThread(List<String> data) {
        this.data = data;
        this.count = 0;
    }

    @Override
    public void run() {
        for (String s : data) {
            count += s.split(" ").length;
        }
    }

    public int getCount() {
        return count;
    }

}
