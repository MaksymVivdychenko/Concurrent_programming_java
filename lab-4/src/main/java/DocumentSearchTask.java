import java.util.concurrent.RecursiveTask;

class DocumentSearchTask extends RecursiveTask<Long> {
    private final Document document;
    private final String searchedWord;

    DocumentSearchTask(Document document, String searchedWord) {
        super();
        this.document = document;
        this.searchedWord = searchedWord;
    }

    @Override
    protected Long compute() {
        long count = 0;
        for (String line : document.getLines()) {
            for (String word : line.split("(\\s|\\p{Punct})+")) {
                if (searchedWord.equals(word)) count++;
            }
        }
        return count;
    }
}