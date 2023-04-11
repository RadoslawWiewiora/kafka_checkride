import pojo.avro.LoanRequest;
import pojo.avro.LoanRequestsWithCreditScore;

import java.util.Random;

public class CreditBureauMock {
    private static LoanRequestsWithCreditScore addCreditScore(LoanRequest application) {
        Random randomNum = new Random();
        int score = randomNum.nextInt(100);
        return new LoanRequestsWithCreditScore(
                application.getName(),
                application.getSurname(),
                application.getAmount(),
                score);
    }
}
