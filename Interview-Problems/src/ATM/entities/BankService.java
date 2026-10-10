package entities;

import java.util.HashMap;
import java.util.Optional;


public class BankService {
    private static BankService instance;
    private HashMap<String, Account> accounts;
    private HashMap<String, Card> cards;
    private HashMap<Card, Account> cardToAccount;

    private BankService(){
        this.accounts = new HashMap<>();
        this.cards = new HashMap<>();
        this.cardToAccount = new HashMap<>();

        Account account1 = createAccount("1234567890", 1000.0);
        Card card1 = createCard("1234-5678-9012-3456", "1234");
        linkCardToAccount(card1, account1);

        Account account2 = createAccount("9876543210", 500.0);
        Card card2 = createCard("9876-5432-1098-7654", "4321");
        linkCardToAccount(card2, account2);
    }

    public static BankService getInstance(){
        if(BankService.instance==null){
            synchronized(BankService.class){
                if(BankService.instance==null){
                    BankService.instance = new BankService();
                }
            }
        }
        return BankService.instance;
    }

    public double checkBalance(Card card){
        return cardToAccount.get(card).getBalance();
    }
    public void deposite(Card card, double amt){
        cardToAccount.get(card).deposit(amt);
    }
    public void withdraw(Card card, double amt){
        cardToAccount.get(card).withdraw(amt);
    }
    public boolean authenticate(Card card, String pin){
        return card.getPin().equals(pin);
    }

    public Card createCard(String cardNumber, String pin){
        Card card = new Card(cardNumber, pin);
        cards.put(card.getCardNumber(), card);
        return card;
    }
    public Account createAccount(String accNumber, double initBalance){
        Account account = new Account(accNumber, initBalance);
        accounts.put(accNumber, account);
        return account;
    }
    public void linkCardToAccount(Card card, Account account){
        cardToAccount.put(card, account);
    }


    //getters
    public Optional<Card> getCard(String cardNumber){
        Card card = cards.get(cardNumber);
        if(card==null){
            return Optional.empty();
        }
        return Optional.of(card);
    }

}
