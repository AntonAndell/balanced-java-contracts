package network.balanced.score.core;

import com.iconloop.score.test.Account;
import com.iconloop.score.test.Score;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;
import com.iconloop.score.token.irc2.IRC2Mintable;
import com.eclipsesource.json.Json;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import score.Context;
import score.Address;
import score.annotation.External;

import java.math.BigInteger;
import java.math.MathContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import network.balanced.score.core.DexMock;
import network.balanced.score.core.token1Mintable;

class User {
    public BigInteger collateral;
    public BigInteger loan;

    public User() {
        this.collateral = BigInteger.ZERO;
        this.loan = BigInteger.ZERO;
    }
}

class ReferenceLoan {
    public BigInteger totalLoan = BigInteger.ZERO;
    public BigInteger totalCollateral= BigInteger.ZERO;
    public BigInteger price = BigInteger.valueOf(2);

    private Map<Address, User> users = new HashMap<>();

    public void depositAndBorrow(Address address, BigInteger collateral, BigInteger loan) {
        User loanTaker = users.getOrDefault(address, new User());
        loanTaker.loan = loanTaker.loan.add(loan);
        loanTaker.collateral = loanTaker.collateral.add(collateral);

        totalCollateral = totalCollateral.add(collateral);
        totalLoan = totalLoan.add(loan);
        users.put(address, loanTaker);
    }

    public void raisePrice(BigInteger collateralToSell, BigInteger loanToRepay) {
        for(Map.Entry<Address, User>  entry : users.entrySet()) {
            Address address = entry.getKey();
            User loanTaker = entry.getValue();
            BigInteger loanShare = loanTaker.loan.multiply(loanToRepay).divide(totalLoan);
            BigInteger collateralShare = loanTaker.loan.multiply(collateralToSell).divide(totalLoan);

            loanTaker.collateral = loanTaker.collateral.subtract(collateralShare);
            loanTaker.loan = loanTaker.loan.subtract(loanShare);
        }

        totalCollateral = totalCollateral.subtract(collateralToSell);
        totalLoan = totalLoan.subtract(loanToRepay);
    }


    public void lowerPrice(BigInteger loanToAdd, BigInteger colalteralToAdd) {
        for(Map.Entry<Address, User>  entry : users.entrySet()) {
            Address address = entry.getKey();
            User loanTaker = entry.getValue();
            BigInteger loanShare = loanTaker.loan.multiply(loanToAdd).divide(totalLoan);
            BigInteger collateralShare = loanTaker.loan.multiply(colalteralToAdd).divide(totalLoan);

            loanTaker.collateral = loanTaker.collateral.add(collateralShare);
            loanTaker.loan = loanTaker.loan.add(loanShare);
            
        }

        totalCollateral = totalCollateral.add(colalteralToAdd);
        totalLoan = totalLoan.add(loanToAdd);
    }

    
    public void repayLoan(Address address, BigInteger repaidAmount) {
        User loanTaker = users.get(address);
        loanTaker.loan = loanTaker.loan.subtract(repaidAmount);
        totalLoan = totalLoan.subtract(repaidAmount);
    }

    public void withdraw(Address address, BigInteger collateral) {
      User loanTaker = users.get(address);
      loanTaker.collateral = loanTaker.collateral.subtract(collateral);
      totalCollateral = totalCollateral.subtract(collateral);
    }

    public Map<String, BigInteger> getPosition(Address address) {
        User loanTaker = users.getOrDefault(address, new User());
        return Map.of(
                "Loan", loanTaker.loan,
                "Collateral", loanTaker.collateral
            );
    }
}

@DisplayName("Loans Tests")
class LoansTests extends TestBase {

    private static final ServiceManager sm = getServiceManager();

    private static final Account owner = sm.createAccount();
    private static Score loans;
    private static Score sicx;
    private static Score bnusd;
    private static Score dex;

    // Loans score deployment settings.
    private static final String nameLoans = "Loans";
    private final ArrayList<Account> accounts = new ArrayList<>();
    private static final BigInteger MINT_AMOUNT = BigInteger.TEN.pow(40);
    private ReferenceLoan referenceLoan;

    // Sicx score deployment settings.
    private static final String nameSicx = "Staked icx";
    private static final String symbolSicx = "SICX";
    private static final int decimalsSicx = 18;
    private static final BigInteger initalsupplySicx = BigInteger.TEN.pow(50);

    // Bnusd score deployment settings.
    private static final String nameBnusd = "Balanced usd";
    private static final String symbolBnusd = "BNUSD";
    private static final int decimalsBnusd = 18;
    private static final BigInteger initalsupplyBnusd = BigInteger.TEN.pow(50);

