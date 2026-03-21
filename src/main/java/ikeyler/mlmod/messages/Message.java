package ikeyler.mlmod.messages;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    public final Pattern[] patterns;
    private boolean active;
    private Matcher matcher;
    public Message(String... patterns) {
        this.patterns = Arrays.stream(patterns).map(Pattern::compile).toArray(Pattern[]::new);
        this.active = true;
    }
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Message message = (Message) object;
        return Arrays.equals(this.patterns, message.patterns);
    }
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.patterns);
    }
    public Matcher getMatcher() {
        return this.matcher;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isActive() {
        return this.active;
    }
    public boolean matches(String message) {
        return Arrays.stream(patterns).filter(Objects::nonNull).anyMatch(pattern -> {
            Matcher matcher = pattern.matcher(message);
            if (matcher.matches()) {
                this.matcher = matcher;
                return true;
            }
            return false;
        });
    }
}
