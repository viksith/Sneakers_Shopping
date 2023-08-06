
import java.sql.*;
import java.util.*;

// Class that represents a Shoes item
class Item {
    int id;
    String itemName;
    int qty;
    int price;

    // Constructor
    Item(int id, String itemName, int qty, int price) {
        this.id = id;
        this.itemName = itemName;
        this.qty = qty;
        this.price = price;
    }
}

// Class that represents a wishlist. Stores an array of items
class Wishlist {
    ArrayList<Item> wishListItems = new ArrayList<Item>();
}

// Class that represents a cart. Stores an array of items
class Cart {
    int totalPrice;
    ArrayList<Item> cartItems = new ArrayList<Item>();
    
    Cart() {
        totalPrice = 0;
    }
}

// Class that represents orders. Stores an array of items
class Order {
    int totalPrice;
    ArrayList<Item> ordersItems = new ArrayList<Item>();
    
    Order() {
        totalPrice = 0;
    }
}

// Represent a user that has a unique username and pass
// Stores a list of items (Shoes) into different ArrayLists as wishlist, cart, and orders
class User {
    String username;
    String password;
    Wishlist wishlist = new Wishlist();
    Cart cart = new Cart();
    Order orders = new Order();
    int totalItems;

    User() {
        totalItems = 0;
    }

    // Called when a new user is created

