package edu.grinnell.csc207.blockchains;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;


/**
 * Some simple tests of our BlockChain class.
 *
 * @author Samuel A. Rebelsky
 */
public class TestBlockChain {
    @Test
    public void simpleTest() { 
        HashValidator simpleValidator = (hash) -> (hash.length() >= 1) && (hash.get(0) == 0);
        BlockChain chain = new BlockChain(simpleValidator);

        Transaction[] transactions = { 
            new Transaction("","Alexis",50),
            new Transaction("Alexis","Blake",25),
            new Transaction("Alexis","Cassidy",10),
            new Transaction("Blake","Cassidy",5),
            new Transaction("Cassidy","Alexis",15)
        };
        
        for(Transaction transaction : transactions) { 
            Block blk = chain.mine(transaction);
            chain.append(blk);
        }

        try {
            chain.check();
        } catch (Exception e) {
            fail(e);
        }
    }
} // class TestBlockChain
