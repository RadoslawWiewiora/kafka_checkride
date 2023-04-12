import org.apache.kafka.streams.kstream.ValueJoiner;
import pojo.avro.LoanRequest;
import pojo.avro.LoanRequestsWithCreditScore;

public class LoanRequestCreditScoreJoiner implements ValueJoiner<LoanRequest, client_credit_score, LoanRequestsWithCreditScore> {
    @Override
    public LoanRequestsWithCreditScore apply(LoanRequest request, client_credit_score creditScore) {

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
