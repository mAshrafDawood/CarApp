import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * RideManager Singleton class
 * <p>
 * Provides a single connection to the current database for other components to use.
 *
 * @author Andrew Naseif
 */
public class RideManager {
    private static RideManager singletonInstance;

    /**
     * Gets the singleton instance of the class
     * @return The active instance of the class
     */
    public static RideManager getInstance() {
        if (singletonInstance == null) singletonInstance = new RideManager();
        return singletonInstance;
    }


    private final Database db =Database.getInstance();
    private final AccountManager accountManager = AccountManager.getInstance();
    /**
     * Makes a query to the database to get al list of offers for a given request.
     * if any error has happened during execution a stacktrace is printed.
     * @param request The SQL will use it to make query.
     * @return {@link ArrayList} if the execution happened successfully.
     */
    public List<Offer> listOffers(Request request) {
        ResultSet table= db.query("SELECT * FROM offer\n" +
                "right JOIN request ON offer.ride_id=request.request_id WHERE ride_id=" + request.getId());
        ArrayList<Offer> result = new ArrayList<>();
        try
        {
            while (table.next()) {
                Offer offer = new Offer(
                        request,
                        table.getFloat("price"),
                        accountManager.getAccount(table.getString("driver_id"))
                );
                result.add(offer);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
    /**
     * Makes a query to the database to insert new offer in database.
     * @param driver The SQL will use it to know who make this offer.
     * @param request The SQL will use it to make offer for this request.
     * @param price  it is the price of ride.
     */
    public void makeOffer(Driver driver, Request request, float price) {
        Offer offer = new Offer(request,price, driver);
        StringBuilder sqlQuery = new StringBuilder("INSERT INTO offer (driver_id,accepted,ride_id,price)\n");
        sqlQuery.append("VALUES (");
        sqlQuery.append("'").append(driver.getUserName()).append("',");
        sqlQuery.append(0).append(",");
        sqlQuery.append(request.getId()).append(",");
        sqlQuery.append(price).append(");");
        db.update(sqlQuery.toString());
        request.getUser().notify(offer);
    }

    /**
     * Makes a query to the database to insert new offer in database.
     * @param offer used to which offer is checked
     * @param accepted {@code true} all offers will be deleted excepted this offer.
     * {@code  false}only this offer will be deleted
     */
    public void setOfferAccepted(Offer offer, boolean accepted) {
        if(accepted){
            db.update("UPDATE offer \n" +
                    "SET \n" +
                    "    accepted = 1\n" +
                    "WHERE\n" +
                    " ride_id ="+offer.getRequest().getId()+"AnD driver_id="+offer.getDriver().getUserName());
            db.update("DELETE FROM offer WHERE accepted=0;\n");
        }else {
            db.update("DELETE FROM offer WHERE ride_id ="+offer.getRequest().getId()+"AnD driver_id="+offer.getDriver().getUserName());
        }
    }
}
