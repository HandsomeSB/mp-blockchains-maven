package edu.grinnell.csc207.blockchains;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Blocks to be stored in blockchains.
 *
 * @author Harrison Zhu
 * @author Samuel A. Rebelsky
 */
public class Block {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+
  private int numBlocks;
  Transaction transaction;
  private Hash previousHash;
  private Hash hash;
  long nonce;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new block from the specified block number, transaction, and previous hash, mining to
   * choose a nonce that meets the requirements of the validator.
   *
   * @param num The number of the block.
   * @param theTransaction The transaction for the block.
   * @param prevHash The hash of the previous block.
   * @param check The validator used to check the block.
   */
  public Block(int num, Transaction theTransaction, Hash prevHash, HashValidator check) {
    this.numBlocks = num;
    this.transaction = theTransaction;
    this.previousHash = prevHash;
    if (check != null) {
      this.mine(check);
    }
  } // Block(int, Transaction, Hash, HashValidator)

  /**
   * Create a new block, computing the hash for the block.
   *
   * @param num The number of the block.
   * @param transaction The transaction for the block.
   * @param prevHash The hash of the previous block.
   * @param theNonce The nonce of the block.
   */
  public Block(int num, Transaction transaction, Hash prevHash, long theNonce) {
    this(num, transaction, prevHash, null);
    this.nonce = theNonce;
    this.computeHash();
  } // Block(int, Transaction, Hash, long)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /** Compute the hash of the block given all the other info already stored in the block. */
  void computeHash() {
    this.hash = Block.computeHash(this);
  } // computeHash()

  public static Hash computeHash(Block blk) {
    try {
      MessageDigest md = MessageDigest.getInstance("sha-256"); // STUB
      /**
       * Avoids recreating structures---such as the `MessageDigest`, the various `ByteBuffer`
       * objects, and other individual arrays---that need not be recreated.
       */
      byte[] ibytes =
          ByteBuffer.allocate(Integer.BYTES).putInt(blk.numBlocks).array(); // block number
      md.update(ibytes);
      byte[] sourceBytes = blk.transaction.getSource().getBytes(); // source
      md.update(sourceBytes);
      byte[] targetBytes = blk.transaction.getTarget().getBytes(); // target
      md.update(targetBytes);
      byte[] amountBytes =
          ByteBuffer.allocate(Integer.BYTES).putInt(blk.transaction.getAmount()).array(); // amount
      md.update(amountBytes);
      if (blk.previousHash != null) {
        byte[] prevBytes = blk.previousHash.getBytes();
        md.update(prevBytes);
      }
      byte[] lbytes = ByteBuffer.allocate(Long.BYTES).putLong(blk.nonce).array(); // nonce
      md.update(lbytes);

      byte[] hash = md.digest();
      return new Hash(hash);
    } catch (Exception e) {
      return new Hash(new byte[0]);
    }
  }

  private void mine(HashValidator check) {
    if (hash != null && check.isValid(hash)) {
      return;
    }
    Random rand = new Random();
    do {
      this.nonce = rand.nextLong();
      this.computeHash();
    } while (!check.isValid(hash));
  }

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Get the number of the block.
   *
   * @return the number of the block.
   */
  public int getNum() {
    return this.numBlocks;
  } // getNum()

  /**
   * Get the transaction stored in this block.
   *
   * @return the transaction.
   */
  public Transaction getTransaction() {
    return this.transaction;
  } // getTransaction()

  /**
   * Get the nonce of this block.
   *
   * @return the nonce.
   */
  public long getNonce() {
    return this.nonce;
  } // getNonce()

  /**
   * Get the hash of the previous block.
   *
   * @return the hash of the previous block.
   */
  Hash getPrevHash() {
    return this.previousHash;
  } // getPrevHash

  /**
   * Get the hash of the current block.
   *
   * @return the hash of the current block.
   */
  Hash getHash() {
    return this.hash;
  } // getHash

  /**
   * Get a string representation of the block.
   *
   * @return a string representation of the block.
   */
  public String toString() {
    return String.format(
        "[%s, Transaction: %s, Hash: %s, PreviousHash: %s]",
        this.numBlocks, this.transaction, this.hash, this.previousHash);
  } // toString()
} // class Block
