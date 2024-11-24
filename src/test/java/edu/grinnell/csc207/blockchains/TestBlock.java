package edu.grinnell.csc207.blockchains;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Some simple tests of our Block class.
 *
 * @author Samuel A. Rebelsky
 */
public class TestBlock {
  // +---------------+-----------------------------------------------
  // | Static fields |
  // +---------------+

  /** The message digest used to compute hashes. */
  static MessageDigest md = null;

  /** The byte buffer used for ints. */
  static ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);

  /** The byte buffer used for longs. */
  static ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Convert an integer into its bytes.
   *
   * @param i The integer to convert.
   * @return The bytes of that integer.
   */
  static byte[] intToBytes(int i) {
    intBuffer.clear();
    return intBuffer.putInt(i).array();
  } // intToBytes(int)

  /**
   * Convert a long into its bytes.
   *
   * @param l The long to convert.
   * @return The bytes in that long.
   */
  static byte[] longToBytes(long l) {
    longBuffer.clear();
    return longBuffer.putLong(l).array();
  } // longToBytes()

  /**
   * Compute the expected hash of a block.
   *
   * @param block The block whose hash we want to compute.
   * @return the expected hash of that block.
   */
  static byte[] expectedHash(Block block) {
    md.update(intToBytes(block.getNum()));
    md.update(block.getTransaction().getSource().getBytes());
    md.update(block.getTransaction().getTarget().getBytes());
    md.update(intToBytes(block.getTransaction().getAmount()));
    md.update(block.getPrevHash().getBytes());
    md.update(longToBytes(block.getNonce()));
    return md.digest();
  } // expectedHash()

  // +-------+-------------------------------------------------------
  // | Setup |
  // +-------+

  /** Get things set up. */
  @BeforeAll
  static void setup() {
    try {
      md = MessageDigest.getInstance("sha-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Cannot load hash algorithm");
    } // try/catch
  } // setup()

  // +-------+-------------------------------------------------------
  // | Tests |
  // +-------+

  /** Simple block stuff. */
  @Test
  public void simpleBlockTest() {
    Transaction t = new Transaction("Here", "There", 12);
    Hash ph = new Hash(new byte[] {3, 4, 5});
    Block b = new Block(10, t, ph, 67);

    assertEquals(10, b.getNum(), "number of block");
    assertEquals(t, b.getTransaction(), "transaction in block");
    assertEquals(ph, b.getPrevHash(), "previous hash");
    assertEquals(67, b.getNonce(), "nonce");
  } // simpleBlockTest

  /** Make sure that we can create a hash with a deposit. */
  @Test
  public void depositTest() {
    Transaction t = new Transaction("", "Someone", 555);
    Hash ph = new Hash(new byte[] {5, 5, 5, 5, 5});
    Block b = new Block(5, t, ph, 5555);

    assertEquals(5, b.getNum(), "number of block for deposit");
    assertEquals(t, b.getTransaction(), "deposit in block");
    assertEquals(ph, b.getPrevHash(), "previous hash for deposit");
    assertEquals(5555, b.getNonce(), "nonce for deposit");
  } // depositTest()

  /** Simple block stuff with a simple validator. */
  @Test
  public void simpleValidatedBlockTest() {
    Transaction t = new Transaction("Source", "Target", 100);
    Hash ph = new Hash(new byte[] {(byte) 255});
    Block b = new Block(5, t, ph, (hash) -> true);

    assertEquals(5, b.getNum(), "number of validated block");
    assertEquals(t, b.getTransaction(), "transaction in validated block");
    assertEquals(ph, b.getPrevHash(), "previous hash in validated block");
  } // simpleValidatedBlockTest

  /** A deposit with a simple validator. */
  @Test
  public void simpleValidatedDepositTest() {
    Transaction t = new Transaction("", "Deposited", 888);
    Hash ph = new Hash(new byte[] {8, 8, 8});
    Block b = new Block(8, t, ph, (hash) -> true);

    assertEquals(8, b.getNum(), "number of validated deposit block");
    assertEquals(t, b.getTransaction(), "transaction in validated deposit block");
    assertEquals(ph, b.getPrevHash(), "previous hash in validated deposit block");
  } // simpleValidatedBlockTest

  /** Simple block stuff with a slightly more complicated validator. */
  @Test
  public void startsWith5ValidatedBlockTest() {
    Transaction t = new Transaction("Source", "Target", 100);
    Hash ph = new Hash(new byte[] {(byte) 255});
    Block b = new Block(8, t, ph, (h) -> (h.length() > 1) && (h.get(0) == 5));

    assertEquals(8, b.getNum(), "number of 5-valid block");
    assertEquals(t, b.getTransaction(), "transaction in 5-valid block");
    assertEquals(ph, b.getPrevHash(), "previous 5-valid hash");
    assertEquals(5, b.getHash().get(0), "valid hash (starts with 5)");
  } // anotherValidatedBlockTest()

  /** A deposit with a slightly more complicated validator. */
  @Test
  public void startsWith7ValidatedDepositTest() {
    Transaction t = new Transaction("", "A Nony Moose", 777);
    Hash ph = new Hash(new byte[] {77, 77, 77, 77});
    Block b = new Block(7, t, ph, (h) -> (h.length() > 0) && (h.get(0) == 7));

    assertEquals(7, b.getNum(), "number of 7-valid deposit block");
    assertEquals(t, b.getTransaction(), "transaction in 7-valid deposit block");
    assertEquals(ph, b.getPrevHash(), "previous hash in 7-valid deposit block");
    assertEquals(7, b.getHash().get(0), "valid hash in 7-valid deposit block (starts with 7)");
  } // anotherValidatedDepositTest()

  /** Ensure that the block calculates the correct hash. */
  @Test
  public void hashTest() {
    Transaction t = new Transaction("Sam", "Sam", 50);
    Hash ph = new Hash(new byte[] {10, 20, 30, 40, 50});
    Block b = new Block(5, t, ph, 100);
    assertArrayEquals(expectedHash(b), b.getHash().getBytes(), "correct hash");
  } // hashTest()

  /** Ensure that a block with a validated hash calculates a correct and valid hash. */
  @Test
  public void aValidatedHashTest() {
    Transaction t = new Transaction("Rebel", "Sky", 250);
    Hash ph = new Hash(new byte[] {42, 42, 42, 42, 42, 42});
    Block b = new Block(5, t, ph, (h) -> (h.length() > 0) && (h.get(0) == 0));
    assertEquals(0, b.getHash().get(0), "hash in validated block starts with 0");
    assertArrayEquals(expectedHash(b), b.getHash().getBytes(), "correct hash in validated block");
  } // validatedHashTest()

  /** Ensure that we can create the standard initial block. */
  @Test
  public void initialBlockTest() {
    Transaction t = new Transaction("", "", 0);
    Hash ph = new Hash(new byte[] {});
    Block b = new Block(0, t, ph, (hash) -> true);

    assertEquals(0, b.getNum(), "correct number in initial block");
    assertEquals(t, b.getTransaction(), "correct transaction in initial block");
    assertEquals(ph, b.getPrevHash(), "correct previous hash in initial block");
  } // initialBlockTest()

  /** Test that the toString method works. Forthcoming. */
  @Test
  public void toStringTest() {} // toStringTest()

  /** Test that the toString method works with a deposit. Forthcoming. */
  @Test
  public void toStringDepositTest() {} // toStringDepositTest()
} // class TestBlock
