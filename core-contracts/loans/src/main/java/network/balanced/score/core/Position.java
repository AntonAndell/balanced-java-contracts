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

import network.balanced.score.core.LoansBase;
import static network.balanced.score.core.Checks.*;

public class Position {
    public BigInteger checkSnap() {
        return BigInteger.ZERO;
    }
    
    public BigInteger getSnapshotId(BigInteger day) {
        return BigInteger.ZERO;
    }

    public boolean hasDebt(BigInteger day) {
        return false;
    }

    public BigInteger totalDebt(BigInteger day, boolean readOnly) {
        return BigInteger.ZERO;
    }

    //public getStanding(BigInteger day, boolean readOnly)

    public BigInteger updateStanding(BigInteger day) {
        return BigInteger.ZERO;
    }



    private BigInteger getCollateralInLoop(BigInteger day) {
        return BigInteger.ZERO;
    }

    //private toMap()
}

