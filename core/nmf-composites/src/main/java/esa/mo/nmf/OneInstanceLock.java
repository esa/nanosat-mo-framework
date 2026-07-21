/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package esa.mo.nmf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Locks the application to one single instance.
 *
 * @author Cesar Coelho
 */
public class OneInstanceLock {

    private static File f;
    private static FileChannel channel;
    private static FileLock lock;

    public OneInstanceLock() {
        try {
            File internalDir = AppStorage.getAppNMFInternalDir();
            f = new File(internalDir, "application.lck");

            // Try to get the lock
            channel = new RandomAccessFile(f, "rw").getChannel();
            lock = channel.tryLock();
            if (lock == null) {
                // File is locked by other application
                channel.close();
                throw new RuntimeException("Two instances cannot run at a time.");
            }
            // Add shutdown hook to release lock when application shutdown
            ShutdownHook shutdownHook = new ShutdownHook();
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        } catch (IOException e) {
            throw new RuntimeException("Could not start process.", e);
        }
    }

    public static void unlockFile() {
        // release and delete file lock
        try {
            if (lock != null) {
                lock.release();
                channel.close();
                f.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ShutdownHook extends Thread {

        @Override
        public void run() {
            unlockFile();
        }
    }

}
