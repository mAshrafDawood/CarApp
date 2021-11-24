/**
 * Rate class, that holds a rate with its customer
 * @author Khaled Waleed
 */
public class Rate {
    private final Customer user;
    private float rateValue = 2.5f;

    Rate(Customer customer, float value) {
        this.user = customer;
        this.rateValue = value;
    }

    /**
     * Gets the rate value
     * @return The rate value
     */
    public float getRateValue() {
        return rateValue;
    }

    /**
     * Gets the user who rated
     */
    public Customer getUser() {
        return user;
    }

    @Override
    public String toString(){
        return user.getUserName() + " : " + String.valueOf(rateValue);
    }
}
