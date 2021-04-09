package esa.nmf.test;

import esa.mo.com.impl.util.ObjectInstanceIdGenerator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Kevin Otto
 */
public class UniqueIDTest
{

  public static boolean debug = true;

  // number of threads to run in generate IDs in parallel.
  private static final int NUM_THREADS = 100;

  private static boolean[] results = new boolean[NUM_THREADS];
  private static LinkedList<Long>[] idLists = new LinkedList[NUM_THREADS];
  private static HashSet<Long> unique = new HashSet<>();

  private class IDRunner implements Runnable
  {

    private final int threadNumber;

    public IDRunner(int threadNumber)
    {
      this.threadNumber = threadNumber;
    }

    @Override
    public void run()
    {
      for (int i = 0; i < 10000; i++) {
        long id = ObjectInstanceIdGenerator.getInstance().generateObjectInstanceId();
        synchronized (unique) {
          results[threadNumber] = unique.add(id);
          idLists[threadNumber].add(id);
          if (!results[threadNumber]) {
            return;
          }
        }
      }
    }
  }

  public void printBinary(long id)
  {
    for (int x = 0; x < Long.numberOfLeadingZeros((long) id); x++) {
      System.out.print('0');
    }
    System.out.println(Long.toBinaryString((long) id));
  }

  @Test
  public void test0()
  {
    Thread[] threads = new Thread[NUM_THREADS];
    for (int i = 0; i < threads.length; i++) {
      idLists[i] = new LinkedList<>();
      threads[i] = new Thread(new IDRunner(i));
    }

    for (Thread thread : threads) {
      thread.start();
    }

    for (int i = 0; i < threads.length; i++) {
      try {
        threads[i].join();

        if (!results[i] && debug) {
          System.err.println("Threadd ID = " + i);
          for (Long id : idLists[i]) {
            System.err.println(id);
            printBinary(id);
          }
        }
        Assert.assertTrue(results[i]);

      } catch (InterruptedException ex) {
        Logger.getLogger(UniqueIDTest.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
