package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.DictDB;
import score.Address;
import score.annotation.External;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.math.BigInteger;

import java.util.Map;

import score.ByteArrayObjectWriter;
import score.Context;
import score.ObjectReader;

import java.math.BigInteger;


public class Loans {

    class LoanTaker {
        public BigInteger collateral;
        public BigInteger rebalanceTokens;
        public BigInteger lockedLoan;

        public LoanTaker() {
            collateral = BigInteger.ZERO;
            rebalanceTokens = BigInteger.ZERO;
            lockedLoan = BigInteger.ZERO;
        }
    }

    // Contract name.
    private final String name;
    
    private static final BigInteger POINTS = BigInteger.TEN.pow(18);

    //Mock variables
    private static final BigInteger PRICE = BigInteger.valueOf(2);
    private static final BigInteger LTV = BigInteger.valueOf(35).pow(17);
    private static final BigInteger BASE_LTV = BigInteger.valueOf(5).pow(17);

    // Balanced contract addresses.
    private final VarDB<Address> sicx = Context.newVarDB("sICX", Address.class);
    private final VarDB<Address> bnusd = Context.newVarDB("bnUSD", Address.class);
    private final VarDB<Address> dex = Context.newVarDB("dex", Address.class);

    private final VarDB<BigInteger> rebalanceCollateral = Context.newVarDB("RCollateral", BigInteger.class);
    private final VarDB<BigInteger> rebalaceLoan = Context.newVarDB("RLoan", BigInteger.class);
    private final VarDB<BigInteger> totalRebalanceShares = Context.newVarDB("TRShares", BigInteger.class);

    private final VarDB<Address> excpectedToken = Context.newVarDB("excpectedToken", Address.class);
    private final VarDB<BigInteger> amountReceived = Context.newVarDB("amountReceived", BigInteger.class);


    private final DictDB<Address, LoanTaker> loanTakers = Context.newDictDB("Loan_Takers",LoanTaker.class);
    
    public Loans(String name) {
        this.name = name;
        rebalanceCollateral.set(BigInteger.ZERO);
        rebalaceLoan.set(BigInteger.ZERO);
        totalRebalanceShares.set(BigInteger.ZERO);
    }

    @External(readonly = true)
    public String name() {
        return name;
    }

    @External
    public void setSicx(Address address) {
        sicx.set(address);
    }

    @External(readonly = true)
    public Address getSicx() {
        return sicx.get();
    }

    @External 
    public void setbnUSD(Address address) {
        bnusd.set(address);
    }

    @External(readonly = true)
    public Address getbnUSD() {
        return bnusd.get();
    }

    @External
    public void setDex(Address address) {
        dex.set(address);
    }

    @External(readonly = true)
    public Address getDex() {
        return dex.get();
    }

    @External(readonly = true)
    public Map<String, BigInteger> get() {
        return Map.of(
                "RebalanceCollateral", rebalanceCollateral.get(),
                "Loan", rebalaceLoan.get()
        );
    }

    @External(readonly = true)
    public Map<String, BigInteger> getPosition(Address address) {
        LoanTaker user  = loanTakers.getOrDefault(address, new LoanTaker());

        return Map.of(
                "Collateral", getUserCollateral(user),
                "Loan", getUserLoan(user)
        );
    }

    @External
    public BigInteger raisePrice(BigInteger amount) {
        excpectedToken.set(bnusd.get());
        byte[] data = createSwapData(bnusd.get());
        transferToken(sicx.get(), dex.get(), amount, data);
    
        rebalanceCollateral.set(rebalanceCollateral.get().subtract(amount));
        rebalaceLoan.set(rebalaceLoan.get().subtract(amountReceived.get()));

        BigInteger amountRepaid = amountReceived.get();
        amountReceived.set(BigInteger.ZERO);

        return amountRepaid;
    }
    
    @External
    public BigInteger lowerPrice(BigInteger amount) {
        Context.call(bnusd.get(), "mintTo", Context.getAddress(), amount);

        excpectedToken.set(sicx.get());
        byte[] data = createSwapData(sicx.get());
        transferToken(bnusd.get(), dex.get(), amount, data);

        rebalanceCollateral.set(rebalanceCollateral.get().add(amountReceived.get()));
        rebalaceLoan.set(rebalaceLoan.get().add(amount));

        BigInteger amountToAdd = amountReceived.get();
        amountReceived.set(BigInteger.ZERO);

        return amountToAdd;
    }

    @External
    public void withdraw(BigInteger collateral) {
        LoanTaker user  = loanTakers.get(Context.getCaller());
        user.collateral = user.collateral.subtract(collateral);

        BigInteger collateralInPool = getUserCollateral(user);
        loanTakers.set(Context.getCaller(), user);
    }

