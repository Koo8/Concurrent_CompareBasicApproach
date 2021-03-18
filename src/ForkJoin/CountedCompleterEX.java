package ForkJoin;

import java.util.concurrent.CountedCompleter;

/**
 * Of the three concrete ForkJoinTasks, CountedCompleter is the most efficient,
 * since it avoids the blocking join() by providing its own continuations.
 * see https://august.nagro.us/CountedCompleter.html for countedCompleter.class explanation
 * if many jobs are being submitted to the same pool, ForkJoinTask::getSurplusQueuedTaskCount
 * can be used to dynamically decide whether to spawn sub-tasks.
 *
 * Conclusion: Don’t use parallel streams, unless for prototyping or one-off jobs…
 * a tailored CountedCompleter sub-class is simply better!
 */

public class CountedCompleterEX {

    // TODO:
}

final class CustomCC extends CountedCompleter<Long> {

    private static final int TARGET_LEAVES =
            Runtime.getRuntime().availableProcessors() << 2;

    // ie, every leaf should be process at least 10_000 elements
    private static final int MIN_SIZE_PER_LEAF = 10_000;

    private static int computeInitialPending(int size) {
        // if x = size / MIN_SIZE_PER_LEAF, and x < TARGET_LEAVES,
        // create x leaves instead. This ensures that every leaf does
        // at least MIN_SIZE_PER_LEAF operations.
        int leaves = Math.min(TARGET_LEAVES, size / MIN_SIZE_PER_LEAF);
        // If the total # leaves = 0, then pending = 0 since we shouldn't fork.
        // Otherwise, pending = log2(leaves), as explained in the doc
        return leaves == 0
                ? 0
                : 31 - Integer.numberOfLeadingZeros(leaves);
    }

    final int[] arr;
    int pos, size;
    long res = 0;
    final CustomCC[] subs;

    public CustomCC(int[] arr) {
        this(null, arr, arr.length, 0, computeInitialPending(arr.length));
    }

    private CustomCC(CustomCC parent, int[] arr, int size, int pos, int pending) {
        super(parent, pending);
        this.arr = arr; this.size = size; this.pos = pos;
        subs = new CustomCC[pending];
    }

    @Override
    public void compute() {
        // subs.length === getPendingCount()
        for (int p = subs.length - 1; p >= 0; --p) {
            size >>>= 1;
            CustomCC sub = new CustomCC(this, arr, size, pos + size, p);
            subs[p] = sub;
            sub.fork();
        }

        // needed for c2 to remove array index checking...
        // modified from Spliterator class
        int[] a; int i, hi;
        if ((a = arr).length >= (hi = pos + size) && (i = pos) >= 0 && i < hi) {
            do {
                int e = a[i];
                if (e < 150) res += e % 5; //todo: not readable -> "fnv(e) % 5 ";
            } while (++i < hi);
        }

        tryComplete();
    }

    @Override
    public void onCompletion(CountedCompleter<?> caller) {
        for (CustomCC sub : subs) res += sub.res;
    }

    @Override
    public Long getRawResult() {
        return res;
    }
}