    public static class sICXToken extends token1Mintable {
        public sICXToken(String _name, String _symbol, int _decimals, BigInteger _totalSupply) {
            super(_name, _symbol, _decimals);
            mintTo(Context.getCaller(), _totalSupply);
        }
    }
    public static class IRC2MintAndBurnable extends IRC2Mintable {
        public IRC2MintAndBurnable(String _name, String _symbol, int _decimals, BigInteger _totalSupply) {
            super(_name, _symbol, _decimals);
            mintTo(Context.getCaller(), _totalSupply);
        }

        @External
        public void burnFrom(Address address, BigInteger _amount) {
            _burn(address, _amount);
        }
    }

    private void setupAccounts() {
        int numberOfAccounts = 10;
        for (int accountNumber = 0; accountNumber < numberOfAccounts; accountNumber++) {
            Account account = sm.createAccount();
            accounts.add(account);
            sicx.invoke(owner, "mintTo", account.getAddress(), MINT_AMOUNT);
        }
    }

    private void setupDex() throws Exception{
        Account account = sm.createAccount();
        BigInteger initalsICX = BigInteger.TEN.pow(23);
        BigInteger initalbnUSD = BigInteger.TEN.pow(23);
        sicx.invoke(owner, "mintTo", account.getAddress(), initalsICX.add(BigInteger.TEN.pow(18)));
        bnusd.invoke(owner, "mintTo", account.getAddress(), initalbnUSD.add(BigInteger.TEN.pow(18)));  
        dex = sm.deploy(owner, DexMock.class, sicx.getAddress(), bnusd.getAddress());
        sicx.invoke(account, "transfer", dex.getAddress(), initalsICX, new byte[0]);
        bnusd.invoke(account, "transfer", dex.getAddress(), initalbnUSD, new byte[0]);
    }

    private void takeLoan(Account account, int collateral, int loan) {
        byte[] params = tokenData("depositAndBorrow", Map.of("amount",  loan));
        sicx.invoke(account, "transfer", loans.getAddress(), BigInteger.valueOf(collateral).multiply(BigInteger.TEN.pow(18)), params);
        referenceLoan.depositAndBorrow(account.getAddress(),  BigInteger.valueOf(collateral).multiply(BigInteger.TEN.pow(18)), BigInteger.valueOf(loan).multiply(BigInteger.TEN.pow(18)));
    }

    private void lowerPrice(BigInteger loanToAdd) {
        loanToAdd = loanToAdd.multiply(BigInteger.TEN.pow(18));
        Map<String, BigInteger> prePostion = (Map<String, BigInteger>)loans.call("get");
        loans.invoke(owner, "lowerPrice", loanToAdd);  
        Map<String, BigInteger> postPosition = (Map<String, BigInteger>)loans.call("get");

        assertEquals(prePostion.get("Loan"), postPosition.get("Loan").subtract(loanToAdd));
        BigInteger collateralAdded = postPosition.get("RebalanceCollateral").subtract(prePostion.get("RebalanceCollateral"));

        referenceLoan.lowerPrice(loanToAdd, collateralAdded);
    }

    private void raisePrice(BigInteger collateralToSell) {
        collateralToSell = collateralToSell.multiply(BigInteger.TEN.pow(18));
        Map<String, BigInteger> prePostion = (Map<String, BigInteger>)loans.call("get");
        loans.invoke(owner, "raisePrice", collateralToSell);
        Map<String, BigInteger> postPosition = (Map<String, BigInteger>)loans.call("get");
        
        assertEquals(prePostion.get("RebalanceCollateral"), postPosition.get("RebalanceCollateral").add(collateralToSell));
        BigInteger bnusdAdded  = prePostion.get("Loan").subtract(postPosition.get("Loan"));


        referenceLoan.raisePrice(collateralToSell, bnusdAdded);
    }

    private void repayLoan(Account account, BigInteger repaidAmount) {
        loans.invoke(account, "repayLoan", repaidAmount.multiply(BigInteger.TEN.pow(18)));
        referenceLoan.repayLoan(account.getAddress(), repaidAmount.multiply(BigInteger.TEN.pow(18)));
    }

    private void withdraw(Account account, BigInteger collateralToWithdraw) {
        loans.invoke(account, "withdraw", collateralToWithdraw.multiply(BigInteger.TEN.pow(18)));
        referenceLoan.withdraw(account.getAddress(), collateralToWithdraw.multiply(BigInteger.TEN.pow(18)));
    }

    public byte[] tokenData(String method, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<>();
        map.put("method", method);
        map.put("params", params);
        JSONObject data = new JSONObject(map);
        return data.toString().getBytes();
    }

