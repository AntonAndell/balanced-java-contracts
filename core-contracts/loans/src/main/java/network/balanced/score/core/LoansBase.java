package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.DictDB;
import score.Address;
import score.annotation.EventLog;
import score.annotation.External;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

import score.ByteArrayObjectWriter;
import score.Context;
import score.ObjectReader;

import static network.balanced.score.core.Checks.*;
import network.balanced.score.core.MarketDB;
import static network.balanced.score.core.Constants.*;



class PrepDelegations {
    public Address _address;
    public BigInteger _votes_in_per;
}

public class LoansBase {
    public final BigInteger POINTS = BigInteger.TEN.pow(5);
    public static final VarDB<Boolean> loansOn = Context.newVarDB("LoanON", Boolean.class);

    public static final VarDB<Address> sicx = Context.newVarDB("sICX", Address.class);
    public static final VarDB<Address> bnusd = Context.newVarDB("bnUSD", Address.class);

    public static final VarDB<Address> admin = Context.newVarDB("admin", Address.class);
    public static final VarDB<Address> governance = Context.newVarDB("governance", Address.class);
    public static final VarDB<Address> dex = Context.newVarDB("dex", Address.class);
    public static final VarDB<Address> rebalancing = Context.newVarDB("rebalancing", Address.class);
    public static final VarDB<Address> dividends = Context.newVarDB("dividends", Address.class);
    public static final VarDB<Address> reserve = Context.newVarDB("reserve", Address.class);
    public static final VarDB<Address> rewards = Context.newVarDB("rewards", Address.class);
    public static final VarDB<Address> staking = Context.newVarDB("staking", Address.class);

    public static final VarDB<BigInteger> snapBatchSize = Context.newVarDB("snapBatchSize", BigInteger.class);
    public static final VarDB<BigInteger> globalIndex = Context.newVarDB("globalIndex", BigInteger.class);
    public static final VarDB<BigInteger> globalBatchIndex = Context.newVarDB("globalBatchIndex", BigInteger.class);

    public static final MarketDB markets = new MarketDB();

    public static final VarDB<Boolean> rewardsDone = Context.newVarDB("rewardsDone", Boolean.class);
    public static final VarDB<Boolean> dividendsDone = Context.newVarDB("dividendsDone", Boolean.class);
    public static final VarDB<BigInteger> currentDay = Context.newVarDB("currentDay", BigInteger.class);
    public static final VarDB<BigInteger> timeOffset = Context.newVarDB("timeOffset", BigInteger.class);
    public static final VarDB<BigInteger> miningRatio = Context.newVarDB("miningRatio", BigInteger.class);
    public static final VarDB<BigInteger> lockingRatio  = Context.newVarDB("lockingRatio", BigInteger.class);

    public static final VarDB<BigInteger> liquidationRatio = Context.newVarDB("dividendsDone", BigInteger.class);
    public static final VarDB<BigInteger> originationFee = Context.newVarDB("originationFee", BigInteger.class);
    public static final VarDB<BigInteger> redemptionFee = Context.newVarDB("redemptionFee", BigInteger.class);
    public static final VarDB<BigInteger> retirementBonus = Context.newVarDB("retirementBonus", BigInteger.class);
    public static final VarDB<BigInteger> liquidationReward = Context.newVarDB("liquidationReward", BigInteger.class);
    public static final VarDB<BigInteger> newLoanMinimum = Context.newVarDB("newLoaMinimum", BigInteger.class);
    public static final VarDB<BigInteger> minMiningDebt = Context.newVarDB("minMiningDebt", BigInteger.class);
    public static final VarDB<BigInteger> maxDebtsListLength = Context.newVarDB("maxDebtsListLength", BigInteger.class);
    public static final VarDB<BigInteger> redeemBatch = Context.newVarDB("redeemBatch", BigInteger.class);
    public static final VarDB<BigInteger> maxRetirePercent = Context.newVarDB("maxRetirePercent", BigInteger.class);

    public LoansBase(Address governance) {
        rewardsDone.set(true);
        dividendsDone.set(true);
        miningRatio.set(MINING_RATIO);
        lockingRatio.set(LOCKING_RATIO);
        liquidationRatio.set(LIQUIDATION_RATIO);
        originationFee.set(ORIGINATION_FEE);
        redemptionFee.set(REDEMPTION_FEE);
        retirementBonus.set(BAD_DEBT_RETIREMENT_BONUS);
        liquidationReward.set(LIQUIDATION_REWARD);
        newLoanMinimum.set(NEW_BNUSD_LOAN_MINIMUM);
        minMiningDebt.set(MIN_BNUSD_MINING_DEBT);
        maxDebtsListLength.set(MAX_DEBTS_LIST_LENGTH);
        redeemBatch.set(REDEEM_BATCH_SIZE);
        maxRetirePercent.set(MAX_RETIRE_PERCENT);
    }

    @External(readonly = true)
    public String name() {
        return "Balanced Loans";
    }

    @External
    public void setSicx(Address address) {
        sicx.set(address);
    }

    @External 
    public void setbnUSD(Address address) {
        bnusd.set(address);
    }

    @External(readonly = true)
    public Address getSicx() {
        return sicx.get();
    }

    @External(readonly = true)
    public Address getbnUSD() {
        return bnusd.get();
    }

