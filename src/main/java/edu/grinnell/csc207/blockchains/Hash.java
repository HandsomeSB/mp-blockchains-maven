package edu.grinnell.csc207.blockchains;

import java.util.Arrays;

/**
 * Encapsulated hashes.
 *
 * @author Harrison Zhu
 * @author Samuel A. Rebelsky
 */
public class Hash {
  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  private byte[] byteData;
  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new encapsulated hash.
   *
   * @param data
   *   The data to copy into the hash.
   */
  public Hash(byte[] data) {
    this.byteData = data.clone();
  } // Hash(byte[])

  // +---------+-----------------------------------------------------
  // | Methods |
  // +---------+

  /**
   * Determine how many bytes are in the hash.
   *
   * @return the number of bytes in the hash.
   */
  public int length() {
    return this.byteData.length;
  } // length()

  /**
   * Get the ith byte.
   *
   * @param i
   *   The index of the byte to get, between 0 (inclusive) and
   *   length() (exclusive).
   *
   * @return the ith byte
   */
  public byte get(int i) {
    return byteData[i];
  } // get()

  /**
   * Get a copy of the bytes in the hash. We make a copy so that the client
   * cannot change them.
   *
   * @return a copy of the bytes in the hash.
   */
  public byte[] getBytes() {
    return this.byteData.clone();
  } // getBytes()

  /**
   * Convert to a hex string.
   *
   * @return the hash as a hex string.
   */
  public String toString() {
    StringBuilder str = new StringBuilder();

    for(byte b : this.byteData) { 
      int unsignedInt = Byte.toUnsignedInt(b);
      String hex = String.format("%X", unsignedInt);
      str.append(hex);
    }

    return str.toString();
  } // toString()

  /**
   * Determine if this is equal to another object.
   *
   * @param other
   *   The object to compare to.
   *
   * @return true if the two objects are conceptually equal and false
   *   otherwise.
   */
  public boolean equals(Object other) {
    return other instanceof Hash && Arrays.equals(this.byteData, ((Hash) other).getBytes());
  } // equals(Object)

  /**
   * Get the hash code of this object.
   *
   * @return the hash code.
   */
  public int hashCode() {
    return this.toString().hashCode();
  } // hashCode()
} // class Hash
