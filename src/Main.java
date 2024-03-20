import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
public class Main {
    public static void main(String[] args) {
        whatCanIKeep("Decks In.txt", "Decks Out.txt");
    }

    //Answers the question "What cards from deckOut can I retrieve from deckIn?"
    static void whatCanIKeep(String deckIn, String deckOut){
        File deckInFile = new File(deckIn);
        Scanner in;
        File deckOutFile = new File(deckOut);
        Scanner out;
        Scanner newout;
        try {
            in = new Scanner(deckInFile);
            out = new Scanner(deckOutFile);
            newout = new Scanner(deckOutFile);
        } catch (Exception e){
            System.err.println("scan failed i guess");
            in = new Scanner("");
            out = new Scanner("");
            newout = new Scanner("");
        }

        ArrayList<Deck> inDecks = readToArrayList(in);
        ArrayList<Deck> outDecks = readToArrayList(out);
        ArrayList<Deck> newOutDecks = readToArrayList(newout);

        System.out.println("Printing overlapping cards");

        for (Deck deck : outDecks){
            HashMap<String, NewCard> similarity = new HashMap<>();
            for (Card card : deck.cards.values()){
                for (int i = 0; i < card.count; i++){
                    String foundInDeck = removeCardFromOldDecksAndGiveDeckName(card.name, inDecks);
                    if (foundInDeck != null){
                        removeCardFromOldDecksAndGiveDeckName(card.name, newOutDecks);
                        String mapKey = foundInDeck + card.name;
                        if (similarity.containsKey(mapKey)){
                            similarity.get(mapKey).count += 1;
                        } else {
                            NewCard newCard = new NewCard();
                            newCard.name = card.name;
                            newCard.oldDeckName = foundInDeck;
                            newCard.count = 1;
                            similarity.put(mapKey, newCard);
                        }
                    }
                }
            }

            System.out.println("# " + deck.name);
            printSimilarity(similarity);
            System.out.println();
        }
        System.out.println("###############################");
        System.out.println("Printing non-overlapping cards from In-Decks");
        for (Deck deck : inDecks){
            printDeck(deck);
        }

        System.out.println("###############################");
        System.out.println("Printing non-overlapping cards from Out-Decks");
        for (Deck deck : newOutDecks){
            printDeck(deck);
        }
    }
    static String removeCardFromOldDecksAndGiveDeckName(String cardName, ArrayList<Deck> inDecks){
        for (Deck deck : inDecks){
            if (deck.cards.containsKey(cardName)){
                deck.cards.get(cardName).count -= 1;
                if (deck.cards.get(cardName).count == 0){
                    deck.cards.remove(cardName);
                }
                return deck.name;
            }
        }
        return null;
    }

    static ArrayList<Deck> readToArrayList(Scanner in){
        ArrayList<Deck> inDecks = new ArrayList<>();
        Deck curDeck = new Deck();
        while (in.hasNext()) {
            String nextLine = in.nextLine();
            String[] words = nextLine.split(" ");
            if (words[0].equals("")) {
                inDecks.add(curDeck);
            } else if (words[0].equals("#")){
                curDeck = new Deck();
                StringBuilder curDeckName = new StringBuilder();
                for (int i = 1; i < words.length; i++){
                    curDeckName.append(words[i]).append(" ");
                }
                curDeck.name = curDeckName.toString().trim();
                curDeck.cards = new HashMap<>();
                continue;
            } else {
                int numCards = Integer.parseInt(words[0]);
                StringBuilder cardNameBuilder = new StringBuilder();
                for (int i = 1; i < words.length; i++){
                    cardNameBuilder.append(words[i]).append(" ");
                }
                String cardName = cardNameBuilder.toString().trim();


                if (curDeck.cards.containsKey(cardName)){
                    Card deckCard = curDeck.cards.get(cardName);
                    deckCard.count += numCards;
                    curDeck.cards.put(cardName, deckCard);
                } else {
                    Card newCard = new Card();
                    newCard.name = cardName;
                    newCard.count = numCards;
                    curDeck.cards.put(cardName, newCard);
                }
            }
        }

        inDecks.add(curDeck);
        return inDecks;
    }
    static void printDeck (Deck deck){
        System.out.println("# " + deck.name);
        PriorityQueue<Card> queue = new PriorityQueue<>(deck.cards.values());
        Card card = queue.poll();
        while(card != null){
            System.out.println(card.count + " " + card.name);
            card = queue.poll();
        }
        System.out.println();
    }

    static void printSimilarity (HashMap<String, NewCard> similarity){
        PriorityQueue<NewCard> queue = new PriorityQueue<>(similarity.values());
        NewCard card = queue.poll();
        while (card != null){
            System.out.println(card.oldDeckName + ": " + card.count + " " + card.name);
            card = queue.poll();
        }
    }

    static class Deck {
        String name;
        HashMap<String, Card> cards;
    }

    static class Card implements Comparable<Card>{
        String name;
        int count;

        @Override
        public int compareTo(Card compCard){
            return this.name.compareTo(compCard.name);
        }
    }

    static class NewCard implements Comparable<NewCard>{
        String name;
        int count;
        String oldDeckName;
        @Override
        public int compareTo(NewCard compCard){
            int deckCompare = this.oldDeckName.compareTo(compCard.oldDeckName);
            if (deckCompare != 0) return deckCompare;
            return this.name.compareTo(compCard.name);
        }
    }
}