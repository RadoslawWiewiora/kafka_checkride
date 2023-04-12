import pojo.avro.LoanRequestsWithCreditScore;

import java.util.Random;

public class CreditBureauMock {
    public static LoanRequestsWithCreditScore addCreditScore(LoanRequestsWithCreditScore application) {
        Random randomNum = new Random();
        int score = randomNum.nextInt(10, 100);
        return new LoanRequestsWithCreditScore(
                application.getName(),
                application.getSurname(),
                application.getAmount(),
                score,
                CreditScoreSource.EXTERNAL.name());
    }
}
