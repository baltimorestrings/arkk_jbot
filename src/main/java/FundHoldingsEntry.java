import java.math.BigDecimal;

public record FundHoldingsEntry(int rank,
                                String company,
                                String symbol,
                                long shares,
                                BigDecimal marketCap,
                                float weight) {};
