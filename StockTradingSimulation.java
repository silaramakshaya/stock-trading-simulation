import java.io.*;
import java.util.*;

// --- Stock Class ---
class Stock {
    private String symbol;
    private double price;

    public Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }

    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }

    public void updatePrice() {
        double change = (Math.random() * 10) - 5; // random fluctuation
        price = Math.max(1, price + change);
    }

    @Override
    public String toString() {
        return symbol + ": $" + String.format("%.2f", price);
    }
}

// --- Portfolio Class ---
class Portfolio {
    private Map<String, Integer> holdings = new HashMap<>();
    private double cash = 10000.0;

    public void buy(Stock stock, int shares) {
        double cost = stock.getPrice() * shares;
        if (cost > cash) {
            System.out.println("❌ Not enough cash to buy.");
            return;
        }
        cash -= cost;
        holdings.put(stock.getSymbol(), holdings.getOrDefault(stock.getSymbol(), 0) + shares);
        System.out.println("✅ Bought " + shares + " shares of " + stock.getSymbol() + " at $" + stock.getPrice());
    }

    public void sell(Stock stock, int shares) {
        int owned = holdings.getOrDefault(stock.getSymbol(), 0);
        if (owned < shares) {
            System.out.println("❌ Not enough shares to sell.");
            return;
        }
        double revenue = stock.getPrice() * shares;
        cash += revenue;
        holdings.put(stock.getSymbol(), owned - shares);
        if (holdings.get(stock.getSymbol()) == 0) {
            holdings.remove(stock.getSymbol());
        }
        System.out.println("✅ Sold " + shares + " shares of " + stock.getSymbol() + " at $" + stock.getPrice());
    }

    public double portfolioValue(Map<String, Stock> market) {
        double value = cash;
        for (String symbol : holdings.keySet()) {
            value += holdings.get(symbol) * market.get(symbol).getPrice();
        }
        return value;
    }

    public void displayPortfolio(Map<String, Stock> market) {
        System.out.println("\n💰 Cash: $" + String.format("%.2f", cash));
        System.out.println("📈 Portfolio Value: $" + String.format("%.2f", portfolioValue(market)));
        System.out.println("Holdings: " + holdings);
    }

    public void save(String filename) {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println(cash);
            for (String symbol : holdings.keySet()) {
                out.println(symbol + "," + holdings.get(symbol));
            }
            System.out.println("💾 Portfolio saved.");
        } catch (IOException e) {
            System.out.println("Error saving portfolio.");
        }
    }

    public void load(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            cash = Double.parseDouble(br.readLine());
            holdings.clear();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                holdings.put(parts[0], Integer.parseInt(parts[1]));
            }
            System.out.println("📂 Portfolio loaded.");
        } catch (IOException e) {
            System.out.println("⚠️ No saved portfolio found.");
        }
    }
}

// --- Market Class ---
class Market {
    private Map<String, Stock> stocks = new HashMap<>();

    public Market(List<Stock> stockList) {
        for (Stock s : stockList) {
            stocks.put(s.getSymbol(), s);
        }
    }

    public void update() {
        for (Stock s : stocks.values()) {
            s.updatePrice();
        }
    }

    public void display() {
        System.out.println("\n📊 Market Data:");
        for (Stock s : stocks.values()) {
            System.out.println(s);
        }
    }

    public Map<String, Stock> getStocks() {
        return stocks;
    }
}

// --- Main Simulation ---
public class StockTradingSimulation {
    public static void main(String[] args) {
        Market market = new Market(Arrays.asList(
                new Stock("AAPL", 150),
                new Stock("GOOG", 2800),
                new Stock("TSLA", 700)
        ));
        Portfolio portfolio = new Portfolio();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            market.update();
            market.display();
            portfolio.displayPortfolio(market.getStocks());

            System.out.print("\nChoose action (buy/sell/save/load/quit): ");
            String action = scanner.nextLine().toLowerCase();

            if (action.equals("buy")) {
                System.out.print("Enter stock symbol: ");
                String symbol = scanner.nextLine().toUpperCase();
                System.out.print("Enter number of shares: ");
                int shares = Integer.parseInt(scanner.nextLine());
                if (market.getStocks().containsKey(symbol)) {
                    portfolio.buy(market.getStocks().get(symbol), shares);
                }
            } else if (action.equals("sell")) {
                System.out.print("Enter stock symbol: ");
                String symbol = scanner.nextLine().toUpperCase();
                System.out.print("Enter number of shares: ");
                int shares = Integer.parseInt(scanner.nextLine());
                if (market.getStocks().containsKey(symbol)) {
                    portfolio.sell(market.getStocks().get(symbol), shares);
                }
            } else if (action.equals("save")) {
                portfolio.save("portfolio.txt");
            } else if (action.equals("load")) {
                portfolio.load("portfolio.txt");
            } else if (action.equals("quit")) {
                System.out.println("👋 Exiting simulation.");
                break;
            }
        }
        scanner.close();
    }
}
