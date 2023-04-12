import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.avro.LoanDecision;
import pojo.avro.LoanRequestsWithCreditScore;

/**
 * This class is responsible for making decision about loan approval or reject.
 * Here we can add any business logic related to this process. For now, it's
 * simple: credit score > 70 approve.
 */
public class LoanDecisionMaker {

    private static final Logger log = LoggerFactory.getLogger(LoanApprovalApp.class.getSimpleName());

    public static LoanDecision AnalyzeApplication(LoanRequestsWithCreditScore request) {
        LoanDecision decision = new LoanDecision();
        decision.setName(request.getName());
        decision.setSurname(request.getSurname());
        decision.setAmount(request.getAmount());
        decision.setSource(request.getCreditScoreSource());
        decision.setApproved(request.getCreditScore() > 70);

        log.info(String.format("DECISION: %s score: %s, source: %s", decision.getApproved(), request.getCreditScore(), request.getCreditScoreSource()));
        return decision;
    }
}
