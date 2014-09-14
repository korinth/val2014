package se.korinth.val2014;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Mandate {
    public static class Cast implements Comparable<Cast> {
        private final String code;
        private final BigDecimal votes;
        private int mandate = 0;
        private int odd = 0;
        private BigDecimal comparison;

        public Cast(String initial, BigDecimal percent) {
            this.code = initial;
            this.votes = percent;
            comparison = percent.divide(NORMALIZATION, 10, RoundingMode.HALF_UP);
        }
        
        private static final BigDecimal SPARE = new BigDecimal("0.04");
        private static final BigDecimal NORMALIZATION = new BigDecimal("1.4");
        
        public boolean isElegible(BigDecimal total) {
            return SPARE.compareTo(votes.divide(total, 10, RoundingMode.HALF_UP)) <= 0;
        }

        @Override
        public int compareTo(Cast cast) {
            return -comparison.compareTo(cast.comparison);
        }

        public void give() {
            mandate++;
            odd++;
            comparison = votes.divide(new BigDecimal(odd * 2 + 1), 10, RoundingMode.HALF_UP);
        }

        public BigDecimal effective() {
            return new BigDecimal(mandate).divide(new BigDecimal(349), 10, RoundingMode.HALF_UP);
        }
        
        public String format(BigDecimal total) {
            return String.format("%s:\tvotes=%s\tpercent=%s%%\tmandate=%d\teffective=%s%%", code, votes, percent(votes.divide(total, 10, RoundingMode.HALF_UP)), mandate, percent(effective()));            
        };
    }
    
    private final BigDecimal total;
    private final List<Cast> casts = new LinkedList<>();

    public Mandate(BigDecimal total, Cast ... casts) {
        this.total = total;
        for (Cast cast : casts) {
            if (cast.isElegible(total)) {
                this.casts.add(cast);
            }
        }
    }
    
    public void calculate() {
        for (int i = 0; i < 349; i++) {
            Collections.sort(casts);
            Cast cast = casts.get(0);
            cast.give();
        }
    }
    
    public static void main(String[] args) {
        Mandate mandate = new Mandate(
            new BigDecimal(6028681),
            new Cast("M",  new BigDecimal(1400225)),
            new Cast("C",  new BigDecimal(370280)),
            new Cast("FP", new BigDecimal(325371)),
            new Cast("KD", new BigDecimal(276654)),
            new Cast("S",  new BigDecimal(1883290)),
            new Cast("V",  new BigDecimal(344013)),
            new Cast("MP", new BigDecimal(407647)),
            new Cast("SD", new BigDecimal(780163)),
            new Cast("FI", new BigDecimal(183964)),
            new Cast("Övr", new BigDecimal(57074))
        );
        mandate.calculate();
        mandate.print();
    }
    
    static BigDecimal percent(BigDecimal bigDecimal) {
        return bigDecimal.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
    }

    private void print() {
        for (Cast cast : casts) {
            System.out.println(cast.format(total));
        }
    }    
}
