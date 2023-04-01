import pojo.avro.CreditScore;
import pojo.avro.LoanApplication;
import pojo.avro.LoanDecision;

public class LoanDecisionMaker {
    public static LoanDecision AnalyzeApplication(LoanApplication application, CreditScore score) {
        if (score.getCreditScore() > 80) {
            return new LoanDecision(score.getName(), score.getSurname(), application.getAmount(), true);
        }
        else {
            return new LoanDecision(score.getName(), score.getSurname(), application.getAmount(), false);
        }
    }
}
