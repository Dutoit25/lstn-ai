package za.co.lstn.enums;
import java.util.Arrays;
import java.util.List;

public enum EmailCategory {
    PRODUCT_QUESTIONS("PRODUCT_QUESTIONS", Arrays.asList("feature", "specification", "compatibility", "usage", "guide")),
    BILLING_PROBLEMS("billing_problems", Arrays.asList("payment", "invoice", "charge", "bill", "subscription")),
    TECHNICAL_SUPPORT("technical_support", Arrays.asList("error", "problem", "bug", "issue", "crash", "install")),
    CLAIM_ISSUES("CLAIM_ISSUES", Arrays.asList("claim", "claims", "complaints")),
    //add dummy category
    NO_CATEGORY("NO_CATEGORY", Arrays.asList("xxxxx", "aaaaa"));
    private final String name;
    private final List<String> keywords;

    EmailCategory(String name, List<String> keywords) {
        this.name = name;
        this.keywords = keywords;
    }

    public String getName() {
        return name;
    }

    public List<String> getKeywords() {
        return keywords;
    }
}
