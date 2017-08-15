public class ParserToken {
    private String tokenType;
    private String tokenText;

    public ParserToken() {
        this.tokenType = "";
        this.tokenText = "";
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getTokenText() {
        return tokenText;
    }

    public void setTokenType(String tokenType_) {
        this.tokenType = tokenType_;
    }

    public void setTokenText(String tokenText_) {
        this.tokenText = tokenText_;
    }
}
