package task_3;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();
    static void main(String[] args) throws IOException {
        File path = new File("/home/maksym_vivdychenko/IdeaProjects/lab_4_texts");
        Folder root = Folder.fromDirectory(path);
        var words = forkJoinPool.invoke(new FolderCalculationTask(root));
        System.out.print("Common words: ");
        for (var w : words)
        {
            System.out.print(w + " ");
        }
    }
}
