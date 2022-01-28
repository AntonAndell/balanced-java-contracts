package network.balanced.score.core;

import java.math.BigInteger;
import score.Address;

public class Constants   {
    private Constants() {
    }
    public static final BigInteger SECOND = BigInteger.TEN.pow(6);
    public static final BigInteger EXA = BigInteger.TEN.pow(18);
    
    public static final BigInteger U_SECONDS_DAY = BigInteger.valueOf(86400).multiply(SECOND);
    public static final BigInteger MIN_UPDATE_TIME = BigInteger.valueOf(30).multiply(SECOND);
    
    //All percentages expressed in terms of points.
    public static final BigInteger POINTS = BigInteger.TEN.pow(5);
    public static final BigInteger MINING_RATIO = BigInteger.valueOf(5).multiply(POINTS);
    public static final BigInteger LOCKING_RATIO = BigInteger.valueOf(4).multiply(POINTS);
    public static final BigInteger LIQUIDATION_RATIO = BigInteger.valueOf(15000);
    public static final BigInteger LIQUIDATION_REWARD = BigInteger.valueOf(67);
    public static final BigInteger ORIGINATION_FEE = BigInteger.TEN.pow(2);
    public static final BigInteger REDEMPTION_FEE = BigInteger.valueOf(50);
    public static final BigInteger BAD_DEBT_RETIREMENT_BONUS = BigInteger.TEN.pow(3);
    public static final BigInteger MAX_RETIRE_PERCENT = BigInteger.TEN.pow(2);
    
     //In USD
    public static final BigInteger NEW_BNUSD_LOAN_MINIMUM = BigInteger.TEN.multiply(EXA);
    public static final BigInteger MIN_BNUSD_MINING_DEBT = BigInteger.valueOf(50).multiply(EXA); 
    
    public static final BigInteger MAX_DEBTS_LIST_LENGTH = BigInteger.valueOf(400);
    public static final BigInteger SNAP_BATCH_SIZE = BigInteger.valueOf(50);
    public static final BigInteger REDEEM_BATCH_SIZE = BigInteger.valueOf(50);
}