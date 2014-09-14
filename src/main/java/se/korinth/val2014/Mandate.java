package se.korinth.val2014;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * <h1>Fasta mandat och utjämningsmandat</h1>
 * 
 * När de fasta mandaten fördelats mellan partierna inom varje valkrets summeras
 * partiernas fasta mandat i alla valkretsar (totalt 310 mandat). Därefter görs
 * en mandatfördelning med partiernas totala röstetal i hela landet som
 * underlag. Denna gång fördelas 349 mandat och hela landet räknas som en
 * valkrets.
 * 
 * Resultaten av de båda mandatfördelningarna jämförs. Om ett parti fått fler
 * mandat vid fördelningen med hela landet som en valkrets har partiet rätt till
 * utjämningsmandat. Ett parti får utjämningsmandat i den valkrets där partiets
 * jämförelsetal är störst efter fördelningen av de fasta mandaten.
 * 
 * Om ett parti får fler mandat vid valkretsfördelningen än vid
 * totalfördelningen behåller partiet dessa mandat. Då görs en ny
 * totalfördelning med det partiets mandat borträknade.
 * 
 * Om ett parti inte har fått fasta mandat i alla valkretsar, när
 * utjämningsmandaten ska fördelas, används partiets röstetal som jämförelsetal
 * i de valkretsar, där partiet ännu inte har fått något mandat.
 * 
 * <h2>OBS!</h2>
 * 
 * Denna implementation illustrerar med körtest endast mandatberäkning med hela
 * landet som en valkrets, och behöver anpassas för att kunna räkna fram
 * korrekta platser i riksdagen, enligt specifikationen ovan.
 */
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
        
        public String getCode() {
            return code;
        }

        public int getMandate() {
            return mandate;
        }
        
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
