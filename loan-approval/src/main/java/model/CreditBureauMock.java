package model;

import pojo.avro.LoanApplication;
import pojo.avro.LoanApplicationWithCreditScore;

import java.util.Random;

public class CreditBureauMock {
    private static LoanApplicationWithCreditScore addCreditScore(LoanApplication application) {
        Random randomNum = new Random();
        int score = randomNum.nextInt(100);
        return new LoanApplicationWithCreditScore(
                application.getName(),
                application.getSurname(),
                application.getAmount(),
                score);
    }
}
