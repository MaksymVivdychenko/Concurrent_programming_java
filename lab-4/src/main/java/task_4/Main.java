package task_4;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();
    static void main(String[] args) throws IOException {
        File path = new File("/home/maksym_vivdychenko/IdeaProjects/lab_4_texts");
        HashSet<String> ADDITIONAL_IT_WORDS = new HashSet<>(Arrays.asList(
                "java", "server", "algorithm", "database", "network",
                "api", "framework", "compiler", "cloud", "security"
        ));

        Folder root = Folder.fromDirectory(path);
        var pathes = forkJoinPool.invoke(new FolderCalculationTask(root, ADDITIONAL_IT_WORDS));
        System.out.println("Files from IT sphere: ");
        for (var p : pathes)
        {
            System.out.println(p.toString());
        }
    }
}
