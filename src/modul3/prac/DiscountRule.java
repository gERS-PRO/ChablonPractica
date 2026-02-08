package modul3.prac;

public interface DiscountRule {
    double applyDiscount(double total);
}

class TenPercentDiscount implements DiscountRule {
    public double applyDiscount(double total) {
        return total * 0.9;
    }
}

class FixedDiscount implements DiscountRule {
    public double applyDiscount(double total) {
        return total - 1000;
    }
}

class DiscountCalculator {
    private DiscountRule rule;

    public DiscountCalculator(DiscountRule rule) {
        this.rule = rule;
    }

    public double calculate(double total) {
        return rule.applyDiscount(total);
    }
}

