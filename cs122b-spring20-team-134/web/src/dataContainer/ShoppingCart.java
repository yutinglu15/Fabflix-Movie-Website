package dataContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private Map<String, ShoppingItem> cart;
//    private Map<cart.getId(), > cart;

    public ShoppingCart(){
        this.cart = new HashMap<String, ShoppingItem>();
    }

    public ShoppingCart(Map<String, ShoppingItem> cart){
        this.cart = cart;
    }

    public void addToCart(ShoppingItem item){
        cart.put(item.getId(), item);
    }

    public void deleteFromCart(String movieId){
        cart.remove(movieId);
    }


    public void increaseQuantity(String movieId){
        ShoppingItem oldItem = cart.get(movieId);
        oldItem.setQuantity(oldItem.getQuantity()+1);
        cart.put(movieId, oldItem);
    }

    public void decreaseQuantity(String movieId){
        ShoppingItem oldItem = cart.get(movieId);
        if (oldItem.getQuantity() - 1 >= 0) {
            oldItem.setQuantity(oldItem.getQuantity() - 1);
            cart.put(movieId, oldItem);
        }
    }


    public Map<String, ShoppingItem> getCart() {
        return cart;
    }
}