    @External
    public void turnLoansOn() {
        onlyGovernance();
        loansOn.set(true);
        ContractActive("Loans", "Active");
        currentDay.set(getDay());
       // positions._snapshot_db.start_new_snapshot()
    }

    @External
    public void toggleLoansOn() {
        onlyGovernance();
        loansOn.set(!loansOn.get());
        ContractActive("Loans", loansOn.get() ? "Active" : "Inactive");
    }

    @External
    public Boolean getLoansOn() {
        return loansOn.get();
    }

    @External(readonly = true)
    public BigInteger getDay() {
        BigInteger blockTime = BigInteger.valueOf(Context.getBlockTimestamp()).subtract(timeOffset.get());
        return blockTime.divide(U_SECONDS_DAY);
    }

    @External
    public void delegate(ArrayList<PrepDelegations> prepDelegations) {
        onlyGovernance();
        Context.call(staking.get(), "delegate", prepDelegations);
    }

    @External
    public Map<String, Boolean> getDistributionsDone() {
        return Map.of (
            "Rewards", rewardsDone.get(),
            "Dividends",  dividendsDone.get()
        );
    }

    @External
    public ArrayList<String> checkDeadMarkets() {
        return markets.getDeadMarkets();
    }

    @External
    public int getNonzeroPositionCount() {
        return 0;
    }

    // @External
    // public ArrayList<String> getPositionStanding(Address address) {
    //     return markets.getDeadMarkets();
    // }

    // @External
    // public Address getPositionAddress() {
    //     return markets.getDeadMarkets();
    // }

    public Map<String, Address> getAssetTokens() {
        return markets.getAssets();
    }

    public Map<String, Address> getCollateralTokens() {
        return markets.getCollateralTypes();
    }

    public BigInteger getTotalCollateral() {
        return markets.getTotalActiveCollateral();
    }

    @External
    public void setAdmin(Address address) {
        onlyGovernance();
        admin.set(address);
    }

    @External
    public void setGovernance(Address address) {
        onlyAdmin();
        Context.require(address.isContract(), "Loans: Governance address should be a contract");
        governance.set(address);
    }

    @External
    public void setDex(Address address) {
        dex.set(address);
    }
    
    @External
    public void setRebalancing(Address address) {
        rebalancing.set(address);
    }

    @External
    public void setDividends(Address address) {
        dividends.set(address);
    }

    @External
    public void setReserve(Address address) {
        reserve.set(address);
    }

    @External
    public void setRewards(Address address) {
        rewards.set(address);
    }

    @External
    public void setStaking(Address address) {
        staking.set(address);
    }

    // @External(readonreadonly = truely = true)
    // public Map<String, Object> getParameters() {
    //     return Map.of(
    //         "admin", admin.get(),
    //         "governance", governance.get(),
    //         "dividends", dividends.get(),
    //         "reserve_fund", reserve.get(),
    //         "rewards", rewards.get(),
    //         "staking", staking.get(),
    //         "mining ratio", miningRatio.get(),
    //         "locking ratio", lockingRatio.get(),
    //         "liquidation ratio", liquidationRatio.get(),
    //         "origination fee", originationFee.get(),
    //         "redemption fee", redemptionFee.get(),
    //         "liquidation reward", liquidationReward.get(),
    //         "new loan minimum", newLoanMinimum.get(),
    //         "min mining debt", minMiningDebt.get(),
    //         "max div debt length", maxDebtsListLength.get(),
    //         "time offset", timeOffset.get(),
    //         "redeem batch size", redeemBatch.get(),
    //         "retire percent max", maxRetirePercent.get()
    //     );
    // }


    @EventLog(indexed = 1)
    public void ContractActive(String contractName, String contractState) {

    }

    // @eventlog(indexed=1)
    // def AssetActive(self, _asset: str, _state: str):
    //     pass

    // @eventlog(indexed=2)
    // def TokenTransfer(self, recipient: Address, amount: int, note: str):
    //     pass

    // @eventlog(indexed=3)
    // def AssetAdded(self, account: Address, symbol: str, is_collateral: bool):
    //     pass

    // @eventlog(indexed=2)
    // def CollateralReceived(self, account: Address, symbol: str, value: int):
    //     pass

    // @eventlog(indexed=3)
    // def OriginateLoan(self, recipient: Address, symbol: str, amount: int, note: str):
    //     pass

    // @eventlog(indexed=3)
    // def LoanRepaid(self, account: Address, symbol: str, amount: int, note: str):
    //     pass

    // @eventlog(indexed=3)
    // def BadDebtRetired(self, account: Address, symbol: str, amount: int, sicx_received: int):
    //     pass

    // @eventlog(indexed=2)
    // def Liquidate(self, account: Address, amount: int, note: str):
    //     pass

    // @eventlog(indexed=3)
    // def FeePaid(self, symbol: str, amount: int, type: str):
    //     pass

    // @eventlog(indexed=2)
    // def Rebalance(self, account: Address, symbol: str, change_in_pos: str,
    //               total_batch_debt: int):
    //     pass

    // @eventlog(indexed=2)
    // def PositionStanding(self, address: Address, standing: str,
    //                      total_collateral: int, total_debt: int):
    //     pass

    // @eventlog(indexed=1)
    // def Snapshot(self, _id: int):
    //     """
    //     Emitted as a new snapshot is generated.
    //     :param _id: ID of the snapshot.
    //     """
    //     pass
}
