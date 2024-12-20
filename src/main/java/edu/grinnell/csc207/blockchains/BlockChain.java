package edu.grinnell.csc207.blockchains;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A full blockchain.
 *
 * @author Harrison Zhu
 */
public class BlockChain implements Iterable<Transaction> {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  /** Total number of blocks in the chain. */
  private int totalBlocks;

  /** The first block. */
  private Node<Block> head;

  /** The last block. */
  private Node<Block> tail;

  /** Has Validator. */
  private HashValidator validator;

  /** Name - Balance pair. */
  private Map<String, Integer> balances = new HashMap<String, Integer>();

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new blockchain using a validator to check elements.
   *
   * @param check The validator used to check elements.
   */
  public BlockChain(HashValidator check) {
    this.totalBlocks = 1;
    this.validator = check;
    Node<Block> first = new Node<Block>(this.mine(new Transaction("", "", 0)));
    this.head = first;
    this.tail = first;
  } // BlockChain(HashValidator)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Mine for a new valid block for the end of the chain, returning that block.
   *
   * @param t The transaction that goes in the block.
   * @return a new block with correct number, hashes, and such.
   */
  public Block mine(Transaction t) {
    Block newBlock = new Block(totalBlocks, t, getHash(), this.validator);
    return newBlock;
  } // mine(Transaction)

  /**
   * Get the number of blocks curently in the chain.
   *
   * @return the number of blocks in the chain, including the initial block.
   */
  public int getSize() {
    return this.totalBlocks;
  } // getSize()

  /**
   * Add a block to the end of the chain.
   *
   * @param blk The block to add to the end of the chain.
   * @throws IllegalArgumentException if (a) the hash is not valid, (b) the hash is not appropriate
   *     for the contents, or (c) the previous hash is incorrect.
   */
  public void append(Block blk) throws IllegalArgumentException {
    checkBlock(blk);

    Node<Block> newNode = new Node<Block>(blk);
    // if(this.head == null) {
    //   this.head = newNode;
    //   this.tail = this.head;
    // } else {
    this.tail.setNext(newNode);
    newNode.setPrevious(this.tail);
    this.tail = newNode;
    // }
    this.totalBlocks++;
    this.processTransaction(this.balances, blk.getTransaction());
  } // append()

  /**
   * Attempt to remove the last block from the chain.
   *
   * @return false if the chain has only one block (in which case it's not removed) or true
   *     otherwise (in which case the last block is removed).
   */
  public boolean removeLast() {
    if (this.totalBlocks <= 1) {
      return false;
    } else {
      Transaction tailTransaction = this.tail.getData().getTransaction();
      this.processTransaction(
          this.balances,
          new Transaction(
              tailTransaction.getTarget(),
              tailTransaction.getSource(),
              tailTransaction.getAmount()));

      this.tail.getPrevious().setNext(null);
      this.tail = this.tail.getPrevious();
      this.totalBlocks--;
      return true;
    } // if else
  } // removeLast()

  /**
   * Get the hash of the last block in the chain.
   *
   * @return the hash of the last sblock in the chain.
   */
  public Hash getHash() {
    if (this.tail != null) {
      return this.tail.getData().getHash();
    } else {
      return null;
    } // if else
  } // getHash()

  /**
   * Check if the NEW transaction is correct, assuming the block chain is valid.
   *
   * @param balanceMap the map to check the transaction against
   * @param transaction the transaction to check
   * @return true if transaction is valid
   */
  private boolean isValidTransaction(Map<String, Integer> balanceMap, Transaction transaction) {
    int sourceBalance = this.balance(balanceMap, transaction.getSource());
    return (transaction.getSource().equals("") || sourceBalance >= transaction.getAmount())
        && transaction.getAmount() >= 0;
  } // isValidTransaction(Map<String, Integer>, Transaction)

  /**
   * Process the transaction, adding to balances table. Does not check or assume that the
   * transaction is correct. Thus source can have negative balance
   *
   * @param balanceMap the balance map to modify
   * @param transaction the transaction to process
   */
  private void processTransaction(Map<String, Integer> balanceMap, Transaction transaction) {
    int sourceBalance = this.balance(balanceMap, transaction.getSource());
    int targetBalance = this.balance(balanceMap, transaction.getTarget());

    if (!transaction.getSource().equals("")) {
      balanceMap.put(transaction.getSource(), sourceBalance - transaction.getAmount());
    } // if source is not empty
    if (!transaction.getTarget().equals("")) {
      balanceMap.put(transaction.getTarget(), targetBalance + transaction.getAmount());
    } // is target is not empty
  } // processTransaction(Map<String, Integer>, Transaction)