    void createUser(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
   
    void createUser() {
        System.out.print("Enter username: ");
        this.username = Main.sc.nextLine();
        System.out.print("Enter password: ");
        this.password = Main.sc.nextLine();
        
        try {
            Main.stmt.executeUpdate("INSERT INTO users VALUES (\"" + this.username + "\",\"" + this.password + "\");");
            Main.stmt.executeUpdate("CREATE TABLE " + username + "_WISHLIST" + " (ID INTEGER PRIMARY KEY, ITEMNAME VARCHAR(20), QTY INTEGER, PRICE INTEGER);");      
            Main.stmt.executeUpdate("CREATE TABLE " + username + "_CART" + " (ID INTEGER PRIMARY KEY, ITEMNAME VARCHAR(20), QTY INTEGER, PRICE INTEGER);");      
            Main.stmt.executeUpdate("CREATE TABLE " + username + "_ORDERS" + " (ID INTEGER PRIMARY KEY, ITEMNAME VARCHAR(20), QTY INTEGER, PRICE INTEGER);");      
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Displays the wishlist and provides option to add the items to cart or remove items from wishlist
    void viewWishlist() {
        // Stores choice for removing item from wishlist or adding to cart
        int choice;
        // Stores id of item to be removed from wishlist
        int id;

        do {
            // Check if wishlist is empty
            try {
                Main.rs = Main.stmt.executeQuery("SELECT COUNT(*) FROM " + username + "_WISHLIST;");
                Main.rs.next();
                if (Main.rs.getInt(1) == 0) {
                    System.out.println("\nWishlist is empty.");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Check if wishlist is empty
            if (wishlist.wishListItems.isEmpty()) {
                System.out.println("\nWishlist is empty.");
                return;
            }

            // Section for printing wishlist
            System.out.format("\n%35s\n", "WISHLIST");
            for (int i = 0; i < 70; i++) 
                System.out.print("-");
            System.out.print("\n");
            System.out.format("|%-5s|%-20s|%-20s|%-20s|\n", "ID", "Item name", "Quantity", "Item total");
            for (int i = 0; i < 70; i++) 
                System.out.print("-");
            System.out.print("\n");

            // for (Item item : wishlist.wishListItems) 
            //     System.out.format("|%-5d|%-20s|%-20d|%-20d|\n", item.id, item.itemName, item.qty, item.price);

            try {
                Main.rs = Main.stmt.executeQuery("SELECT * FROM " + username + "_WISHLIST;");
                
                while (Main.rs.next()) {
                    System.out.format("|%-5d|%-20s|%-20d|%-20d|\n", Main.rs.getInt("ID"), Main.rs.getString("ITEMNAME"), Main.rs.getInt("QTY"), Main.rs.getInt("PRICE"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 70; i++) 
                System.out.print("-");

            System.out.println("\n\n1. Remove item from wishlist");
            System.out.println("2. Remove all items from wishlist");
            System.out.println("3. Add all items to cart");
            System.out.println("4. Return to items menu");
            System.out.print("Enter choice: ");
            choice = Main.sc.nextInt();

            switch (choice) {
                // Remove item from wishlist
                case 1:
                    System.out.print("Enter ID of item to remove: ");  
                    id = Main.sc.nextInt();

                    // Set to 1 if item to be deleted is found
                    int flag = 0;
                    // Search for item and delete 
                    for (int i = 0; i < wishlist.wishListItems.size(); i++) {
                        if (wishlist.wishListItems.get(i).id == id) {
                            flag = 1;

                            wishlist.wishListItems.remove(i);   
                            
                            try {
                                Main.stmt.executeUpdate("DELETE FROM " + username + "_WISHLIST WHERE ID = " + id + ";");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            System.out.println("\nItem successfully removed from wishlist.");   

                            break;
                        }
                    }
                    
                    if (flag == 0)
                        System.out.println("Item not found.");
                    
                    break;

                // Remove all items from cart
                case 2: 
                    wishlist.wishListItems.removeAll(wishlist.wishListItems);

                    try {
                        Main.stmt.executeUpdate("TRUNCATE TABLE " + username + "_WISHLIST;");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("\nAll items removed from wishlist successfully.");

                    break;

                // Add all the items in the wishlist to cart
                case 3:
                    for (Item item : wishlist.wishListItems) {
                        cart.cartItems.add(item);
                        cart.totalPrice += item.price;
                    }

                    try {
                        Main.stmt.executeUpdate("INSERT INTO " + username + "_CART SELECT * FROM " + username + "_WISHLIST;");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("\nItems added to cart.");

                    break;
            }
        } while (choice >= 1 && choice <= 3);
    }

    // Displays the cart and provides option to remove items from the cart or proceed to checkout
    void viewCart() {
        // Stores choice for removing item from cart or placing order
        int choice;
        // Stores id of item to be removed from cart
        int id;

        do {
            // Check if cart is empty
            try {
                Main.rs = Main.stmt.executeQuery("SELECT COUNT(*) FROM " + username + "_CART;");
                Main.rs.next();
                if (Main.rs.getInt(1) == 0) {
                    System.out.println("\nCart is empty.");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Check if cart is empty
            if (cart.cartItems.isEmpty()) {
                System.out.println("\nCart is empty.");
                return;
            }

            // Section for displaying cart      
            System.out.format("\n%35s\n", "CART");
            for (int i = 0; i < 70; i++) 
                System.out.print("-");
            System.out.print("\n");
            System.out.format("|%-5s|%-20s|%-20s|%-20s|\n", "ID", "Item name", "Quantity", "Item total");
            for (int i = 0; i < 70; i++) 
                System.out.print("-");
            System.out.print("\n");
            
            // for (Item item : cart.cartItems) 
            //     System.out.format("|%-5d|%-20s|%-20d|%-20d|\n", item.id, item.itemName, item.qty, item.price);

            try {
                Main.rs = Main.stmt.executeQuery("SELECT * FROM " + username + "_CART;");
                
                while (Main.rs.next()) {
                    System.out.format("|%-5d|%-20s|%-20d|%-20d|\n", Main.rs.getInt("ID"), Main.rs.getString("ITEMNAME"), Main.rs.getInt("QTY"), Main.rs.getInt("PRICE"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 70; i++) 
                System.out.print("-");

            // System.out.format("\n%55s%d\n", "Total: ", cart.totalPrice);
            
            try {
                Main.rs = Main.stmt.executeQuery("SELECT SUM(PRICE) FROM " + username + "_CART;");
                
                while (Main.rs.next()) {
                    System.out.format("\n%55s%d\n", "Total: ", Main.rs.getInt(1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("1. Remove item from cart");
            System.out.println("2. Remove all items from cart");
            System.out.println("3. Checkout");
            System.out.println("4. Return to items menu");
            System.out.print("Enter choice: ");
            choice = Main.sc.nextInt();

            switch (choice) {
                // Remove item from cart
                case 1:
                    System.out.print("Enter ID of item to remove: ");  
                    id = Main.sc.nextInt();
                    
                    // Set to 1 if item to be deleted is found
                    int flag = 0;
                    // Search for item and delete 
                    for (int i = 0; i < cart.cartItems.size(); i++) {
                        if (cart.cartItems.get(i).id == id) {
                            flag = 1;                            
                            cart.totalPrice -= cart.cartItems.get(i).price;
                            cart.cartItems.remove(i);                      
                            
                            try {
                                Main.stmt.executeUpdate("DELETE FROM " + username + "_CART WHERE ID = " + id + ";");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            System.out.println("\nItem successfully removed from cart.");

                            break;
                        }
                    }

                    if (flag == 0)
                        System.out.println("Item not found.");

                    break;

                // Remove all items from cart
                case 2: 
                    cart.cartItems.removeAll(cart.cartItems);

                    try {
                        Main.stmt.executeUpdate("TRUNCATE TABLE " + username + "_CART;");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("\nAll items removed from cart successfully.");

                    break;
                
                // Add all items in cart to orders list
                case 3:
                    for (Item item : cart.cartItems) {
                        orders.ordersItems.add(item);
                        orders.totalPrice += item.price;
                    }

                    try {
                        Main.stmt.executeUpdate("INSERT INTO " + username + "_ORDERS SELECT * FROM " + username + "_CART;");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Remove all items in cart once the order is placed
                    cart.cartItems.removeAll(cart.cartItems);
                    cart.totalPrice = 0;

                    try {
                        Main.stmt.executeUpdate("TRUNCATE TABLE " + username + "_CART;");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("\nOrder successfully placed.");

                    break;
            }  
        } while (choice >= 1 && choice <= 3);    
    }

    // Displays the list of orders and provides option to cancel the orders
    void viewOrders() {
        // Stores choice for cancelling order or returning to menu
        int choice;
        // Stores id of order to be cancelled
        int id;

        do {
            // Check if there are no orders placed
            try {
                Main.rs = Main.stmt.executeQuery("SELECT COUNT(*) FROM " + username + "_ORDERS;");
                Main.rs.next();
                if (Main.rs.getInt(1) == 0) {
                    System.out.println("\nNo orders placed.");
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Check if there are no orders placed
            if (orders.ordersItems.isEmpty()) {
                System.out.println("\nNo orders placed.");
                return;
            }

            // Section for printing orders
            System.out.format("\n%35s\n", "ORDERS");
            for (int i = 0; i < 70; i++) 
                System.out.print("-");
            System.out.print("\n");
            System.out.format("|%-5s|%-20s|%-20s|%-20s|\n", "ID", "Item name", "Quantity", "Item total");
            for (int i = 0; i < 70; i++) 
                System.out.print("-");
            System.out.print("\n");
            
            // for (Item item : orders.ordersItems) 
            //     System.out.format("|%-5d|%-20s|%-20d|%-20d|\n", item.id, item.itemName, item.qty, item.price);

            try {
                Main.rs = Main.stmt.executeQuery("SELECT * FROM " + username + "_ORDERS;");
                
                while (Main.rs.next()) {
                    System.out.format("|%-5d|%-20s|%-20d|%-20d|\n", Main.rs.getInt("ID"), Main.rs.getString("ITEMNAME"), Main.rs.getInt("QTY"), Main.rs.getInt("PRICE"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 70; i++) 
                System.out.print("-");
            
            //System.out.format("\n%55s%d\n", "Total: ", orders.totalPrice);

            try {
                Main.rs = Main.stmt.executeQuery("SELECT SUM(PRICE) FROM " + username + "_ORDERS;");
                
                while (Main.rs.next()) {
                    System.out.format("\n%55s%d\n", "Total: ", Main.rs.getInt(1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("1. Cancel orders");
            System.out.println("2. Cancel all orders");
            System.out.println("3. Return to items menu");
            System.out.print("Enter choice: ");
            choice = Main.sc.nextInt();

            switch (choice) {
                // Cancel order
                case 1:
                    System.out.print("Enter ID of order to cancel: ");  
                    id = Main.sc.nextInt();

                    // Set to 1 if order to be cancelled is found
                    int flag = 0;
                    // Search for order and delete 
                    for (int i = 0; i < orders.ordersItems.size(); i++) {
                        if (orders.ordersItems.get(i).id == id) {
                            flag = 1;                            
                            orders.totalPrice -= orders.ordersItems.get(i).price;
                            orders.ordersItems.remove(i);       
                            
                            try {
                                Main.stmt.executeUpdate("DELETE FROM " + username + "_ORDERS WHERE ID = " + id + ";");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            System.out.println("\nOrder cancelled.");
                            
                            break;
                        }
                    }

                    if (flag == 0)
                        System.out.println("Item not found.");

                    break;

                // Cancel all orders
                case 2: 
                    orders.ordersItems.removeAll(orders.ordersItems);
                    orders.totalPrice = 0;

                    try {
                        Main.stmt.executeUpdate("TRUNCATE TABLE " + username + "_ORDERS;");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("\nAll orders cancelled successfully.");
                    break;       
            }
        } while (choice >= 1 && choice <= 2);
    }

    // Displays the menu for Shoes items and provies options to add items, view the cart, wishlist, and orders
    void buyShoes() {
        // Stores choice for menu
        int choice;
        // Stores choice for adding to cart or wishlist
        int ch;
        // Stores quantity of item chosen
        int qty;
        // Holds reference of item that is added to cart/wishlist
        Item ob;

        do {
            // Section for printing item menu
            System.out.println("\nChoose an item (sno) to add to cart or wishlist:");
            for (int i = 0; i < 49; i++) 
                System.out.print("-");
            System.out.print("\n");
            System.out.format("|%-5s|%-20s|%-20s|\n", "Sno.", "Item name", "Price");
            for (int i = 0; i < 49; i++) 
                System.out.print("-");
            System.out.print("\n");

            // System.out.format("|%-5s|%-20s|%-20s|\n", "1", "Air Jordan 1", "70000");
            // System.out.format("|%-5s|%-20s|%-20s|\n", "2", "Yeezy 350v2", "50000");
            // System.out.format("|%-5s|%-20s|%-20s|\n", "3", "Item 3", "175");
            // System.out.format("|%-5s|%-20s|%-20s|\n", "4", "Nike Air Force1", "225");

            // Printing items menu
            try {
                int j = 1;

                Main.rs = Main.stmt.executeQuery("SELECT * FROM ITEMS;");
                
                while (Main.rs.next()) {
                    System.out.format("|%-5s|%-20s|%-20d|\n", j++, Main.rs.getString("ITEMNAME"), Main.rs.getInt("PRICE"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 49; i++) 
                System.out.print("-");
            System.out.format("\n|%-47s|", "7. View wishlist");
            System.out.format("\n|%-47s|", "8. View cart");
            System.out.format("\n|%-47s|", "9. View orders");
            System.out.format("\n|%-47s|\n", "0. Exit to main menu");
            for (int i = 0; i < 49; i++) 
                System.out.print("-");

            System.out.print("\nEnter choice: ");
            choice = Main.sc.nextInt();

            switch (choice) {
                // Air Jordan 1
                case 1:
                    System.out.print("Add to - 1. Cart, 2. Wishlist: ");
                    ch = Main.sc.nextInt();

                    System.out.print("Enter quantity: ");
                    qty = Main.sc.nextInt();
                    
                    // Add to cart
                    if (ch == 1) {
                        totalItems++;
                        ob = new Item(totalItems, "Air Jordan 1", qty, 70000 * qty);
                        cart.cartItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_CART" + " VALUES (" + totalItems + " , 'Air Jordan 1', " + qty + ", " + (70000 * qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        cart.totalPrice += 70000 * qty;
                    }
                    // Add to wishlist
                    else {
                        totalItems++;
                        ob = new Item(totalItems, "Air Jordan 1", qty, 70000 * qty);
                        wishlist.wishListItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_WISHLIST" + " VALUES (" + totalItems + " , 'Air Jordan 1', " + qty + ", " + (70000 * qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.println("\nItem added successfully.");
                    break;
                
                // Yeezy 350v2
                case 2:
                    System.out.print("Add to - 1. Cart, 2. Wishlist: ");
                    ch = Main.sc.nextInt();

                    System.out.print("Enter quantity: ");
                    qty = Main.sc.nextInt();
                    
                    // Add to cart
                    if (ch == 1) {
                        totalItems++;
                        ob = new Item(totalItems, "Yeezy 350v2", qty, 50000 * qty);
                        cart.cartItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_CART" + " VALUES (" + totalItems + " , 'Yeezy 350v2', " + qty + ", " + (50000 * qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        cart.totalPrice += 50000 * qty;
                    }
                    // Add to wishlist
                    else {
                        totalItems++;
                        ob = new Item(totalItems, "Yeezy 350v2", qty, 50000 * qty);
                        wishlist.wishListItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_WISHLIST" + " VALUES (" + totalItems + " , 'Yeezy 350v2', " + qty + ", " + (50000 * qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    System.out.println("\nItem added successfully.");
                    break;

                // Item 3
                case 3:
                    System.out.print("Add to - 1. Cart, 2. Wishlist: ");
                    ch = Main.sc.nextInt();

                    System.out.print("Enter quantity: ");
                    qty = Main.sc.nextInt();
                    
                    // Add to cart
                    if (ch == 1) {
                        totalItems++;
                        ob = new Item(totalItems, "Item 3", qty,30000 * qty);
                        cart.cartItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_CART" + " VALUES (" + totalItems + " , 'ITEM 3', " + qty + ", " + (175 * qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        cart.totalPrice +=30000 * qty;
                    }
                    // Add to wishlist
                    else {
                        totalItems++;
                        ob = new Item(totalItems, "Item 3", qty,30000 * qty);
                        wishlist.wishListItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_WISHLIST" + " VALUES (" + totalItems + " , 'ITEM 3', " + qty + ", " + (175 * qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                
                System.out.println("\nItem added successfully.");
                break;

                // Nike Air Force1
                case 4:
                    System.out.print("Add to - 1. Cart, 2. Wishlist: ");
                    ch = Main.sc.nextInt();

                    System.out.print("Enter quantity: ");
                    qty = Main.sc.nextInt();
                    
                    // Add to cart
                    if (ch == 1) {
                        totalItems++;
                        ob = new Item(totalItems, "Nike Air Force1", qty, 22500* qty);
                        cart.cartItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_CART" + " VALUES (" + totalItems + " , 'Nike Air Force1', " + qty + ", " + (22500* qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        cart.totalPrice += 22500* qty;
                    }
                    // Add to wishlist
                    else {
                        totalItems++;
                        ob = new Item(totalItems, "Nike Air Force1", qty, 22500* qty);
                        wishlist.wishListItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_WISHLIST" + " VALUES (" + totalItems + " , 'Nike Air Force1', " + qty + ", " + (22500* qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    System.out.println("\nItem added successfully.");
                    break;
                    case 5:
                    System.out.print("Add to - 1. Cart, 2. Wishlist: ");
                    ch = Main.sc.nextInt();

                    System.out.print("Enter quantity: ");
                    qty = Main.sc.nextInt();
                    
                    // Add to cart
                    if (ch == 1) {
                        totalItems++;
                        ob = new Item(totalItems, "Yeezy Foam Runner", qty, 35000* qty);
                        cart.cartItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_CART" + " VALUES (" + totalItems + " , 'Yeezy Foam runner', " + qty + ", " + (35000* qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        cart.totalPrice += 35000* qty;
                    }
                    // Add to wishlist
                    else {
                        totalItems++;
                        ob = new Item(totalItems, "Yeezy Foam Runner", qty, 35000* qty);
                        wishlist.wishListItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_WISHLIST" + " VALUES (" + totalItems + " , 'Yeezy Foam runner', " + qty + ", " + (35000* qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    System.out.println("\nItem added successfully.");
                    break;
                    case 6:
                    System.out.print("Add to - 1. Cart, 2. Wishlist: ");
                    ch = Main.sc.nextInt();

                    System.out.print("Enter quantity: ");
                    qty = Main.sc.nextInt();
                    
                    // Add to cart
                    if (ch == 1) {
                        totalItems++;
                        ob = new Item(totalItems, "New Balance", qty, 10000* qty);
                        cart.cartItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_CART" + " VALUES (" + totalItems + " , 'New Balance', " + qty + ", " + (10000* qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        cart.totalPrice += 10000* qty;
                    }
                    // Add to wishlist
                    else {
                        totalItems++;
                        ob = new Item(totalItems, "New Balance", qty, 10000* qty);
                        wishlist.wishListItems.add(ob);
                        
                        try {
                            Main.stmt.executeUpdate("INSERT INTO " + username + "_WISHLIST" + " VALUES (" + totalItems + " , 'New Balance', " + qty + ", " + (22500* qty) + ");");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    System.out.println("\nItem added successfully.");
                    break;

                // View wishlist
                case 7:
                    viewWishlist();
                    break;
                
                // View cart
                case 8:
                    viewCart();
                    break;

                // View orders
                case 9:
                    viewOrders();
                    break;
            }
        } while (choice >= 1 && choice <= 9);
    }

    // Displays the main menu to buy Shoes, view the wishlist, cart, or orders
    void mainMenu() {
        int choice;

        System.out.println("\nWelcome " + username + "!\n");

        do {
            for (int i = 0; i < 25; i++)
                System.out.print("-");
            System.out.format("\n|%10s%s%9s|\n", " ", "MENU", " ");
            for (int i = 0; i < 25; i++)
                System.out.print("-");
            System.out.format("\n|%-23s|", "1. Select the Sneakers of choice");
            System.out.format("\n|%-23s|", "2. View wishlist");
            System.out.format("\n|%-23s|", "3. View cart");
            System.out.format("\n|%-23s|", "4. View orders");
            System.out.format("\n|%-23s|\n", "5. Exit to login page");
            for (int i = 0; i < 25; i++)
                System.out.print("-");
            System.out.print("\nEnter choice: ");
            choice = Main.sc.nextInt();
            Main.sc.nextLine();

            switch (choice) {
                // Display Shoes items 
                case 1:
                    buyShoes();
                    break;

                // Display wishlist
                case 2:
                    viewWishlist();
                    break;

                // Display cart
                case 3:
                    viewCart();
                    break;

                // Display orders
                case 4:
                    viewOrders();
                    break;
            }
        } while (choice >= 0 && choice <= 4);
    }
}

// Main class
// Creating an account and login is done here
public class Main {
    static Scanner sc = new Scanner(System.in);
    static Connection con;
    static Statement stmt;
    static ResultSet rs;

    // Program starts here
    public static void main(String args[]) throws SQLException {
        Scanner sc = new Scanner(System.in);
        // Array of objects that contains an object of each User
        ArrayList<User> usersList = new ArrayList<User>();
        // Choice variable for menu
        int choice;
        // Stores the index of the user in the usersList. If the user is not found, it is set as -1
        int index = -1;

        // Connecting to database and creating tables
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost/Sneakers", "Viksith", "viksith");
            stmt = con.createStatement();


            ResultSet query = stmt.executeQuery("Select * from Users;");

            while(query.next())
            {
                String Username = query.getString(1);
                String Password = query.getString(2);
                User us = new User();
                us.createUser(Username, Password);
                usersList.add(us);
            }

            
        } catch (Exception e) {
            e.printStackTrace();
        }

        do {
            index = -1;

            System.out.println("");
            for (int i = 0; i < 50; i++) 
                System.out.print("-");
            System.out.format("\n|%11s%s%12s  |\n", " ", "Welcome to Our Project", " ");
            System.out.format("\n|%11s%s%12s  |\n", " ", "Online Shopping service", " ");
            for (int i = 0; i < 50; i++) 
                System.out.print("-");
            System.out.format("\n|%20s%s%20s|", " ", "1. Login", " ");
            System.out.format("\n|%16s%s%15s|", " ", "2. Create account", " ");
            System.out.format("\n|%20s%s%21s|\n", " ", "3. Exit", " ");
            for (int i = 0; i < 50; i++) 
                System.out.print("-");
            System.out.print("\nEnter choice: ");
            choice = sc.nextInt();

            switch (choice) {
                // Login
                case 1:
                    // If the user is found (index != -1), start the main menu
                    index = login(usersList);
                    if (index != -1)
                        usersList.get(index).mainMenu();
                    break;

                // Create an account
                case 2: 
                    sc.nextLine();
                    User ob = new User();
                    usersList.add(ob);
                    ob.createUser();
                    System.out.println("\nAccount created successfully.");
                    break;
                case 3:
                    sc.close();
                    con.close();
            }
        } while (choice == 1 || choice == 2);

        sc.close();
    }

    // Login method that recieves input from user and searches usersList to find a matching username and password
    // Returns the position of the matching user in the usersList. If the user is not found, -1 is returned
    public static int login(ArrayList<User> usersList) {
        // Set to the index in the list if user is found
        int index = -1;
        // Set to 1 if user is found
        int flag = 0;
        String uname, pass;
        rs = null;

        System.out.print("Enter username: ");
        uname = sc.nextLine();
        System.out.print("Enter password: ");
        pass = sc.nextLine();

        try {
            rs = stmt.executeQuery("SELECT COUNT(*) FROM USERS WHERE USERNAME = " + "\"" + uname + "\"" + " AND PASSWORD = " + "\"" + pass + "\";");
            rs.next();
            if (rs.getInt(1) == 0)
                flag = 1; 
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();

        for (int i = 0; i < usersList.size(); i++) {
            if (uname.equals(usersList.get(i).username)) {
                if (pass.equals(usersList.get(i).password)) {
                    index = i;
                    break;
                }
            }
        }

        if (index == -1 || flag == 1) 
            System.out.println("\nInvalid username/password.");

        return index;
    }
}