package network.balanced.score.core;

import score.Address;

import java.math.BigInteger;
import java.math.MathContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class User {
    public BigInteger collateral;
    public BigInteger loan;

    public User() {
        this.collateral = BigInteger.ZERO;
        this.loan = BigInteger.ZERO;
    }
}

public class ReferenceLoan {
    public BigInteger totalLoan = BigInteger.ZERO;
    public BigInteger totalCollateral= BigInteger.ZERO;
    public BigInteger price = BigInteger.valueOf(2);

    private Map<Address, User> users = new HashMap<>();
    public ReferenceLoan(){}
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
