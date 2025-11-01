package ikeyler.mlmod.messages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    private final String template;
    private String newTemplate;
    private Pattern pattern;
    private boolean active;
    private Matcher matcher;
    public Message(String template) {
        this.template = template;
        this.newTemplate = template;
        this.active = true;
        this.pattern = Pattern.compile(getTemplate());
    }
    public String getTemplate() {
        return this.template;
    }
    public String getFixedTemplate() {
        return this.template.replace("\n", "\\n");
    }
    public Matcher getMatcher() {
        return this.matcher;
    }
    public String getNewTemplate() {
        return this.newTemplate;
    }
    public void setNewTemplate(String newTemplate) {
        this.newTemplate = newTemplate.replace("\\n", "\n");
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public boolean isActive() {
        return this.active;
    }
    public boolean matches(String message) {
        this.pattern = Pattern.compile(this.getNewTemplate());
        Matcher matcher = pattern.matcher(message);
        if (matcher.matches()) {
            this.matcher = matcher;
            return true;
        }
        return false;
    }
}
