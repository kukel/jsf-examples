/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.vankuijk.ronald.jsfexamples.view;

import java.util.Comparator;

import net.vankuijk.ronald.jsfexamples.domain.Mail;

import org.primefaces.model.SortOrder;

public class LazySorter implements Comparator<Mail> {

    private String sortField;
   
    private SortOrder sortOrder;
   
    public LazySorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    public int compare(Mail mail1, Mail mail2) {
        try {
            Object value1 = Mail.class.getField(this.sortField).get(mail1);
            Object value2 = Mail.class.getField(this.sortField).get(mail2);

            int value = ((Comparable)value1).compareTo(value2);
           
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        }
        catch(Exception e) {
            throw new RuntimeException();
        }
    }
}
