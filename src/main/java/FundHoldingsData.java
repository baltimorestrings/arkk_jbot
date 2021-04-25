import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class FundHoldingsData {
    public FundHoldingsData(String date) {
        this.date = date;
        securities = new ArrayList<FundHoldingsEntry>();
    }

    public void addSecurity(FundHoldingsEntry entry) {
        securities.add(entry);
    }

    public void addSecurityFromLogline(String line) {
        /** Translates from the following format of line to a security:
         *   18   INTELLIA THERAPEUTICS INC NTLA 45826J105 5,384,487 401,682,730.20 1.70
         * */
        String tokens[] = line.split(" ");
        Integer rank = Integer.valueOf(tokens[0]);
        Float weight = Float.valueOf(tokens[tokens.length - 1]);
        BigDecimal marketCap = new BigDecimal(tokens[tokens.length - 2].replace(",", ""));
        Long shares = Long.valueOf(tokens[tokens.length - 3].replace(",", ""));
        String symbol = tokens[tokens.length - 4];
        String name = String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length - 5));
        securities.add(new FundHoldingsEntry(rank, name, symbol, shares, marketCap, weight));
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder(securities.get(0).toString());
        securities.stream()
                .skip(1)
                .forEach(l -> ret.append("\n" + l.toString()));
        return ret.toString();
    }

    private String date;
    private List<FundHoldingsEntry> securities;
}


