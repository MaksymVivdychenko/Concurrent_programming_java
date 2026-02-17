package task_3;

import java.sql.Array;
import java.util.*;

public class Main {
    private final static Random random = new Random();

    public static void main(String[] args) {
        int maxDelay = 20;
        String[] groups = new String[]{"Group 1", "Group 2", "Group 3"};
        Map<String, List<Student>> journal = generateJournal(groups);

        Thread assist1 = new Thread(() ->
        {
            String groupName = groups[0];
            var group = journal.get(groupName);
            for (int i = 0; i < 100; i++) {
                int grade = random.nextInt(60, 100);
                Student student = group.get(random.nextInt(group.size()));
                student.addGrade(grade);
                System.out.println("Assistant 1 graded student " + student.getName() + " from group "
                        + groupName + " by mark: " + grade);
                try {
                    Thread.sleep(random.nextInt(maxDelay));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread assist2 = new Thread(() ->
        {
            String groupName = groups[1];
            var group = journal.get(groupName);
            for (int i = 0; i < 100; i++) {
                int grade = random.nextInt(60, 100);
                Student student = group.get(random.nextInt(group.size()));
                student.addGrade(grade);
                System.out.println("Assistant 2 graded student " + student.getName() + " from group "
                        + groupName + " by mark: " + grade);
                try {
                    Thread.sleep(random.nextInt(maxDelay));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread assist3 = new Thread(() ->
        {
            String groupName = groups[2];
            var group = journal.get(groupName);
            for (int i = 0; i < 100; i++) {
                int grade = random.nextInt(60, 100);
                Student student = group.get(random.nextInt(group.size()));
                student.addGrade(grade);
                System.out.println("Assistant 3 graded student " + student.getName() + " from group "
                        + groupName + " by mark: " + grade);
                try {
                    Thread.sleep(random.nextInt(maxDelay));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread lecturer = new Thread(() ->
        {
            for (int i = 0; i < 100; i++) {
                String groupName = groups[random.nextInt(groups.length)];
                var group = journal.get(groupName);
                int grade = random.nextInt(60, 100);
                Student student = group.get(random.nextInt(group.size()));
                student.addGrade(grade);
                System.out.println("Lecturer graded student " + student.getName() + " from group "
                        + groupName + " by mark: " + grade);
                try {
                    Thread.sleep(random.nextInt(maxDelay));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assist1.start();
        assist2.start();
        assist3.start();
        lecturer.start();
        try {
            assist1.join();
            assist2.join();
            assist3.join();
            lecturer.join();
        } catch (InterruptedException _) {
        }

        int gradeCount = totalGradesCount(journal);
        System.out.println("Expected grade count: " + 400);
        System.out.println("Total grade count: " + gradeCount);
    }

    private static String generateRandomName() {
        String[] firstNames = {
                "Oleksandr", "Iryna", "Andriy", "Olena", "Dmytro",
                "Svitlana", "Volodymyr", "Natalia", "Ihor", "Mariya"
        };

        String[] lastNames = {
                "Shevchenko", "Bondarchuk", "Tkachenko", "Kovalchuk", "Palytsya",
                "Moroz", "Kravchuk", "Lysenko", "Semerenko", "Savchuk"
        };

        return firstNames[random.nextInt(firstNames.length)] + ' ' + lastNames[random.nextInt(lastNames.length)];
    }

    private static Map<String, List<Student>> generateJournal(String[] groupNames) {
        Map<String, List<Student>> journal = new HashMap<>();
        for (String groupName : groupNames) {
            List<Student> group = new ArrayList<>();
            for (int j = 0; j < 20; j++) {
                group.add(new Student(generateRandomName()));
            }
            journal.put(groupName, group);
        }
        return journal;
    }

    private static int totalGradesCount(Map<String, List<Student>> journal)
    {
        int sum = 0;
        for (var group : journal.entrySet())
        {
            for (var student : group.getValue())
            {
                sum += student.getGradesCount();
            }
        }
        return sum;
    }
}
