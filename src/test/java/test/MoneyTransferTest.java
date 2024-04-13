package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.DashboardPage;
import page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static data.DataHelper.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {
    DashboardPage dashboardPage;
    int firstCardBalance;
    int secondCardBalance;
    CardInfo firstCardInfo;
    CardInfo secondCardInfo;


    @BeforeEach
    void setup() {
        open("http://localhost:9999", LoginPage.class);
        var loginPage = new LoginPage();
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCode(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        firstCardInfo = getCardFirst();
        secondCardInfo = getCardSecond();
        firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
        secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);


    }

    @Test
    void shouldTransferMoneyBetweenOwnCards() {
        var amount = generateValidAmount(firstCardBalance);
        var expectedDBalanceFirstCard = firstCardBalance - amount;
        var expectedDBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        transferPage.validTransfer(String.valueOf(amount), firstCardInfo);
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assertAll(() -> assertEquals(expectedDBalanceFirstCard, actualBalanceFirstCard),
                () -> assertEquals(expectedDBalanceSecondCard, actualBalanceSecondCard));
    }

    @Test
    void shouldntTransferMoneyBetweenOwnCards() {
        var amount = generateInValidAmount(firstCardBalance);
        var expectedDBalanceFirstCard = firstCardBalance - amount;
        var expectedDBalanceSecondCard = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), firstCardInfo);
        transferPage.errorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте");
        var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
        assertAll(() -> assertEquals(expectedDBalanceFirstCard, actualBalanceFirstCard),
                () -> assertEquals(expectedDBalanceSecondCard, actualBalanceSecondCard));
    }

}
