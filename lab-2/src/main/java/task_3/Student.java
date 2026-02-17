package task_3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student {
    private String name;
    private List<Integer> grades;

    public Student(String name) {
        this.name = name;
        grades = Collections.synchronizedList(new ArrayList<>());
    }

    public void addGrade(int grade)
    {
        grades.add(grade);
    }

    public String getName()
    {
        return name;
    }

    public int getGradesCount()
    {
        return grades.size();
    }
}
