import java.util.ArrayList;
import java.util.List;

public class ParserTree {
    private ParserToken token;
    private String ruleName;
    private ParserTree parent;
    private List<ParserTree> children;

    public ParserTree() {
        this.token = new ParserToken();
        this.ruleName = "";
        this.parent = null;
        this.children = new ArrayList<>();
    }

    public ParserToken getToken() {
        return token;
    }

    public String getRuleName() {
        return ruleName;
    }

    public ParserTree getParent() {
        return parent;
    }

    public ParserTree getChild(int i) {
        return children.get(i);
    }

    public void setToken(ParserToken token_) {
        this.token = token_;
    }

    public void setRuleName(String ruleName_) {
        this.ruleName = ruleName_;
    }

    public void setParent(ParserTree parent_) {
        this.parent = parent_;
    }

    public void addChild(ParserTree child) {
        this.children.add(child);
    }

    public ParserTree getLastChild() {
        return children.get(children.size() - 1);
    }

    public int getChildCount() {
        return children.size();
    }
}
