import java.math.BigDecimal;

/**
 * Love Java 11 or whatever records.
 *
 * This class represents a single security.
 */
public record FundHoldingsEntry(int rank,
                                String company,
                                String symbol,
                                long shares,
                                BigDecimal marketCap,
                                float weight) {};
