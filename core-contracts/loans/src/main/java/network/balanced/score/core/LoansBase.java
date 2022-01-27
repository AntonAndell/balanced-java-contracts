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

import static network.balanced.score.core.Checks.*;

public class LoansBase {
    public final BigInteger POINTS = BigInteger.TEN.pow(5);
    //public static final VarDB<boolean> loansOn = Context.newVarDB("LoanON", boolean.class);

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

    public LoansBase() {}

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

    // @External
    // public void turnLoansOn() {
    //     onlyGovernance();
    //     loansOn.set(true);
    //     //self.ContractActive("Loans", "Active")
    //     //self._current_day.set(self.getDay())
    //    // self._positions._snapshot_db.start_new_snapshot()
    // }

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

    // @External(readonly = true)
    // public Map<String, BigInteger> getParameters() {
    //     return Map.of(
    //         "admin", admin.get(),
    //         "governance", governance.get(),
    //         "dex", dex.get(),
    //         "rebalancing", rebalancing.get(),
    //         "dividends", dividends.get(),
    //         "reserve", reserve.get(),
    //         "rewards", rewards.get(),
    //         "staking", staking.get()
    //     );
    // }
}
