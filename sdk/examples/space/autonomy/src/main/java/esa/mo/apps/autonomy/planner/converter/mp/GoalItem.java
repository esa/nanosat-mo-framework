package esa.mo.apps.autonomy.planner.converter.mp;

/**
 * GoalItem is a data class used by MPConverter
 */
public class GoalItem {
    private String name;
    private String[] arguments;

    public GoalItem(String name, String[] arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public String[] getArguments() {
        return arguments;
    }
}
