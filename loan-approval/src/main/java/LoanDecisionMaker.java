import pojo.avro.LoanDecision;
import pojo.avro.LoanRequestsWithCreditScore;

/**
 * This class is responsible for making decision about loan approval or reject.
 * Here we can add any business logic related to this process. For now, it's
 * simple: credit score > 70 approve.
 */
public class LoanDecisionMaker {
    public static LoanDecision AnalyzeApplication(LoanRequestsWithCreditScore application) {
        LoanDecision decision = new LoanDecision();
        decision.setName(application.getName());
        decision.setSurname(application.getSurname());
        decision.setAmount(application.getAmount());
        decision.setSource(application.getCreditScoreSource());

        decision.setApproved(application.getCreditScore() > 70);

        return decision;
    }
}
