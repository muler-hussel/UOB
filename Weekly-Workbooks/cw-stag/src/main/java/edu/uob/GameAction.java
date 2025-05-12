package edu.uob;

import java.util.Map;
import java.util.Set;

public class GameAction
{
    private Set<String> subjects;
    private Set<String> consumed;
    private Set<String> produced;
    private String narration;

    public GameAction(Set<String> subjects, Set<String> consumed, Set<String> produced, String narration) {
        this.subjects = subjects;
        this.consumed = consumed;
        this.produced = produced;
        this.narration = narration;
    }

    public Set<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<String> subjects) {
        this.subjects = subjects;
    }

    public Set<String> getConsumed() {
        return consumed;
    }

    public void setConsumed(Set<String> consumed) {
        this.consumed = consumed;
    }

    public Set<String> getProduced() {
        return produced;
    }

    public void setProduced(Set<String> produced) {
        this.produced = produced;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}
