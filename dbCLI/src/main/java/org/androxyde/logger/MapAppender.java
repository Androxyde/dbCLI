/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package org.androxyde.logger;

import static ch.qos.logback.core.CoreConstants.CODES_URL;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.util.FileUtil;

public class MapAppender<E> extends UnsynchronizedAppenderBase<E> {

    protected Encoder<E> encoder;

    protected final ReentrantLock streamWriteLock = new ReentrantLock(false);

    private List<String> content = new ArrayList<>();

    private ResilientFileOutputStream resilientFOS;

    public List<String> getContent() {
        return content;
    }

    @Override
    public void start() {

        int errors = 0;

        if (this.encoder == null) {
            addStatus(new ErrorStatus("No encoder set for the appender named \"" + name + "\".", this));
            errors++;
        }

        if (errors == 0) {
            super.start();
        }

    }

    public void setLayout(Layout<E> layout) {
        addWarn("This appender no longer admits a layout as a sub-component, set an encoder instead.");
        addWarn("To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.");
        addWarn("See also " + CODES_URL + "#layoutInsteadOfEncoder for details");
        LayoutWrappingEncoder<E> lwe = new LayoutWrappingEncoder<E>();
        lwe.setLayout(layout);
        lwe.setContext(context);
        this.encoder = lwe;
    }

    @Override
    protected void append(E event) {
        if (!isStarted()) {
            return;
        }
        try {
            // this step avoids LBCLASSIC-139
            if (event instanceof DeferredProcessingAware) {
                ((DeferredProcessingAware) event).prepareForDeferredProcessing();
            }
            writeOut(event);

        } catch (IOException ioe) {
            // as soon as an exception occurs, move to non-started state
            // and add a single ErrorStatus to the SM.
            this.started = false;
            addStatus(new ErrorStatus("IO failure in appender", this, ioe));
        }
    }

    @Override
    public void stop() {

        if(!isStarted())
            return;

        streamWriteLock.lock();

        try {
            content.clear();
            super.stop();
        } finally {
            streamWriteLock.unlock();
        }

    }


    void encoderClose() {
        if (encoder != null) {
            try {
                byte[] footer = encoder.footerBytes();
                writeBytes(footer);
            } catch (IOException ioe) {
                this.started = false;
                addStatus(new ErrorStatus("Failed to write footer for appender named [" + name + "].", this, ioe));
            }
        }
    }

    void encoderInit() {
        if (encoder != null ) {
            try {
                byte[] header = encoder.headerBytes();
                writeBytes(header);
            } catch (IOException ioe) {
                this.started = false;
                addStatus(
                        new ErrorStatus("Failed to initialize encoder for appender named [" + name + "].", this, ioe));
            }
        }
    }

    public void setFile(String filename) {
        streamWriteLock.lock();
        try {
            File file = new File(filename);
            boolean result = FileUtil.createMissingParentDirectories(file);
            if (!result) {
                addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
            }
            try {
                resilientFOS = new ResilientFileOutputStream(file, false, 8192L);
                resilientFOS.setContext(context);
                for (String event:content) {
                    resilientFOS.write(event.getBytes("UTF-8"));
                    resilientFOS.flush();
                }
                content.clear();
                content=null;
            } catch (Exception e) {
                resilientFOS=null;
            }
        } finally {
            streamWriteLock.unlock();
        }
    }

    protected void writeOut(E event) throws IOException {
        byte[] byteArray = this.encoder.encode(event);
        writeBytes(byteArray);
    }

    private void writeBytes(byte[] byteArray) throws IOException {
        if (byteArray == null || byteArray.length == 0)
            return;

        streamWriteLock.lock();
        try {
            if (resilientFOS==null)
                content.add(new String(byteArray));
            else {
                safeWriteBytes(byteArray);
            }
        } finally {
            streamWriteLock.unlock();
        }
    }

    private void safeWriteBytes(byte[] byteArray) {

        if (resilientFOS==null) return;

        FileChannel fileChannel = resilientFOS.getChannel();

        if (fileChannel == null) return;

        // Clear any current interrupt (see LOGBACK-875)
        boolean interrupted = Thread.interrupted();

        FileLock fileLock = null;

        try {
            fileLock = fileChannel.lock();
            long position = fileChannel.position();
            long size = fileChannel.size();
            if (size != position) {
                fileChannel.position(size);
            }
            resilientFOS.write(byteArray);
            resilientFOS.flush();
        } catch (IOException e) {
            // Mainly to catch FileLockInterruptionExceptions (see LOGBACK-875)
            resilientFOS.postIOFailure(e);
        } finally {
            releaseFileLock(fileLock);

            // Re-interrupt if we started in an interrupted state (see LOGBACK-875)
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void releaseFileLock(FileLock fileLock) {
        if (fileLock != null && fileLock.isValid()) {
            try {
                fileLock.release();
            } catch (IOException e) {
                addError("failed to release lock", e);
            }
        }
    }

    public Encoder<E> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }

}