  /**
   * Checks if the NEW block is valid. Throws errors if invalid.
   *
   * @param blk The block to add to the end of the chain.
   * @throws IllegalArgumentException if (a) the hash is not valid, (b) the hash is not appropriate
   *     for the contents, or (c) the previous hash is incorrect.
   */
  public void checkBlock(Block blk) throws IllegalArgumentException {
    if (!this.validator.isValid(blk.getHash())) {
      throw new IllegalArgumentException("The Hash is not valid : " + blk);
    } else if (!blk.getHash().equals(Block.computeHash(blk))) {
      throw new IllegalArgumentException("Hash is not appropriate for the contents: " + blk);
    } else if (blk.getPrevHash() != null && !blk.getPrevHash().equals(this.getHash())) {
      throw new IllegalArgumentException(
          "Previous hash is incorrect: " + blk + " tail: " + this.tail.getData());
    } // check valid
  } // checkBloc(Block)

  /**
   * Checks if the NEW block is valid.
   *
   * @param blk The block to add to the end of the chain.
   *     <p>Invalid cases: if (a) the hash is not valid, (b) the hash is not appropriate for the
   *     contents, or (c) the previous hash is incorrect.
   * @return True of blk is valid.
   */
  private boolean isValidBlock(Block blk) {
    try {
      this.checkBlock(blk);
      return true;
    } catch (Exception e) {
      return false;
    } // try catch
  } // isValidBlock(Block)

  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @return true if the blockchain is correct and false otherwise.
   */
  public boolean isCorrect() {
    try {
      check();
      return true;
    } catch (Exception e) {
      return false;
    } // try catch
  } // isCorrect()

  /**
   * Determine if the blockchain is correct in that (a) the balances are legal/correct at every
   * step, (b) that every block has a correct previous hash field, (c) that every block has a hash
   * that is correct for its contents, and (d) that every block has a valid hash.
   *
   * @throws Exception If things are wrong at any block.
   */
  public void check() throws Exception {
    BlockChain dummy = new BlockChain(validator);

    Iterator<Block> blkIterator = this.blocks();
    while (blkIterator.hasNext()) {
      Block blk = blkIterator.next();
      // This check is separate because append does not check for invalid transaction
      if (!this.isValidTransaction(dummy.balances, blk.getTransaction())) {
        throw new IllegalArgumentException("Invalid transaction: " + blk);
      } // if not valid
      dummy.append(blk);
    } // while iterator has next
  } // check()

  /** Recalculates the balance. */
  private void recalculateBalance() {
    Map<String, Integer> balanceMap = new HashMap<>();

    for (Transaction transaction : this) {
      this.processTransaction(balanceMap, transaction);
    } // for

    this.balances = balanceMap;
  } // recalculateBalance

  /**
   * Find one user's balance. Will recalculate the balance.
   *
   * @param user The user whose balance we want to find.
   * @return that user's balance (or 0, if the user is not in the system).
   */
  public int balance(String user) {
    recalculateBalance();
    return this.balance(this.balances, user);
  } // balance()

  /**
   * Returns the users current balance in the map being passed in. Assumes balanceMap is the most
   * up-to-date
   *
   * @param balanceMap
   * @param user
   * @return balance of user
   */
  private int balance(Map<String, Integer> balanceMap, String user) {
    Integer balance = balanceMap.get(user);
    return balance == null ? 0 : balance.intValue();
  } // balance(Map<String, Integer>, String)

  /**
   * Get an interator for all the blocks in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Block> blocks() {
    return new Iterator<Block>() {
      Node<Block> nextNode = head;

      public boolean hasNext() {
        return nextNode != null;
      } // hasNext()

      public Block next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } // if
        Block ret = nextNode.getData();
        nextNode = nextNode.getNext();
        return ret;
      } // next()
    };
  } // blocks()

  /**
   * Return an iterator of all the people who participated in the system.
   *
   * @return an iterator of all the people in the system.
   */
  public Iterator<String> users() {
    Iterator<String> namesIterator = balances.keySet().iterator();

    return new Iterator<String>() {
      public boolean hasNext() {
        return namesIterator.hasNext();
      } // hasNext()

      public String next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } // if
        return namesIterator.next();
      } // next()
    };
  } // users()

  /**
   * Get an interator for all the transactions in the chain.
   *
   * @return an iterator for all the blocks in the chain.
   */
  public Iterator<Transaction> iterator() {
    return new Iterator<Transaction>() {
      Node<Block> nextNode = head;

      public boolean hasNext() {
        return nextNode != null;
      } // hasNext()

      public Transaction next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        } // if
        Transaction ret = nextNode.getData().getTransaction();
        nextNode = nextNode.getNext();
        return ret;
      } // next()
    };
  } // iterator()
} // class BlockChain
