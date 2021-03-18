package ForkJoin;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 *   "When you execute ForkJoinTask in ForkJoinPool, you can do it in a synchronous or an
 * asynchronous way. When you do it in a synchronous way, the method that sends the task
 * to the pool doesn't return until the task sent finishes its execution. When you do it in an
 * asynchronous way, the method that sends the task to the executor returns immediately, so
 * the task can continue with its execution.
 * You should be aware of a big difference between the two methods. When you use the
 * synchronous methods, the task that calls one of these methods (for example, the
 * invokeAll() method) is suspended until the tasks it sent to the pool finish their execution.
 * This allows the ForkJoinPool class to use the work-stealing algorithm to assign a new task
 * to the worker thread that executed the sleeping task. On the contrary, when you use the
 * asynchronous methods (for example, the fork() method), the task continues with its
 * execution, so the ForkJoinPool class can't use the work-stealing algorithm to increase the
 * performance of the application. In this case, only when you call the join() or get()
 * methods to wait for the finalization of a task, the ForkJoinPool class can use that
 * algorithm."
 */

public class ForkJoin_RecursiveTask_ReadDocForWordCount {

    public static void main(String[] args) {
        // generate a document
        CreateDoc doc = new CreateDoc(100, 1000, "java");

        // count the words appearance in the doc
        System.out.println(doc.getWordsCount());

        ForkJoinPool pool = new ForkJoinPool();
        // create task of counting words
        CountWordsInDocument task = new CountWordsInDocument(doc, 0, 100, pool);
        pool.invoke(task);
        pool.shutdown();
        // wait for finish
        try {
//            Blocks until all tasks have completed execution after a
//                    * shutdown request, or the timeout occurs, or the current thread
//                    * is interrupted, whichever happens first.
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(task.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}

class CreateDoc {

    private int lineNumber, wordNumber;
    private String theWord;
    private String[][] doc;
    private String[] wordsBank = {"yes", "python", "school", "class", "teacher", "study", "course", "master", "diploma", "graduate",
            "hello", "very", "bean", "java", "cool", "so", "happy", "you", "we", "sky", "coding", "science"};
    Random rand = new Random();
    int counter = 0;

    CreateDoc(int lineNumber, int wordNumber, String theWord) {
        this.lineNumber = lineNumber;
        this.wordNumber = wordNumber;
        this.theWord = theWord;
        doc = new String[lineNumber][wordNumber];
        generateDoc();
    }

    private void generateDoc() {
        for (int i = 0; i < lineNumber; i++) {
            for (int j = 0; j < wordNumber; j++) {
                doc[i][j] = wordsBank[rand.nextInt(wordsBank.length)]; // generate a random word
                if (doc[i][j].equals(theWord))
                    counter++;
            }
        }
    }

    public int getWordsCount() {
        return counter;
    }

    public String getTheWord() {
        return theWord;
    }

    public int getTheLineNumber() {
        return lineNumber;
    }

    public int getWordNumber() {
        return wordNumber;
    }

    public String[] getLine(int index) {
        return doc[index];
    }
}


class CountWordsInDocument extends RecursiveTask<Integer> {
    CreateDoc doc;
    String theWord;
    int startIndex, endIndex;
    int result;
    ForkJoinPool pool;

    CountWordsInDocument(CreateDoc doc, int startIndex, int endIndex, ForkJoinPool pool) {
        this.doc = doc;
        theWord = doc.getTheWord();
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.pool = pool;
    }

    @Override
    protected Integer compute() {

        if (endIndex - startIndex > 10) {
            int middle = (startIndex + endIndex) / 2;
            CountWordsInDocument task1 = new CountWordsInDocument(doc, startIndex, middle, pool);
            CountWordsInDocument task2 = new CountWordsInDocument(doc, middle, endIndex, pool);
            // fork all the tasks
            invokeAll(task1, task2);
            try {
                result += task1.get() + task2.get();  // get()-> get the Integer result
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            //   return result;
        } else {
            for (int i = startIndex; i < endIndex; i++) {
                String[] line = doc.getLine(i);
                LineWordCount lineWordCount = new LineWordCount(line, 0, line.length, theWord);
                pool.invoke(lineWordCount);
                try {
                    result += lineWordCount.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                //  return result;
            }

        }
        System.out.println("Doc Count from " + startIndex + " to " + endIndex + " the result is " + result);
        return result;
    }

    class LineWordCount extends RecursiveTask<Integer> {
        private int endIndex, startIndex;
        private String theWord;
        private int counter;
        private String[] line;

        LineWordCount(String[] line, int startIndex, int endIndex, String theWord) {
            this.endIndex = endIndex;
            this.startIndex = startIndex;
            this.theWord = theWord;
            this.line = line;
        }

        @Override
        protected Integer compute() {
            if (endIndex - startIndex > 100) {
                int middle = (startIndex + endIndex) / 2;
                LineWordCount task1 = new LineWordCount(line, startIndex, middle, theWord);
                LineWordCount task2 = new LineWordCount(line, middle, endIndex, theWord);
                invokeAll(task1, task2);  // invokeAll
                try {
                    counter += task1.get() + task2.get();   // highlight: must += not just =, otherwise the result can't be added together
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                //return counter;
            } else {
                for (int i = startIndex; i < endIndex; i++) {
                    if (line[i].equals(theWord)) {
                        counter++;
                    }
                }
               // return counter;
            }
            return counter;

        }

    }
}
