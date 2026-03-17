package task_4;

import javax.print.Doc;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class FolderCalculationTask extends RecursiveTask<List<Path>>
{
    private final Folder folder;
    private final HashSet<String> keywords;

    public FolderCalculationTask(Folder folder, HashSet<String> keywords)
    {
        this.folder = folder;
        this.keywords = keywords;
    }
    @Override
    protected List<Path> compute() {
        var folderTasks = new LinkedList<RecursiveTask<List<Path>>>();
        var documentTasks = new HashMap<Document, RecursiveTask<Integer>>();
        var paths = new LinkedList<Path>();
        for (var f : folder.getSubFolders())
        {
            var task = new FolderCalculationTask(f, keywords);
            folderTasks.add(task);
            task.fork();
        }

        for (var d : folder.getDocuments())
        {
            var wordsEnteringTask = new DocumentCalculationTask(d, 0, d.getLines().size(), keywords);
            documentTasks.put(d, wordsEnteringTask);
            wordsEnteringTask.fork();
        }

        for (var folderTask : folderTasks)
        {
            paths.addAll(folderTask.join());
        }

        for (var documentTask : documentTasks.entrySet())
        {
            int wordsEncounters = documentTask.getValue().join();
            if(wordsEncounters > 5)
            {
                paths.add(documentTask.getKey().getPath());
            }
        }

        return paths;
    }
}