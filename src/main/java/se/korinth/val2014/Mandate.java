package se.korinth.val2014;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Mandate {
    
    private static int PARLIAMENT = 349;
    
    public static class Cast implements Comparable<Cast> {
        
        private static final BigDecimal SPARE = new BigDecimal("0.04");
        private static final BigDecimal NORM = new BigDecimal("1.4");
        private static final int SCALE = 10;
        
        private final String code;
        private final BigDecimal votes;
        private int mandate = 0;
        private BigDecimal comparison;

        public Cast(String initial, BigDecimal percent) {
            this.code = initial;
            this.votes = percent;
            comparison = percent.divide(NORM, 10, RoundingMode.HALF_UP);
        }
        
        public boolean isElegible(BigDecimal total) {
            return SPARE.compareTo(votes.divide(total, SCALE, RoundingMode.HALF_UP)) <= 0;
        }

        @Override
        public int compareTo(Cast cast) {
            return -comparison.compareTo(cast.comparison);
        }

        public void increment() {
            mandate++;
            comparison = votes.divide(new BigDecimal(mandate * 2 + 1), SCALE, RoundingMode.HALF_UP);
        }

        public BigDecimal effective() {
            return new BigDecimal(mandate).divide(new BigDecimal(PARLIAMENT), SCALE, RoundingMode.HALF_UP);
        }
        
        public String format(BigDecimal total) {
            return String.format("%s:\tvotes=%s\tpercent=%s%%\tmandate=%d\teffective=%s%%", code, votes, percent(votes.divide(total, 10, RoundingMode.HALF_UP)), mandate, percent(effective()));            
        }

        @Override
        public String toString() {
            return "{ code => " + code + ", mandate => " + mandate + ", comparison => " + comparison + " }";
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
        List<Cast> casts = new LinkedList<>(this.casts);
        for (int i = 0; i < PARLIAMENT; i++) {
            Collections.sort(casts);
            Cast cast = casts.get(0);
//            System.out.println(i + casts.toString());
            cast.increment();
        }
    }
    
    public static void main(String[] args) {
        Mandate mandate = new Mandate(
            new BigDecimal(6036835),
            new Cast("M",  new BigDecimal(1402975)),
            new Cast("C",  new BigDecimal(370709)),
            new Cast("FP", new BigDecimal(325921)),
            new Cast("KD", new BigDecimal(277143)),
            new Cast("S",  new BigDecimal(1885463)),
            new Cast("V",  new BigDecimal(344360)),
            new Cast("MP", new BigDecimal(408224)),
            new Cast("SD", new BigDecimal(780831)),
            new Cast("FI", new BigDecimal(184171)),
            new Cast("Övr", new BigDecimal(57038))
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
