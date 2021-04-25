import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Class represents an ark Fund's holdings
 */
public class FundHoldingsData {
    /**
     * Simple constructor
     *
     * @param date Date the report was generated from
     */
    public FundHoldingsData(String date) {
        this.date = date;
        securities = new ArrayList<FundHoldingsEntry>();
    }

    /**
     * Simple add for FundHoldingsEntry to list
     * @param entry an entry....
     */
    public void addSecurity(FundHoldingsEntry entry) {
        securities.add(entry);
    }

    /**
     * Proceses a log line in the following format and creates a FundHoldingsEntry to add
     *
     *  Ex: 18   INTELLIA THERAPEUTICS INC NTLA 45826J105 5,384,487 401,682,730.20 1.70
     *
     * @param line the log line
     */
    public void addSecurityFromLogline(String line) {
        String tokens[] = line.split(" ");
        Integer rank = Integer.valueOf(tokens[0]);
        Float weight = Float.valueOf(tokens[tokens.length - 1]);
        BigDecimal marketCap = new BigDecimal(tokens[tokens.length - 2].replace(",", ""));
        Long shares = Long.valueOf(tokens[tokens.length - 3].replace(",", ""));
        String symbol = tokens[tokens.length - 4];
        String name = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length - 5));
        securities.add(new FundHoldingsEntry(rank, name, symbol, shares, marketCap, weight));
    }

    @Override public String toString() {
        StringBuilder ret = new StringBuilder(securities.get(0).toString());
        securities.stream()
                .skip(1)
                .forEach(l -> ret.append("\n" + l.toString()));
        return ret.toString();
    }

    private String date;
    private List<FundHoldingsEntry> securities;
}


