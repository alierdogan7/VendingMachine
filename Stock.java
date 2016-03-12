/**
 * Created by burak on 10.03.2016.
 */
public class Stock {

    int productId;
    String productName;
    int amount;

    public Stock(int productId, String productName, int amount) {
        this.productId = productId;
        this.productName = productName;
        this.amount = amount;
    }

    public String toString()
    {
        return "" + productId + " " + productName +  " " + amount;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
