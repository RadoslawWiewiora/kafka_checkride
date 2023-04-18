import org.apache.kafka.streams.kstream.ValueJoiner;
import pojo.avro.LoanRequest;
import pojo.avro.LoanRequestsWithCreditScore;

public class LoanRequestCreditScoreJoiner implements ValueJoiner<LoanRequest, credit_scores, LoanRequestsWithCreditScore> {
    @Override
    public LoanRequestsWithCreditScore apply(LoanRequest request, credit_scores creditScore) {

        LoanRequestsWithCreditScore withCreditScore = new LoanRequestsWithCreditScore();
        withCreditScore.setName(request.getName());
        withCreditScore.setSurname(request.getSurname());
        withCreditScore.setAmount(request.getAmount());
        if (creditScore != null) {
            withCreditScore.setCreditScore(creditScore.getCreditScore());
            withCreditScore.setCreditScoreSource(CreditScoreSource.INTERNAL.name());
        }

        return withCreditScore;
    }
}
