/*
 * TransactionssRow.java
 *
 * Created on November 1, 2004, 7:21 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.shared.data.model.transactionstable;

/**
 *
 * @author  Walter Yuan
 */
public class TransactionsRow implements java.io.Serializable {
    public TransactionsRow() {
              
    }
    
    public String security;
    public float transactedPrice;
    //public float standingPrice;
    public String action;
    public int units;
    public boolean owned;
}