    @BeforeEach
    public void setup() throws Exception {
        sicx = sm.deploy(owner, sICXToken.class, nameSicx, symbolSicx, decimalsSicx, initalsupplySicx);
        loans = sm.deploy(owner, Loans.class, nameLoans);
        bnusd = sm.deploy(owner, IRC2MintAndBurnable.class, nameBnusd, symbolBnusd, decimalsBnusd, initalsupplyBnusd);

        setupAccounts();
        setupDex();
        loans.invoke(owner, "setSicx", sicx.getAddress());
        loans.invoke(owner, "setbnUSD", bnusd.getAddress());
        loans.invoke(owner, "setDex", dex.getAddress());
        bnusd.invoke(owner, "setMinter", loans.getAddress());
        referenceLoan = new ReferenceLoan();
    }

    // @Test
    // void singleLoan() {
    //     byte[] params = tokenData("depositAndBorrow", Map.of("amount",  100));
    //     sicx.invoke(owner, "transfer", loans.getAddress(), BigInteger.TEN.pow(21), params);
    //     System.out.println(loans.call("getPosition", owner.getAddress()));
    //     //System.out.println(stabilityFund.call("getDaofund"));

    // }

    // @Test
    // void testLoan() {
    //     Account account1 = accounts.get(0);
    //     Account account2 = accounts.get(1);
    //     Account account3 = accounts.get(2);
    //     Account account4 = accounts.get(3);

    //     takeLoan(account1, 1020, 100);
    //     takeLoan(account2, 2000, 400);
    //     raisePrice(BigInteger.valueOf(125));
    //     takeLoan(account3, 1000, 100);
    //     raisePrice(BigInteger.valueOf(35));
    //     takeLoan(account4, 1000, 200);
    // }

    @Test
    void testLoan2() {

        Account account1 = accounts.get(0);
        Account account2 = accounts.get(1);
        Account account3 = accounts.get(2);
        Account account4 = accounts.get(3);
        Account account5 = accounts.get(4);
        Account account6 = accounts.get(5);
        Account account7 = accounts.get(6);
        Account account8 = accounts.get(7);

        takeLoan(account1, 1000, 100);
        takeLoan(account2, 2000, 400);
        lowerPrice(BigInteger.valueOf(100));
        raisePrice(BigInteger.valueOf(120));
        takeLoan(account3, 1000, 100);
        raisePrice(BigInteger.valueOf(30));
        takeLoan(account4, 4020, 250);
        
        takeLoan(account5, 2300, 110);
        lowerPrice(BigInteger.valueOf(60));
        takeLoan(account6, 400, 40);
        lowerPrice(BigInteger.valueOf(60));
        lowerPrice(BigInteger.valueOf(60));

        takeLoan(account7, 7000, 1800);

    }

    @AfterEach
    void comparePositions() {
        Map<String, BigInteger> ratio = (Map<String, BigInteger>)loans.call("get");
        BigDecimal collateral = new BigDecimal(ratio.get("RebalanceCollateral"));
        BigDecimal loan = new BigDecimal(ratio.get("Loan"));
        System.out.println("ratio:" + collateral.divide(loan, MathContext.DECIMAL128).toString());
        System.out.println(" Collateral: " + ratio.get("RebalanceCollateral").toString() + " Loan: " + ratio.get("Loan").toString());
        System.out.println(referenceLoan.totalLoan.toString());
        BigInteger LoanTotal = BigInteger.ZERO;
        BigInteger referenceLoanTotal = BigInteger.ZERO;
        for (Account account : accounts) {
            Map<String, BigInteger> position = (Map<String, BigInteger>)loans.call("getPosition", account.getAddress());
            Map<String, BigInteger> referencePosition = referenceLoan.getPosition(account.getAddress());

            referenceLoanTotal = referenceLoanTotal.add(referencePosition.get("Loan"));
            LoanTotal = LoanTotal.add(referencePosition.get("Loan"));

            if (!position.get("Loan").equals(BigInteger.ZERO)) {
                // if (position.get("Collateral").divide(position.get("Loan")).equals(referencePosition.get("Collateral").divide(referencePosition.get("Loan")))){
                System.out.println ("collateral");
                System.out.println (position.get("Collateral").toString() + " == " + referencePosition.get("Collateral").toString());
                System.out.println ("loans");
                System.out.println (position.get("Loan").toString() + " == " + referencePosition.get("Loan").toString());
                // }
            }
        }
        System.out.format("LoanTotal: " + LoanTotal.toString() + "\n");
        System.out.format("referenceLoanTotal: " + referenceLoanTotal.toString() + "\n");
    }  
}