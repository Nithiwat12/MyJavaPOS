import javax.swing.*;
    import java.awt.*;
    import java.io.BufferedReader;
    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.FileReader;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.text.SimpleDateFormat;
    import java.time.LocalDate;
    import java.time.ZoneId;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Date;

    class Users {
    String ID;
    String Name;
        

    public Users(String ID,String Name){
        this.ID = ID;
        this.Name = Name;
    }
    @Override
        public String toString() {
            return ID + "," + Name;
        }

    }
    class Products{
        String ID;
        String Name;
        int price;
        int Stock;   
        
        public Products(String ID, String Name, int price, int Stock){
            this.ID = ID;
            this.Name = Name;
            this.price = price;
            this.Stock = Stock;
        }
        @Override
        public String toString() {
            return ID + "," + Name + "," + price + "," + Stock;
        }
        public String getName() {
            return Name;
        }
        }
    class Sales {
        String ID;
        ArrayList<Order> order;
        int Totalprice;
        Date currentDate;
        public Sales(String ID,ArrayList<Order> order,int Totalprice,Date currentDate){
            this.ID = ID;
            this.order = order;
            this.Totalprice = Totalprice;
            this.currentDate = currentDate;
        }
        @Override
        public String toString() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuilder ordersStr = new StringBuilder();
            for (Order o : order) {
                ordersStr.append(o.nameProduct).append(";")
                         .append(o.countProduct).append(";")
                         .append(o.price).append("|");
            }
            
            if (ordersStr.length() > 0) {
                ordersStr.setLength(ordersStr.length() - 1);
            }
            return ID + "||" + ordersStr.toString() + "||" + Totalprice + "||" + dateFormat.format(currentDate);
        }

        public ArrayList<Order> getOrders() {
            return order;
        }
        public void setSaleDate(Date saleDate) {
            this.currentDate = saleDate;
        }
    }

    class Order{
        String nameProduct;
        int countProduct;
        int price;
        public Order(String nameProduct,int countProduct,int price){
            this.nameProduct = nameProduct;
            this.countProduct = countProduct;
            this.price = price;
        }
        @Override
        public String toString() {
            return nameProduct + "," + countProduct+ "," + price;
        }
    }
        
        

    public class App{
        private JFrame window;
        private JTextField productID,productNAME,productPRICE,productStock,saleName,saleID,saleProduct,saleCount,MemID,MemName,Buy;
        private JButton BProduct,BSale,BSubsale,BCancelsale,BCSale,BSubMem,BCanMem,BBuy,BBacksale,BCBill;
        private JPanel panelC_Sale, panel_B, panel_product1,panel_product2,panel_product3,panel_product4,panel_sale2,panel_sale3,panel_sale4,panel_MemID,panel_MemName,panel_BMem,panel_buy,panel_bbuy,panel_CBill;
        private JLabel label_product1,label_product2,label_product3,label_product4,label_sale1,label_sale2,label_sale3,label_sale4,label_outsale,label_MemID,label_MemName,label_buy;
        private JTextArea textarea_sale,textarea_product,textarea_bill;
        private JComboBox<String> productDropdown;

        private  ArrayList<Users> users = new ArrayList();
        private  ArrayList<Products> product = new ArrayList();
        private  ArrayList<Sales> sales = new ArrayList();
        private  ArrayList<Order> order = new ArrayList();
        static final String FILE_NAME_PRODUCT = "products.txt";
        static final String FILE_SALE = "order.txt";
        static final String FILE_USER = "users.txt";
        static int total = 0; 
        static boolean done = true;
        static int IntAmount;

        public static void main(String[] args){
            App pos = new App();
                pos.POS();

        }
        public void POS() {
    
            while (done) {
                String Menu = """
                    1. AddProduct (Admin)\n 
                    2. Edit Delete(Admin)\n
                    3. Sales (Customer)\n
                    4. Register Member (Customer)\n
                    5. Report Sale of Day\n
                    6. Exit\n
                    """;
            String choice = JOptionPane.showInputDialog(null, Menu, "Point of Sale", JOptionPane.QUESTION_MESSAGE);
            
            if(choice == null || choice.isEmpty() ){
                String alert = "No this choice, please input again";
                JOptionPane.showMessageDialog(null, alert, "Message", JOptionPane.INFORMATION_MESSAGE);
                done = false;
                return;
            }

            if(choice.equals("1")){
                AddProduct();
                done = false;
            }

            else if(choice.equals("2")){
                ED();
                done = false;
            }
            else if(choice.equals("3")){
                BuyProduct();
                done = false;
            }
            else if(choice.equals("4")){
                RegisterMember();
                done = false;
            }
            else if(choice.equals("5")){
                DayOfSale();
                done = false;
            }
            else if(choice.equals("6")){
                JOptionPane.showMessageDialog(null, "Exit Program", "Message", JOptionPane.INFORMATION_MESSAGE);
                done = false;
            }
            else{
                String alert = "No this choice, please input again";
                JOptionPane.showMessageDialog(null, alert, "Message", JOptionPane.INFORMATION_MESSAGE);
                done = false;
            }
            }
        }
        public void DayOfSale() {
            ArrayList<Sales> salesList = loadsale();
            System.out.println(salesList);
            window = new JFrame("Daily Sales Report");
            window.setSize(800, 600);
            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            window.setLocationRelativeTo(null);

            JPanel panel_main = new JPanel(new BorderLayout());
            panel_main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JTextArea reportArea = new JTextArea();
            reportArea.setEditable(false);
            reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scrollPane = new JScrollPane(reportArea);

            StringBuilder report = new StringBuilder();
            
            LocalDate today = LocalDate.now();
            report.append(String.format("%50s\n", "DAILY SALES REPORT"));
            report.append(String.format("%50s\n", "Date: " + today));
            report.append(String.format("%50s\n\n", "=============================="));
            
            if (salesList.isEmpty()) {
                report.append("No sales data available.\n");
            } else {
                double dailyTotal = 0;
                int totalTransactions = 0;
                int totalItemsSold = 0;

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                
                report.append(String.format("%-15s %-20s %-15s %-15s %-15s\n", 
                    "Sale ID", "Time", "Items Sold", "Total Amount", "Customer"));
                report.append(String.format("%-15s %-20s %-15s %-15s %-15s\n", 
                    "-------", "----", "---------", "------------", "--------"));

                for (Sales sale : salesList) {
                    LocalDate saleDate = sale.currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    
                    if (saleDate.equals(today)) {
                        totalTransactions++;
                        int itemsInSale = sale.getOrders().stream().mapToInt(o -> o.countProduct).sum();
                        totalItemsSold += itemsInSale;
                        dailyTotal += sale.Totalprice;
                        
                        String customerName = sale.ID.isEmpty() ? "Guest" : getCustomerName(sale.ID);
                        if(sale.ID.isEmpty()){
                            sale.ID = "No Member";
                        }
                        report.append(String.format("%-15s %-20s %-15d %-15d %-15s\n",  
                            sale.ID,
                            timeFormat.format(sale.currentDate),
                            itemsInSale,
                            sale.Totalprice, 
                            customerName));

                    }
        }

        if (totalTransactions == 0) {
            report.append("\nNo sales recorded for today.\n");
        } else {
            report.append("\n");
            report.append(String.format("%-15s %-20s %-15s %-15s\n", "", "", "-------------", "-------------"));
            report.append(String.format("%-15s %-20s %-15d %-15.2f THB\n", 
                "", "DAILY TOTAL:", totalItemsSold, dailyTotal));
            report.append(String.format("%-35s %15s\n", "", "=============="));
            report.append(String.format("%-35s %15d %s\n", "Total Transactions:", totalTransactions, "transactions"));
        }
    }

    reportArea.setText(report.toString());

    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> {
        window.dispose();
        done = true;
        SwingUtilities.invokeLater(() -> POS());
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(closeButton);

    panel_main.add(scrollPane, BorderLayout.CENTER);
    panel_main.add(buttonPanel, BorderLayout.SOUTH);

    window.add(panel_main);
    window.setVisible(true);
}
        
        
        private String getCustomerName(String customerId) {
            ArrayList<Users> members = loadMember();
            return members.stream()
                         .filter(u -> u.ID.equals(customerId))
                         .findFirst()
                         .map(u -> u.Name)
                         .orElse("Guest");
        }
        
        public void RegisterMember(){
            window = new JFrame("Point of Sale");
            JPanel panel_main = new JPanel();
            panel_main.setAlignmentX(Component.CENTER_ALIGNMENT);
            window.setSize(300, 300);
            panel_main.setPreferredSize(new Dimension(500, 500));
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setLocationRelativeTo(null);
            window.setVisible(true);

            label_MemID = new JLabel("Member ID : ");
            panel_MemID = new JPanel();
            panel_MemID.setMaximumSize(new Dimension(400, 40));
            panel_MemID.add(label_MemID);
            MemID = new JTextField(15);
            MemID.setPreferredSize(new Dimension(350,30));
            MemID.setHorizontalAlignment(JTextField.RIGHT);
            panel_MemID.add(MemID);
            panel_main.add(panel_MemID);

            label_MemName = new JLabel("Member Name : ");
            panel_MemName = new JPanel();
            panel_MemName.setMaximumSize(new Dimension(400, 40));
            panel_MemName.add(label_MemName);
            MemName = new JTextField(15);
            MemName.setPreferredSize(new Dimension(350,30));
            MemName.setHorizontalAlignment(JTextField.RIGHT);
            panel_MemName.add(MemName);
            panel_BMem = new JPanel();
            BSubMem = new JButton("Submit");
            BCanMem = new JButton("Cancle");
            panel_BMem.add(BSubMem);
            panel_BMem.add(BCanMem);
            panel_main.add(panel_MemName);
            panel_main.add(panel_BMem);

            BSubMem.addActionListener(e -> {
                saveMember();
            });
            BCanMem.addActionListener(e -> {
                window.dispose();
                done = true;
                SwingUtilities.invokeLater(() -> POS());
            });
            window.add(panel_main);
        }


        public void BuyProduct(){
            ArrayList<Products> products = loadproduct();
            
            if (window != null) {
                window.dispose(); 
            }
            window = new JFrame("Point of Sale");
            JPanel panel_main = new JPanel();
            panel_main.setAlignmentX(Component.CENTER_ALIGNMENT);
            window.setSize(500, 500);
            panel_main.setPreferredSize(new Dimension(500, 500));
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setLocationRelativeTo(null);
            window.setVisible(true);


            
            label_sale3 = new JLabel("Select Product: ");
            panel_sale3 = new JPanel();
            productDropdown = new JComboBox<>(products.stream().map(Products::getName).toArray(String[]::new));
            productDropdown.setPreferredSize(new Dimension(350, 30));
            panel_sale3.add(label_sale3);
            panel_sale3.add(productDropdown);
            panel_main.add(panel_sale3);  

            label_sale4 = new JLabel("Quantity Product : ");
            panel_sale4 = new JPanel();
            panel_sale4.setMaximumSize(new Dimension(400, 40));
            panel_sale4.add(label_sale4);
            saleCount = new JTextField(15);
            saleCount.setPreferredSize(new Dimension(350,30));
            saleCount.setHorizontalAlignment(JTextField.RIGHT);
            panel_sale4.add(saleCount);
            panel_main.add(panel_sale4);
            BBacksale = new JButton("Back");
            BSubsale = new JButton("Submit");
            BCancelsale = new JButton("Sale");   
            BBacksale.addActionListener(e ->  {
                window.dispose();
                done = true;
                SwingUtilities.invokeLater(() -> POS());
            }); 
            panel_B = new JPanel();
            panel_B.add(BBacksale);
            panel_B.add(BSubsale);
            panel_B.add(BCancelsale);
            panel_main.add(panel_B);
            
            BSubsale.addActionListener(e -> {
                String ProductName = (String) productDropdown.getSelectedItem();
                String ProductCount = saleCount.getText();

                if(ProductCount.isEmpty()){
                    JOptionPane.showMessageDialog(window, "Please fill in all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    int intCount = Integer.parseInt(ProductCount);
                    
                    for (Products p : products) {
                        if (p.Name.equals(ProductName)) {
                            if (p.Stock >= intCount) {
                                Order sale = new Order(ProductName, intCount,p.price);
                                order.add(sale);
            
                                JOptionPane.showMessageDialog(window, "Product: " + ProductName + ", Quantity: " + ProductCount, "Success", JOptionPane.INFORMATION_MESSAGE);
                                saleCount.setText("");  
                                System.out.println(order);
                                BuyProduct();  
                                return;
                            } else {
                                JOptionPane.showMessageDialog(window, "Not enough stock!", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    
                    JOptionPane.showMessageDialog(window, "Invalid quantity entered! Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                    
            });


            BCancelsale.addActionListener(e -> {
                window.dispose();
                Sales();

            });
            panel_main.revalidate();
            panel_main.repaint();
            window.add(panel_main);
            window.pack();
        }
            
        public void Sales(){
            window = new JFrame("Member");
            JPanel panel_main = new JPanel();
            panel_main.setLayout(new BoxLayout(panel_main, BoxLayout.Y_AXIS));
            panel_main.setAlignmentX(Component.CENTER_ALIGNMENT);
            window.setSize(500,500);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);
            window.setLocationRelativeTo(null);

            label_sale2 = new JLabel("Your Member ID : ");
            panel_sale2 = new JPanel();
            panel_sale2.setMaximumSize(new Dimension(400, 40));
            panel_sale2.add(label_sale2);
            saleID = new JTextField(15);
            saleID.setPreferredSize(new Dimension(350,30));
            saleID.setHorizontalAlignment(JTextField.RIGHT);
            panel_sale2.add(saleID);
            panel_main.add(panel_sale2);
            panelC_Sale = new JPanel();
            BSale = new JButton("Submit");
            BCSale = new JButton("No Member");
            panelC_Sale.add(BSale);
            panelC_Sale.add(BCSale);
            BCSale.addActionListener(e -> {
                window.dispose();
                done = true;
                Out_Sale();
            });
            BSale.addActionListener(e -> {
                ArrayList<Users> members = loadMember();
                String ID = saleID.getText();
                boolean idFound = false; 

                for (Users u : members) {
                    if (u.ID != null && u.ID.equals(ID)) { 
                        
                        Out_Sale();
                        idFound = true;  
                        break;
                    }
                }

                if (!idFound) {
                    JOptionPane.showMessageDialog(null, "Error saving updated product data!", "Error", JOptionPane.ERROR_MESSAGE);

                }     
                    
            });
            panel_main.add(panelC_Sale);
            

            window.add(panel_main);
            
        }

        public void Out_Sale() {
            ArrayList<Products> products = loadproduct(); 
            done = false;
            total = 0; 
            boolean found = false;
            
            
            window.dispose();  
            window = new JFrame("Sales Summary");
            window.setSize(400, 400);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setLocationRelativeTo(null);
            
   
            JPanel panel_main = new JPanel();
            panel_main.setLayout(new BoxLayout(panel_main, BoxLayout.Y_AXIS));

            textarea_sale = new JTextArea();
            textarea_sale.setEditable(false);
            textarea_sale.setLineWrap(true);
            textarea_sale.setWrapStyleWord(true);
            

            StringBuilder receipt = new StringBuilder();


                receipt.append("===== Sales Receipt ===== \n\n");


            for (Order o : order) {
                found = false;
                
                for (Products p : products) {
                    if (o.nameProduct.equals(p.Name)) {
                        found = true;
                        

                        
                            receipt.append(String.format("%-15s %-10s %-15s\n", 
                            "Product: " + o.nameProduct,
                            "Qty: " + o.countProduct,
                            "Price: " + p.price + " THB"));
                        
                        

                        total += p.price * o.countProduct; 
                        

                        p.Stock -= o.countProduct;
                        

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME_PRODUCT))) {
                            for (Products prod : products) {
                                if (prod.Stock > 0) {
                                    writer.write(prod.toString());
                                    writer.newLine();
                                }
                            }
                        } catch (IOException e) {
                            JOptionPane.showMessageDialog(null, 
                                "Error saving updated product data!", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                        break;
                    }
                }
                
                if (!found) { 
                    receipt.append(String.format("%-15s %-10s %-15s\n", 
                        "Product: " + o.nameProduct,
                        "Qty: " + o.countProduct,
                        "[ERROR] Product Not Found!"));
                }
            }
            


            receipt.append("\nTotal Price: ").append(total).append(" THB\n");
            textarea_sale.setText(receipt.toString());
            

            label_buy = new JLabel("Enter amount: ");
            panel_buy = new JPanel();
            panel_buy.setMaximumSize(new Dimension(400, 40));
            Buy = new JTextField(15);
            Buy.setPreferredSize(new Dimension(350, 30));
            Buy.setHorizontalAlignment(JTextField.RIGHT);
            
            panel_buy.add(label_buy);
            panel_buy.add(Buy);
            
            
            BBuy = new JButton("Submit");
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> {
                window.dispose();
                done = true;
                SwingUtilities.invokeLater(() -> POS());  
            });

            BBuy.addActionListener(e -> {
                String amount = Buy.getText();
                
                try {
                    IntAmount = Integer.parseInt(amount);
                    String ID = saleID.getText();

                    if(IntAmount >= total){

                        Date currentDate = new Date();
                        Sales newSale = new Sales(ID,order,total,currentDate);
                        sales.add(newSale);                       
                        saveSale();
                        SwingUtilities.invokeLater(() -> Bill());
                        System.out.println(sales);
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Invalid Amount", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid price or stock format!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
            });
            panel_bbuy = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            panel_bbuy.setMaximumSize(new Dimension(350, 40));
            

            panel_bbuy.add(closeButton);
            panel_bbuy.add(BBuy);

            JScrollPane scrollPane = new JScrollPane(textarea_sale);
            panel_main.add(scrollPane);
            panel_main.add(panel_buy);
            panel_main.add(panel_bbuy);
            

            window.add(panel_main);
            window.setVisible(true);
            JOptionPane.showMessageDialog(null, 
                "Product stock updated!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        }
        public void Bill() {
            done = false;
            ArrayList<Users> members = loadMember();
            window = new JFrame("Bill Receipt");
            window.setSize(500, 500);  
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setLocationRelativeTo(null);
            window.setVisible(true);
            JPanel panel_main = new JPanel();
            panel_main.setLayout(new BoxLayout(panel_main, BoxLayout.Y_AXIS));
            panel_main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
            textarea_bill = new JTextArea();
            textarea_bill.setEditable(false);
            textarea_bill.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
            StringBuilder receipt = new StringBuilder();

            boolean found = false;
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; 
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);


            
            String ID = saleID.getText();
            if(ID.isEmpty()){
                receipt.append("===== Bill Receipt ===== "+"\n"+"User : No members"+"\n\n").append(String.format("%d-%02d-%02d %02d:%02d%n", year, month, day, hour, minute)+"\n\n");
            }
            for (Users u : members) {
                if(u.ID.equals(ID)){
                    receipt.append("===== Bill Receipt ===== "+"\n"+"User : "+u.Name+"\n\n").append(String.format("%d-%02d-%02d %02d:%02d%n", year, month, day, hour, minute)+"\n\n");
                    break;
                }
            }
            
            try {
                for (Sales s : sales) {
                    if(s.ID.equals(ID)) {
                        found = true;
                        

                        receipt.append(String.format("%-20s %10s %15s\n", 
                            "Product", "Quantity", "Price"));  
                        receipt.append("----------------------------------------------\n");
                        for (Order o : s.getOrders()) {
                            
                            receipt.append(String.format("%-20s %10d %15.2f THB\n",
                            o.nameProduct,     
                            o.countProduct,    
                            (double)o.price));
                        }
    
                        receipt.append("\nTotal Price: ").append(total).append(" THB\n");
                        receipt.append("Your paid: ").append(IntAmount).append(" THB\n");
                        receipt.append("Your change: ").append(IntAmount-total).append(" THB\n");
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            
            if (!found) {
                receipt.append("Sale ID not found!\n");
            }
            
            
            textarea_sale.setText(receipt.toString());
            panel_CBill = new JPanel();
            BCBill = new JButton("Close");
            BCBill.addActionListener(e ->{
                window.dispose();
                order.clear();
                sales.clear();
                done = true;
                SwingUtilities.invokeLater(() -> POS());
            });
            panel_CBill.add(BCBill);
            panel_main.add(textarea_sale);
            panel_main.add(panel_CBill);
            window.add(panel_main);
        }
     
        

        public void AddProduct() {

            JFrame window = new JFrame("Point of Sale");
            window.setSize(800, 800);
            JPanel panel_main = new JPanel();
            panel_main.setLayout(new BoxLayout(panel_main, BoxLayout.Y_AXIS));
            panel_main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel_main.setBackground(Color.LIGHT_GRAY);
            
        
            panel_product1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            label_product1 = new JLabel("ID Product: ");
            productID = new JTextField(15);
            productID.setPreferredSize(new Dimension(300, 30));
            productID.setHorizontalAlignment(JTextField.RIGHT);
            panel_product1.setPreferredSize(new Dimension(300, 5));
            panel_product1.add(label_product1);
            panel_product1.add(productID);
            

            panel_product2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            label_product2 = new JLabel("Name Product: ");
            productNAME = new JTextField(15);
            productNAME.setPreferredSize(new Dimension(300, 30));
            productNAME.setHorizontalAlignment(JTextField.RIGHT);
            panel_product2.add(label_product2);
            panel_product2.add(productNAME);
            

            panel_product3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            label_product3 = new JLabel("Price Product: ");
            productPRICE = new JTextField(15);
            productPRICE.setPreferredSize(new Dimension(300, 30));
            productPRICE.setHorizontalAlignment(JTextField.RIGHT);
            panel_product3.add(label_product3);
            panel_product3.add(productPRICE);
            

            panel_product4 = new JPanel(new FlowLayout(FlowLayout.CENTER));
            label_product4 = new JLabel("Stock Product: ");
            productStock = new JTextField(15);
            productStock.setPreferredSize(new Dimension(300, 30));
            productStock.setHorizontalAlignment(JTextField.RIGHT);
            panel_product4.add(label_product4);
            panel_product4.add(productStock);
            
    
            BProduct = new JButton("Save Product");
        
            BProduct.setAlignmentX(Component.CENTER_ALIGNMENT);
            BProduct.addActionListener(e -> {
                saveProduct();
                window.dispose();
            });
            
    
            textarea_product = new JTextArea(10, 30);
            textarea_product.setEditable(false);
            textarea_product.setLineWrap(true);
            textarea_product.setWrapStyleWord(true);
            

        
            ArrayList<Products> products = loadproduct();
            if (products.isEmpty()) {
                textarea_product.setText("No products available.\n");
            } else {
                StringBuilder receipt = new StringBuilder();
                receipt.append("\t\t================== Product List =======================\n\n");
                receipt.append("ID :\tName :\tPrice :\tStock :\n\n");
                for (Products p : products) {
                    receipt.append("-----------------------------------------------------------------------------------\n");
                    receipt.append(p.ID + " \t" + p.Name + " \t" + p.price + " \t" + p.Stock + "\n");
                }
                textarea_product.setText(receipt.toString());
            }
            
            JScrollPane scrollPane = new JScrollPane(textarea_product);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setPreferredSize(new Dimension(350, 200)); 

            panel_main.add(panel_product1);
            panel_main.add(Box.createRigidArea(new Dimension(0, 5)));
            panel_main.add(panel_product2);
            panel_main.add(Box.createRigidArea(new Dimension(0, 5)));
            panel_main.add(panel_product3);
            panel_main.add(Box.createRigidArea(new Dimension(0, 5)));
            panel_main.add(panel_product4);
            panel_main.add(Box.createRigidArea(new Dimension(0, 10)));
            panel_main.add(BProduct);
            panel_main.add(Box.createRigidArea(new Dimension(0, 10)));
            panel_main.add(scrollPane);
            

            window.add(panel_main);

            window.setLocationRelativeTo(null);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);
            
        }


        public void saveProduct(){
            String id = productID.getText();
            String name = productNAME.getText();
            String priceText = productPRICE.getText();
            String stockText = productStock.getText();

            ArrayList<Products> products = loadproduct();
            
            if (id.isEmpty() || name.isEmpty() || priceText.isEmpty()|| stockText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (product == null) {
                product = new ArrayList<>();
            }
        

            for (Products p : products) {
                if (p.ID.equals(id)) {
                    JOptionPane.showMessageDialog(null, "Product ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        
            try {
                int price = 0, stock = 0;
                try {
                    price = Integer.parseInt(priceText);
                    stock = Integer.parseInt(stockText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid price or stock format!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            

                File file = new File(FILE_NAME_PRODUCT);
                try {
                    if (!file.exists()) {
                        file.createNewFile();  
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error creating file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                    Products newProduct = new Products(id, name, price, stock);
                    product.add(newProduct);
                    writer.write(newProduct.toString());
                    writer.newLine();
                }
            
                JOptionPane.showMessageDialog(null, "Product Saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
            

                if (window != null) {
                    window.dispose();
                }
            
                done = true;
            

                SwingUtilities.invokeLater(() -> {
                    try {
                        POS();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error in POS(): " + ex, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Unexpected error: " + e, "Error", JOptionPane.ERROR_MESSAGE);
            }

    
        } 

    public void saveMember(){
        String ID = MemID.getText().trim();
        String Name = MemName.getText().trim();
        ArrayList<Users> members = loadMember();
        if(ID.isEmpty() || Name.isEmpty()){
            JOptionPane.showMessageDialog(null, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
  System.out.println(members);
        for (Users u : members) {
            if(u.ID.equals(ID)){
                JOptionPane.showMessageDialog(null, "Member ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }
        }
        File file = new File(FILE_USER);
        try {
            if (!file.exists()) {
                file.createNewFile();  
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error creating file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            Users newUSer = new Users(ID,Name);
            users.add(newUSer);
                    writer.write(newUSer.toString());
                    writer.newLine();
                    JOptionPane.showMessageDialog(null, "Member saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (window != null) {
            window.dispose();
        }
    
        done = true;
        SwingUtilities.invokeLater(() -> {
            try {
                POS();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error in POS(): " + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public ArrayList<Users> loadMember(){
        ArrayList<Users> MemberList = new ArrayList<>();
        File file = new File(FILE_USER);
        if (!file.exists()) {
            System.out.println("Product file not found. Returning empty list.");
            return MemberList;
        }


        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_USER))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Reading line: " + line); 

                line = line.replaceAll("[\\[\\]]", ""); 

                String[] data = line.split(",");
                if (data.length == 2) {
                    try {
                        MemberList.add(new Users(data[0], data[1]));
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid line: " + line);
                    }
                } else {
                    System.out.println("Invalid format line: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading products: " + e.getMessage());
        }

        return MemberList;
    }



        public ArrayList<Products> loadproduct(){
            
            ArrayList<Products> productList = new ArrayList<>();
            File file = new File(FILE_NAME_PRODUCT);

            if (!file.exists()) {
                System.out.println("Product file not found. Returning empty list.");
                return productList;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME_PRODUCT))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Reading line: " + line); 

                    line = line.replaceAll("[\\[\\]]", ""); 

                    String[] data = line.split(",");
                    if (data.length == 4) {
                        try {
                            productList.add(new Products(data[0], data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3])));
                        } catch (NumberFormatException e) {
                            System.out.println("Skipping invalid line: " + line);
                        }
                    } else {
                        System.out.println("Invalid format line: " + line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error loading products: " + e.getMessage());
            }

            System.out.println("Loaded " + productList.size() + " products.");
            return productList;
    }

        public void ED(){
            String Menu = """
                    1. Edit Product (Admin)\n
                    2. Delete Product (Admin)\n
                    3. Edit Member\n
                    4. Delete Member\n
                    """;
            String choice = JOptionPane.showInputDialog(null, Menu, "Point of Sale", JOptionPane.QUESTION_MESSAGE);
            if(choice == null){
                done = true;
                POS();
                return;
            }
            
            if(choice.equals("1")){
                Edit_product();
            }
            else if(choice.equals("2")){
                Delete_product();
            }
            else if(choice.equals("3")){
                Edit_Member();
            }
            else if(choice.equals("4")){
                Delete_Member();
            }
        }
        public void Edit_Member(){
            ArrayList<Users> members = loadMember();
            String id = JOptionPane.showInputDialog(null,"Input File ID for edit:", "Point of Sale", JOptionPane.QUESTION_MESSAGE);
            boolean found = false;
            if (id == null || id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return; //เปลี่ยนไปหน้าแรก
            }

            for (Users u : members) {
                if(u.ID.equals(id)){
                    found = true;
                    String newName = JOptionPane.showInputDialog(null,"Input New Name :", "Point of Sale", JOptionPane.QUESTION_MESSAGE);
                if(!u.Name.equals(newName)){
                    u.Name = newName;
                }else{
                    JOptionPane.showMessageDialog(null, "Name is used", "Error", JOptionPane.ERROR_MESSAGE);
                    break;//เปลี่ยนไปหน้าแรก
                }
                if (!found) {
                    JOptionPane.showMessageDialog(null, "User ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;//เปลี่ยนไปหน้าแรก
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_USER))) {
                    for (Users users : members) {
                        writer.write(users.toString());
                        writer.newLine();
                    }
                    JOptionPane.showMessageDialog(null, "User updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error saving product data!", "Error", JOptionPane.ERROR_MESSAGE);
                }
                
                
                }
                
            }
        }
        public void Delete_Member(){
            ArrayList<Users> members = loadMember();
            String id = JOptionPane.showInputDialog(null,"Input File ID for delete:", "Point of Sale", JOptionPane.QUESTION_MESSAGE);
            boolean found = false;
        
            if (id == null || id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;//เปลี่ยนไปหน้าแรก
            }
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i).ID.equals(id)) {
                    found = true;
                    members.remove(i);
                    break;//เปลี่ยนไปหน้าแรก
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(null, "User ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;//เปลี่ยนไปหน้าแรก
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_USER))){
                for (Users users : members) {
                    writer.write(users.toString());
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(null, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error saving User data!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public void Delete_product(){
            ArrayList<Products> products = loadproduct();
            String id = JOptionPane.showInputDialog(null,"Input File ID for delete:", "Point of Sale", JOptionPane.QUESTION_MESSAGE);
            boolean found = false;
            if (id == null || id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;//เปลี่ยนไปหน้าแรก
            }

            for (int i = 0; i < products.size(); i++) {
                if (products.get(i).ID.equals(id)) {
                    found = true;
                    products.remove(i);
                    break;//เปลี่ยนไปหน้าแรก
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(null, "Product ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;//เปลี่ยนไปหน้าแรก
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME_PRODUCT))){
                for (Products p : products) {
                    writer.write(p.toString());
                    writer.newLine();
                }
                JOptionPane.showMessageDialog(null, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error saving product data!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public void Edit_product(){
            ArrayList<Products> products = loadproduct();
            String id = JOptionPane.showInputDialog(null,"Input File ID for edit:", "Point of Sale", JOptionPane.QUESTION_MESSAGE);
            boolean found = false;
            if (id == null || id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;//เปลี่ยนไปหน้าแรก
            }
        for (Products p : products) {

            if (p.ID.equals(id)) {
                found = true;
            String newName = JOptionPane.showInputDialog(null,"Input New Name :", "Point of Sale", JOptionPane.QUESTION_MESSAGE);
            String newPrice = JOptionPane.showInputDialog(null,"Input New price :", "Point of Sale", JOptionPane.QUESTION_MESSAGE);
            String newStock = JOptionPane.showInputDialog(null,"Input New stock :", "Point of Sale", JOptionPane.QUESTION_MESSAGE);
            if(!p.Name.equals(newName)){
                try {
                    p.Name = newName;
                    p.price = Integer.parseInt(newPrice);
                    p.Stock = Integer.parseInt(newStock);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Price and Stock must be numbers!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;//เปลี่ยนไปหน้าแรก
                    }
            }else{
                JOptionPane.showMessageDialog(null, "Name is used", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }
            break;

            }
        }
        if (!found) {
            JOptionPane.showMessageDialog(null, "Product ID not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;//เปลี่ยนไปหน้าแรก
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME_PRODUCT))) {
            for (Products p : products) {
                writer.write(p.toString());
                writer.newLine();
            }
            JOptionPane.showMessageDialog(null, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving product data!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        //เปลี่ยนไปหน้าแรก
    }



    public void saveSale(){
      
        File file = new File(FILE_SALE);
        try {
            if (!file.exists()) {
                file.createNewFile();  
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error creating file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
           for (Sales s : sales) {
                    writer.write(s.toString());
                    writer.newLine();
           }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (window != null) {
            window.dispose();
        }
    
        done = true;
        SwingUtilities.invokeLater(() -> {
            try {
                SwingUtilities.invokeLater(() -> POS());
                window.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error in POS(): " + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    public ArrayList<Sales> loadsale() {
        ArrayList<Sales> saleList = new ArrayList<>();
        File file = new File(FILE_SALE);
    
        if (!file.exists()) {
            System.out.println("Sales file not found. Returning empty list.");
            return saleList;
        }
    
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_SALE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split("\\|\\|"); 
                    if (parts.length == 4) {
                        String saleId = parts[0];
                        String ordersStr = parts[1];
                        int totalPrice = Integer.parseInt(parts[2]);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date saleDate = dateFormat.parse(parts[3]);
    
                        ArrayList<Order> orders = new ArrayList<>();
                        if (!ordersStr.isEmpty()) {
                            String[] orderItems = ordersStr.split("\\|");
                            for (String item : orderItems) {
                                String[] orderData = item.split(";");
                                if (orderData.length == 3) {
                                    String productName = orderData[0];
                                    int quantity = Integer.parseInt(orderData[1]);
                                    int price = Integer.parseInt(orderData[2]);
                                    orders.add(new Order(productName, quantity, price));
                                }
                            }
                        }
    
                        Sales sale = new Sales(saleId, orders, totalPrice, saleDate);
                        saleList.add(sale);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading sales file: " + e.getMessage());
        }
    
        return saleList;
    }
    


        }