    @External
    public void repayLoan(BigInteger repaidAmount) {
        LoanTaker user  = loanTakers.get(Context.getCaller());

        BigInteger loan = getUserLoan(user);
        BigInteger collateral = getUserCollateral(user);

        BigInteger removedTokens = repaidAmount.multiply(totalRebalanceShares.get()).divide(rebalaceLoan.get());
        BigInteger removedpoolCollateral =  removedTokens.multiply(rebalanceCollateral.get()).divide(totalRebalanceShares.get());
        user.collateral = user.collateral.add(removedpoolCollateral);
        user.rebalanceTokens = user.rebalanceTokens.subtract(removedTokens);

        rebalanceCollateral.set(rebalanceCollateral.get().subtract(removedpoolCollateral));
        totalRebalanceShares.set(totalRebalanceShares.get().subtract(removedTokens));
        rebalaceLoan.set(rebalaceLoan.get().subtract(repaidAmount));
        loanTakers.set(Context.getCaller(), user);
    }

    private void depositAndBorrow(Address _from, BigInteger collateral, BigInteger loanSize) {
        LoanTaker user  = loanTakers.getOrDefault(_from, new LoanTaker());

        BigInteger collateralForRebalancing = calculateCollateralForRebalancing(loanSize);
        BigInteger rebalancingTokens = calculateRebalancingTokens(loanSize);

        user.collateral = user.collateral.add(collateral.subtract(collateralForRebalancing));
        user.rebalanceTokens = user.rebalanceTokens.add(rebalancingTokens);
        loanTakers.set(_from, user);
        Context.call(bnusd.get(), "mintTo", _from, loanSize);
    }

    @External
    public void tokenFallback(Address _from, BigInteger _value, byte[] _data) {
        Address token = Context.getCaller();

        if (token.equals(excpectedToken.get())) {
            amountReceived.set(_value);
            excpectedToken.set(null);
            return;
        }

        Context.require(_value.signum() > 0, "Token Fallback: Token value should be a positive number");
        String unpackedData = new String(_data);
        Context.require(!unpackedData.equals(""), "Token Fallback: Data can't be empty");

        JsonObject json = Json.parse(unpackedData).asObject();

        String method = json.get("method").asString();
        JsonObject params = json.get("params").asObject();

        switch (method) {
            case "depositAndBorrow":
                BigInteger amount = BigInteger.valueOf(params.get("amount").asLong()).multiply(BigInteger.TEN.pow(18));
                depositAndBorrow(_from, _value, amount);
                break;
            default:
                Context.revert("Token fallback: Unimplemented tokenfallback action");
                break;
        }
    }

    private BigInteger calculateRebalancingTokens(BigInteger loanSize) {
        BigInteger rebalanceTokens = loanSize;
        if (!totalRebalanceShares.get().equals(BigInteger.ZERO)) {
            rebalanceTokens = totalRebalanceShares.get().multiply(loanSize).divide(rebalaceLoan.get());
        } else {
            rebalanceTokens = BigInteger.TEN.pow(36);
        }

        totalRebalanceShares.set(totalRebalanceShares.get().add(rebalanceTokens));
        rebalaceLoan.set(rebalaceLoan.get().add(loanSize));

        return rebalanceTokens;
    }

    private BigInteger calculateCollateralForRebalancing(BigInteger loanSize) {
        BigInteger collateralForRebalancing = loanSize;
        if (!rebalanceCollateral.get().equals(BigInteger.ZERO)) {
            collateralForRebalancing = loanSize.multiply(rebalanceCollateral.get()).divide(rebalaceLoan.get());
        }

        rebalanceCollateral.set(rebalanceCollateral.get().add(collateralForRebalancing));

        return collateralForRebalancing;
    }

    private BigInteger getUserLoan(LoanTaker user) {
        return user.rebalanceTokens.multiply(rebalaceLoan.get()).divide(totalRebalanceShares.get());
    }

    private BigInteger getUserCollateral(LoanTaker user) {
        BigInteger poolCollateral =  user.rebalanceTokens.multiply(rebalanceCollateral.get()).divide(totalRebalanceShares.get());
        return poolCollateral.add(user.collateral);
    }

    private void transferToken(Address token, Address to, BigInteger amount, byte[] data) {
        Context.call(token, "transfer", to, amount, data);
    }

    private byte[] createSwapData(Address toToken) {
        JsonObject data = Json.object();
        data.add("method", "_swap");
        data.add("params", Json.object().add("toToken", toToken.toString()));
        return data.toString().getBytes();
    }
}
