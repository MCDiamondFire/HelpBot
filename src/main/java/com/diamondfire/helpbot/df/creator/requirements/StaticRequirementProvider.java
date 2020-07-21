package com.diamondfire.helpbot.df.creator.requirements;

public class StaticRequirementProvider implements RequirementProvider {

    private final int requirement;

    public StaticRequirementProvider(int requirement) {
        this.requirement = requirement;
    }

    @Override
    public int getRequirement() {
        return requirement;
    }
}
