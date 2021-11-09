package dataContainer;

public class ShoppingItem {
    private String id;
    private String title;
    private int quantity;
    private int price;

    public ShoppingItem(String id, String title, int quantity, int price){
        this.id = id;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
    }

    public ShoppingItem(){
        this.id = "";
        this.title = "";
        this.quantity = 0;
        this.price = 0;
    }

    public String getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }
}
