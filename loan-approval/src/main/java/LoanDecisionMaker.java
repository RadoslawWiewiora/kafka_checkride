import pojo.avro.LoanDecision;
import pojo.avro.LoanRequestsWithCreditScore;

/**
 * This class is responsible for making decision about loan approval or reject.
 * Here we can add any business logic related to this process. For now, it's
 * simple: credit score > 70 approve.
 */
public class LoanDecisionMaker {
    public static LoanDecision AnalyzeApplication(LoanRequestsWithCreditScore application) {
        if (application.getCreditScore() > 70) {
            return new LoanDecision(application.getName(), application.getSurname(), application.getAmount(), true);
        }
        else {
            return new LoanDecision(application.getName(), application.getSurname(), application.getAmount(), false);
        }
    }
}
