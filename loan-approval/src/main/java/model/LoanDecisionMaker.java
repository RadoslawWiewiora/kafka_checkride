package model;

import pojo.avro.CreditScore;
import pojo.avro.LoanApplication;
import pojo.avro.LoanDecision;

/**
 * This class is responsible for making decision about loan approval or reject.
 * Here we can add any business logic related with this process. For now, it's
 * simple
 */
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
